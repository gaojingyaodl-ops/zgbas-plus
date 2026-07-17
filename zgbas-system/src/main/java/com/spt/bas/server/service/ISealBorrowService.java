package com.spt.bas.server.service;

import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.SealBorrow;
import com.spt.bas.client.vo.SealBorrowSearchVo;
import com.spt.bas.client.vo.SealBorrowVo;

public interface ISealBorrowService extends IBaseService<SealBorrow> {

	public Page<SealBorrow> findBorrowPage(SealBorrowSearchVo searchVo);

	public void updateSealBorrow(SealBorrowVo borrowVo);

	public void doSealBorrowTask();

}

