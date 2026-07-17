package com.spt.bas.web.controller.ctr;



import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.ICtrContractChainClient;
import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.report.client.entity.RptCtrContractOrverdur;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping(value = "/ctr/dcChontractChain")
public class CtrDcContractChainController extends PageController<CtrContractChain, BaseVo> {

    @Autowired
    private ICtrContractChainClient ctrContractChainClient;
    @Autowired
    private IPmApproveClient pmApproveClient;

    @Override
    public BaseClient<CtrContractChain> getService() {
        return ctrContractChainClient;
    }

    @RequestMapping(value = "")
    public String contract(Model model, HttpServletRequest request) {
        return "ctr/dcContractChain";
    }


    @RequestMapping(value = "approve/{approveId}")
    public void approve(@PathVariable("approveId") Long approveId, HttpServletResponse response){
        PmApprove pmApprove = pmApproveClient.findApproveNoByApproveId(approveId);
        RenderUtil.renderJson(pmApprove.getId(),response);
    }




}
