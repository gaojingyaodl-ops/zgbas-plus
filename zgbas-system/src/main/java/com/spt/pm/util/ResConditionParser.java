package com.spt.pm.util;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.pm.cache.DeptCache;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmProcess;
import com.spt.tools.core.json.JsonUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.ExpressionExecutor;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.ExpressionToken.ETokenType;
import org.wltea.expression.IllegalExpressionException;
import org.wltea.expression.datameta.BaseDataMeta.DataType;
import org.wltea.expression.datameta.Variable;
import org.wltea.expression.op.Operator;

import java.util.*;
import java.util.stream.Collectors;

public class ResConditionParser {
	private static Logger logger = LoggerFactory.getLogger(ResConditionParser.class);
	private static final String ENTITY_SERVICE = "pmApproveContentsService";
	/** 条件变量 */
	public enum VarName {
		deptId("发起部门"), price("单价"), closeFlg("闭口合同"), totalAmount("总金额");

		private String text;

		VarName(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public static String getVarText(String varName) {
			for (VarName var : VarName.values()) {
				if (var.name().equals(varName)) {
					return var.getText();
				}
			}
			return varName;
		}
	}

	/** 验证条件表达式是否通过 */
	public static boolean validCondition(String expression, Map<String, Object> param) {

		// 设置上下文变量
		ArrayList<Variable> variables = new ArrayList<Variable>();
		param.forEach((k, v) -> {
			variables.add(Variable.createVariable(k, v));
		});

//		System.out.println("expression : " + expression);
		Object result = ExpressionEvaluator.evaluate(expression, variables);
		if (result == null) {
			logger.warn("条件解析错误,expression:{},param:{}", expression, JsonUtil.obj2Json(param));
			return false;
		}
		return (Boolean) result;
	}

	/** 获取所有变量名 */
	public static List<ExpressionToken> getVars(String expression) {
//		System.out.println("expression : " + expression);
		ExpressionExecutor ee = new ExpressionExecutor();
		try {
			List<ExpressionToken> list = ee.analyze(expression);
			List<ExpressionToken> lstV = list.stream().filter(e -> e.getTokenType() == ETokenType.ETOKEN_TYPE_VARIABLE)
					.collect(Collectors.toList());
//			System.out.println(lstV.size());
//			lstV.forEach(System.out::println);
			return lstV;
		} catch (IllegalExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, List<ExpressionToken>> analyze(String expression) {
		Map<String, List<ExpressionToken>> mapAll = new HashMap<>();
		ExpressionExecutor ee = new ExpressionExecutor();
		try {
			List<ExpressionToken> list = ee.analyze(expression);
			List<ExpressionToken> lstTmp = null;
			for (ExpressionToken e : list) {
				if (e.getTokenType() == ETokenType.ETOKEN_TYPE_VARIABLE) {
					lstTmp = new ArrayList<>();
					mapAll.put(e.getVariable().getVariableName(), lstTmp);
				}
				if (e.getTokenType() == ETokenType.ETOKEN_TYPE_OPERATOR) {
					if (e.getOperator().getToken().equals(Operator.AND.getToken())) {
						continue;
					}
				}
				lstTmp.add(e);
			}
//			list.forEach(e->{
//				if (e.getTokenType()==ETokenType.ETOKEN_TYPE_VARIABLE) {
//					List<ExpressionToken> lstTmp =new ArrayList<>();
//					lstTmp.add(e);
//					mapAll.put(e.getVariable().getVariableName(), lstTmp);
//				}
//			});

			return mapAll;
		} catch (IllegalExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getVarValue(String variableName, Object entity, Map<String, Object> mapDefault, PmProcess pmProcess) {
		try {
			Object value = null;
			if (entity instanceof PmApproveContents && StringUtils.equals(ENTITY_SERVICE, pmProcess.getEntityService())) {
				PmApproveContents approveContents = (PmApproveContents) entity;
				if (StringUtils.isNotBlank(approveContents.getContents())) {
					Object contentsEntity = JsonUtil.json2Object(Class.forName(pmProcess.getEntityName()), approveContents.getContents());
					value = PropertyUtils.getProperty(contentsEntity, variableName);
				}
			} else {
				value = PropertyUtils.getProperty(entity, variableName);
			}
			if (value == null) {
				if (mapDefault != null) {
					return (T) mapDefault.get(variableName);
				}
			}
			return (T) value;
		} catch (Exception e) {
			logger.error("getVarValue {}", e.getMessage());
			if (mapDefault != null) {
				return (T) mapDefault.get(variableName);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getVarValue(String  variableName, Object entity, Map<String, Object> mapDefault) {
		try {
			Object value = null;
			if (entity instanceof PmApproveContents) {
				PmApproveContents approveContents = (PmApproveContents) entity;
				if (approveContents.getContents() != null) {
					if (StringUtils.equals(BasConstants.SEAL_APPLY_NAME_BORROW, approveContents.getApplyName())) {
						SealBorrow borrow = JsonUtil.json2Object(SealBorrow.class, approveContents.getContents());
						value = PropertyUtils.getProperty(borrow, variableName);
					} else if (StringUtils.equals(BasConstants.SEAL_APPLY_NAME_USAGE, approveContents.getApplyName())) {
						SealUsage usage = JsonUtil.json2Object(SealUsage.class, approveContents.getContents());
						value = PropertyUtils.getProperty(usage, variableName);
					} else if (BasConstants.PROCESS_APPLY_DISPUTE.equals(approveContents.getApplyName())) {
						ApplyDispute applyDispute = JsonUtil.json2Object(ApplyDispute.class, approveContents.getContents());
						value = PropertyUtils.getProperty(applyDispute, variableName);
					} else if(BasConstants.PROCESS_APPLY_SUPPLIER_QUOTA.equals(approveContents.getApplyName())) {
						ApplySupplierQuota applySupplierQuota = JsonUtil.json2Object(ApplySupplierQuota.class, approveContents.getContents());
						value = PropertyUtils.getProperty(applySupplierQuota, variableName);
					} else if(BasConstants.VEHICLE_APPLY_NAME_USE.equals(approveContents.getApplyName())){
						VehicleUse vehicleUse = JsonUtil.json2Object(VehicleUse.class, approveContents.getContents());
						value = PropertyUtils.getProperty(vehicleUse, variableName);
					} else if(BasConstants.APPLY_SEAL_USAGE_DCSX.equals(approveContents.getApplyName())){
						SealUsageDCSX sealUsageDCSX = JsonUtil.json2Object(SealUsageDCSX.class, approveContents.getContents());
						value = PropertyUtils.getProperty(sealUsageDCSX, variableName);
					} else if(BasConstants.APPLY_SEAL_USAGE_DCTP.equals(approveContents.getApplyName())){
						SealUsageDCSX sealUsageDCSX = JsonUtil.json2Object(SealUsageDCSX.class, approveContents.getContents());
						value = PropertyUtils.getProperty(sealUsageDCSX, variableName);
					} else if(BasConstants.PROCESS_APPLY_SUPPLIER_ALLOWED.equals(approveContents.getApplyName())){
						ApplySupplierAllowed applySupplierAllowed = JsonUtil.json2Object(ApplySupplierAllowed.class, approveContents.getContents());
						value = PropertyUtils.getProperty(applySupplierAllowed, variableName);
					} else {
						value = PropertyUtils.getProperty(entity, variableName);
					}

				}
			} else {
				value = PropertyUtils.getProperty(entity, variableName);
				if (value == null) {
					if (mapDefault != null) {
						return (T) mapDefault.get(variableName);
					}
				}
			}
			return (T) value;
		} catch (Exception e) {
			logger.error("getVarValue {}", e.getMessage());
			if (mapDefault!=null) {
				return (T) mapDefault.get(variableName);
			}
		}
		return null;
	}

	public static String getExpressionText(String expression) {
		ExpressionExecutor ee = new ExpressionExecutor();
		try {
			List<ExpressionToken> list = ee.analyze(expression);
			StringBuffer sb = new StringBuffer();
			list.forEach(e -> {
				if (e.getTokenType() == ETokenType.ETOKEN_TYPE_VARIABLE) {
					// 变量
					String text = VarName.getVarText(e.getVariable().getVariableName());
					sb.append(text);
				} else if (e.getTokenType() == ETokenType.ETOKEN_TYPE_OPERATOR) {
					sb.append(" ").append(e.getOperator().getText()).append(" ");
				} else if (e.getTokenType() == ETokenType.ETOKEN_TYPE_CONSTANT) {
					if (e.getConstant().getDataType() == DataType.DATATYPE_LIST) {
						sb.append("“");
						String deptName = DeptCache.getDeptName(e.getConstant().getCollection(), "、");
						sb.append(deptName);
						sb.append("”");
					} else {
						sb.append(e.getConstant().getDataValueText());
					}
				} else if (e.getTokenType() == ETokenType.ETOKEN_TYPE_SPLITOR) {
					sb.append(e.getSplitor());
				}

			});
			return sb.toString();
		} catch (IllegalExpressionException e) {
			e.printStackTrace();
		}
		return expression;
	}

}
