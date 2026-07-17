package com.spt.bas.server.util;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.tools.core.date.DateOperator;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class XMLEncodeModelUtil {
	private static final String request_type = "SA01";									// head-传入操作类型
	private static final String server_version = "1.0";									// head-版本号
	//private static final String uuid = "d1c4ec41-3eff-46b6-b33c-234ab1313723";		// head-交易唯一标识
	private static final String sender = "ZJWS";										// head-发送者
	private static final String request_type_recover = "01030116";
	private static final String user = "0157";
	private static final String passWord = "f3e5f457-90fb-4b67-9580";

	private static LinkedHashMap<String, Object> requesthead = new LinkedHashMap<String, Object>();		// XML报文头
	private static LinkedHashMap<String, Object> RECOVERINFO = new LinkedHashMap<String, Object>();		// XML报文数据-RECOVERINFO
	private static LinkedHashMap<String, Object> CPRPXSALEDECLARE = new LinkedHashMap<String, Object>();// XML报文数据-CPRPXSALEDECLARE
	private static LinkedHashMap<String, Object> CPrpxSaleRecover = new LinkedHashMap<String, Object>();// XML报文数据-CPrpxSaleRecover

	/**
	 * XML模型添加报文头
	 *
	 * @param key   数据名
	 * @param value 数据值
	 * @return
	 */
	public Object setRequesthead(String key, Object value) {
		if (requesthead == null) {
			requesthead = new LinkedHashMap<String, Object>();
		}
		return requesthead.put(key, value);
	}

	/**
	 * XML模型添加报文数据
	 *
	 * @param key   数据名
	 * @param value 数据值
	 * @return
	 */
	public Object setRECOVERINFO(String key, Object value) {
		if (RECOVERINFO == null) {
			RECOVERINFO = new LinkedHashMap<String, Object>();
		}
		return RECOVERINFO.put(key, value);
	}

	/**
	 * XML模型添加报文数据
	 *
	 * @param key   数据名
	 * @param value 数据值
	 * @return
	 */
	public Object setCPRPXSALEDECLARE(String key, Object value) {
		if (CPRPXSALEDECLARE == null) {
			CPRPXSALEDECLARE = new LinkedHashMap<String, Object>();
		}
		return CPRPXSALEDECLARE.put(key, value);
	}

	/**
	 * XML模型添加报文数据
	 *
	 * @param key   数据名
	 * @param value 数据值
	 * @return
	 */
	public Object setCPrpxSaleRecover(String key, Object value) {
		if (CPrpxSaleRecover == null) {
			CPrpxSaleRecover = new LinkedHashMap<String, Object>();
		}
		return CPrpxSaleRecover.put(key, value);
	}

	/**
	 * 1.内贸赊销申请XML数据
	 *
	 * @param charset 编码
	 * @return
	 */
	public String sendCPRPXSALEDECLAREXml(Charset charset) {
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"").append(charset.displayName()).append("\"?>");
		builder.append("<Root>");
		builder.append("<requesthead>");
		if (requesthead != null) {
			for (Entry<String, Object> keyVal : requesthead.entrySet()) {
				if (keyVal != null) {
					builder.append("<").append(keyVal.getKey()).append(">");
					builder.append(keyVal.getValue() != null ? keyVal.getValue() : "");
					builder.append("</").append(keyVal.getKey()).append(">");
				}
			}
		}
		builder.append("</requesthead>");
		builder.append("<CPRPXSALEDECLARE>");
		if (CPRPXSALEDECLARE != null) {
			for (Entry<String, Object> keyVal : CPRPXSALEDECLARE.entrySet()) {
				if (keyVal != null) {
					builder.append("<").append(keyVal.getKey()).append(">");
					builder.append(keyVal.getValue() != null ? keyVal.getValue() : "");
					builder.append("</").append(keyVal.getKey()).append(">");
				}
			}
		}
		builder.append("</CPRPXSALEDECLARE>");
		builder.append("</Root>");
		return builder.toString();
	}

	/**
	 * 2.按金额回款XML数据
	 *
	 * @param charset 编码
	 * @return
	 */
	public String sendRECOVERINFOXml(Charset charset) {
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"").append(charset.displayName()).append("\"?>");
		builder.append("<Root>");
		builder.append("<requesthead>");
		if (requesthead != null) {
			for (Entry<String, Object> keyVal : requesthead.entrySet()) {
				if (keyVal != null) {
					builder.append("<").append(keyVal.getKey()).append(">");
					builder.append(keyVal.getValue() != null ? keyVal.getValue() : "");
					builder.append("</").append(keyVal.getKey()).append(">");
				}
			}
		}
		builder.append("</requesthead>");
		builder.append("<RECOVERINFO>");
		if (RECOVERINFO != null) {
			for (Entry<String, Object> keyVal : RECOVERINFO.entrySet()) {
				if (keyVal != null) {
					builder.append("<").append(keyVal.getKey()).append(">");
					builder.append(keyVal.getValue() != null ? keyVal.getValue() : "");
					builder.append("</").append(keyVal.getKey()).append(">");
				}
			}
		}
		builder.append("</RECOVERINFO>");
		builder.append("</Root>");
		return builder.toString();
	}

	/**
	 * 3.按条件回款XML数据
	 *
	 * @param charset 编码
	 * @return
	 */
	public String sendCPrpxSaleRecoverXml(Charset charset) {
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"").append(charset.displayName()).append("\"?>");
		builder.append("<Root>");
		builder.append("<Requesthead>");
		if (requesthead != null) {
			for (Entry<String, Object> keyVal : requesthead.entrySet()) {
				if (keyVal != null) {
					builder.append("<").append(keyVal.getKey()).append(">");
					builder.append(keyVal.getValue() != null ? keyVal.getValue() : "");
					builder.append("</").append(keyVal.getKey()).append(">");
				}
			}
		}
		builder.append("</Requesthead>");
		builder.append("<CPrpxSaleRecoverValue>");
		builder.append("<CPrpxSaleRecover>");
		if (CPrpxSaleRecover != null) {
			for (Entry<String, Object> keyVal : CPrpxSaleRecover.entrySet()) {
				if (keyVal != null) {
					builder.append("<").append(keyVal.getKey()).append(">");
					builder.append(keyVal.getValue() != null ? keyVal.getValue() : "");
					builder.append("</").append(keyVal.getKey()).append(">");
				}
			}
		}
		builder.append("</CPrpxSaleRecover>");
		builder.append("</CPrpxSaleRecoverValue>");
		builder.append("</Root>");
		return builder.toString();
	}

	/**
	 * picc-根据请求类型生成Xml报文
	 * @param requestType
	 * @param contract
	 * @param bsCompany
	 * @param applyReceive
	 * @return
	 */
	public static String getRequestXml(String requestType,CtrContract contract,BsCompany bsCompany,ApplyReceive applyReceive) {
		XMLEncodeModelUtil model = new XMLEncodeModelUtil();
		String xmlString = "";
		UUID randomUUID = UUID.randomUUID();
		//Xml请求报文头
		if (StringUtils.equals(BasConstants.PICC_XML_CPrpxSaleRecover, requestType)) {
			model.setRequesthead("Request_Type", request_type_recover);
			model.setRequesthead("Uuid", randomUUID);
			model.setRequesthead("Sender", sender);
			model.setRequesthead("Server_Version", server_version);
			model.setRequesthead("User", user);
			model.setRequesthead("PassWord", passWord);
			model.setRequesthead("FlowInTime", DateOperator.formatDate(new Date(), DateOperator.FORMAT_STR_WITH_TIME_S));
		}else {
			model.setRequesthead("request_type", request_type);
			model.setRequesthead("server_version", server_version);
			model.setRequesthead("uuid", randomUUID);
			model.setRequesthead("flowintime", DateOperator.formatDate(new Date(), DateOperator.FORMAT_STR_WITH_TIME_S));
			model.setRequesthead("sender", sender);
		}

		if (StringUtils.equals(BasConstants.PICC_XML_CPRPXSALEDECLARE, requestType)) {
			//picc-内贸赊销请求报文
			BigDecimal totalAmount = contract.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal totalNumber = contract.getTotalNumber().setScale(2, BigDecimal.ROUND_HALF_UP);
			contract.setTotalAmount(totalAmount);
			contract.setTotalNumber(totalNumber);
			model = dealWithCPRPXSALEDECLAREXml(model, contract, bsCompany);
			xmlString = model.sendCPRPXSALEDECLAREXml(Charset.forName("GBK"));
		}else if (StringUtils.equals(BasConstants.PICC_XML_RECOVERINFO, requestType)) {
			//picc-按金额回款请求报文
			BigDecimal receiveAmount = applyReceive.getReceiveAmount().setScale(2,BigDecimal.ROUND_HALF_UP);
			applyReceive.setReceiveAmount(receiveAmount);
			model = dealWithRECOVERINFOXml(model, applyReceive, bsCompany);
			xmlString = model.sendRECOVERINFOXml(Charset.forName("GBK"));
		}else if (StringUtils.equals(BasConstants.PICC_XML_CPrpxSaleRecover, requestType)) {
			//picc-按条件回款请求报文
			BigDecimal receiveAmount = applyReceive.getReceiveAmount().setScale(2,BigDecimal.ROUND_HALF_UP);
			applyReceive.setReceiveAmount(receiveAmount);
			model = dealWithCPrpxSaleRecoverXml(model, applyReceive);
			xmlString = model.sendCPrpxSaleRecoverXml(Charset.forName("GBK"));
		}
		return xmlString;
	}

	/**
	 * picc-处理内贸赊销报文体
	 * @param model
	 * @param contract
	 * @param bsCompany
	 * @return
	 */
	private static XMLEncodeModelUtil dealWithCPRPXSALEDECLAREXml(XMLEncodeModelUtil model,CtrContract contract,BsCompany bsCompany) {
		if (contract != null && bsCompany != null) {
			//String ourCompanyName = contract.getOurCompanyName();
			//String PICCCODE = getPICCCODE(ourCompanyName,contract.getEnterpriseId());
			String happenDate = DateOperator.formatDate(contract.getDeliveryDateFrom(), DateOperator.FORMAT_STR);
			String startDate = DateOperator.formatDate(contract.getContractTime(), DateOperator.FORMAT_STR);
			String accrualDate = DateOperator.formatDate(contract.getPayFullTime(), DateOperator.FORMAT_STR);
			Long compareDays = DateOperator.compareDays(contract.getContractTime(), contract.getPayFullTime());

			model.setCPRPXSALEDECLARE("POLICYNO", "PXAB201911000003F00003");		//1保险单号
			model.setCPRPXSALEDECLARE("INSUREDNAME", "中国石油");						//2被保险人名称
			model.setCPRPXSALEDECLARE("INSUREDPICCCODE", "CN1100210000000712");		//3被保险人PICCCODE
			model.setCPRPXSALEDECLARE("LIMITFLAG", "0");							//4是否为自行掌握限额赊销记录  0/1
			model.setCPRPXSALEDECLARE("RISKNAME", "老德瑞05");						//5买方名称
			model.setCPRPXSALEDECLARE("RISKPICCCODE", "CN0000210000000102");		//6买方PICCCODE
			model.setCPRPXSALEDECLARE("RISKCOMPADDRESS", bsCompany.getAddress());	//7买方地址
			model.setCPRPXSALEDECLARE("RISKPHONE", bsCompany.getCompanyPhone());	//8买方联系电话
			model.setCPRPXSALEDECLARE("RISKMARK", "");								//9买方注册号
			model.setCPRPXSALEDECLARE("PRODUCTCATEGORY", "");						//10类别
			model.setCPRPXSALEDECLARE("PRODUCT", contract.getProductsName());		//11商品名称
			model.setCPRPXSALEDECLARE("PRODUCTNUM", contract.getTotalNumber()+"吨");	//12商品数量及单位
			model.setCPRPXSALEDECLARE("CONTRACTNO", contract.getContractNo());		//13发票号
			model.setCPRPXSALEDECLARE("RECOVERAMOUNT", "");							//14费率计算标准
			model.setCPRPXSALEDECLARE("RECOVERDATE", "");							//15回款日期
			model.setCPRPXSALEDECLARE("HAPPENDATE", happenDate);					//16出货日
			model.setCPRPXSALEDECLARE("STARTDATE", startDate);						//17信用期限起始日
			model.setCPRPXSALEDECLARE("ACCRUALDATE", accrualDate);					//18应付款日
			model.setCPRPXSALEDECLARE("INVOICEAMOUT", 1);	                        //19赊销金额
			model.setCPRPXSALEDECLARE("PAYMENTTERMS", "1");							//20信用期限（天）
			model.setCPRPXSALEDECLARE("EXPORTTRADE", "16");							//21商品类别
			model.setCPRPXSALEDECLARE("EXPORTTRADEINPUT", "塑料原料 ");					//22商品类别说明
			model.setCPRPXSALEDECLARE("SERVLERTNAME", BasConstants.APP_CODE);		//23系统名称
			model.setCPRPXSALEDECLARE("REMARK", "");								//24备注
		}
		return model;
	}

	/**
	 * picc-按金额回款报文体
	 * @param model
	 * @param applyReceive
	 * @param bsCompany
	 * @return
	 */
	private static XMLEncodeModelUtil dealWithRECOVERINFOXml(XMLEncodeModelUtil model,ApplyReceive applyReceive,BsCompany bsCompany) {
		if (applyReceive != null && bsCompany != null) {
			//String PICCCODE = getPICCCODE(applyReceive.getOurCompanyName(), applyReceive.getEnterpriseId());
			String receiveDate = DateOperator.formatDate(applyReceive.getReceiveDate(), DateOperator.FORMAT_STR);
			model.setRECOVERINFO("POLICYNO", "PXAB201911000003F00003");				//1保单号
			model.setRECOVERINFO("INSUREDPICCCODE", "CN1100210000000712");			//2被保险人PICCCODE
			model.setRECOVERINFO("BUYCOMPNAME", "老德瑞05");							//3买方名称
			model.setRECOVERINFO("BUYCOMPADDRESS", bsCompany.getAddress());			//4买方地址
			model.setRECOVERINFO("RISKPICCCODE", "CN0000210000000102");				//5风险方PICCCODE
			model.setRECOVERINFO("RISKNAME", "老德瑞05");								//6风险方名称
			model.setRECOVERINFO("PROJECTCODE", BasConstants.APP_CODE);				//7项目编号
			model.setRECOVERINFO("回款日期", receiveDate);								//8回款日期
			model.setRECOVERINFO("RECOVERAMOUNT", applyReceive.getReceiveAmount());	//9确认收汇金额
		}
		return model;
	}

	/**
	 * picc-按条件回款报文体
	 * @param model
	 * @param applyReceive
	 * @return
	 */
	private static XMLEncodeModelUtil dealWithCPrpxSaleRecoverXml(XMLEncodeModelUtil model,ApplyReceive applyReceive) {
		if (applyReceive != null) {
			//String PICCCODE = getPICCCODE(applyReceive.getOurCompanyName(), applyReceive.getEnterpriseId());
			String receiveDate = DateOperator.formatDate(applyReceive.getReceiveDate(), DateOperator.FORMAT_STR);
			model.setCPrpxSaleRecover("DeclaraTionFormno", "中国石油201907008");		//1申报单号
			model.setCPrpxSaleRecover("InsuredPiccCode", "CN1100210000000712");		//1被保险人PICCCODE
			model.setCPrpxSaleRecover("ContractNo", "23124141");					//2发票号
			model.setCPrpxSaleRecover("PolicyNo", "PXAB201911000003F00003");		//3保险单号
			model.setCPrpxSaleRecover("RecoverDate", receiveDate);					//4回款日期
			model.setCPrpxSaleRecover("XamRecoverAmount",1);						//5确认收汇金额
		}
		return model;
	}

	private static String getPICCCODE(String ourCompanyName,Long enterpriseId) {
		String code = "";
		List<BsDictData> bsDictList = BsCompanyOurUtil.getCompanyOurToBsDictDataList();
    	for (BsDictData dict : bsDictList) {
			if (StringUtils.equals(ourCompanyName, dict.getDictName())) {
				String dictCd = dict.getDictCd();
				code = DictUtil.getValue(BasConstants.PICC_CODE, dictCd);
				break;
			}
		}
		return code;
	}
}
