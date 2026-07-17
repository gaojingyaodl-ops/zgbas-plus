package com.spt.bas.web.controller.apply;


import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApplyCtrContractFactorVo;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/apply/ctrContractFactor")
public class ApplyCtrContractFactorController extends PageController<ApplyCtrContractFactor, BaseVo> {

    @Resource
    private IApplyCtrContractFactoClient applyCtrContractFactoClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IPmProcessClient processClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private ICtrContractClient ctrContractClient;
    /**
     * 中光企业id(固定)
     */
    private static final Long ZG_ENTERPRISE_ID = BasConstants.ZG_ENTERPRISE_ID;

    @Autowired
    private  IPmProcessClient iPmProcessClient;

    @Autowired
    private IPmApproveClient approveClient;



    @Override
    public BaseClient<ApplyCtrContractFactor> getService() {
        return applyCtrContractFactoClient;
    }

//    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request) {
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        // 申请人
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));

        return "apply/applyContractFactor";
    }

    //代采赊销合同列表
    @RequestMapping(value = "findDCSXBL")
    public String findDCSX(Model model) {
        model = initData(model);
        model = checkGcy(model);
        model = checkFileAuthority(model);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_BB);
        model.addAttribute("matchCreditFlg", true);
        return "apply/applyContractFactor";
    }


    public Model initData(Model model) {
        model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_FACTORCONTRACTSTATUS)));
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
        model.addAttribute("deliveryTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        model.addAttribute("applyTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
        model.addAttribute("sellAndBuyStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELLSTATUS)));
        model.addAttribute("contractsTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPES)));
        //model.addAttribute("deliveryModeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("deliveryModeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
//        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("contractAttrJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTATTR)));
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //业务小类
        model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
        //预售合同发起采购权限
        Boolean preSellFlg = canStartBuy();
        model.addAttribute("preSellFlg", preSellFlg);
        //确认收货权限
        Boolean confirmFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_CONFIRM.getPermissionCode());
        model.addAttribute("confirmFlg", confirmFlg);
        //签约权限
        Boolean signingFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_SIGNING.getPermissionCode());
        model.addAttribute("signingFlg", signingFlg);
        //刷新电子合同权限
        boolean refreshFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_REFRESH_CONTRACT.getPermissionCode());
        model.addAttribute("refreshFlg", refreshFlg);
        //资金方合同查看权限
        model.addAttribute("funderViewFlg", ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode()));
        return model;
    }



    @ModelAttribute("preload")
    public BasBrand getEntity(@RequestParam(value = "id", required = false) Long id,
                              @RequestParam(value = "processCode", required = false) String processCode) {
        BasBrand enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (BasBrand) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                BasBrand basBrand = new BasBrand();
                basBrand.setId(0L);
                return basBrand;
            }
        }
        return enObject;
    }

    private Boolean canStartBuy() {
        boolean preSellFlg = true;
        String deptId = Optional.ofNullable(ShiroUtil.getDeptId()).orElse(0L).toString();
        //查看所有预售合同权限
        boolean viewPreSell = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode());
        //需要限制预售发起部门ID
        String preSellDeptId = BsDictUtil.getValue(ShiroUtil.getEnterpriseId(), BasConstants.PRESELLDEPTID, BasConstants.DEPTID);
        if (preSellDeptId != null && preSellDeptId.contains(deptId) && !viewPreSell) {
            preSellFlg = false;
        }
        return preSellFlg;
    }


    private String getStatus(Long id) {
        if (id != null && id > 0L) {
            PmApproveContents entity = pmApproveContentsClient.getEntity(id);
            if (entity != null) {
                return entity.getStatus();
            }
        }
        return BasConstants.APPROVE_STATUS_N;
    }

    /**
     * 判断是否是观察员
     *
     * @param model
     *
     * @return
     */
    private Model checkGcy(Model model) {
        model.addAttribute("isGcy", ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode()));
        return model;
    }

    /**
     * 判断是否有附件替换功能
     * @param model
     * @return
     */
    private Model checkFileAuthority(Model model) {
        boolean canEditContractFile = false;
        // 业务助理
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BIZ_OPE.getPermissionCode())) {
            canEditContractFile = true;
        }
        model.addAttribute("canEditContractFile", canEditContractFile);
        return model;
    }


    //确认收款
    @RequestMapping(value = "confirm/{id}", method = RequestMethod.GET)
    public String confirm(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        CtrContract entity = ctrContractClient.getEntity(id);
        ApplyCtrContractFactor confirm = applyCtrContractFactoClient.confirm( entity.getContractNo());
        model.addAttribute("entity", confirm);
        return "ctr/contract-bl-confirm";
    }

    //确认收款实现
    @RequestMapping(value = "confirmimpl", method = RequestMethod.POST)
    public void confirmimpl(ApplyCtrContractFactorVo applyCtrContractFactorVo, HttpServletResponse response) {
        ApplyCtrContractFactor confirm = applyCtrContractFactoClient.confirm( applyCtrContractFactorVo.getContractNo());
        confirm.setLoanDate(applyCtrContractFactorVo.getLoanDate());
        confirm.setLoanAmount(applyCtrContractFactorVo.getLoanAmount());
        confirm.setFactorStatus(BasConstants.FACTOR_STATUS_F);
        try {
            applyCtrContractFactoClient.updateFacto(applyCtrContractFactorVo);
            CtrContract contract=new CtrContract();
            contract.setContractNo(confirm.getContractNo());
             CtrContract byContractNo = ctrContractClient.findByContractNo(contract);
            byContractNo.setFactorStatus(BasConstants.FACTOR_STATUS_F);
             byContractNo.setFactoringAmount(applyCtrContractFactorVo.getLoanAmount());
            ctrContractClient.save(byContractNo);
            RenderUtil.renderSuccess("success", response);
        }catch (Exception e){
            RenderUtil.renderFailure("保存失败", response);
        }
    }


    //还款给银行
    @RequestMapping(value = "repayment/{contractId}", method = RequestMethod.POST)
    public void repayment(@PathVariable("contractId") String contractId) {
        ApplyCtrContractFactor confirm = applyCtrContractFactoClient.confirm(contractId);
        ApplyPay pay = new ApplyPay();
        pay.setContractId(confirm.getContractId());
        pay.setContractNo(confirm.getContractNo());
        pay.setTotalAmount(confirm.getLoanAmount());
        pay.setPayAmount(confirm.getLoanAmount());
        pay.setCompanyName("放款银行");
        pay.setEnterpriseId(ZG_ENTERPRISE_ID);
        pay.setApproveId(confirm.getApproveId());
        pay.setOurCompanyName(confirm.getOurCompanyName());
        pay.setPayDate(new Date());
        pay.setPayMode("T");
        pay.setPayType("A");
        String bizEntityJson = JsonUtil.obj2Json(pay);
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(BasConstants.APPLY_DCSXBL_PAY);
        PmProcess process = iPmProcessClient.findByProcessCode(searchVo);
        if (process == null) {
            try {
                throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
        }
        String value = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.Factoring_Repayment_Originator, BasConstants.Factoring_Repayment_Originator_User);
        SysUserSdk SysUserSdk = authOpenFacade.findUserById(Long.parseLong(value));
        startVo.setUserId(SysUserSdk.getUserId());
        startVo.setUserName(SysUserSdk.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        try {
            approveClient.startFlow(startVo);
        } catch (WebApplicationException e) {
            e.printStackTrace();
        }
    }




}
