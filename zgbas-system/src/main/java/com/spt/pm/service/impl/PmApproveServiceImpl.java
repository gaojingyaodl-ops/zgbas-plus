package com.spt.pm.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Splitter;
import com.hsoft.push.sdk.remote.PushClientHttp;
import com.hsoft.push.sdk.vo.PushRequest;
import com.hsoft.push.sdk.vo.PushTarget;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.cache.UserCache;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserLoginVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IApplyCtrDcsxClinent;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmApproveHistoryDao;
import com.spt.pm.dao.PmApproveStepDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.*;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.*;
import com.spt.pm.util.ResApproveUtil;
import com.spt.pm.vo.*;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.encrypt.Md5Encrypt;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.InvalidParamException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class PmApproveServiceImpl extends BaseService<PmApprove> implements IPmApproveService {
	private static final ExecutorService threadPool = Executors.newSingleThreadExecutor();
	@Autowired
	private PmApproveDao pmApproveDao;
	@Autowired
	private PmApproveStepDao approveStepDao;
	@Autowired
	private PmApproveHistoryDao approveHistoryDao;
	@Autowired
	private PmProcessDao processDao;
	@Autowired
	private IPmApproveStepService approveStepService;
	@Autowired
	private IPmApproveHistoryService historyService;
	@Autowired
	private PushClientHttp pushRemote;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IBsCompanyClient bsCompanyClient;
	@Autowired
	private IPmProcessNodeService pmProcessNodeService;
	@Autowired
	private ResApproveUtil resApproveUtil;
	@Autowired
	private IPmApprovePushService pmApprovePushService;
	@Autowired
	private IApplyCtrDcsxClinent applyCtrDcsxClient;
	@Value("${res.md5.secret.key}")
	private String resMd5SecretKey;
	@Value("${res.login.url}")
	private String resLoginUrl;
	@Value("${res.mobile.login.url}")
	private String resMobileLoginUrl;
	@Resource
	private ICtrContractClient ctrContractClient;
	@Resource
	private IApplyCtrDcsxClinent dcsxClinent;

	private static  final String PARAM_EQL_PROCESS_ID = "EQL_processId";

	@Override
	public BaseDao<PmApprove> getBaseDao() {
		return pmApproveDao;
	}

	@Override
	public Class<PmApprove> getEntityClazz() {
		return PmApprove.class;
	}

	@Override
	@ServerTransactional
	public void delete(Long id) throws ApplicationException {
		PmApprove approve = pmApproveDao.findOne(id);

		approveStepDao.deleteByApproveId(id);
		approveHistoryDao.deleteByApproveId(id);

		if (approve.getBizId() != null && approve.getBizId() > 0) {
			PmProcess process = processDao.findOne(approve.getProcessId());
			IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
			pmService.delete(approve.getBizId());
		}
		super.delete(id);
	}

	/** 获取审批信息 */
	@Override
	public PmApproveVo getApproveVo(Long approveId) {
		PmApprove approve = pmApproveDao.findOne(approveId);
		if (approve==null) {
			return null;
		}
		PmProcess process = processDao.findOne(approve.getProcessId());
		IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
		IPmEntity bizEntity = pmService.getEntity(approve.getBizId());
		PmApproveVo vo = new PmApproveVo();
		vo.setApprove(approve);
		vo.setBizEntityJson(JsonUtil.obj2Json(bizEntity));
		return vo;
	}

	/**
	 * 发起审批
	 */
	@Override
	@ServerTransactional
	public PmApprove startFlow(PmApproveSaveVo startVo) throws ApplicationException {
		String mode = startVo.getMode();
		// 验证审批必传参数
		resApproveUtil.verifyApproveParam(startVo);
		PmProcess process = processDao.findOne(startVo.getProcessId());
		IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
		// 表单Json转换IPmEntity
		IPmEntity bizEntity = resApproveUtil.parseIPmEntity(startVo, process);
		// 保存审批表单JSON内容
		bizEntity = pmService.saveEntity(bizEntity);
		PmApprove approve = pmApproveDao.findOne(startVo.getApproveId());
		if (approve == null) {
			// 创建审批单参数
			approve = resApproveUtil.buildNewPmApprove(startVo, bizEntity, process);
		} else {
			// 驳回后，再次提交时，清空最近审批人
			resApproveUtil.clearApproveStepUser(approve);
		}
		IPmApproveListener listener = getListenerService(process, pmService);
		// 更新审批内容JSON,赋值审批单ID
		bizEntity.setApproveId(approve.getId());
		bizEntity.setStatus(startVo.getStatus());
		bizEntity = pmService.saveEntity(bizEntity);

		String subject = pmService.getSubject(bizEntity, process);
		String sub = Boolean.TRUE.equals(startVo.getAutoStartFlg()) ? "[线上化]" + subject : subject;
		String subReal = Boolean.TRUE.equals(startVo.getAutoStartFlgReal()) ? sub + BasConstants.AUTO_TYPE_XT : sub;
		approve.setBizId(bizEntity.getId());
		approve.setSubject(subReal);
		approve.setStatus(startVo.getStatus());
		approve = pmApproveDao.save(approve);

		// S-保存，A-发起申请，P-审批中修改
		if (mode.equals("A") && (startVo.getStatus().equals(PmConstants.APPROVE_STATUS_A)
				|| startVo.getStatus().equals(PmConstants.APPROVE_STATUS_D))) {
			// 根据审批单提交内容获取审批流程
			Map<String, Object> conditionDefaultMap = pmService.buildConditionDefaultMap(bizEntity);
			PmProcessConditionStepVo conditionStepVo = resApproveUtil.getApproveSteps(startVo, bizEntity, process, conditionDefaultMap);
			approve.setConditionId(conditionStepVo.getConditionId());
			List<PmProcessStep> steps = conditionStepVo.getSteps();
			Boolean specialAutoSignFlg = conditionStepVo.getSpecialAutoSignFlg();
			// 签署连带责任保证书逻辑处理
			resApproveUtil.dealWithLiabilityGuarantee(process, bizEntity, steps);
			List<PmApproveStep> approveSteps = approveStepService.saveSteps(bizEntity, approve.getId(), startVo.getUserId(), steps, specialAutoSignFlg);

			// 过滤第一个不是自动跳过的审批步骤
			PmApproveStep step = approveSteps.stream().filter(s -> StringUtils.isBlank(s.getApproveOpinion())).findFirst().orElse(null);
			approve = resApproveUtil.setCurrApproveStep(approveSteps, pmService, bizEntity, approve);
			String currApproveUserIds = approve.getCurrApproverUserId();
			approve = pmApproveDao.save(approve);

			// 保存发起申请历史记录
			historyService.addHistory(approve, resApproveUtil.generateStartStep(startVo));

			// 保存审批人重合系统自动跳过history记录
			resApproveUtil.saveMergeStepHistory(approveSteps, approve);

			if (listener != null) {
				listener.doStepIn(approve);
			}

			if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, startVo.getStatus())) {
				// 发起后直接完成
				logger.info("发起后直接完成 startVo:{}", startVo);
				doStepFlow(resApproveUtil.generateStepFlowVo(approve, startVo, step));
			} else {
				// 按照正常流程审批
				PmProcessNode processNode = pmProcessNodeService.getEntity(step.getNodeId());
				boolean jumpFlg = true;
				if (Objects.nonNull(processNode) && PmConstants.PROCESS_NODE_LIABILITY_USER.equals(processNode.getNodeCode())) {
					// 签署连带责任保证书 审批步骤不自动跳过
					jumpFlg = false;
				}
				if (jumpFlg && currApproveUserIds.contains(PmConstants.SEPARATE + startVo.getUserId() + PmConstants.SEPARATE)) {
					logger.info("自动跳过, approveId:{},当前审批人:{},下一审批人:{}", approve.getId(), startVo.getUserId(), currApproveUserIds);
					doStepFlow(resApproveUtil.generateAutoAgreeStepFlowVo(approve, startVo, step));
				} else {
					notifyUser(approve, currApproveUserIds);
				}
			}
		}

		// 保存代采赊销盖章审批调整字段日志
		resApproveUtil.addSealUsageHisLog(startVo, bizEntity);
		return approve;
	}

	/**
	 * 执行审批步骤
	 *
	 * @throws ApplicationException
	 */
	@Override
	@ServerTransactional
	public PmApprove doStepFlow(PmApproveStepFlowVo flowVo) throws ApplicationException {
		if (flowVo.getApproveId() == null || flowVo.getApproveId() == 0) {
			throw new InvalidParamException("ApproveId");
		}
		if (flowVo.getApproveUserId() == null || flowVo.getApproveUserId() == 0) {
			throw new InvalidParamException("ApproveUserId");
		}
		if (StringUtils.isBlank(flowVo.getApproveUserName())) {
			throw new InvalidParamException("ApproveUserName");
		}
		PmApprove approve = pmApproveDao.findOne(flowVo.getApproveId());
		String currApproveUserId = approve.getCurrApproverUserId();
		PmApproveStep step;
		PmProcess process = processDao.findOne(approve.getProcessId());
		IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
		IPmEntity pmEntity = pmService.getEntity(approve.getBizId());
		IPmApproveListener listener = getListenerService(process, pmService);
		logger.info("approveNo:{}, autoSignFlg:{}", approve.getApproveNo(), flowVo.getAutoSignFlg());
		logger.info("flowVo:{}", JsonUtil.obj2Json(flowVo));
		String nextUserId = null;
		if (flowVo.isComplete()) {
			// 审批完成，同步修改业务表状态
			approve.setStatus(PmConstants.APPROVE_STATUS_D);
			approve.setCurrApproverUserId(null);
			approve.setCurrApproveStepId(null);
			approve.setCurrStepName(null);
			approve.setAutoSignLimit(null);
			if (listener != null) {
				listener.doStepFlow(approve, null);
			}
			// 自动完成审批，批量生成审批记录
			approveStepService.completeApproveStep(approve, flowVo.getApproveRemark(), flowVo.getApproveUserName());
		}else {
			if (StringUtils.isNotBlank(currApproveUserId)) {
				if (!currApproveUserId.contains(PmConstants.SEPARATE + flowVo.getApproveUserId() + PmConstants.SEPARATE)) {
					logger.warn("不是当前审批人,approveUserId:{},userId:{}", currApproveUserId, flowVo.getApproveUserId());
					throw new ApplicationException("不是当前审批人，无权审批");
				}
			}
			if (approve.getCurrApproveStepId() == null) {
				throw new ApplicationException("当前审批步骤错误,ApproveId:" + flowVo.getApproveId());
			}
			Long currApproveStepId = resApproveUtil.getCurrStep(flowVo, approve);
			flowVo.setApproveStepId(currApproveStepId);

			logger.info("执行审批步骤, flowVo: {}", JsonUtil.obj2Json(flowVo));
			if (flowVo.getApproveOpinion().equals(PmConstants.APPROVE_OPINION_AGREE)) {
				// 同意，流程走到下一步
				List<PmApproveStep> nextSteps = approveStepService.getNextStep(flowVo.getApproveId(), currApproveStepId);
				PmApproveStep approveStep = approveStepService.getEntity(currApproveStepId);
				if (org.apache.commons.collections.CollectionUtils.isNotEmpty(nextSteps)) {
					// 设置下一个审批人
					resApproveUtil.setNextApproveStep(nextSteps, approve);
					approve.setStatus(PmConstants.APPROVE_STATUS_A);// 审批中
				} else {
					// 审批完成，同步修改业务表状态
					approve.setStatus(PmConstants.APPROVE_STATUS_D);
					approve.setCurrApproverUserId(null);
					approve.setCurrApproveStepId(null);
					approve.setCurrStepName(null);
					approve.setAutoSignLimit(null);
				}
				if (listener != null) {
					listener.doStepFlow(approve, approveStep);
				}
			} else if (flowVo.getApproveOpinion().equals(PmConstants.APPROVE_OPINION_DENY)) {
				// 拒绝，流程回到发起人状态
				approve.setCurrApproverUserId(null);
				approve.setCurrApproveStepId(null);
				approve.setCurrStepName(null);
				approve.setAutoSignLimit(null);
				approve.setStatus(PmConstants.APPROVE_STATUS_B);// 驳回
				PmApproveCurrVo vo = new PmApproveCurrVo();
				BeanUtils.copyProperties(approve, vo);
				vo.setCurrUserId(flowVo.getApproveUserId());
				vo.setCurrUserName(flowVo.getApproveUserName());
				if (listener != null) {
					listener.doStepBack(vo, null);
				}
			} else if (flowVo.getApproveOpinion().equals(PmConstants.APPROVE_OPINION_BACK)) {
				if (PmConstants.APPROVE_STATUS_D.equals(approve.getStatus())) {
					throw new ApplicationException("审批已完成，不能追回");
				}
				resApproveUtil.clearApproveStepUser(approve);
				approve.setStatus(PmConstants.APPROVE_STATUS_N);
			}
		}
		pmEntity.setStatus(approve.getStatus());
		pmService.saveEntity(pmEntity);
		step = approveStepService.doStep(flowVo);
		approve.setLastApproveDate(new Date());
		approve.setLastApproveRemark(flowVo.getApproveRemark());
		approve.setLastApproveUserName(Objects.nonNull(step) ? step.getApproveUserName() : flowVo.getApproveUserName());
		approve.setLastApproveUserId(Objects.nonNull(step) ? step.getApproveUserId() : flowVo.getApproveUserId());
		approve = pmApproveDao.save(approve);
		historyService.addHistory(approve, step);
		nextUserId = approve.getCurrApproverUserId();
		if (nextUserId != null) {
			notifyUser(approve, nextUserId);
		}

		// 执行系统自动推送逻辑
		addSysPush(approve);
		return approve;
	}

	@Override
	public String findNodeUserId(IPmEntity bizEntity, Long nodeId, Long createUserId) throws ApplicationException {
		return resApproveUtil.getNodeUserIds(null, bizEntity, nodeId, createUserId);
	}

	@Override
	@ServiceTransactional
	public void doAutoSign() {
		List<PmApprove> approveList = pmApproveDao.getAutoSignApproveList();
		if (CollectionUtils.isEmpty(approveList)) {
			return;
		}
		// 1.过滤自动审批单，代采赊销付款申请，若上游未付款不自动审批通过
		List<PmApprove> autoSignApproveList = applyCtrDcsxClient.filterAutoSignWithPay(approveList);
		if (CollectionUtils.isEmpty(autoSignApproveList)) {
			return;
		}
		// 2.过滤自动审批单，业务付款申请，供应商-青岛中光-苏高新-上海中光-客户 链条
		//   若 供应商-青岛中光的付款申请未通过不自动审批 青岛中光-苏高新的业务付款申请
		autoSignApproveList = ctrContractClient.filterAutoSignWithPay(approveList);
		if (CollectionUtils.isEmpty(autoSignApproveList)) {
			return;
		}
		logger.info("doAutoSign approveNos:{}", autoSignApproveList.stream().map(PmApprove::getApproveNo).collect(Collectors.joining(BasConstants.COMMA)));
		for (PmApprove entity : autoSignApproveList) {
			try {
				PmApproveStepFlowVo stepFlowVo = resApproveUtil.generateAutoSignLimitVo(entity);
				if (Objects.nonNull(stepFlowVo)) {
					this.doStepFlow(stepFlowVo);
				}
			} catch (Exception e) {
				logger.error("doAutoSign approveNo:{}", entity.getApproveNo());
				logger.error("doAutoSign error", e);
			}
		}
	}


	private void notifyUser(PmApprove approve, String userIds) {
		threadPool.execute(() -> {
            List<String> lstUserIds = Splitter.on(PmConstants.SEPARATE).splitToList(userIds);
            for (String userId : lstUserIds) {
                if (StringUtils.isBlank(userId)) {
                    continue;
                }
                SysUserSdk sysUser = authOpenFacade.findUserById(Long.valueOf(userId));
                if (sysUser != null && StringUtils.isNotBlank(sysUser.getEmail())) {
                    PushRequest req = new PushRequest();
                    req.setBusinessId(approve.getApproveNo());
                    req.setModule("S");
                    req.setPushType("basApproveNotify");// 新审批通知
                    req.setSubmitUserId("sys");
                    List<PushTarget> lst = new ArrayList<>();
                    lst.add(new PushTarget(userId, sysUser.getPhonenumber(), sysUser.getEmail()));
                    req.setTargets(lst);
                    Map<String, Object> param = new HashMap<>();
                    param.put("approveNo", approve.getApproveNo());
                    param.put("processName", approve.getProcessName());
                    param.put("subject", approve.getSubject());
                    String lastApproveDate ="";
                    if (approve.getLastApproveDate() != null) {
                        lastApproveDate = DateOperator.formatDate(approve.getLastApproveDate(), true);
                    }
                    param.put("lastApproveDate", lastApproveDate);
                    param.put("lastApproveUserName", approve.getLastApproveUserName());
                    param.put("lastApproveRemark", approve.getLastApproveRemark());
                    req.setParam(param);

                    // logger.info("notifyUser : " + JsonUtil.obj2Json(req));
                    try {
                        if (StringUtils.equals(sysUser.getEmail(), "wangwei@totrade.cn")) {
                            logger.info("跳过邮件wangwei@totrade.cn");
                            continue;
                        }
                        pushRemote.send(req);
                    } catch (Exception e) {
                        logger.error("notifyUser error", e);
                    }
                }

            }
        });


	}

	@Override
	public Page<PmApproveDownVo> findPageApprove(PmApproveSearchVo queryVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		// logger.info("queryVo:{}", JsonUtil.obj2Json(queryVo));
		PageRequest pageRequest =PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);
		Map<String, Object> searchParams = queryVo.getSearchParams();
		String keyWord = keyWordSearchHandle(searchParams);
		Specification<PmApprove> spec = WebUtil.buildSpecification(searchParams);

		String mode = queryVo.getMode();
		Long deptLeaderId = queryVo.getDeptLeaderId();
		// 我审批过,经过我
		Specification<PmApprove> spec_myhis = (root, query, cb)-> {
			Subquery<PmApproveHistory> sq = query.subquery(PmApproveHistory.class);
			Root<PmApprove> sqc = sq.correlate(root);
			Join<PmApprove, PmApproveHistory> sqo = sqc.join("approveHistories");
			Path<String> expression = sqo.get("approveUserId");
			Predicate predicate = cb.equal(expression, queryVo.getUserId());
			sq.select(sqo).where(predicate);
			return cb.exists(sq);
		};

		// 推送给我的
		Specification<PmApprove> spec_push = getPushSpecification(queryVo);

		// 待我处理
		Specification<PmApprove> spec_my = WebUtil.buildSpecification("EQL_createUserId_OR_EQL_cooperationUserId", queryVo.getUserId());
		Specification<PmApprove> spec_myApprove = WebUtil.buildSpecification("LIKES_currApproverUserId",
				PmConstants.SEPARATE + queryVo.getUserId() + PmConstants.SEPARATE);
		Specification<PmApprove> spec_status = WebUtil.buildSpecification("NEQS_status", PmConstants.APPROVE_STATUS_D);
		Specification<PmApprove> spec_status_C = WebUtil.buildSpecification("NEQS_status", PmConstants.APPROVE_STATUS_C);
//		Specification<PmApprove> spec_my_sp = Specification.where(spec_my).or(spec_myApprove);
//		spec_my_sp = Specification.where(spec_my_sp).and(spec_status);
		// 我发起
		Specification<PmApprove> spec_mystart = WebUtil.buildSpecification("EQL_createUserId_OR_EQL_cooperationUserId", queryVo.getUserId());
		if (StringUtils.equals(PmConstants.APPROVE_MODE_H, mode)) {
			// 我审批过的
			spec = Specification.where(spec).and(spec_myhis);
		} else if (StringUtils.equals(PmConstants.APPROVE_MODE_MC, mode)) {
			// 待我处理的
			spec = Specification.where(spec).and(spec_myApprove).and(spec_status_C).and(spec_status);
		}else if (StringUtils.equals(PmConstants.APPROVE_MODE_CM, mode)) {
			// 我发起待处理
			spec = Specification.where(spec).and(spec_my).and(spec_status_C).and(spec_status);
		} else if (StringUtils.equals(PmConstants.APPROVE_MODE_S, mode)) {
			// 我发起的
			spec = Specification.where(spec).and(spec_mystart);
		} else if (StringUtils.equals(PmConstants.APPROVE_MODE_P, mode)) {
			// 推送给我的
			spec = Specification.where(spec).and(spec_push);
		} else if(StringUtils.equals(PmConstants.APPROVE_MODE_F, mode)){
			// 查看所有业务类型的审批单
			spec = relatedMeBuild(spec, queryVo, deptLeaderId, spec_myhis, spec_push, spec_mystart);
			List<Long> processIdLists = processDao.getBusinessProcessId(queryVo.getEnterpriseId());

			if (searchParams.containsKey(PARAM_EQL_PROCESS_ID) && Objects.nonNull(searchParams.get(PARAM_EQL_PROCESS_ID))
					&& StringUtils.isNotBlank(String.valueOf(searchParams.get(PARAM_EQL_PROCESS_ID)))) {
				long searchProcessId = Long.parseLong(String.valueOf(searchParams.get(PARAM_EQL_PROCESS_ID)));
				if (processIdLists.contains(searchProcessId)) {
					Specification<PmApprove> inProcessId = WebUtil.buildSpecification("INL_processId", searchProcessId);
					spec = Specification.where(spec).or(inProcessId);
				}
			} else {
				if (org.apache.commons.collections.CollectionUtils.isNotEmpty(processIdLists)) {
					Specification<PmApprove> inProcessIds = WebUtil.buildSpecification("INL_processId", processIdLists);
					spec = Specification.where(spec).or(inProcessIds);
				}
			}
			spec = Specification.where(spec).and(WebUtil.buildSpecification(searchParams));
		} else if (StringUtils.isEmpty(mode) || StringUtils.equals(PmConstants.APPROVE_MODE_Z, mode)) {
			// 所有和我有关
			spec = relatedMeBuild(spec, queryVo, deptLeaderId, spec_myhis, spec_push, spec_mystart);

			// 检索相关人员审批流程
			// spec = searchrRelationship(queryVo, spec);

			spec = Specification.where(spec).and(WebUtil.buildSpecification(searchParams));

		}
		Specification<PmApprove> spec_enable = WebUtil.buildSpecification("EQB_enableFlg", true);
		if (queryVo.getBatchSelectDel() != null && queryVo.getBatchSelectDel() == 1) {
			spec_enable = WebUtil.buildSpecification("EQB_enableFlg", false);
		} else if (queryVo.getBatchSelectDel() != null && queryVo.getBatchSelectDel() == 2) {
			spec_enable = null;
		}
		if (StringUtils.isNotBlank(keyWord)){
			Specification<PmApprove> subjectNewSpec = null;
			Specification<PmApprove> newSpec = null;
			List<String> keyWordList = Splitter.on(" ").omitEmptyStrings().splitToList(keyWord);
			if (org.apache.commons.collections.CollectionUtils.isNotEmpty(keyWordList)){
				for (String keyParam : keyWordList) {
					if (NumberUtil.isNumber(keyParam)) {
						String subjectParam = NumberUtil.decimalFormatMoney(Double.parseDouble(keyParam));
						Specification<PmApprove> subjectLikeSpec = WebUtil.buildSpecification("LIKES_subject", keyParam);
						Specification<PmApprove> subjectNumberLikeSpec = WebUtil.buildSpecification("LIKES_subject", subjectParam);
						subjectNewSpec = Specification.where(subjectLikeSpec).or(subjectNumberLikeSpec);
					} else {
						Specification<PmApprove> subjectNumberLikeSpec = WebUtil.buildSpecification("LIKES_subject", keyParam);
						subjectNewSpec = Specification.where(subjectNumberLikeSpec);
					}
					newSpec = Specification.where(newSpec).and(subjectNewSpec);
				}
			}
			spec = Specification.where(spec).and(newSpec);
		}
		spec = Specification.where(spec).and(spec_enable);
		Page<PmApprove> page = getBaseDao().findAll(spec, pageRequest);
		// sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
		PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
		List<PmApproveDownVo> lstVo = new ArrayList<>();
		for (PmApprove entity : page.getContent()) {
			PmApproveDownVo vo = new PmApproveDownVo();
			try {
				PropertyUtils.copyProperties(vo, entity);
				try {
					vo.setCurrApproverUserName(UserCache.getUserName(vo.getCurrApproverUserId()));
				} catch (Exception e) {
					// TODO: handle exception
				}
				vo.setStatusName(DictUtil.getValue(BasConstants.DICT_TYPE_APPROVESTATUS, entity.getStatus()));
			} catch (Exception e) {
				logger.warn("copyProperties", e);
			}
			if (StringUtils.equals("1", entity.getSource())) {
				vo.setOpenUrl(getApproveOpenUrl(entity.getNewApproveId(), queryVo.getEnterpriseId(), queryVo.getLoginName()));
				vo.setMobileOpenUrl(getApproveMobileOpenUrl(entity.getNewApproveId(), queryVo.getEnterpriseId(), queryVo.getLoginName()));
			}
			lstVo.add(vo);
		}

		return new PageImpl<>(lstVo, pageRequest_new, page.getTotalElements());
	}

	private String keyWordSearchHandle(Map<String, Object> searchParams) {
		String searchKey = "LIKES_subject";
		String subjectParam = (String) searchParams.get(searchKey);
		if (StringUtils.isNotBlank(subjectParam)) {
			searchParams.remove(searchKey);
		}
		return subjectParam;
	}

	/**
	 * 如果是新审批单，直接填写新审批单
	 *
	 * @return 跳转地址
	 */
	public String getApproveOpenUrl(Long approveId,Long enterpriseId,String loginName) {
		if (Objects.isNull(approveId)) {
			return null;
		}
		long timestamp = System.currentTimeMillis();
		String md5Ticket = Md5Encrypt.encrypt(loginName + loginName + timestamp + resMd5SecretKey).toLowerCase();
		StringBuilder sbr = new StringBuilder(resLoginUrl);
		sbr.append("?openType=D");
		sbr.append("&loginName=").append(loginName);
		sbr.append("&timestamp=").append(timestamp);
		sbr.append("&ticket=").append(md5Ticket);
		sbr.append("&enterpriseId=").append(enterpriseId);
		sbr.append("&approveId=").append(approveId);
		return sbr.toString();
	}

	/**
	 * 如果是新审批单，直接填写新审批单
	 *
	 * @return 跳转地址
	 */
	public String getApproveMobileOpenUrl(Long approveId,Long enterpriseId,String loginName) {
		if (Objects.isNull(approveId)) {
			return null;
		}
		long timestamp = System.currentTimeMillis();
		String md5Ticket = Md5Encrypt.encrypt(loginName + loginName + timestamp + resMd5SecretKey).toLowerCase();
		StringBuilder sbr = new StringBuilder(resMobileLoginUrl);
		sbr.append("?openType=D");
		sbr.append("&loginName=").append(loginName);
		sbr.append("&timestamp=").append(timestamp);
		sbr.append("&ticket=").append(md5Ticket);
		sbr.append("&enterpriseId=").append(enterpriseId);
		sbr.append("&approveId=").append(approveId);
		return sbr.toString();
	}

	private Specification<PmApprove> relatedMeBuild(Specification<PmApprove> spec, PmApproveSearchVo queryVo, Long deptLeaderId,
													Specification<PmApprove> spec_myhis, Specification<PmApprove> spec_push, Specification<PmApprove> spec_mystart) {
		Specification<PmApprove> spec_all;
		List<Long> deptId = authOpenFacade.findMyDeptId(queryVo.getUserId());
		if (deptLeaderId != null) {
			deptId = authOpenFacade.findMyDeptId(deptLeaderId);
		}
		if (deptId.size() == 0) {
			spec_all = Specification.where(spec_myhis).or(spec_push).or(spec_mystart);
		} else {
			Specification<PmApprove> spec_department = WebUtil.buildSpecification("INL_deptId", deptId);
			spec_all = Specification.where(spec_myhis).or(spec_push).or(spec_mystart).or(spec_department);
		}
		return Specification.where(spec).and(spec_all);
	}

	/**
	 * 检索相关人员审批流程
	 * @param queryVo
	 * @param spec
	 * @return
	 */
	private Specification<PmApprove> searchrRelationship(PmApproveSearchVo queryVo,Specification<PmApprove> spec) {
		// 和我所有有关的
		List<SysUserSdk> usersByDeptIds = new ArrayList<>();
		// 业务助理
		if (StringUtils.equals(queryVo.getMode(), "Z")) {
			SysUserSdk userById = authOpenFacade.findUserById(queryVo.getUserId());
			Long deptId1 = userById.getDeptId();
			List<Long> deptIds = new ArrayList<>();
			deptIds.add(deptId1);
			usersByDeptIds.addAll(authOpenFacade.findByDeptIds(deptIds));
		}
		// 负责人
		List<Long> myDeptIds = authOpenFacade.findMyDeptId(queryVo.getUserId());
		if (myDeptIds != null && !myDeptIds.isEmpty()) {
			usersByDeptIds.addAll(authOpenFacade.findByDeptIds(myDeptIds));
		}
		List<Long> matchUserIds = new ArrayList<>();
		if (!usersByDeptIds.isEmpty()) {
			matchUserIds = usersByDeptIds.stream().map(SysUserSdk::getUserId).collect(Collectors.toList());
		}
		matchUserIds.add(queryVo.getUserId());
		matchUserIds = matchUserIds.stream().distinct().collect(Collectors.toList());
		List<Long> relationShipApproveId = bsCompanyClient.getRelationShipApproveIdByCompanyIds(matchUserIds);
		List<Long> r_approveIds = relationShipApproveId.stream().filter(Objects::nonNull).collect(Collectors.toList());
		if (!r_approveIds.isEmpty()) {
			Specification<PmApprove> inl_id = WebUtil.buildSpecification("INL_id", r_approveIds);

			spec = Specification.where(spec).or(inl_id);
		}
		return spec;
	}

	@Override
	@ServiceTransactional
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		if (vo.getApproveId() == null || vo.getApproveId() == 0) {
			throw new InvalidParamException("approveId");
		}

		PmApprove approve = pmApproveDao.findOne(vo.getApproveId());
		if (PmConstants.APPROVE_STATUS_D.equals(approve.getStatus())||PmConstants.APPROVE_STATUS_A.equals(approve.getStatus())) {
			vo.setBizId(approve.getBizId());
			PmProcess process = processDao.findOne(approve.getProcessId());
			IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
			IPmEntity pmEntity = pmService.getEntity(approve.getBizId());
			IPmApproveListener listener = getListenerService(process, pmService);
			if (listener != null) {
				listener.doWithdraw(vo);
			}
			pmEntity.setStatus(PmConstants.APPROVE_STATUS_C);
			pmService.saveEntity(pmEntity);
			approve.setStatus(PmConstants.APPROVE_STATUS_C);
			pmApproveDao.save(approve);
			PmApproveStep step = new PmApproveStep();
			step.setApproveUserName(vo.getUserName());
			step.setApproveUserId(vo.getUserId());
			step.setStepName("撤回");
			step.setStepId(0L);
			step.setNodeId(0L);
			historyService.addHistory(approve, step);
		} else {
			// throw new ApplicationException("申请单状态不正确");
		}
	}

	@Override
	@ServerTransactional
	public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
		if (vo.getApproveId() == null || vo.getApproveId() == 0) {
			throw new InvalidParamException("approveId");
		}

		PmApprove approve = pmApproveDao.findOne(vo.getApproveId());
		if (approve != null) {
			vo.setBizId(approve.getBizId());
			PmProcess process = processDao.findOne(approve.getProcessId());
			IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
			IPmEntity pmEntity = pmService.getEntity(approve.getBizId());
			pmEntity.setStatus(PmConstants.APPROVE_STATUS_N);
			pmEntity.setApproveId(approve.getId());
			pmService.saveEntity(pmEntity);
			approve.setStatus(PmConstants.APPROVE_STATUS_E);
			approve.setCurrApproverUserId(null);
			approve.setCurrStepName(null);
			approve.setCurrApproveStepId(null);
			approve.setAutoSignLimit(null);
			pmApproveDao.save(approve);
			IPmApproveListener listener = getListenerService(process, pmService);
			PmApproveCurrVo currVo = new PmApproveCurrVo();
			BeanUtils.copyProperties(approve, currVo);
			currVo.setCurrUserId(vo.getUserId());
			currVo.setCurrUserName(vo.getUserName());
			if (listener != null) {
				listener.doStepBack(currVo, null);
			}
			PmApproveStep step = new PmApproveStep();
			step.setApproveUserName(vo.getUserName());
			step.setApproveUserId(vo.getUserId());
			step.setStepName("追回");
			step.setStepId(0L);
			step.setNodeId(0L);
			historyService.addHistory(approve, step);
		} else {
			// throw new ApplicationException("申请单状态不正确");
		}
	}

	//获取审批监听
	public IPmApproveListener getListenerService(PmProcess process,IPmService pmService) {
		IPmApproveListener listener = null;
		if (pmService instanceof IPmApproveListener) {
			listener = (IPmApproveListener) pmService;
		}
		if (StringUtils.isNotBlank(process.getListenerService())) {
			listener = SpringContextHolder.getBean(process.getListenerService());
		}
		return listener;
	}

	@Override
	public PmApprove findByApproveNo(String approveNo) throws ApplicationException {
		return pmApproveDao.findByApproveNo(approveNo);
	}

	@Override
	public List<PmApprove> findApproveByContractIdAndProcessId(Long contractId, Long processId) throws ApplicationException {
		return pmApproveDao.findApproveByContractIdAndProcessId(contractId,processId);
	}

	@Override
	public List<PmApprove> findApproveByContractIdAndStatus(Long contractId, String status) throws ApplicationException {
		return pmApproveDao.findApproveByContractIdAndStatus(contractId,status);
	}

	@Override
	public PmApprove findApproveNoByApproveId(Long approveId) {

		return pmApproveDao.findApproveNoByApproveId(approveId);
	}

	@Override
	@ServerTransactional
	public void deleteRecord(Long approveId) {
		pmApproveDao.deleteRecord(approveId);
	}


	private void addSysPush(PmApprove approve){
		threadPool.execute(()-> pmApprovePushService.addSysPush(approve));
	}

	private Specification<PmApprove> getPushSpecification(PmApproveSearchVo queryVo){
		Specification<PmApprove> spec_push = (root, query, cb)-> {
			Subquery<PmApprovePush> sq = query.subquery(PmApprovePush.class);
			Root<PmApprove> sqc = sq.correlate(root);
			Join<PmApprove, PmApprovePush> sqo = sqc.join("approvePushes");
			Path<Long> expression = sqo.get("pushToUserId");
			Predicate predicate = cb.equal(expression, queryVo.getUserId());
			sq.select(sqo).where(predicate);
			return cb.exists(sq);
		};
		return spec_push;
	}

	/**
	 * 在新审批中发起审批后回调接口
	 *
	 * @param param 新审批参数
	 * @return 结果
	 */
	@Override
	@ServerTransactional
	public RespVo<?> resServerPmApprove(String param) {
		logger.info("===resServerPmApprove===> param={}", param);
		BasPmApproveVo basPmApproveVo = JsonUtil.json2Object(BasPmApproveVo.class, param);
		if (StringUtils.isNotBlank(basPmApproveVo.getCurrApproverUserId())) {
			UserLoginVo userLoginVo = new UserLoginVo();
			String userName = basPmApproveVo.getCurrApproverUserId().replace(BasConstants.SEPARATE,"");
			userLoginVo.setLoginName(userName);
			SysUserSdk SysUserSdk = authOpenFacade.findUserByLoginName(userLoginVo);
			if (Objects.nonNull(SysUserSdk)) {
				basPmApproveVo.setCurrApproverUserId(BasConstants.SEPARATE + SysUserSdk.getUserId() + BasConstants.SEPARATE);
			}
		}
		if (StringUtils.isNotBlank(basPmApproveVo.getStatus())) {
			switch (basPmApproveVo.getStatus()) {
				// 新增
				case "0":
					basPmApproveVo.setStatus(BasConstants.APPROVE_STATUS_N);
					break;
				// 审批中
				case "1":
					basPmApproveVo.setStatus(BasConstants.APPROVE_STATUS_A);
					break;
				// 完成
				case "2":
					basPmApproveVo.setStatus(BasConstants.APPROVE_STATUS_D);
					break;
				// 驳回
				case "3":
					basPmApproveVo.setStatus(BasConstants.APPROVE_STATUS_B);
					break;
				// 删除作废
				case "4":
					basPmApproveVo.setStatus(BasConstants.APPROVE_STATUS_C);
					break;
				default:
					break;
			}
		}
		PmApprove pmApprove = new PmApprove();
		BeanUtils.copyProperties(basPmApproveVo, pmApprove);
		pmApprove.setSource("1"); // 来源于新网批
		BasPmProcessVo basPmProcessVo = basPmApproveVo.getBasPmProcessVo();
		PmProcess pmProcess = new PmProcess();
		BeanUtils.copyProperties(basPmProcessVo, pmProcess);
		processDao.save(pmProcess);
		pmApprove.setProcessId(pmProcess.getId());
		pmApproveDao.save(pmApprove);
		return new RespVo<>();
	}

	@Override
	public List<PmApprove> getAllBusinessApprove(Long approveId) {
		if (Objects.isNull(approveId)){
			return Collections.emptyList();
		}
		List<CtrContract> contractList = ctrContractClient.findAllContractByApproveId(approveId);
		if (CollectionUtils.isEmpty(contractList)){
			return Collections.emptyList();
		}
		String contractNoSuffix = contractList.stream()
				.filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, c.getContractType()))
				.map(CtrContract::getContractNo)
				.map(no -> no.replaceAll("\\D", ""))
				.filter(StrUtil::isNotBlank)
				.findAny()
				.orElse("");
		List<Long> contractIdList = contractList.stream().map(CtrContract::getId).collect(Collectors.toList());
		ApplyCtrDCSX applyCtrDCSX = dcsxClinent.findByDCSXApproveId(approveId);
		if (Objects.nonNull(applyCtrDCSX)){
			contractIdList.add(applyCtrDCSX.getId());
		}
		return pmApproveDao.findByContractIdsInAndContractNo(contractIdList, contractNoSuffix);
	}
}
