package com.spt.bas.client.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.bas.client.entity.BsWarehouse;

public class BsWarehouseVo extends BsWarehouse {

	private String defaultAddr;


	private String areaCode; // 地区代码

	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean wDefaultFlg;//是否默认

	private String address;
	/**
	 * 联系电话
	 */
	private String wContactPhone;

	private Long wCompanyId;

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public Boolean getwDefaultFlg() {
		return wDefaultFlg;
	}

	public void setwDefaultFlg(Boolean wDefaultFlg) {
		this.wDefaultFlg = wDefaultFlg;
	}

	public Long getwCompanyId() {
		return wCompanyId;
	}

	public void setwCompanyId(Long wCompanyId) {
		this.wCompanyId = wCompanyId;
	}

	public String getwContactPhone() {
		return wContactPhone;
	}

	public void setwContactPhone(String wContactPhone) {
		this.wContactPhone = wContactPhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDefaultAddr() {
		return defaultAddr;
	}

	public void setDefaultAddr(String defaultAddr) {
		this.defaultAddr = defaultAddr;
	}

}
