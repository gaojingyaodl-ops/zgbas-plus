package com.spt.bas.report.client.vo;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/17 09:59
 */

public class RptBusinessStatisticVo {
    /**
     * 今日代采
     */
    private RptIndexCommonVo dayBuy;
    /**
     * 今日赊销
     */
    private RptIndexCommonVo daySell;
    /**
     * 今日自营
     */
    private RptIndexCommonVo dayMySell;

    /**
     * 本月代采
     */
    private RptIndexCommonVo monthBuy;
    /**
     * 本月赊销
     */
    private RptIndexCommonVo monthSell;
    /**
     * 本月自营
     */
    private RptIndexCommonVo monthMySell;

    private RptBusinessStatisticVo(Builder builder) {
        setDayBuy(builder.dayBuy);
        setDaySell(builder.daySell);
        setDayMySell(builder.dayMySell);
        setMonthBuy(builder.monthBuy);
        setMonthSell(builder.monthSell);
        setMonthMySell(builder.monthMySell);
    }

    public static final class Builder {
        private RptIndexCommonVo dayBuy;
        private RptIndexCommonVo daySell;
        private RptIndexCommonVo dayMySell;
        private RptIndexCommonVo monthBuy;
        private RptIndexCommonVo monthSell;
        private RptIndexCommonVo monthMySell;

        public Builder() {
        }

        public Builder dayBuy(RptIndexCommonVo val) {
            dayBuy = val;
            return this;
        }

        public Builder daySell(RptIndexCommonVo val) {
            daySell = val;
            return this;
        }

        public Builder dayMySell(RptIndexCommonVo val) {
            dayMySell = val;
            return this;
        }

        public Builder monthBuy(RptIndexCommonVo val) {
            monthBuy = val;
            return this;
        }

        public Builder monthSell(RptIndexCommonVo val) {
            monthSell = val;
            return this;
        }

        public Builder monthMySell(RptIndexCommonVo val) {
            monthMySell = val;
            return this;
        }

        public RptBusinessStatisticVo build() {
            return new RptBusinessStatisticVo(this);
        }
    }


    public RptIndexCommonVo getDayBuy() {
        return dayBuy;
    }

    public void setDayBuy(RptIndexCommonVo dayBuy) {
        this.dayBuy = dayBuy;
    }

    public RptIndexCommonVo getDaySell() {
        return daySell;
    }

    public void setDaySell(RptIndexCommonVo daySell) {
        this.daySell = daySell;
    }

    public RptIndexCommonVo getDayMySell() {
        return dayMySell;
    }

    public void setDayMySell(RptIndexCommonVo dayMySell) {
        this.dayMySell = dayMySell;
    }

    public RptIndexCommonVo getMonthBuy() {
        return monthBuy;
    }

    public void setMonthBuy(RptIndexCommonVo monthBuy) {
        this.monthBuy = monthBuy;
    }

    public RptIndexCommonVo getMonthSell() {
        return monthSell;
    }

    public void setMonthSell(RptIndexCommonVo monthSell) {
        this.monthSell = monthSell;
    }

    public RptIndexCommonVo getMonthMySell() {
        return monthMySell;
    }

    public void setMonthMySell(RptIndexCommonVo monthMySell) {
        this.monthMySell = monthMySell;
    }

    @Override
    public String toString() {
        return "BusinessStatisticVo{" +
                "dayBuy=" + dayBuy +
                ", daySell=" + daySell +
                ", dayMySell=" + dayMySell +
                ", monthBuy=" + monthBuy +
                ", monthSell=" + monthSell +
                ", monthMySell=" + monthMySell +
                '}';
    }



}
