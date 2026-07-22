package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.client.entity.BuyQuote;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public interface BuyQuoteDao extends BaseDao<BuyQuote> {
    @Query(nativeQuery = true, value = "select a.* from t_buy_quote a  where (a.status = 1 or (a.expire_time >= NOW() and a.status=0)) and a.enquiry_id =?1 and a.delete_flg =0 order by a.arrive_date,a.deal_number")
    List<BuyQuote> getEffectiveMin(Long enquiryId);

    @Transactional
    @Modifying
    @Query("update BuyQuote b set b.status =1,b.confirmTime = ?2 where b.id = ?1")
    void confirmDeal(Long id, Date confirmTime);

    @Transactional
    @Modifying
    @Query("update BuyQuote b set b.deleteFlg =?2 where b.enquiryId=?1 ")
    void updateDeleteFlgByEnquiryId(Long enquiryId, Boolean deleteFlg);

    @Query(value ="SELECT a.odd_number 'oddNumber',a.deal_number 'dealNumber',a.sell_price 'sellPrice',DATE_FORMAT(a.arrive_date,'%Y-%m-%d') 'arriveDate',DATE_FORMAT(a.confirm_time,'%Y-%m-%d %H:%i') 'confirmTime',DATE_FORMAT(a.created_date,'%Y-%m-%d') 'createdDate',b.odd_number 'enquiryOddNumber',CONCAT(b.product_name, '/', b.brand_number, '/', b.factory_name) 'productName', b.payment_days 'paymentDays'"+
            "FROM t_buy_quote a LEFT JOIN t_buy_enquiry b ON b.id = a.enquiry_id  WHERE b.company_name = ?1 AND b.delete_flg = 0 AND a.status = 1 AND ((?2 IS NULL OR ?2 = '') OR (a.odd_number Like concat('%',?2,'%') or b.odd_number Like concat('%',?2,'%'))) order by a.id desc"
           ,countQuery = "SELECT COUNT(*) FROM t_buy_quote a LEFT JOIN t_buy_enquiry b on  b.id = a.enquiry_id  WHERE b.company_name = ?1 AND b.delete_flg = 0 AND a.status = 1",nativeQuery = true)
    Page<Map<String,Object>> getQuoteSuccess(String companyName,String oddNumber, Pageable pageable);

}