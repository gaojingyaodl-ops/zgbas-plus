package com.spt.quartz.task;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.vo.CtrContractChooseVo;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.ICtrLogisticsService;
import com.spt.pm.service.IPmApproveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author MoonLight
 * @Date 2023/7/26 10:41
 * @Version 1.0
 *
 * <p>Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.PmApproveTask}.
 * Bean name {@code "pmApproveTask"} aligns with {@code sys_job.invoke_target}
 * short names {@code pmApproveTask.doAutoSign} and
 * {@code pmApproveTask.updateCtrLogistics}.
 */
@Slf4j
@Component("pmApproveTask")
public class PmApproveTask {
    @Resource
    private IPmApproveService pmApproveService;
    @Autowired
    private ICtrLogisticsService ctrLogisticsService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private ICtrContractService ctrContractService;
    /**
     * 定时自动审批功能
     */
    public void doAutoSign() {
        log.info("执行自动审批任务开始======>");
        pmApproveService.doAutoSign();
        log.info("执行自动审批任务结束<======");
    }

    /**
     * 更新物流单据表历史数据
     */
    public void updateCtrLogistics() {
        log.info("updateCtrLogistics开始======>");
         ctrLogisticsService.findAll().stream().forEach(s->{
             Long matchUserId = s.getMatchUserId();
             CtrContractChooseVo byContractId = ctrContractService.findByContractId(s.getSellContractId());
             //刷新有效标识
             if(byContractId!=null){
                 if(!StringUtils.equals("C",byContractId.getContractStatus())){
                     s.setEnableFlg(true);
                 }else{
                     s.setEnableFlg(false);
                 }
             }
             //刷新部门id
             if(matchUserId!=null){
                 SysUserSdk user=authOpenFacade.findUserById(matchUserId);
                 s.setDeptId(user.getDeptId());
             }

             try {
                 ctrLogisticsService.save(s);
                 } catch (Exception e) {
                     log.error("执行任务失败{updateCtrLogistics}", e);
                 }
         });

        log.info("updateCtrLogistics结束<======");
    }
}
