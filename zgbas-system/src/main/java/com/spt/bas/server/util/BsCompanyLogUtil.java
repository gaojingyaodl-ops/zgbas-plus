package com.spt.bas.server.util;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.remote.IBsCompanyOphisClient;
import com.spt.bas.client.vo.BsCompanyOphisVo;
import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.core.annotation.LogField;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonCode2Name;
import com.spt.tools.core.util.SpringContextHolder;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Date;

/**
 *  客户管理日志
 */
public class BsCompanyLogUtil {
    public static final String LOG_OPT_ADD = "0";
    public static final String LOG_OPT_DEL = "1";
    public static final String LOG_OPT_MOD = "2";
    
    
    
    public static final String LOG_FILED_TYPE_USER = "user";
    public static final String LOG_FILED_TYPE_DICT = "dict";
    public static final String LOG_APP_DICT_ADMIN = "admin";
    public static final String LOG_APP_DICT_BAS = "bas";


    private static IAuthOpenFacade authOpenFacade = SpringContextHolder.getBean(IAuthOpenFacade.class);
    private static IBsCompanyOphisClient bsCompanyOphisClient = SpringContextHolder.getBean(IBsCompanyOphisClient.class);
    private static ThreadPoolTaskExecutor taskExecutor = SpringContextHolder.getBean(ThreadPoolTaskExecutor.class);

    /** 异步保存日志 */
    private static void infoAsyn(final HttpServletRequest request, final String operation, final String targetName,
                                 final String content,final Long userId,final String userName,final Long companyId,
                                 final String status,final String optionType) {

        Runnable runnable = () -> infoSyn(request, operation, targetName, content, userId, userName, companyId, status, optionType);
        taskExecutor.execute(runnable);

    }

    /** 同步保存日志 */
    private static void infoSyn(final HttpServletRequest request, final String operation, final String targetName,
                                final String content,final Long userId,final String userName,final Long companyId,
                                final String status,final String optionType) {

        BsCompanyOphisVo opHis = new BsCompanyOphisVo();
        opHis.setCompanyId(companyId);
        opHis.setCreateUserId(userId);
        opHis.setCreateUserName(userName);
        opHis.setStatus(status);
        opHis.setRemark(content);
        opHis.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        opHis.setOptionType(optionType);
        opHis.setOperation(operation);
        opHis.setTargetName(targetName);
        bsCompanyOphisClient.addCompanyHis(opHis);
    }


    /**
     * 日志：删除 
     * @param request
     * @param entity
     */
    public static void del(HttpServletRequest request, Object entity, Long userId, String userName, Long companyId, String status, String optionType) {
        LogEntityName entityName = entity.getClass().getAnnotation(LogEntityName.class);
        if (entityName!=null){
            infoAsyn(request, LOG_OPT_DEL, entityName.value(), toString(entity), userId, userName, companyId, status, optionType);
        }
    }

    /**
     * 日志：新增、修改 
     * @param request
     * @param entity
     * @param id
     */
    public static void saveOrUpdate(HttpServletRequest request, Object old,Object entity, Long id, Long userId,
                                    String userName, Long companyId, String status, String optionType) {
        LogEntityName entityName = entity.getClass().getAnnotation(LogEntityName.class);
        if (entityName != null) {
            if (id == null || id == 0) {
                // 新增
                infoAsyn(request, LOG_OPT_ADD, entityName.value(), toString(entity), userId, userName, companyId, status, optionType);
            } else {
                // 修改
//				commonDao.detach(entity);
                String msg = compare(entity, old);
                if (msg.length() > 0) {
                    infoAsyn(request, LOG_OPT_MOD, entityName.value(), msg, userId, userName, companyId, status, optionType);
                }
            }
        }
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
                    String fileNameValue = fieldName.value();
                    if(StringUtils.isNotBlank(fileNameValue)){
                        if(fileNameValue.contains(LOG_FILED_TYPE_DICT)){
                            // 字典
                            String[] split = fileNameValue.split(",");
                            fileNameValue = split[0];
                            String category = split[2];
                            String app = split[3];
                            if(StringUtils.equals(LOG_APP_DICT_ADMIN,app)){
                                if(StringUtils.isNotBlank(strNew)){
                                    strNew = DictUtil.getValue(category, strNew);
                                }
                            } else if(StringUtils.equals(LOG_APP_DICT_BAS,app)){
                                if(StringUtils.isNotBlank(strNew)){
                                    strNew = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,category, strNew);
                                }
                            }

                        } else if(fileNameValue.contains(LOG_FILED_TYPE_USER)){
                            // 用户
                            fileNameValue = fileNameValue.split(",")[0];
                            if(StringUtils.isNotBlank(strNew)){
                                SysUserSdk newUser = authOpenFacade.findUserById(Long.valueOf(strNew));
                                if(newUser != null) {
                                    strNew = newUser.getNickName();
                                }
                            }
                        }
                    }
                    msg.append(fileNameValue).append(":").append(strNew);
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
                    if (msg.length() > 0) {
                        msg.append(",");
                    } else {
                        msg.append("[").append(entityName.value()).append("]");
                        msg.append("[");
                    }
                    String fileNameValue = fieldName.value();
                    if(StringUtils.isNotBlank(fileNameValue)){
                        if(fileNameValue.contains(LOG_FILED_TYPE_DICT)){
                            // 字典
                            String[] split = fileNameValue.split(",");
                            fileNameValue = split[0];
                            String category = split[2];
                            String app = split[3];
                            if(StringUtils.equals(LOG_APP_DICT_ADMIN,app)){
                                if(StringUtils.isNotBlank(strOld)){
                                    strOld = DictUtil.getValue(category, strOld);
                                }
                                if(StringUtils.isNotBlank(strNew)){
                                    strNew = DictUtil.getValue(category, strNew);
                                }
                            } else if(StringUtils.equals(LOG_APP_DICT_BAS,app)){
                                if(StringUtils.isNotBlank(strOld)){
                                    strOld = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,category, strOld);
                                }
                                if(StringUtils.isNotBlank(strNew)){
                                    strNew = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,category, strNew);
                                }
                            }
                            
                        } else if(fileNameValue.contains(LOG_FILED_TYPE_USER)){
                            // 用户
                            fileNameValue = fileNameValue.split(",")[0];
                            if(StringUtils.isNotBlank(strOld)){
                                SysUserSdk oldUser = authOpenFacade.findUserById(Long.valueOf(strOld));
                                if(oldUser != null) {
                                    strOld = oldUser.getNickName();
                                }
                            }
                            if(StringUtils.isNotBlank(strNew)){
                                SysUserSdk newUser = authOpenFacade.findUserById(Long.valueOf(strNew));
                                if(newUser != null) {
                                    strNew = newUser.getNickName();
                                }
                            }
                        }
                    }
                    msg.append(fileNameValue).append(":").append(strOld).append("->").append(strNew);
                }
            } catch (Exception e) {
                // e.printStackTrace();
                e.printStackTrace();
            }
        }
        if (msg.length() > 0) {
            msg.append("]");
        }
        return msg.toString();
    }
}
