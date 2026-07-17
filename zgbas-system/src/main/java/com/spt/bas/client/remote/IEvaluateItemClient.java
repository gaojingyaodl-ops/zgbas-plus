package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.EvaluateItem;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/evaluate/item",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IEvaluateItemClient extends BaseClient<EvaluateItem> {
    /**
     * 根据id批量查询数据
     * @param evaluateItemIds id，用逗号分割
     * @return
     */
    @PostMapping("/selectEvaluateItemsByIds")
    List<EvaluateItem> selectEvaluateItemsByIds(@RequestParam(value = "evaluateItemIds") String evaluateItemIds);
}
