package com.spt.bas.client.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
import com.spt.bas.client.vo.ContractCfs;
import com.spt.tools.core.json.JsonUtil;

public class ContractCfsUtil {
	private static final String seperate = ",";
	private static final String SerialWriterString = "序列化JSON串发生异常";

	// private static final HashMap<String, Object> DefaultTypeMap = new
	// HashMap<String, Object>();
	public static Long getContractId1(String contractIdStr){
		List<Long> lstIds =getContractId(contractIdStr);
		if (lstIds.size()>0){
			return lstIds.get(0);
		}
		return null;
	}
	public static List<Long> getContractId(String contractIdStr) {
		List<String> list = Splitter.on(seperate).splitToList(contractIdStr);
		List<Long> lstIds=new ArrayList<>();
		for(String strId :list){
			if (StringUtils.isNotBlank(strId)){
				lstIds.add(Long.valueOf(strId));
			}
		}
		return lstIds;
	}
	public static String addContractId(Long contractId) {
		return addContractId(null, contractId);
	}
	public static String addContractId(String contractIdStr, Long contractId) {
		return addContractId(contractIdStr, String.valueOf(contractId));
	}
	public static String addContractId(String contractIdStr, String contractId) {
		if (StringUtils.isBlank(contractIdStr)) {
			return seperate + contractId + seperate;
		}
		if (contractIdStr.indexOf(seperate + contractId + seperate) == -1) {
			return contractIdStr + contractId + seperate;
		} else {
			return contractIdStr;
		}
	}
	
	public static String removeContractId(String contractIdStr, Long contractId) {
		return removeContractId(contractIdStr, String.valueOf(contractId));
	}

	public static String removeContractId(String contractIdStr, String contractId) {
		if (contractIdStr == null) {
			return null;
		}
		contractIdStr =  contractIdStr.replaceAll(contractId + seperate, "");
		if (StringUtils.equals(contractIdStr, seperate)) {
			contractIdStr = "";
		}
		
		return contractIdStr;
	}

//	public static String addContractCfs(String contractCfsStr, String contractId, BigDecimal number) {
//		List<ContractCfs> voList = new ArrayList<ContractCfs>();
//		if (StringUtils.isNotBlank(contractCfsStr)) {
//			voList = toJavaObjectArray(contractCfsStr, ContractCfs.class);
//		}
//		boolean isFound=false;
//		for (Iterator<ContractCfs> iterator = voList.iterator(); iterator.hasNext();) {
//			ContractCfs cfs = iterator.next();
//			if (cfs.getContractId().equals(contractId)) {
//				if (cfs.getDealNumber() == null) {
//					cfs.setDealNumber(BigDecimal.ZERO);
//				}
//				cfs.setDealNumber(cfs.getDealNumber().add(number));
//				isFound=true;
//			}
//		}
//		if (!isFound) {
//			ContractCfs vo = new ContractCfs();
//			vo.setContractId(contractId);
//			vo.setDealNumber(number);
//			voList.add(vo);
//		}
//
//		return toString(voList);
//	}

//	public static String addContractCfs(List<ContractCfs> lstCfs, String contractId, BigDecimal number) {
//		if (lstCfs == null) {
//			lstCfs = new ArrayList<ContractCfs>();
//		}
//		ContractCfs vo = new ContractCfs();
//		vo.setContractId(contractId);
//		vo.setDealNumber(number);
//		lstCfs.add(vo);
//
//		return toString(lstCfs);
//	}

//	public static String removeContractCfs(List<ContractCfs> lstCfs, String contractId, BigDecimal number) {
//
//		for (Iterator<ContractCfs> iterator = lstCfs.iterator(); iterator.hasNext();) {
//			ContractCfs cfs = iterator.next();
//			if (cfs.getContractId().equals(contractId)) {
//				if (cfs.getDealNumber() != null && cfs.getDealNumber().compareTo(number) > 0) {
//					cfs.setDealNumber(cfs.getDealNumber().subtract(number));
//				} else {
//					iterator.remove();
//				}
//			}
//		}
//
//		return toString(lstCfs);
//	}

//	public static String removeContractCfs(String contractCfsStr, String contractId, BigDecimal number) {
//		
//		List<ContractCfs> lstCfs = converList(contractCfsStr);
//		for (Iterator<ContractCfs> iterator = lstCfs.iterator(); iterator.hasNext();) {
//			ContractCfs cfs = iterator.next();
//			if (cfs.getContractId().equals(contractId)) {
//				if (cfs.getDealNumber() != null && cfs.getDealNumber().compareTo(number) > 0) {
//					cfs.setDealNumber(cfs.getDealNumber().subtract(number));
//				} else {
//					iterator.remove();
//				}
//			}
//		}
//
//		return toString(lstCfs);
//	}

	
//	public static BigDecimal removeContractCfs(StockDetail detail, String contractId, BigDecimal number) {
//		BigDecimal fixNumber = BigDecimal.ZERO;
//		List<ContractCfs> lstCfs = converList(detail.getSellContractCfs());
//		for (Iterator<ContractCfs> iterator = lstCfs.iterator(); iterator.hasNext();) {
//			ContractCfs cfs = iterator.next();
//			if (cfs.getContractId().equals(contractId)) {
//				if (cfs.getDealNumber() != null && cfs.getDealNumber().compareTo(number) > 0) {
//					cfs.setDealNumber(cfs.getDealNumber().subtract(number));
//					fixNumber = number;
//				} else {
//					fixNumber = cfs.getDealNumber();
//					iterator.remove();
//				}
//			}
//		}
//		detail.setSellContractCfs(toString(lstCfs));
//		detail.setSellContractId(removeContractId(detail.getSellContractId(), contractId));
//		return fixNumber;
//	}
	
//	public static BigDecimal getContractCfsNumber(StockDetail detail, String contractId) {
//		BigDecimal fixNumber = BigDecimal.ZERO;
//		List<ContractCfs> lstCfs = converList(detail.getSellContractCfs());
//		for (Iterator<ContractCfs> iterator = lstCfs.iterator(); iterator.hasNext();) {
//			ContractCfs cfs = iterator.next();
//			if (cfs.getContractId().equals(contractId)) {
//				fixNumber = cfs.getDealNumber();
//				break;
//			}
//		}
//		return fixNumber;
//	}

	
	public static List<ContractCfs> converList(String contractCfsStr) {
		List<ContractCfs> voList = new ArrayList<ContractCfs>();
		if (StringUtils.isNotBlank(contractCfsStr)) {
			voList = toJavaObjectArray(contractCfsStr, ContractCfs.class);
		}
		return voList;
	}

	public static String subSellContract(String contractCfsStr, String contractId) {
		List<ContractCfs> voList = new ArrayList<ContractCfs>();
		if (StringUtils.isNotBlank(contractCfsStr)) {
			voList = toJavaObjectArray(contractCfsStr, ContractCfs.class);
		}
		List<ContractCfs> returnList = new ArrayList<ContractCfs>();
		for (ContractCfs vo : voList) {
			if (!contractId.equals(vo.getContractId())) {
				returnList.add(vo);
			}
		}
		return toString(returnList);
	}

	public static <T> List<T> toJavaObjectArray(String s, Class<T> clazz) {
		return JsonUtil.json2List(clazz, s);
//		return JSONObject.parseArray(s, clazz);
	}

	public static String toString(Object o) {
		try {
			return JsonUtil.obj2Json(o);
		} catch (Exception e) {
			return SerialWriterString;
		}
	}
}
