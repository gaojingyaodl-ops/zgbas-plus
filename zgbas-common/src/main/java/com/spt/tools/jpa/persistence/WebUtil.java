/**
 * 
 */
package com.spt.tools.jpa.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author huangjian
 * 
 */
public class WebUtil {
	public static final String DESC = "desc";
	public static final String ASC = "asc";

	/**
	 * 创建动态查询条件组合.
	 * <p>
	 * searchParams中key的格式为OPERATOR_FIELDNAME
	 * </p>
	 */
	public static <T> Specification<T> buildSpecification(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<T> spec = DynamicSpecifications.bySearchFilter(filters.values());
		return spec;
	}

	/**
	 * 创建动态查询条件组合.
	 * <p>
	 * searchParams中key的格式为OPERATOR_FIELDNAME
	 * </p>
	 */
	public static <T> Specification<T> buildSpecification(String key, Object value) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		Map<String, SearchFilter> filters = SearchFilter.parse(map);
		Specification<T> spec = DynamicSpecifications.bySearchFilter(filters.values());
		return spec;
	}

	/**
	 * 创建分页请求.
	 */
	public static PageRequest buildPageRequest(int pageNumber, int pagSize, String sortField) {
		Map<String, String> mapOrder = new HashMap<String, String>();
		mapOrder.put(sortField, WebUtil.ASC);
		return buildPageRequest(pageNumber, pagSize, mapOrder);
	}

	/**
	 * 创建分页请求.
	 */
	public static PageRequest buildPageRequest(int pageNumber, int pagSize, Map<String, String> mapOrder) {
		List<Order> orders = new ArrayList<>();
		for (String key : mapOrder.keySet()) {
			Order order = new Order(getDirection(mapOrder.get(key)), key);
			orders.add(order);
		}
		if (orders.isEmpty()) {
			return PageRequest.of(pageNumber - 1, pagSize);
		} else {
			Sort sort = Sort.by(orders);
			return PageRequest.of(pageNumber - 1, pagSize, sort);
		}
	}

	private static Direction getDirection(String order) {
		Direction direction = Direction.DESC;
		if (WebUtil.ASC.equalsIgnoreCase(order)) {
			direction = Direction.ASC;
		}
		return direction;
	}
}
