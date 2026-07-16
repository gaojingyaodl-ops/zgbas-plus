package com.spt.bas.client.vo;

public class BasBrandSearchVo {

	private String productCd;
	private String brandNumber;
	private Long enterpriseId;

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getProductCd() {
		return productCd;
	}

	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}

	public String getBrandNumber() {
		return brandNumber;
	}

	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
}
