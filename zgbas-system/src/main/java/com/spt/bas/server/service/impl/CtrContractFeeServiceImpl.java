package com.spt.bas.server.service.impl;




import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractFee;
import com.spt.bas.client.vo.ContractFeeSearchVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.CtrContractFeeDao;
import com.spt.bas.server.service.ICtrContractFeeService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class CtrContractFeeServiceImpl extends BaseService<CtrContractFee> implements ICtrContractFeeService {
	@Autowired
	private CtrContractFeeDao ctrContractFeeDao;
	
	@Override
	public BaseDao<CtrContractFee> getBaseDao() {
		return ctrContractFeeDao;
	}
	
	@Override
	public Class<CtrContractFee> getEntityClazz() {
		return CtrContractFee.class;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		ctrContractFeeDao.updateFileId(id, fileId);
		
	}

	@Override
	public Page<CtrContractFee> findPageContractFee(ContractFeeSearchVo queryVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);
		Specification<CtrContractFee> spec = WebUtil.buildSpecification(queryVo.getSearchParams());
		Specification<CtrContractFee> whereSpec = Specification.where(spec);
		Page<CtrContractFee> page = getBaseDao().findAll(whereSpec, pageRequest);
		return page;
	}

	@Override
	public 	List<CtrContractFee> findByContractIdAndFeeType(Long contractId,String feeType) {
		return ctrContractFeeDao.findByContractIdAndFeeType(contractId,feeType);
	}
	
	/**
	 * 保存系统仓储费
	 */
	@Override
	@ServerTransactional
	public void saveWarehouseFee(CtrContract contract, BigDecimal feeAmount, Boolean backFlg) {
		if (!backFlg) {
			CtrContractFee fee = new CtrContractFee();
			fee.setContractId(contract.getId());
			fee.setContractNo(contract.getContractNo());
			fee.setEnterpriseId(contract.getEnterpriseId());
			fee.setFeeAmount(feeAmount);
			fee.setFeeDate(new Date());
			fee.setFeeType(BasConstants.DICT_TYPE_FEETYPE_SWF);
			ctrContractFeeDao.save(fee);
		} else {
			ctrContractFeeDao.deleteByContractIdAndFeeTypeAndFeeAmount(contract.getId(),
					BasConstants.DICT_TYPE_FEETYPE_SWF, feeAmount);
		}
	}

}

