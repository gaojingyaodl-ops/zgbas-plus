package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyOphis;
import com.spt.bas.client.vo.BsCompanyOphisVo;
import com.spt.bas.client.vo.CompanyStatusVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.dao.BsCompanyOphisDao;
import com.spt.bas.server.service.IBsCompanyOphisService;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Transactional(readOnly = true)
public class BsCompanyOphisServiceImpl extends BaseService<BsCompanyOphis> implements IBsCompanyOphisService {
	@Autowired
	private BsCompanyOphisDao bsCompanyOphisDao;
	@Autowired
	private BsCompanyDao bsCompanyDao;
	@Override
	public BaseDao<BsCompanyOphis> getBaseDao() {
		return bsCompanyOphisDao;
	}
	
	@Override
	public Class<BsCompanyOphis> getEntityClazz() {
		return BsCompanyOphis.class;
	}

	@Override
	public Boolean haveFllowByUser(CompanyStatusVo companyVo) {
		// TODO Auto-generated method stub
		//查询该用户两个月内是否有跟进过该公司
		Map<String, Object> map = new HashMap<>();
		Date nowDate = new Date();
		//两个月前的一天
		Date compareDate = DateOperator.addMonthes(nowDate, -2);
		
		map.put("EQL_createUserId", companyVo.getCreateUserId());
		map.put("EQL_companyId", companyVo.getId());
		//跟进的公司在2个月的以内的
		map.put("GTE_createdDate", compareDate);
		map.put("EQS_remark", "system");
	    Specification<BsCompanyOphis> spec = WebUtil.buildSpecification(map);
		long count = bsCompanyOphisDao.count(spec);
		if(count>0){	
			return false;
		}else{
			return true;
		}
	}

	@Override
	@ServerTransactional
	public void addCompanyHis(BsCompanyOphisVo opHis) {
		String option = opHis.getOptionType();
		BsCompany bsCompany = bsCompanyDao.findOne(opHis.getCompanyId());
		String remark = "";
		if(StringUtils.isBlank(option)){
			if(opHis.getStatus().equals(BasConstants.COMPANY_STATUS_N)){
				remark = BasConstants.COMPANY_N;
			}else if(opHis.getStatus().equals(BasConstants.COMPANY_STATUS_F)){
				remark = BasConstants.COMPANY_F;
			}
		//指派
		}else if(option.equals(BasConstants.COMPANY_STATUS_Z)){
			remark = BasConstants.COMPANY_Z;
		//共享
		}else if(option.equals(BasConstants.COMPANY_STATUS_S)){
			remark = BasConstants.COMPANY_S;
		//新增
		}else if(option.equals(BasConstants.COMPANY_STATUS_T)){
			remark = BasConstants.COMPANY_T;
		//修改
		}else if(option.equals(BasConstants.COMPANY_STATUS_U)){
			remark = BasConstants.COMPANY_U;
		//退回共享
		}else if(option.equals(BasConstants.COMPANY_STATUS_O)){
			remark = BasConstants.COMPANY_O;
		//逾期
		}else if(option.equals(BasConstants.COMPANY_STATUS_R)){
			remark = BasConstants.COMPANY_R;
		//审核塑料分类
		}else if(option.equals(BasConstants.COMPANY_STATUS_P)){
			remark = BasConstants.COMPANY_P;
		}
		BsCompanyOphis his = new BsCompanyOphis();
		BeanUtils.copyProperties(opHis, his);
		StringBuilder hisMsg = new StringBuilder();
		hisMsg.append(remark);
		if(option!=null){
			if(option.equals(BasConstants.COMPANY_STATUS_S) || option.equals(BasConstants.COMPANY_STATUS_Z) || 
				option.equals(BasConstants.COMPANY_STATUS_R)){
				hisMsg.append("[").append(his.getRemark()).append("]");
				hisMsg.append(bsCompany==null?"":"["+bsCompany.getCompanyName()+"]");
			} else {
				hisMsg.append(opHis.getRemark());
			}
		} else {
			hisMsg.append(opHis.getRemark());
		}
//		hisMsg.append(bsCompany==null?"null":bsCompany.getCompanyName()).append("/");
//		hisMsg.append(bsCompany==null?"null":bsCompany.getAddress()).append("/");
//		hisMsg.append(bsCompany==null?"null":bsCompany.getContactName()).append("]");
//		hisMsg.append(remark).append(opHis.getRemark());
		his.setRemark(hisMsg.toString());
		bsCompanyOphisDao.save(his);
	}
}

