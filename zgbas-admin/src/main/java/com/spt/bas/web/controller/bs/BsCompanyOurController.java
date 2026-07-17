package com.spt.bas.web.controller.bs;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.bas.client.entity.EvaluateItem;
import com.spt.bas.client.remote.IBsCompanyOurClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/bs/companyOur")
public class BsCompanyOurController extends SingleCrudControll<BsCompanyOur, BaseVo> {
    @Autowired
    private IBsCompanyOurClient bsCompanyOurClient;
    @Override
    public BaseClient<BsCompanyOur> getService() {
        return bsCompanyOurClient;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("creditTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_CREDIT_TYPE)));//授信类别

        return "bs/companyOur";
    }
    @Override
    protected Map<String, String> getDefaultOrder() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("dispOrderNo", "desc");
        return map;
    }

    @RequestMapping(value = "listOrder")
    public void listOrder(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        searchVo.setSort("dispOrderNo");
        searchVo.setOrder("ASC");
        PageDown<BsCompanyOur> page = bsCompanyOurClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

}
