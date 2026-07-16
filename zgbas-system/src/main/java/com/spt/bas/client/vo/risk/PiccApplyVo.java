package com.spt.bas.client.vo.risk;

import lombok.Data;

import java.util.List;

@Data
public class PiccApplyVo {

    /**
     * 合同编号List
     */
    private List<String> contractNoList;
    private String contractNoListStr;
    private String canPiccSendStateDate;
}
