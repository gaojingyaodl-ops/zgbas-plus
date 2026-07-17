package com.spt.bas.report.client.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author MoonLight
 * @Date 2024/8/26 17:00
 * @Version 1.0
 */
@Data
@Getter
@Setter
public class RptExportSearchVo {
    private List<Long> contractIdList;

    private List<Long> approveIdList;
}
