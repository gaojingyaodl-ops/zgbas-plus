package com.spt.bas.server.api;

import com.spt.bas.client.entity.EvaluateItem;
import com.spt.bas.server.service.IEvaluateItemService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "evaluate/item")
public class EvaluateItemApi extends BaseApi<EvaluateItem> {
    @Autowired
    private IEvaluateItemService evaluateItemService;
    @Override
    public IDataService<EvaluateItem> getService() {
        return evaluateItemService;
    }

    @PostMapping("/selectEvaluateItemsByIds")
    public List<EvaluateItem> selectEvaluateItemsByIds(@RequestParam(value = "evaluateItemIds") String evaluateItemIds){
        return evaluateItemService.selectEvaluateItemsByIds(evaluateItemIds);
    }
}
