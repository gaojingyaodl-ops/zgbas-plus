package com.spt.bas.web.controller.bs;


import com.spt.bas.client.entity.BsLog;
import com.spt.bas.client.remote.IBsLogClient;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/bs/log")
public class BsLogController extends PageController<BsLog, BaseVo> {

    @Autowired
    private IBsLogClient bsLogClient;

    @RequestMapping(value = "index")
    public String index(Model model) {
        return "bs/BsLog";
    }

    @RequestMapping(value = "LogList")
    public void LogList(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        searchVo.setAdmin(true);
        logger.info("searchVo : " + JsonUtil.obj2Json(searchVo));
        Map<String, Object> footer = new HashMap<>();
        Page<BsLog> page = bsLogClient.findPage(searchVo);
        List<BsLog> content = page.getContent();
        for (BsLog bsLogVo : content) {
            if (!StringUtils.isEmpty(bsLogVo.getOperation())) {
                switch (bsLogVo.getOperation()) {
                    case "0":
                        bsLogVo.setOperation("增加");
                        break;
                    case "1":
                        bsLogVo.setOperation("删除");
                        break;
                    case "2":
                        bsLogVo.setOperation("修改");
                        break;
                    case "3":
                        bsLogVo.setOperation("导出");
                        break;
                }
            }
        }
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    @Override
    public BaseClient<BsLog> getService() {
        return bsLogClient;
    }


    @RequestMapping("detail")
    public  String detail (BsLog bsLog, HttpServletRequest request, HttpServletResponse response, Model model){
        BsLog entity = bsLogClient.getEntity(bsLog.getId());
        if (!StringUtils.isEmpty(entity.getOperation())) {
            switch (entity.getOperation()) {
                case "0":
                    entity.setOperation("增加");
                    break;
                case "1":
                    entity.setOperation("删除");
                    break;
                case "2":
                    entity.setOperation("修改");
                    break;
            }
        }
        model.addAttribute("entity",entity);
        return "bs/bsLogDetail";
    }
}
