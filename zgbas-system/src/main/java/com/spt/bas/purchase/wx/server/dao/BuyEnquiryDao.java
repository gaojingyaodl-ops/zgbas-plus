package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.client.entity.BuyEnquiry;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

public interface BuyEnquiryDao extends BaseDao<BuyEnquiry> {

    List<BuyEnquiry> findBuyEnquiryByCompanyName(String companyName);

    @Transactional
    @Modifying
    @Query("update BuyEnquiry b set b.deleteFlg =?2 where b.id=?1 ")
    void updateDeleteFlg(Long id, Boolean deleteFlg);

    @Query(nativeQuery = true, value = "select sum(a.deal_number) from t_buy_quote a where enquiry_id =?1 and `status` =1 and delete_flg =0")
    BigDecimal getTransactionNum(Long enquiryId);

    @Transactional
    @Modifying
    @Query("update BuyEnquiry b set b.status =1 where b.id=?1")
    void updateStatus(Long id);

    @Query(nativeQuery = true, value = "SELECT IFNULL(Max(CAST(SUBSTRING(odd_number, LENGTH(odd_number) - 2) AS SIGNED)),0)+1 AS oddNumber FROM t_buy_enquiry WHERE odd_number like  CONCAT('%',?1,'%')")
    int getOddNumber(String s);
}
