package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserDetailDao extends BaseDao<UserDetail> {

    @Query(nativeQuery = true, value = "SELECT d.* FROM t_wx_company_user_detail d WHERE d.user_id = ?1 AND d.enable_flg = ?2 ORDER BY d.updated_date DESC LIMIT 1")
    UserDetail findByUserIdAndEnableFlg(Long userId, Boolean enableFlg);

    @Query(nativeQuery = true, value = "SELECT d.* FROM t_wx_company_user_detail d WHERE d.user_id = ?1 AND d.enable_flg = TRUE ORDER BY d.updated_date DESC LIMIT 1")
    UserDetail findByUserIdAndEnableFlgTrue(Long userId);

    @Query(nativeQuery = true, value = "SELECT d.* FROM t_wx_company_user_detail d WHERE d.user_id = ?1 AND d.enable_flg = TRUE AND d.is_bind = TRUE ORDER BY d.updated_date DESC LIMIT 1")
    UserDetail findByUserIdAndEnableFlgTrueAndIsBindTrue(Long userId);

    @Query(nativeQuery = true, value = "SELECT d.* FROM t_wx_company_user_detail d WHERE d.company_id = ?1 AND d.is_bind = TRUE ORDER BY d.updated_date DESC LIMIT 1")
    UserDetail findByCompanyIdAndIsBindTrue(Long companyId);

    @Query(nativeQuery = true, value = "SELECT d.* FROM t_wx_company_user_detail d WHERE d.company_id = ?1 AND d.is_bind = TRUE AND d.enable_flg = TRUE ORDER BY d.updated_date DESC LIMIT 1")
    UserDetail findByCompanyIdAndIsBindTrueAndEnableFlgTrue(Long companyId);

    @Query(nativeQuery = true, value = "SELECT d.* FROM t_wx_company_user_detail d WHERE d.company_id = ?1 AND d.enable_flg = TRUE ORDER BY d.updated_date DESC LIMIT 1")
    List<UserDetail>  findByCompanyIdAndEnableFlgTrue(Long companyId);
}
