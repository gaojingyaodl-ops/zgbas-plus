package com.spt.bas.server.api;

import com.spt.bas.client.entity.PiccAvailableRecord;
import com.spt.bas.server.service.IPiccAvailableRecordService;
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
 * @Date: Created in 2021-04-25 15:24
 */
@RestController
@RequestMapping(value = "bs/piccAvailableRecord")
public class PiccAvailableRecordApi extends BaseApi<PiccAvailableRecord> {

    @Autowired
    private IPiccAvailableRecordService piccAvailableRecordService;

    @Override
    public IDataService<PiccAvailableRecord> getService() {
        return piccAvailableRecordService;
    }



}
