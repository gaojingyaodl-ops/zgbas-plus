//package com.spt.quartz.task;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.hsoft.admin.sdk.entity.SysUser;
//import com.hsoft.admin.sdk.open.IAdminOpenFacade;
//import com.hsoft.push.sdk.remote.PushClientHttp;
//import com.hsoft.push.sdk.vo.PushRequest;
//import com.hsoft.push.sdk.vo.PushTarget;
//import com.spt.bas.client.entity.CtrContract;
//import com.spt.bas.server.dao.CtrContractDao;
//import com.spt.bas.server.service.IApproveDealService;
//import com.spt.tools.core.date.DateOperator;
//
//// Phase 6 (06-02) — ported from com.spt.bas.server.task.RepaymentTask.
//// Source file was entirely commented out (no active class). Bean name
//// "repaymentTask" reserved by class-naming convention but no bean is
//// registered because the class body is fully commented. Kept for
//// historical reference; no sys_job row references this bean.
//
////@Component
////public class RepaymentTask {
////
////	private Logger logger = LoggerFactory.getLogger(RepaymentTask.class);
////
////	@Autowired
////	private CtrContractDao ctrContractDao;
////	@Autowired
////	private IAdminOpenFacade adminOpenFacade;
////	@Autowired
////	private PushClientHttp pushRemote;
////	@Autowired
////	private IApproveDealService approveDealService;
////
////	/**
////	 * 提前两天推送应付款通知给业务员和业务助理
////	 * */
////	@Scheduled(cron = "0 0 8 * * ?" )
////	//@Scheduled(cron = "0/10 * * * * *")
////	public void timingTask(){
////		logger.info(">>>>>>定时任务执行中:<<<<<");
////		Date date = DateOperator.truncDate(new Date());
////		Date startTime = DateOperator.addDays(date, 2);
////		Date endTime = DateOperator.addDays(date, 3);
////		//查询2天内到期待付款的采购合同和待收款的销售合同
////		List<CtrContract> repaymentList = ctrContractDao.findContractByPayFullTime(startTime, endTime);
////		approveDealService.dueToRemind(repaymentList);
////
////		//待付款通知
////		//noticeToBizUser(ids,repaymentList,"付");
////		//待收款通知
////		//noticeToBizUser(ids,receiveList,"收");
////	}
////
////
////
////	private void noticeToBizUser(Set<Long> ids,List<CtrContract> contractList,String payOrReceive){
////		if(!contractList.isEmpty()){
////			for (CtrContract entity : contractList) {
////				ids.add(entity.getMatchUserId());
////
////				List<PushTarget> lst = new ArrayList<>();
////				for (Long userId : ids) {
////					SysUser sysUser = adminOpenFacade.findUserById(userId);
////					lst.add(new PushTarget(String.valueOf(userId), sysUser.getMobile(), sysUser.getEmail()));
////				}
////
////				PushRequest req = new PushRequest();
////				req.setBusinessId(entity.getContractNo());
////				req.setModule("S");
////				req.setPushType("payOrReceiveNotice");
////				req.setSubmitUserId("sys");
////				req.setTargets(lst);
////				Map<String, Object> param = new HashMap<>();
////				param.put("payOrReceive",payOrReceive);
////				param.put("contractNo", entity.getContractNo());
////				param.put("dealAmount", entity.getTotalAmount().subtract(entity.findRealDealedAmount()).toString());
////				req.setParam(param);
////				try {
////					pushRemote.send(req);
////				} catch (Exception e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////			}
////		}
////	}
//
////}
