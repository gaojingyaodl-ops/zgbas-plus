package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.EvaluateUserDetail;
import com.spt.bas.server.dao.EvaluateUserDetailDao;
import com.spt.bas.server.service.IEvaluateUserDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考核明细
 */
@Component
@Slf4j
public class EvaluateUserDetailServiceImpl extends BaseService<EvaluateUserDetail> implements IEvaluateUserDetailService {
    @Autowired
    private EvaluateUserDetailDao evaluateUserDetailDao;
    @Override
    public BaseDao<EvaluateUserDetail> getBaseDao() {
        return evaluateUserDetailDao;
    }

    /**
     * 根据 ids 查询数据
     * @param ids ids
     * @return 数据
     */
    @Override
    public List<EvaluateUserDetail> selectEvaluateUserDetailByIds(String ids) {
        List<EvaluateUserDetail> result = new ArrayList<>();
        if(StringUtils.isNotBlank(ids)){
            List<Long> idList = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
            Iterable<EvaluateUserDetail> evaluateUserDetails = evaluateUserDetailDao.findAllById(idList);
            evaluateUserDetails.forEach(result::add);
        }
        return result;
    }

    @Override
    public List<EvaluateUserDetail> selectDetailByEvaluateUserId(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
        Specification<EvaluateUserDetail> inl_evaluateUserId = WebUtil.buildSpecification("INL_evaluateUserId", idList);
        Specification<EvaluateUserDetail> spe = Specification.where(inl_evaluateUserId);
        return evaluateUserDetailDao.findAll(spe);
    }
}
