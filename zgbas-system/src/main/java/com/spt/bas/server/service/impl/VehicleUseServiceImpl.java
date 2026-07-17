package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealBorrow;
import com.spt.bas.client.entity.VehicleUse;
import com.spt.bas.client.vo.VehicleUseVo;
import com.spt.bas.server.dao.VehicleUseDao;
import com.spt.bas.server.service.IVehicleUseService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Component("vehicleUseService")
@Transactional(readOnly = true)
public class VehicleUseServiceImpl extends BaseService<VehicleUse> implements IVehicleUseService , IPmApproveListener {
	@Autowired
	private VehicleUseDao vehicleUseDao;
	@Autowired
	private PmApproveContentsDao pmApproveContentsDao;

	@Override
	public BaseDao<VehicleUse> getBaseDao() {
		return vehicleUseDao;
	}

	@Override
	public Class<VehicleUse> getEntityClazz() {
		return VehicleUse.class;
	}

	@Override
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
			String contents = pmApproveContents.getContents();
			VehicleUse entity = JsonUtil.json2Object(VehicleUse.class, contents);
			entity.setApproveId(approve.getId());//审批id
			entity.setId(0L);//编号
			entity.setCreatedDate(approve.getCreatedDate());//创建时间
			entity.setUpdatedDate(approve.getUpdatedDate());//更新时间
			entity.setStatus(approve.getStatus());
			entity.setFileId(pmApproveContents.getFileId());
			save(entity);
		}
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

	}

	@Override
	public Page<VehicleUse> findVehiclePage(VehicleUseVo vehicleUseVo) {
		Sort sort = Sort.by(Sort.Direction.DESC, "id");
		Map<String, Object> searchParams = vehicleUseVo.getSearchParams();
		Specification<VehicleUse> spec = WebUtil.buildSpecification(searchParams);
		PageRequest pageRequest = PageRequest.of(vehicleUseVo.getPage() - 1, vehicleUseVo.getRows(), sort);
		Page<VehicleUse> page = getBaseDao().findAll(spec, pageRequest);

		PageRequest pageRequest_new = PageRequest.of(vehicleUseVo.getPage() - 1, vehicleUseVo.getRows());
		Page<VehicleUse> pageVo = new PageImpl<>(page.getContent(), pageRequest_new, page.getTotalElements());
		return pageVo;
	}
}
