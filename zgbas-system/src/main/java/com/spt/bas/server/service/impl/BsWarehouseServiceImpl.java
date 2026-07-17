package com.spt.bas.server.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.common.base.Splitter;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.entity.BsWarehouseAddr;
import com.spt.bas.client.vo.BsWarehouseAddrVo;
import com.spt.bas.client.vo.BsWarehouseSearchVo;
import com.spt.bas.client.vo.BsWarehouseVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsWarehouseAddrDao;
import com.spt.bas.server.dao.BsWarehouseDao;
import com.spt.bas.server.service.IBsWarehouseAddrService;
import com.spt.bas.server.service.IBsWarehouseService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsWarehouseServiceImpl extends BaseService<BsWarehouse> implements IBsWarehouseService {
	@Autowired
	private BsWarehouseDao bsWarehouseDao;
	@Autowired
	private IBsWarehouseAddrService warehouseAddrService;
	@Autowired
	private BsWarehouseAddrDao bsWarehouseAddrDao;
	@Autowired
	private IWxUserDetailClient wxUserDetailClient;


	@Override
	public BaseDao<BsWarehouse> getBaseDao() {
		return bsWarehouseDao;
	}

	@Override
	public Class<BsWarehouse> getEntityClazz() {
		return BsWarehouse.class;
	}

	@Override
	public List<BsWarehouse> findByEnterpriseId(Long enterpriseId){
		List<BsWarehouse> warehouseList = bsWarehouseDao.findByEnterpriseId(enterpriseId);
		List<BsWarehouseAddr> warehouseAddrList = warehouseAddrService.findAll();
		// 使用 Java 8 的 Stream API 对 warehouseAddrList 进行分组和排序
		Map<Long, List<BsWarehouseAddr>> warehouseIdGroupedMap = warehouseAddrList.stream()
				.collect(Collectors.groupingBy(BsWarehouseAddr::getWarehouseId));
		// 对每个分组中的 List<BsWarehouseAddr> 按 defaultFlg 进行排序
		warehouseIdGroupedMap.values().forEach(list -> list.sort(Comparator.comparing(BsWarehouseAddr::getDefaultFlg).reversed()));
		for (BsWarehouse bsWarehouse : warehouseList) {
			List<BsWarehouseAddr> warehouseAddrs = warehouseIdGroupedMap.get(bsWarehouse.getId());
			if(CollectionUtils.isNotEmpty(warehouseAddrs)) {
				bsWarehouse.setWarehouseAddr(warehouseAddrs.get(0).getWarehouseAddr());
			}
		}

		return warehouseList;
	}

	@Override
	@ServerTransactional
	public BsWarehouse save(BsWarehouse entity) throws ApplicationException {
//		//保存前查询是否存在相同的仓库名称
//		List<BsWarehouse> list = this.bsWarehouseDao.findByWarehouseName(entity.getWarehouseName());
//		if(list!=null&&list.size()>0){
//			if(list.size()==1){
//				BsWarehouse warehouse = list.get(0);
//				if(entity.getId()!=warehouse.getId()){
//					//修改保存同名
//					return entity;
//				}
//			}else if(list.size()>1){
//				return entity;
//			}
//		}
		return getBaseDao().save(entity);
	}

	@Override
	public Long countWarehousewarehouse(BsWarehouse warehouse) {
		Map<String, Object> queryParams = new HashMap<String, Object>();
		if (warehouse.getId()!=null && warehouse.getId()>0) {
			queryParams.put("NEQL_id", warehouse.getId());
		}
		queryParams.put("EQS_warehouseName", warehouse.getWarehouseName().trim());
		queryParams.put("EQL_enterpriseId", warehouse.getEnterpriseId());
		Specification<BsWarehouse> spec = WebUtil.buildSpecification(queryParams);
		return bsWarehouseDao.count(spec);
	}

	@Override
	@ServerTransactional
	public void saveBsWarehouseAddrVo(BsWarehouseAddrVo vo) throws ApplicationException {
		BsWarehouse warehouse = new BsWarehouse();
		if (vo.getId()!=null&&vo.getId()!=0L) {
			warehouse = getEntity(vo.getId());
		}
		warehouse.setWarehouseName(vo.getWarehouseName());
		warehouse.setCity(vo.getCity());
		warehouse.setContactName(vo.getContactName());
		warehouse.setContactPhone(vo.getContactPhone());
		warehouse.setRemark(vo.getRemark());
		warehouse.setEnterpriseId(vo.getEnterpriseId());
		warehouse.setEnableFlg(vo.getEnableFlg());
		warehouse.setTpBusinessFlg(vo.getTpBusinessFlg());
		warehouse.setZyBusinessFlg(vo.getZyBusinessFlg());
		warehouse.setStorageComputeFormula(vo.getStorageComputeFormula());
		warehouse = save(warehouse);
		warehouseAddrService.saveBatch(vo.getLstInsert(),vo.getLstUpdate(),vo.getLstDelete(),warehouse);

	}

	@Override
	@ServerTransactional
	public void delete(Long id) throws ApplicationException {
		bsWarehouseAddrDao.deleteByWarehouseId(id);
		super.delete(id);
	}

	@Override
	public List<BsWarehouse> findByWarehouseName(@RequestBody String warehouseName) {
		return bsWarehouseDao.findByWarehouseName(warehouseName);
	}

	@Override
	public List<BsWarehouse> findList(BsWarehouseSearchVo vo) {
		Map<String,Object> map =new HashMap<>();
		map.put("EQL_enterpriseId", vo.getEnterpriseId());
		map.put("EQB_enableFlg", true);
		Specification<BsWarehouse> spec=WebUtil.buildSpecification(map);
		if (StringUtils.isNotBlank(vo.getIds())) {
			List<String> lstIdStr = (List<String>)Splitter.on(",").splitToList(vo.getIds());
			List<Long> lstId =new ArrayList<>();
			for(String strId :lstIdStr) {
				if (StringUtils.isNotBlank(strId)) {
					lstId.add(Long.valueOf(strId.trim()));
				}
			}

			Specification<BsWarehouse>  specIds= WebUtil.buildSpecification("INL_id", lstId);
			spec = Specification.where(spec).and(specIds);
		}
		if (StringUtils.isNotBlank(vo.getWarehouseName())) {
			Specification<BsWarehouse>  specIds= WebUtil.buildSpecification("EQS_warehouseName", vo.getWarehouseName());
			spec = Specification.where(spec).and(specIds);
		}

		List<BsWarehouse> list = bsWarehouseDao.findAll(spec);
		return list;
	}
	// 查询仓库集合
	@Override
	public Page<BsWarehouse> findWarehouses(PageSearchVo searchVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		Specification<BsWarehouse> spec = WebUtil.buildSpecification(searchVo.getSearchParams());
		PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
		Page<BsWarehouse> page = getBaseDao().findAll(spec, pageRequest);
		return page;
	}

	@Override
	public List<BsWarehouse> findByWarehouseNameAndEnterpriseId(BsWarehouse bs) {
		List<BsWarehouse> list = bsWarehouseDao.findByWarehouseNameAndEnterpriseId(bs.getWarehouseName(), bs.getEnterpriseId());
		return list;
	}

	@Override
	public List<BsWarehouse> findByWxUserId(Long wxUserId,Long enterpriseId) {
		return bsWarehouseDao.findByWxUserIdAndEnableFlgTrueAndEnterpriseIdOrderByCreatedDateDesc(wxUserId, enterpriseId);
	}

	@Override
	public BsWarehouse findByWxUserIdAndWarehouseId(Long wxUserId,Long enterpriseId, Long warehouseId) {
		return bsWarehouseDao.findByWxUserIdAndEnableFlgTrueAndEnterpriseIdAndId(wxUserId, enterpriseId, warehouseId);
	}

	@Override
	public Integer countByWxUserId(Long wxUserId,Long enterpriseId) {
		return bsWarehouseDao.countByWxUserIdAndEnableFlgTrueAndEnterpriseId(wxUserId, enterpriseId);
	}

	@Override
	public Integer countByCompanyId(Long companyId) {
		return bsWarehouseDao.countByCompanyIdAndEnableFlgTrueAndEnterpriseId(companyId, BasConstants.ZG_ENTERPRISE_ID);
	}

	@Override
	public BsWarehouse findid(Long id) {
		return bsWarehouseDao.findid(id);
	}

	@Override
	public List<BsWarehouse> findByBusiness(BsWarehouseSearchVo vo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		Map<String, Object> searchParams = new HashMap<>();
		if(vo.getEnterpriseId()!=null){
			searchParams.put("EQL_enterpriseId",vo.getEnterpriseId());
		} else{
			searchParams.put("EQL_enterpriseId",BasConstants.ZG_ENTERPRISE_ID);
		}
		searchParams.put("EQB_enableFlg",true);
		if(vo.getTpBussinessFlg()!=null){
			searchParams.put("EQB_tpBusinessFlg",vo.getTpBussinessFlg());
		}
		if(vo.getZyBussinessFlg()!=null){
			searchParams.put("EQB_zyBusinessFlg",vo.getZyBussinessFlg());
		}

		Specification<BsWarehouse> spec = WebUtil.buildSpecification(searchParams);
		return bsWarehouseDao.findAll(spec,sort);
	}

	@Override
	public List<BsWarehouseVo> findWarehouseByCompanyId(Long companyId) {
		UserDetail userDetail = wxUserDetailClient.findByCompanyIdAndIsBindTrue(companyId);
		if (userDetail != null) {
			Long wxUserId = userDetail.getUserId();
			List<BsWarehouse> bsWarehouses = bsWarehouseDao.findByWxUserIdAndEnableFlgTrueAndEnterpriseIdOrderByCreatedDateDesc(wxUserId, BasConstants.ZG_ENTERPRISE_ID);
			List<BsWarehouseVo> result = new ArrayList<>(bsWarehouses.size());
			for (BsWarehouse bsWarehouse : bsWarehouses) {
				BsWarehouseVo vo = new BsWarehouseVo();
				List<BsWarehouseAddr> list = bsWarehouseAddrDao.queryDefaultFlg(bsWarehouse.getId(), true);
				BeanUtils.copyProperties(bsWarehouse, vo);
				if (!list.isEmpty()) {
					vo.setDefaultAddr(list.get(0).getWarehouseAddr());
				}
				result.add(vo);
			}
			return result;
		}
		return new ArrayList<>();
	}


	@Override
	public List<BsWarehouseVo> findByCompanyId(Long companyId) {
		List<BsWarehouse> bsWarehouses = bsWarehouseDao.findByCompanyIdAndEnableFlgTrueAndEnterpriseIdOrderByCreatedDateDesc(companyId, BasConstants.ZG_ENTERPRISE_ID);
		List<BsWarehouseVo> result = new ArrayList<>(bsWarehouses.size());
		for (BsWarehouse bsWarehouse : bsWarehouses) {
			BsWarehouseVo vo = new BsWarehouseVo();
			List<BsWarehouseAddr> list = bsWarehouseAddrDao.queryDefaultFlg(bsWarehouse.getId(), true);
			BeanUtils.copyProperties(bsWarehouse, vo);
			if (!list.isEmpty()) {
				vo.setDefaultAddr(list.get(0).getWarehouseAddr());
			}
			result.add(vo);
		}
		return result;
	}

	@Override
	public List<BsWarehouse> findByCompanyIdAddr(Long companyId) {
		return bsWarehouseDao.findByCompanyId(companyId);
	}


}

