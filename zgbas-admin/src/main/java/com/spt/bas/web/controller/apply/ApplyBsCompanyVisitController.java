package com.spt.bas.web.controller.apply;

import com.spt.bas.client.entity.BsCompanyVisit;
import com.spt.bas.client.remote.IBsCompanyVisitClient;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author 田起立
 * @Date 2024/6/3 10:04
 * @Description: 访厂报告申请
 */
@Controller
@RequestMapping(value = "/apply/bsCompanyVisit")
public class ApplyBsCompanyVisitController extends PageController<BsCompanyVisit, BaseVo> {
    @Autowired
    private IBsCompanyVisitClient bsCompanyVisitClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Override
    public BaseClient<BsCompanyVisit> getService() {
        return null;
    }
    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        BsCompanyVisit companyVisit = bsCompanyVisitClient.getCompanyVisitById(id);
        model.addAttribute("companyVisit",companyVisit);
        permissionVo = webParamUtils.verifyPermission(permissionVo, companyVisit.getApproveId());
        model.addAttribute("psv", permissionVo);
        return "bs/bsCompanyVisit";
    }
}
