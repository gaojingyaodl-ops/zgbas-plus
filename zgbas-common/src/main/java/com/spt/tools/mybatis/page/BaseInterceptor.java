/**
 * 
 */
package com.spt.tools.mybatis.page;

import java.io.Serializable;
import java.util.Properties;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.reflect.Reflections;
import com.spt.tools.mybatis.dialect.Dialect;
import com.spt.tools.mybatis.dialect.MySQLDialect;
import com.spt.tools.mybatis.dialect.OracleDialect;

/**
 * Mybatis分页拦截器基类
 */
public abstract class BaseInterceptor implements Interceptor, Serializable {
	
	private static final long serialVersionUID = 1L;

    protected static final String PAGE = "page";
    
    protected static final String DELEGATE = "delegate";

    protected static final String MAPPED_STATEMENT = "mappedStatement";

    protected Log log = LogFactory.getLog(this.getClass());

    protected Dialect dialectDefault;

//    /**
//     * 拦截的ID，在mapper中的id，可以匹配正则
//     */
//    protected String _SQL_PATTERN = "";

    /**
     * 对参数进行转换和检查
     * @param parameterObject 参数对象
     * @param page            分页对象
     * @return 分页对象
     * @throws NoSuchFieldException 无法找到参数
     */
	protected static PageSearchVo convertParameter(Object parameterObject) {
    	try{
            if (parameterObject instanceof PageSearchVo) {
                return (PageSearchVo) parameterObject;
            } else {
                return (PageSearchVo)Reflections.getFieldValue(parameterObject, PAGE);
            }
    	}catch (Exception e) {
			return null;
		}
    }

    /**
     * 设置属性，支持自定义方言类和制定数据库的方式
     * <code>dialectClass</code>,自定义方言类。可以不配置这项
     * <ode>dbms</ode> 数据库类型，插件支持的数据库
     * <code>sqlPattern</code> 需要拦截的SQL ID
     * @param p 属性
     */
    protected void initProperties(Properties p) {
    	Dialect dialect = null;
        String dbType =p.getProperty("jdbc.type");
        if("oracle".equals(dbType)){
        	dialect = new OracleDialect();
        }else{
        	dialect = new MySQLDialect();
        }
//        if (dialect == null) {
//            throw new RuntimeException("mybatis dialect error.");
//        }
        dialectDefault = dialect;
//        _SQL_PATTERN = p.getProperty("sqlPattern");
//        _SQL_PATTERN = Global.getConfig("mybatis.pagePattern");
//        if (StringUtils.isEmpty(_SQL_PATTERN)) {
//            throw new RuntimeException("sqlPattern property is not found!");
//        }
    }
}
