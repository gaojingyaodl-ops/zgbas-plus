package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptUserRoiResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:52
 */
@FeignClient(name = ReportConstant.SERVER_NAME, path = ReportConstant.SERVER_NAME + "/userRoi", url = ReportConstant.SERVER_URL, configuration = FeignConfig.class)
public interface IRptUserRoiClient {

    @PostMapping("/findPage")
    List<RptUserRoiResultVo> findPage(@RequestBody RptUserRoiVo vo);

    /**
     * 合计
     *
     * @return 合计
     */
    @PostMapping("/getTotal")
    Map<String, Object> getTotal(@RequestBody RptUserRoiVo userRoiVo);

}
