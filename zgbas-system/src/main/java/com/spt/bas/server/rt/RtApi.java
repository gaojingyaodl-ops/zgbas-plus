package com.spt.bas.server.rt;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.fasterxml.jackson.core.type.TypeReference;
import com.spt.bas.client.constant.ApiEnum;
import com.spt.bas.client.constant.RtConstants;
import com.spt.bas.client.entity.ApiRequestHis;
import com.spt.bas.client.remote.IApiRequestHisClient;
import com.spt.bas.client.vo.rtVo.*;
import com.spt.bas.server.config.RtConfig;
import com.spt.bas.server.util.FileUtil;
import com.spt.bas.server.util.RtUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.util.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 融拓对接API
 *
 * @Author: gaojy
 * @create 2022/4/8 10:09
 * @version: 1.0
 * @description:
 */
@Slf4j
@Component
@EnableConfigurationProperties(RtConfig.class)
public class RtApi extends RtUtil {
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN_BEARER = "Bearer ";
    private static final Long TOKEN_TIME_OUT = 900000L;
    private static final TimedCache<String, String> TOKEN_CACHE = CacheUtil.newTimedCache(100);
    static {
        // 每15分钟检查一次过期
        TOKEN_CACHE.schedulePrune(TOKEN_TIME_OUT);
    }
    @Value("${file.server.url}")
    private String fileServerUrl;
    @Autowired
    private RtConfig rtConfig;
    @Autowired
    private IApiRequestHisClient apiRequestHisClient;

    /**
     * 获取融拓访问Token
     *
     * @return
     */
    public String getRtToken() {
        if (TOKEN_CACHE.containsKey(AUTHORIZATION)){
            return TOKEN_CACHE.get(AUTHORIZATION,false);
        }
        String url = MessageFormat.format(RtConstants.RT_GET_TOKEN, rtConfig.getClientId(), rtConfig.getClientSecret());
        String requestUrl = rtConfig.getUrl() + url;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_GET_TOKEN.getApiCode(),
                    ApiEnum.RT_GET_TOKEN.getApiName(), url, null);
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            String result = RtUtil.doGet(requestUrl);
            TypeReference<RtTokenResp> reference = new TypeReference<RtTokenResp>() {
            };
            RtTokenResp rtTokenResp = JsonUtil.json2Object(reference, result);
            if (Objects.nonNull(rtTokenResp)) {
                TOKEN_CACHE.remove(AUTHORIZATION);
                TOKEN_CACHE.put(AUTHORIZATION, rtTokenResp.getAccess_token(), TOKEN_TIME_OUT);
                return rtTokenResp.getAccess_token();
            }
        } catch (Exception e) {
            log.error("getRtToken error", e);
        }
        return null;
    }

    /**
     * 推送企业信息至融拓
     *
     * @param rtCompanyReq
     * @return
     */
    public RtResp<RtCompanyResp> pushCompanyToRt(RtCompanyReq rtCompanyReq) {
        RtResp<RtCompanyResp> result = new RtResp<>();
        String reqUrl = rtConfig.getUrl() + RtConstants.RT_PUSH_COMPANY;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_PUSH_COMPANY.getApiCode(),
                    ApiEnum.RT_PUSH_COMPANY.getApiName(), reqUrl, JsonUtil.obj2Json(rtCompanyReq));
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            rtCompanyReq.setSceneId(rtConfig.getSceneId());
            rtCompanyReq.setSceneKey(rtConfig.getSceneKey());
            String jsonResult = RtUtil.doPostBody(reqUrl, rtCompanyReq, getHeadMap());
            TypeReference<RtResp<RtCompanyResp>> reference = new TypeReference<RtResp<RtCompanyResp>>() {
            };
            result = JsonUtil.json2Object(reference, jsonResult);
        } catch (Exception e) {
            result.setFail(201, "推送企业信息至融拓,系统内部异常!");
        }
        return result;
    }

    /**
     * 推送确认收货信息至融拓
     *
     * @param rtConfirmReq
     * @return
     */
    public RtResp<T> pushConfirmToRt(RtConfirmReq rtConfirmReq) {
        RtResp<T> result = new RtResp<>();
        Map<String, String> param = MapUtils.tansBean2Map(rtConfirmReq);
        String reqUrl = rtConfig.getUrl() + RtConstants.RT_PUSH_CONFIRM_RECEIVE;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_PUSH_CONFIRM_RECEIVE.getApiCode(),
                    ApiEnum.RT_PUSH_CONFIRM_RECEIVE.getApiName(), reqUrl, JsonUtil.obj2Json(rtConfirmReq));
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            String jsonResult = RtUtil.doMultipartFormDate(reqUrl, getHeadMap(), param, getFileMap(rtConfirmReq.getFileId()));
            TypeReference<RtResp<T>> reference = new TypeReference<RtResp<T>>() {
            };
            result = JsonUtil.json2Object(reference, jsonResult);
        } catch (Exception e) {
            result.setFail(201, "推送确认收货信息至融拓,系统内部异常!");
        }
        return result;
    }

    /**
     * 推送赊销预算信息至融拓
     *
     * @param rtContractReq
     * @return
     */
    public RtResp<T> pushContractToRt(RtContractReq rtContractReq) {
        RtResp<T> result = new RtResp<>();
        String reqUrl = rtConfig.getUrl() + RtConstants.RT_PUSH_CREDIT_DETAIL;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_PUSH_CREDIT_DETAIL.getApiCode(),
                    ApiEnum.RT_PUSH_CREDIT_DETAIL.getApiName(), reqUrl, JsonUtil.obj2Json(rtContractReq));
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            String jsonResult = RtUtil.doPostBody(reqUrl, rtContractReq, getHeadMap());
            TypeReference<RtResp<T>> reference = new TypeReference<RtResp<T>>() {
            };
            log.info(JsonUtil.obj2Json(rtContractReq));
            result = JsonUtil.json2Object(reference, jsonResult);
        } catch (Exception e) {
            result.setFail(201, "推送赊销预算信息至融拓,系统内部异常!");
        }
        return result;
    }

    /**
     * 推送付款信息至融拓
     *
     * @param rtPayReq
     * @return
     */
    public RtResp<T> pushPayToRt(RtPayReq rtPayReq) {
        RtResp<T> result = new RtResp<>();
        Map<String, String> param = MapUtils.tansBean2Map(rtPayReq);
        String reqUrl = rtConfig.getUrl() + RtConstants.RT_PUSH_PAY_DETAIL;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_PUSH_PAY_DETAIL.getApiCode(),
                    ApiEnum.RT_PUSH_PAY_DETAIL.getApiName(), reqUrl, JsonUtil.obj2Json(rtPayReq));
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            String jsonResult = RtUtil.doMultipartFormDate(reqUrl, getHeadMap(), param, getFileMap(rtPayReq.getFileId()));
            TypeReference<RtResp<T>> reference = new TypeReference<RtResp<T>>() {
            };
            result = JsonUtil.json2Object(reference, jsonResult);
        } catch (Exception e) {
            result.setFail(201, "推送付款信息至融拓,系统内部异常!");
        }
        return result;
    }

    /**
     * 推送收款信息至融拓
     *
     * @param rtReceiveReq
     * @return
     */
    public RtResp<T> pushReceiveToRt(RtReceiveReq rtReceiveReq) {
        RtResp<T> result = new RtResp<>();
        Map<String, String> param = MapUtils.tansBean2Map(rtReceiveReq);
        String reqUrl = rtConfig.getUrl() + RtConstants.RT_PUSH_RECEIVE_DETAIL;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_PUSH_RECEIVE_DETAIL.getApiCode(),
                    ApiEnum.RT_PUSH_RECEIVE_DETAIL.getApiName(), reqUrl, JsonUtil.obj2Json(rtReceiveReq));
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            String jsonResult = RtUtil.doMultipartFormDate(reqUrl, getHeadMap(), param, getFileMap(rtReceiveReq.getFileId()));
            TypeReference<RtResp<T>> reference = new TypeReference<RtResp<T>>() {
            };
            result = JsonUtil.json2Object(reference, jsonResult);
        } catch (Exception e) {
            result.setFail(201, "推送收款信息至融拓,系统内部异常!");
        }
        return result;
    }

    /**
     * 推送入库信息至融拓
     *
     * @param rtDeliveryInReq
     * @return
     */
    public RtResp<T> pushDeliveryInToRt(RtDeliveryInReq rtDeliveryInReq) {
        RtResp<T> result = new RtResp<>();
        Map<String, String> param = MapUtils.tansBean2Map(rtDeliveryInReq);
        String reqUrl = rtConfig.getUrl() + RtConstants.RT_PUSH_DELIVERY_IN_DETAIL;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_PUSH_DELIVERY_IN_DETAIL.getApiCode(),
                    ApiEnum.RT_PUSH_DELIVERY_IN_DETAIL.getApiName(), reqUrl, JsonUtil.obj2Json(rtDeliveryInReq));
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            String jsonResult = RtUtil.doMultipartFormDate(reqUrl, getHeadMap(), param, getFileMap(rtDeliveryInReq.getFileId()));
            TypeReference<RtResp<T>> reference = new TypeReference<RtResp<T>>() {
            };
            result = JsonUtil.json2Object(reference, jsonResult);
        } catch (Exception e) {
            result.setFail(201, "推送入库信息至融拓,系统内部异常!");
        }
        return result;
    }

    /**
     * 推送出库信息至融拓
     *
     * @param rtDeliveryOutReq
     * @return
     */
    public RtResp<T> pushDeliveryOutToRt(RtDeliveryOutReq rtDeliveryOutReq) {
        RtResp<T> result = new RtResp<>();
        Map<String, String> param = MapUtils.tansBean2Map(rtDeliveryOutReq);
        String reqUrl = rtConfig.getUrl() + RtConstants.RT_PUSH_DELIVERY_OUT_DETAIL;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_PUSH_DELIVERY_OUT_DETAIL.getApiCode(),
                    ApiEnum.RT_PUSH_DELIVERY_OUT_DETAIL.getApiName(), reqUrl, JsonUtil.obj2Json(rtDeliveryOutReq));
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            String jsonResult = RtUtil.doMultipartFormDate(reqUrl, getHeadMap(), param, getFileMap(rtDeliveryOutReq.getFileId()));
            TypeReference<RtResp<T>> reference = new TypeReference<RtResp<T>>() {
            };
            result = JsonUtil.json2Object(reference, jsonResult);
        } catch (Exception e) {
            result.setFail(201, "推送出库信息至融拓,系统内部异常!");
        }
        return result;
    }

    /**
     * 推送收票信息至融拓
     *
     * @param rtInvoiceReceiveReq
     * @return
     */
    public RtResp<T> pushInvoiceReceiveToRt(RtInvoiceReceiveReq rtInvoiceReceiveReq) {
        RtResp<T> result = new RtResp<>();
        Map<String, String> param = MapUtils.tansBean2Map(rtInvoiceReceiveReq);
        String reqUrl = rtConfig.getUrl() + RtConstants.RT_PUSH_INVOICE_RECEIVE_DETAIL;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_PUSH_INVOICE_RECEIVE_DETAIL.getApiCode(),
                    ApiEnum.RT_PUSH_INVOICE_RECEIVE_DETAIL.getApiName(), reqUrl, JsonUtil.obj2Json(rtInvoiceReceiveReq));
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            String jsonResult = RtUtil.doMultipartFormDate(reqUrl, getHeadMap(), param, getFileMap(rtInvoiceReceiveReq.getFileId()));
            TypeReference<RtResp<T>> reference = new TypeReference<RtResp<T>>() {
            };
            result = JsonUtil.json2Object(reference, jsonResult);
        } catch (Exception e) {
            result.setFail(201, "推送收票信息至融拓,系统内部异常!");
        }
        return result;
    }

    /**
     * 推送开票信息至融拓
     *
     * @param rtInvoiceReq
     * @return
     */
    public RtResp<T> pushInvoiceToRt(RtInvoiceReq rtInvoiceReq) {
        RtResp<T> result = new RtResp<>();
        Map<String, String> param = MapUtils.tansBean2Map(rtInvoiceReq);
        String reqUrl = rtConfig.getUrl() + RtConstants.RT_PUSH_INVOICE_DETAIL;
        try {
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.RT_PUSH_INVOICE_DETAIL.getApiCode(),
                    ApiEnum.RT_PUSH_INVOICE_DETAIL.getApiName(), reqUrl, JsonUtil.obj2Json(rtInvoiceReq));
            apiRequestHisClient.addRtRequestHis(apiRequestHis);

            String jsonResult = RtUtil.doMultipartFormDate(reqUrl, getHeadMap(), param, getFileMap(rtInvoiceReq.getFileId()));
            TypeReference<RtResp<T>> reference = new TypeReference<RtResp<T>>() {
            };
            result = JsonUtil.json2Object(reference, jsonResult);
        } catch (Exception e) {
            result.setFail(201, "推送开票信息至融拓,系统内部异常!");
        }
        return result;
    }

    public Map<String, String> getHeadMap() {
        Map<String, String> headMap = new HashMap<>();
        String rtToken = getRtToken();
        headMap.put(AUTHORIZATION, TOKEN_BEARER + rtToken);
        return headMap;
    }

    public Map<String, String> getFileMap(String fileId) {
        Map<String, String> fileMap = new HashMap<>();
        if (StringUtils.isNotBlank(fileId)) {
            fileId = fileId.replaceAll(",", "");
            File file = FileUtil.getFile(fileId, fileServerUrl);
            if (file.exists()) {
                fileMap.put("file", file.getPath());
            }
        }
        return fileMap;
    }
}
