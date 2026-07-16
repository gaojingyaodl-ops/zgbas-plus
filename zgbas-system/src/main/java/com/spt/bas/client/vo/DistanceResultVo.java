package com.spt.bas.client.vo;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/7/7 11:28
 */

public class DistanceResultVo {

    /**
     * 状态码
     * 0：成功
     * 1：服务内部错误
     * 2：参数无效
     * 7：无返回结果
     */
    private Integer status;

    /**
     * 状态码对应的信息
     */
    private String message;

    private Result result;

    static class Result {
        /**
         * 起点经纬度
         */
        private Location origin;

        /**
         * 终点经纬度
         */
        private Location destination;

        /**
         * 返回方案集
         */
        private List<Routes> routes;

        static class Routes {

            /**
             * 方案距离
             */
            private double distance;

            /**
             * 耗时秒
             */
            private double duration;

            public double getDistance() {
                return distance;
            }

            public void setDistance(double distance) {
                this.distance = distance;
            }

            public double getDuration() {
                return duration;
            }

            public void setDuration(double duration) {
                this.duration = duration;
            }
        }

        static class Location {
            /**
             * 纬度值
             */
            private double lng;

            /**
             * 经度值
             */
            private double lat;

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }
        }

        public List<Routes> getRoutes() {
            return routes;
        }

        public void setRoutes(List<Routes> routes) {
            this.routes = routes;
        }

        public Location getOrigin() {
            return origin;
        }

        public void setOrigin(Location origin) {
            this.origin = origin;
        }

        public Location getDestination() {
            return destination;
        }

        public void setDestination(Location destination) {
            this.destination = destination;
        }

    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
