package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ClaimBuyer;
import com.spt.tools.jpa.dao.BaseDao;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-03-03 11:10
 */
public interface ClaimBuyerDao extends BaseDao<ClaimBuyer> {

    ClaimBuyer findByCorpSerialNo(String corpSerialNo);

}
