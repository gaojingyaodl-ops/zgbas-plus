package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/bs/companyDcsx", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IBsCompanyDcsxClient extends  BaseClient<BsCompanyDcsx> {

    @PostMapping("findByCompanyName")
    BsCompanyDcsx findByCompanyName(@RequestParam("companyName") String companyName);

    @PostMapping("findByCompanyCd")
    BsCompanyDcsx findByCompanyCd(@RequestParam("companyCd") String companyCd);

    @PostMapping("getCompanyConfigMap")
    Map<String, BsCompanyDcsx> getCompanyConfigMap();

    @PostMapping("findDcsxCompanyList")
    List<BsCompanyDcsx> findDcsxCompanyList();

    @PostMapping(value = "importPiccInsuranceExcel")
    public List<String> importPiccInsuranceExcel(@RequestBody String fileId);

}

