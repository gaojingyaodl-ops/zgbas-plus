package com.spt.bas.server.util;

import com.spt.tools.core.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 融拓对接工具类
 *
 * @Author: gaojy
 * @create 2022/4/8 9:37
 * @version: 1.0
 * @description:
 */
public class RtUtil {
    private static final Logger log = LoggerFactory.getLogger(RtUtil.class);
    private static PoolingHttpClientConnectionManager connManager = null;
    private static CloseableHttpClient httpClient = null;

    public RtUtil() {
    }

    static {
        try {
            // SSLContext
            org.apache.http.conn.ssl.SSLContextBuilder sslContextbuilder = new SSLContextBuilder();
            sslContextbuilder.useTLS();
            // 信任所有
            SSLContext sslContext = sslContextbuilder.loadTrustMaterial(null, (chain, authType) -> true).build();

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext,
                            SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))
                    .build();

            // Create ConnectionManager
            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);

            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
                    .setMaxLineLength(2000).build();

            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(StandardCharsets.UTF_8)
                    .setMessageConstraints(messageConstraints).build();

            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(20);

            // Create httpClient
            httpClient = HttpClients.custom().disableRedirectHandling().setConnectionManager(connManager).build();
        } catch (KeyManagementException e) {
            log.error("KeyManagementException", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String doGet(String url) throws Exception {
        return doGet(url, 12000, null);
    }

    public static String doPostBody(String url, Object param, Map<String, String> headerMap) throws Exception {
        return doPostBody(url, param, headerMap, false);
    }

    public static String doPostBody(String url, Object param, Map<String, String> headerMap, boolean showLog) throws Exception {
        String json = JsonUtil.obj2Json(param);
        return doPostJson(url, json, headerMap, showLog);
    }

    public static String doPostJson(String url, String jsonStr) throws Exception {
        return doPostJson(url, jsonStr, null);
    }

    public static String doPostJson(String url, String jsonStr, Map<String, String> headerMap) throws Exception {
        return doPostJson(url, jsonStr, headerMap, false);
    }

    public static String doGet(String url, int requestTimeout, Map<String, String> headerMap) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(requestTimeout).setConnectTimeout(12000).setConnectionRequestTimeout(12000).build();
        httpGet.setConfig(requestConfig);
        if (headerMap != null) {
            Iterator var5 = headerMap.keySet().iterator();

            while (var5.hasNext()) {
                String key = (String) var5.next();
                httpGet.setHeader(key, headerMap.get(key));
            }
        }

        long responseLength;
        String responseContent;
        String strRep = null;

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                responseLength = entity.getContentLength();
                responseContent = EntityUtils.toString(entity, "UTF8");
                log.debug("内容编码: " + entity.getContentEncoding());
                log.debug("响应状态: " + httpResponse.getStatusLine());
                log.debug("响应长度: " + responseLength);
                log.info("请求地址: " + httpGet.getURI());
                log.info("响应内容: \r\n" + responseContent);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    strRep = responseContent;
                }

                EntityUtils.consume(entity);
                httpGet.abort();
            }
        } finally {
            httpGet.releaseConnection();
        }

        return strRep;
    }

    public static String doPostJson(String url, String jsonStr, Map<String, String> headerMap, boolean showLog) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        if (headerMap != null) {
            Iterator var5 = headerMap.keySet().iterator();

            while (var5.hasNext()) {
                String key = (String) var5.next();
                httpPost.setHeader(key, headerMap.get(key));
            }
        }

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(12000).setConnectTimeout(12000).setConnectionRequestTimeout(12000).setExpectContinueEnabled(false).build();
        httpPost.setConfig(requestConfig);
        if (StringUtils.isNotBlank(jsonStr)) {
            httpPost.setEntity(new StringEntity(jsonStr, "utf-8"));
        }

        long responseLength;
        String responseContent;
        String strRep = null;

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity entityRep = httpResponse.getEntity();
            if (entityRep != null) {
                responseLength = entityRep.getContentLength();
                responseContent = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                if (showLog) {
                    if (log.isDebugEnabled()) {
                        log.debug("内容编码: " + entityRep.getContentEncoding());
                        log.debug("响应状态: " + httpResponse.getStatusLine());
                        log.debug("响应长度: " + responseLength);
                    }

                    log.info("请求地址: " + httpPost.getURI());
                    log.info("响应内容: \r\n" + responseContent);
                }

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    strRep = responseContent;
                } else if (statusCode == 302 || statusCode == 301 || statusCode == 303 || statusCode == 307) {
                    Header locationHeader = httpResponse.getFirstHeader("Location");
                    if (locationHeader != null) {
                        String successUrl = locationHeader.getValue();
                        log.info(successUrl);
                    }
                }

                EntityUtils.consume(entityRep);
                httpPost.abort();
            }
        } finally {
            httpPost.releaseConnection();
        }

        return strRep;
    }

    public static String doMultipartFormDate(String url, Map<String, String> headerMap, Map<String, String> params, Map<String, String> fileMap) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 设置请求头
        if (headerMap != null) {
            for (String key : headerMap.keySet()) {
                httpPost.setHeader(key, headerMap.get(key));
            }
        }

        // 设置参数
        if (params != null && !params.isEmpty()) {
            Set<String> keySet = params.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = params.get(key);
                builder.addTextBody(key, value, ContentType.create("text/plain", Consts.UTF_8));
            }
        }

        // 添加文件
        if ((fileMap != null) && (!fileMap.isEmpty())) {
            for (Map.Entry<String, String> filePath : fileMap.entrySet()) {
                File file = new File(filePath.getValue());
                if (file.exists()) {
                    FileBody bin = new FileBody(file);
                    builder.addPart(filePath.getKey(), bin);
                }
            }
        }
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(12000).setConnectTimeout(12000).
                setConnectionRequestTimeout(12000).setExpectContinueEnabled(false).build();
        httpPost.setConfig(requestConfig);

        long responseLength;
        String responseContent;
        String strRep = null;

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity entityRep = httpResponse.getEntity();
            if (entityRep != null) {
                responseLength = entityRep.getContentLength();
                responseContent = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                log.info("请求地址: " + httpPost.getURI());
                log.info("响应内容: \r\n" + responseContent);

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    strRep = responseContent;
                } else if (statusCode == 302 || statusCode == 301 || statusCode == 303 || statusCode == 307) {
                    Header locationHeader = httpResponse.getFirstHeader("Location");
                    if (locationHeader != null) {
                        String successUrl = locationHeader.getValue();
                        log.info(successUrl);
                    }
                }

                EntityUtils.consume(entityRep);
                httpPost.abort();
            }
        } finally {
            httpPost.releaseConnection();
        }

        return strRep;
    }
}
