//package com.spt.bas.server.util;
//
//import com.alibaba.fastjson.JSONObject;
//import com.spt.bas.client.entity.SyncData;
//import com.spt.bas.server.service.ISyncDataService;
//import com.spt.tools.core.json.JsonUtil;
//import com.spt.tools.core.util.SpringContextHolder;
//import com.spt.tools.http.util.HTTPUtility;
//
//import java.util.Map;
//
//public class HttpPostAxqUtil {
//
//    private static ISyncDataService syncDataService = SpringContextHolder.getBean(ISyncDataService.class);
//
//    public HttpPostAxqUtil() {
//    }
//
//    public static void doPostBody(String url, Object param, Map<String, String> headerMap, String dataType, String serviceName) {
//
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // insert snyc_data表数据
//                    SyncData syncData = new SyncData();
//                    syncData.setUrl(url);
//                    syncData.setData(JsonUtil.obj2Json(param));
//                    syncData.setDataCount(1L);
//                    syncData.setDataType(dataType);
//                    syncData.setServiceName(serviceName);
//                    syncData = syncDataService.save(syncData);
//                    String uuidJson = HTTPUtility.doPostBody(url, param, headerMap);
//                    JSONObject jsonObj = JSONObject.parseObject(uuidJson);
//                    String code = jsonObj.getString("code");
//                    String message = jsonObj.getString("message");
//                    // 将返回的数据转为Object对象
//                    if ("200".equals(code) || "success".equals(message)) {
//                        // 若成功，更新snyc_data表数据
//                        syncData.setStatus("1");
//                        syncData = syncDataService.save(syncData);
////						if(StringUtils.isNotBlank(serviceName)){
////							HttpPostFactoryConfig.getBack(serviceName).backOper(syncData);
////						}
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        t.start();
//    }
//}
