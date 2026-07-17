package com.spt.bas.web.util;

import com.google.common.base.Splitter;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.entity.PmApplySet;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: gaojy
 * @create 2022/7/19 15:40
 * @version: 1.0
 * @description:
 */
@Component
public class WebParamUtils {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsFunderClient bsFunderClient;
    @Autowired
    private IPmApproveStepClient pmApproveStepClient;
    @Autowired
    private IPmApplySetClient pmApplySetClient;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Resource
    private IBsCompanyDcsxClient bsCompanyDcsxClient;

    private static final String ZL_ROLE = "zgbas_new_cyuser";
    private static final String FIN_ROLE = "zgbas_new_fin";


    /**
     * 审批编辑逻辑判断
     *
     * @param permissionVo
     * @param approveId
     * @return
     */
    public PmPermissionVo verifyPermission(PmPermissionVo permissionVo, Long approveId) {
        return verifyPermission(permissionVo, approveId, false);
    }

    /**
     * 审批编辑逻辑判断
     *
     * @param permissionVo
     * @param approveId
     * @return
     */
    public PmPermissionVo verifyPermission(PmPermissionVo permissionVo, Long approveId, Boolean specialFlag) {
        List<PmApplySet> applySetlist = new ArrayList<>();
        if (approveId != null) {
            PmApprove approve = pmApproveClient.getEntity(approveId);
            String currApproveStepId = approve.getCurrApproveStepId();
            if (StringUtils.isNotBlank(currApproveStepId)) {
                List<String> stepIdStr = Splitter.on(BasConstants.SEPARATE).omitEmptyStrings().trimResults().splitToList(currApproveStepId);
                List<Long> stepIdList = stepIdStr.stream().map(Long::valueOf).collect(Collectors.toList());
                List<PmApproveStep> approveSteps = pmApproveStepClient.findStepByIds(stepIdList);
                List<Long> stepIds = approveSteps.stream().map(PmApproveStep::getStepId).collect(Collectors.toList());
                applySetlist = pmApplySetClient.findByProcessId(approve.getProcessId());

                List<PmApplySet> targetApplySetList = applySetlist.stream().filter(s -> stepIds.contains(s.getStepId())).collect(Collectors.toList());
                for (PmApplySet set : targetApplySetList) {
                    if (stepIds.contains(set.getStepId())) {
                        PmPermissionVo.Options op = new PmPermissionVo.Options(set.getEditFlg(), set.getRequireFlg());
                        permissionVo.addEdit(set.getFieldName(), op);
                    }
                }
            }

        }
        // 是否可在审批中编辑
        if (Boolean.TRUE.equals(permissionVo.getHasApprove()) && !permissionVo.getMapEdit().isEmpty()) {
            permissionVo.setCanApproveEdit(true);
        } else {
            permissionVo.setCanApproveEdit(false);
        }
        if (Boolean.TRUE.equals(specialFlag) && (ShiroUtil.hasRole(ZL_ROLE) || ShiroUtil.hasRole(FIN_ROLE))){
            permissionVo.setCanApproveEdit(true);
            structureSet(permissionVo, applySetlist);
        }
        return permissionVo;
    }

    private void structureSet(PmPermissionVo permissionVo, List<PmApplySet> applySetlist) {
        if (CollectionUtils.isEmpty(applySetlist)) {
            return;
        }
        List<PmApplySet> structureSetList = new ArrayList<>(applySetlist.stream().collect(Collectors.toMap(PmApplySet::getFieldName, s -> s, (existing, replacement) -> existing)).values());
        if (CollectionUtils.isNotEmpty(structureSetList)) {
            structureSetList.forEach(pmApplySet -> {
                PmPermissionVo.Options op = new PmPermissionVo.Options(pmApplySet.getEditFlg(), pmApplySet.getRequireFlg());
                permissionVo.addEdit(pmApplySet.getFieldName(), op);
            });
        }
    }

    /**
     * 查询与我相关的我方抬头列表
     *
     * @return
     */
    public List<BsDictData> getMyOurCompanyList() {
        List<BsDictData> resultList = BsCompanyOurUtil.getCompanyOurToBsDictDataList();
        List<BsFunder> funderList = bsFunderClient.findAllByUserId(ShiroUtil.getCurrentUserId());
        if (CollectionUtils.isNotEmpty(funderList)) {
            String companyNames = funderList.stream().map(BsFunder::getCompanyNames).collect(Collectors.joining(BasConstants.COMMA));
            List<String> companyNameList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(companyNames).stream().distinct().collect(Collectors.toList());
            resultList = resultList.stream().filter(dict -> companyNameList.contains(dict.getDictName())).collect(Collectors.toList());
        }
        return resultList;
    }

    /**
     * 查询当前登录人所关联的资方方（已开通资金权限的资金方，多个结果的情况返回第一个资金方）
     *
     * @return
     */
    public BsCompanyDcsx queryFundCompany() {
        List<BsFunder> funderList = bsFunderClient.findAllByUserId(ShiroUtil.getCurrentUserId());
        List<BsCompanyDcsx> companyList = bsCompanyDcsxClient.findDcsxCompanyList();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(funderList)) {
            Set<String> funderCompanyNames = funderList.stream()
                    .flatMap(f -> Splitter.on(BasConstants.COMMA)
                            .omitEmptyStrings()
                            .splitToList(f.getCompanyNames())
                            .stream())
                    .collect(Collectors.toSet());
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(funderCompanyNames)) {
                return companyList.stream()
                        .filter(f -> Boolean.TRUE.equals(f.getFundFlg()))
                        .filter(f -> funderCompanyNames.contains(f.getCompanyName()))
                        .max(Comparator.comparing(BsCompanyDcsx::getFundAmountQg).thenComparing(BsCompanyDcsx::getFundAmountWs))
                        .orElse(null);
            }
        } else {
            return companyList.stream()
                    .filter(f -> Boolean.TRUE.equals(f.getFundFlg()))
                    .max(Comparator.comparing(BsCompanyDcsx::getFundAmountQg).thenComparing(BsCompanyDcsx::getFundAmountWs))
                    .orElse(null);
        }
        return null;
    }

    /**
     * 查询与当前登录人关联的资金方
     * 1.当前登录人为-资金方，返回配置管理的资金方抬头列表
     * 2.当前登录人为-非资金方，返回所有资金方抬头列表
     *
     * @return
     */
    public List<BsCompanyDcsx> queryFundCompanyListWithUser() {
        List<BsCompanyDcsx> companyList = bsCompanyDcsxClient.findDcsxCompanyList();
        List<BsFunder> funderList = bsFunderClient.findAllByUserId(ShiroUtil.getCurrentUserId());
        List<BsCompanyDcsx> fundCompanyList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(funderList)) {
            Set<String> funderCompanyNames = funderList.stream()
                    .flatMap(f -> Splitter.on(BasConstants.COMMA)
                            .omitEmptyStrings()
                            .splitToList(f.getCompanyNames())
                            .stream())
                    .collect(Collectors.toSet());

            if (CollectionUtils.isNotEmpty(funderCompanyNames)) {
                fundCompanyList = companyList.stream()
                        .filter(f -> Boolean.TRUE.equals(f.getFundFlg()))
                        .filter(f -> funderCompanyNames.contains(f.getCompanyName()))
                        .collect(Collectors.toList());
            }
        } else {
            fundCompanyList = companyList.stream()
                    .filter(BsCompanyDcsx::getFundFlg)
                    .collect(Collectors.toList());
        }
        return fundCompanyList;
    }


    /**
     * 获取机构用户树
     *
     * @param initUser 是否初始化用户信息
     * @return 机构用户树
     */
    public EasyTreeNode getDeptEasyTreeNode(boolean initUser) {
        List<SysDeptSdk> deptList = getDeptAll();
        return EasyTreeUtil2.getDeptTree(deptList, initUser);
    }

    /**
     * 获取机构用户树
     *
     * @param initUser   是否初始化用户信息
     * @param onlyUserId 是否仅返回用户信息
     * @return 机构用户树
     */
    public EasyTreeNode getDeptEasyTreeNode(boolean initUser, boolean onlyUserId) {
        List<SysDeptSdk> deptList = getDeptAll();
        return EasyTreeUtil2.getDeptTree(deptList, initUser, onlyUserId);
    }

    /**
     * 获取部门负责人ID
     *
     * @return 部门负责人ID
     */
    public Long getDeptLeader() {
        Long deptLeader = 0L;
        try {
            DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getCurrentUserId(), PmConstants.NODE_TYPE_CENTER, ShiroUtil.getEnterpriseId());
            deptLeader = authOpenFacade.findDeptLeader(deptSearchVo);
        } catch (Exception e) {
            logger.error("getDeptLeader error:{}", JsonUtil.obj2Json(e));
        }
        return Objects.isNull(deptLeader) ? 0L : deptLeader;
    }

    /**
     * 查询所有部门信息
     *
     * @return 部门列表
     */
    public List<SysDeptSdk> getDeptAll() {
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        return authOpenFacade.findDeptAll(deptSearchVo);
    }

    /**
     * 根据部门ID查询部门信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SysDeptSdk getDeptById(Long deptId) {
        return authOpenFacade.findDeptById(deptId);
    }

    /**
     * 根据用户ID查询该用户负责的部门ID
     *
     * @param deptLeader 用户ID
     * @return 该用户负责的部门ID
     */
    public List<Long> getMyDeptId(Long deptLeader) {
        return authOpenFacade.findMyDeptId(deptLeader);
    }

    /**
     * 根据用户ID查询该用户负责的部门下的用户ids
     *
     * @param userId 用户ID
     * @return 该用户负责的部门ID
     */
    public List<Long> getMyDeptToUsers(Long userId) {
        List<Long> deptIds = authOpenFacade.findMyDeptId(userId);
        if (CollectionUtils.isNotEmpty(deptIds)) {
            List<SysUserSdk> userSdkList = authOpenFacade.findByDeptIds(deptIds);
            if (CollectionUtils.isNotEmpty(userSdkList)) {
                return userSdkList.stream().map(SysUserSdk::getUserId).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();

    }

    public String formatterWaitDealNum(Long userWaitDealNum) {
        if (Objects.isNull(userWaitDealNum) || userWaitDealNum <= 0) {
            return "";
        }
        if (userWaitDealNum > 99) {
            return "99+";
        }
        return String.valueOf(userWaitDealNum);
    }

    public static boolean verifySpecialChain(ApplyMatch entity) {
        if (Objects.isNull(entity) || entity.getId() == 0L) {
            return false;
        }
        String buyOurCompanyName = entity.getBuyOurCompanyName();
        String sellOurCompanyName = entity.getSellOurCompanyName();

        // 代采方与中游代采方一致为正常业务链条
        if (StringUtils.equals(buyOurCompanyName, sellOurCompanyName)) {
            return false;
        }
        return StringUtils.equals(BasConstants.COMPANY_NAME_FLK, buyOurCompanyName)
                || StringUtils.equals(BasConstants.COMPANY_NAME_ZJKR, buyOurCompanyName);
    }

    public void dealWithSpecialFundView(Model model, ApplyMatch entity, List<ApplyMatchDetail> buyList) {
        model.addAttribute("fundViewFlag", false);
        model.addAttribute("specialChainFlag", false);
        if (Objects.isNull(entity) || entity.getId() == 0L) {
            return;
        }
        String buyOurCompanyName = entity.getBuyOurCompanyName();
        String sellOurCompanyName = entity.getSellOurCompanyName();

        // 代采方与中游代采方一致为正常业务链条
        if (StringUtils.equals(buyOurCompanyName, sellOurCompanyName)) {
            return;
        }
        ApplyMatchDetail buyDetail = buyList.stream().findFirst().orElse(null);
        // 资金方视角查看详情页
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
            model.addAttribute("fundViewFlag", true);
        }
        if (Objects.isNull(buyDetail)) {
            return;
        }
        BigDecimal dealPrice = buyDetail.getDealPrice();
        BigDecimal dealNumber = entity.getDealNumber();
        BigDecimal fundDealPrice = dealPrice;
        if (org.apache.commons.lang3.StringUtils.equals(BasConstants.COMPANY_NAME_FLK, buyOurCompanyName)) {
            fundDealPrice = dealPrice.multiply(new BigDecimal("1.003")).setScale(2, RoundingMode.HALF_UP);
        } else if (org.apache.commons.lang3.StringUtils.equals(BasConstants.COMPANY_NAME_ZJKR, buyOurCompanyName)) {
            fundDealPrice = dealPrice.add(new BigDecimal("5"));
        }
        BigDecimal fundDealPriceNoTax = fundDealPrice.divide(new BigDecimal("1.13"), 2, RoundingMode.HALF_UP);
        BigDecimal fundTotalAmount = fundDealPrice.multiply(dealNumber).setScale(2, RoundingMode.HALF_UP);
        model.addAttribute("fundDealPrice", fundDealPrice);
        model.addAttribute("fundDealPriceNoTax", fundDealPriceNoTax);
        model.addAttribute("fundTotalAmount", fundTotalAmount);
        model.addAttribute("specialChainFlag", true);
    }

}
