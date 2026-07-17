package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.google.common.base.Splitter;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IApplyCtrDcsxClinent;
import com.spt.bas.client.remote.IApplyMatchClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.*;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveContentsService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Component("applyDcsxService")
@Transactional(readOnly = true)
public class ApplyDcsxServiceImpl extends BaseService<ApplyCtrDCSX> implements IApplyDcsxService, IPmApproveListener,IPmService {
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrProductDao ctrProductDao;
    @Autowired
    private DcsxHisDao dcsxHisDao;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IPmApproveContentsService pmApproveContentsService;
    @Autowired
    private IApplyPayService applyPayService;
    @Override
    public BaseDao<ApplyCtrDCSX> getBaseDao() {
        return applyDcsxDao;
    }
    @Resource
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private IBsContractTemplateService bsContractTemplateService;
    @Resource
    private ApplyChargeSalesService applyChargeSalesService;
    @Resource
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Resource
    private IBsCompanyOurService bsCompanyOurService;
    @Resource
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IApplyCtrDcsxClinent applyCtrDcsxClinent;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Autowired
    private ICtrContractClient contractClient;
    @Value("${file.show.url}")
    private String fileShowUrl;
    @Autowired
    private BsDictDataDao bsDictDataDao;
    @Autowired
    private IApplyMatchClient applyMatchClient;
    @Autowired
    private ISealUsageDCSXService sealUsageDCSXService;
    @Autowired
    private ICtrContractTextService contractTextService;
    @Autowired
    private PmProcessDao processDao;

    @Override
    public Page<DcsxShowVo> findPageContract(ContractSearchVo queryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        String businessType = queryVo.getBusinessType();
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) || StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, businessType)) {
            List<Sort.Order> sorts = new ArrayList<>();
            //sorts.add(new Sort.Order(Direction.ASC, "source"));
            sorts.add(new Sort.Order(Sort.Direction.DESC, "id"));
            sort = Sort.by(sorts);
        }
        Long deptLeaderId = queryVo.getDeptLeaderId();
        PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);//分页
        Specification<ApplyCtrDCSX> spec_userId = WebUtil.buildSpecification("EQL_matchUserId_OR_EQL_cooperationMatchUserId", queryVo.getUserId());
        Specification<ApplyCtrDCSX> spec_viewPreSell = WebUtil.buildSpecification("EQS_source", BasConstants.APPLY_TYPE_L);//预售

        String searchType = queryVo.getSearchType();// A：查询本中心所有hetong  P:查看本中心所有预售合同
        String payCondition = queryVo.getPayCondition(); // 付款条件
        String receiveCondition = queryVo.getReceiveCondition(); // 收款条件
        String warehouseCondition = queryVo.getWarehouseCondition();// 出入库条件
        String billCondition = queryVo.getBillCondition(); // 收票条件
        String invoiceBillCondition = queryVo.getInvoiceBillCondition(); // 开票条件
        String contractTypes = queryVo.getContractTypes();
        String status = queryVo.getStatus();
        Boolean saasContractFlg = queryVo.getSaasContractFlg();
        Boolean funderFlg = queryVo.getFunderFlg();
        String contractSource = queryVo.getContractSource();
        Specification<ApplyCtrDCSX> spe = WebUtil.buildSpecification(queryVo.getSearchParams());
        Specification<ApplyCtrDCSX> spec = dealFindCondition(contractSource, saasContractFlg, payCondition, receiveCondition,
                warehouseCondition, billCondition, invoiceBillCondition, contractTypes, status, queryVo.getProductType(), queryVo.getHgMatchUserIdList(), spe);
        List<Long> myDeptId = authOpenFacade.findMyDeptId(queryVo.getUserId());
        List<Long> allDeptId = authOpenFacade.findMyDeptId(deptLeaderId);

        if (!queryVo.isAdmin() && Boolean.FALSE.equals(funderFlg)) {
            if (allDeptId.size() == 0 || myDeptId.size() == 0) {
                //spec = Specification.where(spec).and(spec_userId);
                myDeptId.add(0L);
                allDeptId.add(0L);
            }
            if (StringUtils.equals("A", searchType)) {
                //可以查看本中心所有合同
                Specification<ApplyCtrDCSX> spec_department = WebUtil.buildSpecification("INL_deptId", allDeptId);
                Specification<ApplyCtrDCSX> spec_department_userId = Specification.where(spec_userId).or(spec_department);
                spec = Specification.where(spec).and(spec_department_userId);
            } else if (StringUtils.equals("P", searchType)) {
                //可以查看本中心所有预售合同
                Specification<ApplyCtrDCSX> newSpec;
                Specification<ApplyCtrDCSX> spec_department_mydept = WebUtil.buildSpecification("INL_deptId", myDeptId);
                Specification<ApplyCtrDCSX> spec_department_allDept = WebUtil.buildSpecification("INL_deptId", allDeptId);
                Specification<ApplyCtrDCSX> spec_department_userId = Specification.where(spec_userId).or(spec_department_mydept);
                Specification<ApplyCtrDCSX> spec_department_viewPreSell = Specification.where(spec_department_allDept).and(spec_viewPreSell);
                newSpec = Specification.where(spec_department_userId).or(spec_department_viewPreSell);
                spec = Specification.where(spec).and(newSpec);
            } else if (StringUtils.equals("D", searchType)) {
                // 业务助理查询本业务部所有合同
                //可以查看所属于自己的合同
                Specification<ApplyCtrDCSX> spec_department = WebUtil.buildSpecification("INL_deptId", queryVo.getDeptIdList());
                Specification<ApplyCtrDCSX> spec_department_userId = Specification.where(spec_userId).or(spec_department);
                spec = Specification.where(spec).and(spec_department_userId);
            } else {
                //可以查看所属于自己的合同
                Specification<ApplyCtrDCSX> spec_department = WebUtil.buildSpecification("INL_deptId", myDeptId);
                Specification<ApplyCtrDCSX> spec_department_userId = Specification.where(spec_userId).or(spec_department);
                spec = Specification.where(spec).and(spec_department_userId);
            }
        }
        Page<ApplyCtrDCSX> page = getBaseDao().findAll(spec, pageRequest);


        List<Long> lstIds = new ArrayList<>();
        List<DcsxShowVo> voList = new ArrayList<DcsxShowVo>();
        int i = 0;
        for (ApplyCtrDCSX ctr : page.getContent()) {
            i++;
            Long contractId = ctr.getId();
            DcsxShowVo vo = new DcsxShowVo();
            vo.setPairId((long) i);
            BeanUtils.copyProperties(ctr, vo);
            lstIds.add(contractId);
            if (StringUtils.isNotBlank(ctr.getFileId())) {
                List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(ctr.getFileId());
                if (!CollectionUtils.isEmpty(idList)){
                    vo.setFileId(fileShowUrl + "/view/show/" + idList.get(idList.size() - 1));
                }
            }
            String protocolFileUrl = "";
            if (StringUtils.isNotBlank(vo.getProtocolFileId())) {
                List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getProtocolFileId());
                protocolFileUrl = fileShowUrl + "/view/show/" + idList.get(idList.size() - 1);
            }
            vo.setProtocolFileUrl(protocolFileUrl);
            voList.add(vo);
        }
        // sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
        PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
        Page<DcsxShowVo> pageVo = new PageImpl<>(voList, pageRequest_new, page.getTotalElements());




        return pageVo;
    }

    @Override
    public ApplyCtrDCSX sumPageContract(ContractSearchVo queryVo) {
        Long deptLeaderId = queryVo.getDeptLeaderId();
        String searchType = queryVo.getSearchType();// A：查询本中心所有hetong  P:查看本中心所有预售合同
        String payCondition = queryVo.getPayCondition(); // 收付款条件
        String receiveCondition = queryVo.getReceiveCondition(); // 收款条件
        String warehouseCondition = queryVo.getWarehouseCondition();// 出入库条件
        String billCondition = queryVo.getBillCondition(); // 收票条件
        String invoiceBillCondition = queryVo.getInvoiceBillCondition(); // 开票条件
        String contractTypes = queryVo.getContractTypes();
        String status = queryVo.getStatus();
        Boolean saasContractFlg = queryVo.getSaasContractFlg();
        Boolean funderFlg = queryVo.getFunderFlg();
        String contractSource = queryVo.getContractSource();
        Specification<ApplyCtrDCSX> spe = WebUtil.buildSpecification(queryVo.getSearchParams());
        Specification<ApplyCtrDCSX> spec = dealFindCondition(contractSource, saasContractFlg, payCondition, receiveCondition,
                warehouseCondition, billCondition, invoiceBillCondition, contractTypes, status,queryVo.getProductType(), queryVo.getHgMatchUserIdList(), spe);
        // Specification<ApplyCtrDCSX> spec = WebUtil.buildSpecification(queryVo.getSearchParams());
        Specification<ApplyCtrDCSX> spec_userId = WebUtil.buildSpecification("EQL_matchUserId", queryVo.getUserId());
        Specification<ApplyCtrDCSX> spec_viewPreSell = WebUtil.buildSpecification("EQS_source", BasConstants.APPLY_TYPE_L);
        List<Long> myDeptId = authOpenFacade.findMyDeptId(queryVo.getUserId());
        List<Long> allDeptId = authOpenFacade.findMyDeptId(deptLeaderId);
        if (!queryVo.isAdmin() && Boolean.FALSE.equals(funderFlg)) {
            if (allDeptId.size() == 0 || myDeptId.size() == 0) {
                //spec = Specification.where(spec).and(spec_userId);
                myDeptId.add(0L);
                allDeptId.add(0L);
            }
            if (StringUtils.equals("A", searchType)) {
                //可以查看本中心所有合同
                Specification<ApplyCtrDCSX> spec_department = WebUtil.buildSpecification("INL_deptId", allDeptId);
                Specification<ApplyCtrDCSX> spec_department_userId = Specification.where(spec_userId).or(spec_department);
                spec = Specification.where(spec).and(spec_department_userId);
            } else if (StringUtils.equals("P", searchType)) {
                //可以查看本中心所有预售合同
                Specification<ApplyCtrDCSX> newSpec;
                Specification<ApplyCtrDCSX> spec_department_mydept = WebUtil.buildSpecification("INL_deptId", myDeptId);
                Specification<ApplyCtrDCSX> spec_department_allDept = WebUtil.buildSpecification("INL_deptId", allDeptId);
                Specification<ApplyCtrDCSX> spec_department_userId = Specification.where(spec_userId).or(spec_department_mydept);
                Specification<ApplyCtrDCSX> spec_department_viewPreSell = Specification.where(spec_department_allDept).and(spec_viewPreSell);
                newSpec = Specification.where(spec_department_userId).or(spec_department_viewPreSell);
                spec = Specification.where(spec).and(newSpec);
            } else if (StringUtils.equals("D", searchType)) {
                // 业务助理查询本业务部所有合同
                Specification<ApplyCtrDCSX> spec_department = WebUtil.buildSpecification("INL_deptId", queryVo.getDeptIdList());
                Specification<ApplyCtrDCSX> spec_department_userId = Specification.where(spec_userId).or(spec_department);
                spec = Specification.where(spec).and(spec_department_userId);
            } else {
                //可以查看所属于自己的合同
                Specification<ApplyCtrDCSX> spec_department = WebUtil.buildSpecification("INL_deptId", myDeptId);
                Specification<ApplyCtrDCSX> spec_department_userId = Specification.where(spec_userId).or(spec_department);
                spec = Specification.where(spec).and(spec_department_userId);
            }
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery();
        Root<ApplyCtrDCSX> root = query.from(ApplyCtrDCSX.class);
        CriteriaQuery<?> cq = query.where(spec.toPredicate(root, query, cb)).multiselect(
                cb.sum(root.get("totalNumber")), cb.sum(root.get("warehouseNumber")), cb.sum(root.get("totalAmount")),
                cb.sum(root.get("dealedAmount")), cb.sum(root.get("billedAmount")), cb.sum(root.get("bondAmount")),
                cb.sum(root.get("refundAmount")));
        TypedQuery<?> tq = em.createQuery(cq);
        Object[] result = ((Object[]) tq.getSingleResult());
        ApplyCtrDCSX sum = new ApplyCtrDCSX();
        BigDecimal totalNumber = (BigDecimal) result[0];
        BigDecimal warehouseNumber = (BigDecimal) result[1];
        BigDecimal totalAmount = (BigDecimal) result[2];
        BigDecimal dealedAmount = (BigDecimal) result[3];
        BigDecimal billedAmount = (BigDecimal) result[4];
        BigDecimal bondAmount = (BigDecimal) result[5];
        BigDecimal refundAmount = (BigDecimal) result[6];
        sum.setTotalNumber(totalNumber);
        sum.setWarehouseNumber(warehouseNumber);
        sum.setTotalAmount(totalAmount);
        sum.setBilledAmount(billedAmount);
        sum.setBondAmount(bondAmount);
        sum.setDealedAmount(dealedAmount);
        return sum;
    }

    @Override
    public ApplyDcsxChooseVo findById(Long contractId) {
        ApplyDcsxChooseVo vo = new ApplyDcsxChooseVo();
        if (contractId != null) {
            ApplyCtrDCSX entity = this.getEntity(contractId);
            BeanUtils.copyProperties(entity, vo);
            ApplyCtrDCSX apply = applyDcsxDao.findOne(contractId);
            if (apply != null) {
                vo.setApplyBillAmount(apply.getBilledAmount());
            }
            if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, entity.getContractType())) {
                //查询对应采购合同对方企业ID
                String linkContractId = entity.getLinkContractId();
                if (StringUtils.isNotBlank(linkContractId)) {
                    List<String> sellIdList = Splitter.on(",").omitEmptyStrings().splitToList(linkContractId);
                    List<Long> buyContractList = sellIdList.stream().map(a -> Long.valueOf(a)).collect(Collectors.toList());
                    if (buyContractList != null && buyContractList.size() > 0) {
                        Long buyContractId = buyContractList.get(0);
                        ApplyCtrDCSX buyContract = this.getEntity(buyContractId);
                        vo.setBuyCompanyId(buyContract.getCompanyId());
                    }
                }
            }
        }
        return vo;
    }

    @Override
    public ApplyCtrDCSX findByDCSXApproveId(Long approveId) {
        return applyDcsxDao.findByDCSXApproveId(approveId);
    }

    @Override
    public List<ApplyCtrDCSX> findByDCSXApproveIdAll(Long approveId) {
        return applyDcsxDao.findByDCSXApproveIdAll(approveId);
    }

    private Specification<ApplyCtrDCSX> dealFindCondition(String contractSource, Boolean saasContractFlg, String payCondition,String receiveCondition,
                                                          String warehouseCondition, String billCondition,String invoiceBillCondition,
                                                          String contractTypes, String status, String productTypeCondition, List<Long> matchUserIdList,
                                                          Specification<ApplyCtrDCSX> spec) {
        // 未出入库：WN 已出入库：WY
        if (BasConstants.APPLY_TYPE_WN.equals(warehouseCondition)) {
            Specification<ApplyCtrDCSX> spec_warehouseNumber_N = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Predicate predicate = cb.gt(root.get("totalNumber"), root.get("warehouseNumber"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_warehouseNumber_N);
        }
        if (BasConstants.APPLY_TYPE_WY.equals(warehouseCondition)) {
            Specification<ApplyCtrDCSX> spec_warehouseNumber_Y = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Predicate predicate = cb.equal(root.get("totalNumber"), root.get("warehouseNumber"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_warehouseNumber_Y);
        }

        // 未收付款：PN 已收付款：PY
        if (BasConstants.APPLY_TYPE_PN.equals(payCondition)) {
            Specification<ApplyCtrDCSX> spec_pay_N = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"), root.get("interestAmount"));
                    Predicate predicate = cb.gt(sumAmount, root.get("dealedAmount"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_pay_N);
        }
        if (BasConstants.APPLY_TYPE_PY.equals(payCondition)) {
            Specification<ApplyCtrDCSX> spec_pay_Y = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"), root.get("interestAmount"));
                    Predicate predicate = cb.equal(sumAmount, root.get("dealedAmount"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_pay_Y);
        }

        // 未收款：PN 已收款：PY
        if (BasConstants.APPLY_TYPE_PN.equals(receiveCondition)) {
            Specification<ApplyCtrDCSX> spec_pay_N = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"), root.get("interestAmount"));
                    Predicate predicate = cb.gt(sumAmount, root.get("receiveAmount"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_pay_N);
        }
        if (BasConstants.APPLY_TYPE_PY.equals(receiveCondition)) {
            Specification<ApplyCtrDCSX> spec_pay_Y = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"), root.get("interestAmount"));
                    Predicate predicate = cb.equal(sumAmount, root.get("receiveAmount"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_pay_Y);
        }

        // 未收票：BN 已收票：BY
        if (BasConstants.APPLY_TYPE_BN.equals(billCondition)) {
            Specification<ApplyCtrDCSX> spec_bill_N = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"), root.get("interestAmount"));
                    Predicate predicate = cb.gt(sumAmount, root.get("billedAmount"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_bill_N);
        }
        if (BasConstants.APPLY_TYPE_BY.equals(billCondition)) {
            Specification<ApplyCtrDCSX> spec_bill_Y = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"), root.get("interestAmount"));
                    Predicate predicate = cb.equal(sumAmount, root.get("billedAmount"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_bill_Y);
        }
        
        // 未开票：BN 已开票：BY
        if (BasConstants.APPLY_TYPE_BN.equals(invoiceBillCondition)) {
            Specification<ApplyCtrDCSX> spec_bill_N = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"), root.get("interestAmount"));
                    Predicate predicate = cb.gt(sumAmount, root.get("invoiceBillAmount"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_bill_N);
        }
        if (BasConstants.APPLY_TYPE_BY.equals(invoiceBillCondition)) {
            Specification<ApplyCtrDCSX> spec_bill_Y = new Specification<ApplyCtrDCSX>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"), root.get("interestAmount"));
                    Predicate predicate = cb.equal(sumAmount, root.get("invoiceBillAmount"));
                    return predicate;
                }
            };
            spec = Specification.where(spec).and(spec_bill_Y);
        }

        // 判断有效无效
        if (StringUtils.isNotBlank(contractTypes)) {
            Specification<ApplyCtrDCSX> spec_actives = null;
            if (BasConstants.DICT_TYPE_Y.equals(contractTypes)) {
                spec_actives = WebUtil.buildSpecification("NEQS_contractStatus", BasConstants.CONTRACTSTATUS_C);
            } else if (BasConstants.DICT_TYPE_N.equals(contractTypes)) {
                spec_actives = WebUtil.buildSpecification("EQS_contractStatus", BasConstants.CONTRACTSTATUS_C);
            }
            if (spec_actives != null) {
                spec = Specification.where(spec).and(spec_actives);
            }
        }
        // 判断有效无效
        if (StringUtils.isNotBlank(status)) {
            Specification<ApplyCtrDCSX> spec_actives = null;
            if (BasConstants.DICT_TYPE_Y.equals(status)) {
                spec_actives = WebUtil.buildSpecification("NEQS_status", BasConstants.CONTRACTSTATUS_C);
            } else if (BasConstants.DICT_TYPE_N.equals(status)) {
                spec_actives = WebUtil.buildSpecification("EQS_status", BasConstants.CONTRACTSTATUS_C);
            }
            if (spec_actives != null) {
                spec = Specification.where(spec).and(spec_actives);
            }
        }

        // 判断是否为化工
//        if (StringUtils.isNotBlank(productType)) {
//            Specification<ApplyCtrDCSX> spec_productType = null;
//            if (BasConstants.DICT_PRODUCT_TYPE_HG.equals(productType)) {
//                spec_productType = WebUtil.buildSpecification("LIKES_productBrand", BasConstants.DICT_PRODUCT_TYPE_HG);
//            } else if (BasConstants.DICT_PRODUCT_TYPE_NHG.equals(productType)) {
//                spec_productType = buildSpecificationNotLike("productBrand", BasConstants.DICT_PRODUCT_TYPE_HG);
//
//            }
//            if (spec_productType != null) {
//                spec = Specification.where(spec).and(spec_productType);
//            }
//        }

        if (BasConstants.PRODUCT_TYPE_HG.equals(productTypeCondition)) {
            // 如果 matchUserIdList 不为空，则创建一个包含 matchUserId 的 IN 查询条件
            if (matchUserIdList != null && !matchUserIdList.isEmpty()) {
                Specification<ApplyCtrDCSX> specInMatchUserId = new Specification<ApplyCtrDCSX>() {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        return root.get("matchUserId").in(matchUserIdList);
                    }
                };
                spec = Specification.where(spec).and(specInMatchUserId);
            }
        }
        if (BasConstants.PRODUCT_TYPE_NHG.equals(productTypeCondition)) {
            if (matchUserIdList != null && !matchUserIdList.isEmpty()) {
                Specification<ApplyCtrDCSX> specNotInMatchUserId = new Specification<ApplyCtrDCSX>() {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public Predicate toPredicate(Root<ApplyCtrDCSX> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        return cb.not(root.get("matchUserId").in(matchUserIdList));
                    }
                };
                spec = Specification.where(spec).and(specNotInMatchUserId);
            }

        }

        //是否为查看saas合同
        if (saasContractFlg && StringUtils.isNotBlank(contractSource)) {
            Specification<ApplyCtrDCSX> spec_viewSaasContract = WebUtil.buildSpecification("EQS_source", contractSource);
            spec = Specification.where(spec).and(spec_viewSaasContract);
        }
        return spec;
    }

    public static Specification<ApplyCtrDCSX> buildSpecificationNotLike(String field, String value) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.notLike(root.get(field), value + "%");
        };
    }



    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyCtrDCSX entity = (ApplyCtrDCSX) pmEntity;
            String dealAmount = NumberUtil.formatNumber(entity.getTotalAmount(), "#.##");
            String subject = String.format("%s %s %s元  %s", entity.getContractNo(),
                    entity.getCompanyName(), dealAmount, entity.getOurCompanyName());
            return subject;
        }
        return null;
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        return null;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyCtrDCSX invoice = applyDcsxDao.findOne(approve.getBizId());
        }

    }
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public ApplyCtrDCSX findByContractNo(String contractNo) {
        return applyDcsxDao.findByContractNo(contractNo);
    }

    @Override
    @ServerTransactional
    public void updateFileId(UpdateDcsxContractVo vo) {
        BigDecimal newTotalAmount = vo.getNewTotalAmount();
        if(newTotalAmount == null){
            newTotalAmount = vo.getTotalAmount();
        }
        BigDecimal newTotalNumber = vo.getNewTotalNumber();
        if(newTotalNumber == null){
            newTotalNumber = vo.getTotalNumber();
        }
        BigDecimal newDealPrice = vo.getNewDealPrice();
        if(newDealPrice == null){
            newDealPrice = vo.getDealPrice();
        }
        Date newPayFullTime = vo.getNewPayFullTime();
        if(newPayFullTime == null){
            newPayFullTime = vo.getPayFullTime();
        }
        applyDcsxDao.updateFileId(vo.getId(), vo.getFileId(),newTotalAmount,newTotalNumber,newDealPrice,newPayFullTime);
        List<ApplyPay> applyPayList = applyPayService.findByContractId(vo.getId());
        // 修改付款申请单
        if(!CollectionUtils.isEmpty(applyPayList)) {
            for (ApplyPay applyPay : applyPayList) {
                if(StringUtils.equals(applyPay.getPayType(),BasConstants.PAY_TYPE_ALL)){
                    try {
                        PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(applyPay.getApproveId());
                        String subject = pmApprove.getSubject();

                        pmApprove.setSubject(getNewSubject(subject,newTotalAmount));
                        pmApproveService.save(pmApprove);

                        applyPay.setTotalAmount(newTotalAmount);
                        applyPay.setUnpayedAmount(newTotalAmount);
                        applyPay.setPayAmount(newTotalAmount);
                        applyPay.setPayDate(newPayFullTime);
                        applyPayService.save(applyPay);
                    } catch (Exception e){
                        logger.error("付款申请单修改失败:{}",e);
                    }
                    break;
                }
            }
        }
        // 代采赊销更新合同附件同步修改代采赊销盖章附件
        String fileId = vo.getFileId();
        String oldFileId = vo.getOldFileId();
        
        if(StringUtils.isNotBlank(fileId)) {
            if(!StringUtils.equals(fileId,oldFileId)){
                ApplyCtrDCSX ctrDCSX = applyDcsxDao.findOne(vo.getId());
                List<SealUsageDCSX> sealUsageDCSXList = sealUsageDCSXService.findSealUsageDcsxByContractNo(ctrDCSX.getContractNo());
                if(!CollectionUtils.isEmpty(sealUsageDCSXList)) {
                    for (SealUsageDCSX sealUsageDCSX : sealUsageDCSXList) {
                        PmApprove entity = pmApproveService.getEntity(sealUsageDCSX.getApproveId());
                        if(Objects.nonNull(entity) && !StringUtils.equals("C",entity.getStatus())) {
                            // 修改代采赊销盖章附件
                            sealUsageDCSXService.updateFileId(sealUsageDCSX.getId(),sealUsageDCSX.getFileId() + fileId);
                            PmApproveContents pmApproveContents = pmApproveContentsService.findByApproveId(entity.getId());
                            if(Objects.nonNull(pmApproveContents)) {
                                // 修改审批内容附件
                                pmApproveContentsService.updateFileId(pmApproveContents.getId(),pmApproveContents.getFileId()+fileId);
                            }
                        }

                    }
                }
            }
            
        }
        
        // 保存修改流水
        DcsxHis dcsxHis = new DcsxHis();
        dcsxHis.setContractId(vo.getId());
        dcsxHis.setNewFileId(vo.getFileId());
        dcsxHis.setOldFileId(vo.getOldFileId());
        dcsxHis.setNewTotalAmount(newTotalAmount);
        dcsxHis.setOldTotalAmount(vo.getTotalAmount());
        dcsxHis.setNewTotalNumber(newTotalNumber);
        dcsxHis.setOldTotalNumber(vo.getTotalNumber());
        dcsxHis.setNewDealPrice(newDealPrice);
        dcsxHis.setOldDealPrice(vo.getDealPrice());
        dcsxHis.setNewPayFullTime(newPayFullTime);
        dcsxHis.setOldPayFullTime(vo.getPayFullTime());
        dcsxHis.setMatchUserId(vo.getMatchUserId());
        dcsxHis.setMatchUserName(vo.getMatchUserName());

        dcsxHisDao.save(dcsxHis);
    }

    @Override
    public void updateStatus(Long id, BigDecimal amount, String contractStatus ,String status) {
        applyDcsxDao.updateStatus(id,amount,contractStatus,status);
    }

    /**
     * 已确认收货数量
     *
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public void addConfirmReceiptNumber(Long contractId, BigDecimal dealAmount,Date confirmReceiptDate, String approveNo) throws ApplicationException {
        ApplyCtrDCSX applyCtrDCSX = applyDcsxDao.findOne(contractId);
        BigDecimal confirmReceiveNumber = BigDecimal.ZERO;
        if (applyCtrDCSX.getConfirmReceiveNumber() != null) {
            confirmReceiveNumber = applyCtrDCSX.getConfirmReceiveNumber();
        }
        // 已收货
        BigDecimal curRealNumber = confirmReceiveNumber.add(dealAmount);
        applyCtrDCSX.setConfirmReceiveNumber(curRealNumber);
        if (curRealNumber.compareTo(applyCtrDCSX.getTotalNumber()) >= 0) {
            applyCtrDCSX.setConfirmReceiptFlg(true);
            applyCtrDCSX.setConfirmDate(confirmReceiptDate);
        } else {
            applyCtrDCSX.setConfirmReceiptFlg(false);
            applyCtrDCSX.setConfirmDate(null);
        }
        applyDcsxDao.save(applyCtrDCSX);

    }

    @Override
    public String getDcsxTemplateContract(ApplyCtrDCSX entity)  {
        DcContractText tContract = new DcContractText();
        BeanUtils.copyProperties(entity, tContract);

        String contractNo = entity.getContractNo();
        String wrapSpecs = entity.getWrapSpecs();
        if(StringUtils.isNotBlank(wrapSpecs)) {
            tContract.setWrapSpecs(DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT, wrapSpecs));
        }
        
        String companyName = tContract.getCompanyName();
        String ourCompanyName = tContract.getOurCompanyName();
        
        
        String rates = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_TAX_RATES, BasConstants.DICT_TYPE_TAX_RATES_SL);
        BigDecimal taxRates = BigDecimal.ONE.add(new BigDecimal(rates));
        setOurCompanyParam(tContract, ourCompanyName);
        setCompanyParam(tContract, companyName);
        BsCompany bsCompany = bsCompanyService.findByCompanyName(companyName);
        if(Objects.nonNull(bsCompany)){
            tContract.setAddress(bsCompany.getAddress());
        }
        tContract.setContractTimeStr(DateUtil.format(entity.getContractTime(), "yyyy年MM月dd日"));
        BigDecimal dealPriceNoTax = entity.getDealPrice().divide(taxRates, 2, RoundingMode.HALF_UP);
        tContract.setDealPriceNoTax(dealPriceNoTax);
        BigDecimal totalPriceNoTax = dealPriceNoTax.multiply(entity.getTotalNumber());
        tContract.setTotalPriceNoTax(totalPriceNoTax);
        String cnMoney = RmbUtil.number2Chinese(entity.getTotalAmount());
        tContract.setCnMoney(cnMoney);
        String deliveryType = entity.getDeliveryType();
        String deliveryTypeDictName = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, deliveryType);
        if (StringUtils.isNotBlank(deliveryTypeDictName)) {
            tContract.setDeliveryType(deliveryTypeDictName);
        } else {
            tContract.setDeliveryType(deliveryType);
        }
        if (StringUtils.equals("需方自提", deliveryType) || StringUtils.equals(BasConstants.DICT_TYPE_BUYDELIVERY_Z, deliveryType)) {
            tContract.setTransAmountRemark("如需方自提，提货费用自理；若产生入库费用，由需方承担。仓储费自交割日期免三天，超期仓储费和损耗由需方承担。");
        } else {
            tContract.setTransAmountRemark("如供方配送，费用由供方承担。");
        }
        tContract.setPayFullTimeStr(DateUtil.format(entity.getPayFullTime(), "yyyy年MM月dd日"));
        ApplyCtrDCSX byContractNo = applyCtrDcsxClinent.findByContractNo(entity.getContractNo());
        // 奥顺宇特殊处理
        if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, companyName)) {
            BsBankVo specialBank = applyChargeSalesService.getSpecialBank(tContract.getEnterpriseId());
            if (Objects.nonNull(specialBank)) {
                tContract.setOurCompanyBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
                tContract.setOurCompanyBankNo(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());
            }
            List<CtrContract> contractList = ctrContractDao.findByApproveId(byContractNo.getApproveId());
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(contractList)){
                CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_STATUS_S, c.getContractType())).findFirst().orElse(null);
                if (Objects.nonNull(sellContract) && StringUtils.isNotBlank(sellContract.getBusinessTypeDcsx()) && sellContract.getBusinessTypeDcsx().contains("HDFK")){
                    BsCompanyOur companyOur = bsCompanyOurService.findByCompanyName(companyName);
                    if (Objects.nonNull(companyOur)){
                        tContract.setCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
                        tContract.setCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
                    }
                }
            }
        }
        if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName)) {
            BsBankVo specialBank = applyChargeSalesService.getSpecialBank(tContract.getEnterpriseId());
            if (Objects.nonNull(specialBank)) {
                tContract.setCompanyBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
                tContract.setCompanyBankNo(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());
            }
            List<CtrContract> contractList = ctrContractDao.findByApproveId(byContractNo.getApproveId());
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(contractList)){
                CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_STATUS_S, c.getContractType())).findFirst().orElse(null);
                if (Objects.nonNull(sellContract) && StringUtils.isNotBlank(sellContract.getBusinessTypeDcsx()) && sellContract.getBusinessTypeDcsx().contains("HDFK")){
                    BsCompanyOur companyOur = bsCompanyOurService.findByCompanyName(ourCompanyName);
                    if (Objects.nonNull(companyOur)){
                        tContract.setOurCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
                        tContract.setOurCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
                    }
                }
            }
        }

        CtrContract contract = ctrContractDao.findByApproveIdAndContractType(entity.getApproveId(), BasConstants.CONTRACT_TYPE_S);
        if (Objects.nonNull(contract)){
            List<CtrProduct> productList = ctrProductDao.findByCtrContractId(contract.getId());
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(productList)){
                CtrProduct product = productList.get(0);
                tContract.setProductName(product.getProductName());
                tContract.setBrandNumber(product.getBrandNumber());
                tContract.setDealNumber(product.getDealNumber()+"");
            }
        }
        tContract.setDealPrice(entity.getDealPrice()+"");
        tContract.setTotalPrice(entity.getTotalAmount() + "");
        tContract.setDeliAddr(entity.getDeliveryAddr());
        tContract.setDeliveryDateStr(DateUtil.format(entity.getDeliveryDateTo(), "yyyy年MM月dd日"));


        PmApprove byApproveNo = pmApproveClient.findByApproveNo(byContractNo.getBudgetNo());
        List<CtrContract> byApproveId = contractClient.findByApproveId(byApproveNo.getId());
        ApplyMatch applyMatch = applyMatchClient.findByApproveId(byApproveNo.getId());
        List<CtrContract> s1 = byApproveId.stream().filter(s -> s.getContractType().equals("B")).collect(Collectors.toList());
        List<CtrContract> s2 = byApproveId.stream().filter(s -> s.getContractType().equals("S")).collect(Collectors.toList());
        CtrContract buyContract = s1.get(0);
        CtrContract sellContract = s2.get(0);
        BigDecimal bondAmount = buyContract.getBondAmount();
        if(bondAmount.compareTo(BigDecimal.ZERO)>0){
            //付定金
            tContract.setClause("需要于"+DateUtil.format(buyContract.getPayBondTime(), "yyyy年MM月dd日")+"支付定金"+bondAmount+"元，交货前付清全款");
        }else{
            //付全款
            tContract.setClause("需方于合同签订之日起一周内支付货款");
        }

        String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,sellContract.getPayType());
        if(StringUtils.isBlank(payType)) {
            tContract.setPayMode("电汇");
        } else {
            tContract.setPayMode(payType);
        }
        tContract.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT, sellContract.getQualityStandard()));
        tContract.setContractModel(sellContract.getContractModel());

        Date buyPayFullTime = entity.getBuyPayFullTime();
        if (buyPayFullTime != null) {
            tContract.setPayRemaindTime(DateUtil.format(buyPayFullTime, "yyyy年MM月dd日"));
        } else {
            tContract.setPayRemaindTime(DateUtil.format(entity.getPayFullTime(), "yyyy年MM月dd日"));
        }
        if (Objects.nonNull(buyContract.getPayBondTime())){
            tContract.setBuyPayBondDate(DateUtil.format(buyContract.getPayBondTime(), "yyyy年MM月dd日"));
        }
        tContract.setTotalPriceNum(entity.getTotalAmount());
        tContract.setBuyBondAmount(buyContract.getBondAmount());
        tContract.setBuyPayFullDate(DateUtil.format(buyContract.getPayFullTime(), "yyyy年MM月dd日"));
        tContract.setSellPayFullDate(DateUtil.format(sellContract.getPayFullTime(), "yyyy年MM月dd日"));
        tContract.setExtraTerm(StringUtils.isNotBlank(sellContract.getExtraTerm())?sellContract.getExtraTerm():"无");
        tContract.setBondAmount(sellContract.getBondAmount());
        tContract.setPayRateAmount(sellContract.getBondAmount());

        if (contractNo.contains("KCX")){
            tContract.setBuyBondAmount(null);
            tContract.setBuyPayBondDate(null);
            tContract.setBuyPayFullDate(DateUtil.format(sellContract.getPayFullTime(), "yyyy年MM月dd日"));
        }
        if (StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, companyName)){
            tContract.setBuyPayFullDate(DateUtil.format(sellContract.getPayFullTime(), "yyyy年MM月dd日"));
            tContract.setSigningAddr("上海市金山区");
        }
        BsContractTemplate template = new BsContractTemplate();
        template.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        tContract.setCreditDays(sellContract.getCreditCycle().intValue());
        if (sellContract.getCompanyName().contains("远东") && (!entity.getCompanyName().contains("奥顺宇")) && !entity.getOurCompanyName().contains("奥顺宇")){
            template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_YD);
        }else if (StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, companyName)) {
            template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_SDNH);
        } else if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, companyName)) {
            template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_SUGX);
            tContract.setDeliveryDateStr(DateUtil.format(entity.getSellDeliveryDate(), "yyyy年MM月dd日"));
        } else if(StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName) && entity.getContractNo().contains(BasConstants.STOCK_VIRTUAL_KC)){
            template.setTemplateTag(BasConstants.TEMPLATETAG_SELL_DC_TP_CONTRACT_SUGX);
        } else {
            template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT);
        }
        BsContractTemplate bsContractTemplate = bsContractTemplateService.findByTemplateTagAndEnterpriseId(template);
        
        tContract = dealWithSpecialBank(tContract , entity);
        tContract = contractTextService.dealWithExtraBank(tContract, applyMatch,  entity, "C");
        if (StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, ourCompanyName)
                && StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, companyName)){
            tContract.setOurCompanyBankName("上海银行股份有限公司杨浦支行");
            tContract.setOurCompanyBankNo("03006283844");
        }

        if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName)
                && StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, companyName)){
            tContract.setCompanyBankName("宁波银行阳明支行");
            tContract.setCompanyBankNo("61040122000182065");
        }

        entity.setOurBankName(tContract.getOurCompanyBankName());
        entity.setOurBankAccount(tContract.getOurCompanyBankNo());
        entity.setBankName(tContract.getCompanyBankName());
        entity.setBankAccount(tContract.getCompanyBankNo());
        applyDcsxDao.save(entity);
        logger.info("saveBank contractNo:{} ourBankName:{}, ourBankAccount:{}, bankName:{}, bankAccount:{}",
                entity.getContractNo(), entity.getOurBankName(), entity.getOurBankAccount(), entity.getBankName(), entity.getBankAccount());
        return contentMerge(bsContractTemplate.getContent(), tContract);
    }

    /**
     * 目前奥顺宇赊销，如果青岛中光抬头，普通赊销——2400账户；货到付款——1034账户
     * 目前奥顺宇赊销，如果网塑宁波抬头，暂时还是全部用1034账户
     * @param vo
     * @param entity
     * @return
     */
    private DcContractText dealWithSpecialBank(DcContractText vo, ApplyCtrDCSX entity) {
        ApplyMatch applyMatch = applyMatchClient.findByApproveId(entity.getApproveId());
        String ourCompanyName = applyMatch.getOurCompanyName();
        String sellOurCompanyName = applyMatch.getSellOurCompanyName();
        String businessTypeDcsx = applyMatch.getContractModel();
        Long enterpriseId = applyMatch.getEnterpriseId();
        BsDictData specialBankFlg = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "specialBankFlg", enterpriseId);
        if (Objects.isNull(specialBankFlg) || !StringUtils.equalsIgnoreCase("true", specialBankFlg.getDictName())) {
            // 不启用特殊银行账号逻辑
            return vo;
        }
        BsDictData bk1 = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "bk1", enterpriseId);
        BsDictData bk2 = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "bk2", enterpriseId);
        String bank1034 = Objects.nonNull(bk1) ? bk1.getDictName() : "636651034";
        String bank2400 = Objects.nonNull(bk2) ? bk2.getDictName() : "637632400";
        // 青岛奥顺宇-青岛中光
        if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, sellOurCompanyName)) {
            boolean modeFlg = StringUtils.isNotBlank(businessTypeDcsx) && businessTypeDcsx.contains("HDFK");
            vo.setOurCompanyBankNo(modeFlg ? bank1034 : bank2400);
        }

        // 青岛奥顺宇-网塑宁波
        if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_WSNB, sellOurCompanyName)) {
            vo.setOurCompanyBankNo(bank1034);
        }
        BsDictData sugxData = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, BasConstants.CONFIG_FLG_SWITCH_SUGX, enterpriseId);
        // 青岛中光-苏高新
        if (StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, sellOurCompanyName)) {
            if(Objects.nonNull(sugxData)) {
                vo.setOurCompanyBankNo(sugxData.getDictName());
                vo.setOurCompanyBankName(sugxData.getRemark());
            }
        }
        // 苏高新-青岛中光
        if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, sellOurCompanyName)) {
            if(Objects.nonNull(sugxData)) {
                vo.setCompanyBankNo(sugxData.getDictName());
                vo.setCompanyBankName(sugxData.getRemark());
            }
        }
        return vo;
    }

    @Override
    public List<ApplyCtrDCSX> autoDcsxPayAmount() {
        return applyDcsxDao.autoDcsxPayAmount();
    }

    @Override
    public List<ApplyCtrDCSX> autoDcsxPayBondAmount() {
        return applyDcsxDao.autoDcsxPayBondAmount();
    }

    @Override
    public List<ApplyCtrDCSX> autoDcsxReceiveAmount() {
        return applyDcsxDao.autoDcsxReceiveAmount();
    }

    /**
     * 调整付退款金额 （收货款金额大于合同总金额） 收货款-退款
     */
    @Override
    public void addPayRefundAmount(Long contractId, BigDecimal dealAmount) throws ApplicationException {
        ApplyCtrDCSX ctrDCSX = applyDcsxDao.findOne(contractId);
        //退款金额
        BigDecimal refundAmount = dealAmount.add(ctrDCSX.getPayRefundAmount());
        ctrDCSX.setPayRefundAmount(refundAmount);
        ctrDCSX.setReceiveAmount(ctrDCSX.getReceiveAmount().subtract(dealAmount));
        applyDcsxDao.save(ctrDCSX);
    }

    /**
     * 调整收退款金额  （付款金额大于合同总金额） 付款-退款
     */
    @Override
    public void addReceiveRefundAmount(Long contractId, BigDecimal dealAmount) throws ApplicationException {
        ApplyCtrDCSX ctrDCSX = applyDcsxDao.findOne(contractId);
        //退款金额
        BigDecimal refundAmount = dealAmount.add(ctrDCSX.getReceiveRefundAmount());
        ctrDCSX.setReceiveRefundAmount(refundAmount);
        ctrDCSX.setDealedAmount(ctrDCSX.getDealedAmount().subtract(dealAmount));
        applyDcsxDao.save(ctrDCSX);
    }

    @Override
    public List<PmApprove> filterAutoSignWithPay(List<PmApprove> autoSignApproveList) {
        Long enterpriseId = autoSignApproveList.stream().map(PmApprove::getEnterpriseId).findAny().orElse(44L);
        PmProcess targetProcess = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_CODE_DCSX_PAY, enterpriseId);
        Long processId = targetProcess.getId();
        List<Long> dcsxContractIdList = autoSignApproveList.stream()
                .filter(a -> Objects.equals(a.getProcessId(), processId))
                .filter(a -> a.getSubject().contains("全款"))
                .map(PmApprove::getContractId)
                .distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dcsxContractIdList)) {
            return autoSignApproveList;
        }
        List<ApplyCtrDCSX> dcsxContractList = applyDcsxDao.findByIds(dcsxContractIdList);
        if (CollectionUtils.isEmpty(dcsxContractList)) {
            return autoSignApproveList;
        }
        List<Long> approveIdList = dcsxContractList.stream().map(ApplyCtrDCSX::getApproveId).distinct().collect(Collectors.toList());
        List<Long> paidApproveIds = ctrContractDao.findPaidApproveId(approveIdList);
        if (CollectionUtils.isEmpty(paidApproveIds)) {
            return autoSignApproveList;
        }
        List<Long> paidContractIds = dcsxContractList.stream().filter(d -> paidApproveIds.contains(d.getApproveId())).map(ApplyCtrDCSX::getId).collect(Collectors.toList());
        return autoSignApproveList.stream().filter(p -> !(Objects.equals(p.getProcessId(), processId) && paidContractIds.contains(p.getContractId()))).collect(Collectors.toList());
    }

    private void setOurCompanyParam(DcContractText tContract, String companyName){
        BsCompanyOur companyOur = bsCompanyOurService.findByCompanyName(companyName);
        if(Objects.nonNull(companyOur)){
            tContract.setOurCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName()) ? "" : companyOur.getCompanyBankName());
            tContract.setOurCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId()) ? "" : companyOur.getCompanyCardId());
            tContract.setOurAddress(StringUtils.isBlank(companyOur.getAddress()) ? "" : companyOur.getAddress());
            tContract.setOurCompanyFax(StringUtils.isBlank(companyOur.getCompanyFax()) ? "" : companyOur.getCompanyFax());
            tContract.setOurCompanyPerson(StringUtils.isBlank(companyOur.getCompanyPerson()) ? "" : companyOur.getCompanyPerson());
            tContract.setOurCompanyPhone(StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone());
            tContract.setOurCompanyContact(StringUtils.isBlank(companyOur.getCompanyContact()) ? "" : companyOur.getCompanyContact());
            tContract.setOurCompanyName(StringUtils.isBlank(companyOur.getCompanyName()) ? "" : companyOur.getCompanyName());
            tContract.setSigningAddr(StringUtils.isBlank(companyOur.getSigningAddr()) ? "" : companyOur.getSigningAddr());
            tContract.setOurCompanyTaxNo(StringUtils.isBlank(companyOur.getCompanyTaxNo()) ? "" : companyOur.getCompanyTaxNo());
        } else {
            BsCompanyDcsx byCompanyName = bsCompanyDcsxService.findByCompanyName(companyName);
            if (byCompanyName!=null) {
                String signingAddr =byCompanyName.getSigningAddr();
                String ourCompanyPerson = byCompanyName.getCompanyPerson();
                String ourCompanyContact = byCompanyName.getCompanyContact();
                String ourCompanyFax =byCompanyName.getCompanyFax();
                String ourCompanyPhone = byCompanyName.getCompanyPhone();
                String ourCompanyTaxNo = byCompanyName.getCompanyTaxNo();

                tContract.setOurCompanyBankName(StringUtils.isBlank(byCompanyName.getCompanyBankName()) ? "" : byCompanyName.getCompanyBankName());
                tContract.setOurCompanyBankNo(StringUtils.isBlank(byCompanyName.getCompanyCardId()) ? "" : byCompanyName.getCompanyCardId());
                tContract.setOurAddress(StringUtils.isBlank(byCompanyName.getAddress()) ? "" : byCompanyName.getAddress());
                tContract.setOurCompanyFax(StringUtils.isBlank(ourCompanyFax) ? "" : ourCompanyFax);
                tContract.setOurCompanyPerson(StringUtils.isBlank(ourCompanyPerson) ? "" : ourCompanyPerson);
                tContract.setOurCompanyPhone(StringUtils.isBlank(ourCompanyPhone) ? "" : ourCompanyPhone);
                tContract.setOurCompanyContact(StringUtils.isBlank(ourCompanyContact) ? "" : ourCompanyContact);
                tContract.setOurCompanyName(StringUtils.isBlank(companyName) ? "" : companyName);
                tContract.setSigningAddr(StringUtils.isBlank(signingAddr) ? "" : signingAddr);
                tContract.setOurCompanyTaxNo(StringUtils.isBlank(ourCompanyTaxNo) ? "" : ourCompanyTaxNo);
            }
        }
    }

    private void setCompanyParam(DcContractText tContract, String companyName){
        BsCompanyDcsx byCompanyName = bsCompanyDcsxService.findByCompanyName(companyName);
        if (byCompanyName!=null) {
            String companyPerson =byCompanyName.getCompanyPerson();
            String companyContact = byCompanyName.getCompanyContact();
            String companyFax =byCompanyName.getCompanyFax();
            String companyPhone =byCompanyName.getCompanyPhone();
            String companyTaxNo =byCompanyName.getCompanyTaxNo();
            String companyBankName = byCompanyName.getCompanyBankName();
            String companyBankNo =byCompanyName.getCompanyCardId();
            String address = byCompanyName.getAddress();

            tContract.setCompanyPerson(StringUtils.isBlank(companyPerson) ? "" : companyPerson);
            tContract.setCompanyContact(StringUtils.isBlank(companyContact) ? "" : companyContact);
            tContract.setCompanyFax(StringUtils.isBlank(companyFax) ? "" : companyFax);
            tContract.setCompanyPhone(StringUtils.isBlank(companyPhone) ? "" : companyPhone);
            tContract.setCompanyTaxNo(StringUtils.isBlank(companyTaxNo) ? "" : companyTaxNo);
            tContract.setCompanyBankName(StringUtils.isBlank(companyBankName) ? "" : companyBankName);
            tContract.setCompanyBankNo(StringUtils.isBlank(companyBankNo) ? "" : companyBankNo);
            tContract.setAddress(address);
        }


    }

    private String contentMerge(String content, DcContractText entity) {
        Configuration cfg = new Configuration();
        StringWriter sw = new StringWriter();
        try {
            Template t = new freemarker.template.Template("", new StringReader(content), cfg);
            t.process(entity, sw);
            content = sw.toString();
        } catch (Exception e) {
            logger.error("合并模板异常", e);
        }
        return content;
    }

    public String getNewSubject(String subject,BigDecimal newTotalAmount){
        String title = NumberUtil.formatNumber(newTotalAmount, "#.##");
        if(StringUtils.isBlank(title)) {
            return subject;
        }
        String[] spit = subject.split(" ");
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String s : spit) {
            if(i == 2) {
                builder.append(title).append(" ");
            } else {
                builder.append(s).append(" ");
            }
            i++;
        }
        String newSubject = "";
        if(StringUtils.isNotBlank(builder) && builder.length() > 0) {
            newSubject = builder.substring(0,builder.length()-1);
        }

        return newSubject.toString();
    }

    /**
     * 查找60天内未申请代采赊销付款的代采赊销订单
     * @param companyName
     * @return
     */
    @Override
    public List<ApplyCtrDCSX> findHb60DayNotApplyList(String companyName) {
        return applyDcsxDao.findHb60DayNotApplyList(companyName);
    }
}
