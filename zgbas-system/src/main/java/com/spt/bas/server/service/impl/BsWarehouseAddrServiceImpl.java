package com.spt.bas.server.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.spt.bas.client.entity.BsArea;
import com.spt.bas.client.remote.IBsAreaClient;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.entity.BsWarehouseAddr;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsWarehouseAddrDao;
import com.spt.bas.server.dao.BsWarehouseDao;
import com.spt.bas.server.service.IBsWarehouseAddrService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsWarehouseAddrServiceImpl extends BaseService<BsWarehouseAddr> implements IBsWarehouseAddrService {
	@Autowired
	private BsWarehouseAddrDao bsWarehouseAddrDao;
	@Autowired
	private  BsWarehouseDao bsWarehouseDao;
	@Autowired
	private IBsAreaClient areaClient;
	
	
	@Override
	public BaseDao<BsWarehouseAddr> getBaseDao() {
		return bsWarehouseAddrDao;
	}
	@Override
	public Class<BsWarehouseAddr> getEntityClazz() {
		return BsWarehouseAddr.class;
	}
	
	@Override
	@ServerTransactional
	public BsWarehouseAddr save(BsWarehouseAddr addr){
		addr = this.bsWarehouseAddrDao.save(addr);
		Long id = addr.getWarehouseId();
		List<BsWarehouseAddr> addrList = bsWarehouseAddrDao.findByWarehouseId(id);
		for (BsWarehouseAddr bsAddr : addrList) {
			if (addr.getId() != bsAddr.getId()) {
				bsAddr.setDefaultFlg(false);
			}
		}
		/*BsWarehouse warehouse = this.bsWarehouseDao.findOne(id);
		warehouse.setWarehouseAddr(addr.getWarehouseAddr());
		bsWarehouseDao.save(warehouse);*/
		return addr;
	}
	@Override
	@ServerTransactional
	public void saveBatchAddr(List<BsWarehouseAddr> insertedAddr, List<BsWarehouseAddr> updatedAddr, List<BsWarehouseAddr> deletedAddr) throws ApplicationException{
		List<BsWarehouseAddr> adds = new ArrayList<BsWarehouseAddr>();
		for (BsWarehouseAddr entity : insertedAddr) {
			adds.add(entity);
		}
		for (BsWarehouseAddr entity : updatedAddr) {
			adds.add(entity);
		}
		Long parentId = null;
		String defaultAddr = "";
		for(BsWarehouseAddr entity:adds){
			boolean defaultFlg = entity.getDefaultFlg();
			List<BsWarehouseAddr> list = bsWarehouseAddrDao.findByWarehouseId(entity.getWarehouseId());
			if(defaultFlg){
				//查询仓库下的所有地址
				for(BsWarehouseAddr addr:list){
					if (addr.getId() != entity.getId()) {
						addr.setDefaultFlg(false);
					}
					else{
						addr.setDefaultFlg(true);
						/*BsWarehouse warehouse = this.bsWarehouseDao.findOne(entity.getWarehouseId());
						warehouse.setWarehouseAddr(addr.getWarehouseAddr());
						bsWarehouseDao.save(warehouse);*/
					}
				}
			}else if(list==null||list.size()<=0){
				entity.setDefaultFlg(true);
				parentId = entity.getWarehouseId();
				defaultAddr = entity.getWarehouseAddr();
			}
			entity = getBaseDao().save(entity);
		}
		/*if(parentId!=null){
			BsWarehouse warehouse = this.bsWarehouseDao.findOne(parentId);
			warehouse.setWarehouseAddr(defaultAddr);
			bsWarehouseDao.save(warehouse);
		}*/
		for (BsWarehouseAddr entity : deletedAddr) {
			delete(entity.getId());
		}
	}
	@Override
	@ServerTransactional
	public void saveBatch(List<BsWarehouseAddr> lstInsert, List<BsWarehouseAddr> lstUpdate,
			List<BsWarehouseAddr> lstDelete, BsWarehouse warehouse) {
		List<BsWarehouseAddr> adds = new ArrayList<BsWarehouseAddr>();
		for (BsWarehouseAddr entity : lstInsert) {
			entity.setWarehouseId(warehouse.getId());
			adds.add(entity);
		}
		for (BsWarehouseAddr entity : lstUpdate) {
			adds.add(entity);
		}
		// 标识是否有仓库地址是默认
		for(BsWarehouseAddr entity:adds){
			boolean defaultFlg = entity.getDefaultFlg();
			// 查询仓库下的所有地址
			List<BsWarehouseAddr> list = bsWarehouseAddrDao.findByWarehouseId(entity.getWarehouseId());
			if (list.size()>0) {
				if(defaultFlg) {
					for(BsWarehouseAddr addr:list) {
						if (addr.getId() != entity.getId()) {
							addr.setDefaultFlg(false);
							getBaseDao().save(addr);// 将数据库中仓库默认状态改为false
						}
					}
				}
			}
			getBaseDao().save(entity);
		}
		List<BsWarehouseAddr> list = bsWarehouseAddrDao.findByWarehouseId(warehouse.getId());
		if(CollectionUtils.isNotEmpty(list)){
			List<Boolean> collect = list.stream().map(BsWarehouseAddr::getDefaultFlg).collect(Collectors.toList());
			// 判断是否存在默认有效的仓库地址
			boolean b = collect.stream().anyMatch(bool -> bool);
			if(b){
				Optional<BsWarehouseAddr> first = list.stream().filter(BsWarehouseAddr::getDefaultFlg).findFirst();
				if(first.isPresent()){
					BsWarehouseAddr bsWarehouseAddr = first.get();
					List<BsArea> bsAreaList = areaClient.findAll();
					// 完整路径
					String fullAddr="";
					Optional<BsArea> province = bsAreaList.stream().filter(it -> it.getId().toString().equals(bsWarehouseAddr.getProvinceCode())).findFirst();
					Optional<BsArea> city = bsAreaList.stream().filter(it -> it.getId().toString().equals(bsWarehouseAddr.getCityCode())).findFirst();
					Optional<BsArea> area = bsAreaList.stream().filter(it -> it.getId().toString().equals(bsWarehouseAddr.getAreaCode())).findFirst();
					if(province.isPresent()){
						fullAddr+=province.get().getName()+"/";
					}
					if(city.isPresent()){
						fullAddr+=city.get().getName()+"/";
					}
					if(area.isPresent()){
						fullAddr+=area.get().getName()+"/";
					}
					fullAddr+=bsWarehouseAddr.getWarehouseAddr();
					// 设置仓库的地址和仓储费单价为有效的
					warehouse.setProvinceCode(bsWarehouseAddr.getProvinceCode());
					warehouse.setCityCode(bsWarehouseAddr.getCityCode());
					warehouse.setAreaCode(bsWarehouseAddr.getAreaCode());
					warehouse.setWarehouseUnitCost(bsWarehouseAddr.getWarehouseUnitCost());
					warehouse.setWarehouseAddr(bsWarehouseAddr.getWarehouseAddr());
					warehouse.setFullAddr(fullAddr);
					bsWarehouseDao.save(warehouse);
				}
			} else {
				// 没有默认仓库清除数据
				warehouse.setProvinceCode(null);
				warehouse.setCityCode(null);
				warehouse.setAreaCode(null);
				warehouse.setWarehouseUnitCost(null);
				warehouse.setWarehouseAddr(null);
				warehouse.setFullAddr(null);
				bsWarehouseDao.save(warehouse);
			}
		}
		for (BsWarehouseAddr entity : lstDelete) {
			delete(entity.getId());
		}
		
	}
	@Override
	@ServerTransactional
	public void delete(Long id) throws IllegalAccessError{
			BsWarehouseAddr entity = bsWarehouseAddrDao.findOne(id);
			boolean flg = entity.getDefaultFlg();
			if(flg==true){
				resetDefaultFlg(entity.getWarehouseId());
			}
			getBaseDao().delete(id);
		} 
	
	
	private void resetDefaultFlg(Long id) throws IllegalAccessError{
		  List<BsWarehouseAddr> listAddr = bsWarehouseAddrDao.findByWarehouseId(id);
		  if(listAddr.size()==1){
			 throw new IllegalAccessError("只有一条数据不能删除");
		  }
		  if(listAddr.size()>1){
			  List<BsWarehouseAddr> list = bsWarehouseAddrDao.queryDefaultFlg(id,false);
			  if(list!=null&&list.size()>0){
				  BsWarehouseAddr addr = list.get(0);
				  addr.setDefaultFlg(true);
				  bsWarehouseAddrDao.save(addr);
				  /*BsWarehouse warehouse = bsWarehouseDao.findOne(addr.getWarehouseId());
				  warehouse.setWarehouseAddr(addr.getWarehouseAddr());
				  bsWarehouseDao.save(warehouse);*/
			  }
		  }
	}
	@Override
	public Long countWarehouse(BsWarehouseAddr addr) {
		Map<String, Object> queryParams = new HashMap<String, Object>();
		if (addr.getId()!=null && addr.getId()>0) {
			queryParams.put("NEQL_id", addr.getId());
		}
		queryParams.put("EQS_warehouseShortName", addr.getWarehouseAddr());
		queryParams.put("EQL_warehouseId", addr.getWarehouseId());
		Specification<BsWarehouse> spec = WebUtil.buildSpecification(queryParams);
		return bsWarehouseDao.count(spec);
	}
	
	@Override
	public BsWarehouseAddr findWarehouseAddr(BsWarehouse wh) {
		BsWarehouseAddr addr = null;
		List<BsWarehouseAddr> list = bsWarehouseAddrDao.queryDefaultFlg(wh.getId(), true);
		if (list.size()>0) {
			addr = list.get(0);
		}
		return addr;
	}
	@Override
	public List<BsWarehouseAddr> findAllWarehouseAddr() {
		List<BsWarehouseAddr> findAllWarehouseAddr = bsWarehouseAddrDao.findAllWarehouseAddr();
		return findAllWarehouseAddr;
	}
	@Override
	public List<BsWarehouseAddr> findWarehouseAddrDesc(Long warehouseId) {
		return bsWarehouseAddrDao.findWarehouseAddrDesc(warehouseId);
	}
}

