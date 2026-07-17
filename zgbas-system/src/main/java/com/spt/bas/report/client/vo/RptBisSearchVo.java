package com.spt.bas.report.client.vo;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/11/8 09:56
 */

public class RptBisSearchVo {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 类别：
     *
     * CL-差旅费用：管理费用(差旅) + 经营费用(差旅)
     * GG-公关费用：管理费用(业务招待费) + 经营费用(业务招待费)
     */
    private String type;

    /**
     * 年月
     */
    private String ym;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getYm() {
        return ym;
    }

    public void setYm(String ym) {
        this.ym = ym;
    }

    @Override
    public String toString() {
        return "BisSearchVo{" +
                "userId=" + userId +
                ", type='" + type + '\'' +
                ", ym='" + ym + '\'' +
                '}';
    }
}
