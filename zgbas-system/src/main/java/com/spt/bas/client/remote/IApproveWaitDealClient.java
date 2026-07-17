package com.spt.bas.client.remote;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApproveWaitDeal;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApproveWaitDealVo;
import com.spt.bas.client.vo.ApproveWaitSearchVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME+"/wait/deal", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApproveWaitDealClient extends BaseClient<ApproveWaitDeal> {

    @PostMapping(value = "/findPageWaitDeal")
    PageDown<ApproveWaitDeal> findPageWaitDeal(@RequestBody PageSearchVo searchVo);

    @PostMapping(value = "/findPageWaitDealById")
    PageDown<ApproveWaitDeal> findPageWaitDealById(@RequestBody PageSearchVo searchVo);

    @PostMapping(value = "/updateStatus")
    void updateStatus(@RequestBody ApproveWaitSearchVo searchVo);

    @PostMapping(value = "/updateFlg")
    void updateFlg(@RequestBody ApproveWaitSearchVo searchVo);

    @PostMapping(value = "/findPageWaitDealCount")
    List<ApproveWaitDeal> findPageWaitDealCount(@RequestBody ApproveWaitSearchVo searchVo);

    @PostMapping(value = "/findSubject")
    String findSubject(@RequestBody ApproveWaitSearchVo searchVo);

    @PostMapping(value = "/doContractSaveWaitDeal")
    void doContractSaveWaitDeal(@RequestBody CtrContract searchVo);

    // 添加待办事项
    @PostMapping("/saveWaitDeal")
    void saveWaitDeal(@RequestBody ApproveWaitDealVo vo);

    @PostMapping("/getUserWaitDealNum")
    Long getUserWaitDealNum(@RequestBody ApproveWaitSearchVo searchVo);
}
