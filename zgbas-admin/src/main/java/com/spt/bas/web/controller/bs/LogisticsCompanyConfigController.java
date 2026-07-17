package com.spt.bas.web.controller.bs;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.bas.client.entity.LogisticsCompanyDetail;
import com.spt.bas.client.remote.LogisticsCompanyConfigClient;
import com.spt.bas.client.remote.LogisticsCompanyDetailClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * 承运商管理表
 */
@Controller
@RequestMapping("/bs/logisticsCompanyConfig")
public class LogisticsCompanyConfigController extends SingleCrudControll<LogisticsCompanyConfig, BaseVo> {

    @Autowired
    private LogisticsCompanyConfigClient logisticsCompanyConfigClient;

    @Autowired
    private LogisticsCompanyDetailClient logisticsCompanyDetailClient;


    @Override
    public BaseClient<LogisticsCompanyConfig> getService() {
        return logisticsCompanyConfigClient;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
                BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //是否有效
        model.addAttribute("enableFlgJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        return "bs/logisticsCompanyConfig";
    }

    /**
     * 分页查询
     *
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findByAll")
    public void findByAll(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<LogisticsCompanyConfig> page = logisticsCompanyConfigClient.findPage(searchVo);
        //计算平均分
        List<LogisticsCompanyConfig> content = page.getContent();
        content.stream().forEach(s->{
            BigDecimal  byCarrierScoreAVG = logisticsCompanyDetailClient.findByCarrierScoreAVG(s.getId());
            s.setAverageScore(byCarrierScoreAVG);
        });
        JsonEasyUI.renderJson(response, page);
    }


    /**
     * 修改方法
     *
     * @param request
     * @param response
     */
    @RequestMapping("updateConfig")
    public void updateConfig(LogisticsCompanyConfig config,HttpServletRequest request, HttpServletResponse response) {
            try {
                logisticsCompanyConfigClient.save(config);
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
        //我方企业
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
                BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //是否有效
        model.addAttribute("enableFlgJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        LogisticsCompanyConfig entity ;
        if (id != null && id != 0) {
            entity = logisticsCompanyConfigClient.getEntity(id);
            BigDecimal byCarrierScoreAVG = logisticsCompanyDetailClient.findByCarrierScoreAVG(entity.getId());
            entity.setAverageScore(byCarrierScoreAVG);
            model.addAttribute("entity", entity);
        } else {
            entity = new LogisticsCompanyConfig();
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        return "bs/logisticsCompanyConfig-detail";

    }


    /**
     * 删除方法
     *
     * @param id
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            logisticsCompanyConfigClient.delete(id);
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
    @RequestMapping("saveConfig")
    public void saveBsConfig(LogisticsCompanyConfig config,HttpServletRequest request, HttpServletResponse response) {
        try {
            logisticsCompanyConfigClient.save(config);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderFailure("fail", response);
        }
     }


    //承运商评分记录
    @RequestMapping(value = "/carrierDetail")
    public void delete(String carrier, HttpServletRequest request, HttpServletResponse response) {
        LogisticsCompanyConfig logisticsCompanyConfig=new LogisticsCompanyConfig();
        logisticsCompanyConfig.setCarrier(carrier);
        LogisticsCompanyConfig byCarrier = logisticsCompanyConfigClient.getByCarrier(logisticsCompanyConfig);
        List<LogisticsCompanyDetail> byLogisticsCompanyId = logisticsCompanyDetailClient.findByLogisticsCompanyId(byCarrier.getId());
        JsonEasyUI.renderListJson(response, byLogisticsCompanyId);
    }
}
