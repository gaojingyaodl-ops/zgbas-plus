package com.spt.tools.http.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * IP地址转换工具.
 * </p>
 * 
 * <p>
 * IP地址的十点分位法，和INT数据描述的转换.
 * </p>
 */
public class IPUtil {
	private static final Logger log = LoggerFactory.getLogger(IPUtil.class);
	/** ip地址前缀正则表达式 */
	public static final String IP_PREFIX_ERG = "((0[0-9]|1[0-9]\\d{1,2})|(2[0-4][0-9])|(2[0-5][0-5])|(\\d{1,2}))(\\.((0[0-9]|1[0-9]\\d{1,2})|(2[0-4][0-9])|(2[0-5][0-5])|(\\d{1,2}))){0,3}";

	/** ip地址正则表达式. */
	public static final String IP_REG = "((0[0-9]|1[0-9]\\d{1,2})|(2[0-5][0-5])|(2[0-4][0-9])|(\\d{1,2}))\\.((0[0-9]|1[0-9]\\d{1,2})|(2[0-5][0-5])|(2[0-4][0-9])|(\\d{1,2}))\\.((0[0-9]|1[0-9]\\d{1,2})|(2[0-4][0-9])|(2[0-5][0-5])|(\\d{1,2}))\\.((0[0-9]|1[0-9]\\d{1,2})|(2[0-4][0-9])|(2[0-5][0-5])|(\\d{1,2}))";

	/**
	 * 十点分位法转int描述.
	 * 
	 * @param ip
	 * @return
	 */
	public static long str2long(String ip) {
		boolean match = ip.matches(IP_REG);
		if (!match) {
			log.warn("IP[" + ip + "}地址格式错误");
			return 0;
			// throw new IllegalArgumentException("IP地址格式错误");
		}
		String[] ips = ip.split("\\.");
		return (Long.parseLong(ips[0]) << 24) | (Long.parseLong(ips[1]) << 16) | (Long.parseLong(ips[2]) << 8)
				| (Long.parseLong(ips[3]));

	}

	/**
	 * int描述转十点分位法 int2str:
	 * 
	 * @param ip
	 * @return
	 */
	public static String long2str(long ip) {
		return ((0xFF000000 & ip) >>> 24) + "." + ((0x00FF0000 & ip) >>> 16) + "." + ((0x0000FF00 & ip) >>> 8) + "."
				+ ((0x000000FF & ip));
	}

	/**
	 * 使用十点分位法，根据IP前缀获取最小IP地址。
	 * 例如传入"10.1"则返回“10.1.0.0”对应的IP地址,传入“10.1.2”则返回“10.1.2.0”对应的IP地址.
	 * 
	 * @param ipPrefix
	 *            IP前缀，十点分位法的前N段IP信息.
	 * @return
	 */
	public static long minIp(String ipPrefix) {
		if (!ipPrefix.matches(IP_PREFIX_ERG)) {
			log.error("IP地址前缀[" + ipPrefix + "]格式错误");
			return 0;
			// throw new IllegalArgumentException("IP地址前缀格式错误");
		}
		String[] ips = ipPrefix.split("\\.");
		for (int i = ips.length; i < 4; i++) {
			ipPrefix = ipPrefix + ".0";
		}
		return str2long(ipPrefix);
	}

	/**
	 * 使用十点分位法，根据IP前缀获取最大IP地址。
	 * 例如传入"10.1"则返回“10.1.255.255”对应的IP地址,传入“10.1.2”则返回“10.1.2.255”对应的IP地址.
	 * 
	 * @param ipPrefix
	 *            IP前缀，十点分位法的前N段IP信息.
	 * @return
	 */
	public static long maxIp(String ipPrefix) {
		if (!ipPrefix.matches(IP_PREFIX_ERG)) {
			log.error("IP地址前缀[" + ipPrefix + "]格式错误");
			return 0;
			// throw new IllegalArgumentException("IP地址前缀格式错误");
		}
		String[] ips = ipPrefix.split("\\.");
		for (int i = ips.length; i < 4; i++) {
			ipPrefix = ipPrefix + ".255";
		}
		return str2long(ipPrefix);
	}

	/**
	 * 从HTTP请求中获取IP地址 如果请求存在代理或转发，则根据HTTP消息头，获取对应的的真实地址. getIpAddr:
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/** 获得本机IP */
	public static String getIpAddress() {
		String ip = "";
		try {

			ip = InetAddress.getLocalHost().getHostAddress();

			InetAddress address = null;
			for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
					.hasMoreElements();) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
					continue;
				}
				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				if (addresses.hasMoreElements() && networkInterface.getInterfaceAddresses().size() == 1) {
					address = addresses.nextElement();
					break;
				}
			}
			if (address != null) {
				ip = address.getHostAddress();
			}
		} catch (Exception e) {
			log.error("获取本机ip地址错误");
		}
		return ip;

	}

	/**
	 * main:
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		System.out.println(long2str(244972560L));
	}

}
