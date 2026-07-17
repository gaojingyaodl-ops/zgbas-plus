package com.spt.bas.web.controller.pm;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.remote.IPmProcessPushClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.entity.PmProcessPush;
import com.spt.pm.vo.PmProcessSearchVo;
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

/**
 * 流程推送配置
 * @Author: gaojy
 * @create 2022/4/26 18:07
 * @version: 1.0
 * @description:
 */
@Controller
@RequestMapping(value = "/pm/push")
public class PmProcessPushController extends SingleCrudControll<PmProcessPush, BaseVo> {
    @Autowired
    private IPmProcessPushClient pmProcessPushClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IPmProcessClient pmProcessClient;

    @Override
    public BaseClient<PmProcessPush> getService() {
        return pmProcessPushClient;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        // 是否有效数据字典
        model.addAttribute("enableFlgs",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));

        // 获取部门负责人数据
        EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
        model.addAttribute("deptTree",JsonUtil.obj2Json(nodes.getChildren()));

        // 查询所有流程
        PmProcessSearchVo processSearchVo = new PmProcessSearchVo(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = pmProcessClient.findByEnterpriseId(processSearchVo);
        model.addAttribute("processList",JsonUtil.obj2Json(processList));
        return "pm/processPush";
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        PmProcessPush entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new PmProcessPush();
            entity.setId(0L);
            entity.setEnableFlg(true);
            entity.setEnterpriseId(ShiroUtil.getEnterpriseId());
        }
        model.addAttribute("entity", entity);
        return "pm/processPush-detail";

    }

    @RequestMapping(value = "saveProcessPush", method = RequestMethod.POST)
    public void saveProcessPush(PmProcessPush processPush, HttpServletResponse response) {
        try {
            pmProcessPushClient.save(processPush);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("saveProcessPush:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @ModelAttribute("preload")
    public PmProcessPush getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                PmProcessPush entity = new PmProcessPush();
                entity.setId(0L);
                entity.setEnableFlg(true);
                entity.setEnterpriseId(ShiroUtil.getEnterpriseId());
                return entity;
            }
        }
        return null;
    }
}
