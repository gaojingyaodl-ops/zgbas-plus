package com.spt.bas.server.service;

import com.spt.bas.client.entity.EvaluateUser;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.bas.client.vo.EvaluateSearchVo;
import com.spt.bas.client.vo.EvaluateStartVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IEvaluateUserService extends IBaseService<EvaluateUser> {
    /**
     * 查询考核人员明细
     * @param searchVo 查询参数
     * @return 查询结构
     */
    Page<EvaluateUser> findPageBySearch(EvaluateSearchVo searchVo);
    public void startEvaluate(@RequestBody EvaluateStartVo vo);

    List<EvaluateUser> findAllByEvaluateMonthAndDeptId(String evaluateMonh, Long deptId);
    List<EvaluateUser> findAllByEvaluateMonthAndUserId(String evaluateMonh,Long useriId);

    /**
     * 根据evaluateUserId 批量查询数据
     * @param evaluateUserIds id字符串，用英文逗号分割
     * @return 结果集
     */
    List<EvaluateUser> selectDataByIds(String evaluateUserIds);

    /**
     * 添加代办事项
     * @param approveWaitDealVo
     */
    void approveWaitDeal(EvaluateUserApproveWaitDealVo approveWaitDealVo);
}
