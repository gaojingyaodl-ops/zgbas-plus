package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptUserRoiResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;
import com.spt.bas.report.server.service.IRptUserRoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:53
 */
@RequestMapping("/userRoi")
@RestController
public class RptUserRoiApi {

    @Autowired
    private IRptUserRoiService userRoiService;


    @PostMapping("/findPage")
    public List<RptUserRoiResultVo> findPage(@RequestBody RptUserRoiVo vo){
        return userRoiService.findPage(vo);
    }

    @PostMapping("/getTotal")
    Map<String, Object> getTotal(@RequestBody RptUserRoiVo userRoiVo){
        return userRoiService.getTotal(userRoiVo);
    }
}
