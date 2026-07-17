package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.Feedback;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *      意见反馈审批
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-10 13:42
 */
@Controller
@RequestMapping(value = "/apply/feedback")
public class ApplyFeedbackController extends PageController<Feedback, BaseVo> {
    @Override
    public BaseClient<Feedback> getService() {
        return null;
    }
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    @Value("${file.show.url}")
    private String fileShowUrl;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        Feedback entity = getEntity(id, processCode);
        model.addAttribute("entity", entity);
        String[] split = entity.getAttachIds().split(",");
        List<String> attachIds = new ArrayList<>();
        for (String s : split) {
            attachIds.add(fileShowUrl + "/view/show/" + s);
        }
        model.addAttribute("attachUrls", attachIds);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "apply/feedback";
    }

    @ModelAttribute("preload")
    public Feedback getEntity(@RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "processCode", required = false) String processCode) {
        Feedback enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (Feedback) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                Feedback bsCompanyAllowed = new Feedback();
                bsCompanyAllowed.setId(0L);
                return bsCompanyAllowed;
            }
        }
        return enObject;
    }

    private String getStatus(Long id) {
        if (id != null && id > 0L) {
            PmApproveContents entity = pmApproveContentsClient.getEntity(id);
            if (entity != null) {
                return entity.getStatus();
            }
        }
        return BasConstants.APPROVE_STATUS_N;
    }
}
