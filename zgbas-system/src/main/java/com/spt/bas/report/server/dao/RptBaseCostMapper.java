package com.spt.bas.report.server.dao;


import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.vo.RptBaseCostVo;
import com.spt.bas.report.client.vo.RptBaseCostAndContractVo;
import com.spt.bas.report.client.vo.RptBaseCostReportVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface RptBaseCostMapper {



    List<RptBaseCostReportVo> selectBaseCostPage(RptBaseCostVo rptBaseCostVo);
    RptBaseCostReportVo selectBaseCostSum(RptBaseCostVo rptBaseCostVo);
    
    
    List<RptBaseCostReportVo> selectUserRoi(String baseStartDate, String baseEndDate, List<Long> userList);
    List<CtrContractSettlement> selectContractSettlements(String baseStartDate, String baseEndDate);

    List<RptBaseCostAndContractVo> findBaseCostAndContract(@Param("baseDate") String baseDate, @Param("branchCd") String branchCd, @Param("branchCds") List<String> branchCds);

    /**
     * 获取业务成本导入人员
     *
     * @param baseDate 年月
     * @return 人数
     */
    List<RptBaseCostReportVo> selectUser(String baseDate);
}
