/**
 * 
 */
package com.spt.bas.web.util;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.entity.BsLog;
import com.spt.bas.client.remote.IBsLogClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.core.annotation.LogField;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonCode2Name;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.http.util.IPUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * 日志记录
 * 
 * @author jian
 * 
 */
public class LogUtil {
	public static final String LOG_OPT_ADD = "0";
	public static final String LOG_OPT_DEL = "1";
	public static final String LOG_OPT_MOD = "2";

	public static final String LOG_OPT_LOGIN = "3";
	public static final String LOG_OPT_LOGOUT = "4";
	
	private static IBsLogClient logClient = SpringContextHolder.getBean(IBsLogClient.class);
	private static ThreadPoolTaskExecutor taskExecutor = SpringContextHolder.getBean(ThreadPoolTaskExecutor.class);

	/** 异步保存日志 */
	private static void infoAsyn(final HttpServletRequest request, final String operation, final String targetName,
			final String content) {

		Runnable runnable = () -> infoSyn(request, operation, targetName, content);
		taskExecutor.execute(runnable);

	}

	/** 同步保存日志 */
	private static void infoSyn(final HttpServletRequest request, final String operation, final String targetName,
			final String content) {

		Long userId = ShiroUtil.getCurrentUserId();
		String userName = ShiroUtil.getCurrentUserName();
		BsLog log = new BsLog();
		log.setIpAddre(IPUtil.getIpAddr(request));
		log.setRemortPort(request.getRemotePort());
		log.setOperation(operation);
		log.setOperatorId(userId);
		log.setOperatorName(userName);
		log.setTargetName(targetName);
		log.setRemark(content);
		logClient.save(log);
	}

	/**
	 *  登录
	 * @param request
	 */
	public static void loginSuccess(HttpServletRequest request) {
		infoSyn(request, LOG_OPT_LOGIN, "用户", "登录成功");
	}

	/**
	 *  登录失败
	 * @param request
	 */
	public static void loginFail(final HttpServletRequest request, final UsernamePasswordToken upToken) {
		 
		 Runnable runnable = () -> {
			 if (StringUtils.isNoneBlank(upToken.getUsername()) && upToken.getPassword()!=null){
				 BsLog log = new BsLog();
				 log.setIpAddre(upToken.getHost());
				 log.setRemortPort(request.getRemotePort());
				 log.setOperation(LOG_OPT_LOGIN);
				 log.setTargetName("用户");
				 StringBuffer remark = new StringBuffer("[登录失败]");
				 remark.append("用户名:").append(upToken.getUsername());
				 remark.append("密码:").append(upToken.getPassword());

				 log.setRemark(remark.toString());
				 logClient.save(log);
			 }
		 };
			taskExecutor.execute(runnable);
	}

	/**
	 *  退出
	 * @param request
	 */
	public static void logout(HttpServletRequest request) {
		infoSyn(request, LOG_OPT_LOGOUT, "用户", "退出");
	}
	
	
	/**
	 * 日志：删除 
	 * @param request
	 * @param entity
	 */
	public static void del(HttpServletRequest request, Object entity) {
		LogEntityName entityName = entity.getClass().getAnnotation(LogEntityName.class);
		if (entityName!=null){
			infoAsyn(request, LOG_OPT_DEL, entityName.value(), toString(entity));
		}
	}

	/**
	 * 日志：新增、修改 
	 * @param request
	 * @param entity
	 * @param id
	 */
	public static void saveOrUpdate(HttpServletRequest request, Object old,Object entity, Long id) {
		LogEntityName entityName = entity.getClass().getAnnotation(LogEntityName.class);
		if (entityName != null) {
			if (id == null || id == 0) {
				// 新增
				infoAsyn(request, LOG_OPT_ADD, entityName.value(), toString(entity));
			} else {
				// 修改
//				commonDao.detach(entity);
				String msg = compare(entity, old);
				if (msg.length() > 0) {
					infoAsyn(request, LOG_OPT_MOD, entityName.value(), msg);
				}
			}
		}
	}

	/**
	 * 日志：修改密碼
	 * @param request
	 * @param msg
	 */
	public static void changePwd(HttpServletRequest request, String msg) {
		infoAsyn(request, LOG_OPT_MOD, "修改密码", msg);
	}

	private static String toString(Object n) {
		Field[] fields = n.getClass().getDeclaredFields();
		Class<?> superClass=n.getClass().getSuperclass();
		if (superClass!=null){
			Field[] fields_sup = superClass.getDeclaredFields();
			fields = ArrayUtils.addAll(fields_sup, fields);
		}
		LogEntityName entityName = n.getClass().getAnnotation(LogEntityName.class);
		StringBuilder msg = new StringBuilder();
		for (Field field : fields) {
			LogField fieldName = field.getAnnotation(LogField.class);
			if (fieldName == null) {
				continue;
			}
			try {
				Object valNew;
				if (StringUtils.isNotBlank(fieldName.cascadeField())) {
					Object objNew = PropertyUtils.getProperty(n, field.getName());
					valNew = PropertyUtils.getProperty(objNew, fieldName.cascadeField());
				} else {
					valNew = PropertyUtils.getProperty(n, field.getName());
				}
				String strNew;
				if (valNew instanceof Date){
					strNew = DateOperator.formatDate((Date)valNew);
				}else{
					strNew = StringUtils.trimToEmpty(valNew == null ? "" : valNew.toString());
				}

				JsonCode2Name code2Name = field.getAnnotation(JsonCode2Name.class);
				if (code2Name != null) {
					strNew = DictUtil.getValue(code2Name.dictType(), strNew);
				}
				strNew = StringUtils.trimToEmpty(strNew);
				if (StringUtils.isNotBlank(strNew)) {
					if (msg.length() > 0) {
						msg.append(",");
					} else {
						msg.append(entityName.value()).append("[");
					}
					msg.append(fieldName.value()).append(":").append(strNew);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		if (msg.length() > 0) {
			msg.append("]");
		}
		return msg.toString();
	}

	private static String compare(Object n, Object o) {
		Field[] fields = n.getClass().getDeclaredFields();
		Class<?> superClass=n.getClass().getSuperclass();
		if (superClass!=null){
			Field[] fields_sup = superClass.getDeclaredFields();
			fields = ArrayUtils.addAll(fields_sup, fields);
		}
		
		LogEntityName entityName = n.getClass().getAnnotation(LogEntityName.class);
		StringBuilder msg = new StringBuilder();
		Long id = null;
		for (Field field : fields) {
			try {
				LogField fieldName = field.getAnnotation(LogField.class);
//				Id idAnno = field.getAnnotation(Id.class);
				if ("id".equals(field.getName())) {
					id = (Long) PropertyUtils.getProperty(n, field.getName());
				}
				if (fieldName == null) {
					continue;
				}

				Object valNew;
				Object valOld;
				if (StringUtils.isNotBlank(fieldName.cascadeField())) {
					Object objNew = PropertyUtils.getProperty(n, field.getName());
					valNew = PropertyUtils.getProperty(objNew, fieldName.cascadeField());
					Object objOld = PropertyUtils.getProperty(o, field.getName());
					valOld = PropertyUtils.getProperty(objOld, fieldName.cascadeField());
				} else {
					valNew = PropertyUtils.getProperty(n, field.getName());
					valOld = PropertyUtils.getProperty(o, field.getName());
				}
				String strNew,strOld;
				if (valNew instanceof Date){
					strNew = DateOperator.formatDate((Date)valNew);
					strOld = DateOperator.formatDate((Date)valOld);
				}else{
					strNew = StringUtils.trimToEmpty(valNew == null ? "" : valNew.toString());
					strOld = StringUtils.trimToEmpty(valOld == null ? "" : valOld.toString());
				}
				JsonCode2Name code2Name = field.getAnnotation(JsonCode2Name.class);
				if (code2Name != null) {
					strNew = DictUtil.getValue(code2Name.dictType(), strNew);
					strOld = DictUtil.getValue(code2Name.dictType(), strOld);
				}
				strNew = StringUtils.trimToEmpty(strNew);
				strOld = StringUtils.trimToEmpty(strOld);
				if (!StringUtils.equals(strNew, strOld)) {
					// map.put(fieldName.value(), valNew>" + valOld);
					if (msg.length() > 0) {
						msg.append(",");
					} else {
						msg.append(entityName.value());
						if (id != null) {
							msg.append(" ").append(id).append(" ");
						}
						msg.append("[");
					}
					msg.append(fieldName.value()).append(":").append(strOld).append("->").append(strNew);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		if (msg.length() > 0) {
			msg.append("]");
		}
		return msg.toString();
	}
}
