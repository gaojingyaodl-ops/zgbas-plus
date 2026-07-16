package com.spt.tools.core.bean;

import java.util.Map;

public class PageSearchVo {
	/** 页码 */
	private int page = 1;
	/** 每页条数 */
	private int rows = 10;
	/** 排序字段 */
	private String sort;
	/** 排序顺序：DESC,ASC */
	private String order;
	private Map<String, Object> searchParams;

	private long count;// 总记录数，设置为“-1”表示不查询总数

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Map<String, Object> getSearchParams() {
		return searchParams;
	}

	public void setSearchParams(Map<String, Object> searchParams) {
		this.searchParams = searchParams;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
