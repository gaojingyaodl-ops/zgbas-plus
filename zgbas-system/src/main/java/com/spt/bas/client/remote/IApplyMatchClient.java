package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.vo.ApplyMatchVo;
import com.spt.bas.client.vo.ApproveMatchFormPrintVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(qualifier = "applyMatchClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/match", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyMatchClient extends BaseClient<ApplyMatch> {
    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("updateLiabilityFileId")
    void updateLiabilityFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("composeContractNo")
    public String composeContractNo(@RequestBody String contractType);

    @PostMapping("printApplyMatch")
    public ApproveMatchFormPrintVo printApplyMatch(@RequestBody Long applyId);

    @PostMapping("findByApproveId")
    public ApplyMatch findByApproveId(@RequestBody Long approveId);

    @PostMapping("applyMatch")
    public void applyMatch(@RequestBody ApplyMatchVo matchVo)throws WebApplicationException;

    @PostMapping("applyMatchIous")
    void applyMatchIous(@RequestBody ApplyMatchVo matchVo) throws WebApplicationException;



}

