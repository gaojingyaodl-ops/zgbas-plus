package com.spt.bas.web.controller.ctr;

import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.client.entity.CtrContractFollow;
import com.spt.bas.client.remote.ICtrContractFollowClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/10/17 14:48
 * @Version 1.0
 */
@Controller
@RequestMapping("/ctr/follow")
public class CtrContractFollowController extends SingleCrudControll<CtrContractFollow, BaseVo> {
    @Resource
    private ICtrContractFollowClient ctrContractFollowClient;

    @Override
    public BaseClient<CtrContractFollow> getService() {
        return ctrContractFollowClient;
    }

    @RequestMapping(value = "openFollowDetail/{contractId}", method = RequestMethod.GET)
    public String openFollowDetail(@PathVariable("contractId") Long contractId, Model model) {
        model.addAttribute("contract_id", contractId);
        model.addAttribute("followCreateUserId", ShiroUtil.getCurrentUserId());
        model.addAttribute("followCreateUserName", ShiroUtil.getCurrentUserName());
        return "ctr/warn-follow";
    }

    @RequestMapping(value = "getFollowDetail/{contractId}", method = RequestMethod.POST)
    public void getFollowDetail(@PathVariable("contractId") Long contractId, HttpServletResponse response) {
        List<CtrContractFollow> followList = ctrContractFollowClient.findByCtrContractId(contractId);
        JsonEasyUI.renderListJson(response, followList);
    }
}
