package com.spt.bas.web.controller.bs;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyConfig;
import com.spt.bas.client.remote.IBsCompanyConfigClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 企业计算配置表
 * @Author: gaojy
 * @create 2022/4/2 11:12
 * @version: 1.0
 * @description:
 */
@Controller
@RequestMapping(value = "/bs/companyConfig")
public class BsCompanyConfigController extends SingleCrudControll<BsCompanyConfig, BaseVo> {
    @Autowired
    private IBsCompanyConfigClient bsCompanyConfigClient;
    @Resource
    private IAuthOpenFacade authOpenFacade;
    @Override
    public BaseClient<BsCompanyConfig> getService() {
        return bsCompanyConfigClient;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("enableFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        return "bs/companyConfig";
    }

    @RequestMapping(value = "saveCompanyConfig", method = RequestMethod.POST)
    public void saveCompanyConfig(BsCompanyConfig companyConfig, HttpServletResponse response) {
        try {
            BsCompanyConfig config = bsCompanyConfigClient.findByBsCompanyIdAndMatchUserId(companyConfig);
            if (Objects.nonNull(config) && !config.getId().equals(companyConfig.getId())){
                RenderUtil.renderFailure("该企业配置已存在,不可重复添加!", response);
                return;
            }
            companyConfig.setEnterpriseId(ShiroUtil.getEnterpriseId());
            bsCompanyConfigClient.save(companyConfig);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("saveCompanyConfig:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        BsCompanyConfig entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new BsCompanyConfig();
            entity.setId(0L);
        }
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("entity", entity);
        return "bs/companyConfig-detail";

    }

    @ModelAttribute("preload")
    public BsCompanyConfig getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                BsCompanyConfig entity = new BsCompanyConfig();
                entity.setId(0L);
                return entity;
            }
        }
        return null;
    }
}
