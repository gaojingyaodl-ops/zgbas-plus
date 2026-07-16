package com.spt.tools.core.constants;

/**
 * 共通错误字
 * 
 * @author wangyilin
 *
 */
public interface CommonErrorId 
{
	final int ERROR_NONE = 0;                			//无错误
	final int ERROR_OFFLINE = 255;						//服务时间带外
	final int ERROR_DATA_EXCHANGE = 256;				//数据转换异常
	
	//DB Exception区间
	final int ERROR_DB_UNMAPPED = 257;					//数据库操作异常（未映射）
	final int ERROR_DB_CONNECTION_FAIL = 258; 			//数据库连接失败
	final int ERROR_DB_BAD_SQL = 259; 					//不正确的SQL语句
	final int ERROR_DB_DUPLICATE_KEY = 260;   			//主键唯一性违反
	//DB Exception区间
	
	final int ERROR_UNKNOWN_SERVICEID = 280;   			//不能识别的ServiceId
	final int ERROR_ILLEGAL_CLIENT_REGISTER = 281;   	//非法客户端登录
	final int ERROR_NO_SUCH_ALGORITHM = 282; 			//不支持的算法
	final int ERROR_UP_PARAM = 283;          			//参数值错误：
	final int ERROR_EMPTY_RESULT = 284;					//记录不存在
	final int ERROR_INVALID_OPERATION = 285;           	//不正确的操作：
	final int ERROR_OVER_MAX_CLIENT_REGISTER_COUNT = 286;	//用户登录次数超过上限
	final int ERROR_CLIENTID_NOT_FOUND = 287;				//没有找到clientId
	final int ERROR_SYSTEM_BUSY = 288;						//系统正繁忙
	final int ERROR_NOTLOGIN = 289;						//用户未登录
	final int ERROR_PERSON_FORBID = 290;						//个人账户不允许操作，请升级企业账户
	final int ERROR_ENTERINFO_WIT_AUDIT = 291;						//企业申请资料已提交，请等待审核！
	final int ERROR_ENTERINFO_NOT_PASSED = 292;						//您的企业资料未认证通过，有任何疑问请联系客服！
	
	final int ERROR_UNKNOWN = 400;                     		//未知错误
	final int ERROR_REMOTE_CALL_TIMEOUT = 401;          	//远程调用超时
	final int ERROR_REMOTE_SERVICE_UNIMPLEMENTED = 402; 	//未实现的远程服务
	final int ERROR_REMOTE_CONNECTION_FAIL = 403; 			//远程连接异常
	final int ERROR_REMOTE_SERVICE_NOT_FOUND = 404;			//无法定位的远程服务
	final int ERROR_REMOTE_SECURITY_ERROR = 405;			//安全性异常
	final int ERROR_NEGO_USER_STATUS_INVALID = 501;			//对不起，当前挂单用户已被禁用或者解除绑定！
}
