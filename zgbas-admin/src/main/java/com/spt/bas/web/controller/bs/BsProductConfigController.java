package com.spt.bas.web.controller.bs;

import com.spt.bas.client.entity.BsProductConfig;
import com.spt.bas.client.remote.IBsProductConfigClient;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品信息配置表
 */
@Controller
@RequestMapping("/bs/productConfig")
public class BsProductConfigController extends SingleCrudControll<BsProductConfig, BaseVo> {

    @Autowired
    private IBsProductConfigClient productConfigClient;

    @Override
    public BaseClient<BsProductConfig> getService() {
        return productConfigClient;
    }

    @RequestMapping(value = "")
    public String index() {
        return "bs/productConfig";
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
        BsProductConfig entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = productConfigClient.getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new BsProductConfig();
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        return "bs/productConfig-detail";

    }

    /**
     * 修改方法
     */
    @RequestMapping("updateConfig")
    public void updateConfig(BsProductConfig bsProductConfig, HttpServletResponse response) {
        try {
            bsProductConfig.setEnterpriseId(ShiroUtil.getEnterpriseId());
            productConfigClient.save(bsProductConfig);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("updateConfig:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    /**
     * 删除方法
     */
    @Override
    @RequestMapping(value = "delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            productConfigClient.delete(id);
            RenderUtil.renderSuccess("删除成功", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("操作错误，请联系管理员", response);
        }
        return null;
    }
}
