package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.AssComplaints;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.vo.AssComplaintsSearchVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.AssComplaintsDao;
import com.spt.bas.server.service.IAssComplaintsService;
import com.spt.bas.server.util.SMSUtils;
import com.spt.bas.server.util.StringUtility;
import com.spt.pm.constant.PmConstants;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 投诉记录
 * @Author: gaojy
 * @create 2022/5/23 15:58
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class AssComplaintsServiceImpl extends BaseService<AssComplaints> implements IAssComplaintsService {
    @Autowired
    private AssComplaintsDao assComplaintsDao;
    //@Autowired
    //private IAdminOpenFacade adminOpenFacade;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseDao<AssComplaints> getBaseDao() {
        return assComplaintsDao;
    }

    @Override
    @ServerTransactional
    public AssComplaints saveComplaints(AssComplaints assComplaints) {
        Long complaintsId = assComplaints.getId();
        Long userId = assComplaints.getToUserId();
        if (Objects.nonNull(userId)){
            SysUserSdk sysUser = authOpenFacade.findUserById(userId);
            if (Objects.nonNull(sysUser) && Objects.nonNull(sysUser.getDeptId())){
                assComplaints.setToUserDeptId(sysUser.getDeptId());
            }
        }
        AssComplaints complaints = assComplaintsDao.save(assComplaints);
        if (Objects.isNull(complaintsId) || complaintsId == 0L){
            // 发起投诉邮件
            List<String> complaintsEmail = getComplaintsEmail(assComplaints);
            SMSUtils.sendComplaintsEmail(complaints, complaintsEmail);
        }
        return complaints;
    }

    @Override
    public Page<AssComplaints> findComplaintsPage(AssComplaintsSearchVo searchVo) {
        Specification<AssComplaints> newSpec;
        Specification<AssComplaints> spec = WebUtil.buildSpecification(searchVo.getSearchParams());
        Specification<AssComplaints> specUserId = WebUtil.buildSpecification("EQL_fromUserId", searchVo.getSearchUserId());
        if (StringUtils.equals("A", searchVo.getSearchType())) {
            newSpec = spec;
        } else if (StringUtils.equals("C", searchVo.getSearchType())) {
            Specification<AssComplaints> specMyDept = WebUtil.buildSpecification("INL_toUserDeptId_OR_INL_toDeptId", getMyDeptIds(searchVo));
            newSpec = Specification.where(spec).and(specMyDept).or(specUserId);
        } else {
            newSpec = Specification.where(spec).and(specUserId);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
        return getBaseDao().findAll(newSpec, pageRequest);
    }

    private List<Long> getMyDeptIds(AssComplaintsSearchVo searchVo) {
        List<Long> myDeptId = authOpenFacade.findMyDeptId(searchVo.getSearchUserId());
        List<Long> resultList = new ArrayList<>(myDeptId);

        DeptSearchVo vo = new DeptSearchVo();
        vo.setEnterpriseId(searchVo.getEnterpriseId());

        List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(vo);
        for (SysDeptSdk sysDept : deptAll) {
            Long deptId = sysDept.getDeptId();
            if (searchVo.getSearchUserId().equals(sysDept.getLeaderId())) {
                resultList.add(deptId);
                //List<Long> childrenIdLsit = deptAll.stream().filter(d -> (Objects.nonNull(d.getParent()) && d.getParent().getId().equals(deptId))).map(SysDept::getId).collect(Collectors.toList());
                List<Long> childrenIdLsit = deptAll.stream().filter(d -> (Objects.nonNull(d.getParentId()) && d.getParentId().equals(deptId))).map(SysDeptSdk::getDeptId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(childrenIdLsit)) {
                    resultList.addAll(childrenIdLsit);
                }
            }
        }
        return resultList;
    }

    private List<String> getComplaintsEmail(AssComplaints assComplaints){
        List<String> emailList = new ArrayList<>();
        List<BsDictData> dictDataList = BsDictUtil.getListByCategory(assComplaints.getEnterpriseId(), BasConstants.DICT_COMPLAINTS_CMEAIL);
        dictDataList.forEach(d->{
            if (StringUtility.isNotBlank(d.getDictName()) && Boolean.TRUE.equals(d.getEnableFlg())){
                emailList.add(d.getDictName());
            }
        });
        if (Objects.isNull(assComplaints.getToUserId()) && Objects.isNull(assComplaints.getToDeptId())){
            return emailList;
        }
        Long userId = assComplaints.getToUserId();
        if (Objects.isNull(userId)){
            SysDeptSdk sysDept = authOpenFacade.findDeptById(assComplaints.getToDeptId());
            if (Objects.isNull(sysDept) || Objects.isNull(sysDept.getLeaderId())){
                return emailList;
            }
            userId = sysDept.getLeaderId();
        }
        DeptSearchVo searchVo = new DeptSearchVo();
        searchVo.setUserId(userId);
        searchVo.setDeptType(PmConstants.NODE_TYPE_CENTER);
        Long centerUserId = authOpenFacade.findDeptLeader(searchVo);
        if (Objects.nonNull(centerUserId)){
            SysUserSdk sysUser = authOpenFacade.findUserById(centerUserId);
            if (Objects.nonNull(sysUser) && StringUtils.isNotBlank(sysUser.getEmail())){
                emailList.add(sysUser.getEmail());
            }
        }
        return emailList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
    }
}
