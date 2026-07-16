package com.spt.bas.client.vo;

public class BsWarehouseSearchVo {

	private String ids;
	private String warehouseName;//仓库名称
	private Long enterpriseId;
	/**
	 * 托盘flg
	 */
	private Boolean tpBussinessFlg;
	/**
	 * 自营flg
	 */
	private Boolean zyBussinessFlg;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Boolean getTpBussinessFlg() {
		return tpBussinessFlg;
	}

	public void setTpBussinessFlg(Boolean tpBussinessFlg) {
		this.tpBussinessFlg = tpBussinessFlg;
	}

	public Boolean getZyBussinessFlg() {
		return zyBussinessFlg;
	}

	public void setZyBussinessFlg(Boolean zyBussinessFlg) {
		this.zyBussinessFlg = zyBussinessFlg;
	}
}
