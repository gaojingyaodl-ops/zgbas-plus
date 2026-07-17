package com.spt.bas.server.service.impl;



import com.spt.bas.client.entity.PartnerUser;
import com.spt.bas.server.dao.PartnerUserDao;
import com.spt.bas.server.service.IPartnerUserService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartnerUserServiceImpl extends BaseService<PartnerUser> implements IPartnerUserService {

    @Autowired
    private PartnerUserDao partnerUserDao;


    @Override
    public BaseDao<PartnerUser> getBaseDao() {
        return partnerUserDao;
    }

    @Override
    public List<PartnerUser> getByCompanyId(Long companyId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Specification<PartnerUser> specification = WebUtil.buildSpecification("EQL_partnerCompanyId",companyId);
        Specification<PartnerUser> eqbEnableFlg = WebUtil.buildSpecification("EQB_delFlg",false);
        specification = specification.and(eqbEnableFlg);
        List<PartnerUser> all = partnerUserDao.findAll(specification, sort);

        return all;
    }

    @Override
    public PartnerUser getByUserId(Long userId) {
        return partnerUserDao.findByUserId(userId);
    }
}
