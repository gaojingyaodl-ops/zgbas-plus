package com.spt.pm.constant;

public interface PmConstants {
	String SEPARATE = "|";

	String CONDITION_DEFAULT = "-";

	String NODE_TYPE_USER = "user"; 	// 人员类型节点
	String NODE_TYPE_DEPT = "dept";		// 部门类型节点
	String NODE_TYPE_CENTER = "center";	// 中心类型节点

	/** 审批状态 */
	String DICT_TYPE_APPROVESTATUS = "approveStatus";
	/** 审批意见 */
	String DICT_TYPE_APPROVEOPINION = "approveOpinion";

	/** 审批意见：同意 */
	String APPROVE_OPINION_AGREE = "A";
	/** 审批意见：拒绝 */
	String APPROVE_OPINION_DENY = "D";
	/** 审批意见：追回 */
	String APPROVE_OPINION_BACK = "R";

	/** 审批编号 */
	String KEYSEQUENCE_CATEGORY_APPROVENO = "approveNo";

	/** 状态 'N-新增，A-审批中，B-驳回，D-完成，C-取消' */
	String APPROVE_STATUS_N = "N";
	String APPROVE_STATUS_A = "A";
	String APPROVE_STATUS_B = "B";
	String APPROVE_STATUS_D = "D";
	String APPROVE_STATUS_C = "C";
	String APPROVE_STATUS_E = "E";

	String APPROVE_MODE_MC = "MC";	// 待我处理
	String APPROVE_MODE_CM = "CM";	// 我发起待处理
	String APPROVE_MODE_H = "H";	// 我审批过的
	String APPROVE_MODE_S = "S";	// 我发起的
	String APPROVE_MODE_P = "P";	// 推送给我的

	String APPROVE_MODE_Z = "Z";	// 助理角色查询审批单列表
	String APPROVE_MODE_F = "F";	// 查看所有业务审批单权限查询"业务类型"审批单列表
	String APPROVE_MODE_A = "A";	// 管理员角色查看"所有"审批单列表

	/** 审批节点：发起人 */
	String PROCESS_NODE_START_USER = "start_user";
	/** 审批节点：货主业务员 */
	String PROCESS_NODE_SHIPPER_USER = "shipper_user";
	/** 审批节点：业务小组长 */
	String PROCESS_NODE_BIZ_GROUPER = "biz_grouper";
	/** 审批节点：总经理 */
	String PROCESS_NODE_BS_MANAGER = "bs_manager";
	/** 审批节点：采购员 */
	String PROCESS_NODE_BIZ_BUY_USER = "biz_buy_user";
	/** 签署连带责任保证书 */
	String PROCESS_NODE_LIABILITY_USER = "liability_user";

	/**
	 * 业务中心负责VP
	 */
	String PROCESS_NODE_COMPANY_LEADER = "company_leader";

}
