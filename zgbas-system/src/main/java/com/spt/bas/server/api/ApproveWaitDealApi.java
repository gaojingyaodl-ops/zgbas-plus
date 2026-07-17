package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApproveWaitDeal;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApproveWaitDealVo;
import com.spt.bas.client.vo.ApproveWaitSearchVo;
import com.spt.bas.server.service.IApproveWaitDealService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "wait/deal")
public class ApproveWaitDealApi extends BaseApi<ApproveWaitDeal>  {
    @Autowired
    private IApproveWaitDealService iApproveWaitDealService;


    @PostMapping("findPageWaitDeal")
    public Page<ApproveWaitDeal> findPageWaitDealVo(@RequestBody ApproveWaitSearchVo queryVo) throws ApplicationException {
        return iApproveWaitDealService.findPageWaitDeal(queryVo);
    }

    @PostMapping("findPageWaitDealById")
    public Page<ApproveWaitDeal> findPageWaitDealById(@RequestBody ApproveWaitSearchVo queryVo) throws ApplicationException {
        return iApproveWaitDealService.findPageWaitDealById(queryVo);
    }

    @PostMapping("findPageWaitDealCount")
    public List<ApproveWaitDeal>findPageWaitDealCount(@RequestBody ApproveWaitSearchVo queryVo) throws ApplicationException {
        return iApproveWaitDealService.findPageWaitDealCount(queryVo);
    }

    @PostMapping("updateStatus")
    public void updateStatus(@RequestBody ApproveWaitSearchVo queryVo) throws ApplicationException {
       iApproveWaitDealService.updateStatus(queryVo);
    }

    @PostMapping("updateFlg")
    public void updateFlg(@RequestBody ApproveWaitSearchVo queryVo) throws ApplicationException {
        iApproveWaitDealService.updateFlg(queryVo);
    }
    @PostMapping("findSubject")
    public String findSubject(@RequestBody ApproveWaitSearchVo queryVo) throws ApplicationException {
       return iApproveWaitDealService.findSubject(queryVo);
    }
    // 定时任务：修改履约状态时，添加待办事项
    @PostMapping("/doContractSaveWaitDeal")
    public void doContractSaveWaitDeal(@RequestBody CtrContract searchVo){
        iApproveWaitDealService.doContractSaveWaitDeal(searchVo);
    }
    // 添加待办事项
    @PostMapping("/saveWaitDeal")
    public void saveWaitDeal(@RequestBody ApproveWaitDealVo vo){
        iApproveWaitDealService.saveWaitDeal(vo);
    }

    @Override
    public IDataService<ApproveWaitDeal> getService() {
        return iApproveWaitDealService;
    }

    @PostMapping("/getUserWaitDealNum")
    public Long getUserWaitDealNum(@RequestBody ApproveWaitSearchVo searchVo){
        return iApproveWaitDealService.getUserWaitDealNum(searchVo.getRelaUserId());
    }
}
