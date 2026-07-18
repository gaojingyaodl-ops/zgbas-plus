package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptSummaryResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;
import com.spt.bas.report.server.service.IRptSummaryRoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:53
 */
@RequestMapping("/summaryRoi")
@RestController
public class RptSummaryRoiApi {

    @Autowired
    private IRptSummaryRoiService summaryRoiService;


    @PostMapping("/findPage")
    public RptSummaryResultVo findPage(@RequestBody RptUserRoiVo vo){
        return summaryRoiService.findPage(vo);
    }
}
