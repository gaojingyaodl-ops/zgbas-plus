package com.spt.bas.web.controller.bas;

import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.config.BasicErrorController;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 代采方管理
 * @author
 *
 */
@Controller
@RequestMapping(value = "/bs/companyDcsx")
public class BsCompanyDcsxController extends SingleCrudControll<BsCompanyDcsx, BaseVo> {

    @Resource
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsCompanyDcsxClient companyDcsxClient;

    @Override
    public BaseClient<BsCompanyDcsx> getService() {
        return bsCompanyDcsxClient;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("creditTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_CREDIT_TYPE)));//授信类别
        model.addAttribute("listByCategory",  JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_CALCULATE_TYPE)));
        model.addAttribute("enterpriseId", ShiroUtil.getEnterpriseId());
        model.addAttribute("chainPayType",  JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_CHAIN_PAY_TYPE)));
        model.addAttribute("defaultFlg",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DEFAULTFLG)));// 是否

        return "bas/companyDcsx";
    }
    @Override
    protected Map<String, String> getDefaultOrder() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("dispOrderNo", "desc");
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "ourCompanyName", method = RequestMethod.POST)
    public BsCompanyDcsx findCompanyByName(String ourCompanyName) {
        BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(ourCompanyName);
        if (byCompanyName != null) {
            return byCompanyName;
        }
        return null;
    }
    // 代采方余额界面
    @RequestMapping(value = "balance")
    public String balance(Model model) {
        boolean importPiccInsurancePerm = ShiroUtil.isPermitted(PermissionEnum.PERM_BS_COMPANYDCSX_IMPORT_INSURANCE.getPermissionCode());
        model.addAttribute("importPiccInsurancePerm", importPiccInsurancePerm);
        return "bas/companyDcsxBalance";
    }
    // 资金方保费充值界面
    @RequestMapping(value = "recharge/{companyCd}/{flowType}",method = RequestMethod.GET)
    public String recharge(@PathVariable("companyCd") String companyCd,@PathVariable("flowType")String  flowType,Model model) {
        BsCompanyDcsx byCompanyCd = bsCompanyDcsxClient.findByCompanyCd(companyCd);
        model.addAttribute("entity",byCompanyCd);
        model.addAttribute("flowType",flowType);

        return "bas/companyDcsxRecharge";
    }
    @RequestMapping(value = "showImportPiccInsuranceExcel")
    public String showImportPiccInsuranceExcel(Model model) {
        return "bs/import_picc_insurance_excel";
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        logger.info("updateFileId : " + JsonUtil.obj2Json(vo));
        try {
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }
    /**
     * 人保保费流水导入
     * @param request
     * @param response
     */
    @RequestMapping(value = "importPiccInsuranceExcel")
    public void importPiccInsuranceExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileIds = request.getParameter("fileId");
            if(StringUtils.isNotBlank(fileIds)){
                String[] split = fileIds.split(",");
                List<String> results = new ArrayList<>();
                for (String fileId : split) {
                    results = companyDcsxClient.importPiccInsuranceExcel(fileId);
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


}
