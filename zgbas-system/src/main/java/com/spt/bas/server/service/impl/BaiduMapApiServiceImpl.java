package com.spt.bas.server.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.spt.bas.client.vo.BaiduMapGeocodeVo;
import com.spt.bas.client.vo.DistanceResultVo;
import com.spt.bas.server.service.IBaiduMapApiService;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.http.util.HTTPUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.MessageFormat;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/7/6 15:18
 */
@Service
public class BaiduMapApiServiceImpl implements IBaiduMapApiService {

    @Value("${baidu.map.ak}")
    private String baiduMapAk;

    /**
     * 驾车路线图api
     */
    private final static String DRIVING_URL = "https://api.map.baidu.com/directionlite/v1/driving?origin={0}&destination={1}&ak={2}";

    /**
     * 获取经纬度api
     */
    private final static String BAI_DU_MAP_GEO_CODE = "https://api.map.baidu.com/geocoding/v3/?address={0}&output=json&ak={1}";



    /**
     * 获取两个地点之间的距离
     *
     * @param start 开始地址
     * @param end   结束地址
     * @return 结果
     */
    @Override
    public DistanceResultVo getTwoDistance(String start, String end) {
        BaiduMapGeocodeVo startGeocode = getGeocoding(start);
        BaiduMapGeocodeVo endGeocode = getGeocoding(end);
        // 纬度,经度
        String startPoint = startGeocode.getResult().getLocation().getLat().setScale(6, RoundingMode.HALF_UP) + "," + startGeocode.getResult().getLocation().getLng().setScale(6, RoundingMode.HALF_UP);
        String endPoint = endGeocode.getResult().getLocation().getLat().setScale(6, RoundingMode.HALF_UP) + "," + endGeocode.getResult().getLocation().getLng().setScale(6, RoundingMode.HALF_UP);
        String getTwoDistanceURL = MessageFormat.format(DRIVING_URL, startPoint, endPoint, baiduMapAk);
        DistanceResultVo resultVo = new DistanceResultVo();
        try {
            String result = HTTPUtility.doGet(getTwoDistanceURL);
            TypeReference<DistanceResultVo> reference = new TypeReference<DistanceResultVo>() {
            };
            resultVo = JsonUtil.json2Object(reference, result);
        } catch (Exception e) {
            resultVo.setStatus(1);
        }
        return resultVo;
    }

    public BaiduMapGeocodeVo getGeocoding(String address) {
        BaiduMapGeocodeVo geocodeVo = new BaiduMapGeocodeVo();
        if (StringUtils.isBlank(address)) {
            geocodeVo.setStatus(2);
            return geocodeVo;
        }
        String geoCodeRequestUrl = MessageFormat.format(BAI_DU_MAP_GEO_CODE, address, baiduMapAk);
        try {
            String result = HTTPUtility.doGet(geoCodeRequestUrl);
            TypeReference<BaiduMapGeocodeVo> reference = new TypeReference<BaiduMapGeocodeVo>() {
            };
            geocodeVo = JsonUtil.json2Object(reference, result);
        } catch (Exception e) {
            geocodeVo.setStatus(1);
        }
        return geocodeVo;
    }
}
