package com.spt.bas.server.api;


import com.spt.bas.client.entity.SysQuickSearch;
import com.spt.bas.client.vo.SysQuickSearchVo;
import com.spt.bas.server.service.ISysQuickSearchService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "sys/quick/search")
public class SysQuickSearchApi extends BaseApi<SysQuickSearch> {

    @Autowired
    private ISysQuickSearchService sysQuickSearchService;

    @PostMapping("findListByUserIdAndModuleUrl")
    List<SysQuickSearch> findListByUserIdAndModuleUrl(@RequestBody SysQuickSearchVo searchVo){
        return sysQuickSearchService.findListByUserIdAndModuleUrl(searchVo);
    }

    @Override
    public IDataService<SysQuickSearch> getService() {
        return sysQuickSearchService;
    }
}
