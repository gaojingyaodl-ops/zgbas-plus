package com.spt.bas.server.api;

import com.spt.bas.client.vo.DistanceResultVo;
import com.spt.bas.server.service.IBaiduMapApiService;
import com.spt.bas.server.util.MidstreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/7/6 15:08
 */
@RestController
@RequestMapping(value = "/midstream")
public class MidstreamApi {

    @Resource
    private MidstreamUtil midstreamUtil;


    @GetMapping("/generateRespStr")
    String generateRespStr(@RequestParam(value = "reqStr", required = false) String reqStr,
                                     @RequestParam(value = "oldValue", required = false) String oldValue){
        return midstreamUtil.generateRespStr(reqStr,oldValue);
    }



}
