/**
 *
 */
package com.spt.bas.web.controller.bs;


import com.google.common.collect.Maps;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 历史罚息
 *
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/bs/penaltyInterest")
public class PenaltyInterestController extends SingleCrudControll<PenaltyInterest, BaseVo> {
    @Autowired
    private IPenaltyInterestClient penaltyInterestClient;

    @Override
    public BaseClient<PenaltyInterest> getService() {
        return penaltyInterestClient;
    }

    @RequestMapping(value = "findPenaltyInterestPage/{bizId}")
    public void findPenaltyInterestPage(@PathVariable("bizId") Long bizId,PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map = Maps.newHashMap();
        map.put("EQL_bizId",bizId);
        searchVo.setSearchParams(map);

        PageDown<PenaltyInterest> page = penaltyInterestClient.findPage(searchVo);
        if (!CollectionUtils.isEmpty(page.getContent())){
            page  =  findPageContent(page);
        }
        JsonEasyUI.renderJson(response, page);
    }

    public PageDown<PenaltyInterest> findPageContent(PageDown<PenaltyInterest>  page){
        List<PenaltyInterest> list = new ArrayList<>();
        List<PenaltyInterest> content = page.getContent();
        PenaltyInterest interest = content.get(0);
        String interestContractNo = interest.getInterestContractNo();
        String[] contractNoArray = interestContractNo.split(",");
        String interestAmount = interest.getInterestAmount();
        String[] amountArray = interestAmount.split(",");

        for (int i = 0; i <contractNoArray.length ; i++) {
            PenaltyInterest entity = new PenaltyInterest();
            String  contractNo = contractNoArray[i];
            entity.setInterestContractNo(contractNo);
            String amount = amountArray[i];
            entity.setInterestAmount(amount);
            list.add(entity);
        }
        page.setContent(list);
        return page;
    }

}
