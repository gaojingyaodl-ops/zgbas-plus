package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.CtrContract;

/**
 * @Author MoonLight
 * @Date 2024/2/26 14:27
 * @Version 1.0
 */
public class ExtraBankTextVo {
    private DcContractText textVo;

    private ApplyMatch applyMatch;

    private CtrContract contract;

    private ApplyCtrDCSX entity;

    private String textKind;

    public DcContractText getTextVo() {
        return textVo;
    }

    public void setTextVo(DcContractText textVo) {
        this.textVo = textVo;
    }

    public ApplyMatch getApplyMatch() {
        return applyMatch;
    }

    public void setApplyMatch(ApplyMatch applyMatch) {
        this.applyMatch = applyMatch;
    }

    public ApplyCtrDCSX getEntity() {
        return entity;
    }

    public void setEntity(ApplyCtrDCSX entity) {
        this.entity = entity;
    }

    public String getTextKind() {
        return textKind;
    }

    public void setTextKind(String textKind) {
        this.textKind = textKind;
    }

    public CtrContract getContract() {
        return contract;
    }

    public void setContract(CtrContract contract) {
        this.contract = contract;
    }

    public ExtraBankTextVo() {
    }

    public ExtraBankTextVo(DcContractText textVo, ApplyMatch applyMatch, String textKind) {
        this.textVo = textVo;
        this.applyMatch = applyMatch;
        this.textKind = textKind;
    }

    public ExtraBankTextVo(DcContractText textVo, ApplyCtrDCSX entity, String textKind) {
        this.textVo = textVo;
        this.entity = entity;
        this.textKind = textKind;
    }

    public ExtraBankTextVo(DcContractText textVo, ApplyMatch applyMatch, ApplyCtrDCSX entity, String textKind) {
        this.textVo = textVo;
        this.applyMatch = applyMatch;
        this.entity = entity;
        this.textKind = textKind;
    }

    public ExtraBankTextVo(DcContractText textVo, ApplyMatch applyMatch, CtrContract contract, ApplyCtrDCSX entity, String textKind) {
        this.textVo = textVo;
        this.applyMatch = applyMatch;
        this.contract = contract;
        this.entity = entity;
        this.textKind = textKind;
    }
}
