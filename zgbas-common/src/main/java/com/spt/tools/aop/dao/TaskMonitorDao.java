package com.spt.tools.aop.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.spt.tools.aop.task.TaskMonitorEntity;

/**
 * 任务记录DAO
 *
 */
public class TaskMonitorDao extends DaoBase
{
	private static final String UPDATE_TASK_SQL = "REPLACE INTO T_SYS_TASKMONITOR (TASKNAME,THDID,EXECUTERST,EXCEPTIONMSG,EXECUTELENGTH,EXECUTETIME,UPDATETIME,UPDATEUSER)"
		+ " VALUES (?,?,?,?,?,NOW(),NOW(),'stage')";
	
	private static final String SELECT_TASK_SQL="SELECT TASKNAME,THDID,EXECUTERST,EXCEPTIONMSG,EXECUTELENGTH,EXECUTETIME,UPDATETIME,UPDATEUSER FROM T_SYS_TASKMONITOR WHERE TASKNAME =?";
	
	public void updateTask(TaskMonitorEntity entity)
	{
		try
		{
			getJdbcTemplate().update(UPDATE_TASK_SQL, 
				new Object[]{
					entity.getTaskName(),
					entity.getThdId(),
					entity.getExecuteRst(),
					entity.getExceptionMsg(),
					entity.getExecuteLength(),
				}, 
				new int[]{
					java.sql.Types.VARCHAR,
					java.sql.Types.VARCHAR,
					java.sql.Types.VARCHAR,
					java.sql.Types.VARCHAR,
					java.sql.Types.INTEGER
				}
			);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public TaskMonitorEntity findByTaskName(String taskName) {
		getJdbcTemplate().setSkipUndeclaredResults(true);
		TaskMonitorEntity result = null;
		try {
			result = getJdbcTemplate().queryForObject(SELECT_TASK_SQL, new Object[] { taskName }, (rs, rowNum) -> {
				TaskMonitorEntity entity = new TaskMonitorEntity();
				entity.setTaskName(rs.getString(1));
				entity.setThdId(rs.getString(2));
				entity.setExecuteRst(rs.getString(3));
				entity.setExceptionMsg(rs.getString(4));
				entity.setExecuteLength(rs.getLong(5));
				entity.setExecuteTime(rs.getDate(6));
				entity.setUpdateTime(rs.getDate(7));
				entity.setUpdateUser(rs.getString(8));
				return entity;
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (DataAccessException e) {
			logger.error("findByTaskName error",e);
		}
		return result;
	}
}
