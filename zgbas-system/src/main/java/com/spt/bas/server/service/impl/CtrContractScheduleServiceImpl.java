package com.spt.bas.server.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.google.common.base.Stopwatch;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrContractScheduleDao;
import com.spt.bas.server.dao.StockContractDao;
import com.spt.bas.server.dao.StockDetailPresellDao;
import com.spt.bas.server.service.IApplyInvoiceService;
import com.spt.bas.server.service.IApproveWaitDealService;
import com.spt.bas.server.service.ICtrContractScheduleService;
import com.spt.bas.server.util.SMSUtils;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class CtrContractScheduleServiceImpl extends BaseService<CtrContractSchedule> implements ICtrContractScheduleService {
	@Autowired
	private CtrContractScheduleDao ctrContractScheduleDao;
	@Autowired
	private StockContractDao stockContractDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private StockDetailPresellDao stockDetailPresellDao;
	@Autowired
	private IApproveWaitDealService approveWaitDealService;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Resource
	private IApplyInvoiceService applyInvoiceService;

	@Override
	public BaseDao<CtrContractSchedule> getBaseDao() {
		return ctrContractScheduleDao;
	}

	@Override
	public Class<CtrContractSchedule> getEntityClazz() {
		return CtrContractSchedule.class;
	}

	/**
	 * 入库日期超过7日生成待办事项
	 */
	@Override
	@ServerTransactional
	public void doWarehouseScheduleTask() {
		List<StockContract> stockList = stockContractDao.findWarehouseSchedule(7);
		stockList.forEach(s->{
			addSchedule(s.getBuyContractId(), BasConstants.WARNING_TODO_W1, BasConstants.SCHEDULE_TYPE_W);
		});


	}

	/**
	 * 预售超过7日未回补生成待办事项
	 */
	@Override
	@ServerTransactional
	public void doPreSellScheduleTask() {
		List<StockDetailPresell> presellList = stockDetailPresellDao.findPresellSchedulte(7);
		presellList.forEach(p->{
			addSchedule(p.getContractId(), BasConstants.WARNING_TODO_W2, BasConstants.SCHEDULE_TYPE_B);
		});

	}

	/**
	 * 月末3日内未收到进项发票生成待办事项
	 */
	@Override
	@ServerTransactional
	public void doBilledScheduleTask() {
		List<CtrContract> contractList = ctrContractDao.findNoBilledContract();
		contractList.forEach(c->{
			addSchedule(c.getId(), BasConstants.WARNING_TODO_B1, BasConstants.SCHEDULE_TYPE_P);
		});

	}

	private void addSchedule(Long contractId,String subject,String scheduleType) {
		CtrContractSchedule entity = ctrContractScheduleDao.findByContractIdAndScheduleType(contractId, scheduleType);
		CtrContract contract = ctrContractDao.findOne(contractId);
		if (entity == null && contract != null) {
			String contractNo = contract.getContractNo();
			Long matchUserId = contract.getMatchUserId();
			String matchUserName = contract.getMatchUserName();
			Long enterpriseId = contract.getEnterpriseId();

			CtrContractSchedule schedule = new CtrContractSchedule();
			schedule.setId(0L);
			schedule.setContractId(contractId);
			schedule.setContractNo(contractNo);
			schedule.setMatchUserId(matchUserId);
			schedule.setMatchUserName(matchUserName);
			schedule.setEnterpriseId(enterpriseId);
			schedule.setSubject(subject);
			schedule.setStatus(BasConstants.SCHEDULE_STATUS_N);
			schedule.setScheduleType(scheduleType);
			ctrContractScheduleDao.save(schedule);
		}
	}

	@Override
	public Page<CtrContractSchedule> findSchedulePage(PageSearchVo searchVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		Map<String, Object> searchParams = searchVo.getSearchParams();
		Specification<CtrContractSchedule> spec = WebUtil.buildSpecification(searchParams);
		PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
		Page<CtrContractSchedule> page = getBaseDao().findAll(spec,pageRequest);

		PageRequest pageRequest_new = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<CtrContractSchedule> pageVo = new PageImpl<>(page.getContent(), pageRequest_new, page.getTotalElements());
		return pageVo;
	}

	/**
	 * N	进行中	未到付全款日期，页面不显示
	 * B	宽限期	过付全款日期10天以内，[0,10]
	 * D	催告期	过付全款日期10-15天，(10, 15]
	 * S	逾期	过付全款日期15天以上，(15, -]
	 * P	违约	手动标记
	 * A	已完成	已全部回款
	 */
	@Override
	@ServerTransactional
	public void doUpdatePerformanceStatusTask() {
		// N-进行中
		ctrContractDao.updatePerformanceStatusN();

		// B-宽限期
		ctrContractDao.updatePerformanceStatusB();

		// D-催告期
		ctrContractDao.updatePerformanceStatusD();

		// S-逾期
		ctrContractDao.updatePerformanceStatusS();

		// A-已完成
		ctrContractDao.updatePerformanceStatusA();
	}

	/**
	 * 计算履约状态时间差值
	 * @param c
	 * @return
	 */
	private long getBetweenDays(CtrContract c) {
		Date payFullTime = c.getPayFullTime();
		Date appointPayFullTime = c.getAppointPayFullTime();

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		if (appointPayFullTime != null) {
			int res = payFullTime.compareTo(appointPayFullTime);
			if (res < 0) {
				cal.setTime(appointPayFullTime);
			} else {
				cal.setTime(payFullTime);
			}
		} else {
			cal.setTime(payFullTime);
		}

		long time1 = cal.getTimeInMillis();
		cal.setTime(date);
		long time2 = cal.getTimeInMillis();
		return (time2 - time1) / (1000 * 3600 * 24);
	}

	/**
	 * 合同签订后，到了发货日期还未发货，邮件通知相关人员，并且给出系统待办事项提醒
	 */
	@Override
	@ServerTransactional
	public void doUnDelieryNotifyTask() {
		Stopwatch started = Stopwatch.createStarted();
		List<CtrContract> unDelieryNotifyList = ctrContractDao.findUnDelieryNotify(BasConstants.ZG_ENTERPRISE_ID);
		if (CollectionUtils.isEmpty(unDelieryNotifyList)) {
			logger.info("doUnDelieryNotifyTask unDelieryNotifyList is empty!");
			return;
		}
		logger.info("doUnDelieryNotifyTask size:{},contractNos:{}", unDelieryNotifyList.size(),
				unDelieryNotifyList.stream().map(CtrContract::getContractNo).collect(Collectors.joining(BasConstants.COMMA)));

		unDelieryNotifyList.forEach(c->{
			// 通知对象
			List<Long> unDeliveryNotifyUserId = getUnDeliveryNotifyUserId(c.getMatchUserId());

			// 添加待办事项
			approveWaitDealService.addUnDeliveryDeal(c, unDeliveryNotifyUserId);

			// 发送邮件通知
			sendUnDeliveryEmail(c,unDeliveryNotifyUserId);
		});
		logger.info("doUnDelieryNotifyTask success耗时:{}",started.elapsed(TimeUnit.MILLISECONDS));
	}

	@Override
	@ServerTransactional
	public void refreshContractStatus() {
		Map<String, Object> searchMap = new HashMap<>();
		searchMap.put("NEQS_contractStatus", BasConstants.CONTRACTSTATUS_C);
		Specification<CtrContract> specification = WebUtil.buildSpecification(searchMap);
		List<CtrContract> contractList = ctrContractDao.findAll(specification);

		// 合同状态-已审批
		List<Long> contractStatusBList = contractList.stream().filter(c -> Boolean.FALSE.equals(c.getSealFlg())).
				map(CtrContract::getId).collect(Collectors.toList());

		// 合同状态-已签约
		List<Long> contractStatusSList = contractList.stream().
				filter(c -> Boolean.TRUE.equals(c.getSealFlg()) &&
						(c.getDealedAmount().compareTo(c.getTotalAmount()) < 0 ||
								c.getBilledAmount().compareTo(c.getTotalAmount()) < 0 ||
								c.getWarehouseNumber().compareTo(c.getTotalNumber()) < 0 ||
								c.getBreachAmount().compareTo(c.getReceiveBreachAmount()) > 0) &&
						!StringUtils.equals(BasConstants.CONTRACTSTATUS_S, c.getContractStatus())).
				map(CtrContract::getId).collect(Collectors.toList());

		// 合同状态-已完成
		List<Long> contractStatusDList = contractList.stream().
				filter(c -> Boolean.TRUE.equals(c.getSealFlg()) &&
						c.getDealedAmount().compareTo(c.getTotalAmount()) >= 0 &&
						c.getBilledAmount().compareTo(c.getTotalAmount()) >= 0 &&
						c.getWarehouseNumber().compareTo(c.getTotalNumber()) >= 0 &&
						c.getBreachAmount().compareTo(c.getReceiveBreachAmount()) <= 0 &&
						!StringUtils.equals(BasConstants.CONTRACTSTATUS_D, c.getContractStatus())).
				map(CtrContract::getId).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(contractStatusBList)) {
			ctrContractDao.updateContractStatusByIds(BasConstants.CONTRACTSTATUS_B, contractStatusBList);
		}
		if (CollectionUtils.isNotEmpty(contractStatusSList)) {
			ctrContractDao.updateContractStatusByIds(BasConstants.CONTRACTSTATUS_S, contractStatusSList);
		}
		if (CollectionUtils.isNotEmpty(contractStatusDList)) {
			ctrContractDao.updateContractStatusAndWxStatusByIds(BasConstants.CONTRACTSTATUS_D, BasConstants.CONTRACT_STATUS_O, contractStatusDList);
		}
	}

	@Override
	@ServerTransactional
	public void startDaDiInvoiceApply() {
		List<Long> daDiUnBillList = ctrContractDao.findDaDiUnBillList();
		if (CollectionUtils.isEmpty(daDiUnBillList)){
			logger.info("startDaDiInvoiceApply daDiUnBillList size is 0!");
			return;
		}
		daDiUnBillList.forEach(contractId-> applyInvoiceService.autoInitiatedInvoice(contractId));
	}

	/**
	 * 发送发货预警邮件通知
	 *
	 * @param contract
	 */
	private void sendUnDeliveryEmail(CtrContract contract, List<Long> unDeliveryNotifyUserId) {
		try {
			// 结算方式
			String deliveryModeStr = DictUtil.getValue(BasConstants.DICT_TYPE_BUY_DELIVERYMODE, contract.getDeliveryMode());

			// 交货方式
			String deliveryTypeStr = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, contract.getDeliveryType());

			// 交货日期
			String deliveryDateStr = DateOperator.formatDate(contract.getDeliveryDateFrom());

			// 合同状态
			String statusStr = DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTSTATUS, contract.getContractStatus());

			List<SysUserSdk> sysUserList = authOpenFacade.findByUserIds(unDeliveryNotifyUserId);
			List<String> emailList = sysUserList.stream().map(SysUserSdk::getEmail).filter(StringUtils::isNotBlank).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(emailList)) {
				logger.info("sendUnDeliveryEmail emailList is empty!");
				return;
			}
			SMSUtils.sendUnDeliveryEmail(contract, deliveryModeStr, deliveryTypeStr, deliveryDateStr, statusStr, emailList);
		} catch (Exception e) {
			logger.error("sendUnDeliveryEmail error", e);
		}
	}

	private List<Long> getUnDeliveryNotifyUserId(Long matchUserId){
		List<BsDictData> bsDictDataList = BsDictUtil.getListByCategory(BasConstants.DEAL_UN_DELIVRY_PARTY);
		List<Long> userIdList = bsDictDataList.stream().filter(b -> NumberUtil.isNumber(b.getDictName())).map(s -> Long.parseLong(s.getDictName())).distinct().collect(Collectors.toList());
		if (CollectionUtils.isEmpty(userIdList)){
			userIdList = new ArrayList<>();
		}
		userIdList.add(matchUserId);
		return userIdList;
	}
}

