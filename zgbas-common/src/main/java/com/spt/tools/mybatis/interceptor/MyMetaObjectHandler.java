package com.spt.tools.mybatis.interceptor;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

public class MyMetaObjectHandler implements MetaObjectHandler {
	@Override
	public void insertFill(MetaObject metaObject) {
		Object createdDate = getFieldValByName("createdDate", metaObject);
		Date now = new Date();
		if (createdDate == null) {
			setFieldValByName("createdDate", now, metaObject);
		}
		setFieldValByName("updatedDate", now, metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		setFieldValByName("updatedDate", new Date(), metaObject);
	}
}
