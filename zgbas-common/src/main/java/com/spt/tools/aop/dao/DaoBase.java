package com.spt.tools.aop.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;


/**
 * DAO基类，所有DAO从此类派生
 * 
 * @author wangyilin
 *
 * @param <T>	实体对象类
 */
public abstract class DaoBase extends JdbcDaoSupport 
{
	protected final Logger log = LoggerFactory.getLogger(getClass());
//	@Autowired
//	protected LobHandler lobHandler;
	
	@Autowired
	public void autoDataSource(DataSource dataSource) {
		setDataSource(dataSource);
	}
	
	/**
	 * 获取用于分页的limit查询语句
	 * 
	 * @param numberPerPage			每页记录数
	 * @param currentPageNumber		当前页数
	 * @return
	 */
	protected String getPageLimitQueryString(int numberPerPage, int currentPageNumber)
	{
		if (currentPageNumber == 0)
		{
			return " LIMIT 0," + String.valueOf(numberPerPage) + " ";
		}
		else
		{
			int from = numberPerPage * (currentPageNumber - 1);
			return " LIMIT " + String.valueOf(from) + "," + String.valueOf(numberPerPage) + " ";
		}
	}
	
	/**
	 * 获取用于分页的limit信息
	 * 
	 * @param numberPerPage			每页记录数
	 * @param currentPageNumber		当前页数
	 * @return	FROM, TO
	 */
	protected int[] getPageLimitQueryParam(int numberPerPage, int currentPageNumber)
	{
		int[] rtn = new int[2];
		if (currentPageNumber == 0)
		{
			rtn[0] = 0;
			rtn[1] = numberPerPage;
		}
		else
		{
			rtn[0]= numberPerPage * (currentPageNumber - 1);
			//rtn[1] = rtn[0] + numberPerPage;
			rtn[1] = numberPerPage;
		}
		return rtn;
	}
	
	
	/**
	 * 将String[]转化成'A','B','C'这样用于IN查询子句的形式
	 * 
	 * @param arr
	 * @return
	 */
	protected String getInClauseContent(String[] arr)
	{
        if (arr != null && arr.length != 0)
        {
        	StringBuilder sb = new StringBuilder();
        	for (String s : arr)
        	{
        		sb.append("'");
        		sb.append(s);
        		sb.append("'");
        		sb.append(",");
        	}
        	return sb.substring(0, sb.length()-1);
        }
        else
        {
        	return null;
        }
	}
	
	/**
	 * 将一个字符串值添加到SQL
	 * 
	 * @param sql				SQL语句
	 * @param s					字符串值
	 * @param withQuota			是否添加单引号
	 * @param withComma			是否添加逗号
	 */
	protected void appendToSql(StringBuffer sql, String s, boolean withQuota, boolean withComma)
	{
		if (withQuota)
		{
			sql.append("'");
		}
		sql.append(s);
		if (withQuota)
		{
			sql.append("'");
		}
		if (withComma)
		{
			sql.append(",");
		}
	}
	
	/**
	 * 将一个可为空的字段添加到SQL
	 * 
	 * @param sql				SQL语句
	 * @param s					字符串值
	 * @param withQuota			是否添加单引号
	 * @param withComma			是否添加逗号
	 */
	protected void appendToSqlNullable(StringBuffer sql, String s, boolean withQuota, boolean withComma)
	{
		if (s == null)
		{
			sql.append("NULL");
			if (withComma)
			{
				sql.append(",");
			}
		}
		else
		{
			appendToSql(sql, s, withQuota, withComma);
		}
	}
	
	/**
	 * 获取当前时间
	 * 
	 * @return 
	 */
	protected java.sql.Timestamp now()
	{
		return new java.sql.Timestamp((new Date()).getTime());
	}
	
	/**
	 * 将BLOB数据读取到byte[]
	 * @param blob  BLOB数据
	 * @return	读到的数据,返回null表示读取数据失败
	 */
	protected byte[] readBlob(Blob blob)
	{
		InputStream in = null;
		try 
		{
			in = blob.getBinaryStream();
			int count = in.available();
		    byte[] buffer = new byte[count];
		    in.read(buffer);
		    return buffer;
		} 
		catch (Exception e) 
		{
			log.error(e.getMessage(),e);
			return null;
		} 
		finally
		{
			if (in != null)
			{
				try 
				{
					in.close();
				} 
				catch (IOException e) 
				{
					log.error(e.getMessage(),e);
				}
			}
		}
	}

}
