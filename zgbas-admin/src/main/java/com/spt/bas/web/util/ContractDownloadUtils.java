/**
 * 
 */
package com.spt.bas.web.util;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.cache.UserCache;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsWarehouseAddrClient;
import com.spt.bas.client.remote.IBsWarehouseClient;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.CompanyAccountVo;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.core.util.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wlddh
 *
 */
public class ContractDownloadUtils {
	public static final String PARAM_WORDOUTPUTURL="wordOutputUrl";
	public static final String PARAM_PROCESSCODE="processCode";
	public static final String PARAM_WAREHOUSENAME="wareHouseName";
	public static final String PARAM_ZIP="zip";
//	public static final String PARAM_BANKNAME="bankName";
//	public static final String PARAM_RECEIVEACCOUNT="receiveAccount";
	public static final String PARAM_MATCHUSERID="matchUserId";
	public static final String PARAM_TABLELIST="tableList";
	public static final String PARAM_BSTEMPLATE="bsTemplate";
	public static final String PARAM_FILESHOWURL="fileShowUrl";
	public static File downLoadContract(Map<String, Object> param,CtrContract ctr,List<BsDictData> bsDictList){
		IBsWarehouseClient warehouseClient =SpringContextHolder.getBean(IBsWarehouseClient.class);
		IBsWarehouseAddrClient warehouseAddrClient =SpringContextHolder.getBean(IBsWarehouseAddrClient.class);
		IBsCompanyClient bsCompanyClient =SpringContextHolder.getBean(IBsCompanyClient.class);
		
		String bankName="";
		String receiveAccount="";
		if(ctr.getCompanyId()!=null){
			CompanyAccountVo company = bsCompanyClient.findCompanyAccountVo(ctr.getCompanyId());
			bankName=company.getBankName();
		    receiveAccount=company.getBankAccount();
		} 
		
		String wordOutputUrl=(String)param.get(PARAM_WORDOUTPUTURL);
		String processCode=(String)param.get(PARAM_PROCESSCODE);
		String wareHouseName=(String)param.get(PARAM_WAREHOUSENAME);
        String zip=(String)param.get(PARAM_ZIP);
       
        Long matchUserId=(Long)param.get(PARAM_MATCHUSERID);
        List<String[]> testList = (List<String[]>)param.get(PARAM_TABLELIST);
		SysUserSdk matchUser = UserCache.getEntity(matchUserId);
//		CompanyAccountVo company = bsCompanyClient.findCompanyAccountVo(ctr.getCompanyId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat fmt=new SimpleDateFormat("yyyyMMddHHmmssSSS");
//		BigDecimal payPrice = BigDecimal.ZERO;
	    //##################根据Word模板导出单个Word文档###################################################
		Map<String, String> map = new HashMap<String, String>();
        map.put("ourCompanyName", ctr.getOurCompanyName());
        map.put("contractNo",ctr.getContractNo());
        map.put("companyName", ctr.getCompanyName());
        if (ctr.getContractTime()!=null) {
        	map.put("contractTimeStr",sdf.format(ctr.getContractTime()));
        }
        
        String contractPhone = ctr.getContactPhone()==null ? "":ctr.getContactPhone();
        String contractAddr = ctr.getContactAddr() ==null ? "" :ctr.getContactAddr();
        String contractName = ctr.getContactName() == null?"":ctr.getContactName();
        map.put("contactAddr",contractAddr);
        map.put("contactName", contractName);
        map.put("contactFax",contractPhone);
        map.put("matchUserName", matchUser.getNickName());
        map.put("matchUserPhone", matchUser.getPhonenumber());
        
        String wareHouseAddr="";
		if (StringUtils.isNotBlank(wareHouseName)) {
			BsWarehouse bs = new BsWarehouse();
			bs.setWarehouseName(wareHouseName);
			List<BsWarehouse> list = warehouseClient.queryBsWarehouseName(bs);
			if (list.size() > 0) {
				BsWarehouse bsWarehouse = list.get(0);
				BsWarehouseAddr addr = warehouseAddrClient.findWarehouseAddr(bsWarehouse);
				if(addr!=null){
					wareHouseAddr = addr.getWarehouseAddr();
				}
			}
		}
		
		if(ctr.getTotalAmount()!=null){
			 map.put("totalAmountCn", RmbUtil.number2Chinese(ctr.getTotalAmount()));
			 map.put("totalAmount", NumberUtil.formatDealPrice(ctr.getTotalAmount()));
			 map.put("total", "合计人民币金额(大写)： "+ RmbUtil.number2Chinese(ctr.getTotalAmount()) +"                            (小写)： ￥"+ctr.getTotalAmount());
		}
		String deliAddr="";         //交货地点
        String deliveryMode="";     //付款方式
        String deliveryType="";		//交货方式
        String deliveryTime="";     //交货时间
        String payTime,deliveryTimeStr="";
        if (ctr.getDeliveryDateTo()!=null) {
        	deliveryTimeStr=sdf.format(ctr.getDeliveryDateTo());
        }
        if(ctr.getPayFullTime()!=null){
        	payTime=sdf.format(ctr.getPayFullTime());
        }else{
        	payTime="";
        }
        BigDecimal payAmount=new BigDecimal(0);
    	if(ctr.getBondAmount()!=null){
    		payAmount=ctr.getBondAmount();
    	}
        if(ctr.getContractAttr()!=null && !ctr.getContractAttr().equals("")){
        	 if(ctr.getContractType().equals("B")){
             	if(ctr.getDeliveryType().equals("Z")){
					deliveryType = "乙方自提。甲方须凭乙方加盖公章或合同章的提货单放货，否则相关风险全部由甲方承担。";
					deliAddr = wareHouseAddr;
                 }else if(ctr.getDeliveryType().equals("P")){
                 	deliveryType="甲方配送";
                 	if(StringUtils.isNotBlank(ctr.getDeliveryAddr())){
                      	deliAddr=ctr.getDeliveryAddr();
                      }
                 }
				 if (ctr.getBondAmount() == null || ctr.getBondAmount().compareTo(BigDecimal.ZERO) == 0) {
					deliveryMode = "货到付款，甲方指定收款账户（" + bankName + "，账号：" + receiveAccount + "）。";
					deliveryTime = "款到发货";
                 }else if(ctr.getTotalAmount().equals(ctr.getBondAmount())){
                 	deliveryMode="乙方于"+payTime+"日前付合同全款至甲方指定账户中（"+bankName+"，账号："+receiveAccount+"）。";
                 	deliveryTime=deliveryTimeStr+"前";
                 }else{
                 	deliveryMode="乙方于"+payTime+"日前支付保证金"+payAmount+"元至甲方指定账户中（"+bankName+"，账号："+receiveAccount+"），保证金充抵货款，余款支付时间：甲方到货后通知乙方，乙方2日内付清余款。";
                 	deliveryTime=deliveryTimeStr+"左右";
                 }
             }else if(ctr.getContractType().equals("S")){
             	if(ctr.getDeliveryType().equals("Z")){
                 	deliveryType="乙方自提，乙方须于提货后2日内将收货确认书寄送或传真给甲方，逾期未寄送或传真视为收到全部货物。";
                 	deliAddr=wareHouseAddr;
                 }else if(ctr.getDeliveryType().equals("P")){
                 	deliveryType="甲方配送";
                 	if(StringUtils.isNotBlank(ctr.getDeliveryAddr())){
                      	deliAddr=ctr.getDeliveryAddr();
                      }
                 }
                 if(ctr.getBondAmount()==null || ctr.getBondAmount().compareTo(BigDecimal.ZERO)==0){
                 	deliveryMode="货到付款，甲方指定收款账户（宁波银行阳明支行，账号：61040122000182065）。";
                 	deliveryTime="款到发货";
                 }else if(ctr.getTotalAmount().equals(ctr.getBondAmount())){
                 	deliveryMode="乙方于"+payTime+"日前付合同全款至甲方指定账户中（宁波银行阳明支行，账号：61040122000182065）。";
                 	deliveryTime=deliveryTimeStr+"前";
                 }else{
                 	deliveryMode="乙方于"+payTime+"日前支付保证金"+payAmount+"元至甲方指定账户中（宁波银行阳明支行，账号：61040122000182065），保证金充抵货款，余款支付时间：甲方到货后通知乙方，乙方2日内付清余款。";
                 	deliveryTime=deliveryTimeStr+"左右";
                 }
             }
        }
        String qualityStandard = ctr.getQualityStandard();
        String attachDeliveryTime = ctr.getAttachDeliveryTime();
        Date deliveryDateFrom = ctr.getDeliveryDateFrom();
        String deliveryAddr = ctr.getDeliveryAddr();
        String delivery_type = ctr.getDeliveryType();
        String ourCompanyName = ctr.getOurCompanyName();
        String qualityStandardStr = "";
		String deliveryTypeStr = "";
		String deliveryDateStr = "";
		String deliAddrStr = "";
		if (StringUtils.equals(BasConstants.CONTRACTTYPE_BUY, ctr.getContractType())) {
			qualityStandardStr = DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT,
					StringUtils.isBlank(qualityStandard) ? BasConstants.QUALITY_Y : qualityStandard);
			deliveryTypeStr = DictUtil.getValue(BasConstants.DICT_DELIVERYTYPETEXT, delivery_type);
		} else {
			qualityStandardStr = DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD,
					StringUtils.isBlank(qualityStandard) ? BasConstants.QUALITY_Y : qualityStandard);
			deliveryTypeStr = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, delivery_type);
		}
		//交货日期
		attachDeliveryTime = attachDeliveryTime == null ? BasConstants.ATTACH_DELIVERY_TIME_LR :attachDeliveryTime;
		String attachDeliveryTimeStr = DictUtil.getValue(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME, attachDeliveryTime);
		String deliveryDate = "";
		if(deliveryDateFrom!=null) {
			deliveryDate = sdf.format(deliveryDateFrom);
		}
		attachDeliveryTimeStr = StringUtils.isBlank(attachDeliveryTimeStr) ? "" : attachDeliveryTimeStr;
		deliveryDateStr = deliveryDate + attachDeliveryTimeStr;
		if (StringUtils.equals(attachDeliveryTime, BasConstants.ATTACH_DELIVERY_TIME_K)) {
			deliveryDateStr = attachDeliveryTimeStr;
		}
		//配送地址deliveryAddr
		if (StringUtils.isNotBlank(deliveryAddr)) {
			deliAddrStr = deliveryAddr;
		}else {
			deliAddrStr = wareHouseAddr;
		}
		BsCompanyOur bsCompanyOur = BsCompanyOurUtil.getBsCompanyOur(BasConstants.ZG_ENTERPRISE_ID, ourCompanyName);
		if(Objects.nonNull(bsCompanyOur)){
			String addres = bsCompanyOur.getAddress();
			String fax = bsCompanyOur.getCompanyFax();
			String email = bsCompanyOur.getEmail();
			String person = bsCompanyOur.getCompanyPerson();
			String phone = bsCompanyOur.getCompanyPhone();
			map.put("ourCompanyAddres", addres);
			map.put("ourCompanyPerson", person);
			map.put("ourCompanyFax", fax);
			map.put("ourCompanyEmail", email);
			map.put("matchUserPhone",phone);
		}
//    	for (BsDictData dict : bsDictList) {
//			if (StringUtils.equals(ourCompanyName, dict.getDictName())) {
//				String dictCd = dict.getDictCd();
//				String addres = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_ADDRES, dictCd);
//				String fax = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_FAX, dictCd);
//				String email = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_EMAIL, dictCd);
//				String person = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_PERSON, dictCd);
//				String phone = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_PHONE, dictCd);
//				map.put("ourCompanyAddres", addres);
//				map.put("ourCompanyPerson", person);
//				map.put("ourCompanyFax", fax);
//				map.put("ourCompanyEmail", email);
//				map.put("matchUserPhone",phone);
//			}
//		}
		BsCompany bsCompany = bsCompanyClient.getEntity(ctr.getCompanyId());
        if (bsCompany != null) {
        	map.put("companyPerson", bsCompany.getLegalRepresent());
        	map.put("contactName", bsCompany.getContactName());
        	map.put("email", bsCompany.getEmail());
        	map.put("contactPhone", ctr.getContactPhone());
        }
        
        //付款方式 
        map.put("deliveryMode", deliveryMode);
        //交货方式
        map.put("deliveryType", deliveryType);
        //交货时间
        map.put("deliveryTime",deliveryTime);
        //交货地址  自提读取仓库地址，配送读取配送地址
        //map.put("deliAddr", deliAddr);
        map.put("ourCompanyName", ctr.getOurCompanyName());
        map.put("payMode", ctr.getPayMode());
        map.put("qualityStandardStr", qualityStandardStr);
        map.put("deliveryTypeStr", deliveryTypeStr);
        map.put("deliveryDateStr", deliveryDateStr);
        map.put("deliAddr", deliAddrStr);
        map.put("extraTerm", ctr.getExtraTerm());
        
        String viewDownLoad = "/view/download/";
        File file=null;
        //远程文件
        BsContractTemplate template = (BsContractTemplate)param.get(PARAM_BSTEMPLATE);
        String fileShowUrl = (String)param.get(PARAM_FILESHOWURL);
        String fileId = template.getFileId();
        String[] split = fileId.split(",");
        String url;
        //本地文件
        String tempName="sales.docx";
        if(ctr.getContractType().equals(BasConstants.CONTRACT_TYPE_B)){
        	tempName ="buyer.docx";
        }
        String templatepath ="/word/";
        InputStream in = null;
        if(StringUtils.isBlank(fileId)){
        	url = templatepath+tempName;
        	in = WorderToNewWordUtils.class.getResourceAsStream(url);
        }else{
        	url =fileShowUrl+viewDownLoad+split[0];
        	File loadTemplate = WorderToNewWordUtils.loadTemplate(url);
			try {
				in = new FileInputStream(loadTemplate);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
        file = WorderToNewWordUtils.changWord2(in, map, testList);
        if (wordOutputUrl!=null && zip != null) {
        	File zipFiletmp = new File(wordOutputUrl+zip);
        	if(!zipFiletmp.exists() && !(zipFiletmp.isDirectory())){
        		zipFiletmp.mkdirs();
        	}
        	if(processCode.equals("APPLY_MATCH") || processCode.equals("APPLY_IMPORT")){
                String outputUrl = wordOutputUrl+zip+File.separator+fmt.format(new Date())+tempName;
                return   WorderToNewWordUtils.changWords(templatepath+tempName,outputUrl, map, testList);
            }
        }
	   return file;

	}
}
