package com.spt.bas.report.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.report.client.vo.RptAssessmentResultVo;
import com.spt.bas.report.client.vo.RptAssessmentSearch;
import com.spt.bas.report.server.dao.RptCtrContractSettlementMapper;
import com.spt.bas.report.server.service.IRptAssessmentService;
import com.spt.bas.report.server.util.MyBigDecimalUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/4/25 15:53
 */
@Service
public class RptAssessmentServiceImpl implements IRptAssessmentService {

    @Autowired
    private RptCtrContractSettlementMapper contractSettlementMapper;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    /**
     * 查询月度业务员考核数据
     * 赊销：
     * 当月赊销利润 = 销售价 - 采购价 - 运杂费 - 供应链金融服务费 - 业务提成  （当月签订合同）
     * 代采：
     * 当月代采利润=销售价-采购价-运杂费-业务提成（当月签订合同）
     * @param selectParam 查询参数
     * @return 业务员月度考核数据
     */
    @Override
    public Page<RptAssessmentResultVo> selectAssessment(RptAssessmentSearch selectParam) {
        // 查询数据
        List<RptAssessmentResultVo> result = getAssessment(selectParam);
        // 内存分页
        Pageable pageable = PageRequest.of(selectParam.getPage() - 1, selectParam.getRows());
        return new PageImpl<>(result, pageable, selectParam.getCount());
    }

    /**
     * 获取考核数据
     * @param selectParam 查询参数
     * @return 考核数据
     */
    private List<RptAssessmentResultVo> getAssessment(RptAssessmentSearch selectParam) {

        // 如果部门不为空
        if(Objects.nonNull(selectParam.getDeptId())){
            // 根据部门找出部门下的业务员
            List<SysUserSdk> userList = authOpenFacade.findUserByDeptIdsButNoChild(Collections.singletonList(selectParam.getDeptId()));
            List<Long> userIdsList = userList.stream().map(SysUserSdk::getUserId).distinct().collect(Collectors.toList());
            // 如果部门下没有业务员，直接返回
            if(CollectionUtils.isEmpty(userList)){
                return new ArrayList<>();
            }
            selectParam.setUserIds(userIdsList);
        }
        List<RptAssessmentResultVo> assessmentResultVos = contractSettlementMapper.selectAssessment(selectParam);
        if(CollectionUtils.isEmpty(assessmentResultVos)){
            return new ArrayList<>();
        }
        List<Long> matchUserIds = assessmentResultVos.stream().map(RptAssessmentResultVo::getUserId).distinct().collect(Collectors.toList());
        List<SysUserSdk> matchUsers = authOpenFacade.findByUserIds(matchUserIds);
        Map<Long, SysUserSdk> matchUserMap = matchUsers.stream().filter(Objects::nonNull).collect(Collectors.toMap(SysUserSdk::getUserId, e -> e, (a, b) ->b));

        // 赋值操作
        for (RptAssessmentResultVo assessmentResultVo : assessmentResultVos) {
            Long matchUserId = assessmentResultVo.getUserId();
            if (Objects.nonNull(matchUserId)) {
                SysUserSdk sysUserSdk = matchUserMap.get(matchUserId);
                if(Objects.isNull(sysUserSdk)){
                    assessmentResultVo.setDeptName("");
                    assessmentResultVo.setEntryDate("");
                }else{
                    String deptName = Objects.nonNull(sysUserSdk.getDept()) ? sysUserSdk.getDept().getDeptName() : "";
                    assessmentResultVo.setDeptName(deptName);
                    String entryDate = DateUtil.format(sysUserSdk.getCreateTime(), "yyyy-MM-dd");
                    assessmentResultVo.setEntryDate(StringUtils.isBlank(entryDate) ? "" : entryDate);
                }
            }
        }
        return assessmentResultVos;
    }

    /**
     * 查询业务员季度考核表
     * 赊销：
     * 当季度赊销利润 = 销售价 - 采购价 - 运杂费 - 供应链金融服务费 - 业务提成（当季/年签订合同）
     * 当季度月平均赊销利润=销售价-采购价-运杂费-供应链金融服务费-业务提成（当季/年签订合同）
     * 代采：
     * 当季度代采利润 =销售价-采购价-运杂费-业务提成（当季/年签订合同）
     * 当季度月平均代采利润-销售价-采购价-运杂费-业务提成（当季/年签订合同）
     * @param selectParam 查询参数
     * @return 返回业务员季度考核表
     */
    @Override
    public Page<RptAssessmentResultVo> selectAssessmentQuarterOrYear(RptAssessmentSearch selectParam) {
        List<RptAssessmentResultVo> result = getAssessment(selectParam);
        for (RptAssessmentResultVo e : result) {
            if(selectParam.getQuarter()!=null){
                // 获取季度
                String quarter = getQuarter(selectParam,e.getCreatedDate());
                e.setQuarter(quarter);
            }else{
                // 获取年度
                String year = getYear(e.getCreatedDate());
                e.setYear(year);
            }
            // 获取月平均赊销额
            BigDecimal sellMoneyAverage = getAverage(selectParam,e.getSellMoney());
            // 获取月平均赊销利润
            BigDecimal sellMoneyProfitAverage = getAverage(selectParam,e.getSellMoneyProfit());
            // 获取代采金额
            BigDecimal buyMoneyAverage = getAverage(selectParam,e.getBuyMoney());
            // 获取月平均代采利润
            BigDecimal buyMoneyProfitAverage = getAverage(selectParam,e.getBuyMoneyProfit());
            // 获取月平均利润
            BigDecimal sumProfitMoneyAverage = getAverage(selectParam,e.getSumProfitMoney());
            e.setSellMoneyAverage(sellMoneyAverage);
            e.setSellMoneyProfitAverage(sellMoneyProfitAverage);
            e.setBuyMoneyAverage(buyMoneyAverage);
            e.setBuyMoneyProfitAverage(buyMoneyProfitAverage);
            e.setSumProfitMoneyAverage(sumProfitMoneyAverage);
        }
        // 内存分页
        Pageable pageable = PageRequest.of(selectParam.getPage() - 1, selectParam.getRows());
        return new PageImpl<>(result, pageable, selectParam.getCount());
    }



    /**
     * 获取月平均赊销利润
     * @param selectParam 查询参数
     * @param money 赊销利润
     * @return 月平均赊销利润
     */
    private BigDecimal getAverage(RptAssessmentSearch selectParam, BigDecimal money) {
        // 如果季度不为空说明是季度
        return selectParam.getQuarter() != null ? MyBigDecimalUtils.divide(money,3,2) : MyBigDecimalUtils.divide(money,12,2);
    }

    /**
     * 获取季度
     * @param selectParam 查询参数
     * @param createdDate 合同月份
     * @return 季度
     */
    private String getQuarter(RptAssessmentSearch selectParam, String createdDate) {
        String year = getYear(createdDate);
        return year+"年"+selectParam.getQuarter()+"季度";
    }

    /**
     * 获取年度
     * @param createdDate 合同日期 年-月
     * @return 年
     */
    private String getYear(String createdDate) {
        String[] split = createdDate.split("-");
        return split[0];
    }
}
