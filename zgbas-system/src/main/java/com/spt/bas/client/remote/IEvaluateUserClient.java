package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.EvaluateUser;
import com.spt.bas.client.vo.EvaluateSearchVo;
import com.spt.bas.client.vo.EvaluateStartVo;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/evaluate/user",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IEvaluateUserClient extends BaseClient<EvaluateUser> {
    /**
     * 查询考核人员明细
     * @param searchVo 查询参数
     * @return 查询结构
     */
    @PostMapping("/findPageBySearch")
    PageDown<EvaluateUser> findPageBySearch(@RequestBody EvaluateSearchVo searchVo);
    /**
     * 发起考评
     * @param vo
     */
    @RequestMapping(value = "startEvaluate")
    public void startEvaluate(@RequestBody EvaluateStartVo vo);
    /**
     * 查询考评
     * @param vo
     */
    @RequestMapping(value = "findAllByEvaluateMonthAndDeptId")
    public List<EvaluateUser> findAllByEvaluateMonthAndDeptId(@RequestBody EvaluateSearchVo vo);
    /**
     * 查询考评
     * @param vo
     */
    @RequestMapping(value = "findAllByEvaluateMonthAndUserId")
    public List<EvaluateUser> findAllByEvaluateMonthAndUserId(@RequestBody EvaluateSearchVo vo);

    /**
     * 根据evaluateUserId 批量查询数据
     * @param evaluateUserIds id字符串，用英文逗号分割
     * @return 结果集
     */
    @PostMapping("/selectDataByIds")
    List<EvaluateUser> selectDataByIds(@RequestParam(value = "evaluateUserIds") String evaluateUserIds);

    /**
     * 添加代办事项
     * @param vo
     */
    @PostMapping("/approveWaitDeal")
    void approveWaitDeal(@RequestBody EvaluateUserApproveWaitDealVo vo);
}
