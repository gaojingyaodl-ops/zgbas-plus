package com.spt.bas.server.service.impl;

import com.google.common.base.Stopwatch;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.entity.RptBaseCost;
import com.spt.bas.report.client.entity.RptApplyBusinessPayVo;
import com.spt.bas.report.client.remote.IRptApplyBusinessPayRepClient;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.RptBaseCostMapper;
import com.spt.bas.server.service.IRptBaseCostService;
import com.spt.bas.server.util.BaseCostExcelImportUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RptBaseCostServiceImpl extends BaseService<RptBaseCost> implements IRptBaseCostService {

    @Value("${file.server.url}")
    private String fileServerUrl;

    @Autowired
    private RptBaseCostMapper rptBaseCostMapper;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private IRptApplyBusinessPayRepClient applyBusinessPayRepClient;

    @Override
    public BaseDao<RptBaseCost> getBaseDao() {
        return rptBaseCostMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<String> initData(String fileId) {
        List<String> messageList = new ArrayList<>();
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            if (StringUtils.isBlank(fileId)) {
                log.error("fileId 不可为空!");
                messageList.add("fileId 不可为空!");
                return messageList;
            }
            fileId = fileId.replace(",", "");
            if (StringUtils.isBlank(fileId)) {
                log.error("无效的fileId!");
                messageList.add("无效的fileId!");
                return messageList;
            }
            InputStream inputStream = null;
            try {
                URL url = new URL(fileServerUrl + "/view/download/" + fileId);
                inputStream = url.openStream();
            } catch (Exception e) {
                log.error("openStream error", e);
                messageList.add("openStream error");
                return messageList;
            }
            List<RptBaseCost> excelInfo = BaseCostExcelImportUtil.getExcelInfo(inputStream);
            log.info(JsonUtil.obj2Json(excelInfo));
            if (CollectionUtils.isEmpty(excelInfo)) {
                log.error("excelInfo is empty!");
                messageList.add("excelInfo is empty!");
                return messageList;
            }
            List<String> nickNameList = excelInfo.stream().map(RptBaseCost::getMatchUserName).distinct().collect(Collectors.toList());
            List<SysUserSdk> userSdkList = authOpenFacade.findUserByNickName(nickNameList);
            List<RptBaseCost> insertList = excelInfo.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getMatchUserName() + ";" + o.getBranchName()))), ArrayList::new)
            );
            String baseDate = null;
            for (RptBaseCost cost : insertList) {
                baseDate = cost.getBaseDate();
                if (CollectionUtils.isNotEmpty(userSdkList)) {
                    SysUserSdk userSdk = userSdkList.stream().filter(s -> s.getNickName().equals(cost.getMatchUserName())).findFirst().orElse(null);
                    if (Objects.nonNull(userSdk)) {
                        cost.setMatchUserId(userSdk.getUserId());
                    } else {
                        log.error("查找人员{}失败!", cost.getMatchUserName());
                        messageList.add("查找人员<" + cost.getMatchUserName() + ">失败!");
                        return messageList;
                    }
                } else {
                    log.error("查找全部人员失败，请核对人名!");
                    messageList.add("查找全部人员失败，请核对人名!");
                    return messageList;
                }
                String branchCd = BsDictUtil.getDictLabel("branchCd", cost.getBranchName());
                if (StringUtils.isBlank(branchCd)) {
                    log.error("查找人员{}所属区域失败!", cost.getMatchUserName());
                    messageList.add("查找人员<" + cost.getMatchUserName() + ">所属区域失败!");
                    return messageList;
                }
                cost.setBranchCd(branchCd);
            }

            List<RptBaseCost> originalList = rptBaseCostMapper.findByBaseDate(baseDate);
            originalList.forEach(
                    cost -> insertList.stream().filter(
                            insert -> StringUtils.equals(cost.getBranchName(), insert.getBranchName()) && StringUtils.equals(cost.getMatchUserName(), insert.getMatchUserName())).forEach(
                            insert -> rptBaseCostMapper.deleteById(cost.getId())));


            ExecutorService executorService = Executors.newCachedThreadPool();
            ExecutorCompletionService<Integer> execu = new ExecutorCompletionService<>(executorService);
            int taskSize = insertList.size();
            float bathSize = 25F;
            int bath = (int) Math.ceil((taskSize / bathSize));
            int syncSuccessNum = 0;
            for (int i = 0; i < bath; i++) {
                int start = (int) (bathSize * i);
                int end = (int) (start + bathSize);
                end = Math.min(end, taskSize);
                List<RptBaseCost> list = insertList.subList(start, end);
                execu.submit(() -> saveInitData(list));
            }
            for (int i = 0; i < bath; i++) {
                Future<Integer> future = execu.take();
                log.info("result:{} OK,syncSuccessNum:{}", i, future.get());
                syncSuccessNum += future.get();
            }
            executorService.shutdown();
            log.info("initDate 读取到数据{}条", insertList.size());
            log.info("initData 同步成功:{}条", syncSuccessNum);
            log.info("initData success耗时:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            messageList.add("读取"+insertList.size()+"条业务成本数据;");
            messageList.add("同步成功"+syncSuccessNum+"条业务成本数据;");
            messageList.add("耗时"+stopwatch.elapsed(TimeUnit.MILLISECONDS)*1.0/1000+"秒;");
            return messageList;
        } catch (Exception e){
            log.error("导入业务成本数据失败:{}", e.getMessage());
            messageList.add("导入业务成本数据失败,请联系管理员;");
            return messageList;
        }
    }

    @Override
    @ServerTransactional
    public void refreshUserEvectionCost(String baseDate) {
        List<RptBaseCost> resultList = rptBaseCostMapper.findByBaseDate(baseDate);
        if (CollectionUtils.isEmpty(resultList)) {
            return;
        }
        List<RptApplyBusinessPayVo> evectionCostList = applyBusinessPayRepClient.selectUserEvectionCost(baseDate);
        if (CollectionUtils.isEmpty(evectionCostList)) {
            return;
        }
        Map<Long, BigDecimal> evectionCostMap = evectionCostList.stream().collect(Collectors.toMap(RptApplyBusinessPayVo::getApplyUserId, RptApplyBusinessPayVo::getDealAmount));
        resultList.forEach( item ->{
            if (evectionCostMap.containsKey(item.getMatchUserId())) {
                item.setEvectionCost(evectionCostMap.get(item.getMatchUserId()));
            }
        });
        rptBaseCostMapper.saveAll(resultList);
    }

    @Override
    public RptBaseCost findSumPage(Map<String, Object> searchParams) {
        Specification<RptBaseCost> spec = WebUtil.buildSpecification(searchParams);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery();
        Root<RptBaseCost> root = query.from(RptBaseCost.class);
        CriteriaQuery<?> cq = query.where(spec.toPredicate(root, query, cb)).multiselect(
                cb.sum(root.get("wages")), cb.sum(root.get("commission")), cb.sum(root.get("otherCost")),
                cb.sum(root.get("socialSecurity")), cb.sum(root.get("providentFund")), cb.sum(root.get("evectionCost")),
                cb.sum(root.get("totalCost")));
        TypedQuery<?> tq = em.createQuery(cq);
        Object[] result = ((Object[]) tq.getSingleResult());
        RptBaseCost sum = new RptBaseCost();
        BigDecimal wages = (BigDecimal) result[0];
        BigDecimal commission = (BigDecimal) result[1];
        BigDecimal otherCost = (BigDecimal) result[2];
        BigDecimal socialSecurity = (BigDecimal) result[3];
        BigDecimal providentFund = (BigDecimal) result[4];
        BigDecimal evectionCost = (BigDecimal) result[5];
        BigDecimal totalCost = (BigDecimal) result[6];
        sum.setWages(wages);
        sum.setCommission(commission);
        sum.setOtherCost(otherCost);
        sum.setSocialSecurity(socialSecurity);
        sum.setProvidentFund(providentFund);
        sum.setEvectionCost(evectionCost);
        sum.setTotalCost(totalCost);
        return sum;
    }

    @Override
    public String getCostbaseByImportExcel(String fileId) {
        String str="0";
        if (StringUtils.isNotBlank(fileId)) {
            fileId = fileId.replace(",", "");
            if(StringUtils.isNotBlank(fileId)){
                InputStream inputStream = null;
                try {
                    URL url = new URL(fileServerUrl + "/view/download/" + fileId);
                    inputStream = url.openStream();
                    List<RptBaseCost> excelInfo = BaseCostExcelImportUtil.getExcelInfo(inputStream);
                    if(CollectionUtils.isNotEmpty(excelInfo)){
                        List<RptBaseCost> originalList = rptBaseCostMapper.findByBaseDate(excelInfo.get(0).getBaseDate());
                        if(CollectionUtils.isNotEmpty(originalList)){
                           str="1";
                        }
                    }
                } catch (Exception e) {
                    str = "0";
                }
            }
        }
        return str;
    }

    @Override
    public List<RptBaseCost> findRptBaseCostByBaseDate(Date baseDate) {
        String yearMonth = DateTimeFormatter.ofPattern("yyyy-MM")
                .format(baseDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate());
        return rptBaseCostMapper.findByBaseDate(yearMonth);
    }

    private int saveInitData(List<RptBaseCost> list) {
        rptBaseCostMapper.saveAll(list);
        return list.size();
    }
}
