package com.spt.bas.web.controller.ctr;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractFee;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.ICtrContractFeeClient;
import com.spt.bas.client.vo.ContractFeeSearchVo;
import com.spt.bas.client.vo.CtrContractChooseVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/ctr/contractFee")
public class CtrContractFeeController extends PageController<CtrContractFee, BaseVo> {
    @Autowired
    private ICtrContractFeeClient ctrContractFeeClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Autowired
    private ICtrContractClient ctrContractClient;

    @Override
    public BaseClient<CtrContractFee> getService() {
        return ctrContractFeeClient;
    }

    // 转发费用登记列表页面
    @RequestMapping(value = "")
    public String index(Model model) {
        //获取业务员树
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("contractFeeType", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTFEETYPE)));
        model.addAttribute("contractFeeRate", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTFEERATE)));        //利率
        return "ctr/contract_fee";
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }

    // 展示合同费用信息分页
    @RequestMapping(value = "findContractFeeList")
    public void findTemplateList(ContractFeeSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        Map<String, Object> footer = new HashMap<>();
        Page<CtrContractFee> page = ctrContractFeeClient.findPageContractFee(searchVo);
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    // 查看费用详情
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        CtrContractFee contractFee = getEntity(id);
        if (null != contractFee.getContractId()) {
            CtrContractChooseVo findByContractId = ctrContractClient.findByContractId(contractFee.getContractId());
            contractFee.setContractNo(findByContractId.getContractNo());
        }
        model.addAttribute("entity", contractFee);
        return "ctr/contractFee_detail";
    }

    @ModelAttribute("preload")
    public CtrContractFee getEntity(@RequestParam(value = "id", required = false) Long id) {
        CtrContractFee entity = null;
        if (id != null) {
            if (id > 0L)
                entity = getService().getEntity(id);
            else {
                entity = new CtrContractFee();
                entity.setId(0L);
            }
        }
        return entity;
    }

    // 更改附件ID
    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            ctrContractFeeClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    // 保存/更新费用
    @RequestMapping(value = "saveContractFee")
    public void saveTemplate(@Valid @ModelAttribute("preload") CtrContractFee ctrContractFee, HttpServletRequest request, HttpServletResponse response) {
        try {
            CtrContract ctrContract = new CtrContract();
            ctrContract.setContractNo(ctrContractFee.getContractNo());
            CtrContract contractNo = ctrContractClient.findByContractNo(ctrContract);
            if (null != contractNo) {
                //是否添加过合同同一类型的费用
                ctrContractFee.setContractId(contractNo.getId());
                ctrContractFee.setBizUserId(ShiroUtil.getCurrentUserId());
                ctrContractFee.setBizUserName(ShiroUtil.getCurrentUserName());
                ctrContractFee.setEnterpriseId(ShiroUtil.getEnterpriseId());
                CtrContractFee saveFee = ctrContractFeeClient.save(ctrContractFee);
                if (null != saveFee) {
                    //添加/修改合同表里的费用
                    CtrContract entity = ctrContractClient.getEntity(saveFee.getContractId());
                    switch (saveFee.getFeeType()) {
                        //仓储费
                        case BasConstants.DICT_TYPE_FEETYPE_WF:
                            entity.setWarehouseAmount(saveFee.getFeeAmount());
                            break;
                        //运输费
                        case BasConstants.DICT_TYPE_FEETYPE_TF:
                            entity.setTransportAmount(saveFee.getFeeAmount());
                            break;
                        //装车费
                        case BasConstants.DICT_TYPE_FEETYPE_LF:
                            entity.setLoadingAmount(saveFee.getFeeAmount());
                            break;
                        //罚息费
                        case BasConstants.DICT_TYPE_FEETYPE_IPF:
                            entity.setInterestAmount(saveFee.getFeeAmount());
                            break;
                        default:
                            break;
                    }
                    ctrContractClient.save(entity);
                }
                RenderUtil.renderText("保存成功！", response);
                return;
            } else {
                RenderUtil.renderText("合同不存在！", response);
            }
        } catch (Exception e) {
            logger.error("saveContractFee:", e);
            RenderUtil.renderText("saveTemplate:" + e.getMessage(), response);
        }
    }
}
