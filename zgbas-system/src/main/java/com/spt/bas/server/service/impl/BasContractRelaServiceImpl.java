package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasContractRela;
import com.spt.bas.client.entity.BasContractText;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.vo.BasContractRelaVo;
import com.spt.bas.client.vo.BasContractVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BasContractRelaDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.*;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component("basContractRelaService")
@Transactional(readOnly = true)
public class BasContractRelaServiceImpl extends BaseService<BasContractRela> implements IBasContractRelaService, IPmService, IPmApproveListener {
	@Autowired
	private BasContractRelaDao basContractRelaDao;
	@Autowired
	private IBasContractService contractService;
	@Autowired
	private BsCompanyDao companyDao;
	@Autowired
	private IBasContractTextService contractTextService;
	@Autowired
	private IBasBrandService brandService;
	@Autowired
	private IBasReceiveService receiveService;
	
	@Override
	public BaseDao<BasContractRela> getBaseDao() {
		return basContractRelaDao;
	}
	
	@Override
	public Class<BasContractRela> getEntityClazz() {
		return BasContractRela.class;
	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		BasContractRela rela=null;
		if(pmEntity instanceof BasContractRelaVo){
			BasContractRelaVo vo = (BasContractRelaVo) pmEntity;
			rela = new BasContractRela();
			BeanUtils.copyProperties(vo, rela);
			//保存合同关联表
			rela = basContractRelaDao.save(rela);
			vo.setId(rela.getId());
			//保存合同，保存合同关联表
			List<BasContractVo> bcList = contractService.saveBatchByRelaApprove(vo);
			if(rela.getExposureFlg()){		
				//修改敞口业务的buyerId,sellId并保存场口业务相关合同的剩余数量
				rela = upateExposureContract(rela,bcList);
			}
			//再次保存关联合同表相关信息，如：contractID，总金额等
			parseEntiy(rela);
		}else{
			BasContractRela entity = (BasContractRela) pmEntity;
			//查询关联表
			List<BasContract> bcList = contractService.findByContractRelaId(entity.getId());
			for(BasContract bc:bcList){
				bc.setStatus(entity.getStatus());
				if (entity.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
					//更新合同状态：已签约
					bc.setContractStatus(BasConstants.CONTRACTSTATUS_S);
				}
				contractService.save(bc);
			}
			basContractRelaDao.save(entity);
		}
		
		return rela;
	}
	//保存关联合同表
	private void parseEntiy(BasContractRela rela) throws ApplicationException {
		//查询关联表
		List<BasContract> bcList = contractService.findByContractRelaId(rela.getId());
		boolean closeFlg =false;
		BigDecimal buyNumber=BigDecimal.ZERO;//采购数量
		BigDecimal buyAmount=BigDecimal.ZERO;//采购总价
		BigDecimal sellNumber=BigDecimal.ZERO;//销售数量
		BigDecimal sellAmount=BigDecimal.ZERO;//销售总价
		for(BasContract bc:bcList){
			if (BasConstants.CONTRACTTYPE_BUY.equals(bc.getContractType())) {
				//采购
				buyNumber=buyNumber.add(bc.getDealNumber());
				buyAmount=buyAmount.add(bc.getDealAmount());
			}else {
				//销售
				sellNumber=sellNumber.add(bc.getDealNumber());
				sellAmount=sellAmount.add(bc.getDealAmount());
			}
		}
		rela.setBuyNumber(buyNumber);
		rela.setBuyAmount(buyAmount);
		rela.setSellNumber(sellNumber);
		rela.setSellAmount(sellAmount);
		if (buyNumber.compareTo(sellNumber)==0) {
			closeFlg=true;
		}
		rela.setCloseFlg(closeFlg);
		rela = basContractRelaDao.save(rela);
		String relaBuyerId = rela.getBuyContractId();
		String relaSellId = rela.getSellContractId();
		//非敞口业务的判断,判断剩余数量
		if(!rela.getExposureFlg()){
			for(BasContract bc:bcList){
				bc.setCloseFlg(closeFlg);
				
					if(closeFlg){
						//闭口业务，剩余数量设为0
						bc.setRemainNumber(BigDecimal.ZERO);
					}else{
						//如果非闭口业务，则将剩的设为剩余数量
						if(buyNumber.compareTo(sellNumber)>0){
							BigDecimal curRemainNumber = sellNumber;
							//有buyNumber剩余数量
							if (BasConstants.CONTRACTTYPE_BUY.equals(bc.getContractType())) {
								BigDecimal da = bc.getDealNumber();
								if(curRemainNumber.compareTo(BigDecimal.ZERO)<=0){
									bc.setRemainNumber(da);
								}else if(curRemainNumber.compareTo(da)>0){
									bc.setRemainNumber(BigDecimal.ZERO);
									curRemainNumber = curRemainNumber.subtract(da);
								}else{
									bc.setRemainNumber(da.subtract(curRemainNumber));
								}
							}else{
								bc.setRemainNumber(BigDecimal.ZERO);
							}
						}else{
							//卖的数量>买的数量,则卖有剩余
							BigDecimal curRemainNumber = buyNumber;
							if (BasConstants.CONTRACTTYPE_SELL.equals(bc.getContractType())) {
								BigDecimal da = bc.getDealNumber();
								if(curRemainNumber.compareTo(BigDecimal.ZERO)<=0){
									bc.setRemainNumber(da);
								}else if(curRemainNumber.compareTo(da)>0){
									bc.setRemainNumber(BigDecimal.ZERO);
									curRemainNumber = curRemainNumber.subtract(da);
								}else{
									bc.setRemainNumber(da.subtract(curRemainNumber));
									curRemainNumber = curRemainNumber.subtract(da);
								}
							}else{
								bc.setRemainNumber(BigDecimal.ZERO);
							}
							
						}
					}
					//判断剩余数量结束
					contractService.save(bc);
			}
		}else if(StringUtils.isNotBlank(relaBuyerId)&&StringUtils.isNotBlank(relaSellId)){
			//若属于敞口业务，没有关联合同，则在contract save里已保存好了剩余数量，则这里不需要再次判断操作。若有关联合同，则需要扣减关联合同的剩余数量，并新增合同的剩余数量
			//目前这里属于敞口业务,有关联合同的情况
			String[] relaBuyerIds = rela.getBuyContractId().split("\\|");
			String[] relaSellIds = rela.getSellContractId().split("\\|");
			for(int i=0;i<bcList.size();i++){
				BasContract bc = bcList.get(i);
				BasContract compareBc = null;
				if (BasConstants.CONTRACTTYPE_BUY.equals(bc.getContractType())) {
					//获取卖的Id
					Long sellId = Long.parseLong(relaSellIds[i]);
					compareBc = contractService.getEntity(sellId);
				}else{
					//获取买的ID
					Long buyerId = Long.parseLong(relaBuyerIds[i]);
					compareBc = contractService.getEntity(buyerId);
				}
				BigDecimal remainNumber = compareBc.getRemainNumber();
				BigDecimal dealNumber = bc.getDealNumber();
				if(remainNumber.compareTo(dealNumber)==0){
					compareBc.setRemainNumber(BigDecimal.ZERO);
					bc.setRemainNumber(BigDecimal.ZERO);
					//compareBc.setCloseFlg(true);
					//bc.setCloseFlg(true);
				}else {
					//采购合同数量少于原合同数量
					remainNumber = remainNumber.subtract(dealNumber);
					compareBc.setRemainNumber(remainNumber);
					//compareBc.setCloseFlg(false);
					bc.setRemainNumber(BigDecimal.ZERO);
					//bc.setCloseFlg(true);
				}
				contractService.save(compareBc);
				//保存剩余数量结束
				contractService.save(bc);
			}
		}
	}


	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			BasContractRela entity = (BasContractRela) pmEntity;
			String productName = entity.getProductName();
			//获取品名对应的名称
			List<BasContract> bcList = contractService.findByContractRelaId(entity.getId());
			StringBuffer buyCompanyName = new StringBuffer("");
			StringBuffer sellCompanyName = new StringBuffer("");
			//吨
			BigDecimal dealNumber =BigDecimal.ZERO;
			String numberUnit = "";
			for(BasContract bc:bcList){
				Long companyId = bc.getOppCompanyId();
				BsCompany company = companyDao.findOne(companyId);
				
				if("B".equals(bc.getContractType())){
					buyCompanyName.append(company.getCompanyName()+",");
					dealNumber = dealNumber.add(bc.getDealNumber());
					if(StringUtils.isBlank(numberUnit)){
						numberUnit=bc.getNumberUnit();
					}
				}else{
					sellCompanyName.append(company.getCompanyName()+",");
					dealNumber = dealNumber.add(bc.getDealNumber());
					if(StringUtils.isBlank(numberUnit)){
						numberUnit=bc.getNumberUnit();
					}
				}
			}
			String dealNumberStr = NumberUtil.formatNumber(dealNumber, "#.###");
			//String dealNumber = entity.getDealNumber()==null?"":entity.getDealNumber().intValue()+"";
			
			
			if(StringUtils.isBlank(buyCompanyName)){
				buyCompanyName =new StringBuffer(BasConstants.UN_COMPANY_NAME+",");
			}
			if(StringUtils.isBlank(sellCompanyName)){
				sellCompanyName =new StringBuffer(BasConstants.UN_COMPANY_NAME+",");
			}
			String subject = String.format("%s/%s-%s/%s%s", productName,
					buyCompanyName.substring(0, buyCompanyName.length()-1), sellCompanyName.substring(0, sellCompanyName.length()-1),dealNumberStr,numberUnit);
			return subject;
		}
		return null;
	}
	/**
	 * 审批完成后，有牌号数据，则要保存相关的牌号数据
	 */
	@Override
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			BasContractRela rela = basContractRelaDao.findOne(approve.getBizId());
			String brandNumber = rela.getBrandNumber();
			String productCode = rela.getProductCode();
			brandService.saveBrand(productCode, brandNumber,approve.getEnterpriseId());
			List<BasContract> contList = contractService.findByContractRelaId(rela.getId());
			if(rela.getProductCode().indexOf("SL")>=0){
				for(BasContract bc:contList){
					//保存电子合同Id
					BasContractText contractText = contractTextService.saveContract(bc);
					if(contractText!=null){
						bc.setContractTextId(contractText.getId());
						contractService.save(bc);
					}
				}
			}
			//销售合同审批完后，自动生成两条收款记录（定金（为0不显示）、余款）
			for(BasContract bc:contList){
				if(bc.getContractType().equals(BasConstants.CONTRACTTYPE_SELL)){
					receiveService.saveReceive(bc);
				}
			}
		}
	}
	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		basContractRelaDao.updateFileId(id, fileId);
	}

	@Override
	public String findSaleContractIdByBuyId(String buyContractId) {
		return basContractRelaDao.findSaleContractIdByBuyId(buyContractId);
	}
	
	/**
	 * 更新合同关联表的buyerId,sellId字段，并更新剩余数量
	 * @param rela
	 * @param bcVoList
	 * @return
	 */
	public BasContractRela upateExposureContract(BasContractRela rela,List<BasContractVo> bcVoList){
		StringBuffer buyerContractId=new StringBuffer();
		StringBuffer sellContractId=new StringBuffer();
		if(bcVoList!=null&&bcVoList.size()>0){
			String contractType=bcVoList.get(0).getContractType();
			for(BasContractVo vo:bcVoList){
				Long buyOrSellContractId = vo.getBuyOrSellContractId();
				if("B".equals(contractType)){
					buyerContractId.append(vo.getId()).append("|");
					if(buyOrSellContractId!=null)
						sellContractId.append(vo.getBuyOrSellContractId()).append("|");
				}else{
					sellContractId.append(vo.getId()).append("|");
					if(buyOrSellContractId!=null)
						buyerContractId.append(vo.getBuyOrSellContractId()).append("|");
				}
			}
		}
		if(buyerContractId.length()>0){
			rela.setBuyContractId(buyerContractId.substring(0,buyerContractId.length()-1));
		}else{
			rela.setBuyContractId(null);
		}
		if(sellContractId.length()>0){
			rela.setSellContractId(sellContractId.substring(0,sellContractId.length()-1));
		}else{
			rela.setSellContractId(null);
		}
		return rela;
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		BasContractRela entity = this.getBaseDao().findOne(vo.getBizId());
		if(BasConstants.APPROVE_STATUS_D.equals(entity.getStatus())){
			entity.setStatus(BasConstants.APPROVE_STATUS_N);
		}
		this.save(entity);
	}
	
	@Override
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException{
		BasContractRela rela = basContractRelaDao.findOne(approve.getBizId());
		String relaBuyerId = rela.getBuyContractId();
		String relaSellId = rela.getSellContractId();
		if(rela.getExposureFlg()&&StringUtils.isNotBlank(relaBuyerId)&&StringUtils.isNotBlank(relaSellId)){
			List<BasContract> bcList = contractService.findByContractRelaId(rela.getId());
			String[] relaBuyerIds = relaBuyerId.split("\\|");
			String[] relaSellIds = relaSellId.split("\\|");
			for(int i=0;i<bcList.size();i++){
				BasContract bc = bcList.get(i);
				BasContract compareBc = null;
				if (BasConstants.CONTRACTTYPE_BUY.equals(bc.getContractType())) {
					//获取卖的Id
					Long sellId = Long.parseLong(relaSellIds[i]);
					compareBc = contractService.getEntity(sellId);
				}else{
					//获取买的ID
					Long buyerId = Long.parseLong(relaBuyerIds[i]);
					compareBc = contractService.getEntity(buyerId);
				}
				BigDecimal remainNumber = compareBc.getRemainNumber();
				BigDecimal dealNumber = bc.getDealNumber();
				compareBc.setRemainNumber(remainNumber.add(dealNumber));
				contractService.save(compareBc);
			}
		}
	}
	
}

