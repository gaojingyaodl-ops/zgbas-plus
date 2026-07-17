package com.spt.bas.server.util;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;

/**
 * HTTP协议请求
 * 
 * @author liuxl
 *
 */
public final class HttpUtil {

	private final static Logger log = LoggerFactory.getLogger(HttpUtil.class);

	private static final int REQUEST_TIMEOUT = 20 * 1000; // 设置请求超时10秒钟
	private static final int TIMEOUT = 20 * 1000; // 连接超时时间
	private static final int SO_TIMEOUT = 20 * 1000; // 数据传输超时
	private static final String CHARSET = "GBK";

	private static PoolingHttpClientConnectionManager connManager = null;
	private static CloseableHttpClient httpClient = null;

	static {
		try {
			// SSLContext
			SSLContextBuilder sslContextbuilder = new SSLContextBuilder();
			sslContextbuilder.useTLS();
			SSLContext sslContext = sslContextbuilder.loadTrustMaterial(null, new TrustStrategy() {

				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}

			}).build();

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
					.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Charset.forName("GBK"))
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

	/**
	 * 指定参数名GET方式请求数据
	 * 
	 * @param url
	 * @param paramsMap
	 *            QueryString
	 * @return
	 */
	public static String doGet(String url, Map<String, String> paramsMap,Map<String, String> headerMap)
			throws ClientProtocolException, ConnectTimeoutException, SocketTimeoutException, ApplicationException {
		return doGet(invokeUrl(url, paramsMap),  REQUEST_TIMEOUT,headerMap);
	}
	public static String doGet(String url, Map<String, String> paramsMap)
			throws ClientProtocolException, ConnectTimeoutException, SocketTimeoutException, ApplicationException {
		return doGet(invokeUrl(url, paramsMap),REQUEST_TIMEOUT,null);
	}
	public static String doGet(String url, Map<String, String> paramsMap,int requestTimeout)
			throws ClientProtocolException, ConnectTimeoutException, SocketTimeoutException, ApplicationException {
		return doGet(invokeUrl(url, paramsMap),requestTimeout,null);
	}

	public static String doGet(String url) throws ApplicationException {
		return doGet(url, REQUEST_TIMEOUT,null);
	}
	/**
	 * GET方式请求数据
	 * 
	 * @param url
	 */
	public static String doGet(String url,int requestTimeout,Map<String, String> headerMap) throws ApplicationException {
		HttpGet httpGet = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(requestTimeout).setConnectTimeout(TIMEOUT)
				.setConnectionRequestTimeout(REQUEST_TIMEOUT).build();
		httpGet.setConfig(requestConfig);
		if (headerMap != null) {
			for (String key : headerMap.keySet()) {
				httpGet.setHeader(key, headerMap.get(key));
			}
		}
		long responseLength = 0; // 响应长度
		String responseContent = null; // 响应内容
		String strRep = null;
		try {
			// 执行get请求
			HttpResponse httpResponse = httpClient.execute(httpGet);

			// 头信息
			printHeaders(httpResponse);

			// 获取响应消息实体
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				responseLength = entity.getContentLength();
				responseContent = EntityUtils.toString(entity, CHARSET);// 不能重复调用此方法，IO流已关闭。

				log.info("内容编码: " + entity.getContentEncoding());
				log.info("请求地址: " + httpGet.getURI());
				log.info("响应状态: " + httpResponse.getStatusLine());
				log.info("响应长度: " + responseLength);
				log.info("响应内容: \r\n" + responseContent);

				// 获取HTTP响应的状态码
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					strRep = responseContent; // EntityUtils.toString(httpResponse.getEntity());
				}

				// Consume response content
				EntityUtils.consume(entity);
				// Do not need the rest
				httpGet.abort();
			}
		} catch (ClientProtocolException e) {
			log.error("ClientProtocolException", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} catch (ConnectTimeoutException e) {
			log.error("ConnectTimeoutException", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} catch (SocketTimeoutException e) {
			log.error("SocketTimeoutException", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} catch (Exception e) {
			log.error("Exception", e);
			throw new ApplicationException(e);
		} finally {
			httpGet.releaseConnection();
		}

		return strRep;
	}

	/**
	 * 不指定参数名的方式来POST数据
	 * 
	 * @param url
	 * @param jsonXMLString
	 * @return
	 */
	public static String doPost(String url, String jsonXMLString) throws ApplicationException {
		return doPost(url, null, jsonXMLString, true);
	}

	/**
	 * 指定参数名POST方式请求数据
	 * 
	 * @param url
	 */
	public static String doPost(String url, Map<String, String> paramsMap) throws ApplicationException {
		return doPost(url, paramsMap, null, true);
	}

	/**
	 * 指定参数、是否是XML数据post方式请求数据
	 * 
	 * @param url
	 * @param paramsMap
	 * @param isXml
	 * @return
	 * @throws ApplicationException
	 */
	public static String doPost(String url, Map<String, String> paramsMap, Boolean isXml) throws ApplicationException {
		return doPost(url, paramsMap, null, isXml);
	}
	private static String doPost(String url, Map<String, String> paramsMap, String jsonXMLString, Boolean isXml)
			throws ApplicationException {
		return doPost(url, paramsMap, null, jsonXMLString, isXml);
	}
	public static String doPost(String url, Map<String, String> paramsMap,Map<String, String> headerMap)
			throws ApplicationException {
		return doPost(url, paramsMap, headerMap, null, false);
	}
	private static String doPost(String url, Map<String, String> paramsMap,Map<String, String> headerMap, String jsonXMLString, Boolean isXml)
			throws ApplicationException {
		HttpPost httpPost = new HttpPost(url);
		if (isXml) {
			httpPost.setHeader("Content-type", "text/xml; charset=gbk");// 指定头信息
		}
		if (headerMap != null) {
			for (String key : headerMap.keySet()) {
				httpPost.setHeader(key, headerMap.get(key));
			}
		}
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(TIMEOUT)
				.setConnectionRequestTimeout(REQUEST_TIMEOUT).setExpectContinueEnabled(false).build();

		httpPost.setConfig(requestConfig);// RequestConfig.DEFAULT

		long responseLength = 0; // 响应长度
		String responseContent = null; // 响应内容
		String strRep = null;
		try {
			if (paramsMap != null && jsonXMLString == null) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(getParamsList(paramsMap), CHARSET);
				httpPost.setEntity(entity);
			} else {
				httpPost.setEntity(new StringEntity(jsonXMLString, CHARSET));
			}

			// 执行post请求
			HttpResponse httpResponse = httpClient.execute(httpPost);

			// 头信息
			printHeaders(httpResponse);

			// 获取响应消息实体
			HttpEntity entityRep = httpResponse.getEntity();
			if (entityRep != null) {
				responseLength = entityRep.getContentLength();
				responseContent = EntityUtils.toString(httpResponse.getEntity(), CHARSET);

				// byte[] bytes = EntityUtils.toByteArray(entityRep);

				log.info("内容编码: " + entityRep.getContentEncoding());
				log.info("请求地址: " + httpPost.getURI());
				log.info("响应状态: " + httpResponse.getStatusLine());
				log.info("响应长度: " + responseLength);
				log.info("响应内容: \r\n" + responseContent);

				// 获取HTTP响应的状态码
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					strRep = responseContent; // EntityUtils.toString(httpResponse.getEntity());
				} else if ((statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
						|| (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) || (statusCode == HttpStatus.SC_SEE_OTHER)
						|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
					// 重定向处理，获得跳转的网址
					Header locationHeader = httpResponse.getFirstHeader("Location");
					if (locationHeader != null) {
						String successUrl = locationHeader.getValue();
						log.info(successUrl);
					}
				}

				// Consume response content
				EntityUtils.consume(entityRep);
				// Do not need the rest
				httpPost.abort();
			}
		} catch (ClientProtocolException e) {
			log.error("ClientProtocolException", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} catch (ConnectTimeoutException e) {
			log.error("ConnectTimeoutException", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} catch (SocketTimeoutException e) {
			log.error("SocketTimeoutException", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} catch (Exception e) {
			log.error("Exception", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} finally {
			httpPost.releaseConnection();
			// try
			// {
			// httpClient.close();
			// }
			// catch (IOException e)
			// {
			// e.printStackTrace();
			// }
		}

		return strRep;
	}

	public static String doPostBody(String url, Object param, Map<String, String> headerMap)
			throws ApplicationException {
		String json = JsonUtil.obj2Json(param);
		return doPostJson(url, json, headerMap);

	}

	public static String doPostJson(String url, String jsonStr) throws ApplicationException {
		return doPostJson(url, jsonStr, null);
	}

	public static String doPostJson(String url, String jsonStr, Map<String, String> headerMap)
			throws ApplicationException {

		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json; charset=gbk");
		if (headerMap != null) {
			for (String key : headerMap.keySet()) {
				httpPost.setHeader(key, headerMap.get(key));
			}
		}
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(TIMEOUT)
				.setConnectionRequestTimeout(REQUEST_TIMEOUT).setExpectContinueEnabled(false).build();

		httpPost.setConfig(requestConfig);// RequestConfig.DEFAULT

		httpPost.setEntity(new StringEntity(jsonStr, "gbk"));
		long responseLength = 0; // 响应长度
		String responseContent = null; // 响应内容
		String strRep = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);

			printHeaders(httpResponse);

			// 获取响应消息实体
			HttpEntity entityRep = httpResponse.getEntity();
			if (entityRep != null) {
				responseLength = entityRep.getContentLength();
				responseContent = EntityUtils.toString(httpResponse.getEntity(), "gbk");

				// byte[] bytes = EntityUtils.toByteArray(entityRep);

				log.info("内容编码: " + entityRep.getContentEncoding());
				log.info("请求地址: " + httpPost.getURI());
				log.info("响应状态: " + httpResponse.getStatusLine());
				log.info("响应长度: " + responseLength);
				log.info("响应内容: \r\n" + responseContent);

				// 获取HTTP响应的状态码
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					strRep = responseContent; // EntityUtils.toString(httpResponse.getEntity());
				} else if ((statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
						|| (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) || (statusCode == HttpStatus.SC_SEE_OTHER)
						|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
					// 重定向处理，获得跳转的网址
					Header locationHeader = httpResponse.getFirstHeader("Location");
					if (locationHeader != null) {
						String successUrl = locationHeader.getValue();
						log.info(successUrl);
					}
				}

				// Consume response content
				EntityUtils.consume(entityRep);
				// Do not need the rest
				httpPost.abort();
			}
		} catch (ClientProtocolException e) {
			log.error("ClientProtocolException", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} catch (ConnectTimeoutException e) {
			log.error("ConnectTimeoutException", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} catch (SocketTimeoutException e) {
			log.error("SocketTimeoutException", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} catch (Exception e) {
			log.error("Exception", e);
			e.printStackTrace();
			throw new ApplicationException(e);
		} finally {
			httpPost.releaseConnection();
			// try
			// {
			// httpClient.close();
			// }
			// catch (IOException e)
			// {
			// e.printStackTrace();
			// }
		}

		return strRep;

	}

	// 打印头信息
	private static void printHeaders(HttpResponse httpResponse) {
		log.debug("------------------------------");
		// 头信息
		HeaderIterator it = httpResponse.headerIterator();
		while (it.hasNext()) {
			log.debug(String.valueOf(it.next()));
		}
		log.debug("------------------------------");
	}

	// 读取内容
	protected static String readContent(InputStream in) throws Exception {
		BufferedInputStream buffer = new BufferedInputStream(in);
		StringBuilder builder = new StringBuilder();
		byte[] bytes = new byte[1024];
		int line = 0;
		while ((line = buffer.read(bytes)) != -1) {
			builder.append(new String(bytes, 0, line, CHARSET));
		}

		return builder.toString();
	}

	/**
	 * GET方式传参
	 * 
	 * @param url
	 * @param paramsMap
	 * @return
	 */
	public static String invokeUrl(String url, Map<String, String> paramsMap) {
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		int i = 0;
		if (paramsMap != null && paramsMap.size() > 0) {
			for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
				if (i == 0 && !url.contains("?")) {
					sb.append("?");
				} else {
					sb.append("&");
				}
				sb.append(entry.getKey());
				sb.append("=");
				String value = entry.getValue();
				try {
					sb.append(URLEncoder.encode(value, CHARSET));
				} catch (UnsupportedEncodingException e) {
					log.warn("encode http get params error, value is " + value, e);
					try {
						sb.append(URLEncoder.encode(value, null));
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
				}

				i++;
			}
		}

		return sb.toString();
	}

	/**
	 * 将传入的键/值对参数转换为NameValuePair参数集
	 * 
	 * @param paramsMap
	 *            参数集, 键/值对
	 * @return NameValuePair参数集
	 */
	private static List<NameValuePair> getParamsList(Map<String, String> paramsMap) {
		if (paramsMap == null || paramsMap.size() == 0) {
			return null;
		}

		// 创建参数队列
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> map : paramsMap.entrySet()) {
			params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
		}

		return params;
	}

}
