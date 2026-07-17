package com.spt.bas.web.controller.bs;

import com.spt.bas.client.entity.BsTextContent;
import com.spt.bas.client.remote.IBsTextContentClient;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/text/content")
public class BsTextContentController extends SingleCrudControll<BsTextContent, BaseVo> {

    @Autowired
    private IBsTextContentClient bsTextContentClient;

    @Override
    public BaseClient<BsTextContent> getService() {
        return bsTextContentClient;
    }

    @RequestMapping(value = "noticeInterest")
    public String index(Model model) {
        return "bs/noticeInterest";
    }

    @RequestMapping(value = "textContentList")
    public void textContentList(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        logger.info("searchVo : " + JsonUtil.obj2Json(searchVo));
        Map<String, Object> footer = new HashMap<>();
        Page<BsTextContent> page = bsTextContentClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        BsTextContent bsTextContent = getEntity(id);
        model.addAttribute("textConsent", bsTextContent);
        return "bs/noticeInterest-detail";
    }

    /**
     * 保存
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(@Valid @ModelAttribute("preload") BsTextContent bsTextContent, HttpServletRequest request, HttpServletResponse response) {
        try{
            bsTextContentClient.save(bsTextContent);
            RenderUtil.renderText("success", response);
        }catch(Exception e){
            e.printStackTrace();
            RenderUtil.renderText("fail", response);
        }
    }

    /**
     * 使用@ModelAttribute, 实现Struts2
     * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
     * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
     */
    @ModelAttribute("preload")
    public BsTextContent getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0)
                return bsTextContentClient.getEntity(id);
            else {
                BsTextContent bsTextContent = new BsTextContent();
                bsTextContent.setId(0l);
                return bsTextContent;
            }
        }
        return null;
    }
}
