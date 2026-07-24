package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.dao.SaveInfoDao;
import com.spt.bas.purchase.wx.server.dao.UserDetailDao;
import com.spt.bas.purchase.wx.server.service.ITempSaveInfoService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  临时保存信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-18 17:45
 */
@Component
@Transactional(readOnly = true)
public class TempSaveInfoService extends BaseService<SaveInfo> implements ITempSaveInfoService {

    @Autowired
    private SaveInfoDao saveInfoDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Override
    public BaseDao<SaveInfo> getBaseDao() {
        return saveInfoDao;
    }

    /**
     * 获取临时保存信息
     *
     * @param userId
     * @return
     */
    @Override
    public SaveInfo getAppletNotify(Long userId, String type) {
        return saveInfoDao.findByUserIdAndTypeAndCommitFlg(userId, type, false);
    }

    /**
     * 查询临时保存信息
     *
     * @param companyId
     * @param isCommit
     * @param types
     * @return
     */
    @Override
    public List<SaveInfo> getInfosByCompanyId(Long companyId, Boolean isCommit, String types) {
        if (companyId == null) {
            throw new BaseException(Status.PARAM_NOT_NULL);
        }
        List<String> typeArray = new ArrayList<>();
        if (!StringUtils.isEmpty(types) && types.split(",").length > 0) {
            typeArray = Arrays.asList(types.split(","));
        }
        if (isCommit == null) {
            return saveInfoDao.findByCompanyIdAndTypeIn(companyId, typeArray);
        }
        return saveInfoDao.findByCompanyIdAndCommitFlgAndTypeIn(companyId, isCommit, typeArray);
    }

    /**
     * 查询临时保存信息
     *
     * @param companyId
     * @param isCommit
     * @param type
     * @return
     */
    @Override
    public SaveInfo getInfoByCompanyId(Long companyId, Boolean isCommit, String type) {
        if (StringUtils.isEmpty(type)) {
            throw new BaseException(Status.PARAM_NOT_NULL);
        }
        isCommit = isCommit == null ? false : isCommit;
        return saveInfoDao.findTopByCompanyIdAndTypeAndCommitFlgOrderByCreatedDateDesc(companyId, type, isCommit);
    }
    public SaveInfo getInfoByCompanyId(Long companyId, String type) {
        if (StringUtils.isEmpty(type)) {
            throw new BaseException(Status.PARAM_NOT_NULL);
        }
        return saveInfoDao.findTopByCompanyIdAndTypeOrderByCreatedDateDesc(companyId, type);
    }

    /**
     * 提交临时保存信息
     * @param type
     * @param userId
     */
    @Override
    @Transactional
    public void commitAppletNotify(String type, Long userId) {
        SaveInfo appletNotify = getAppletNotify(userId, type);
        SaveInfo saveInfo = saveInfoDao.findByUserIdAndTypeAndCommitFlg(userId, type, true);
        if (saveInfo == null) {
            saveInfo = new SaveInfo();
        }
        saveInfo.setContent(appletNotify.getContent());
        saveInfo.setType(type);
        saveInfo.setUserId(userId);
        saveInfo.setCommitFlg(true);
        saveInfoDao.save(saveInfo);
    }



}
