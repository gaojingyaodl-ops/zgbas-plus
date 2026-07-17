package com.spt.bas.report.server.dao;

import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.report.client.entity.RptCompanyCreditInfo0;
import com.spt.bas.report.client.vo.RptCompanyCreditVo;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditQueryVo;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditVo;
import com.spt.bas.report.client.vo.RptPartBsCompanyVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface RptBsCompanyMapper {
	
	List<RptPartBsCompanyVo> findCompanyList(RptPartBsCompanyVo vo);
	
	RptPartBsCompanyVo findCompanyById(RptPartBsCompanyVo vo);
	
	List<RptPartBsCompanyVo> findCompany(RptPartBsCompanyVo vo);

	int countCompanyByName(String companyName);

	List<Long> getRelationShipApproveIdByCompanyId(Long matchUserId);

	List<Long> getRelationShipApproveIdByCompanyIds(@Param("matchUserIds") List<Long> matchUserIds);

	List<RptPartBsCompanyVo> findAllCompany();

	List<RptCompanyCreditVo> findCompanyCredit(RptCompanyCreditVo vo);

    List<RptCompanyCreditInfo0> getCompanyCreditInfo0();

	List<BsCompanyCredit> findAllCompanyCredit0();

	List<RptOpenCompanyCreditVo> findOpenCreditList(RptOpenCompanyCreditQueryVo vo);
}
