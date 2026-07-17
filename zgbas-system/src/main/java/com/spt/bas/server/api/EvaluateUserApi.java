package com.spt.bas.server.api;

import com.spt.bas.client.entity.EvaluateUser;
import com.spt.bas.client.vo.EvaluateSearchVo;
import com.spt.bas.client.vo.EvaluateStartVo;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.bas.server.service.IEvaluateUserService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "evaluate/user")
public class EvaluateUserApi extends BaseApi<EvaluateUser> {
    @Autowired
    private IEvaluateUserService evaluateUserService;
    @Override
    public IDataService<EvaluateUser> getService() {
        return evaluateUserService;
    }

    @PostMapping("/findPageBySearch")
    Page<EvaluateUser> findPageBySearch(@RequestBody EvaluateSearchVo searchVo){
        return evaluateUserService.findPageBySearch(searchVo);
    }

    /**
     * 发起考评
     * @param vo
     */
    @RequestMapping(value = "startEvaluate")
    public void startEvaluate(@RequestBody EvaluateStartVo vo){
        evaluateUserService.startEvaluate(vo);
    }

    /**
     * 查询考评
     * @param vo
     */
    @RequestMapping(value = "findAllByEvaluateMonthAndDeptId")
    public List<EvaluateUser> findAllByEvaluateMonthAndDeptId(@RequestBody EvaluateSearchVo vo){
        return evaluateUserService.findAllByEvaluateMonthAndDeptId(vo.getEvaluateMonth(),vo.getDeptId());
    }
    /**
     * 查询考评
     * @param vo
     */
    @RequestMapping(value = "findAllByEvaluateMonthAndUserId")
    public List<EvaluateUser> findAllByEvaluateMonthAndUserId(@RequestBody EvaluateSearchVo vo){
        return evaluateUserService.findAllByEvaluateMonthAndUserId(vo.getEvaluateMonth(),vo.getUserId());
    }

    /**
     * 根据evaluateUserId 批量查询数据
     * @param evaluateUserIds id字符串，用英文逗号分割
     * @return 结果集
     */
    @PostMapping("/selectDataByIds")
    public List<EvaluateUser> selectDataByIds(@RequestParam(value = "evaluateUserIds")String evaluateUserIds){
        return evaluateUserService.selectDataByIds(evaluateUserIds);
    }

    @PostMapping("/approveWaitDeal")
    void approveWaitDeal(@RequestBody EvaluateUserApproveWaitDealVo approveWaitDealVo){
        evaluateUserService.approveWaitDeal(approveWaitDealVo);
    }

}
