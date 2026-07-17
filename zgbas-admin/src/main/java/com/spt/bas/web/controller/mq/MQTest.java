package com.spt.bas.web.controller.mq;

import com.spt.bas.client.remote.MQClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/2/28 11:07
 */

@RestController
@RequestMapping("/mq/test")
public class MQTest {

    @Autowired
    private MQClient mqClient;

    @GetMapping("/mqtest")
    public void mqTest() {
        mqClient.test();
    }
}
