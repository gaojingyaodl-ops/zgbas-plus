package com.spt.bas.server.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsArea;
import com.spt.bas.client.vo.CompanyAreaVo;
import com.spt.bas.server.dao.BsAreaDao;
import com.spt.bas.server.service.IBsAreaService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsAreaServiceImpl  extends BaseService<BsArea> implements IBsAreaService {

	@Autowired
	private BsAreaDao bsAreaDao;
	@Override
	public BaseDao<BsArea> getBaseDao() {
		return bsAreaDao;
	}
	/**
	 * 查询客户对应的省市县
	 * @param id
	 * @return
	 */
	@Override
	public CompanyAreaVo getAreaVo(Long id){
		CompanyAreaVo vo = new CompanyAreaVo();
		BsArea area = this.getEntity(id);
		if (StringUtils.isNotBlank(area.getParentId())) {
			BsArea curParentArea = this.getEntity(Long.parseLong(area.getParentId()));
			if (curParentArea.getGrand() == 2) {// 市
				Long parentId = Long.parseLong(curParentArea.getCode());// 市
				vo.setCityId(parentId);
				vo.setCityName(curParentArea.getName());
				if(StringUtils.isNotBlank(curParentArea.getParentId())){
					BsArea  curParentsArea= this.getEntity(Long.parseLong(curParentArea.getParentId()));
					if(curParentsArea.getGrand() == 1){//省
						Long parentsId = Long.parseLong(curParentsArea.getCode());// 省
						vo.setProvinceId(parentsId);
						vo.setProvinceName(curParentsArea.getName());
					}
				}
			}
			else {
				Long parentId = Long.parseLong(curParentArea.getCode());// 省
				vo.setProvinceId(parentId);
				vo.setProvinceName(curParentArea.getName());
			}
			// 县
			Long curId = Long.parseLong(area.getCode());
			vo.setRegionId(curId);
			vo.setRegionName(area.getName());
		} else {
			// 省
			vo.setProvinceId(Long.parseLong(area.getCode()));
			vo.setProvinceName(area.getName());
		}
		return vo;
	}

	@Override
	public List<BsArea> getAllArea() {
		return bsAreaDao.findAll((Specification<BsArea>) null);
	}


	/**
	 * 查询最顶级
	 */
	@Override
	public List<BsArea> findTopLevel() {
		return bsAreaDao.findTopLevel();
	}
	/**
	 * 查询最下级
	 */
	@Override
	public List<BsArea> findByparentId(String pid) {
		return bsAreaDao.findByParentId(pid);
	}
	@Override
	public List<BsArea> findByCode(String code) {
		return bsAreaDao.findByCode(code);
	}
	
	
	
}
