/**
 * 
 */
package com.spt.tools.data.vo;

/**
 * @author wlddh
 *
 */
public class GeneralResult<T> {

	private T value;

	public GeneralResult() {
	};

	public GeneralResult(T value) {
		this.value = value;
	};

	public static <T> GeneralResult<T> of(T value) {
		return new GeneralResult<>(value);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

}
