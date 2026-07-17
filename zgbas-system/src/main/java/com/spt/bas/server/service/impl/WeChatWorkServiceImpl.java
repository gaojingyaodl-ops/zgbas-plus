package com.spt.bas.server.service.impl;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.vo.WeChatWorkVo;
import com.spt.bas.report.client.remote.IRptWeChatWorkClient;
import com.spt.bas.server.service.IWeChatWorkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WeChatWorkServiceImpl implements IWeChatWorkService {

    @Autowired
    private IRptWeChatWorkClient weChatWorkClient;


    @Override
    public void pushWeChatWorkLeaderboard() {

        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_REGION_CONTRAST_WECHAT);
        List<Long> deptIdList = new ArrayList<>();
        for (BsDictData bsDictData : listByCategory) {
            if (!StringUtils.contains(bsDictData.getDictName(), "化工")) {
                deptIdList.add(Long.valueOf(bsDictData.getDictCd()));
            }
        }
        WeChatWorkVo vo = new WeChatWorkVo();
        vo.setDeptIdList(deptIdList);
        weChatWorkClient.pushWeChatWorkLeaderboard(vo);

    }

    /**
     * 推送客户开发业绩排行榜到企业微信
     */
    @Override
    public void pushWeChantWorkLeaderboardForCustomerDevelop() {
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_REGION_CONTRAST_WECHAT);
        List<Long> deptIdList = listByCategory.stream()
                .filter(bsDictData -> (StringUtils.contains(bsDictData.getDictName(), "客户发展") || StringUtils.contains(bsDictData.getDictName(), "改性塑料")))
                .map(bsDictData -> Long.parseLong(bsDictData.getDictCd()))
                .collect(Collectors.toList());
        WeChatWorkVo vo = new WeChatWorkVo();
        vo.setDeptIdList(deptIdList);
        weChatWorkClient.pushWeChantWorkLeaderboardForCustomerDevelop(vo);
    }
}
