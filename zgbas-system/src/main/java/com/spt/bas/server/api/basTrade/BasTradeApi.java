package com.spt.bas.server.api.basTrade;

import com.spt.bas.server.service.IBasTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 采销中心服务接口
 */
@RestController
@RequestMapping(value = "bas/trade")
public class BasTradeApi {
    
    @Autowired
    private IBasTradeService basTradeService;

    /**
     * 测试
     */
    @PostMapping("/test")
    public void test(){
        basTradeService.test();
    }
   
}
