package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BsCompanyOurDao extends BaseDao<BsCompanyOur> {

    @Query(nativeQuery = true, value = "SELECT o.* FROM t_bs_company_our o WHERE o.company_name =?1 LIMIT 1")
    BsCompanyOur findByCompanyName(String companyName);

    @Query(value = "from BsCompanyOur o where o.enableFlg = true")
    List<BsCompanyOur> findAllEnableOurCompany();

    /**
     * 查询有效的我司企业
     * @return
     */
    @Query(value = "from BsCompanyOur o where o.enableFlg = true and o.ourCompanyFlag = true")
    List<BsCompanyOur> findAllEnableAndOurCompanyFlag();
}
