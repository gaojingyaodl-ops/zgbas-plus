package com.spt.bas.server.api;

import com.spt.bas.client.entity.DataScore;
import com.spt.bas.server.service.IDataScoreService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-20 09:51
 */
@RestController
@RequestMapping(value = "bs/dataScore")
public class DataScoreApi extends BaseApi<DataScore> {
    @Autowired
    private IDataScoreService dataScoreService;
    @Override
    public IDataService<DataScore> getService() {
        return dataScoreService;
    }


}
