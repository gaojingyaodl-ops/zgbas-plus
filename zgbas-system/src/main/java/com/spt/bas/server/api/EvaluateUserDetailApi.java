package com.spt.bas.server.api;

import com.spt.bas.client.entity.EvaluateUserDetail;
import com.spt.bas.server.service.IEvaluateUserDetailService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "evaluate/user/detail")
public class EvaluateUserDetailApi extends BaseApi<EvaluateUserDetail> {
    @Autowired
    private IEvaluateUserDetailService evaluateUserDetailService;
    @Override
    public IDataService<EvaluateUserDetail> getService() {
        return evaluateUserDetailService;
    }

    /**
     * 根据 ids 查询数据
     * @param ids ids
     * @return 数据
     */
    @PostMapping("/selectEvaluateUserDetailByIds")
    List<EvaluateUserDetail> selectEvaluateUserDetailByIds(@RequestParam(value = "ids") String ids){
        return evaluateUserDetailService.selectEvaluateUserDetailByIds(ids);
    }

    @PostMapping("/selectDetailByEvaluateUserId")
    List<EvaluateUserDetail> selectDetailByEvaluateUserId(@RequestParam(value = "ids") String ids){
        return evaluateUserDetailService.selectDetailByEvaluateUserId(ids);
    }
}
