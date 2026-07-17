package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.vo.ApplyDcsxChooseVo;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.DcsxShowVo;
import com.spt.bas.client.vo.UpdateDcsxContractVo;
import com.spt.bas.server.service.IApplyDcsxService;
import com.spt.pm.entity.PmApprove;
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
@RequestMapping(value = "ctr/applydcsx")
public class ApplyDcsxApi extends BaseApi<ApplyCtrDCSX> {

    @Autowired
    private IApplyDcsxService applyDcsxService;


    @Override
    public IDataService<ApplyCtrDCSX> getService() {
        return applyDcsxService;
    }

    @PostMapping("findPageContract")
    public Page<DcsxShowVo> findPageContract(@RequestBody ContractSearchVo queryVo) {
        return applyDcsxService.findPageContract(queryVo);
    }

    @PostMapping("sumPageContract")
    public ApplyCtrDCSX sumPageContract(@RequestBody ContractSearchVo searchVo) {
        return applyDcsxService.sumPageContract(searchVo);
    }

    @PostMapping("findById")
    public ApplyDcsxChooseVo findById(@RequestBody Long contractId) {
        return applyDcsxService.findById(contractId);
    }

    @PostMapping("findByDCSXApproveId")
    public ApplyCtrDCSX findByDCSXApproveId(@RequestBody Long approveId) {
        return applyDcsxService.findByDCSXApproveId(approveId);
    }
    @PostMapping("findByDCSXApproveIdAll")
    public List<ApplyCtrDCSX> findByDCSXApproveIdAll(@RequestBody Long approveId) {
        return applyDcsxService.findByDCSXApproveIdAll(approveId);
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody UpdateDcsxContractVo vo) {
        applyDcsxService.updateFileId(vo);
    }


    @PostMapping("findByContractNo")
    public ApplyCtrDCSX findByContractNo(@RequestBody String ContractNO) {
      return applyDcsxService.findByContractNo(ContractNO);
    }

    @PostMapping("filterAutoSignWithPay")
    public List<PmApprove> filterAutoSignWithPay(@RequestBody List<PmApprove> autoSignApproveList){
        return applyDcsxService.filterAutoSignWithPay(autoSignApproveList);
    }

}
