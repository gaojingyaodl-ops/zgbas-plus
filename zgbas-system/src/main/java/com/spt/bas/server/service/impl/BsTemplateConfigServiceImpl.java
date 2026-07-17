package com.spt.bas.server.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsTemplateConfig;
import com.spt.bas.client.vo.DictDataVo;
import com.spt.bas.client.vo.TemplateQueryVo;
import com.spt.bas.server.dao.BsTemplateConfigDao;
import com.spt.bas.server.service.IBsTemplateConfigService;
import com.spt.bas.server.util.FormConfigUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsTemplateConfigServiceImpl extends BaseService<BsTemplateConfig> implements IBsTemplateConfigService {
	@Autowired
	private BsTemplateConfigDao bsTemplateConfigDao;
	
	@Override
	public BaseDao<BsTemplateConfig> getBaseDao() {
		return bsTemplateConfigDao;
	}
	
	@Override
	public Class<BsTemplateConfig> getEntityClazz() {
		return BsTemplateConfig.class;
	}

	@Override
	public List<BsTemplateConfig> findByTemplateCat(String templateCat) {
		// TODO Auto-generated method stub
		List<BsTemplateConfig> list = bsTemplateConfigDao.findByTemplateCat(templateCat);
		return list;
	}

	@Override
	public Map<String, List<DictDataVo>> getTemplateMap(TemplateQueryVo queryVo) {
		// TODO Auto-generated method stub
		String typeCd = queryVo.getTypeCd();
		String templateCat = queryVo.getTemplateCat();
		String lang = queryVo.getLang();
		List<String> dictCds = queryVo.getDictCdList();
		return FormConfigUtil.findByTypeCd(templateCat,typeCd,lang,dictCds);
	}

	@Override
	public List<DictDataVo> getTemplateByDictCd(TemplateQueryVo queryVo) {
		// TODO Auto-generated method stub
		String typeCd = queryVo.getTypeCd();
		String templateCat = queryVo.getTemplateCat();
		String lang = queryVo.getLang();
		String dictCd = queryVo.getDictCd();
		return FormConfigUtil.findByTypeCd(templateCat,typeCd,lang,dictCd);
	}
	
	@Override
	public  Map<String, String> findTemplateValue(TemplateQueryVo queryVo) {
		String typeCd = queryVo.getTypeCd();
		String templateCat = queryVo.getTemplateCat();
		return FormConfigUtil.findTemplateValue(templateCat,typeCd);
	}
	
}

