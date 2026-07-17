package com.spt.pm.service.impl;

import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.BsKeySequenceDao;
import com.spt.pm.entity.BsKeySequence;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.dao.CommonDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Transactional(readOnly = true)
public class BsKeySequenceServiceImpl extends BaseService<BsKeySequence> implements IBsKeySequenceService {
	@Autowired
	private BsKeySequenceDao bsKeySequenceDao;
	@Autowired
	private CommonDao commonDao;

	@Override
	public BaseDao<BsKeySequence> getBaseDao() {
		return bsKeySequenceDao;
	}

	@Override
	public Class<BsKeySequence> getEntityClazz() {
		return BsKeySequence.class;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public String getNextKey(String category, Long enterpriseId) {
		return generateKey(category, enterpriseId, "");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public String getNextKey(String category, Long enterpriseId, String deptAbbr) {
		return generateKey(category, enterpriseId, deptAbbr);
	}

	private String generateKey(String category, Long enterpriseId, String deptAbbr){
		if (StringUtils.isBlank(category)){
			return "";
		}
		StringBuilder tmp = new StringBuilder();
		BsKeySequence entity = nextKey(category, enterpriseId);
		if (Objects.isNull(entity)){
			return null;
		}
		String prefix = entity.getKeyPrefix();
		if (prefix.contains("{0}")) {
			prefix = MessageFormat.format(prefix, deptAbbr);
		}
		String fmt = String.format("%0" + entity.getSeqLenth() + "d", entity.getMaxValue());
		return tmp.append(prefix).append(StringUtils.trimToEmpty(entity.getLastDateVal())).append(fmt).toString();
	}

	private synchronized BsKeySequence nextKey(String category, Long enterpriseId) {
		BsKeySequence entity;
		if (Objects.nonNull(enterpriseId)) {
			entity = bsKeySequenceDao.findByEnterpriseIdAndKeyCategory(enterpriseId, category);
		} else {
			entity = bsKeySequenceDao.findByKeyCategory(category);
		}
		entity = bsKeySequenceDao.findById(entity.getId()).orElse(null);
		if (Objects.isNull(entity)) {
			return null;
		}
		long maxVal = 1L;
		String dateVal = null;
		if (StringUtils.isNotBlank(entity.getDateRule())) {
			dateVal = DateOperator.formatDate(new Date(), entity.getDateRule());
			if (StringUtils.equals(dateVal, entity.getLastDateVal())) {
				maxVal = entity.getMaxValue() + 1;
			}
		} else {
			maxVal = entity.getMaxValue() + 1;
		}
		entity.setMaxValue(maxVal);
		entity.setLastDateVal(dateVal);
		bsKeySequenceDao.save(entity);
		return entity;
	}

	@Override
	@ServerTransactional
	public boolean initKeySequence(String prefix, Long enterpriseId) {
		String sql = "{call up_init_keysequence(?,?)}";
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(0, String.valueOf(enterpriseId));
		map.put(1, prefix);
		boolean flag = commonDao.executeStoreProcedure(sql, map);
		return flag;
	}
}
