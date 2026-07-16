package com.spt.bas.client.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 百度地图API-地理编码Vo
 *
 * @Author: gaojy
 * @create 2022/2/11 10:48
 * @version: 1.0
 * @description:
 */
@Data
public class BaiduMapGeocodeVo {
    /**
     * 返回结果状态值
     * 0-正常
     * 1-服务器内部错误
     * 2-请求参数非法
     * 3-权限校验失败
     * 4-配额校验失败
     * 5-ak不存在或者非法
     * 101-服务禁用
     * 102-不通过白名单或者安全码不对
     * 2xx-无权限
     * 3xx-配额错误
     */
    private Integer status;

    /**
     * 返回结果
     */
    private Result result;


    @Data
    public static class Location {
        /**
         * 经度值
         */
        private BigDecimal lng;

        /**
         * 纬度值
         */
        private BigDecimal lat;
    }

    @Data
    public static class Result {

        /**
         * 经纬度坐标
         */
        private Location location;

        /**
         * 位置的附加信息，是否精确查找。1为精确查找，即准确打点；0为不精确，即模糊打点
         */
        private Integer precise;

        /**
         * 描述打点绝对精度（即坐标点的误差范围）。
         * confidence=100，解析误差绝对精度小于20m；
         * confidence≥90，解析误差绝对精度小于50m；
         * confidence≥80，解析误差绝对精度小于100m；
         * confidence≥75，解析误差绝对精度小于200m；
         * confidence≥70，解析误差绝对精度小于300m；
         * confidence≥60，解析误差绝对精度小于500m；
         * confidence≥50，解析误差绝对精度小于1000m；
         * confidence≥40，解析误差绝对精度小于2000m；
         * confidence≥30，解析误差绝对精度小于5000m；
         * confidence≥25，解析误差绝对精度小于8000m；
         * confidence≥20，解析误差绝对精度小于10000m；
         */
        private Integer confidence;

        /**
         * 描述地址理解程度。分值范围0-100，分值越大，服务对地址理解程度越高（建议以该字段作为解析结果判断标准）；
         * 当comprehension值为以下值时，对应的准确率如下：
         * comprehension=100，解析误差100m内概率为91%，误差500m内概率为96%；
         * comprehension≥90，解析误差100m内概率为89%，误差500m内概率为96%；
         * comprehension≥80，解析误差100m内概率为88%，误差500m内概率为95%；
         * comprehension≥70，解析误差100m内概率为84%，误差500m内概率为93%；
         * comprehension≥60，解析误差100m内概率为81%，误差500m内概率为91%；
         * comprehension≥50，解析误差100m内概率为79%，误差500m内概率为90%；
         * //解析误差：地理编码服务解析地址得到的坐标位置，与地址对应的真实位置间的距离。
         */
        private Integer comprehension;

        /**
         * 能精确理解的地址类型
         */
        private String level;
    }
}
