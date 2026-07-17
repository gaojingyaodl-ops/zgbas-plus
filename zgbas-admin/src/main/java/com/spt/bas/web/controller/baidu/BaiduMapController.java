package com.spt.bas.web.controller.baidu;

import com.spt.bas.client.remote.BaiduMapClient;
import com.spt.bas.client.vo.DistanceResultVo;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * 百度地图相关接口
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/7/4 10:39
 */

@Controller
@RequestMapping(value = "/baiduMap")
public class BaiduMapController {

    @Autowired
    private BaiduMapClient baiduMapClient;


    @RequestMapping("/index")
    public String calDistance(Model model) {
        return "baidu/calDistance";
    }

    @GetMapping("/getTwoDistance")
    public void getTwoDistance(@RequestParam(value = "start", required = false) String start,
                               @RequestParam(value = "end", required = false) String end,
                               HttpServletResponse response) {
        DistanceResultVo twoDistance = baiduMapClient.getTwoDistance(start, end);
        RenderUtil.renderJson(twoDistance, response);
    }

}
