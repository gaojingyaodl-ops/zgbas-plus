package com.spt.bas.client.remote;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.vo.*;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.spt.tools.data.vo.PageDown;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/ctr/applydcsx", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyCtrDcsxClinent extends BaseClient<ApplyCtrDCSX> {

    @PostMapping("findPageContract")
    PageDown<DcsxShowVo> findPageContract(@RequestBody ContractSearchVo queryVo);

    @PostMapping("sumPageContract")
    ApplyCtrDCSX sumPageContract(@RequestBody ContractSearchVo searchVo);

    @PostMapping("findById")
    ApplyDcsxChooseVo findById(@RequestBody Long contractId);

    @PostMapping("findByDCSXApproveId")
    ApplyCtrDCSX findByDCSXApproveId(@RequestBody Long approveId);

    @PostMapping("findByDCSXApproveIdAll")
    List<ApplyCtrDCSX> findByDCSXApproveIdAll(@RequestBody Long approveId);

    @PostMapping("updateFileId")
    void updateFileId(UpdateDcsxContractVo vo);

    @PostMapping("findByContractNo")
    ApplyCtrDCSX findByContractNo(@RequestBody String ContractNO) ;

    @PostMapping("filterAutoSignWithPay")
    List<PmApprove> filterAutoSignWithPay(@RequestBody List<PmApprove> autoSignApproveList);

}
