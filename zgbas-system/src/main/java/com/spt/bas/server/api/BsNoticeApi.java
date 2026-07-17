package com.spt.bas.server.api;


import com.spt.bas.client.entity.BsNotice;

import com.spt.bas.server.service.IBsNoticeService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "bs/bsNotice")
public class BsNoticeApi  extends BaseApi<BsNotice> {
    @Autowired
    private IBsNoticeService bsNoticeService;
    @Override
    public IDataService<BsNotice> getService() {
        return bsNoticeService;
    }

    @RequestMapping("findLast")
    public BsNotice findLast(){
        return bsNoticeService.findLast();
    }

    @RequestMapping("findLimit5")
    public   List<BsNotice> findLimit5(@RequestBody String deptId){
       return  bsNoticeService.findLimit5(deptId);
    }


    @RequestMapping("findLimit")
    public   List<BsNotice> findLimit(){
        return  bsNoticeService.findLimit();
    }
}
