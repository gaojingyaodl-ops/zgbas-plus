package com.spt.bas.report.client.payload;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-01 14:40
 */
public class MatchUser extends PageSearchVo {
    private Long matchUserId;

    private String timeFrom;

    private String timeTo;

    private List<Long> matchUserIds;

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public List<Long> getMatchUserIds() {
        return matchUserIds;
    }

    public void setMatchUserIds(List<Long> matchUserIds) {
        this.matchUserIds = matchUserIds;
    }
}
