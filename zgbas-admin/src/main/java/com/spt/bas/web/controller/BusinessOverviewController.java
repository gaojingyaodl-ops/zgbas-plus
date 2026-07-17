package com.spt.bas.web.controller;

import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.entity.RptBusinessOverview;
import com.spt.bas.report.client.remote.IRptBusinessOverviewClient;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务总览
 */
@Controller
@RequestMapping(value = "/business/overview")
public class BusinessOverviewController {
    @Autowired
    private IRptBusinessOverviewClient businessOverviewClient;

    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("businessTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_BUSINESS_TYPE)));
        //我方抬头
        model.addAttribute("ourCompanyJson",
                JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        model.addAttribute("businessZlPerm", ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())); 
        return "index/businessOverview";
    }
    
    @RequestMapping(value = "queryList")
    public void queryList(RptBusinessOverviewSearchVo searchVo, HttpServletResponse response) {
        String businessType = searchVo.getBusinessType();
        if(StringUtils.equals(BasConstants.DICT_TYPE_BUSINESS_SX,businessType)){
            searchVo.setMatchCreditFlg(true);
        }
        if(StringUtils.equals(BasConstants.DICT_TYPE_BUSINESS_DC,businessType)){
            searchVo.setMatchCreditFlg(false);
        }
        boolean businessZlPerm = ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode());
        searchVo.setBusinessZlPerm(businessZlPerm);
        if (businessZlPerm) {
            String dictLabel = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_ASSISTANT_DICT, ShiroUtil.getCurrentUserId() + "");
            List<Long> deptIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(dictLabel)) {
                String[] split = dictLabel.split(",");
                // 使用 Stream 将 String 数组转换为 List<Long>
                deptIdList = java.util.Arrays.stream(split)
                        .map(Long::parseLong)  // 将 String 转换为 Long
                        .collect(Collectors.toList());  // 收集到 List<Long>

            } else {
                deptIdList.add(-1L);
            }
            searchVo.setDeptIdList(deptIdList);
        }
        
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
        List<Long> hgMatchUserIdList = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listByCategory)) {
            for (BsDictData bsDictData : listByCategory) {
                try {
                    String dictCd = bsDictData.getDictCd();
                    Long matchUserId = Long.valueOf(dictCd);
                    hgMatchUserIdList.add(matchUserId);
                } catch (Exception e) {
                }
            }
        }
        searchVo.setHgMatchUserIdList(hgMatchUserIdList);
        List<RptBusinessOverview> businessOverviewList = businessOverviewClient.findBusinessOverviewList(searchVo);
        JsonEasyUI.renderListJson(response, businessOverviewList);
    }
    

}
