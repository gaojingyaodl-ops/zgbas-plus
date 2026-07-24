package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface ITempSaveInfoService extends IBaseService<SaveInfo> {
    /**
     * 提交临时保存信息
     * @param type
     * @param userId
     */
    void commitAppletNotify(String type, Long userId);

    /**
     * 获取临时保存信息
     * @param userId
     * @param type
     * @return
     */
    SaveInfo getAppletNotify(Long userId, String type);

    /**
     * 查询临时保存信息
     * @param companyId
     * @param isCommit
     * @param types
     * @return
     */
    List<SaveInfo> getInfosByCompanyId(Long companyId, Boolean isCommit, String types);

    /**
     * 查询临时保存信息
     * @param companyId
     * @param isCommit
     * @param type
     * @return
     */
    SaveInfo getInfoByCompanyId(Long companyId, Boolean isCommit, String type);
    SaveInfo getInfoByCompanyId(Long companyId, String type);
}
