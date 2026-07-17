package com.spt.bas.web.controller.bs;

import com.spt.bas.client.entity.SysQuickSearch;
import com.spt.bas.client.remote.ISysQuickSearchClient;
import com.spt.bas.client.vo.SysQuickSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/sys/quick/search")
public class SysQuickSearchController extends PageController<SysQuickSearch, BaseVo> {

    @Autowired
    private ISysQuickSearchClient sysQuickSearchClient;
    
    @RequestMapping(value = "content")
    public String content(Model model, HttpServletRequest request) {
        String moduleUrl = request.getParameter("moduleUrl");
        SysQuickSearchVo searchVo = new SysQuickSearchVo();
        searchVo.setModuleUrl(moduleUrl);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        List<SysQuickSearch> sysQuickSearchList = sysQuickSearchClient.findListByUserIdAndModuleUrl(searchVo);
        model.addAttribute("sysQuickSearchList", sysQuickSearchList);
        model.addAttribute("quickSearchSize",sysQuickSearchList.size());
        return "common/quick-search";
    }

    /**
     * 保存查询条件
     * @param sysQuickSearch
     */
    @RequestMapping(value = "saveQuickSearch", method = RequestMethod.POST)
    public void saveQuickSearch(SysQuickSearch sysQuickSearch, HttpServletResponse response){
        System.out.println("保存");
        sysQuickSearch.setUserId(ShiroUtil.getCurrentUserId());
        sysQuickSearchClient.save(sysQuickSearch);
        RenderUtil.renderSuccess("success", response);
    }
    
    
    

    @Override
    public BaseClient<SysQuickSearch> getService() {
        return sysQuickSearchClient;
    }
}
