package com.spt.bas.web.controller.bs;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsConfig;
import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.remote.IBsConfigClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.LogUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 业务开关配置
 */
@Controller
@RequestMapping("/bs/config")
public class BsConfigController extends SingleCrudControll<BsConfig, BaseVo> {

    @Autowired
    private IBsConfigClient bsConfigClient;
    @Autowired
    private IBsCompanyDcsxClient bsCompanyDcsxClient;

    @Override
    public BaseClient<BsConfig> getService() {
        return bsConfigClient;
    }

    @RequestMapping(value = "getConfigMessageList")
    public void findAllConfig(HttpServletResponse response) {
        List<String> configMessageList = bsConfigClient.findConfigMessageList(ShiroUtil.getEnterpriseId());
        RenderUtil.renderJson(configMessageList, response);
    }

    @RequestMapping(value = "getBsConfigList")
    public String getBsConfigList(HttpServletResponse response,Model model) {
        List<BsConfig> bsConfigList = bsConfigClient.getBsConfigList(ShiroUtil.getEnterpriseId());
        model.addAttribute("bsConfigList",JsonUtil.obj2Json(bsConfigList));
        return "admin/index_business_inquiry";
    }
    @RequestMapping(value = "getStockInquiryList")
    public String getStockInquiryList(HttpServletResponse response,Model model) {
        return "admin/index_stock_inquiry";
    }
    /**
     * 查询所有信息
     *
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findConfig")
    public void findAllConfig(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<BsConfig> page = bsConfigClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    protected void preInsert(BsFactory e) {
        e.setEnterpriseId(ShiroUtil.getEnterpriseId());
    }

    /**
     * 赊销模式：contractModel
     * 资金来源：fundSource
     * 我方抬头：ourCompanyName
     * 代采赊销单位：sxCompany
     * 是否有效：enableFlgs
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "")
    public String findFactory(Model model) {
        Long enterpriseId = ShiroUtil.getEnterpriseId();
        // 授信类别
        model.addAttribute("creditTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_CREDIT_TYPE)));
        //是否有效
        model.addAttribute("enableFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        //赊销模式
        model.addAttribute("contractModel",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(enterpriseId, BasConstants.CONFIG_TYPE_CONTRACT_MODEL)));
        //资金来源
        model.addAttribute("fundSource",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(enterpriseId, BasConstants.CONFIG_TYPE_FUND_SOURCE)));
        //我方抬头
        model.addAttribute("ourCompanyName",
                JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //代采赊销单位
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("sxCompany", JsonUtil.obj2Json(dcsxCompanyList));
        return "bs/config";
    }

    /**
     * 修改方法
     *
     * @param request
     * @param response
     */
    @RequestMapping("updateConfig")
    public void updateConfig(BsConfig bsConfig, HttpServletRequest request, HttpServletResponse response) {
        logger.info(">>>>>updateConfig<<<<<,业务员:{}", ShiroUtil.getCurrentUserName());
        logger.info(">>>>>param<<<<<:{}", bsConfig);
        bsConfig.setSxCompany(StringUtils.isBlank(bsConfig.getSxCompany()) ? null : bsConfig.getSxCompany());
        bsConfig.setEnterpriseId(ShiroUtil.getEnterpriseId());
        bsConfig.setMatchUserId(String.valueOf(ShiroUtil.getCurrentUserId()));
        bsConfig.setMatchUserName(ShiroUtil.getCurrentUserName());
        try {
            logger.info(">>>>>业务开关配置修改<<<<<,{}", JsonUtil.obj2Json(bsConfig));
            BsConfig old = bsConfigClient.getEntity(bsConfig.getId());
            bsConfigClient.save(bsConfig);
            LogUtil.saveOrUpdate(request, old, bsConfig, bsConfig.getId());
            RenderUtil.renderSuccess("修改成功", response);
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderSuccess("修改失败", response);
        }
    }

    /**
     * 跳转到详情页
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        Long enterpriseId = ShiroUtil.getEnterpriseId();
        model.addAttribute("enableFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        //赊销模式
        model.addAttribute("contractModel",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(enterpriseId, BasConstants.CONFIG_TYPE_CONTRACT_MODEL)));
        //资金来源
        model.addAttribute("fundSource",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(enterpriseId, BasConstants.CONFIG_TYPE_FUND_SOURCE)));
        //我方抬头
        model.addAttribute("ourCompanyName",
                JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //代采赊销单位
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("sxCompany", JsonUtil.obj2Json(dcsxCompanyList));
        BsConfig entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new BsConfig();
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        return "bs/config-detail";

    }

    @ModelAttribute("preload")
    public BsConfig getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0)
                return getService().getEntity(id);
            else {
                BsConfig entity = new BsConfig();
                entity.setId(0L);
                return entity;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }

    /**
     * 删除方法
     *
     * @param id
     * @param request
     * @param response
     * @return
     */
    @Override
    @RequestMapping(value = "delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            LogUtil.del(request, bsConfigClient.getEntity(id));
            bsConfigClient.delete(id);
            RenderUtil.renderSuccess("删除成功", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("操作错误，请联系管理员", response);
        }
        return null;
    }

    /**
     * 添加方法
     *
     * @param request
     * @param response
     */
    @RequestMapping("/save")
    public void saveBsConfig(BsConfig bsConfig, HttpServletRequest request, HttpServletResponse response) {
        logger.info(">>>>>param<<<<<:{}", bsConfig);
        bsConfig.setId(0L);
        bsConfig.setSxCompany(StringUtils.isBlank(bsConfig.getSxCompany()) ? null : bsConfig.getSxCompany());
        bsConfig.setEnterpriseId(ShiroUtil.getEnterpriseId());
        bsConfig.setMatchUserId(String.valueOf(ShiroUtil.getCurrentUserId()));
        bsConfig.setMatchUserName(ShiroUtil.getCurrentUserName());
        try {
            logger.info(">>>>>业务开关配置新增<<<<<,{}", JsonUtil.obj2Json(bsConfig));
            bsConfigClient.save(bsConfig);
            LogUtil.saveOrUpdate(request, new BsConfig(), bsConfig, bsConfig.getId());
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderFailure("fail", response);
        }
    }
}
