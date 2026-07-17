package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptLeaderboard;
import com.spt.bas.report.client.vo.RptLeaderboardSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptWeChatWorkMapper {
	
 List<RptLeaderboard> findLeaderBoardMatchUserGroupList(RptLeaderboardSearchVo searchVo);

 List<RptLeaderboard> findLeaderBoardDeptGroupList(RptLeaderboardSearchVo searchVo);

 RptLeaderboard findLeaderBoardTotalGrossProfitAmount(RptLeaderboardSearchVo searchVo);

 List<RptLeaderboard> findLeaderBoardCustomerDevelopMatchList(RptLeaderboardSearchVo searchVo);

 RptLeaderboard findLeaderBoardCustomerDevelopTotalGrossProfitAmount(RptLeaderboardSearchVo searchVo);
}
