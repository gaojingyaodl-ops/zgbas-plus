/**
 * 
 */
package com.spt.tools.jpa.persistence;

/**
 * @author wlddh
 *
 */
public class FromToValue<T> {

	T from;

	T to;

	public FromToValue(T from, T to) {
		super();
		this.from = from;
		this.to = to;
	}

}
