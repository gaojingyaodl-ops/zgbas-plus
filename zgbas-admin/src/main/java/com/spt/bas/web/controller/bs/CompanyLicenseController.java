package com.spt.bas.web.controller.bs;

import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.bas.client.entity.CompanyLicense;
import com.spt.bas.client.remote.IBsCompanyOurClient;
import com.spt.bas.client.remote.ICompanyLicenseClient;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 公司证照管理controller
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/20 10:31
 */
@Controller
@RequestMapping(value = "/company/license")
public class CompanyLicenseController extends PageController<CompanyLicense, BaseVo> {

    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private ICompanyLicenseClient companyLicenseClient;
    @Autowired
    private IBsCompanyOurClient bsCompanyOurClient;

    @Override
    public BaseClient<CompanyLicense> getService() {
        return companyLicenseClient;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("companyLicenseFileTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.COMPANY_LICENSE_FILE_TYPE)));
        model.addAttribute("companyLicenseUserTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.COMPANY_LICENSE_USER_TYPE)));
        return "bs/companyLicense";
    }


    @RequestMapping(value = "/listPage")
    public void listPage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<CompanyLicense> page = companyLicenseClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    /**
     * 跳转到新增或编辑页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") Long id, Model model) {
        model.addAttribute("companyLicenseFileTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.COMPANY_LICENSE_FILE_TYPE)));
        List<BsCompanyOur> companyOurs = bsCompanyOurClient.findAll();
        if (CollectionUtils.isNotEmpty(companyOurs)) {
            List<Map<String, Object>> companyList = companyOurs.stream().filter(e -> Boolean.TRUE.equals(e.getEnableFlg())).map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("companyName", e.getCompanyName());
                map.put("id", e.getId());
                map.put("companyCode", e.getCompanyCd());
                map.put("companyAbbr", e.getCompanyAbbr());
                return map;
            }).collect(Collectors.toList());
            String s = JsonUtil.obj2Json(companyList);
            model.addAttribute("companyListJson", s);
        } else {
            model.addAttribute("companyListJson", new ArrayList<>());
        }
        if (Objects.equals(id, -1L)) {
            model.addAttribute("entity", new CompanyLicense());
        } else {
            CompanyLicense companyLicense = companyLicenseClient.getEntity(id);
            model.addAttribute("entity", companyLicense);
        }
        return "bs/companyLicenseDetail";
    }

    /**
     * 保存
     *
     * @param entity   保存实体
     * @param response 响应
     */
    @PostMapping(value = "/save")
    public void save(@RequestBody CompanyLicense entity, HttpServletResponse response) {
        if (StringUtils.isBlank(entity.getCompanyCode()) || StringUtils.isBlank(entity.getCompanyName())) {
            RenderUtil.renderFailure("公司编码和公司名称不能为空！", response);
            return;
        }
        if (StringUtils.isBlank(entity.getFileType())) {
            RenderUtil.renderFailure("文件类型不能为空！", response);
            return;
        }
        if (StringUtils.isBlank(entity.getFileName())) {
            RenderUtil.renderFailure("文件名称不能为空！", response);
            return;
        }
        if (StringUtils.isBlank(entity.getFileId())) {
            RenderUtil.renderFailure("附件不能为空！", response);
            return;
        }
        if (!StringUtils.equals(entity.getFileType(), BasConstants.FILE_TYPE_4)) {
            List<CompanyLicense> list = companyLicenseClient.getCodeAndFileType(entity.getCompanyCode(), entity.getFileType());
            if (CollectionUtils.isNotEmpty(list)) {
                if (Objects.isNull(entity.getId()) || !Objects.equals(list.get(0).getId(), entity.getId())) {
                    RenderUtil.renderFailure("该公司已存在该文件类型的文件", response);
                    return;
                }
            }
        }

        companyLicenseClient.save(entity);
        RenderUtil.renderSuccess("success", response);
    }

    /**
     * 删除
     *
     * @param id id
     */
    @GetMapping("/remove/{id}")
    public void remove(@PathVariable Long id, HttpServletResponse response) {
        if (Objects.isNull(id)) {
            RenderUtil.renderFailure("id不能为空！", response);
        }
        companyLicenseClient.delete(id);
        RenderUtil.renderSuccess("success", response);
    }



}
