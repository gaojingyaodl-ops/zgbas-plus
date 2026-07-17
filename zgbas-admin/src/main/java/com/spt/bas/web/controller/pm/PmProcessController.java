/**
 *
 */
package com.spt.bas.web.controller.pm;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmProcess;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * 流程管理
 *
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/pm/process")
public class PmProcessController extends PageController<PmProcess, BaseVo> {

    @Autowired
    private IPmProcessClient processClient;

    @Override
    public BaseClient<PmProcess> getService() {
        return processClient;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("enableFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        model.addAttribute("processGroup", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.DictType.DICT_PROCESS_GROUP)));
        return "pm/process";
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("enableFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
        model.addAttribute("processGroup",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.DictType.DICT_PROCESS_GROUP)));
        model.addAttribute("stepGroupType",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.DictType.DICT_STEP_GROUP_TYPE)));
        PmProcess entity;
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new PmProcess();
        }
        model.addAttribute("entity", entity);
        return "pm/process-detail";
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }


    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(@Valid PmProcess entity, HttpServletRequest request,
                     HttpServletResponse response) {
        try {
            entity.setEnterpriseId(ShiroUtil.getEnterpriseId());
            entity = getService().save(entity);
            RenderUtil.renderSuccess(entity.getId() + "", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 使用@ModelAttribute, 实现Struts2
     * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
     * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
     */
    @ModelAttribute("preload")
    public PmProcess getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0)
                return getService().getEntity(id);
            else {
                PmProcess entity = new PmProcess();
                entity.setId(0l);
                return entity;
            }
        }
        return null;
    }
}
