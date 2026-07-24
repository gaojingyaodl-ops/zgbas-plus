package com.spt.bas.purchase.wx.server.service.impl;

import cfca.etl.common.client.exception.ClientException;
import cfca.etl.common.communicate.junyu.request.JVerifyRequest;
import cfca.etl.common.communicate.junyu.vo.wrapper.JVerifyWrapper;
import cfca.etl.common.util.Base64ImgUtil;
import cfca.etl.uaclient.UAClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spt.bas.client.constant.ApiEnum;
import com.spt.bas.client.entity.ApiRequestHis;
import com.spt.bas.client.entity.SyncData;
import com.spt.bas.client.remote.IApiRequestHisClient;
import com.spt.bas.purchase.wx.client.payload.AuthFaceRecognition;
import com.spt.bas.purchase.wx.server.config.JinXinConfig;
import com.spt.bas.purchase.wx.server.util.CommonUtil;
import com.spt.bas.purchase.wx.server.util.RsaUtil;
import com.spt.bas.purchase.wx.server.util.SignUtil;
import com.spt.bas.purchase.wx.server.vo.JinXinAuthFaceVo;
import com.spt.tools.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 金信服务调用API
 *
 * @Author: gaojy
 * @create 2022/1/14 17:18
 * @version: 1.0
 * @description:
 */
@Slf4j
@Component
@EnableConfigurationProperties(JinXinConfig.class)
public class JinXinApi extends CommonUtil {
    @Autowired
    private JinXinConfig jinXinConfig;
    @Autowired
    private IApiRequestHisClient apiRequestHisClient;

    /**
     * 获取活体认证人脸识别 NEW
     */
    public JinXinAuthFaceVo authenticationNew(AuthFaceRecognition authFaceRecognition) {
        JinXinAuthFaceVo jinXinAuthFaceVo = new JinXinAuthFaceVo();
        SyncData syncData = new SyncData();
        String url = jinXinConfig.getHost() + jinXinConfig.getLivingUrl();
        syncData.setUrl(url);
        syncData.setDataType("authFaceRecognition");
        String orderId = UUID.randomUUID().toString().replaceAll("-", "");
        syncData.setOrderId(orderId);
        try {
            // 创建UAClient后执行initSSL方法初始化SSL环境，之后多次调用可使用同一UAClient对象
            UAClient client = new UAClient(url, jinXinConfig.getConnectTimeout(), jinXinConfig.getReadTimeout());
            client.initSSL(jinXinConfig.getKeyStorePath(), jinXinConfig.getKeyStorePassword(), 
                    jinXinConfig.getTrustStorePath(), jinXinConfig.getTrustStorePassword());
            // 如需指定ssl协议、算法、证书库类型，使用如下方式
            // client.initSSL(keyStorePath, keyStorePassword, trustStorePath, trustStorePassword, "SSL", "IbmX509", "IbmX509", "JKS", "JKS");

            JVerifyRequest jVerifyRequest = new JVerifyRequest();
            jVerifyRequest.setTxCode("08035");
            jVerifyRequest.setStrName(authFaceRecognition.getName());
            jVerifyRequest.setStrId(authFaceRecognition.getIdNumber());


            saveBase64VideoToFile(jinXinConfig.getTmpFilePath()+"/"+orderId+".mp4",authFaceRecognition.getVideoStr());
            jVerifyRequest.setStrVideo(Base64ImgUtil.GetImageStr(jinXinConfig.getTmpFilePath()+"/"+orderId+".mp4"));
            // 使用Base64类进行解码
//            byte[] decodedBytes = Base64.getDecoder().decode(authFaceRecognition.getVideoStr());
//
//            // 使用BASE64Encoder类进行编码
//            String encodedString = new sun.misc.BASE64Encoder().encode(decodedBytes);
//
//            jVerifyRequest.setStrVideo(encodedString);
            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.JINXIN_AUTHENTICATION.getApiCode(),
                    ApiEnum.JINXIN_AUTHENTICATION.getApiName(), url,JsonUtil.obj2Json(jVerifyRequest));
            apiRequestHisClient.addJINXINRequestHis(apiRequestHis);

            
            JVerifyWrapper jVerifyWrapper = (JVerifyWrapper) client.process(jVerifyRequest);
            syncData.setRequest(JsonUtil.obj2Json(jVerifyRequest));
            syncData.setResponse(JsonUtil.obj2Json(jVerifyWrapper));

            //需要判断的结果: code* = 0 且 retCode = 00000000 表示通过
            if(StringUtils.equals("00000000",jVerifyWrapper.getResultCode())) {
                jinXinAuthFaceVo.setPlatformCode(jVerifyWrapper.getStatus());
                jinXinAuthFaceVo.setPlatformDesc(jVerifyWrapper.getStatus()+"："+jVerifyWrapper.getStatusMessage());
            } else {
                jinXinAuthFaceVo.setPlatformCode(jVerifyWrapper.getStatus());
                jinXinAuthFaceVo.setPlatformDesc("接口调用失败");
            }
            log.info("authentication return: resultCode {}, resultMessage {}, status {},statusMessage {}, errorClassification {}",
                    jVerifyWrapper.getResultCode(),jVerifyWrapper.getResultMessage(),jVerifyWrapper.getStatus(),jVerifyWrapper.getStatusMessage(),jVerifyWrapper.getErrorClassification());

        } catch (ClientException e) {
            log.error("金信-获取活体认证人脸识别 请求失败", e);
        } catch (IOException e) {
            log.error("金信-获取活体认证保存临时文件失败", e);
        }

        jinXinAuthFaceVo.setSyncData(syncData);
        return jinXinAuthFaceVo;
    }

    public void saveBase64VideoToFile(String filePath,String videoStr) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(videoStr);

        Path outputPath = Paths.get(filePath);
        try (FileOutputStream fos = new FileOutputStream(new File(outputPath.toString()))) {
            fos.write(decodedBytes);
            fos.flush();
        } catch (IOException e) {
            throw new IOException("Failed to write video file", e);
        }

        System.out.println("Video saved successfully to " + filePath);
    }
    
    /**
     * 获取活体认证人脸识别
     *
     * @return
     */
    public JinXinAuthFaceVo authentication(AuthFaceRecognition authFaceRecognition) {
        JinXinAuthFaceVo jinXinAuthFaceVo = new JinXinAuthFaceVo();
        SyncData syncData = new SyncData();
        String url = jinXinConfig.getHost() + jinXinConfig.getLivingUrl();
        syncData.setUrl(url);
        // 1.设置活体认证接口默认参数
        Map<String, Object> paramMap = setDefaultParam(authFaceRecognition, syncData);
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 2.生成签名
            String sign = SignUtil.getSign(paramMap, jinXinConfig.getMerchKey());
            log.info("authentication 生成签名 sign:{}", sign);
            paramMap.put("sign", sign);

            // 3.请求参数格式处理
            byte[] request = mapper.writeValueAsBytes(paramMap);

            // 保存接口调用记录
            ApiRequestHis apiRequestHis = new ApiRequestHis(ApiEnum.JINXIN_AUTHENTICATION.getApiCode(),
                    ApiEnum.JINXIN_AUTHENTICATION.getApiName(), url, JsonUtil.obj2Json(request));
            apiRequestHisClient.addJINXINRequestHis(apiRequestHis);
            // 4.发起请求
            String resultStr = post(url, request);
            log.info("authentication resultStr:{}", JsonUtil.obj2Json(resultStr));

            // 5.设置数据请求流水表保存数据
            syncData.setRequest(mapper.writeValueAsString(paramMap));
            syncData.setResponse(resultStr);
            // 6.响应参数处理
            TypeReference<JinXinAuthFaceVo> reference = new TypeReference<JinXinAuthFaceVo>() {
            };
            jinXinAuthFaceVo = JsonUtil.json2Object(reference, resultStr);
            Map<String, Object> respMap = mapper.readValue(resultStr, Map.class);
            sign = SignUtil.getSign(respMap, jinXinConfig.getMerchKey());
            if (StringUtils.equals(sign, jinXinAuthFaceVo.getSign())) {
                String data = jinXinAuthFaceVo.getData();
                if (StringUtils.isBlank(data)) {
                    log.error("response data is null !");
                } else {
                    try {
                        // 置信度结果RSA解密
                        String realConfidence = RsaUtil.decrypt(data, jinXinConfig.getMerchRsaPrivateKey());
                        if (StringUtils.isNotBlank(realConfidence)) {
                            jinXinAuthFaceVo.setData(realConfidence);
                            log.info("realConfidence:{}", realConfidence);
                        }
                    } catch (Exception e) {
                        log.error("解密失败!", e);
                    }
                }
            } else {
                log.error("验签失败 localhostSign:{},responseSign:{}", sign, jinXinAuthFaceVo.getSign());
            }
        } catch (Exception e) {
            log.error("金信-获取活体认证人脸识别 请求失败", e);
        }
        jinXinAuthFaceVo.setSyncData(syncData);
        log.info("authentication return: dsorderid {}, orderid {}, merchno {}, platformCode {}, platformDesc {}", jinXinAuthFaceVo.getDsorderid(), jinXinAuthFaceVo.getOrderid(), jinXinAuthFaceVo.getMerchno(),
                jinXinAuthFaceVo.getPlatformCode(), jinXinAuthFaceVo.getPlatformDesc());
        return jinXinAuthFaceVo;
    }

    // 获取活体认证人脸识别默认参数
    private Map<String, Object> setDefaultParam(AuthFaceRecognition authFaceRecognition, SyncData syncData) {
        // 设置默认参数
        Map<String, Object> paramMap = new HashMap<>();
        String orderId = UUID.randomUUID().toString().replaceAll("-", "");
        paramMap.put("transcode", "311");
        paramMap.put("idtype", "01");
        paramMap.put("merchno", jinXinConfig.getMerchNo());
        //商户流水号
        paramMap.put("ordersn", orderId);
        //商户订单号
        paramMap.put("dsorderid", orderId);
        //真实场景 见接口文档枚举
        paramMap.put("scenecode", "02");
        //版本号 固定0100
        paramMap.put("version", "0100");
        paramMap.put("videoData", authFaceRecognition.getVideoStr());
        paramMap.put("username", authFaceRecognition.getName());
        paramMap.put("idcard", authFaceRecognition.getIdNumber());

        syncData.setDataType("authFaceRecognition");
        syncData.setOrderId(orderId);
        return paramMap;
    }
}
