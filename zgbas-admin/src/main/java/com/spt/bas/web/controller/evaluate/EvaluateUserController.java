package com.spt.bas.web.controller.evaluate;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.EvaluateAppeal;
import com.spt.bas.client.entity.EvaluateUser;
import com.spt.bas.client.entity.EvaluateUserDetail;
import com.spt.bas.client.remote.IEvaluateAppealClient;
import com.spt.bas.client.remote.IEvaluateUserClient;
import com.spt.bas.client.remote.IEvaluateUserDetailClient;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.bas.report.client.remote.IRptEvaluateUserDetailRemoteClient;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.jpa.vo.IdEntity;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * 考核明细
 * @version 1.0.0
 * @date 2022/5/19 16:14
 */
@Controller
@RequestMapping(value = "/evaluate/evaluateDetail")
public class EvaluateUserController extends PageController<EvaluateUser, BaseVo> {
    @Autowired
    private IEvaluateUserClient evaluateUserClient;
    @Autowired
    private IEvaluateUserDetailClient evaluateUserDetailClient;
    @Autowired
    private IRptEvaluateUserDetailRemoteClient evaluateUserDetailRemoteClient;
    @Autowired
    private IEvaluateAppealClient evaluateAppealClient;
    @Override
    public BaseClient<EvaluateUser> getService() {
        return evaluateUserClient;
    }

    /**
     * 查询考核人员列表页面
     * @return 返回页面信息
     */
    @RequestMapping("")
    public String evaluateUserPage(Model model){
        model.addAttribute("evaluateUserStatusJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.EVALUATE_USER_STATUS)));
        // 是否拥有审核权限，1-有，0-没有，主要是用来控制批量审核、审核按钮
        model.addAttribute("auditPermission", chechkAuditPermission() ? "1" : "0");
        String yearAndMonth = getYearAndMonth();
        model.addAttribute("yearAndMonth",yearAndMonth);
        return "evaluate/evaluateUserPage" ;
    }

    /**
     * 默认年月
     * @return
     */
    private String getYearAndMonth() {
        LocalDate now = LocalDate.now().plusMonths(-1);
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM");
        return now.format(pattern);
    }

    @RequestMapping("/hrEvaluateUserPage")
    public String hrEvaluateUserPage(Model model){
        model.addAttribute("evaluateUserStatusJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.EVALUATE_USER_STATUS)));
        model.addAttribute("evaluateGroupJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_GROUP)));
        model.addAttribute("evaluateMetricsJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_METRICS)));
        String yearAndMonth = getYearAndMonth();
        model.addAttribute("yearAndMonth",yearAndMonth);
        return "evaluate/hrEvaluateUserPage" ;
    }

    /**
     * 查询考核人员列表
     * @param searchVo 查询参数
     * @param request 请求
     * @param response 响应
     */
    @RequestMapping(value = "/findDataList")
    public void findDataList(RptEvaluateUserSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        Long currentUserId = ShiroUtil.getCurrentUserId();
        searchVo.setCurrentUserId(currentUserId);
        // 查看上海的权限
        if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_SH.getPermissionCode())){
            searchVo.setBranchCd("SH");
        }
        if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_QD.getPermissionCode())){
            searchVo.setBranchCd("QD");
        }
        if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_GZ.getPermissionCode())){
            searchVo.setBranchCd("GZ");
        }
        if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_YY.getPermissionCode())){
            searchVo.setBranchCd("YY");
        }
        // 查看是否拥有查询所有考核人员的权限
        if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_VIEWDETAIL.getPermissionCode())){
            searchVo.setCurrentUserId(null);
        }
        PageDown<RptEvaluateUserVo> result =  evaluateUserDetailRemoteClient.findEvaluateUserIdBySourceId(searchVo);
        JsonEasyUI.renderJson(response, result);
    }

    /**
     * 查询考核
     * @param model
     * @param evaluateUserId
     * @param type 查询类型 0-考评， 1-详情，2-已考评，3-审核，4-待确认
     * @param updateAssessment 是否是修改考评，1 是，0 否
     * @return
     */
    @GetMapping("/getEvaluateDetail")
    public String getEvaluateUserDetail(Model model,Long evaluateUserId,Integer type,String yearAndMonth,Integer updateAssessment){
        EvaluateUser entity = evaluateUserClient.getEntity(evaluateUserId);
        model.addAttribute("entity",entity);
        model.addAttribute("type",type);
        model.addAttribute("updateAssessment",updateAssessment);
        model.addAttribute("yearAndMonth",yearAndMonth);
        model.addAttribute("evaluateGroupJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_GROUP)));
        model.addAttribute("evaluateMetricsJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_METRICS)));
        return "evaluate/evaluateDetailPage";
    }

    /**
     * 获取详情考核详情列表
     * @param queryVo 查询参数
     * @param response 响应
     */
    @RequestMapping("/evaluatelist")
    public void getEvaluateUserDetailList(RptEvaluateUserDetailQueryVo queryVo, HttpServletResponse response){
        PageDown<RptEvaluateUserDetailRemoteVo> page = new PageDown<>();
        if(StringUtils.isNotBlank(queryVo.getEvaluateDept())){
            queryVo.setCurrentUserId(ShiroUtil.getCurrentUserId());
            // 查看上海的权限
            if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_SH.getPermissionCode())){
                queryVo.setBranchCd("SH");
            }
            if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_QD.getPermissionCode())){
                queryVo.setBranchCd("QD");
            }
            if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_GZ.getPermissionCode())){
                queryVo.setBranchCd("GZ");
            }
            if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_YY.getPermissionCode())){
                queryVo.setBranchCd("YY");
            }
            // // 如果当前角色拥有查看所有考核权限的话，直接不用设置当前登录人
            if(ShiroUtil.isPermitted(PermissionEnum.PERM_EVALUATE_VIEWDETAIL.getPermissionCode())){
                queryVo.setCurrentUserId(null);
            }
            if(StringUtils.isBlank(queryVo.getYearAndMonth())){
                String yearAndMonth = getYearAndMonth();
                queryVo.setYearAndMonth(yearAndMonth);
            }
            // 如果类型不是 0，就说明此时上级+人力已经评过分了，此时查的是所有的考评项
            if(!"0".equals(queryVo.getType())){
                queryVo.setEvaluateDept(null);
            }
            page = evaluateUserDetailRemoteClient.selectEvaluateUserDetail(queryVo);
        }
        JsonEasyUI.renderJson(response, page);
    }

    /**
     * 保存上级评分数据
     * @param detailSaveScoreVo 参数
     * @param response 响应
     */
    @PostMapping(value = "/saveAssessment")
    @ServerTransactional
    public void saveAssessment(@RequestBody RptDetailSaveScoreVo detailSaveScoreVo, HttpServletResponse response) {
        logger.info("detailSaveScoreVo{}" + JsonUtil.obj2Json(detailSaveScoreVo));
        // 评分数据
        List<RptDetailScoreVo> detailScoreList = detailSaveScoreVo.getDetailScoreArray();
        if(CollectionUtils.isNotEmpty(detailScoreList)){
            // 被评分人id
            //Long evaluateUserId = detailScoreList.get(0).getEvaluateUserId();
            // 过滤出被评分人的id
            String ids = detailScoreList.stream().map(e->String.valueOf(e.getEvaluateUserId())).distinct().collect(Collectors.joining(","));
            // 根据被评分人 id 找被评分详情数据
            List<EvaluateUserDetail> evaluateUserDetails = evaluateUserDetailClient.selectDetailByEvaluateUserId(ids);
            // 根据被评分人id找出被评分人数据
            List<EvaluateUser> evaluateUsers = evaluateUserClient.selectDataByIds(ids);
            // 更新evaluateUser表
            saveEvaluateUser(detailSaveScoreVo,evaluateUsers,ids);
            // 更新evaluateUserDetail表
            saveEvaluateUserDetail(evaluateUserDetails,detailScoreList);
        }
        RenderUtil.renderJson(JsonUtil.obj2Json("success"), response);
    }

    /**
     * 保存 hr 评分数据
     * @param detailSaveScoreVo
     * @param response
     */
    @PostMapping(value = "/hrSaveAssessment")
    @ServerTransactional
    public void batchSaveAssessment(@RequestBody RptDetailSaveScoreVo detailSaveScoreVo, HttpServletResponse response) {

        List<RptDetailScoreVo> detailScoreList = detailSaveScoreVo.getDetailScoreArray();
        // 更新evaluateUser表
        if(CollectionUtils.isNotEmpty(detailScoreList)){
            String ids = detailScoreList.stream().map(e->String.valueOf(e.getEvaluateUserId())).distinct().collect(Collectors.joining(","));
            // 根据 evaluateUserid 找出原来的数据
            List<EvaluateUserDetail> evaluateUserDetails = getEvaluateUserDetails(ids);
            if(!checkHrAssessmentPermission(evaluateUserDetails)){
                RenderUtil.renderJson(JsonUtil.obj2Json("noPermission"), response);
                return ;
            }
            Map<Long,EvaluateUser> evaluateUsersMap = getEvaluateUserMap(detailScoreList);
            List<RptDetailAndItemRemoteVo> detailAndItemRemoteVoList = evaluateUserDetailRemoteClient.getDetailAndItemByEvaluateUserId(ids);
            Map<Long,EvaluateUserDetail> evaluateUserDetailsIdMap = getEvaluateUserDetailsIdMap(evaluateUserDetails);
            List<EvaluateUserDetail> updateEvaluateUserDetails = new ArrayList<>();
            List<EvaluateUser> updateEvaluateUser = new ArrayList<>();
            for (RptDetailScoreVo detailScoreVo : detailScoreList) {
                Long evaluateUserId = detailScoreVo.getEvaluateUserId();
                EvaluateUser evaluateUser = evaluateUsersMap.get(evaluateUserId);
                Long evaluateUserDetailId = detailScoreVo.getEvaluateDetailId();
                // 历史评分数据
                EvaluateUserDetail evaluateUserDetail = evaluateUserDetailsIdMap.get(evaluateUserDetailId);
                // 获取上级评分状态
                Optional<RptDetailAndItemRemoteVo> upIsSource = getDetailAndItemRemoteVo(detailAndItemRemoteVoList, evaluateUser, BasConstants.EVALUATE_DEPT_UP);
                // 如果上级已经评过分了
                if(upIsSource.isPresent()){
                    evaluateUser.setStatus("1");
                    evaluateUser.setUpdatedDate(new Date());
                    Integer oldScore = evaluateUserDetail.getScore() == null ? 0 : evaluateUserDetail.getScore();
                    evaluateUser.setScore(evaluateUser.getScore() - oldScore + detailScoreVo.getDetailScore());
                }else{
                    evaluateUser.setUpdatedDate(new Date());

                    evaluateUser.setScore(detailScoreVo.getDetailScore());
                }
                evaluateUserDetail.setUpdatedDate(new Date());
                evaluateUserDetail.setStatus("1");
                evaluateUserDetail.setScore(detailScoreVo.getDetailScore());
                updateEvaluateUser.add(evaluateUser);
                updateEvaluateUserDetails.add(evaluateUserDetail);
            }
            saveData(updateEvaluateUser,updateEvaluateUserDetails);
        }
        RenderUtil.renderJson(JsonUtil.obj2Json("success"), response);
    }

    /**
     * 判断提交hr考评是否拥有权限
     * true-有，false-没有
     * @param evaluateUserDetails
     * @return
     */
    private boolean checkHrAssessmentPermission(List<EvaluateUserDetail> evaluateUserDetails) {
        Long currentUserId = ShiroUtil.getCurrentUserId();
        return evaluateUserDetails.stream().anyMatch(e -> compareUserIdAndSourceUserIds(currentUserId, e.getScoreUserId()));
    }

    /**
     *
     * 判断当前登录人是否存在评分人里面
     * @param currentUserId
     * @param scoreUserIds
     * @return
     */
    private boolean compareUserIdAndSourceUserIds(Long currentUserId, String scoreUserIds) {
        // 如果有分隔符
        if(scoreUserIds.contains("|")){
            String[] scoreUserIdArray = scoreUserIds.split("\\|");
            for (String scoreUserId : scoreUserIdArray) {
                if(scoreUserId.equals(String.valueOf(currentUserId))){
                    return true;
                }
            }
            return false;
        }
        // 如果没有分隔符，直接比较
        return scoreUserIds.equals(String.valueOf(currentUserId));
    }

    /**
     * 保存数据
     * @param updateEvaluateUser
     * @param updateEvaluateUserDetails
     */
    private void saveData(List<EvaluateUser> updateEvaluateUser, List<EvaluateUserDetail> updateEvaluateUserDetails) {
        BatchSaveVo<EvaluateUser> evaluateUserBatchSaveVo = new BatchSaveVo<>();
        evaluateUserBatchSaveVo.setDeletedRecords(Collections.emptyList());
        evaluateUserBatchSaveVo.setInsertedRecords(Collections.emptyList());
        evaluateUserBatchSaveVo.setUpdatedRecords(updateEvaluateUser);

        BatchSaveVo<EvaluateUserDetail> evaluateUserDetailBatchSaveVo = new BatchSaveVo<>();
        evaluateUserDetailBatchSaveVo.setDeletedRecords(Collections.emptyList());
        evaluateUserDetailBatchSaveVo.setInsertedRecords(Collections.emptyList());
        evaluateUserDetailBatchSaveVo.setUpdatedRecords(updateEvaluateUserDetails);

        evaluateUserClient.saveBatch(evaluateUserBatchSaveVo);
        evaluateUserDetailClient.saveBatch(evaluateUserDetailBatchSaveVo);
    }

    private Map<Long,EvaluateUser> getEvaluateUserMap(List<RptDetailScoreVo> detailScoreList) {
        String ids = detailScoreList.stream().map(e->String.valueOf(e.getEvaluateUserId())).collect(Collectors.joining(","));
        return evaluateUserClient.selectDataByIds(ids).stream().collect(Collectors.toMap(IdEntity::getId, e->e,(a, b)->b));
    }

    /**
     * EvaluateUserDetailMap
     * key=evaluateUserDetailId
     * value=实体数据
     * @param evaluateUserDetails
     * @return
     */
    private Map<Long, EvaluateUserDetail> getEvaluateUserDetailsIdMap(List<EvaluateUserDetail> evaluateUserDetails) {
        return evaluateUserDetails.stream().collect(Collectors.toMap(IdEntity::getId,e->e,(a,b)->b));
    }

    private List<EvaluateUserDetail> getEvaluateUserDetails(String ids) {
        return evaluateUserDetailClient.selectDetailByEvaluateUserId(ids);
    }

    /**
     * 审核方法
     * @param evaluateUserIds 需要审核的考评人员 id
     * @param response 返回结果
     */
    @PostMapping(value = "/auditDetail")
    @ServerTransactional
    public void auditDetail(String evaluateUserIds, HttpServletResponse response) {
        // 更新evaluateUser表
        if(StringUtils.isNotEmpty(evaluateUserIds)){
            // 查看是否拥有审核权限 true-有，false-没有
            boolean haveAuditPermission = chechkAuditPermission();
            // 如果没有权限，直接返回
            if(!haveAuditPermission){
                RenderUtil.renderJson(JsonUtil.obj2Json("noAuditPermission"), response);
                return;
            }
            List<EvaluateUser> list = evaluateUserClient.selectDataByIds(evaluateUserIds);
            // 标记
            List<EvaluateUser> updateList = new ArrayList<>();
            for (EvaluateUser evaluateUser : list) {
                if(!"1".equals(evaluateUser.getStatus())){
                    continue;
                }
                // 将状态改为已审批
                evaluateUser.setStatus("2");
                // 更新时间
                evaluateUser.setUpdatedDate(new Date());
                updateList.add(evaluateUser);
            }
            if(CollectionUtils.isEmpty(updateList)){
                RenderUtil.renderJson(JsonUtil.obj2Json("success"), response);
                return;
            }
            BatchSaveVo<EvaluateUser> update = new BatchSaveVo<>();
            update.setUpdatedRecords(updateList);
            update.setInsertedRecords(Collections.emptyList());
            update.setDeletedRecords(Collections.emptyList());
            evaluateUserClient.saveBatch(update);
            List<String> userIds = list.stream().map(e->String.valueOf(e.getUserId())).collect(Collectors.toList());
            EvaluateUserApproveWaitDealVo evaluateApproveWaitDealVo = new EvaluateUserApproveWaitDealVo();
            evaluateApproveWaitDealVo.setUserIds(userIds);
            evaluateApproveWaitDealVo.setEvaluateDate(new Date());
            evaluateApproveWaitDealVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
            // 发送代办事项
            evaluateUserClient.approveWaitDeal(evaluateApproveWaitDealVo);
        }
        RenderUtil.renderJson(JsonUtil.obj2Json("success"), response);
    }

    /**
     * 检查审核人是否拥有审核权限
     * true-有，false-没有
     * @return
     */
    private boolean chechkAuditPermission() {
        return ShiroUtil.isPermitted(PermissionEnum.BAS_EVALUATE_AUDIT.getPermissionCode());
    }

    /**
     * 保存考核人员明细相关信息
     * @param evaluateUserDetails 待更新的数据
     */
    private void saveEvaluateUserDetail(List<EvaluateUserDetail> evaluateUserDetails,List<RptDetailScoreVo> detailScoreList) {
        Map<Long, EvaluateUserDetail> userDetailMap = evaluateUserDetails.stream().collect(Collectors.toMap(IdEntity::getId, e -> e, (a, b) -> b));
        List<EvaluateUserDetail> updateList = new ArrayList<>();
        for (RptDetailScoreVo detailScoreVo : detailScoreList) {
            EvaluateUserDetail evaluateUserDetail = userDetailMap.get(detailScoreVo.getEvaluateDetailId());
            // 更新评分
            evaluateUserDetail.setScore(detailScoreVo.getDetailScore());
            // 更新状态
            evaluateUserDetail.setStatus("1");
            // 更新时间
            evaluateUserDetail.setUpdatedDate(new Date());
            updateList.add(evaluateUserDetail);
        }
        BatchSaveVo<EvaluateUserDetail> update = new BatchSaveVo<>();
        update.setUpdatedRecords(updateList);
        update.setDeletedRecords(new ArrayList<>());
        update.setInsertedRecords(new ArrayList<>());
        // 批量更新
        evaluateUserDetailClient.saveBatch(update);
    }

    /**
     * 保存考核人员相关信息，
     * 评分：hr 评分 + 部门领导评分
     * 主要更新：总分+上级评语+状态
     *
     * @param detailSaveScoreVo 当前评分数据
     */
    private void saveEvaluateUser(RptDetailSaveScoreVo detailSaveScoreVo, List<EvaluateUser> evaluateUsers, String evaluateUserIds) {
        List<RptDetailScoreVo> detailScoreArray = detailSaveScoreVo.getDetailScoreArray();
        List<RptDetailAndItemRemoteVo> detailAndItemRemoteVoList = evaluateUserDetailRemoteClient.getDetailAndItemByEvaluateUserId(evaluateUserIds);
        for (EvaluateUser evaluateUser : evaluateUsers) {
            // 总分
            int sumSource = 0;
            // 查询HR是否评分
            Optional<RptDetailAndItemRemoteVo> hrIsSource = getDetailAndItemRemoteVo(detailAndItemRemoteVoList, evaluateUser,BasConstants.EVALUATE_DEPT_HR);
            // 如果HR未评分,将状态更新为待HR评分
            if(!hrIsSource.isPresent()){
                evaluateUser.setStatus("4");
            }else{
                // 将hr分数累加
                sumSource = hrIsSource.get().getDetailScore();
                // 否则更新为已考评
                evaluateUser.setStatus("1");
            }
            if(StringUtils.isNotBlank(detailSaveScoreVo.getEvaluateRemark())){
                // 设置上级评分
                evaluateUser.setEvaluateRemark(detailSaveScoreVo.getEvaluateRemark());
            }
            // 计算总分
            sumSource += getSumSource(detailScoreArray);
            evaluateUser.setScore(sumSource);
            // 更新时间
            evaluateUser.setUpdatedDate(new Date());
            // 更新考评日期
            evaluateUser.setEvaluateDate(new Date());
        }
        BatchSaveVo<EvaluateUser> update = new BatchSaveVo<>();
        update.setUpdatedRecords(evaluateUsers);
        update.setDeletedRecords(new ArrayList<>());
        update.setInsertedRecords(new ArrayList<>());
        // 批量更新
        evaluateUserClient.saveBatch(update);
    }

    /**
     * 计算总分
     * @param detailScoreArray 分数数据
     * @return 总分
     */
    private int getSumSource(List<RptDetailScoreVo> detailScoreArray) {
        return detailScoreArray.stream().map(RptDetailScoreVo::getDetailScore).reduce(0, Integer::sum);
    }

    private Optional<RptDetailAndItemRemoteVo> getDetailAndItemRemoteVo(List<RptDetailAndItemRemoteVo> detailAndItemRemoteVoList, EvaluateUser evaluateUser, String hrOrUp) {
        return detailAndItemRemoteVoList.stream().filter(e -> e.getEvaluateUserId().equals(evaluateUser.getId())
                && hrOrUp.equals(e.getEvaluateDept())
                && e.getDetailStatus().equals("1")).findFirst();
    }

    /**
     * 申诉界面
     * @param model model
     * @param evaluateUserId 考核人或申诉人
     * @param appealType 申诉类型，0-申诉，1-申诉详情
     * @return
     */
    @GetMapping("/evaluateAppealPage")
    public String evaluateAppealPage(Model model,Long evaluateUserId,String appealType){
        EvaluateUser entity = evaluateUserClient.getEntity(evaluateUserId);
        EvaluateAppeal evaluateAppeal;
        if("1".equals(appealType)){
            evaluateAppeal = evaluateAppealClient.findOneByEvaluateUserId(evaluateUserId);
        }else{
            evaluateAppeal = new EvaluateAppeal();
        }
        model.addAttribute("evaluateAppeal",evaluateAppeal);
        model.addAttribute("entity",entity);
        model.addAttribute("appealType",appealType);
        return "evaluate/evaluateAppealPage" ;
    }

    /**
     * 保存申诉
     * @param evaluateAppeal 申诉实体
     * @param response
     */
    @PostMapping("/saveAppeal")
    @ServerTransactional
    public void saveAppeal(@RequestBody EvaluateAppeal evaluateAppeal,HttpServletResponse response){
        evaluateAppeal.setCreatedDate(new Date());
        evaluateAppeal.setUpdatedDate(new Date());
        Long evaluateUserId = evaluateAppeal.getEvaluateUserId();
        EvaluateUser evaluateUser = evaluateUserClient.getEntity(evaluateUserId);
        evaluateUser.setAppealFlag("1");
        evaluateUser.setUpdatedDate(new Date());
        evaluateUserClient.save(evaluateUser);
        evaluateAppealClient.save(evaluateAppeal);
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.EVALUATE_EMAIL_KEY);
        boolean flag = false;
        if(CollectionUtils.isNotEmpty(listByCategory)){
            String dictCd = listByCategory.get(0).getDictCd();
            String dictName = listByCategory.get(0).getDictName();
            if("switch".equals(dictCd)){
                flag = "true".equals(dictName);
            }
            if(flag){
                // 从数据字典中获取申诉邮件地址
                List<String> enterpriseAppealEmails = listByCategory.stream().filter(e -> !"switch".equals(e.getDictCd()) && e.getEnableFlg()).map(BsDictData::getDictName).collect(Collectors.toList());
                EvaluateUserApproveWaitDealVo vo = new EvaluateUserApproveWaitDealVo();
                vo.setEnterpriseAppealEmail(enterpriseAppealEmails);
                vo.setEvaluateUser(evaluateUser);
                vo.setAppealRemark(evaluateAppeal.getAppealRemark());
                evaluateAppealClient.sendEmail(vo);
            }
        }
        RenderUtil.renderJson(JsonUtil.obj2Json("success"), response);
    }
    /**
     * 确认审核函数
     * @param evaluateUserId 确认人
     * @param response
     */
    @PostMapping("/evaluateConfirm")
    @ServerTransactional
    public void evaluateConfirm(Long evaluateUserId,HttpServletResponse response){
        EvaluateUser evaluateUser = evaluateUserClient.getEntity(evaluateUserId);
        if(!checkConfimPermission(evaluateUser.getUserId())){
            RenderUtil.renderJson(JsonUtil.obj2Json("noPermission"), response);
            return ;
        }
        // 将状态更新为已完成
        evaluateUser.setStatus("3");
        evaluateUser.setUpdatedDate(new Date());
        evaluateUserClient.save(evaluateUser);
        RenderUtil.renderJson(JsonUtil.obj2Json("success"), response);
    }

    /**
     * 校验我已知晓并确认是否是本人
     * true-有，false-没有
     * @return
     */
    private boolean checkConfimPermission(Long userId) {
        return userId.equals(ShiroUtil.getCurrentUserId());
    }

    /**
     * 导出接口
     * @param evaluateUserId
     * @param type
     * @param response
     */
    @RequestMapping("/exportFile")
    public void exportFile(Long evaluateUserId,String type,HttpServletResponse response){
        RptEvaluateUserDetailQueryVo queryVo = new RptEvaluateUserDetailQueryVo();
        queryVo.setEvaluateUserId(evaluateUserId);
        //queryVo.setEvaluateDept(null);
        PageDown<RptEvaluateUserDetailRemoteVo> page = evaluateUserDetailRemoteClient.selectEvaluateUserDetail(queryVo);
        List<RptEvaluateUserDetailRemoteVo> list = page.getContent();
        Map<String, String> groupMap = getGroupMap();
        Map<String, String> metricsMap = getMetricsMap();
        list.forEach(e->{
            e.setEvaluateGroup(groupMap.getOrDefault(e.getEvaluateGroup(),e.getEvaluateGroup()));
            e.setEvaluateMetrics(metricsMap.getOrDefault(e.getEvaluateMetrics(),e.getEvaluateMetrics()));
        });

        String title = "考核详情表";
        String[] titles = new String[] { "考核项目", "考核指标", "权重(分)","指标定义", "评分", "评分人员"};
        String[] attrs = new String[] {"evaluateGroup", "evaluateMetrics", "weight","metricsContent","detailScore" ,"scoreUserName"};
        int[] widths = new int[] { 25, 25, 20, 50, 15, 15};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        //// 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        //// 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(20);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 创建表头
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;

        //PoiExcelUtil.createRows(sheet, list, attrs, start, cellStyle,DateOperator.FORMAT_STR);
        createRows(sheet, list, attrs, start, cellStyle,DateOperator.FORMAT_STR);
        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error("考核详情表导出失败{}",e);
        }

    }
    private Map<String, String> getMetricsMap() {
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.EVALUATE_METRICS);
        return listByCategory.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName, (a, b) -> b));
    }

    private Map<String, String> getGroupMap() {
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.EVALUATE_GROUP);
        return listByCategory.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName, (a, b) -> b));
    }

    private void createRows(Sheet sheet, List<RptEvaluateUserDetailRemoteVo> list, String[] attrs, int start, CellStyle cellStyle, String pattern){
        Iterator var7 = list.iterator();

        int attrsLength = attrs.length;
        while(var7.hasNext()) {
            Object o = var7.next();
            ++start;
            Row row = sheet.createRow(start);
            row.setHeightInPoints(20.0F);

            for(int i = 0; i < attrsLength; ++i) {
                String attrName = attrs[i];
                Cell cell = row.createCell(i);
                cell.setCellStyle(cellStyle);

                try {
                    Object value = PropertyUtils.getProperty(o, attrName);
                    PoiExcelUtil.setCellValue(cell, value, pattern);
                } catch (Exception var13) {
                }
            }
        }
        RptEvaluateUserDetailRemoteVo userDetailRemoteVo = list.get(0);
        String evaluateRemark = userDetailRemoteVo.getEvaluateRemark();
        int sumSorce = list.stream().map(RptEvaluateUserDetailRemoteVo::getDetailScore).reduce(0,Integer::sum);


        int r1 = list.size()+1;
        Row row1 = sheet.createRow(r1);
        Cell cell = row1.createCell(0);
        mySetCellStyle(attrsLength,row1,cellStyle);
        PoiExcelUtil.setCellValue(cell, "上级评语："+evaluateRemark, pattern);
        CellRangeAddress region = new CellRangeAddress(r1, r1, 0, 3);
        sheet.addMergedRegion(region);

        Cell cell1 = row1.createCell(4);
        //mySetCellStyle(attrsLength,row1,cellStyle);
        PoiExcelUtil.setCellValue(cell1, "评定结果："+sumSorce, pattern);
        CellRangeAddress region1 = new CellRangeAddress(r1, r1, 4, 5);
        sheet.addMergedRegion(region1);

        Row row2 = sheet.createRow(r1 + 1);
        Cell cell2 = row2.createCell(0);
        mySetCellStyle(attrsLength,row2,cellStyle);
        PoiExcelUtil.setCellValue(cell2, "日期："+userDetailRemoteVo.getEvaluateDate(), pattern);

        Cell cell3 = row2.createCell(1);
        cell3.setCellStyle(cellStyle);
        //mySetCellStyle(attrsLength,row2,cellStyle);
        PoiExcelUtil.setCellValue(cell3, "姓名："+userDetailRemoteVo.getUserName(), pattern);

        Cell cell4 = row2.createCell(2);
        cell4.setCellStyle(cellStyle);
        //mySetCellStyle(attrsLength,row2,cellStyle);
        PoiExcelUtil.setCellValue(cell4, "部门："+userDetailRemoteVo.getDeptName(), pattern);
    }

    private void mySetCellStyle(int length, Row row, CellStyle cellStyle) {
        for (int i = 0; i < length; ++i) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
        }
    }


}
