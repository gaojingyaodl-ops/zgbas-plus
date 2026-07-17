package com.spt.bas.web.controller.manual;

import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.entity.BasManual;
import com.spt.bas.client.remote.IBasManualClient;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户手册
 */
@Controller
@RequestMapping("/bas/manual")
public class ManualController {

    @Autowired
    private IBasManualClient basManualClient;

    @RequestMapping(value = "")
    public String index(){
        return "manual/manual";
    }

    // 初始化树数据
    @RequestMapping(value = "initTree")
    public void initTree(HttpServletResponse response, DeptSearchVo vo) {
        List<BasManual> manuals = basManualClient.findAllEnable();
        EasyTreeNode nodes = EasyTreeUtil2.getManualTree(manuals);
        RenderUtil.renderJson(nodes.getChildren(), response);
    }


}
