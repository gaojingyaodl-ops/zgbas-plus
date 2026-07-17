package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInterestPay;
import com.spt.bas.client.remote.IApplyInterestPayClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
@RequestMapping(value = "/apply/interestPay")
public class ApplyInterestPayController extends PageController<ApplyInterestPay, BaseVo> {
    @Resource
    private IApplyInterestPayClient applyInterestPayClient;

    @Override
    public BaseClient<ApplyInterestPay> getService() {
        return applyInterestPayClient;
    }


    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyInterestPay entity = getEntity(id);
        model.addAttribute("entity", entity);
        return "apply/interestPay-content";
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            applyInterestPayClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
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
    public ApplyInterestPay getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                ApplyInterestPay entity = new ApplyInterestPay();
                entity.setId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                entity.setPayDate(new Date());
                return entity;
            }
        }
        return null;
    }

    @RequestMapping(value = "queryCancelList", method = RequestMethod.POST)
    public void queryCancelList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {

    }
}
