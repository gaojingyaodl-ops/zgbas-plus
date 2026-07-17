/**
 *
 */
package com.spt.bas.web.controller.bs;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.entity.SysFile;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileSearchVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.cache.UserCache;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.auth.sdk.vo.UserSearchVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BaseException;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.constant.Status;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.purchase.wx.client.constant.SaveInfoType;
import com.spt.bas.purchase.wx.client.entity.CompanyUser;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.client.remote.ISaveTempClient;
import com.spt.bas.purchase.wx.client.remote.IWxUserClient;
import com.spt.bas.purchase.wx.client.vo.CompanyOnLineApplyVo;
import com.spt.bas.report.client.entity.RptCompanyCreditInfo0;
import com.spt.bas.report.client.vo.RptPartBsCompanyVo;
import com.spt.bas.web.config.BasicErrorController;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.FormConfigUtil;
import com.spt.bas.web.util.LogUtil;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.http.util.IPUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 企业管理
 *
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/bs/company")
public class BsCompanyController extends PageController<BsCompany, BaseVo> {
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private com.spt.bas.report.client.remote.IRptBsCompanyClient reportCompanyClient;
    @Autowired
    private IBsAreaClient areaClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsCompanyShareClient bsCompanyShareClient;
    @Autowired
    private IBsCompanyOphisClient bsCompanyOphisClient;
    @Autowired
    private IBsCompanyAccountClient bsCompanyAccountClient;
    @Autowired
    private IBsCompanyIndustryClient bsCompanyIndustryClient;
    @Autowired
    private IPmProcessClient processClient;
    @Autowired
    private IBsCompanyUserBakClient bsCompanyUserBakClient;
    @Autowired
    private IWxUserClient wxUserClient;
    @Autowired
    private ISaveTempClient saveTempClient;
    @Autowired
    private IFileRecordClient fileRecordClient;
    @Autowired
    private com.spt.bas.report.client.remote.IRptBsCompanyClient bsCompanyClient;
    private String fileServerUrl="https://file.tosupply.cn/ys-file-server";
    @Resource
    private FileRemote fileRemote;
    @Resource
    private IBsLogClient bsLogClient;

    @Override
    public BaseClient<BsCompany> getService() {
        return companyClient;
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("companyStatus",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_COMPANY_STATUS)));
        model.addAttribute("creditRating",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));// 信用等级
        model.addAttribute("companyType",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_COMPANYTYPE)));// 客户分类
        model.addAttribute("companyGrade",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));// 客户分类
        model.addAttribute("onLineFlg",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ONLiNEFLG)));// 线上化查询
        model.addAttribute("logisticsSealTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_LOGISTICSSEALTYPE)));//物流章类型
        List<BsArea> bsAreaLs = areaClient.findAll();
        model.addAttribute("areaJson", JsonUtil.obj2Json(bsAreaLs));
        UserSearchVo userSearchVo = new UserSearchVo(ShiroUtil.getEnterpriseId(), false);
        List<SysUserSdk> userAll = authOpenFacade.findUserAll(userSearchVo);
        model.addAttribute("userJson", JsonUtil.obj2Json(userAll));
        // 获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo();
        deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        EasyTreeNode deptNodes = EasyTreeUtil2.getDeptTree(deptList, false,true);
        model.addAttribute("deptNameTree", JsonUtil.obj2Json(deptNodes.getChildren()));
        //行业分类
        List<EasyTreeNode> allIndustryTree = bsCompanyIndustryClient.getAllIndustryTree();
        model.addAttribute("companyIndustryJson", JsonUtil.obj2Json(allIndustryTree));
        // 删除企业权限
        boolean canDelete = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_DELETE.getPermissionCode());
        //数据导出权限
        boolean canExcel = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EPX.getPermissionCode());
        //查看企业详情权限
        boolean viewdetail = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_VIEWDETAIL.getPermissionCode());
        // 追加经办人权限
        boolean additionalmanagerPermission = ShiroUtil.isPermitted(PermissionEnum.BAS_ADDITIONAL_MANAGER.getPermissionCode());
        model.addAttribute("deptAllJson", JsonUtil.obj2Json(deptList));
        model.addAttribute("viewdetail", viewdetail);
        model.addAttribute("canExcel", canExcel);
        model.addAttribute("canDelete", canDelete);
        model.addAttribute("additionalmanagerPermission", additionalmanagerPermission);
        model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
        // 查看所有用户权限
        boolean lookAllCompany = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_VIEWALL.getPermissionCode());
        model.addAttribute("lookAllCompany", lookAllCompany);
        // 区域总企业操作权限
        boolean companyDeptLeaderPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_DEPT_LEADER.getPermissionCode());
        model.addAttribute("companyDeptLeaderPerm", companyDeptLeaderPerm);
        // 是否不可操作
        boolean operationFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_OPERATION.getPermissionCode());
        model.addAttribute("operationFlg", operationFlg);
        boolean zgbasRiskFlag = ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_RISK.getPermissionCode());
        model.addAttribute("zgbasRiskFlag", zgbasRiskFlag);
        // 风控编辑权限
        boolean editFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EDIT.getPermissionCode());
        model.addAttribute("editFlg", editFlg);
        boolean wfqAuthFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_WFQAUTH.getPermissionCode());
        model.addAttribute("wfqAuthFlg", wfqAuthFlg);
        boolean showWfqData = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_WFQ_VIEWDATA.getPermissionCode());
        model.addAttribute("showWfqData", showWfqData);
        boolean importPiccDataPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_IMPORT_PICCDATA.getPermissionCode());
        model.addAttribute("importPiccDataPerm", importPiccDataPerm);
        boolean importDaDiDataPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_IMPORT_DADI_AMOUNT.getPermissionCode());
        model.addAttribute("importDaDiDataPerm", importDaDiDataPerm);
        boolean importZhongYinDataPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_IMPORT_ZHONGYIN_AMOUNT.getPermissionCode());
        model.addAttribute("importZhongYinDataPerm", importZhongYinDataPerm);
        boolean importCreditReportPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_IMPORT_CREDIT_REPORT.getPermissionCode());
        model.addAttribute("importCreditReportPerm", importCreditReportPerm);
        boolean companyExportPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EXPORT.getPermissionCode());
        model.addAttribute("companyExportPerm", companyExportPerm);
        boolean companyExportAccessReportPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EXPORT_ACCESS_REPORT.getPermissionCode());
        model.addAttribute("companyExportAccessReportPerm", companyExportAccessReportPerm);
        boolean companyExportCreditInfo0 = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EXPORT_CREDIT_INF0_0.getPermissionCode());
        model.addAttribute("companyExportCreditInfo0", companyExportCreditInfo0);
        // 企业分配权限
        boolean companyAssignPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_BAS_COMPANY_ASSIGN.getPermissionCode());
        model.addAttribute("companyAssignPerm", companyAssignPerm);
        // 企业指派权限
        boolean companyAssignedPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_BAS_COMPANY_ASSIGNED.getPermissionCode());
        model.addAttribute("companyAssignedPerm", companyAssignedPerm);
        // 企业分享权限
        boolean companySharePerm = ShiroUtil.isPermitted(PermissionEnum.PERM_BAS_COMPANY_SHARE.getPermissionCode());
        model.addAttribute("companySharePerm", companySharePerm);
        // 企业释放权限
        boolean companyReleasePerm = ShiroUtil.isPermitted(PermissionEnum.PERM_BAS_COMPANY_RELEASE.getPermissionCode());
        model.addAttribute("companyReleasePerm", companyReleasePerm);
        model.addAttribute("plasticTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_PLASTIC_TYPE)));
        return "bs/company";
    }

    /**
     * 获取准入管理公司列表
     * @param model
     * @return
     */
    @RequestMapping(value = "allowedList")
    public String allowedList(Model model) {
        model.addAttribute("enableFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        model.addAttribute("companyStatus",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_COMPANY_STATUS)));
        model.addAttribute("creditRating",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));// 信用等级
        model.addAttribute("companyType",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_COMPANYTYPE)));// 客户分类
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        List<BsArea> bsAreaLs = areaClient.findAll();
        model.addAttribute("areaJson", JsonUtil.obj2Json(bsAreaLs));
        UserSearchVo userSearchVo = new UserSearchVo( ShiroUtil.getEnterpriseId(), true);
        List<SysUserSdk> userAll = authOpenFacade.findUserAll(userSearchVo);
        model.addAttribute("userJson", JsonUtil.obj2Json(userAll));
        // 获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        //行业分类
        List<EasyTreeNode> allIndustryTree = bsCompanyIndustryClient.getAllIndustryTree();
        model.addAttribute("companyIndustryJson", JsonUtil.obj2Json(allIndustryTree));
        // 删除企业权限
        boolean canDelete = false;
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_DELETE.getPermissionCode())) {
            canDelete = true;
        }
        model.addAttribute("canDelete", canDelete);
        model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
        // 查看所有用户权限
        boolean lookAllCompany = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_VIEWALL.getPermissionCode());
        model.addAttribute("lookAllCompany", lookAllCompany);
        // 是否不可操作
        boolean operationFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_OPERATION.getPermissionCode());
        model.addAttribute("operationFlg", operationFlg);
        // 风控编辑权限
        boolean editFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EDIT.getPermissionCode());
        model.addAttribute("editFlg", editFlg);
        boolean zgbasRiskFlag = ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_RISK.getPermissionCode());
        model.addAttribute("zgbasRiskFlag", zgbasRiskFlag);
        model.addAttribute("allowedJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_ALLOWED)));// 是否准入

        model.addAttribute("companyCategoryJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYCATEGORY)));// 企业类别
        model.addAttribute("logisticsSealTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_LOGISTICSSEALTYPE)));//物流章类型
        return "bs/companyAllowedList";
    }

    /**
     * 获取准入管理公司列表
     * @param model
     * @return
     */
    @RequestMapping(value = "quotaList")
    public String quotaList(Model model) {
        model.addAttribute("enableFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        model.addAttribute("companyStatus",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_COMPANY_STATUS)));
        model.addAttribute("creditRating",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));// 信用等级
        model.addAttribute("companyType",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_COMPANYTYPE)));// 客户分类
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));

        List<BsArea> bsAreaLs = areaClient.findAll();
        model.addAttribute("areaJson", JsonUtil.obj2Json(bsAreaLs));
        UserSearchVo userSearchVo = new UserSearchVo(ShiroUtil.getEnterpriseId(), true);
        List<SysUserSdk> userAll = authOpenFacade.findUserAll(userSearchVo);
        model.addAttribute("userJson", JsonUtil.obj2Json(userAll));
        // 获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo();
        deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        //行业分类
        List<EasyTreeNode> allIndustryTree = bsCompanyIndustryClient.getAllIndustryTree();
        model.addAttribute("companyIndustryJson", JsonUtil.obj2Json(allIndustryTree));
        // 删除企业权限
        boolean canDelete = false;
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_DELETE.getPermissionCode())) {
            canDelete = true;
        }
        model.addAttribute("canDelete", canDelete);
        model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
        // 查看所有用户权限
        boolean lookAllCompany = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_VIEWALL.getPermissionCode());
        model.addAttribute("lookAllCompany", lookAllCompany);
        // 是否不可操作
        boolean operationFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_OPERATION.getPermissionCode());
        model.addAttribute("operationFlg", operationFlg);
        // 风控编辑权限
        boolean editFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EDIT.getPermissionCode());
        model.addAttribute("editFlg", editFlg);
        boolean zgbasRiskFlag = ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_RISK.getPermissionCode());
        model.addAttribute("zgbasRiskFlag", zgbasRiskFlag);
        model.addAttribute("companyCategoryJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYCATEGORY)));// 企业类别
        model.addAttribute("logisticsSealTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_LOGISTICSSEALTYPE)));//物流章类型
        return "bs/companyQuotaList";
    }

    @RequestMapping(value = "listCompany")
    public void listCompany(BsCompanySearchVo queryVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(queryVo, request);
        queryVo.setUserId(ShiroUtil.getCurrentUserId());
        queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        //是否有查看所有企业权限
        boolean lookAllCompany = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_VIEWALL.getPermissionCode());
        queryVo.setLookAllCompany(lookAllCompany);
        boolean businessHhrPrem = ShiroUtil.isPermitted(PermissionEnum.PERM_BUSINESS_HHR.getPermissionCode());
        queryVo.setHhrPerm(businessHhrPrem);
        boolean lookCurDeptCompanyPrem = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_VIEWDEPT.getPermissionCode());
        queryVo.setLookCurDeptCompanyPrem(lookCurDeptCompanyPrem);

        String search_eqs_status = queryVo.getSearchParams().get("INS_status") == null ? null : queryVo.getSearchParams().get("INS_status").toString();
        if(search_eqs_status != null && StringUtils.equals("N",search_eqs_status)){
            queryVo.setMode(BasConstants.COMPANY_SEARCH_MODE_PUBLIC);//公海
        }
        if (BooleanUtils.isTrue(queryVo.getApprovePlasticTypeFlag())){
            queryVo.getSearchParams().put("EQS_temporaryPlasticType", "PS");
        }
        if(StringUtils.equals("F",search_eqs_status)){
            queryVo.getSearchParams().put("INS_status",new String[] {BasConstants.COMPANY_STATUS_D,BasConstants.COMPANY_STATUS_F});
            queryVo.setMode(BasConstants.COMPANY_SEARCH_MODE_MY);//私海
        }
        if(search_eqs_status==""){
            queryVo.setMode(BasConstants.COMPANY_SEARCH_MODE_ALL);// 全部
        }
        Map<String, Object> searchParams = queryVo.getSearchParams();
        Long currDeptId = ShiroUtil.getDeptId();
        if (!ShiroUtil.isPermitted(PermissionEnum.PERM_ZGBAS_CUSTOMER_DEPT_VIEW.getPermissionCode()) && currDeptId != 67957L){
            searchParams.put("NEQL_deptId", 67957L);
        }
        if (searchParams != null && Objects.equals(67957L, currDeptId)) {
            searchParams.remove("NEQL_deptId");
        }
        logger.info("listCompany : " + JsonUtil.obj2Json(queryVo));
        Page<BsCompanyVo> page = companyClient.findPageCompnayVo(queryVo);
        JsonEasyUI.renderJson(response, page);
    }

    @ResponseBody
    @RequestMapping(value = "downloadAccessReportFileZip")
    public void downloadAccessReportFileZip(BsCompanySearchVo queryVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(queryVo, request);
        StringBuffer url = request.getRequestURL();
        String uri = request.getRequestURI();
        String domain = url.substring(0, url.indexOf(uri));
        logger.info("url:{}", url);
        logger.info("uri:{}", uri);
        logger.info("domain:{}", domain);
        queryVo.setRequestUrl(domain);
        queryVo.setUserId(ShiroUtil.getCurrentUserId());
        queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        //是否有查看所有企业权限
        boolean lookAllCompany = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_VIEWALL.getPermissionCode());
        queryVo.setLookAllCompany(lookAllCompany);
        String search_eqs_status = queryVo.getSearchParams().get("INS_status") == null ? null : queryVo.getSearchParams().get("INS_status").toString();
        if(search_eqs_status != null && StringUtils.equals("N",search_eqs_status)){
            queryVo.setMode(BasConstants.COMPANY_SEARCH_MODE_PUBLIC);//公海
        }
        if (BooleanUtils.isTrue(queryVo.getApprovePlasticTypeFlag())){
            queryVo.getSearchParams().put("EQS_temporaryPlasticType", "PS");
        }
        if(StringUtils.equals("F",search_eqs_status)){
            queryVo.getSearchParams().put("INS_status",new String[] {BasConstants.COMPANY_STATUS_D,BasConstants.COMPANY_STATUS_F});
            queryVo.setMode(BasConstants.COMPANY_SEARCH_MODE_MY);//私海
        }
        if(search_eqs_status==""){
            queryVo.setMode(BasConstants.COMPANY_SEARCH_MODE_ALL);// 全部
        }
        Long userId = ShiroUtil.getCurrentUserId();
        String userName = ShiroUtil.getCurrentUserName();
        BsLog log = new BsLog();
        log.setIpAddre(IPUtil.getIpAddr(request));
        log.setRemortPort(request.getRemotePort());
        log.setOperation("3");
        log.setOperatorId(userId);
        log.setOperatorName(userName);
        log.setTargetName("企业管理");
        log.setRemark("导出企业访厂报告");
        bsLogClient.save(log);
        companyClient.downloadAccessReportFileZip(queryVo);
    }

    /**
     * 导出Eacel表格
     * @param searchVo
     * @param request
     * @param response
     * @throws ApplicationException
     */
    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(BsCompanySearchVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        
        boolean permitted = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EPX.getPermissionCode());
        if (permitted) {
            
            // 当前登录人
            Long currentUserId = ShiroUtil.getCurrentUserId();
            // 查看所有用户权限
            boolean lookAllCompany = ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_VIEWALL.getPermissionCode());
            // 合伙人权限
            boolean businessHhrPrem = ShiroUtil.isPermitted(PermissionEnum.PERM_BUSINESS_HHR.getPermissionCode());
            // 如果拥有区域总导出本区域公海数据权限，则公海数据只保留开户人是本区域的数据，其余数据剔除掉
            boolean leaderExportPermitted = ShiroUtil.isPermitted(PermissionEnum.PERM_BAS_COMPANY_LEADER_EXPORT.getPermissionCode());
            // 查看本部门企业权限
            boolean lookCurDeptCompanyPrem = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_VIEWDEPT.getPermissionCode());
            
            List<BsArea> bsAreaLs = areaClient.findAll();
            initSearch(searchVo, request);
            int batchSize = 5000;
            searchVo.setRows(batchSize);
            searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
            searchVo.setUserId(currentUserId);
            searchVo.setCurrentUserId(currentUserId);
            searchVo.setLookAllCompany(lookAllCompany);
            searchVo.setHhrPerm(businessHhrPrem);
            searchVo.setLookCurDeptCompanyPrem(lookCurDeptCompanyPrem);
            searchVo.setLeaderExportPermitted(leaderExportPermitted);
            
            String search_eqs_status = searchVo.getSearchParams().get("INS_status") == null ? null : searchVo.getSearchParams().get("INS_status").toString();
            if(search_eqs_status != null && StringUtils.equals("N",search_eqs_status)){
                searchVo.setMode(BasConstants.COMPANY_SEARCH_MODE_PUBLIC);//公海
            }
            if (BooleanUtils.isTrue(searchVo.getApprovePlasticTypeFlag())){
                searchVo.getSearchParams().put("EQS_temporaryPlasticType", "PS");
            }
            if(StringUtils.equals("F",search_eqs_status)){
                searchVo.getSearchParams().put("INS_status",new String[] {BasConstants.COMPANY_STATUS_D,BasConstants.COMPANY_STATUS_F});
                searchVo.setMode(BasConstants.COMPANY_SEARCH_MODE_MY);//私海
            }
            if(search_eqs_status==""){
                searchVo.setMode(BasConstants.COMPANY_SEARCH_MODE_ALL);// 全部
            }
            Page<BsCompanyVo> page = companyClient.findPageCompnayVoExcel(searchVo);
            dealWithData(page, bsAreaLs);
            String title = "企业管理查询";
            String[] titles = new String[]{"客户名称", "客户等级", "供应商等级","客户分类", "信用评分", "授信额度(元)",
                    "账期(天)", "已用授信额度(元)", "剩余授信额度(元)", "供应商分类", "供应商评分", "企业类型","塑料分类",
                    "区域", "联系人", "联系电话", "领用人", "开户人","共享人", "所属部门", "领用时间",
                    "是否线上化", "上传资料", "申请CFCA", "委托授权","访厂报告", "更新时间"};
            String[] attrs = new String[]{"companyName", "creditRating", "supplierRating", "companyGrade", "creditScore", "totalCreditAmount",
                    "creditDays", "usedCreditAmount", "remainCreditAmount", "supplierGrade", "supplierScore", "companyType","plasticType",
                    "companyArea", "contactName", "contactPhone", "muName", "ooaName", "shareUserNames","deptName", "matchFllowDate",
                    "lines", "companyApplyStatus", "cfcaApprovedStatus", "entrustApplyStatus","accessReportExist", "updatedDate"};
            int[] widths = new int[]{30, 15, 15, 15, 15, 15, 15, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 20, 15, 15, 15, 15, 20};
            Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
            // 生成一个表格
            Sheet sheet = workbook.createSheet(title);
            // 设置表格默认列宽度为 15 个字节
            sheet.setDefaultColumnWidth(15);
            // 产生表格标题行
            // 生成一个样式
            CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
            /** 创建表头 */
            int[] widthes = new int[titles.length];
            for (int i = 0; i < titles.length; i++) {
                widthes[i] = widths[i];
            }

            PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
            int start = 0;
            while (page != null && page.getContent().size() > 0) {
                PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR_WITH_TIME);
                if (page.hasNext()) {
                    searchVo.setPage(searchVo.getPage() + 1);
                    page = companyClient.findPageCompnayVoExcel(searchVo);
                    dealWithData(page, bsAreaLs);
                    start += batchSize;
                } else {
                    page = null;
                }
            }
            try {
                PoiExcelUtil.write(workbook, response, title);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            Long userId = ShiroUtil.getCurrentUserId();
            String userName = ShiroUtil.getCurrentUserName();
            BsLog log = new BsLog();
            log.setIpAddre(IPUtil.getIpAddr(request));
            log.setRemortPort(request.getRemotePort());
            log.setOperation("3");
            log.setOperatorId(userId);
            log.setOperatorName(userName);
            log.setTargetName("企业管理");
            log.setRemark("导出企业Excel");
            bsLogClient.save(log);
        } else {
            logger.error("权限不足，不能导出数据");
        }

    }

    public void dealWithData(Page<BsCompanyVo> page, List<BsArea> bsAreaLs){
        // 领用人ids
        List<Long> matchUserIds = new ArrayList<>();
        List<Long> ownerOfAccountIds = new ArrayList<>();
        for (int i = 0; i < page.getContent().size(); i++) {
            BsCompanyVo companyVo = page.getContent().get(i);
            matchUserIds.add(companyVo.getMatchUserId());
            ownerOfAccountIds.add(companyVo.getOwnerOfAccountId());
        }
        List<SysUserSdk> matchUsers = authOpenFacade.findByUserIds(matchUserIds);
        // 领用人Map
        Map<Long, SysUserSdk> matchUserMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(matchUsers)) {
            for (SysUserSdk SysUserSdk : matchUsers) {
                matchUserMap.put(SysUserSdk.getUserId(), SysUserSdk);
            }
        }
        List<SysUserSdk> ownerOfAccountUsers = authOpenFacade.findByUserIds(ownerOfAccountIds);
        // 开户人Map
        Map<Long, SysUserSdk> ownerOfAccountUserMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ownerOfAccountUsers)) {
            for (SysUserSdk SysUserSdk : ownerOfAccountUsers) {
                ownerOfAccountUserMap.put(SysUserSdk.getUserId(), SysUserSdk);
            }
        }
        for (int i = 0; i < page.getContent().size(); i++) {
            BsCompanyVo companyVo = page.getContent().get(i);
            /*  上传资料，申请CFCA，委托授权 */
            String companyApplyStatus = companyVo.getCompanyApplyStatus();
            if (StringUtils.isBlank(companyApplyStatus)) {
                companyVo.setCompanyApplyStatus("未开始");
            } else if (companyApplyStatus.equals("1")) {
                companyVo.setCompanyApplyStatus("审批中");
            } else if (companyApplyStatus.equals("2")) {
                companyVo.setCompanyApplyStatus("未确定");
            } else if (companyApplyStatus.equals("3")) {
                companyVo.setCompanyApplyStatus("审批驳回");
            } else if (companyApplyStatus.equals("4")) {
                companyVo.setCompanyApplyStatus("完成");
            } else {
                companyVo.setCompanyApplyStatus("未开始");
            }
            /*  上传资料，申请CFCA，委托授权 */
            String cfcaApprovedStatus = companyVo.getCfcaApprovedStatus();
            if (StringUtils.isBlank(cfcaApprovedStatus)) {
                companyVo.setCfcaApprovedStatus("未开始");
            } else if (cfcaApprovedStatus.equals("1")) {
                companyVo.setCfcaApprovedStatus("审批中");
            } else if (cfcaApprovedStatus.equals("2")) {
                companyVo.setCfcaApprovedStatus("未确定");
            } else if (cfcaApprovedStatus.equals("3")) {
                companyVo.setCfcaApprovedStatus("审批驳回");
            } else if (cfcaApprovedStatus.equals("4")) {
                companyVo.setCfcaApprovedStatus("完成");
            } else {
                companyVo.setCfcaApprovedStatus("未开始");
            }
            /*  上传资料，申请CFCA，委托授权 */
            String entrustApplyStatus = companyVo.getEntrustApplyStatus();
            if (StringUtils.isBlank(entrustApplyStatus)) {
                companyVo.setEntrustApplyStatus("未开始");
            } else if (entrustApplyStatus.equals("1")) {
                companyVo.setEntrustApplyStatus("审批中");
            } else if (entrustApplyStatus.equals("2")) {
                companyVo.setEntrustApplyStatus("未确定");
            } else if (entrustApplyStatus.equals("3")) {
                companyVo.setEntrustApplyStatus("审批驳回");
            } else if (entrustApplyStatus.equals("4")) {
                companyVo.setEntrustApplyStatus("完成");
            } else {
                companyVo.setEntrustApplyStatus("未开始");
            }
            /* 是否线上化 */
            Boolean enableFlg = companyVo.getOnLineFlg();
            if (Boolean.TRUE.equals(enableFlg)) {
                companyVo.setLines("是");
            } else {
                companyVo.setLines("否");
            }
            /* 客户等级 */
            String creditRating = companyVo.getCreditRating();
            String ratingValue = DictUtil.getValue(BasConstants.DICT_TYPE_CREDITRATING, creditRating);
            companyVo.setCreditRating(ratingValue);

            /* 供应商等级 */
            String supplierRating = companyVo.getSupplierRating();
            String supplierRatingValue = DictUtil.getValue(BasConstants.DICT_TYPE_CREDITRATING, supplierRating);
            companyVo.setSupplierRating(supplierRatingValue);

            /* 客户分类 */
            String companyGrade = companyVo.getCompanyGrade();
            String companyGradeValue = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_TYPE_COMPANYGRADE, companyGrade);
            companyVo.setCompanyGrade(companyGradeValue);

            /* 供应商分类分类 */
            String supplierGrade = companyVo.getSupplierGrade();
            String supplierGradeValue = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_TYPE_SUPPLIERGRADE, supplierGrade);
            companyVo.setSupplierGrade(supplierGradeValue);

            /* 企业类型  */
            String companyType = companyVo.getCompanyType();
            String typeValue = DictUtil.getValue(BasConstants.DICT_TYPE_COMPANYTYPE, companyType);
            companyVo.setCompanyType(typeValue);

            /* 塑料分类  */
            String plasticType = companyVo.getPlasticType();
            String plasticTypeValue = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_TYPE_PLASTIC_TYPE, plasticType);
            companyVo.setPlasticType(plasticTypeValue);

            /* 区域 */
            String companyArea = companyVo.getCompanyArea();
            if(bsAreaLs != null && bsAreaLs.size() > 0) {
                for (BsArea bsArea:bsAreaLs) {
                    if(StringUtils.equals(companyArea,bsArea.getCode())){
                        companyVo.setCompanyArea(bsArea.getName());
                        break;
                    }
                }
            }
            /* 领用人 */
            if (companyVo.getMatchUserId() != null && companyVo.getMatchUserId() != 0) {
                SysUserSdk SysUserSdk = matchUserMap.get(companyVo.getMatchUserId());
                companyVo.setMuName(Objects.nonNull(SysUserSdk) ? SysUserSdk.getNickName() : "");
            } else {
                companyVo.setMuName("");
            }
            /* 开户人 */
            if (companyVo.getOwnerOfAccountId() != null && companyVo.getOwnerOfAccountId() != 0) {
                SysUserSdk SysUserSdk = ownerOfAccountUserMap.get(companyVo.getOwnerOfAccountId());
                companyVo.setOoaName(Objects.nonNull(SysUserSdk) ? SysUserSdk.getNickName() : "");
            } else {
                companyVo.setOoaName("");
            }
            if (StringUtils.isBlank(companyVo.getAccessReportId())){

            }
            Boolean accessReportFlg = companyVo.getAccessReportFlg();
            if (Objects.nonNull(accessReportFlg) && accessReportFlg) {
                companyVo.setAccessReportExist("通过");
            } else {
                companyVo.setAccessReportExist("未通过");
            }
//            companyVo.setAccessReportExist(companyVo.getAccessReportFlg()? "通过" : "未通过");
        }
    }

    /*
     * 返回企业准入申请操作弹出框
     * */
    @RequestMapping(value = "allowedOperate/{id}", method = RequestMethod.GET)
    public String allowedOperate(@PathVariable("id") Long id, Model model) {
        BsCompany entity = getEntity(id);


        model.addAttribute("creditRatingJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));// 信用等级

        model.addAttribute("allowedJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_ALLOWED)));// 是否准入

        model.addAttribute("entity", entity);
        return "bs/companyAllowedOperate";
    }

    /*
     * 返回企业额度申请操作弹出框
     * */
    @RequestMapping(value = "quotaOperate/{id}", method = RequestMethod.GET)
    public String quotaOperate(@PathVariable("id") Long id, Model model) {
        BsCompany entity = getEntity(id);
        logger.info("quotaOperate : " + JsonUtil.obj2Json(entity));

        model.addAttribute("entity", entity);
        return "bs/companyQuotaOperate";
    }

    @RequestMapping(value = "findCompany/{id}")
    public void findCompany(@PathVariable("id") Long id, RptPartBsCompanyVo queryVo, HttpServletRequest request,
                            HttpServletResponse response) {
        String companyName = request.getParameter("q");
        if (StringUtils.isNotBlank(companyName)) {
            queryVo.setCompanyName(companyName);
        }
        queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        queryVo.setId(id);
        List<RptPartBsCompanyVo> listAll = reportCompanyClient.findCompany(queryVo);
        BsCompany entity = companyClient.getEntity(id);
        BsCompanyVo vo = new BsCompanyVo();
        RptPartBsCompanyVo newVo = new RptPartBsCompanyVo();
        vo.setEnterpriseId(entity.getEnterpriseId());
        vo.setId(entity.getId());
        List<BsCompanyAccount> accountList = bsCompanyAccountClient.findCompanyAccountFlg(vo);
        if (!accountList.isEmpty()) {
            BsCompanyAccount bsCompanyAccount = accountList.get(0);
            newVo.setBankAccount(bsCompanyAccount.getBankAccount());
            newVo.setBankName(bsCompanyAccount.getBankName());
        }
        if (StringUtils.isBlank(companyName) && entity != null) {
            newVo.setId(entity.getId());
            newVo.setCompanyPhone(entity.getCompanyPhone());
            newVo.setCompanyName(entity.getCompanyName());
            newVo.setContactPhone(entity.getCompanyPhone());
            newVo.setText(entity.getCompanyName());
            listAll.add(0, newVo);
        }
        RenderUtil.renderJson(listAll, response);
    }

    @RequestMapping(value = "listCompany/{id}")
    public void listCompany(@PathVariable("id") Long id, RptPartBsCompanyVo queryVo, HttpServletRequest request, HttpServletResponse response) {
        String q = request.getParameter("q");
        if (StringUtils.isNotBlank(q)) {
            queryVo.setCompanyName(q);
        }
        queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<RptPartBsCompanyVo> listAll = reportCompanyClient.findCompanyList(queryVo);
        if (id != 0 && StringUtils.isBlank(q)) {
            RptPartBsCompanyVo newVo = new RptPartBsCompanyVo();
            BsCompanyVo vo = new BsCompanyVo();
            BsCompany company = companyClient.getEntity(id);
            vo.setEnterpriseId(company.getEnterpriseId());
            vo.setId(company.getId());
            List<BsCompanyAccount> accountList = bsCompanyAccountClient.findCompanyAccountFlg(vo);
            if (!accountList.isEmpty()) {
                BsCompanyAccount bsCompanyAccount = accountList.get(0);
                newVo.setBankAccount(bsCompanyAccount.getBankAccount());
                newVo.setBankName(bsCompanyAccount.getBankName());
            }
            newVo.setId(company.getId());
            newVo.setCompanyPhone(company.getCompanyPhone());
            newVo.setCompanyName(company.getCompanyName());
            newVo.setContactPhone(company.getContactPhone());
            newVo.setContactName(company.getCompanyName());
            newVo.setText(company.getCompanyName());
            newVo.setAddress(company.getAddress());
            newVo.setMyFlag(true);
            BigDecimal totalCreditAmount = company.getTotalCreditAmount();
            BigDecimal usedCreditAmount = company.getUsedCreditAmount();
            totalCreditAmount = totalCreditAmount == null ? BigDecimal.ZERO : totalCreditAmount;
            usedCreditAmount = usedCreditAmount == null ? BigDecimal.ZERO : usedCreditAmount;
            newVo.setTotalCreditAmount(totalCreditAmount);
            newVo.setUsedCreditAmount(usedCreditAmount);
            newVo.setRemainCreditAmount(totalCreditAmount.subtract(usedCreditAmount));
            newVo.setCreditAmountDays(company.getCreditDays());
            newVo.setSupplierPurchaseAmount(company.getSupplierPurchaseAmount());
            newVo.setUsedSupplierPurchaseAmount(company.getUsedSupplierPurchaseAmount());
            listAll.add(0, newVo);
        }
        RenderUtil.renderJson(listAll, response);
    }

    @RequestMapping(value = "listMyCompany/{id}/{fid}")
    public void listMyCompany(@PathVariable("id") Long id, @PathVariable("fid") Integer fid, RptPartBsCompanyVo queryVo, HttpServletRequest request,
                              HttpServletResponse response) {
        //queryVo.setSearchType(null);//暂时去除白条合同赊销客户限制
        String q = request.getParameter("q");
        String type = request.getParameter("type");
        boolean virtualFlg = StringUtils.equals("virtual", type);
        if (StringUtils.isNotBlank(q)) {
            queryVo.setCompanyName(q);
        }
        Long currentUserId = ShiroUtil.getCurrentUserId();
        queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        if (Boolean.FALSE.equals(virtualFlg)) {
            queryVo.setMatchUserId(currentUserId);
        }
        // 所有采购员都能使用全部供应商 需求（TAPD:1001832）
        if (fid == 1) {
            queryVo.setMatchUserId(null);
        }
        List<RptPartBsCompanyVo> listAll = reportCompanyClient.findCompanyList(queryVo);

        if (id != 0 && StringUtils.isBlank(q)) {
            RptPartBsCompanyVo newVo = new RptPartBsCompanyVo();
            BsCompanyVo vo = new BsCompanyVo();
            BsCompany company = companyClient.getEntity(id);
            vo.setEnterpriseId(company.getEnterpriseId());
            vo.setId(company.getId());
            List<BsCompanyAccount> accountList = bsCompanyAccountClient.findCompanyAccountFlg(vo);
            if (!accountList.isEmpty()) {
                BsCompanyAccount bsCompanyAccount = accountList.get(0);
                newVo.setBankAccount(bsCompanyAccount.getBankAccount());
                newVo.setBankName(bsCompanyAccount.getBankName());
            }
            newVo.setId(company.getId());
            newVo.setCompanyPhone(company.getCompanyPhone());
            newVo.setCompanyName(company.getCompanyName());
            newVo.setContactPhone(company.getContactPhone());
            newVo.setContactName(company.getCompanyName());
            newVo.setText(company.getCompanyName());
            newVo.setAddress(company.getAddress());
            newVo.setPiccCreditAmount(company.getPiccCreditAmount() == null ? BigDecimal.ZERO : company.getPiccCreditAmount());
            newVo.setAccessReportFlg(company.getAccessReportFlg()?true:false);
            newVo.setActualGuaranteeFlg(company.getActualGuaranteeFlg()?true:false);
            newVo.setMyFlag(true);
            BigDecimal totalCreditAmount = company.getTotalCreditAmount();
            BigDecimal usedCreditAmount = company.getUsedCreditAmount();
            totalCreditAmount = totalCreditAmount == null ? BigDecimal.ZERO : totalCreditAmount;
            usedCreditAmount = usedCreditAmount == null ? BigDecimal.ZERO : usedCreditAmount;
            newVo.setTotalCreditAmount(totalCreditAmount);
            newVo.setUsedCreditAmount(usedCreditAmount);
            newVo.setRemainCreditAmount(totalCreditAmount.subtract(usedCreditAmount));
            newVo.setCreditAmountDays(company.getCreditDays());
            newVo.setSupplierPurchaseAmount(company.getSupplierPurchaseAmount());
            newVo.setUsedSupplierPurchaseAmount(company.getUsedSupplierPurchaseAmount());
            listAll.add(0, newVo);
        }
        if (!listAll.isEmpty()) {
            BsCompanyShare share = new BsCompanyShare();
            share.setSharedUserId(currentUserId);
            List<BsCompanyShare> companyShareList = bsCompanyShareClient.findBySharedUserId(share);
            Map<Long, BsCompanyShare> companyShareMap = new HashMap<>();
            if (CollUtil.isNotEmpty(companyShareList)) {
                companyShareMap = companyShareList.stream().collect(Collectors.toMap(BsCompanyShare::getCompanyId, Function.identity(), (existing, replacement) -> existing));
                
            }
            Iterator<RptPartBsCompanyVo> it = listAll.iterator();
            while (it.hasNext()) {
                RptPartBsCompanyVo entity = it.next();
                entity.setPiccCreditAmount(entity.getPiccCreditAmount() == null ? BigDecimal.ZERO : entity.getPiccCreditAmount());
                entity.setAccessReportFlg(Boolean.TRUE.equals(entity.getAccessReportFlg())?true:false);
                entity.setActualGuaranteeFlg(Boolean.TRUE.equals(entity.getActualGuaranteeFlg())?true:false);
                if (fid == 1 && (StringUtils.isEmpty(entity.getSupplierRating()) || !"W".equals(entity.getSupplierRating()))) {
                    //采购合同，供应商
                    it.remove();
                    continue;
                }
                if (fid == 2 && (StringUtils.isEmpty(entity.getCreditRating()) || !"W".equals(entity.getCreditRating()))) {
                    //销售合同，采购商
                    it.remove();
                    continue;
                }
                // q: 企业名称
                if (StringUtils.isNotBlank(q) && !id.equals(entity.getId())) {
                    if (entity.getMatchUserId() == null) {
                        entity.setText(entity.getCompanyName() + "[公海]");
                        entity.setMyFlag(false);
                    } else if (currentUserId.equals(entity.getMatchUserId())) {
                        entity.setMyFlag(true);
                    } else if (!virtualFlg){
                        BsCompanyShare shareCompany = companyShareMap.get(entity.getId());
                        if (Objects.isNull(shareCompany)) {
                            String userName = UserCache.getUserName(entity.getMatchUserId());
                            entity.setMyFlag(false);
                            entity.setText(entity.getCompanyName() + " [" + userName + "]");
                            it.remove();
                            continue;
                        } else {
                            entity.setMyFlag(true);
                        }

                    }
                    // 贸易商-所有业务员都可用
                    if (StringUtils.equals(entity.getCompanyType(), BasConstants.DICT_TYPE_COMPANYTYPE_T)) {
                        // entity.setText(entity.getCompanyName());
                        entity.setMyFlag(true);
                    }
                }
                entity.setMyFlag(true);
            }
        }
        RenderUtil.renderJson(listAll, response);
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        BsCompany entity = getEntity(id);
        Long userId = ShiroUtil.getCurrentUserId();
        if (id > 0L) {
            String status = entity.getStatus();
            boolean isFollow = false;
            // 判断是否是私海 同时是私海用户
            if (BasConstants.COMPANY_STATUS_F.equals(status) && userId == entity.getMatchUserId()) {
                isFollow = true;
            }
            model.addAttribute("isFollow", isFollow);
        }
        model.addAttribute("enableFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        model.addAttribute("applyTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
        model.addAttribute("contractStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("followTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_FOLLOWTYPE)));
        model.addAttribute("creditFlowTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.CREDIT_FLOW_TYPE)));
        List<EasyTreeNode> allIndustryTree = bsCompanyIndustryClient.getAllIndustryTree();

        model.addAttribute("companyStatus", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_COMPANY_STATUS)));

        model.addAttribute("companyIndustryJson", JsonUtil.obj2Json(allIndustryTree));
        model.addAttribute("creditRatingJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));// 信用等级
        String companyType = entity.getCompanyType();
        // 贸易商和客户选项不同
        if(StringUtils.isNotBlank(companyType) && StringUtils.equals(BasConstants.DICT_TYPE_COMPANYTYPE_T,companyType)){
            model.addAttribute("companyCategoryJson",
                    JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_SUPPLIERCATEGORY)));// 供应商性质
        } else {
            model.addAttribute("companyCategoryJson",
                    JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYCATEGORY)));// 企业类别
        }
        model.addAttribute("logisticsSealTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_LOGISTICSSEALTYPE)));//物流章类型
        model.addAttribute("companyTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_CPYTYPE)));// 企业类别
        model.addAttribute("companyGradeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));// 企业类别
        model.addAttribute("onLineFlg",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_ONLiNEFLG)));// 线上化查询
        model.addAttribute("landTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_LANDTYPE)));// 土地类型

        model.addAttribute("plantTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_PLANTTYPE)));// 厂房类型

        model.addAttribute("equipmentTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_EQUIPMENTTYPE)));// 机械设备类型

        model.addAttribute("allowedJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_ALLOWED)));// 是否准入
        model.addAttribute("actualGuaranteeTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_ACTUAL_GUARANTEE)));// 实控人担保
        model.addAttribute("accessReportFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ACCESSREPORTFLG)));
        model.addAttribute("plasticTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_PLASTIC_TYPE)));
        // 获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo();
        deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        // 针对访厂报告文件下载,过滤文件id存在的数据
        FileSearchVo fileSearchVo = new FileSearchVo();
        fileSearchVo.setFileIds(entity.getAccessReportId());
        List<SysFile> sysFiles = fileRemote.loadFiles(fileSearchVo);
        if(CollectionUtils.isNotEmpty(sysFiles)){
            String collect = sysFiles.stream().map(it -> it.getId().toString()).collect(Collectors.joining(","));
            entity.setAccessReportId(collect);
        }
        model.addAttribute("entity", entity);
        SignSealVo companyCfcaSeal = companyClient.findCompanyCfcaSeal(entity.getId());
        model.addAttribute("companyCfcaSeal",companyCfcaSeal);
        model.addAttribute("Risk", ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_RISK.getPermissionCode()));
        //是否有风控编辑客户权限
        boolean companyEditFlag = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EDIT.getPermissionCode());
        model.addAttribute("companyEditFlag", companyEditFlag || Objects.equals(ShiroUtil.getCurrentUserId(), entity.getMatchUserId()));
        model.addAttribute("fileServerUrl",fileServerUrl+"/view/download/");
        return "bs/company-detail";
    }
    @RequestMapping(value = "showImportPiccExcel")
    public String showImportPiccExcel(Model model) {
        return "bs/company_import_picc_excel";
    }

    @RequestMapping(value = "showImportDaDiExcel")
    public String showImportDaDiExcel(Model model) {
        return "bs/company_import_dadi_excel";
    }
    
    @RequestMapping(value = "showImportZhongYinExcel")
    public String showImportZhongYinExcel(Model model) {
        return "bs/company_import_zhongyin_excel";
    }
    @RequestMapping(value = "updateDaDiFileId", method = RequestMethod.POST)
    public void updateDaDiFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        logger.info("updateDaDiFileId : " + JsonUtil.obj2Json(vo));
        try {
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 人保授信数据导入
     * @param request
     * @param response
     */
    @RequestMapping(value = "importPiccExcel")
    public void importPiccExcel(HttpServletRequest request, HttpServletResponse response) {
        ImportExcelVo importExcelVo = new ImportExcelVo();
        try {
            importExcelVo.setUserId(ShiroUtil.getCurrentUserId());
            importExcelVo.setUserName(ShiroUtil.getCurrentUserName());
            String fileIds = request.getParameter("fileId");
            if(StringUtils.isNotBlank(fileIds)){
//                companyClient.updatePiccFlgToFalse();
                String[] split = fileIds.split(",");
                List<String> results = new ArrayList<>();
                for (String fileId : split) {
//                    result += companyClient.importPiccExcel(fileId);
                    importExcelVo.setFileId(fileId);
                    results = companyClient.importPiccExcel(importExcelVo);
                }
                //人保授信额度为0的修改为黑名单
//                companyClient.updateCreditRatingToBlack();
//                RenderUtil.renderSuccess(result, response);
                RenderUtil.renderJson(results,response);
//                RenderUtil.renderSuccess("success", response);
            } else {
                RenderUtil.renderFailure("文件id为空", response);
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
        return;
    }
    /**
     * 大地额度导入
     * @param request
     * @param response
     */
    @RequestMapping(value = "importDaDiExcel")
    public void importDaDiExcel(HttpServletRequest request, HttpServletResponse response) {
        ImportExcelVo importExcelVo = new ImportExcelVo();
        try {
            importExcelVo.setUserId(ShiroUtil.getCurrentUserId());
            importExcelVo.setUserName(ShiroUtil.getCurrentUserName());
            String fileIds = request.getParameter("fileId");
            if(StringUtils.isNotBlank(fileIds)){
                String[] split = fileIds.split(",");
                List<String> results = new ArrayList<>();
                for (String fileId : split) {
                    importExcelVo.setFileId(fileId);
                    results = companyClient.importDaDiExcel(importExcelVo);
                }
                RenderUtil.renderJson(results,response);
            } else {
                RenderUtil.renderFailure("文件id为空", response);
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
        return;
    }

    @RequestMapping("/downloadZhongYinExcel")
    public void downloadExcel(HttpServletResponse response) {
        OutputStream out = null;
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        String fileName = "中银额度导入模版";
        String filePath ="/excel/importZhongYin.xlsx";
        try {
            // 读取模板
            org.springframework.core.io.Resource res = new ClassPathResource(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(res.getInputStream());

            // 转换为字节流
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            in = new ByteArrayInputStream(barray);

            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            out = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) > 0) {
                out.write(b, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            logger.error("下载模板失败",e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("关闭资源异常",e);
                }
                in = null;
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("关闭资源异常",e);
                }
                out = null;
            }
            if (null != bos) {
                try {
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    logger.error("关闭资源异常",e);
                }
                out = null;
            }
        }
    }
    
    /**
     * 中银额度导入
     * @param request
     * @param response
     */
    @RequestMapping(value = "importZhongYinExcel")
    public void importZhongYinExcel(HttpServletRequest request, HttpServletResponse response) {
        ImportExcelVo importExcelVo = new ImportExcelVo();
        try {
            importExcelVo.setUserId(ShiroUtil.getCurrentUserId());
            importExcelVo.setUserName(ShiroUtil.getCurrentUserName());
            String fileIds = request.getParameter("fileId");
            if(StringUtils.isNotBlank(fileIds)){
                String[] split = fileIds.split(",");
                List<String> results = new ArrayList<>();
                for (String fileId : split) {
                    importExcelVo.setFileId(fileId);
                    results = companyClient.importZhongYinExcel(importExcelVo);
                }
                RenderUtil.renderJson(results,response);
            } else {
                RenderUtil.renderFailure("文件id为空", response);
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
    }

    @RequestMapping(value = "showImportCreditReport")
    public String showImportCreditReport(Model model) {
        return "bs/company_import_credit_report";
    }

    /**
     * 人保授信数据导入
     * @param request
     * @param response
     */
    @RequestMapping(value = "importCreditReportZip")
    public void importCreditReportZip(HttpServletRequest request, HttpServletResponse response) {
        List<String> results = new ArrayList<>();
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            String fileIds = request.getParameter("fileId");
            if(StringUtils.isNotBlank(fileIds)){
                FileSearchVo fileSearchVo = new FileSearchVo();
                fileSearchVo.setFileIds(fileIds);
                List<SysFile> sysFiles = fileRemote.loadFiles(fileSearchVo);
                Map<Long, SysFile> fileMap = sysFiles.stream().collect(Collectors.toMap(SysFile::getId, s -> s, (a, b) -> b));
                List<DownLoadFileVo> downloadList = new ArrayList<>();

                for (SysFile sysFile : sysFiles) {
                    DownLoadFileVo vo = new DownLoadFileVo();
                    vo.setFileName(sysFile.getOriginalFilename());
                    vo.setFileId(sysFile.getId());
                    downloadList.add(vo);
                }
                int successCount = 0;
                int errorCount = 0;
                StringBuilder errorFileName = new StringBuilder();
                List<DownloadedFile> downloadedFiles = downloadFiles(downloadList, fileMap);
                for (DownloadedFile downloadedFile : downloadedFiles) {
                    InputStream inputStream = downloadedFile.getInputStream();
                    InputStream bufferedInput = new BufferedInputStream(inputStream);


                    ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(bufferedInput);
                    ArchiveEntry entry;
                    
                    while ((entry = ais.getNextEntry()) != null) {
                        if (!entry.isDirectory()) {
                            String fileName = entry.getName();
                            if(fileName.contains("__MACOSX")){
                                continue;
                            }
                            // 获取文件内容并转换为 base64
                            String fileContentBase64 = getFileContentAsBase64(ais);
                            UploadBase64Request uploadBase64Request = new UploadBase64Request();
                            uploadBase64Request.setFileName(fileName);
                            uploadBase64Request.setBase64Data(fileContentBase64);
                            
                            UploadFileVo uploadFileVo = uploadBase64(uploadBase64Request, request);

                            BsCompany bsCompany = companyClient.findByCompanyName(removeFileExtension(fileName));
                            // 更新征信报告
                            if(Objects.nonNull(bsCompany)) {
                                bsCompany.setCorporateCreditId(uploadFileVo.getFileId());
                                companyClient.save(bsCompany);
                                successCount++;
                            } else {

                                if(errorCount > 0) {
                                    errorFileName.append("，").append(fileName);
                                } else {
                                    errorFileName.append(fileName);
                                }
                                errorCount++;
                            }
                            
                        }
                    }
                }

                results.add("导入成功【"+successCount+"】家企业征信报告;");
                results.add("导入失败【"+errorCount+"】家企业征信报告;");
                if(errorCount > 0) {
                    results.add("导入失败文件列表:【"+errorFileName+"】");
                }
                results.add("耗时"+stopwatch.elapsed(TimeUnit.MILLISECONDS)*1.0/1000+"秒;");
                RenderUtil.renderJson(results,response);
            } else {
                RenderUtil.renderFailure("文件id为空", response);
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
        return;
    }
    public static String removeFileExtension(String fileName) {
        // 使用正则表达式去除文件后缀
        return fileName.replaceFirst("[.][^.]+$", "");
    }
    public static String getFileContentAsBase64(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(is, baos);
        byte[] fileContentBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(fileContentBytes);
    }

    private List<DownloadedFile> downloadFiles(List<DownLoadFileVo> downloadList, Map<Long, SysFile> fileMap) {
        List<DownloadedFile> downloadedFiles = new ArrayList<>();

        for (DownLoadFileVo vo : downloadList) {
            try {
                SysFile sysFile = fileMap.get(vo.getFileId());
                if (Objects.nonNull(sysFile)) {
                    vo.setFileType(getFileType(sysFile));
                    URL fileUrl = new URL(fileServerUrl + "/view/download/" + vo.getFileId());

                    // 打开连接并获取 InputStream
                    HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
                    connection.setRequestMethod("GET");
                    InputStream is = connection.getInputStream();

                    // 将文件名和 InputStream 存储在 DownloadedFile 对象中
                    DownloadedFile downloadedFile = new DownloadedFile(vo.getFileName(), is);
                    downloadedFiles.add(downloadedFile);
                }
            } catch (Exception e) {
                logger.error("downloadFiles error", e);
            }
        }

        return downloadedFiles;
    }

    // 定义一个用于保存文件名和 InputStream 的类
    public class DownloadedFile {
        private String fileName;
        private InputStream inputStream;

        public DownloadedFile(String fileName, InputStream inputStream) {
            this.fileName = fileName;
            this.inputStream = inputStream;
        }

        public String getFileName() {
            return fileName;
        }

        public InputStream getInputStream() {
            return inputStream;
        }
    }

    
    /**
     * 根据文件名获取文件类型
     *
     * @param sysFile
     * @return
     */
    private String getFileType(SysFile sysFile) {
        String fileType = ".zip";
        String contentType = sysFile.getContentType();
        if (StringUtils.isNotBlank(contentType)) {
            List<String> result = Splitter.on(BasConstants.OBL).omitEmptyStrings().splitToList(contentType);
            fileType = result.size() > 1 ? "." + result.get(1) : fileType;
        }
        return fileType;
    }
    /**
     * 上传base64文件
     * @param uploadBase64Request
     * @param request
     * @return
     */
    public UploadFileVo uploadBase64(UploadBase64Request uploadBase64Request, HttpServletRequest request) {
        try {
            String[] allTypes = {".jpg", ".gif", ".png", ".bmp", ".pdf", ".docx", ".doc"};
            FileUploadBase64Request fileRequest = new FileUploadBase64Request();
            fileRequest.setAllowTypes(allTypes);
            fileRequest.setFilePath("bas/");
            fileRequest.setServerName(request.getServerName());
            fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
            //将上传的文件转为base64
            FileUploadBase64Request.Base64DataVo base64DataVo = new FileUploadBase64Request.Base64DataVo();
            base64DataVo.setFileName(uploadBase64Request.getFileName());
            base64DataVo.setBase64Data(uploadBase64Request.getBase64Data());
            List<FileUploadBase64Request.Base64DataVo> dataList = new ArrayList<>();
            dataList.add(base64DataVo);
            fileRequest.setDataList(dataList);
            FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
            if (fileRespVo == null) {
                throw new BaseException(Status.ERROR, "文件上传失败");
            }
            if (fileRespVo.getResult()) {
                if (!StrUtil.isEmpty(fileRespVo.getFileId())) {
                    String[] fileId = fileRespVo.getFileId().split(",");
                    try {
                        saveFileRecord(fileId[0], dataList);
                    } catch (Exception e) {
                        logger.error("上传附件,保存fileRecord失败", e);
                    }
                    return new UploadFileVo(fileId[0]);
                }
            }
            throw new BaseException(Status.ERROR, "文件上传失败");
        } catch (Exception e) {
            logger.error("uploadFile error!", e);
            throw new BaseException(Status.ERROR, "文件上传失败");
        }
    }
    /**
     * 附件上传，保存fileRecord
     *
     * @param fileId
     * @param dataList
     */
    private void saveFileRecord(String fileId,List<FileUploadBase64Request.Base64DataVo> dataList) {
        FileRecord fileRecord = new FileRecord();
        if (StringUtils.isEmpty(fileId)) {
            return;
        }
        // 去除","
        if (fileId.indexOf(",") > 0) {
            fileId = fileId.split(",")[0];
        }
        fileRecord.setFileId(fileId);
        fileRecord.setFileName(dataList.get(0).getFileName());
        fileRecordClient.save(fileRecord);
    }


    @RequestMapping(value = "verify/{companyName}", method = RequestMethod.GET)
    public void verify(@Valid @ModelAttribute("companyName") String companyName, HttpServletRequest request,
                       HttpServletResponse response) {

        if (StringUtils.isNotBlank(companyName)) {
            BsCompanyShare company = new BsCompanyShare();
            company.setCompanyName(companyName);
            company.setEnterpriseId(ShiroUtil.getEnterpriseId());
            List<BsCompany> list = companyClient.queryCompanyName(company);
            if (list != null && list.size() > 0) {
                RenderUtil.renderFailure("fail", response);
            } else {
                RenderUtil.renderSuccess("success", response);
            }
        }
    }

    @ModelAttribute("preload")
    public BsCompany getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                BsCompany entity = new BsCompany();
                entity.setId(0L);
                entity.setEnableFlg(true);
                return entity;
            }
        }
        return null;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        logger.info("updateFileId : " + JsonUtil.obj2Json(vo));
        try {
//			companyClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 设置准入，并发起准入审批
     * @param company
     * @param request
     * @param response
     */
    @RequestMapping(value = "saveAllowed")
    public void saveAllowed(BsCompany company, HttpServletRequest request, HttpServletResponse response) {
        try {
            BsCompany entity = companyClient.getEntity(company.getId());
            entity.setCreditRating(company.getCreditRating());
            entity.setAllowed(company.getAllowed());

            companyClient.save(entity);
            RenderUtil.renderSuccess("申请成功！", response);
            //发起准入审批
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
        return;
    }

    /**
     * 设置额度申请，并发起额度申请审批
     * @param company
     * @param request
     * @param response
     */
    @RequestMapping(value = "saveQuota")
    public void saveQuota(BsCompany company, HttpServletRequest request, HttpServletResponse response) {
        try {
            BsCompany entity = companyClient.getEntity(company.getId());

            entity.setTotalCreditAmount(company.getTotalCreditAmount());
            entity.setTotalSpotAmount(company.getTotalSpotAmount());
            entity.setTotalFuturesAmount(company.getTotalFuturesAmount());

            companyClient.save(entity);
            RenderUtil.renderSuccess("申请成功！", response);
            //发起准入审批
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
        return;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(@Valid @ModelAttribute("preload") BsCompany company, HttpServletRequest request,
                     HttpServletResponse response) {
        try {
            //是否有风控编辑客户权限
            boolean permitted = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EDIT.getPermissionCode());
            if (!permitted) {
                company.setEnableFlg(true);
                company.setCreditRating(BasConstants.DICT_TYPE_CREDITRATING_G);
                // allowed字段不再使用 为兼容默认设置为'Y'
                company.setAllowed(BasConstants.DICT_TYPE_ALLOWED_Y);
            }
            BsCompany old = companyClient.getEntity(company.getId());
            company.setEnterpriseId(ShiroUtil.getEnterpriseId());
            company.setCreateUserId(ShiroUtil.getCurrentUserId());
            LogUtil.saveOrUpdate(request, old, company, company.getId());// 记录日志
            // companyClient.save(company);
            BsCompany bsCompany = companyClient.save(company);
            RenderUtil.renderJson(bsCompany, response);
            // RenderUtil.renderText("success", response);
        } catch (Exception e) {
            logger.error("save", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }

    }

    /**
     * 回收功能
     *
     * @param id
     * @param request
     * @param response
     */
    @RequestMapping(value = "recycle/{id}")
    public String recycle(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            BsCompany entity = companyClient.getEntity(id);
            entity.setEnableFlg(true);
            companyClient.save(entity);
            RenderUtil.renderSuccess("回收成功！", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
        return null;
    }

    /**
     * 批量回收功能
     * @param request
     * @param response
     */
    @RequestMapping(value = "batchRecycle/{update}")
    public String batchRecycle(@PathVariable("update") String ids, HttpServletRequest request,
                               HttpServletResponse response) {
        try {
            if (ids != null) {
                String[] split = ids.split("-");
                Long[] condition = FormConfigUtil.formateArray(split);
                // List<Long> condition = Arrays.asList(companyIdStr);
                companyClient.updateByIds(condition);
                RenderUtil.renderSuccess("回收成功！", response);
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
        return null;
    }

    /**
     * 审核客户PS (热塑性塑料)塑料分类
     * @param request
     * @param response
     */
    @RequestMapping(value = "approvePlasticType", method = RequestMethod.POST)
    public void approvePlasticType(HttpServletRequest request, HttpServletResponse response) {
        try {
            String id = request.getParameter("id");
            Long userId = ShiroUtil.getCurrentUserId();
            if (!ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_RISK.getPermissionCode())){
                RenderUtil.renderText("missPermission", response);
            } else if (StringUtils.isNotBlank(id)) {
                Long companyId = Long.valueOf(id);
                BsCompany company = companyClient.getEntity(Long.valueOf(companyId));
                company.setPlasticType("PS");
                company.setTemporaryPlasticType(null);
                companyClient.save(company);

                SysUserSdk SysUserSdk = authOpenFacade.findUserById(userId);
                BsCompanyOphisVo opHis = new BsCompanyOphisVo();
                opHis.setCompanyId(companyId);
                opHis.setCreateUserId(userId);
                opHis.setCreateUserName(SysUserSdk.getNickName());
                opHis.setStatus(company.getStatus());
                opHis.setEnterpriseId(company.getEnterpriseId());
                opHis.setOptionType(BasConstants.COMPANY_STATUS_P);
                opHis.setRemark(company.getCompanyName());
                opHis.setOperation("2");
                opHis.setTargetName("企业管理");
                bsCompanyOphisClient.addCompanyHis(opHis);
                RenderUtil.renderText("success", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderText("fail", response);
        }
    }

    /**
     * 领用按钮 新增领用限制（每天可领用5个客户）
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "updateStatusByFllow", method = RequestMethod.POST)
    public void updateStatusByFllow(HttpServletRequest request, HttpServletResponse response) {
        String value = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.COMPANY_COLLECT, BasConstants.EVERYDAY_LIMIT);
        int clientValues = StringUtils.isEmpty(value) ? 10 : Integer.parseInt(value);
        try {
            // 设置每个业务员每天最多能领用5个客户
            Long userId = ShiroUtil.getCurrentUserId();
            List<BsCompany> list = companyClient.getCompanyForDate(userId);
            if (list.size() >= clientValues) {
                RenderUtil.renderText("upper", response);
                return;
            }
            String id = request.getParameter("id");
            if (StringUtils.isNotBlank(id)) {
                // 查询当前客户状态
                Long companyId = Long.parseLong(id);
                BsCompany bc = companyClient.getEntity(companyId);

                BsCompanyUserBak bak = bsCompanyUserBakClient.getBsCompanyUserBak(companyId, ShiroUtil.getCurrentUserId());
                BsCompanyUserBak bsCompanyUserBak = null;
                if (bak != null) {
                    bsCompanyUserBak = bsCompanyUserBakClient.getBsCompanyUserBak(companyId, bak.getMatchUserId());
                } else {
                    if (BasConstants.COMPANY_STATUS_N.equals(bc.getStatus())) {
                        updateStatus(companyId, BasConstants.COMPANY_STATUS_F);
                        RenderUtil.renderText("success", response);
                        return;
                    } else {
                        RenderUtil.renderText("alreadyFllow", response);
                        return;
                    }
                }

                if (bsCompanyUserBak != null) {
                    String pattern = "yyyy-MM-dd hh:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    Date disuseTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(sdf.format(bsCompanyUserBak.getDisuseFollowDate()));
                    Date nowTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(sdf.format(new Date()));
                    long betweenTime = disuseTime.getTime() - nowTime.getTime() > 0 ? disuseTime.getTime() - nowTime.getTime() :
                            nowTime.getTime() - disuseTime.getTime();
                    long intervalDate = betweenTime / (1000 * 3600 * 24);
                    String date = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.COMPANY_INTERVAL_DAYS, BasConstants.INTERVAL_DATES);
                    long values = (StringUtils.isEmpty(date) ? 30 : Integer.parseInt(date));
                    if (intervalDate <= values && bsCompanyUserBak.getMatchUserId().equals(ShiroUtil.getCurrentUserId())) {
                        RenderUtil.renderText("disUse", response);
                    } else {
                        if (BasConstants.COMPANY_STATUS_N.equals(bc.getStatus())) {
                            updateStatus(companyId, BasConstants.COMPANY_STATUS_F);
                            RenderUtil.renderText("success", response);
                        } else {
                            RenderUtil.renderText("alreadyFllow", response);
                        }
                    }
                } else {
                    if (BasConstants.COMPANY_STATUS_N.equals(bc.getStatus())) {
                        updateStatus(companyId, BasConstants.COMPANY_STATUS_F);
                        RenderUtil.renderText("success", response);
                    } else {
                        RenderUtil.renderText("alreadyFllow", response);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("updateStatusByFllow", e);
            RenderUtil.renderText("fail", response);
        }
    }

    /**
     * 退回按钮
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "updateStatusByRelease", method = RequestMethod.POST)
    public void updateStatusByRelease(HttpServletRequest request, HttpServletResponse response) {
        try {
            String id = request.getParameter("id");
            Long companyId = Long.valueOf(id);
            Long userId = ShiroUtil.getCurrentUserId();
            if (StringUtils.isNotBlank(id)) {
                BsCompanyShare share = new BsCompanyShare();
                share.setCompanyId(companyId);
                share.setCreateUserId(userId);
                List<BsCompanyShare> shareCompany = bsCompanyShareClient.findByCompanyIdAndCreateUserId(share);
                for (BsCompanyShare bsCompanyShare : shareCompany) {
                    if (bsCompanyShare != null) {
                        bsCompanyShareClient.delete(bsCompanyShare.getId());
                    }
                }
                BsCompany company = companyClient.getEntity(companyId);
                updateStatus(companyId, BasConstants.COMPANY_STATUS_N);
                //重新归入公海的企业
                BsCompanyUserBak bsCompanyUserBak = new BsCompanyUserBak();
                //备份领用人id
                bsCompanyUserBak.setMatchUserId(company.getMatchUserId());
                //备份领用时间
                bsCompanyUserBak.setMatchFollowDate(company.getMatchFllowDate());
                //设置丢弃时间
                bsCompanyUserBak.setDisuseFollowDate(new Date());
                //设置领用企业Id
                bsCompanyUserBak.setCompanyId(company.getId());
                bsCompanyUserBakClient.save(bsCompanyUserBak);
                //重新归入公海后的企业，前所属业务员30天内不得再次领用.
                RenderUtil.renderText("success", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderText("fail", response);
        }
    }

    /**
     * 管理员指派业务员
     */
    @RequestMapping(value = "updateStatusByAssigned", method = RequestMethod.POST)
    public void updateStatusByAssigned(HttpServletRequest request, HttpServletResponse response) {
        try {
            String companyId = request.getParameter("companyId");
            String matchUserId = request.getParameter("matchUserId");
            if (StringUtils.isNotBlank(companyId) && StringUtils.isNotBlank(matchUserId)) {
                SysUserSdk SysUserSdk = authOpenFacade.findUserById(Long.parseLong(matchUserId));
                BsCompany company = companyClient.getEntity(Long.parseLong(companyId));
                // 区域总企业操作权限
                boolean companyDeptLeaderPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_DEPT_LEADER.getPermissionCode());
                Long currentUserId = ShiroUtil.getCurrentUserId();
                Long matchUserIdOld = company.getMatchUserId();
                Boolean updateFlg = false;
                if(currentUserId != null && matchUserIdOld != null && currentUserId.equals(matchUserIdOld)) {
                    updateFlg = true;
                }

                if (!BasConstants.COMPANY_STATUS_F.equals(company.getStatus()) || updateFlg) {
                    CompanyStatusVo vo = new CompanyStatusVo();
                    vo.setId(Long.parseLong(companyId));
                    vo.setStatus(BasConstants.COMPANY_STATUS_F);
                    vo.setMatchUserId(Long.parseLong(matchUserId));
                    vo.setMatchUserName(SysUserSdk.getNickName());
                    handelDeptId(vo,vo.getMatchUserId());
                    vo.setCreateUserId(ShiroUtil.getCurrentUserId());
                    vo.setCreateUserName(ShiroUtil.getCurrentUserName());
                    vo.setAssignedUserId(ShiroUtil.getCurrentUserId());
                    vo.setAssignedUserName(ShiroUtil.getCurrentUserName());
                    companyClient.updateStatusByAssigned(vo);
                    RenderUtil.renderText("success", response);
                } else {
                    RenderUtil.renderText("alreadyFllow", response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderText("fail", response);
        }
    }

    /**
     * 操作历史按钮
     *
     */
    @RequestMapping(value = "opreateHis/{id}", method = RequestMethod.GET)
    public String opreateHis(@PathVariable("id") Long id, Model model) {
        if (id != null && id > 0L) {
            model.addAttribute("companyId", id);
        }
        model.addAttribute("companyStatus",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_COMPANY_STATUS)));// 公司状态
        return "bs/companyOperateHistory";
    }

    /**
     * 退回共享按钮
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "backShare", method = RequestMethod.POST)
    public void backShare(HttpServletRequest request, HttpServletResponse response) {
        try {
            String id = request.getParameter("id");
            Long userId = ShiroUtil.getCurrentUserId();
            SysUserSdk SysUserSdk = authOpenFacade.findUserById(userId);
            if (StringUtils.isNotBlank(id)) {
                Long companyId = Long.valueOf(id);
                BsCompanyShare share = new BsCompanyShare();
                share.setCompanyId(companyId);
                share.setSharedUserId(userId);
                BsCompany entity = getService().getEntity(companyId);
                BsCompanyShare shareCompany = bsCompanyShareClient.findByCompanyIdAndSharedUserId(share);
                bsCompanyShareClient.delete(shareCompany.getId());
                BsCompanyOphisVo opHis = new BsCompanyOphisVo();
                opHis.setCompanyId(companyId);
                opHis.setCreateUserId(userId);
                opHis.setCreateUserName(SysUserSdk.getNickName());
                opHis.setStatus(entity.getStatus());
                opHis.setEnterpriseId(entity.getEnterpriseId());
                opHis.setOptionType(BasConstants.COMPANY_STATUS_O);
                opHis.setRemark(entity.getCompanyName());
                opHis.setOperation("2");
                opHis.setTargetName("企业管理");
                bsCompanyOphisClient.addCompanyHis(opHis);
                RenderUtil.renderText("success", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderText("fail", response);
        }
    }

    private void updateStatus(Long id, String status) {
        CompanyStatusVo vo = new CompanyStatusVo();
        vo.setId(id);
        vo.setStatus(status);
        vo.setCreateUserId(ShiroUtil.getCurrentUserId());
        vo.setCreateUserName(ShiroUtil.getCurrentUserName());
        vo.setAssignedUserId(null);
        vo.setAssignedUserName(null);
        vo.setOwnerOfAccountId(ShiroUtil.getCurrentUserId());

        if (StringUtils.equals(BasConstants.COMPANY_STATUS_F,status)) {
            // 私海
            handelDeptId(vo,ShiroUtil.getCurrentUserId());
        } else {
            // 公海
            BsCompany bc = companyClient.getEntity(id);
            if (Objects.nonNull(bc) && bc.getOwnerOfAccountId() != null) {
                handelDeptId(vo,bc.getOwnerOfAccountId());
            } else {
                vo.setDeptId(null);
            }

        }
        companyClient.updateCompanyStatus(vo);
    }

    /**
     * 处理部门ID数据
     * @param vo
     * @param userId
     */
    public void handelDeptId(CompanyStatusVo vo,Long userId){
        if (userId != null) {
            SysUserSdk user = authOpenFacade.findUserById(userId);
            if (Objects.nonNull(user) && user.getDeptId() != null) {
                Long deptId;
                SysDeptSdk dept = authOpenFacade.findDeptById(user.getDeptId());
                if (Objects.nonNull(dept)) {
                    if (StringUtils.equals(BasConstants.DEPTTYPE_TEAM,dept.getDeptType())) {
                        deptId = dept.getParentId();
                    } else {
                        deptId = dept.getDeptId();
                    }
                } else {
                    deptId = user.getDeptId();
                }
                vo.setDeptId(deptId);
            } else {
                vo.setDeptId(null);
            }
        } else {
            vo.setDeptId(null);
        }

    }

    @Override
    protected void doLog(HttpServletRequest request, BsCompany e, String operation) {
        LogUtil.del(request, e);
    }

    @ResponseBody
    @RequestMapping(value = "findCompanyById", method = RequestMethod.POST)
    public BsCompany findCompanyById(Long id) {
        BsCompany company = getEntity(id);
        if (company != null) {
            return company;
        }
        return null;
    }

    @Override
    @RequestMapping(value = "delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            BsCompany entity = getService().getEntity(id);
            entity.setEnableFlg(false);
            companyClient.save(entity);
            RenderUtil.renderSuccess("删除成功", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("操作错误，请联系管理员", response);
        }
        return null;
    }

    /**
     * 刷新线上化标识
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "refreshOnLineFlg", method = RequestMethod.POST)
    public String refreshOnLineFlg(HttpServletRequest request, HttpServletResponse response) {
        try {
            String id = request.getParameter("id");
            if (StringUtils.isNotBlank(id)) {
                companyClient.refreshCompanyFlg(Long.valueOf(id));
                RenderUtil.renderSuccess("success", response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @PostMapping(value = "findByCompanyId")
    @ResponseBody
    public Map<String, Object> findByCompanyId(@RequestParam("companyId") Long companyId) {
        Map<String, Object> map = new HashMap<>();
        if (companyId != null && companyId != 0L) {
            BsCompany company = companyClient.getEntity(companyId);
            map.put("company", company);
        }
        return map;
    }


    /*
     * 帮助线上化申请
     * */
    @RequestMapping(value = "saveApplyOnLineData", method = RequestMethod.POST)
    @ResponseBody
    public void saveApplyOnLineData(CompanyOnLineApplyVo vo, HttpServletResponse response) {
        RespVo<CompanyUser> companyUserRespVo = wxUserClient.saveApplyOnLineData(vo);
        RenderUtil.renderSuccess(companyUserRespVo.getMessage(), response);
    }


    @RequestMapping(value = "getInfoByCompanyId/{companyId}")
    @ResponseBody
    public void getInfoByCompanyId(@PathVariable("companyId") Long companyId, HttpServletResponse response) {
        if (Objects.isNull(companyId)) {
            RenderUtil.renderJson("", response);
            return;
        }
        List<SaveInfo> saveInfoList = saveTempClient.getInfosByCompanyId(companyId, false, SaveInfoType.BASE_INFO.getType());
        if (CollectionUtils.isNotEmpty(saveInfoList)) {
            SaveInfo saveInfo = saveInfoList.stream().max(Comparator.comparing(SaveInfo::getUpdatedDate)).orElse(null);
            if (Objects.nonNull(saveInfo) && StringUtils.isNotBlank(saveInfo.getContent())) {
                CompanyOnLineApplyVo applyVo = JsonUtil.json2Object(CompanyOnLineApplyVo.class, saveInfo.getContent());
                if (Objects.nonNull(applyVo)) {
                    applyVo.setBusinessLicenseWithSealUrl(applyVo.parseBusinessLicenseWithSealUrl());
                    applyVo.setLegalPersonPicUrl(applyVo.parseLegalPersonPicUrl());
                    applyVo.setLegalPersonOppositePicUrl(applyVo.parseLegalPersonOppositePicUrl());
                    applyVo.setElectronicSignFileId(applyVo.parseElectronicSignFileId());
                }
                RenderUtil.renderJson(applyVo, response);
                return;
            }
        }
        RenderUtil.renderJson("", response);
    }
    @RequestMapping(value = "exportCreditInfo0Excel")
    public void exportCreditInfo0Excel(BsCompanySearchVo queryVo, HttpServletRequest request, HttpServletResponse response) {
        List<RptCompanyCreditInfo0> content = bsCompanyClient.getCompanyCreditInfo0();
        Long userId = ShiroUtil.getCurrentUserId();
        String userName = ShiroUtil.getCurrentUserName();
        BsLog log = new BsLog();
        log.setIpAddre(IPUtil.getIpAddr(request));
        log.setRemortPort(request.getRemotePort());
        log.setOperation("3");
        log.setOperatorId(userId);
        log.setOperatorName(userName);
        log.setTargetName("企业管理");
        log.setRemark("导出企业访厂报告");
        bsLogClient.save(log);
        if (CollectionUtils.isNotEmpty(content)) {
            List<CompanyCreditInfo0Vo> list = content.stream().map(it -> {
                CompanyCreditInfo0Vo vo = new CompanyCreditInfo0Vo();
                vo.setAddress(it.getAddress());
                vo.setCompanyName(it.getCompanyName());
                vo.setContractTime(it.getContractTime());
                vo.setFileId(it.getFileId());
                vo.setLegalRepresent(it.getLegalRepresent());
                vo.setRegisterCapital(it.getRegisterCapital());
                vo.setStartDate(it.getStartDate());
                vo.setCreditAmount(it.getCreditAmount());
                return vo;
            }).collect(Collectors.toList());
            // 设置请求参数
            StringBuffer url = request.getRequestURL();
            String uri = request.getRequestURI();
            String domain = url.substring(0, url.indexOf(uri));
            queryVo.setRequestUrl(domain);
            queryVo.setUserId(ShiroUtil.getCurrentUserId());
            queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
            CompanyCreditExportVo companyCreditExportVo = new CompanyCreditExportVo();
            companyCreditExportVo.setBsCompanySearchVo(queryVo);
            companyCreditExportVo.setCompanyCreditInfo0VoList(list);
            companyClient.exportCreditInfo0Excel(companyCreditExportVo);
        }
    }
}
