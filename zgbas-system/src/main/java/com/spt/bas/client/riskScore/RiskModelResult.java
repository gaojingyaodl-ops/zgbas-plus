package com.spt.bas.client.riskScore;

import lombok.Data;

@Data
public class RiskModelResult {
    private RiskModelScoreType scoreType;
    private String subTypeName;
    private int score;
    private String scoreItem;
    private String remark;
}
