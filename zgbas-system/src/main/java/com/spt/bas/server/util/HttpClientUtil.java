package com.spt.bas.server.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpClientUtil {

    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final int CONNECTION_TIMEOUT = 3000;
    private static final int REQUEST_TIMEOUT = 12 * 1000;
    private static final int SO_TIMEOUT = 5000;
    private PoolingHttpClientConnectionManager pool = null;
    private int maxConnection = 32;
    private static final String DEFAULT_CHARSET = "UTF-8";
    private int conntimeout = CONNECTION_TIMEOUT;
    private int sotimeout = SO_TIMEOUT;
    private String reqCharset = DEFAULT_CHARSET;
    private String resCharset = DEFAULT_CHARSET;
    private String agentHeader = "Netease/0.1";

    public HttpClientUtil() {

    }

    public HttpClientUtil(int conntimeout, int sotimeout) {
        this.sotimeout = sotimeout;
        this.conntimeout = conntimeout;
    }

    public HttpClientUtil(int maxConnection, int conntimeout, int sotimeout) {
        this(conntimeout, sotimeout);
        this.maxConnection = maxConnection;
    }

    public HttpClientUtil(int maxConnection, String charset, int conntimeout, int sotimeout) {
        this(conntimeout, sotimeout);
        this.maxConnection = maxConnection;
        this.reqCharset = charset;
    }

    public HttpClientUtil(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public HttpClientUtil(int maxConnection, String charset) {
        this.maxConnection = maxConnection;
        this.reqCharset = charset;
    }

    public HttpClientUtil(int maxConnection, String charset, String resCharset) {
        this.maxConnection = maxConnection;
        this.reqCharset = charset;
        this.resCharset = resCharset;
    }

    private HttpClient httpClient;

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                	 httpClient = HttpClients.custom().disableRedirectHandling().setConnectionManager(getPool()).build();
                }
            }
        }
        return httpClient;
    }

    public HttpClient getHttpClient(boolean forcenew) {
        if (forcenew) {
        	 // Create httpClient
            httpClient = HttpClients.custom().disableRedirectHandling().setConnectionManager(pool).build();
            return httpClient;
        } else {
            return getHttpClient();
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        logger.info("Http connection pool will destory...");
        if (pool != null) {
            pool.shutdown();
        }
        logger.info("Http connection pool destroyed!");
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        getPool();
    }

    private PoolingHttpClientConnectionManager getPool() {
        if (pool == null) {
            synchronized (this) {

            	 try {
					// SSLContext
					SSLContextBuilder sslContextbuilder = new SSLContextBuilder();
					sslContextbuilder.useTLS();
					SSLContext sslContext = sslContextbuilder.loadTrustMaterial(null, new TrustStrategy() {

					    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException
					    {
					        return true;
					    }

					}).build();
					 Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					         .register("http", PlainConnectionSocketFactory.INSTANCE)
					         .register("https", new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)).build();

					pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
					SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();

					pool.setMaxTotal(maxConnection);
					pool.setDefaultMaxPerRoute(maxConnection);
					pool.setDefaultSocketConfig(socketConfig);

				} catch (KeyManagementException e) {
					 logger.error("KeyManagementException", e);
				} catch (NoSuchAlgorithmException e) {
					 logger.error("NoSuchAlgorithmException", e);
				} catch (KeyStoreException e) {
					logger.error("KeyStoreException", e);
				}
            }
        }
        return pool;
    }

    /**
     * ֱ�ӷ����ַ�
     *
     * @param url
     * @return
     * @throws IOException
     */
    public String getData(String url, List<NameValuePair> params) throws IOException {
        return fetchData(createGet(url, params, null));
    }

    public String getData(String url) throws IOException {
    	List<NameValuePair> list = Collections.emptyList();
        return fetchData(createGet(url, list, null));
    }

    public String getData(String url, ApplicationType type) throws IOException {
    	List<NameValuePair> list = Collections.emptyList();
        return fetchData(createGet(url, list, type));
    }

    public String getData(String url, List<NameValuePair> params, ApplicationType type) throws IOException {
        return fetchData(createGet(url, params, type));
    }

    public String putData(String url, List<NameValuePair> params) throws IOException {
        return fetchData(createPut(url, params, null));
    }

    public String putData(String url, List<NameValuePair> params, ApplicationType type) throws IOException {
        return fetchData(createPut(url, params, type));
    }

    public String deleteData(String url, List<NameValuePair> params) throws IOException {
        return fetchData(createDelete(url, params, null));
    }

    public String deleteData(String url, List<NameValuePair> params, ApplicationType type) throws IOException {
        return fetchData(createDelete(url, params, type));
    }

    public String postData(String url, final HttpEntity entity, ApplicationType type) throws IOException {
        return fetchData(this.createPost(url, entity, type));
    }

    public String postData(String url, final HttpEntity entity) throws IOException {
        return fetchData(this.createPost(url, entity, null));
    }

    public String postData(String url, final List<NameValuePair> params) throws IOException {
        return fetchData(this.createPost(url, params, null));
    }

    public String postData(String url, final List<NameValuePair> params, ApplicationType type) throws IOException {
        return fetchData(this.createPost(url, params, type));
    }

    public String fetchData(HttpRequestBase request) {
        String result = null;
        if (request == null)
            return result;
        HttpClient client = null;
        long watch = System.nanoTime();
        try {
            client = decHttpClient(request);
            HttpResponse response = client.execute(request);
            HttpEntity rsentity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Charset rescharset = ContentType.getOrDefault(rsentity).getCharset();
                if (rescharset != null && rescharset.name().equals("ISO-8859-1")) {
                    result = EntityUtils.toString(rsentity);
                    if (resCharset == null) {
                        result = new String(result.getBytes(rescharset), DEFAULT_CHARSET);
                    } else {
                        result = new String(result.getBytes(rescharset), resCharset);
                    }
                } else {
                    if (resCharset != null) {
                        result = EntityUtils.toString(rsentity, resCharset);
                    } else {
                        result = EntityUtils.toString(rsentity);
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(" fetch request {} ", result);
                }
            } else {
                logger.error("fetch request return error status:{} code:{}", request.getURI(), response.getStatusLine().getStatusCode());
            }
            // EntityUtils.consume(rsentity);
        } catch (ClientProtocolException e) {
            logger.error("fetch request ClientProtocolException {} msg {}", request.getURI(), e.getMessage());
        } catch (ParseException e) {
            logger.error("fetch request ParseException {} msg {}", request.getURI(), e.getMessage());
        } catch (IOException e) {
            logger.error("fetch request IOException {} msg {}", request.getURI(), e.getMessage());
        } finally {
            request.releaseConnection();
            if (logger.isDebugEnabled())
                logger.debug("fetch url:{} consume:{} ", request.getURI(), (System.nanoTime() - watch) / 1000);
        }
        return result;
    }

    private HttpClient decHttpClient(HttpRequestBase request) {
        HttpClient client;
        client = getHttpClient();
        request.addHeader("User-Agent", agentHeader);
        return client;
    }

    public static void addHeader(HttpRequestBase request, String key, String val) {
        request.addHeader(key, val);
    }

    /**
     * ����post����
     *
     *            ·��
     * @return ����
     * @throws UnsupportedEncodingException
     */
    public HttpPost createPost(String url, final List<NameValuePair> params, ApplicationType type) throws UnsupportedEncodingException {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, reqCharset);
        return createPost(url, entity, type);
    }

    public HttpPost createPost(String url, final List<NameValuePair> params) throws UnsupportedEncodingException {
        return createPost(url, params, null);
    }

    public HttpPost createPost(String url, HttpEntity entity) {
        return createPost(url, entity, null);
    }

    public HttpPost createPost(String url, HttpEntity entity, ApplicationType accept) {
        HttpPost method = new HttpPost(url);
        if (null != accept) {
            method.addHeader("accept", accept.val());
        }
        method.setEntity(entity);
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(sotimeout)
                .setConnectTimeout(conntimeout)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT).build();
        method.setConfig(config);
        return method;
    }

    public HttpGet createGet(String url, final List<NameValuePair> params) throws IOException {
        return createGet(url, params, null);
    }

    public HttpGet createGet(String url, final List<NameValuePair> params, ApplicationType accept) throws IOException {
        HttpGet method = new HttpGet(urlEncode(url, params));
        if (null != accept) {
            method.addHeader("accept", accept.val());
        }
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(sotimeout)
                .setConnectTimeout(conntimeout)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT).build();
        method.setConfig(config);
        return method;
    }

    private String urlEncode(String url, final List<NameValuePair> params) {
        if (params == null)
            return url;
        String param = URLEncodedUtils.format(params, reqCharset);
        if (url.indexOf("?") == -1) {
            url += "?" + param;
        } else {
            url += param;
        }
        return url;
    }

    public HttpPut createPut(String url, final List<NameValuePair> params, ApplicationType accept) throws IOException {
        HttpPut method = new HttpPut(url);
        if (params != null) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, reqCharset);
            method.setEntity(entity);
        }
        if (null != accept) {
            method.addHeader("accept", accept.val());
        }
        return method;
    }

    public HttpPut createPut(String url, final List<NameValuePair> params) throws IOException {
        return createPut(url, params, null);
    }

    public HttpDelete createDelete(String url, final List<NameValuePair> params) throws IOException {
        return createDelete(url, params, null);
    }

    public HttpDelete createDelete(String url, final List<NameValuePair> params, ApplicationType accept) throws IOException {
        HttpDelete method = new HttpDelete(urlEncode(url, params));
        if (null != accept) {
            method.addHeader("accept", accept.val());
        }
        return method;
    }

    public String getAgentHeader() {
        return agentHeader;
    }

    public void setAgentHeader(String agentHeader) {
        this.agentHeader = agentHeader;
    }

    public enum ApplicationType {

		JSON("application/json"), XML("application/xml"), TEXT("text/xml"), FORM("application/x-www-form-urlencoded");

		private String type;

		private ApplicationType(String type) {
			this.type = type;
		}

		public String val() {
			return type;
		}

	}

}
