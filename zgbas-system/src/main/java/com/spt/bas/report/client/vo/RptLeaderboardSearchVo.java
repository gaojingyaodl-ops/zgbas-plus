package com.spt.bas.report.client.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RptLeaderboardSearchVo {

    private Boolean deptFlg;
    private List<Long> deptIdList;

    private Date startDate;
    private Date endDate;
}
