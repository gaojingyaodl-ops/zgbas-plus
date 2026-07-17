package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsEntrust;
import com.spt.bas.server.service.IBsEntrustService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-28 11:05
 */
@RestController
@RequestMapping(value = "api/bsEntrust")
public class BsEntrustApi extends BaseApi<BsEntrust> {

    @Autowired
    private IBsEntrustService bsEntrustService;

    @Override
    public IDataService<BsEntrust> getService() {
        return bsEntrustService;
    }

    @PostMapping(value = "findByWxUserId")
    public BsEntrust findByWxUserId(@RequestBody Long wxUserId) {
        return bsEntrustService.findByWxUserId(wxUserId);
    }

    @PostMapping(value = "findByCompanyId")
    List<BsEntrust> findByCompanyId(@RequestBody Long companyId){
        return bsEntrustService.findByCompanyId(companyId);
    };
}
