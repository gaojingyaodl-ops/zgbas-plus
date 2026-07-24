package com.spt.bas.purchase.wx.server.api;

import com.spt.bas.purchase.wx.client.constant.SaveInfoType;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.server.dao.SaveInfoDao;
import com.spt.bas.purchase.wx.server.service.ITempSaveInfoService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-10 10:09
 */
@RestController
@RequestMapping(value = "purchase/saveTemp")
public class SaveTempApi extends BaseApi<SaveInfo> {
    @Autowired
    private ITempSaveInfoService iTempSaveInfoService;

    @Override
    public IDataService<SaveInfo> getService() {
        return iTempSaveInfoService;
    }

    /**
     * 临时保存信息提交
     *
     * @param type
     * @param userId
     */
    @RequestMapping(value = "commitTempInfo")
    void commitTempInfo(String type, Long userId) {
        iTempSaveInfoService.commitAppletNotify(type, userId);
    }

    /**
     * 获取委托授权内容
     *
     * @param wxUserId
     * @return
     */
    @RequestMapping(value = "getEntrustInfo")
    SaveInfo getEntrustInfo(@RequestBody Long wxUserId) {
        return iTempSaveInfoService.getAppletNotify(wxUserId, SaveInfoType.ENTRUST.getType());
    }

    /**
     * 根据保存类型获取内容
     *
     * @param wxUserId
     * @param saveType
     * @return
     */
    @RequestMapping(value = "getInfoByType")
    SaveInfo getInfoByType(Long wxUserId, String saveType) {
        return iTempSaveInfoService.getAppletNotify(wxUserId, saveType);
    }

    /**
     * 查询临时保存信息
     *
     * @param companyId 公司id
     * @param isCommit  是否已提交
     * @param types     类型 : 2,3
     * @return
     */
    @RequestMapping(value = "getInfosByCompanyId")
    List<SaveInfo> getInfosByCompanyId(@RequestParam("companyId") Long companyId,
                                       @RequestParam("isCommit") Boolean isCommit,
                                       @RequestParam("types") String types) {
        return iTempSaveInfoService.getInfosByCompanyId(companyId, isCommit, types);
    }

    /**
     * 查询临时保存信息
     *
     * @param companyId 公司id
     * @param isCommit  是否已提交
     * @param type      类型
     * @return
     */
    @RequestMapping(value = "getInfoByCompanyId")
    SaveInfo getInfoByCompanyId(@RequestParam("companyId") Long companyId,
                                @RequestParam("isCommit") Boolean isCommit,
                                @RequestParam("type") String type) {
        return iTempSaveInfoService.getInfoByCompanyId(companyId, isCommit, type);
    }

    @RequestMapping(value = "getInfoByCompanyId2")
    SaveInfo getInfoByCompanyId2(@RequestParam("companyId") Long companyId,
                                 @RequestParam("type") String type) {
        return iTempSaveInfoService.getInfoByCompanyId(companyId, type);
    }


}
