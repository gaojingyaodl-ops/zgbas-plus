package com.spt.bas.server.api;

import com.spt.bas.client.vo.DistanceResultVo;
import com.spt.bas.server.service.IBaiduMapApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/7/6 15:08
 */
@RestController
@RequestMapping(value = "/baiduMapApi")
public class BaiduMapApi {

    @Autowired
    private IBaiduMapApiService baiduMapApiService;


    @GetMapping("/getTwoDistance")
    public DistanceResultVo getTwoDistance(@RequestParam(value = "start", required = false) String start,
                                           @RequestParam(value = "end", required = false) String end) {


        return baiduMapApiService.getTwoDistance(start, end);
    }



}
