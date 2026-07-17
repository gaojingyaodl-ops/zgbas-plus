package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompany;
import com.spt.tools.jpa.service.IBaseService;


/**
 * 企业管理服务类 用来处理企业领用、释放
 */
public interface IBsCompanyManageService extends IBaseService<BsCompany> {

    void updateStatusByTask(String companyName);

}
