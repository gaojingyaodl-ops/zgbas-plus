package com.spt.quartz.task;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.ctr.service.ICtrServiceContractService;
import com.spt.bas.server.ctr.service.impl.CtrContractDataRefService;
import com.spt.bas.server.dao.CtrContractSettlementDao;
import com.spt.bas.server.filter.IAutoSealPdfSignFilter;
import com.spt.bas.server.rocketmq.TestRocketmqProducer;
import com.spt.bas.server.rt.RtApi;
import com.spt.bas.server.service.*;
import com.spt.bas.server.service.impl.CtrContractPdfService;
import com.spt.bas.server.service.impl.OverdueInterestProcessor;
import com.spt.bas.server.service.impl.PiccDataSyncServiceImpl;
import com.spt.bas.server.stock.service.IStockVirtualService;
import com.spt.bas.server.stock.service.impl.StockDataTransferService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.cmd.ICommand;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * basServer command executor.
 *
 * <p>Phase 6 (06-04) — ported from {@code com.spt.bas.server.command.BasCommandExecutor}. Bean name
 * {@code "basCommandExecutor"} aligns with {@code sys_job.invoke_target} short name
 * {@code basCommandExecutor.executeCommand('${cmd}')}. Per D-P6-07/D-P6-09, command executors land
 * alongside other handlers in {@code com.spt.quartz.task}; the {@code ICommand} interface stays in
 * {@code com.spt.tools.core.cmd} (zgbas-common, no migration needed).
 *
 * <p>Pitfall 9: the 4 {@code @Autowired} task classes ({@link ApplyPayTask} /
 * {@link BudgetSettlementTask} / {@link CtrContractScheduleTask} / {@link DcsxAutoApplyPayTask})
 * were ported to {@code com.spt.quartz.task} in 06-02, so the imports resolve same-package.
 *
 * <p>Pitfall 6: the source annotation-based routing (xxl-job shared {@code executeCommand}
 * value across 3 executors: BasCommandExecutor / ReportCommandExecutor / BasWebCommand). In quartz
 * mode, sys_job rows distinguish them by bean name in {@code invoke_target}. The source job-param
 * fallback is deleted because {@code commandline} is now passed directly by
 * {@code JobInvokeUtil.invokeMethod} reflection (sys_job.invoke_target conveys the arg).
 *
 * <p><b>Phase 6 06-06 (D-P6-06 fail-fast caught wiring defect — Rule 1 auto-fix):</b>
 * added {@code @Primary}. The pre-existing {@code com.spt.tools.core.cmd.CommandExecutor}
 * (zgbas-common) has {@code @Autowired(required=false) ICommand commandExecutor;} which expects
 * a single {@code ICommand} bean. In the source microservices each module had at most one
 * {@code ICommand} impl, so the injection was unambiguous. The Phase 6 06-04 monolith convergence
 * puts all three ICommand implementations ({@code basCommandExecutor} / {@code basWebCommand} /
 * {@code reportCommandExecutor}) in the same Spring context, breaking startup with
 * {@code NoUniqueBeanDefinitionException}. {@code @Primary} on BasCommandExecutor resolves the
 * ambiguity in favor of the most feature-complete executor (56 sub-commands vs 2 / 3). The other
 * two beans remain addressable by name via {@code sys_job.invoke_target} and via
 * {@code context.getBean("reportCommandExecutor")} / {@code context.getBean("basWebCommand")}.
 *
 * @author wlddh
 */
@Primary
@Component("basCommandExecutor")
public class BasCommandExecutor implements ICommand {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ICtrContractUpdateService ctrContractUpdateService;
	@Autowired
	private ICtrContractSaveService ctrContractSaveService;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private IPushContractService pushContractService;
	@Autowired
	private StockDataTransferService stockDataTransferService;
	@Autowired
	private IBsCompanyService bsCompanyService;
	@Autowired
	private IBsCompanyManageService bsCompanyManageService;
	@Autowired
	private ICtrContractTextService textService;
	@Autowired
	private ICtrServiceContractService serviceContractService;
	@Autowired
	private IPmApproveService pmApproveService;
	@Autowired
	private ISealUsageService sealUsageService;
	@Autowired
	private BudgetSettlementTask budgetSettlementTask;
	@Autowired
	private IApplyInvoiceService invoiceService;
	@Autowired
	private ICtrContractLoadingService ctrContractLoadingService;
	@Autowired
	private ApplyPayTask applyPayTask;
	@Autowired
	private IBudgetSettlementService budgetSettlementService;
	@Autowired
	private ICtrContractSettlementService ctrContractSettlementService;
	@Autowired
	private PiccDataSyncServiceImpl piccDataSync;
	@Autowired
	private ICtrContractScheduleService scheduleService;
	@Autowired
	private RtApi rtApi;
	@Autowired
	private IStockVirtualService stockVirtualService;
	@Autowired
	private CtrContractDataRefService ctrContractDataRefService;
	@Autowired
	private TestRocketmqProducer testRocketmqProducer;
	@Autowired
	private ICtrContractProfitService ctrContractProfitService;
	@Autowired
	private IApplyConfrimReceiptDcsxService applyConfrimReceiptDcsxService;
	@Resource
	private CtrContractPdfService ctrContractPdfService;
	@Resource
	private ICtrContractSettlementAmountService settlementAmountService;
	@Resource
	private CtrContractSettlementDao ctrContractSettlementDao;
	@Resource
	private OverdueInterestProcessor overdueInterestProces;
	@Resource
	private CtrContractScheduleTask contractScheduleTask;
	@Resource
	private IBsCompanyCreditService companyCreditService;
	@Resource
	private IAutoSealPdfSignFilter autoSealPdfSignFilter;
	@Resource
	private IApplyDcsxService applyDcsxService;
	@Autowired
	private IBusinessRestrictRelieveService businessRestrictRelieveService;
	@Resource
	private IApplyDeliveryOutService applyDeliveryOutService;
	@Autowired
	private DcsxAutoApplyPayTask dcsxAutoApplyPayTask;
	@Autowired
	private IWeChatWorkService weChatWorkService;

	@Override
	public boolean executeCommand(String commandline) throws Exception {
		if (StringUtils.isNotBlank(commandline)) {
			if (commandline.indexOf("reContract") >= 0) {
				// reContract 1452
				ctrContractSaveService.refreshContractText(commandline.replace("reContract ", ""));
				return true;
			} else if (commandline.indexOf("refreshProdutsName") >= 0) {
				// refreshProdutsName
				ctrContractUpdateService.refreshProdutsName();
				return true;
			} else if (commandline.indexOf("refUcsTask") >= 0) {
				// 刷新ucs合同推送定时任务
				PushContract pushContract = new PushContract();
				pushContract.setTargetCode("ucs");
				pushContract.setPushType("t_ctr_contract");
				pushContractService.doContractTask(pushContract);
				pushContract.setPushType("settlement");
				pushContractService.doContractTask(pushContract);
				pushContract.setPushType("contractStatus");
				pushContractService.doContractTask(pushContract);
				pushContract.setPushType("settlementStatus");
				pushContractService.doContractTask(pushContract);
				return true;
			} else if (commandline.indexOf("refSaasTask") >= 0) {
				// 刷新saas合同推送定时任务
				PushContract pushContract = new PushContract();
				pushContract.setTargetCode("saas");
				pushContract.setPushType("t_ctr_contract");
				pushContractService.doContractTask(pushContract);
				pushContract.setPushType("settlement");
				pushContractService.doContractTask(pushContract);
				pushContract.setPushType("contractStatus");
				pushContractService.doContractTask(pushContract);
				return true;
			} else if (commandline.startsWith("rela ")) {
				//刷新合同关联表数据
				String[] args = commandline.split(" ");
				ctrContractSaveService.refreshRela(args[1]);
				return true;
			} else if (commandline.startsWith("relaAll ")) {
				// 刷新所有合同关联表数据
				String[] args = commandline.split(" ");
				if (args.length != 2) {
					System.out.println("参数不匹配");
					return false;
				}
				refreshRelaAll(Long.valueOf(args[1]));
				return true;
			} else if (commandline.startsWith("transfer ")) {
				String[] args = commandline.split(" ");
				if (args.length != 2) {
					System.out.println("参数不匹配");
					return false;
				}
				stockDataTransferService.transfer(Long.valueOf(args[1]));
				return true;
			} else if (commandline.startsWith("retf ")) {
				//retf ZSYB190701034
				//重新生成合同库存关联数据
				String[] args = commandline.split(" ");
				String buyContractNo = args[1];
				stockDataTransferService.transfer(buyContractNo);
				return true;
			}else if (commandline.startsWith("retfAll ")) {
				//刷新所有合同库存关联表
				String[] args = commandline.split(" ");
				if (args.length != 2) {
					System.out.println("参数不匹配");
					return false;
				}
				stockDataTransferService.refreshRela(Long.valueOf(args[1]));
				return true;
			}else if (commandline.startsWith("makePairCode ")) {
				//makePairCode 1
				//刷新代采合同排序号pairCode
				String[] args = commandline.split(" ");
				ctrContractUpdateService.makePairCodeForMatch(Long.valueOf(args[1]));
				return true;
			}else if (commandline.equalsIgnoreCase("cache")){
				// 刷新缓存
				LocalCacheManager.refreshAll();
				return true;
			}else if (commandline.startsWith("refText ")){
				String[] args = commandline.split(" ");
				Long id = Long.valueOf(args[1]);
				CtrServiceContract entity = serviceContractService.getEntity(id);
				textService.saveServiceText(entity);
				return true;
			}else if (commandline.startsWith("sealUsage ")){
				String[] args = commandline.split(" ");
				Long id = Long.valueOf(args[1]);
				PmApprove entity = pmApproveService.getEntity(id);
				sealUsageService.startSealUsage(entity);
				return true;
			}else if (commandline.startsWith("updateBudgetSettlement")){
				budgetSettlementTask.updateBudgetSettlement();
				return true;
			}else if (commandline.startsWith("recoverTotalCreditAmount")){
				bsCompanyService.recoverTotalCreditAmount();
				return true;
			}else if (commandline.startsWith("autoInitiatedInvoice")){
				invoiceService.autoInitiatedInvoice(2926L);
				return true;
			}else if (commandline.startsWith("axqLoading")){
				ctrContractLoadingService.axqLoadingBill(4L);
				return true;
			}else if (commandline.startsWith("refreshLoadingBillStatus")){
				ctrContractLoadingService.refreshLoadingBillStatus(4L);
				return true;
			}else if (commandline.startsWith("autoStartPayProcess")){
				applyPayTask.autoStartPayProcess();
				return true;
			}else if (commandline.startsWith("getParamByCompanyGrade")){
				budgetSettlementService.getParamByCompanyGrade(6362L,"");
				return true;
			}else if (commandline.startsWith("refreshAllSettlement")){
				ctrContractSettlementService.refreshAllSettlement();
				return true;
			}else if (commandline.startsWith("initPiccDate ")){
				// piccDataSync.initPiccData("664740");
				// piccDataSync.initPiccData("664861");
//				String[] args = commandline.split(" ");
//				piccDataSync.initPiccData(args[1]);
				return true;
			}else if (commandline.startsWith("doUnDelieryNotifyTask")){
				scheduleService.doUnDelieryNotifyTask();
				return true;
			}else if (commandline.startsWith("getRtToken")){
				rtApi.getRtToken();
				return true;
			}else if (commandline.startsWith("updateCompanyGrey")){
				bsCompanyManageService.updateStatusByTask("宁波新三和壳体有限公司");
				return true;
			}else if (commandline.startsWith("updateGreyListByTask")){
				bsCompanyService.updateGreyListByTask();
				return true;
			}else if (commandline.startsWith("autoDeleteStockVirtual")){
				stockVirtualService.autoDeleteStockVirtual();
				return true;
			}else if (commandline.startsWith("refreshBuyBilledAmount")){
				ctrContractDataRefService.refreshBuyBilledAmount();
				return true;
			}else if (commandline.startsWith("refreshSellBilledAmount")){
				ctrContractDataRefService.refreshSellBilledAmount();
				return true;
			} else if (commandline.startsWith("refreshContractStatus")) {
				scheduleService.refreshContractStatus();
				return true;
			} else if (commandline.startsWith("doUpdatePerformanceStatus")) {
				scheduleService.doUpdatePerformanceStatusTask();
				return true;
			} else if (commandline.startsWith("leaveReleasePublic")){
				bsCompanyService.leaveReleasePublic();
				return true;
			} else if (commandline.startsWith("sendString")){
				testRocketmqProducer.sendString();
				return true;
			} else if (commandline.startsWith("sendContract")) {
				testRocketmqProducer.sendContract();
				return true;
			} else if (commandline.startsWith("sendOrder")) {
				testRocketmqProducer.send();
				return true;
			} else if (commandline.startsWith("initHistoryProfit")){
				ctrContractProfitService.initHistoryProfit();
				return true;
			} else if (commandline.startsWith("refreshProfitData")){
				ctrContractProfitService.refreshProfitData("SPT202412180010");
				return true;
			} else if (commandline.startsWith("initHistoryConfirmReceiptDcsx")){
				applyConfrimReceiptDcsxService.initHistoryConfirmReceiptDcsx();
				return true;
			} else if (commandline.startsWith("generateContractPdf")){
				ctrContractPdfService.generateContractPdf(5520L);
				return true;
			} else if (commandline.startsWith("refreshBreachCommission ")){
				String[] args = commandline.split(" ");
				settlementAmountService.refreshBreachCommission(args[1]);
				return true;
			} else if(commandline.startsWith("refreshSettlement ")){
				String[] args = commandline.split(" ");
				if (args.length > 0){
					List<CtrContractSettlement> settlements = ctrContractSettlementDao.findBySellContractNo(args[1]);
					ctrContractSettlementService.refreshSettlement(settlements.stream().map(CtrContractSettlement::getId).collect(Collectors.toList()));
				}
				return true;
			}else if (commandline.startsWith("doAutoSign")){
				pmApproveService.doAutoSign();
				return true;
			} else if (commandline.startsWith("syncCompanyBusinessExpansion")) {
				bsCompanyService.syncCompanyBusinessExpansion();
				return true;
			} else if (commandline.startsWith("refreshOverdueInterest ")){
				String[] args = commandline.split(" ");
				if (args.length > 1){
					overdueInterestProces.refreshOverdueInterest(args[1]);
				}else {
					overdueInterestProces.refreshOverdueInterest("");
				}
				return true;
			} else if (commandline.startsWith("doSignLogistics")){
				contractScheduleTask.doSignLogistics();
				return true;
			} else if (commandline.startsWith("doAutoSign")){
				pmApproveService.doAutoSign();
				return true;
			} else if (commandline.startsWith("doTaskByContractNo ")){
				String[] args = commandline.split(" ");
				if (args.length < 1){
					return false;
				}
				budgetSettlementService.doTaskByContractNo(args[1]);
				return true;
			} else if (commandline.startsWith("initCompanyCredit")){
				companyCreditService.initCompanyCredit();
				return true;
			} else if (commandline.startsWith("syncHisCompanyCreditId")){
				companyCreditService.syncHisCompanyCreditId();
				return true;
			} else if (commandline.startsWith("syncHisCreditUserAmount")){
				companyCreditService.syncHisCreditUserAmount();
				return true;
			} else if (commandline.startsWith("generateSealPDFSignDCSX")){
				PmApprove approve = pmApproveService.findByApproveNo("SPT202411260013");
				ApplyCtrDCSX entity = applyDcsxService.findByContractNo("SPTX241126006");
				autoSealPdfSignFilter.generateSealPDFSignDCSXV2(approve, entity);
				return true;
			} else if (commandline.startsWith("startDaDiInvoiceApply")){
				scheduleService.startDaDiInvoiceApply();
				return true;
			} else if (commandline.startsWith("resetUsableCount")){
				businessRestrictRelieveService.resetUsableCount();
				return true;
			} else if (commandline.startsWith("refreshShippingFile")){
				applyDeliveryOutService.refreshShippingFile(null);
				return true;
			} else if (commandline.startsWith("autoApplyDcsxPay")){
				dcsxAutoApplyPayTask.autoHb60DayNotApplyDcsxPay();
				return true;
			} else if (commandline.startsWith("pushWeChatWorkLeaderboard")){
				weChatWorkService.pushWeChantWorkLeaderboardForCustomerDevelop();
				return true;
			}

		}
		return false;
	}

	/** 迁移数据 */
	public void refreshRelaAll(Long enterpriseId) {
		int pageIndex = 1;
		PageSearchVo queryVo = new PageSearchVo();
		queryVo.setRows(100);
		queryVo.setPage(pageIndex);
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("EQL_enterpriseId", enterpriseId);
		searchParams.put("NEQS_contractStatus", BasConstants.CONTRACTSTATUS_C);
		searchParams.put("EQS_contractType", BasConstants.CONTRACT_TYPE_B);
		searchParams.put("NNS_linkContractId", "1");
		queryVo.setSearchParams(searchParams);
		Page<CtrContract> page = ctrContractService.findPage(queryVo);
		AtomicInteger cnt = new AtomicInteger(0);
		while (page.hasContent()) {
			logger.info("page:{} total:{}",pageIndex, page.getTotalElements());
			page.forEach(c -> {
				ctrContractSaveService.refreshRela(c.getContractNo());
				int index = cnt.incrementAndGet();
				logger.info("contract index:{}", index);
			});
			pageIndex++;
			queryVo.setPage(pageIndex);
			page = ctrContractService.findPage(queryVo);
		}
	}

}
