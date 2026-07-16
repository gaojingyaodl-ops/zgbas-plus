/**
 * 
 */
package com.spt.pm.inter;

import java.util.List;

/**
 * @author wlddh
 *
 */
public interface IPmEntity {

	default void setEnterpriseId(Long enterpriseId) {
	}

	Long getId();

	default void setApproveId(Long approveId) {
	}

	default void setStatus(String status) {
	}


	default Class<?> getSubClass() {
		return null;
	}

	default void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {

	}

	default void setChainBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {

	}

	default void setFileId(String fileId) {
	}
}
