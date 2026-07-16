/**
 * 
 */
package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 商品权限表
 * 
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_bs_product_type_access")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsProductTypeAccess extends IdEntity {
	private static final long serialVersionUID = -3907433711045468159L;
	private String productCd; // 商品代码
	private Long enterpriseId; // 企业公司ID

	public String getProductCd() {
		return productCd;
	}

	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

}
