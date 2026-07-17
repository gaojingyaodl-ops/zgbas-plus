package com.spt.bas.server.service.impl;


import com.spt.bas.server.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 采销中心服务实现
 */
@Component("basTradeService")
@Transactional(readOnly = true)
@Slf4j
public class BasTradeServiceImpl implements IBasTradeService {


    /**
     * 测试
     */
    @Override
    public void test() {
        log.info("测试：接口调用成功");
    }
}

