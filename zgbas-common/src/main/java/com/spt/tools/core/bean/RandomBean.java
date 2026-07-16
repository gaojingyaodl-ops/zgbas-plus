package com.spt.tools.core.bean;

import com.spt.tools.core.util.RandomUtils;

public class RandomBean {

	public String getStr() {
		return RandomUtils.generateString(4);
	}
}
