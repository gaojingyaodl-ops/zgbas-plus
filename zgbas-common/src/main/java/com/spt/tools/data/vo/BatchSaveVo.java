/**
 * 
 */
package com.spt.tools.data.vo;

import java.util.List;

/**
 * @author wlddh
 *
 */
public class BatchSaveVo<T> {
	private List<T> insertedRecords;
	private List<T> updatedRecords;
	private List<T> deletedRecords;

	
	public List<T> getInsertedRecords() {
		return insertedRecords;
	}

	public void setInsertedRecords(List<T> insertedRecords) {
		this.insertedRecords = insertedRecords;
	}

	public List<T> getUpdatedRecords() {
		return updatedRecords;
	}

	public void setUpdatedRecords(List<T> updatedRecords) {
		this.updatedRecords = updatedRecords;
	}

	public List<T> getDeletedRecords() {
		return deletedRecords;
	}

	public void setDeletedRecords(List<T> deletedRecords) {
		this.deletedRecords = deletedRecords;
	}
}
