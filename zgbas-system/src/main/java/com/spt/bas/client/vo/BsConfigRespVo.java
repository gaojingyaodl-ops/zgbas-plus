package com.spt.bas.client.vo;

import com.spt.bas.client.entity.BsConfig;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Author: gaojy
 * @create 2021/12/17 10:32
 * @version: 1.0
 * @description:
 */
@Setter
@Getter
public class BsConfigRespVo {
    private Boolean startFlg = false;
    private BsConfig bsConfig;
    private String message;
    private BigDecimal profitRate;
    private Boolean businessRestrictRelieveFlg = false;

}
