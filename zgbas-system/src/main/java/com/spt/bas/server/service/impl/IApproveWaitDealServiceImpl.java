package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCompanyLicense;
import com.spt.bas.client.entity.ApproveWaitDeal;
import com.spt.bas.client.entity.CompanyLicense;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.dao.ApproveWaitDealDao;
import com.spt.bas.server.service.IApproveWaitDealService;
import com.spt.bas.server.util.HttpUtil;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Transactional(readOnly = true)
public class IApproveWaitDealServiceImpl extends BaseService<ApproveWaitDeal> implements IApproveWaitDealService {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Autowired
    private ApproveWaitDealDao approveWaitDealDao;

    @Override
    public BaseDao<ApproveWaitDeal> getBaseDao() {
        return approveWaitDealDao;
    }

    @Override
    public Page<ApproveWaitDeal> findPageWaitDeal(ApproveWaitSearchVo searchVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest_new = PageRequest.of(searchVo.getPage()-1, searchVo.getRows(),sort);
        Page<ApproveWaitDeal> page = approveWaitDealDao.findAll(new Specification<ApproveWaitDeal>() {
            @Override
            public Predicate toPredicate(Root<ApproveWaitDeal> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(StringUtils.isNotBlank(searchVo.getDealType())) {
                    list.add(cb.equal(root.get("dealType").as(String.class), searchVo.getDealType()));
                }
                if(StringUtils.isNotBlank(searchVo.getReadFlg())) {
                    list.add(cb.equal(root.get("readFlg").as(String.class), searchVo.getReadFlg()));
                }
                if(StringUtils.isNotBlank(searchVo.getSubject())) {
                    list.add(cb.like(root.get("subject").as(String.class), "%"+searchVo.getSubject()+"%"));
                }
                if (searchVo.getCreatedDate()!=null) {
                    list.add(cb.greaterThanOrEqualTo(root.get("createdDate").as(String.class), dateToString(searchVo.getCreatedDate())));
                }
                if (searchVo.getCreatedDate()!=null){
                    Date date = DateUtils.addDays(searchVo.getCreatedDate(),1);
                    list.add(cb.lessThanOrEqualTo(root.get("createdDate").as(String.class), dateToString(date)));
                }

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        }, pageRequest_new);

        Page<ApproveWaitDeal> pageVo = new PageImpl<>(page.getContent(), pageRequest_new, page.getTotalElements());
        return pageVo;
    }

    @Override
    public Page<ApproveWaitDeal> findPageWaitDealById(ApproveWaitSearchVo queryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest_new = PageRequest.of(queryVo.getPage()-1, queryVo.getRows(),sort);
        Page<ApproveWaitDeal> page = approveWaitDealDao.findAll(new Specification<ApproveWaitDeal>() {
            @Override
            public Predicate toPredicate(Root<ApproveWaitDeal> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(StringUtils.isNotBlank(String.valueOf(queryVo.getRelaUserId()))) {
                    list.add(cb.equal(root.get("relaUserId").as(String.class), queryVo.getRelaUserId()));
                }
                if(StringUtils.isNotBlank(String.valueOf(queryVo.getRelaDeptId()))) {
                    list.add(cb.equal(root.get("relaDeptId").as(Long.class), queryVo.getRelaDeptId()));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.or(list.toArray(p));
            }
        }, pageRequest_new);

        Page<ApproveWaitDeal> pageVo = new PageImpl<>(page.getContent(), pageRequest_new, page.getTotalElements());
        return pageVo;
    }

    @Override
    @ServiceTransactional
    public void updateStatus(ApproveWaitSearchVo queryVo) {
        if (queryVo!=null) {
            approveWaitDealDao.updateStatus(queryVo.getId());
        }
    }

    @Override
    @ServiceTransactional
    public void updateFlg(ApproveWaitSearchVo queryVo) {
        if (queryVo!=null) {
            approveWaitDealDao.updateFlg(queryVo.getId());
        }
    }

    @Override
    public List<ApproveWaitDeal> findPageWaitDealCount(ApproveWaitSearchVo queryVo) {
        return approveWaitDealDao.findPageWaitDealCount(queryVo.getRelaDeptId(),queryVo.getRelaUserId());
    }

    @Override
    public String findSubject(ApproveWaitSearchVo queryVo) {
        if (queryVo!=null) {
            return approveWaitDealDao.findSubject(queryVo.getId());
        }
        return null;
    }
    // 定时任务：修改履约状态时，添加待办事项
    @Override
    @ServerTransactional
    public void doContractSaveWaitDeal(CtrContract searchVo) {
        Long matchUserId = searchVo.getMatchUserId();
        ApproveWaitDeal entity = new ApproveWaitDeal();
        entity.setEnterpriseId(Long.valueOf(44));// 企业账套Id
        entity.setCreatedDate(new Date());//创建时间
        entity.setRelaUserId(String.valueOf(matchUserId));// 负责人
        entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);//事项类型
        entity.setReadFlg(BasConstants.READ_FLG_NOT);//已读状态
        entity.setCompleteFlg("0");//完成状态
        entity.setSource("履约状态发生改变");
        String res = "";
        String performanceStatus = searchVo.getPerformanceStatus();
        if ( performanceStatus != null ){
            if (performanceStatus.equals("B")){
                res = "宽限期";
            }else if (performanceStatus.equals("D")){
                res = "催告期";
            }else{
                res = "逾期";
            }
        }
        // 设置待办事项摘要内容
        String str = "合同: "+searchVo.getContractNo()+" 履约状态修改为 : "+ res;
        entity.setSubject(str);//摘要
        saveApproveWaitDeal(entity);
    }

    /**
     * 添加待办事项
     * @param vo
     */
    @Override
    @ServerTransactional
    public void saveWaitDeal(ApproveWaitDealVo vo){
        try {
            ApproveWaitDeal entity = new ApproveWaitDeal();
            BeanUtils.copyProperties(vo,entity);
            entity.setCreatedDate(new Date());//创建时间
            saveApproveWaitDeal(entity);
        } catch (Exception e) {
            logger.error("待办事项创建失败");
        }
    }

    /**
     * 添加发货预警通知
     * @param userIdList
     */
    @Override
    @ServiceTransactional
    public void addUnDeliveryDeal(CtrContract contract, List<Long> userIdList) {
        List<ApproveWaitDeal> approveWaitDeals = new ArrayList<>();
        userIdList.forEach(userId -> {
            ApproveWaitDeal entity = new ApproveWaitDeal();
            entity.setEnterpriseId(contract.getEnterpriseId());
            entity.setCreatedDate(new Date());
            entity.setRelaUserId(String.valueOf(userId));
            entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);
            entity.setReadFlg(BasConstants.READ_FLG_NOT);
            entity.setCompleteFlg("0");
            entity.setSource("发货预警通知");
            String subject = String.format("【到期未入库】%s，%s吨，%s", contract.getContractNo(), contract.getTotalNumber(), DateOperator.formatDate(contract.getDeliveryDateFrom()));
            entity.setSubject(subject);
            approveWaitDeals.add(entity);
        });
        approveWaitDealDao.saveAll(approveWaitDeals);
        notifyWaitDealMessageList(userIdList);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");//日期格式
        String tiem = sformat.format(date);

        return tiem;
    }

    /**
     * 添加考核确认代办事项
     * @param evaluateApproveWaitDealVo 代办事项人
     */
    @Override
    @ServiceTransactional
    public void addEvaluateUserDeal(EvaluateUserApproveWaitDealVo evaluateApproveWaitDealVo) {
        List<ApproveWaitDeal> approveWaitDeals = new ArrayList<>();
        List<String> userIdList = evaluateApproveWaitDealVo.getUserIds();
        if(CollectionUtils.isEmpty(userIdList)){
            return;
        }
        userIdList.forEach(userId -> {
            ApproveWaitDeal entity = new ApproveWaitDeal();
            entity.setEnterpriseId(evaluateApproveWaitDealVo.getEnterpriseId());
            entity.setCreatedDate(new Date());
            entity.setRelaUserId(String.valueOf(userId));
            entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);
            entity.setReadFlg(BasConstants.READ_FLG_NOT);
            entity.setCompleteFlg("0");
            entity.setSource("考核待确认通知");
            String subject = String.format("【考核待确认通知】%s %s","您有个考核结果待确认，如不认同考核结果，可以发起申诉！","考核年月："+DateOperator.formatDate(evaluateApproveWaitDealVo.getEvaluateDate()));
            entity.setSubject(subject);
            approveWaitDeals.add(entity);
        });
        approveWaitDealDao.saveAll(approveWaitDeals);
        notifyWaitDealMessageList2(userIdList);
    }

    /**
     * 保存双签附件导出通知
     * @param searchVo
     * @param path
     */
    @Override
    @ServiceTransactional
    public void addZipFileDeal(ContractSearchVo searchVo, String queryDateStr, String path) {
        ApproveWaitDeal entity = new ApproveWaitDeal();
        entity.setEnterpriseId(searchVo.getEnterpriseId());
        entity.setCreatedDate(new Date());
        entity.setRelaUserId(String.valueOf(searchVo.getUserId()));
        entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);
        entity.setReadFlg(BasConstants.READ_FLG_NOT);
        entity.setCompleteFlg("0");
        entity.setSource("双签附件导出通知");
        String subject = String.format("【双签附件导出通知】<br> 查询时间：%s<br> 有效时间：24小时<br> 下载链接：<a href='%s'>%s</a><br>", queryDateStr, path, path);
        entity.setSubject(subject);
        saveApproveWaitDeal(entity);
    }
    @Override
    @ServiceTransactional
    public void addZipFileDeal(Long enterpriseId, Long userId, String queryDateStr, String path) {
        ApproveWaitDeal entity = new ApproveWaitDeal();
        entity.setEnterpriseId(enterpriseId);
        entity.setCreatedDate(new Date());
        entity.setRelaUserId(String.valueOf(userId));
        entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);
        entity.setReadFlg(BasConstants.READ_FLG_NOT);
        entity.setCompleteFlg("0");
        entity.setSource("开票合同附件导出通知");
        String subject = String.format("【开票合同附件导出通知】<br> 查询时间：%s<br> 有效时间：24小时<br> 下载链接：<a href='%s'>%s</a><br>", queryDateStr, path, path);
        entity.setSubject(subject);
        saveApproveWaitDeal(entity);
    }

    @Override
    @ServiceTransactional
    public void addZipFileInvoiceDcsx(Long enterpriseId, Long userId, String queryDateStr, String path) {
        ApproveWaitDeal entity = new ApproveWaitDeal();
        entity.setEnterpriseId(enterpriseId);
        entity.setCreatedDate(new Date());
        entity.setRelaUserId(String.valueOf(userId));
        entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);
        entity.setReadFlg(BasConstants.READ_FLG_NOT);
        entity.setCompleteFlg("0");
        entity.setSource("开票中游合同附件导出通知");
        String subject = String.format("【开票中游合同附件导出通知】<br> 查询时间：%s<br> 有效时间：24小时<br> 下载链接：<a href='%s'>%s</a><br>", queryDateStr, path, path);
        entity.setSubject(subject);
        saveApproveWaitDeal(entity);
    }

    /**
     * 保存双签附件导出通知
     * @param companyLicense 公司证照相关信息
     * @param path 路径
     */
    @Override
    @ServiceTransactional
    public void addCompanyLicenseFile(ApplyCompanyLicense companyLicense, List<CompanyLicense> path) {
        ApproveWaitDeal entity = new ApproveWaitDeal();
        entity.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        entity.setCreatedDate(new Date());
        entity.setRelaUserId(String.valueOf(companyLicense.getApplyUserId()));
        entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);
        entity.setReadFlg(BasConstants.READ_FLG_NOT);
        entity.setCompleteFlg("0");
        entity.setSource("公司证照申请通知");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("【公司证照申请通知】<br>  有效期限：%s <br>  下载地址：", DateOperator.formatDate(companyLicense.getPeriodDate())));
        if (CollectionUtils.isEmpty(path)) {
            sb.append("暂无公司证照，请联系管理员上传后再从审批单【下载证照】按钮处下载");
        } else {
            for (CompanyLicense license : path) {
                sb.append(String.format("<a href='%s' target='_blank'>%s</a>&nbsp&nbsp", license.getUrl(), license.getFileName()));
            }
        }
        entity.setSubject(sb.toString());
        saveApproveWaitDeal(entity);
    }

    @Override
    public Long getUserWaitDealNum(String userId) {
        return approveWaitDealDao.getUserWaitDealNum(userId);
    }

    /**
     * 保存双签附件导出通知
     * @param searchVo
     * @param path
     */
    @Override
    @ServiceTransactional
    public void addCompanyZipFileDeal(BsCompanySearchVo searchVo, String queryDateStr, String path) {
        ApproveWaitDeal entity = new ApproveWaitDeal();
        entity.setEnterpriseId(searchVo.getEnterpriseId());
        entity.setCreatedDate(new Date());
        entity.setRelaUserId(String.valueOf(searchVo.getUserId()));
        entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);
        entity.setReadFlg(BasConstants.READ_FLG_NOT);
        entity.setCompleteFlg("0");
        entity.setSource("访厂报告附件导出通知");
        String subject = String.format("【访厂报告附件导出通知】<br> 查询时间：%s<br> 有效时间：24小时<br> 下载链接：<a href='%s'>%s</a><br>", queryDateStr, path, path);
        entity.setSubject(subject);
        saveApproveWaitDeal(entity);
    }

    private void saveApproveWaitDeal(ApproveWaitDeal entity){
        approveWaitDealDao.save(entity);
        notifyWaitDealMessage(entity.getRelaUserId());
    }

    /**
     * 更新待办数量给前端
     * @param relaUserId
     */
    private void notifyWaitDealMessage(String relaUserId) {
        SCHEDULED_POOL.schedule(() -> {
            try {
                if (StringUtils.isBlank(relaUserId)) {
                    return;
                }
                Long userWaitDealNum = approveWaitDealDao.getUserWaitDealNum(relaUserId);
                WsMessage wsMessage = new WsMessage(Long.valueOf(relaUserId), WsMessage.MESSAGE_TYPE_W, userWaitDealNum);
                String result = HttpUtil.doPostJson("http://localhost:82/open/wsMessage/notifyWsMessage", JsonUtil.obj2Json(wsMessage));
                logger.info("notifyWaitDealMessage result:{}", result);
            } catch (Exception e) {
                logger.error("notifyWaitDealMessage error:", e);
            }
        }, 2, TimeUnit.SECONDS);
    }

    private void notifyWaitDealMessageList(List<Long> userIdList){
        if (CollectionUtils.isNotEmpty(userIdList)) {
            userIdList.forEach(userId -> {this.notifyWaitDealMessage(String.valueOf(userId));});
        }
    }

    private void notifyWaitDealMessageList2(List<String> userIdList){
        if (CollectionUtils.isNotEmpty(userIdList)) {
            userIdList.forEach(this::notifyWaitDealMessage);
        }
    }
    /**
     * 保存人保客户
     * @param searchVo
     * @param path
     */
    @Override
    @ServiceTransactional
    public void addCompanyCreditFileDeal(BsCompanySearchVo searchVo, String queryDateStr, String path) {
        ApproveWaitDeal entity = new ApproveWaitDeal();
        entity.setEnterpriseId(searchVo.getEnterpriseId());
        entity.setCreatedDate(new Date());
        entity.setRelaUserId(String.valueOf(searchVo.getUserId()));
        entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);
        entity.setReadFlg(BasConstants.READ_FLG_NOT);
        entity.setCompleteFlg("0");
        entity.setSource("人保客户附件导出通知");
        String subject = String.format("【人保客户附件导出通知】<br> 查询时间：%s<br> 有效时间：24小时<br> 下载链接：<a href='%s'>%s</a><br>", queryDateStr, path, path);
        entity.setSubject(subject);
        saveApproveWaitDeal(entity);
    }
}
