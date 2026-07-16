package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractLoading;
import com.spt.tools.jpa.dao.BaseDao;

/**
 * @Author: gaojy
 * @create 2022/3/16 9:27
 * @version: 1.0
 * @description:
 */
public interface CtrContractLoadingDao extends BaseDao<CtrContractLoading> {

    CtrContractLoading findByCfcaContractNo(String cfcaContractNo);
}
