package com.spt.bas.server.service;


import com.spt.bas.client.entity.SysQuickSearch;
import com.spt.bas.client.vo.SysQuickSearchVo;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface ISysQuickSearchService extends IBaseService<SysQuickSearch> {
    
    List<SysQuickSearch> findListByUserIdAndModuleUrl(SysQuickSearchVo searchVo);

}
