/**
 * 
 */
package com.spt.tools.data.vo;

import org.springframework.data.domain.Sort;

/**
 * @author wlddh
 *
 */
public class SortImpl extends Sort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 186347750210467043L;
	public SortImpl() {
		this(new Order[0]);
	}
	public SortImpl(Order... orders) {
		super(java.util.Arrays.asList(orders));
	}
}
