package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.EvaluateSearchVo;
import com.spt.bas.client.vo.EvaluateStartVo;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.EvaluateItemDao;
import com.spt.bas.server.dao.EvaluateUserDao;
import com.spt.bas.server.dao.EvaluateUserDetailDao;
import com.spt.bas.server.dao.EvaluateUserManageDao;
import com.spt.bas.server.service.IApproveWaitDealService;
import com.spt.bas.server.service.IEvaluateUserManageService;
import com.spt.bas.server.service.IEvaluateUserService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 考核人员清单
 */
@Component
@Slf4j
public class EvaluateUserServiceImpl extends BaseService<EvaluateUser> implements IEvaluateUserService {
    @Autowired
    private EvaluateUserDao evaluateUserDao;
    @Autowired
    private IApproveWaitDealService approveWaitDealService;
    @Autowired
    private EvaluateUserDetailDao evaluateUserDetailDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private EvaluateItemDao evaluateItemDao;
    @Autowired
    private IEvaluateUserManageService evaluateUserManageService;

    @Override
    public BaseDao<EvaluateUser> getBaseDao() {
        return evaluateUserDao;
    }

    /**
     * 查询考核人员明细
     * @param searchVo 查询参数
     * @return 查询结构
     */
    @Override
    public Page<EvaluateUser> findPageBySearch(EvaluateSearchVo searchVo) {

        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());//分页
        Specification<EvaluateUser> spe = WebUtil.buildSpecification(searchVo.getSearchParams());
        String evaluateTypes = searchVo.getEvaluateTypes();
        if(StringUtils.isNotBlank(evaluateTypes)){
            // 查询本中心的员工的考核明细
           if("B".equals(evaluateTypes)){
               Specification<EvaluateUser> spec_dept = WebUtil.buildSpecification("INL_deptId",searchVo.getDeptIdList());
               //Specification<EvaluateUser> dept = Specification.where(spec_dept);
               spe = Specification.where(spe).and(spec_dept);
           }else if("C".equals(evaluateTypes)){
               // 查询自己的
               Specification<EvaluateUser> spec_userId = WebUtil.buildSpecification("EQL_userId", searchVo.getCurrentUserId());
               //Specification<EvaluateUser> userId = Specification.where(spec_userId);
               spe = Specification.where(spe).and(spec_userId);
           }
        }
        return evaluateUserDao.findAll(spe,pageRequest);
    }

    /**
     * 发起考评
     */
    @Override
    @ServerTransactional
    public void startEvaluate(EvaluateStartVo vo) {
        List<SysUserSdk> sysUsers = authOpenFacade.findByDeptIds(vo.getDeptIds());
        if(sysUsers != null && sysUsers.size() > 0){
            List<BsDictData> dictDataList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.EVALUATE_USER_HR);
            List<BsDictData> evaluateUserHRConfigList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.EVALUATE_USER_HR_CONFIG);
            StringBuilder hrIds = new StringBuilder();
            StringBuilder hrNames = new StringBuilder();
            if(CollectionUtils.isNotEmpty(dictDataList)){
                for(int i=0;i<dictDataList.size();i++){
                    BsDictData dictData = dictDataList.get(i);
                    String hrId = dictData.getDictName();
                    String hrName = dictData.getRemark();
                    if(StringUtils.isNotBlank(hrId)){
                        hrIds.append(hrId);
                    }
                    if(StringUtils.isNotBlank(hrName)){
                        hrNames.append(hrName);
                    }
                    if(i < dictDataList.size() - 1){
                        hrIds.append("|");
                        hrNames.append("|");
                    }
                }
            }
            List<BsDictData> noEvaluateUsersDictData = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.NO_EVALUATE_USERS);
            StringBuilder noEvaluateUsersIds = new StringBuilder();
            if(noEvaluateUsersDictData != null && noEvaluateUsersDictData.size() > 0) {
                for (BsDictData noEvaluateUsers:noEvaluateUsersDictData) {
                    String noEvaluateUsersId = noEvaluateUsers.getDictName();
                    noEvaluateUsersIds.append(noEvaluateUsersId).append(",");
                }
            }
//            Long hrUserId = Long.valueOf(value);
//            SysUser hrUser = adminOpenFacade.findUserById(hrUserId);
            for (SysUserSdk user:sysUsers) {
                boolean enableFlg = "0".equals(user.getStatus());
                if(!enableFlg) {
                    // 无效用户-不参与考核
                    continue;
                }
                boolean delFlg = "2".equals(user.getDelFlag());
                if(delFlg) {
                    // 删除用户-不参与考核
                    continue;
                }
                if (Boolean.FALSE.equals(user.getAssessmentFlg())){
                    // 考核标识-不参与考核
                    continue;
                }
                // 判断该用户是否配置在不参与考评人员名单中
                if(StringUtils.isNotBlank(noEvaluateUsersIds.toString()) && noEvaluateUsersIds.toString().contains(String.valueOf(user.getUserId()))){
                    continue;
                }
                Long deptLeaderId;
                // 查询所在部门
                //SysDept dept = adminOpenFacade.findDeptByUserId(user.getId());
                SysDeptSdk dept = user.getDept();
                List<EvaluateUser> evaluateUserList = evaluateUserDao.findAllByEvaluateMonthAndUserId(vo.getEvaluateMonth(),user.getUserId());
                if(evaluateUserList != null && evaluateUserList.size() > 0) {
                    // 该部门已发起当月考评，不再重新发起
                    continue;
                }
                if(!"0".equals(dept.getStatus())){
                    continue;
                }

                Long leaderUserId = dept.getLeaderId();
                // 取得部门负责人id
                if(leaderUserId != null && leaderUserId.equals(user.getUserId())){
                    SysDeptSdk parent = dept.getParent();
                    if(parent != null && parent.getLeaderId() != null){
                        deptLeaderId = parent.getLeaderId();
                    } else {
                        deptLeaderId = leaderUserId;
                    }
                } else {
                    deptLeaderId = leaderUserId;
                }
                // 查询部门负责人
                SysUserSdk deptLeade = authOpenFacade.findUserById(deptLeaderId);
                if(deptLeade == null){
                    continue;
                }
                String deptLeaderName = deptLeade.getNickName();
                Long userLeaderId = user.getUserLeaderId();
                String userLeaderName = user.getUserLeaderName();
                if(userLeaderId != null && StringUtils.isNotBlank(userLeaderName)){
                    deptLeaderId = userLeaderId;
                    deptLeaderName = userLeaderName;
                }
                
                EvaluateUser evaluateUser = new EvaluateUser();
                evaluateUser.setEvaluateMonth(vo.getEvaluateMonth());
                evaluateUser.setUserId(user.getUserId());
                evaluateUser.setUserName(user.getNickName());
                evaluateUser.setDeptId(user.getDeptId());
                evaluateUser.setDeptName(dept.getDeptName());
                evaluateUser.setStatus("0");
                evaluateUser.setBranchCd(user.getBranchCd());
                evaluateUser.setAssessmentUserId(deptLeaderId);
                evaluateUser.setAssessmentUserName(deptLeaderName);
                evaluateUser = evaluateUserDao.save(evaluateUser);
                List<EvaluateItem> evaluateItemList = evaluateItemDao.findAllEvaluateItem();
                if(evaluateItemList != null && evaluateItemList.size() > 0){
                    for (EvaluateItem evaluateItem:evaluateItemList) {
                        EvaluateUserDetail evaluateUserDetail = new EvaluateUserDetail();
                        evaluateUserDetail.setEvaluateUserId(evaluateUser.getId());
                        evaluateUserDetail.setEvaluateItemId(evaluateItem.getId());
                        evaluateUserDetail.setEvaluateMetrics(evaluateItem.getEvaluateMetrics());
                        evaluateUserDetail.setWeight(evaluateItem.getWeight());
                        evaluateUserDetail.setDispOrderNo(evaluateItem.getDispOrderNo());
                        if (StringUtils.equals(BasConstants.EVALUATE_DEPT_HR, evaluateItem.getEvaluateDept())) {
                            BsDictData hrConfig = evaluateUserHRConfigList.stream().filter(d -> StringUtils.equals(d.getDictCd(), user.getBranchCd())).findFirst().orElse(null);
                            if (Objects.nonNull(hrConfig)) {
                                evaluateUserDetail.setScoreUserId(hrConfig.getDictName());
                                evaluateUserDetail.setScoreUserName(hrConfig.getRemark());
                            } else {
                                evaluateUserDetail.setScoreUserId(hrIds.toString());
                                evaluateUserDetail.setScoreUserName(hrNames.toString());
                            }
                        } else {
                            evaluateUserDetail.setScoreUserId(String.valueOf(deptLeaderId));
                            evaluateUserDetail.setScoreUserName(deptLeaderName);
                        }
                        evaluateUserDetail.setStatus("0");
                        evaluateUserDetailDao.save(evaluateUserDetail);
                    }
                }

            }
        }
    }

    @Override
    public List<EvaluateUser> findAllByEvaluateMonthAndDeptId(String evaluateMonh, Long deptId) {
        return evaluateUserDao.findAllByEvaluateMonthAndDeptId(evaluateMonh,deptId);
    }

    @Override
    public List<EvaluateUser> findAllByEvaluateMonthAndUserId(String evaluateMonh,Long userId) {
        return evaluateUserDao.findAllByEvaluateMonthAndUserId(evaluateMonh,userId);
    }

    /**
     * 根据evaluateUserId 批量查询数据
     * @param evaluateUserIds id字符串，用英文逗号分割
     * @return 结果集
     */
    @Override
    public List<EvaluateUser> selectDataByIds(String evaluateUserIds) {
        if(StringUtils.isBlank(evaluateUserIds)){
            return Collections.emptyList();
        }
        List<Long> idList = Arrays.stream(evaluateUserIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
        Iterable<EvaluateUser> allById = evaluateUserDao.findAllById(idList);
        List<EvaluateUser> result = new ArrayList<>();
        allById.forEach(result::add);
        return result;
    }

    /**
     * 发送代办事项通知
     * @param approveWaitDealVo
     */
    @Override
    public void approveWaitDeal(EvaluateUserApproveWaitDealVo approveWaitDealVo) {
        approveWaitDealService.addEvaluateUserDeal(approveWaitDealVo);
    }
}
