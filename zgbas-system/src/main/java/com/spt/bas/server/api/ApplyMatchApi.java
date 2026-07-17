package com.spt.bas.server.api;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.vo.ApplyMatchVo;
import com.spt.bas.client.vo.ApproveMatchFormPrintVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.dao.ApplyMatchDao;
import com.spt.bas.server.service.IApplyMatchService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/match")
public class ApplyMatchApi extends BaseApi<ApplyMatch> {
    @Autowired
    private IApplyMatchService applyMatchService;
    @Autowired
    private ApplyMatchDao applyMatchDao;

    @Override
    public IBaseService<ApplyMatch> getService() {
        return applyMatchService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyMatchService.updateFileId(vo.getId(), vo.getFileId());
    }
    @PostMapping("composeContractNo")
    public String composeContractNo(@RequestBody String contractType) {
        return BasBusinessUtil.composeContractNo(BasConstants.ZG_ENTERPRISE_ID, null, contractType);
    }

    @PostMapping("updateLiabilityFileId")
    public void updateLiabilityFileId(@RequestBody FileIdUpdateVo vo){
        applyMatchService.updateLiabilityFileId(vo.getId(), vo.getLiabilityFileId());
    }

    @PostMapping("printApplyMatch")
    public ApproveMatchFormPrintVo printApplyBuy(@RequestBody Long applyId) {
        return applyMatchService.printApplyMatch(applyId);
    }

    @PostMapping("findByApproveId")
    public ApplyMatch findByApproveId(@RequestBody Long approveId) {
        return applyMatchDao.findByApproveId(approveId);
    }

    @PostMapping("applyMatch")
    public void applyMatch(@RequestBody ApplyMatchVo matchVo)throws ApplicationException  {
        applyMatchService.applyMatch(matchVo);
    }

    @PostMapping("applyMatchIous")
    public void applyMatchIous(@RequestBody ApplyMatchVo matchVo) throws ApplicationException {
        applyMatchService.applyMatchIous(matchVo);
    }


}

