package com.spt.bas.report.client.vo;

import lombok.Data;

import java.util.List;

/**
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2025/5/21 10:43
 */
@Data
public class RptCtrContractSettlementDateSearchVo {
    private List<Long> buyContractIdList;

    private List<Long> sellContractIdList;
}
