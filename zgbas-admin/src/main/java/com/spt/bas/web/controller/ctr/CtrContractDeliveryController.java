package com.spt.bas.web.controller.ctr;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractDelivery;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.ICtrContractDeliveryClient;
import com.spt.bas.client.remote.ICtrProductClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.CtrContractChooseVo;
import com.spt.bas.client.vo.CtrContractDeliveryVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.XssExcelExp;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping(value = "/ctr/delivery")
public class CtrContractDeliveryController extends PageController<CtrContractDelivery, BaseVo> {

    @Autowired
    private ICtrContractDeliveryClient ctrContractDeliveryClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IPmProcessClient ProcessClient;
    @Autowired
    private ICtrProductClient ctrProductClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<CtrContractDelivery> getService() {
        return ctrContractDeliveryClient;
    }


    //保存数据
    @RequestMapping(value = "save")
    public void save(@RequestBody @Valid @ModelAttribute("preload") CtrContractDeliveryVo ctrContractDeliveryVo, HttpServletRequest request, HttpServletResponse response) {
        try {
            CtrContractChooseVo ctrContractChooseVo = ctrContractClient.findByContractId(ctrContractDeliveryVo.getContractId());
            ctrContractDeliveryVo.setContractNo(ctrContractChooseVo.getContractNo());
            ctrContractDeliveryVo.setOurCompanyName(ctrContractChooseVo.getOurCompanyName());
            ctrContractDeliveryVo.setMatchUserId(ShiroUtil.getCurrentUserId());
            SysDeptSdk sysDept = authOpenFacade.findDeptByUserId(ShiroUtil.getCurrentUserId());
            ctrContractDeliveryVo.setDeptId(Objects.nonNull(sysDept) ? sysDept.getDeptId() : 0L);
            ctrContractDeliveryClient.save(ctrContractDeliveryVo);
            CtrContract ctrContract = new CtrContract();
            ctrContractChooseVo.setDeliveryStaus(BasConstants.APPROVE_DELIVERY_STAUS);
            BeanUtils.copyProperties(ctrContractChooseVo, ctrContract);
            ctrContractClient.save(ctrContract);
            RenderUtil.renderText("success", response);
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderText("fail", response);
        }

    }


    @RequestMapping("select")
    public void select(Model model, HttpServletRequest request,HttpServletResponse response) {
        Long id = new Long(request.getParameter("id"));
        String waybillCode = request.getParameter("waybillCode");
        CtrContractDelivery delivery = null;
        if (StringUtils.isNotBlank(waybillCode) && !StringUtils.equals("undefined", waybillCode)) {
            delivery = ctrContractDeliveryClient.findByDeliveryId(waybillCode);
        } else {
            // 非则一的根据合同id查询送货通知单
            delivery = ctrContractDeliveryClient.findByContractId(id);
        }
        try {
            String deliveryDate = DateOperator.formatDate(delivery.getDeliveryDate(), "yyyy年MM月dd日");
            String deliveryDateMust = DateOperator.formatDate(delivery.getDeliveryDateMust(), "yyyy年MM月dd日");
            delivery.getReceiveContactor();
            delivery.getReceiveContactPhone();
            delivery.getReceiveAddress();
            delivery.getTransportContactor();
            delivery.getTransportContactPhone();
            delivery.getTransportAddress();
            delivery.getDeliveryAddress();
            delivery.getDeliveryContactPhone();
            delivery.getDeliveryContactor();
            delivery.getCarryNumber().toString();
            delivery.getTransportCompany();
            delivery.getPlateNumber();
            delivery.getDriverName();
            delivery.getDriverPhone();
            delivery.getDriverCardNo();
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("操作错误，请编辑完整送货单", response);
        }
    }

    @RequestMapping("inssert")
    public String detail(Model model, HttpServletRequest request) {
        Long id = new Long(request.getParameter("id"));
        String waybillCode = request.getParameter("waybillCode");
        CtrContractDelivery delivery = null;
        if (StringUtils.isNotBlank(waybillCode) && !StringUtils.equals("undefined", waybillCode)) {
            delivery = ctrContractDeliveryClient.findByDeliveryId(waybillCode);
        } else {
            // 非则一的根据合同id查询送货通知单
            delivery = ctrContractDeliveryClient.findByContractId(id);

        }
        if (delivery != null) {
            CtrContractDeliveryVo vo = new CtrContractDeliveryVo();
            BeanUtils.copyProperties(delivery, vo);
            CtrContractChooseVo ctrContractChooseVo = ctrContractClient.findByContractId(id);
            List<CtrProduct> byOutCtrContractId = ctrProductClient.findByOutCtrContractId(id);
            CtrProduct product = byOutCtrContractId.get(0);
            vo.setWaybillCode(waybillCode);
            vo.setCustomerOrderCode(ctrContractChooseVo.getCustomerOrderCode());
            vo.setContractId(id);
            vo.setProductName(product.getProductName());
            vo.setBrandNumber(product.getBrandNumber());
            vo.setDealNumber(product.getDealNumber());
            vo.setDealPrice(product.getDealPrice());
            vo.setFactoryName(product.getFactoryName());
            vo.setFactoryId(product.getFactoryId());
             if(StringUtils.isNotBlank(waybillCode) && !StringUtils.equals("undefined", waybillCode)){
                 vo.setCarryAmount(delivery.getCarryAmount());
                 vo.setLoadAmount(delivery.getLoadAmount());
                 vo.setTotalAmount(delivery.getCarryAmount().add(ctrContractChooseVo.getWarehouseAmount()));
             }else{
                 vo.setCarryAmount(ctrContractChooseVo.getTransportAmount());
                 vo.setLoadAmount(ctrContractChooseVo.getWarehouseAmount());
                 vo.setTotalAmount(ctrContractChooseVo.getTransportAmount().add(ctrContractChooseVo.getTransportAmount()));
             }
            vo.setDealPriceAmount(product.getDealPrice().multiply(product.getDealNumber()));

            model.addAttribute("product", vo);
        } else {
            CtrContractChooseVo ctrContractChooseVo = ctrContractClient.findByContractId(id);
            List<CtrProduct> byOutCtrContractId = ctrProductClient.findByOutCtrContractId(id);
            CtrProduct product = byOutCtrContractId.get(0);
            CtrContractDeliveryVo ctrContractDeliveryVo = new CtrContractDeliveryVo();
            ctrContractDeliveryVo.setWaybillCode(waybillCode);
            ctrContractDeliveryVo.setCustomerOrderCode(ctrContractChooseVo.getCustomerOrderCode());
            ctrContractDeliveryVo.setContractId(id);
            ctrContractDeliveryVo.setProductName(product.getProductName());
            ctrContractDeliveryVo.setBrandNumber(product.getBrandNumber());
            ctrContractDeliveryVo.setDealNumber(product.getDealNumber());
            ctrContractDeliveryVo.setDealPrice(product.getDealPrice());
            ctrContractDeliveryVo.setFactoryName(product.getFactoryName());
            ctrContractDeliveryVo.setFactoryId(product.getFactoryId());
            if(StringUtils.isNotBlank(waybillCode) && !StringUtils.equals("undefined", waybillCode)){
                ctrContractDeliveryVo.setCarryAmount(delivery.getCarryAmount());
                ctrContractDeliveryVo.setLoadAmount(delivery.getLoadAmount());
                ctrContractDeliveryVo.setTotalAmount(delivery.getCarryAmount().add(ctrContractChooseVo.getWarehouseAmount()));
            }else{
                ctrContractDeliveryVo.setCarryAmount(ctrContractChooseVo.getTransportAmount());
                ctrContractDeliveryVo.setLoadAmount(ctrContractChooseVo.getWarehouseAmount());
                ctrContractDeliveryVo.setTotalAmount(ctrContractChooseVo.getTransportAmount().add(ctrContractChooseVo.getWarehouseAmount()));
            }
            ctrContractDeliveryVo.setDealPriceAmount(product.getDealPrice().multiply(product.getDealNumber()));
            model.addAttribute("product", ctrContractDeliveryVo);
        }
        return "bs/delivery";
    }

    @RequestMapping("show")
    public String show(Model model, HttpServletRequest request) {
        Long id = new Long(request.getParameter("id"));// 合同id
        String waybillCode = request.getParameter("waybillCode");
        CtrContractDeliveryVo vo = new CtrContractDeliveryVo();
        // 则一运单号非空
        if (StringUtils.isNotBlank(waybillCode) && !StringUtils.equals("undefined", waybillCode)) {
            CtrContractDelivery delivery = ctrContractDeliveryClient.findByDeliveryId(waybillCode);
        } else {
            // 非则一的根据合同id查询送货通知单
            CtrContractDelivery delivery = ctrContractDeliveryClient.findByContractId(id);
            BeanUtils.copyProperties(delivery, vo);
        }
        CtrContractChooseVo ctrContractChooseVo = ctrContractClient.findByContractId(id);
        List<CtrProduct> byOutCtrContractId = ctrProductClient.findByOutCtrContractId(id);
        CtrProduct product = byOutCtrContractId.get(0);
        vo.setContractId(id);
        vo.setProductName(product.getProductName());
        vo.setBrandNumber(product.getBrandNumber());
        vo.setDealNumber(product.getDealNumber());
        vo.setDealPrice(product.getDealPrice());
        vo.setFactoryName(product.getFactoryName());
        vo.setFactoryId(product.getFactoryId());
        if(StringUtils.isNotBlank(waybillCode) && !StringUtils.equals("undefined", waybillCode)){
            CtrContractDelivery delivery = ctrContractDeliveryClient.findByDeliveryId(waybillCode);
            vo.setCarryAmount(delivery.getCarryAmount());
            vo.setLoadAmount(delivery.getLoadAmount());
            vo.setTotalAmount(delivery.getCarryAmount().add(ctrContractChooseVo.getWarehouseAmount()));
        }else{
            vo.setCarryAmount(ctrContractChooseVo.getTransportAmount());
            vo.setTotalAmount(ctrContractChooseVo.getTransportAmount().add(ctrContractChooseVo.getWarehouseAmount()));
            vo.setLoadAmount(ctrContractChooseVo.getWarehouseAmount());
        }
        vo.setDealPriceAmount(product.getDealPrice().multiply(product.getDealNumber()));
        model.addAttribute("product", vo);
        return "bs/delivery";
    }


    //物流管理-送货单
    @RequestMapping(value = "contractDelivery")
    public String findDCSXYS(Model model) {
        model = initData(model);
        model = checkGcy(model);
        model = checkFileAuthority(model);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_BB);
        model.addAttribute("matchCreditFlg", true);
        return "ctr/contractDelivery";
    }

    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletResponse response, HttpServletRequest request)
            throws ApplicationException, ParseException, IOException {
        Map<String, String> paramMap = new HashMap<>();
        Long id = new Long(request.getParameter("id"));
        String waybillCode = request.getParameter("waybillCode");
        CtrContractDelivery delivery = null;
        // 则一运单号非空
        if (StringUtils.isNotBlank(waybillCode) && !StringUtils.equals("undefined", waybillCode)) {
            delivery = ctrContractDeliveryClient.findByDeliveryId(waybillCode);
        } else {
            // 非则一的根据合同id查询送货通知单
            delivery = ctrContractDeliveryClient.findByContractId(id);
        }
        if (delivery != null) {
            CtrContractChooseVo ctrContractChooseVo = ctrContractClient.findByContractId(id);
            List<CtrProduct> products = ctrProductClient.findByOutCtrContractId(id);
            CtrProduct product = products.get(0);
                String deliveryDate = DateOperator.formatDate(delivery.getDeliveryDate(), "yyyy年MM月dd日");
                String deliveryDateMust = DateOperator.formatDate(delivery.getDeliveryDateMust(), "yyyy年MM月dd日");
                paramMap.put("receiveContactor", delivery.getReceiveContactor());
                paramMap.put("receiveContactPhone", delivery.getReceiveContactPhone());
                paramMap.put("receiveAddress", delivery.getReceiveAddress());
                paramMap.put("transportContactor", delivery.getTransportContactor());
                paramMap.put("transportContactPhone", delivery.getTransportContactPhone());
                paramMap.put("transportAddress", delivery.getTransportAddress());
                paramMap.put("deliveryDateMust", deliveryDateMust);
                paramMap.put("deliveryAddress", delivery.getDeliveryAddress());
                paramMap.put("deliveryContactPhone", delivery.getDeliveryContactPhone());
                paramMap.put("deliveryContactor", delivery.getDeliveryContactor());
                paramMap.put("deliveryDate", deliveryDate);
                paramMap.put("contractNo", ctrContractChooseVo.getContractNo());
                paramMap.put("productName", product.getProductName());
                paramMap.put("brandNumber", product.getBrandNumber());
                paramMap.put("factoryName", product.getFactoryName());
                paramMap.put("dealNumber", delivery.getCarryNumber().toString());
                paramMap.put("dealPrice", ctrContractChooseVo.getDealPrice().toString());
                paramMap.put("conpanyName", ctrContractChooseVo.getCompanyName());
                paramMap.put("carrier", delivery.getTransportCompany());
                paramMap.put("dealPriceAmount", (delivery.getCarryNumber().multiply(product.getDealPrice()).toString()));
                paramMap.put("ourCompanyName", ctrContractChooseVo.getOurCompanyName());
                paramMap.put("companyName", ctrContractChooseVo.getCompanyName());
                paramMap.put("plateNumber", delivery.getPlateNumber());
                paramMap.put("driverName", delivery.getDriverName());
                paramMap.put("driverPhone", delivery.getDriverPhone());
                paramMap.put("driverCardNo", delivery.getDriverCardNo());
                paramMap.put("carryAmount", delivery.getCarryAmount().toString());
                paramMap.put("loadAmount", delivery.getLoadAmount().toString());
                XssExcelExp.excelExp("/excel/delivery.xlsx", paramMap, response, "送货通知单.xlsx");
        }
    }

    /**
     * 判断是否有附件替换功能
     *
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

    /**
     * 判断是否是观察员
     *
     * @param model
     * @return
     */
    private Model checkGcy(Model model) {
        boolean isGcy = false;
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            isGcy = true;
        }
        model.addAttribute("isGcy", isGcy);
        return model;
    }

    public Model initData(Model model) {
        model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
        model.addAttribute("deliveryTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        model.addAttribute("applyTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
        model.addAttribute("sellAndBuyStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELLSTATUS)));
        model.addAttribute("contractsTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPES)));
        //model.addAttribute("deliveryModeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("contractAttrJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTATTR)));
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //业务小类
        model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = ProcessClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
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
        return model;
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


    @RequestMapping(value = "deliveryList")
    public void deliveryList(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Boolean piccRemainCredit = searchVo.getPiccRemainCredit();
        Map<String, Object> searchParams = searchVo.getSearchParams();
        if (piccRemainCredit != null) {
            if (piccRemainCredit) {
                searchParams.put("GTEM_piccRemainCredit", BigDecimal.ZERO);
            } else {
                searchParams.put("LTM_piccRemainCredit", BigDecimal.ZERO);
            }
        }
        //中心负责人ID
        Long deptLeader = getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }

        // 业务助理 查看本业务部所有预算
        if (SecurityUtils.getSubject().isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
        }

        //1.可以查看本中心所有预售合同权限
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        //2.可以查看本中心所有合同权限
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        //2.可以查看本中心所有的保理合同
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_VIEWBL.getPermissionCode())) {
            searchVo.setSearchType("B");
        }

        Boolean saasContractFlg = searchVo.getSaasContractFlg();
        if (saasContractFlg != null && saasContractFlg) {
            searchVo.setAdmin(true);
            searchParams.remove("EQL_enterpriseId");
        }
        logger.info("searchVo : " + JsonUtil.obj2Json(searchVo));
        Map<String, Object> footer = new HashMap<>();
        Page<CtrContractDeliveryVo> page = ctrContractDeliveryClient.findPageContract(searchVo);
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    public Long getDeptLeader() {
        Long deptLeader = 0L;
        try {
            DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getCurrentUserId(), PmConstants.NODE_TYPE_CENTER, ShiroUtil.getEnterpriseId());
            deptLeader = authOpenFacade.findDeptLeader(deptSearchVo);
            logger.info("getDeptLeader : " + JsonUtil.obj2Json(deptLeader));
        } catch (Exception e) {
            logger.error("getDeptLeader error:{}",e);
        }
        return Objects.isNull(deptLeader) ? 0L : deptLeader;
    }


}
