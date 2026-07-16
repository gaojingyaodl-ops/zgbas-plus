
package com.spt.bas.client.entity;

/**
 * @author 邵安伟
 * 2021-02-20
 * 盖章申请
 * 将盖章xxx对象和PmProcessSearchVo通用工具实体类没有的字段存放此处 混合对象，最后set入Vo的字段
 * 继承SealUsage类获得原有字段
 */
public class SealMixture extends SealUsage {

    private Long bizId;
    private String deptAbbr;

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getDeptAbbr() {
        return deptAbbr;
    }

    public void setDeptAbbr(String deptAbbr) {
        this.deptAbbr = deptAbbr;
    }
}