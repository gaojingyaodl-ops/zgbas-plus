package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaveInfoDao extends BaseDao<SaveInfo> {
    @Query(nativeQuery = true, value = "select s.* from t_save_info s where s.user_id = ?1 ORDER BY s.updated_date DESC LIMIT 1")
    SaveInfo findByUserId(Long userId);

    @Query(nativeQuery = true, value = "select s.* from t_save_info s where s.company_id = ?1 ORDER BY s.updated_date DESC LIMIT 1")
    SaveInfo findByCompanyId(Long userId);

    @Query(nativeQuery = true, value = "select s.* from t_save_info s where s.user_id = ?1 AND s.type = ?2 AND commit_flg = ?3 ORDER BY s.updated_date DESC LIMIT 1")
    SaveInfo findByUserIdAndTypeAndCommitFlg(Long userId, String type, Boolean commitFlg);

    SaveInfo findTopByCompanyIdAndTypeOrderByCreatedDateDesc(Long companyId, String type);
    SaveInfo findTopByCompanyIdAndTypeAndCommitFlgOrderByCreatedDateDesc(Long companyId, String type, Boolean commitFlg);

    @Query(value = "SELECT * from t_save_info where (company_id = ?1 or user_id = ?2) and  type = ?3 and commit_flg = ?4" ,nativeQuery = true)
    List<SaveInfo> findByCompanyIdOrUserIdAndTypeAndCommitFlg(Long companyId, Long userId, String type, Boolean commitFlg);

    List<SaveInfo> findByCompanyIdAndCommitFlgAndTypeIn(Long companyId, Boolean commitFlg, List<String> types);

    List<SaveInfo> findByCompanyIdAndTypeIn(Long companyId, List<String> types);



}
