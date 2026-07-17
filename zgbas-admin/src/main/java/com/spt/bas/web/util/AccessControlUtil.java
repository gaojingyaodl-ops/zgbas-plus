package com.spt.bas.web.util;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.vo.PmApproveDownVo;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class AccessControlUtil {

	//是否有撤回权限
	public static boolean hasWithdraw(PmApproveDownVo approve){
		boolean accessFlag = false;
		if(ShiroUtil.isPermitted(PermissionEnum.APPROVE_WITHDRAW.getPermissionCode()) && BasConstants.APPROVE_STATUS_D.equals(approve.getStatus())){
			accessFlag = true;
		}
		return accessFlag;
	}

	//是否有出入库作废权限
	public static boolean hasInvalid(String processCode,String statuc){
		boolean accessFlag = false;
		boolean deliveryInFlag = false;
		boolean deliveryOutFlag = false;
		boolean payFlag = false;
		boolean receiveFlag = false;
		boolean invoiceFlag = false;
		boolean invoiceReceiveFlag = false;
		boolean sealBorrowFlg = false;
		boolean confirmReceiptFlg = false;
		boolean confirmReceiptDcsxFlg = false;
		boolean interestPayFlg = false;
		boolean fundRechargeFlg = false;
		boolean dcsxPayFlg = false;
		boolean deliveryIn = ShiroUtil.isPermitted(PermissionEnum.APPROVE_IN_INVALID.getPermissionCode());
		boolean deliveryOut = ShiroUtil.isPermitted(PermissionEnum.APPROVE_OUT_INVALID.getPermissionCode());
		boolean pay = ShiroUtil.isPermitted(PermissionEnum.APPROVE_PAY_INVALID.getPermissionCode());
		boolean receive = ShiroUtil.isPermitted(PermissionEnum.APPROVE_RECEIVE_INVALID.getPermissionCode());
		boolean invoice = ShiroUtil.isPermitted(PermissionEnum.APPROVE_INVOICE_INVALID.getPermissionCode());				//开票单作废
		boolean invoiceReceive = ShiroUtil.isPermitted(PermissionEnum.APPROVE_INVOICERECEIVE_INVALID.getPermissionCode());//收票单作废
		boolean sealBorrow = ShiroUtil.isPermitted(PermissionEnum.PERM_SEAL_BORROW.getPermissionCode());//印章编辑
		boolean confirmReceipt = ShiroUtil.isPermitted(PermissionEnum.APPROVE_CONFIRMRECEIPT_INVALID.getPermissionCode());//收票单作废
		boolean confirmReceiptDcsx = ShiroUtil.isPermitted(PermissionEnum.APPROVE_CONFIRMRECEIPTDCSX_INVALID.getPermissionCode());//收票单作废
		boolean interestPay = ShiroUtil.isPermitted(PermissionEnum.APPROVE_PAY_INVALID.getPermissionCode());//中游付息申请
		boolean fundRecharge = ShiroUtil.isPermitted(PermissionEnum.APPROVE_FUND_RECHARGE_INVALID.getPermissionCode());//中游付息申请
		boolean dcsxPay = ShiroUtil.isPermitted(PermissionEnum.APPROVE_DCSX_PAY_INVALID.getPermissionCode());//中游付息申请


		if(deliveryIn && StringUtils.equals(processCode, BasConstants.PROCESS_CODE_IN)){
			deliveryInFlag = true;
		}
		if(deliveryOut && StringUtils.equals(processCode, BasConstants.PROCESS_CODE_OUT)){
			deliveryOutFlag = true;
		}
		if(pay && StringUtils.equals(processCode, BasConstants.PROCESS_CODE_PAY)){
			payFlag = true;
		}
		if(receive && StringUtils.equals(processCode, BasConstants.PROCESS_CODE_RECEIVE)){
			receiveFlag = true;
		}
		if(invoice && StringUtils.equals(processCode, BasConstants.PROCESS_CTR_INVOICE)){
			invoiceFlag = true;
		}
		if(invoiceReceive && StringUtils.equals(processCode, BasConstants.PROCESS_APPLY_INRECEIVED)){
			invoiceReceiveFlag = true;
		}
		if(sealBorrow && StringUtils.equals(processCode, BasConstants.PROCESS_APPLY_SEAL_USAGE)) {
			sealBorrowFlg = true;
		}
		if(sealBorrow && StringUtils.equals(processCode, BasConstants.PROCESS_APPLY_SEAL_BORROW)) {
			sealBorrowFlg = true;
		}
		if(confirmReceipt && StringUtils.equals(processCode, BasConstants.PROCESS_APPLY_CONFIRM_RECEIPT)) {
			confirmReceiptFlg = true;
		}	
		if(confirmReceiptDcsx && StringUtils.equals(processCode, BasConstants.PROCESS_APPLY_CONFIRM_RECEIPT_DCSX)) {
			confirmReceiptDcsxFlg = true;
		}
		if(interestPay && StringUtils.equals(processCode, BasConstants.PROCESS_CODE_INTEREST_PAY)) {
			interestPayFlg = true;
		}
		if(fundRecharge && StringUtils.equals(processCode, BasConstants.PROCESS_APPLY_FUND_RECHARGE)) {
			fundRechargeFlg = true;
		}
		if(dcsxPay && StringUtils.equals(processCode, BasConstants.PROCESS_CODE_DCSX_PAY)) {
			dcsxPayFlg = true;
		}
		if (BasConstants.APPROVE_STATUS_D.equals(statuc)) {
			if(deliveryInFlag || deliveryOutFlag || payFlag || receiveFlag || invoiceFlag || invoiceReceiveFlag || sealBorrowFlg || confirmReceiptFlg || confirmReceiptDcsxFlg || interestPayFlg || fundRechargeFlg || dcsxPayFlg){
				accessFlag = true;
			}
		}
		return accessFlag;
	}

	//是否有审批权限
	public static boolean hasApprove(PmApproveDownVo approve){
		boolean accessFlag = false;
		if(StringUtils.isNotBlank(approve.getCurrApproverUserId())) {
			if (approve.getCurrApproverUserId().endsWith(BasConstants.SEPARATE)) {
				//多人审批，使用|隔开
				if (approve.getCurrApproverUserId().indexOf(BasConstants.SEPARATE + ShiroUtil.getCurrentUserId() + BasConstants.SEPARATE) != -1
						&& !BasConstants.APPROVE_STATUS_C.equals(approve.getStatus())
						&& !BasConstants.APPROVE_STATUS_B.equals(approve.getStatus())) {
					accessFlag = true;
				}
			}else {
				if (approve.getCurrApproverUserId().equals(ShiroUtil.getCurrentUserId()+"")
						&& !BasConstants.APPROVE_STATUS_C.equals(approve.getStatus())
						&& !BasConstants.APPROVE_STATUS_B.equals(approve.getStatus())) {
					accessFlag = true;
				}
			}
		}
		return accessFlag;
	}

	//是否有编辑权限
	public static boolean hasEdit(PmApproveDownVo approve){
		boolean accessFlag = false;
		Long currentUserId = ShiroUtil.getCurrentUserId();
		Long createUserId = approve.getCreateUserId();
		Long cooperationUserId = approve.getCooperationUserId();
		if (Boolean.TRUE.equals(approve.getTradeFlg())) {
			return false;
		}
		if (Objects.equals(currentUserId, createUserId) || Objects.equals(currentUserId, cooperationUserId)) {
			if (BasConstants.APPROVE_STATUS_N.equals(approve.getStatus())
					|| BasConstants.APPROVE_STATUS_E.equals(approve.getStatus())
					|| BasConstants.APPROVE_STATUS_B.equals(approve.getStatus())
					|| BasConstants.APPROVE_STATUS_C.equals(approve.getStatus())) {
				accessFlag = true;
			}
		}
		return accessFlag;
	}

	//是否有驳回权限
	public static boolean hasBack(PmApproveStep step){
		boolean accessFlag = false;
		if (step != null && step.getBackFlg()) {
			accessFlag = true;
		}
		return accessFlag;
	}

	//是否有编辑文件权限
	public static boolean hasEditFile(PmApproveDownVo approve){
		boolean editFileFlag = false;
		String createUserName = approve.getCreateUserName();
		String currApproverUserName = approve.getCurrApproverUserName();
		String status = approve.getStatus();
		if (StringUtils.equals(status, BasConstants.APPROVE_STATUS_A)
				|| StringUtils.equals(status, BasConstants.APPROVE_STATUS_D)) {
			editFileFlag = false;
		} else if (!StringUtils.equals(status, BasConstants.APPROVE_STATUS_A)
				&& !StringUtils.equals(status, BasConstants.APPROVE_STATUS_D)
				&& StringUtils.equals(createUserName, currApproverUserName)) {
			editFileFlag = true;
		} else if (StringUtils.isBlank(currApproverUserName)) {
			editFileFlag = true;

		}
		if (hasApprove(approve) || hasEdit(approve)) {
			editFileFlag = true;
		}
		return editFileFlag;
	}

}
