package com.spt.tools.web.filter;

import java.util.Map;

/** 全局默认查询条件 */
public interface IDefaultSearchFilter {

	public Map<String, Object> filter();
}
