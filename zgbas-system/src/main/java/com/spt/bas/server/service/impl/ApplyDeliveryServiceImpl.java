package com.spt.bas.server.service.impl;

import cn.hutool.core.convert.Convert;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.IApplyDeliveryService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.ICtrProductFeeService;
import com.spt.bas.server.util.TemplateContentUtility;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.InvalidParamException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class ApplyDeliveryServiceImpl extends BaseService<ApplyDelivery> implements IApplyDeliveryService {
	@Autowired
	private ApplyDeliveryDao applyDeliveryDao;
	@Autowired
	private ApplyDeliveryOutDao applyDeliveryOutDao;
	@Autowired
	private ApplyDeliveryInDao applyDeliveryInDao;
	@Autowired
	private ApplyProductDetailDao applyProductDetailDao;
	@Autowired
	private IBsKeySequenceService bsKeySequenceService;
	@Autowired
	private IPmApproveService approveService;
	@Autowired
	private BsWarehouseDao bsWarehouseDao;
	@Autowired
	private BsWarehouseAddrDao bsWarehouseAddrDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private BsCompanyDao bsCompanyDao;
	@Autowired
	private StockDetailDao stockDetailDao;
	@Autowired
	private ICtrProductFeeService ctrProductFeeService;
	@Autowired
	private IApplyProductDetailService applyProductDetailServiceImpl;
	@Override
	public BaseDao<ApplyDelivery> getBaseDao() {
		return applyDeliveryDao;
	}

	@Override
	public Class<ApplyDelivery> getEntityClazz() {
		return ApplyDelivery.class;
	}

	/** 提货单作废 */
	@Override
	@ServerTransactional
	public void doCancel(ApplyDeliveryCancelVo cancelVo) throws ApplicationException {
		if (cancelVo.getId() == null || cancelVo.getId() == 0) {
			throw new InvalidParamException("id");
		}
		ApplyDelivery entity = getEntity(cancelVo.getId());
		entity.setOperation(BasConstants.PRIN_CANCEL);
		applyDeliveryDao.save(entity);
		PmApproveWithdrawVo vo = new PmApproveWithdrawVo();
		vo.setApproveId(entity.getApproveId());
		vo.setUserId(cancelVo.getUserId());
		vo.setUserName(cancelVo.getUserName());
		approveService.doWithdraw(vo);
	}

	@Override
	@ServerTransactional
	public void startPrint(ApplyDeliveryVo vo) {
		vo.setOperation("已打印");
		applyDeliveryDao.startPrint(vo.getId(), vo.getPrintCount(), vo.getOperation());
	}

	@Override
	@ServerTransactional
	public void insertDelivery(PmApprove approve, ApplyDeliveryOut out,String applyType) throws ApplicationException {
		List<ApplyProductDetail> list = applyProductDetailDao.findApplyDetail(approve.getBizId(),applyType);
		for (ApplyProductDetail d : list) {
			saveDelivery(d, approve, out);
		}
	}
	
	@Override
	public void saveDelivery(ApplyProductDetail d,PmApprove approve, ApplyDeliveryOut out) throws ApplicationException {
		if (d.getCurNumber().compareTo(BigDecimal.ZERO) > 0) {
			ApplyDelivery applyDelivery = new ApplyDelivery();
			applyDelivery.setProductId(d.getCtrProductId());
			applyDelivery.setContractId(out.getContractId());
			applyDelivery.setCompanyId(out.getCompanyId());
			applyDelivery.setCompanyName(out.getCompanyName());
			applyDelivery.setApproveId(out.getApproveId());
			applyDelivery.setDriverName(out.getDriverName());
			applyDelivery.setDriverCardNo(out.getDriverCardNo());
			applyDelivery.setDeliveryAddr(out.getDeliveryAddr());
			applyDelivery.setDriverPhone(out.getDriverPhone());
			applyDelivery.setPlateNumber(out.getPlateNumber());
			
			applyDelivery.setContactName(out.getContactName());
			applyDelivery.setContactAddr(out.getContactAddr());
			applyDelivery.setContactPhone(out.getContactPhone());
			
			applyDelivery.setWarehouseBatchNo(out.getWarehouseBatchNo());
			applyDelivery.setWarehousePosition(out.getWarehousePosition());
			applyDelivery.setRemark(out.getRemark());
			//applyDelivery.setWarehouseNo(out.getWarehouseNo());
			applyDelivery.setWarehouseNo(out.getApplyNo());
			
			//柜数
			applyDelivery.setCountersNumber(out.getCountersNumber());
			//入库单号
			StockDetail detail = stockDetailDao.findOne(d.getStockDetailId());
			if (detail.getDeliveryInApplyId() != null && detail.getDeliveryInApplyId() > 0) {
				ApplyDeliveryIn deliveryIn = applyDeliveryInDao.findOne(detail.getDeliveryInApplyId());
				//applyDelivery.setWarehouseInNo(deliveryIn.getWarehouseNo());
				applyDelivery.setWarehouseInNo(deliveryIn.getBillNoPre());
			}
			
			if (out.getCompanyId() != null) {
				BsCompany company = bsCompanyDao.findOne(out.getCompanyId());
				if (company != null) {
					applyDelivery.setCompanyPhone(company.getCompanyPhone());
				}
			}
			applyDelivery.setPrintCount(0);
			applyDelivery.setStockDetailId(d.getStockDetailId());
			applyDelivery.setOperation(BasConstants.PRINTSTATUS_NONE);
			applyDelivery.setEnterpriseId(d.getEnterpriseId());
			applyDelivery.setDeliveryOutApplyId(out.getId());
			applyDelivery.setId(null);
			applyDelivery.setWarehouseId(d.getWarehouseId());
			applyDelivery.setWarehouse(d.getWarehouseName());
			applyDelivery.setFactoryName(d.getFactoryName());
			applyDelivery.setBrandNumber(d.getBrandNumber());
			applyDelivery.setProductCd(d.getProductCd());
			applyDelivery.setProductName(d.getProductName());
			applyDelivery.setDealNumber(d.getCurNumber());
			applyDelivery.setOperatorName(approve.getCreateUserName());
			String blankStr = "";
			if (StringUtils.isBlank(applyDelivery.getWarehouseBatchNo())) {
				applyDelivery.setWarehouseBatchNo(blankStr);
			}
			if (StringUtils.isBlank(applyDelivery.getWarehousePosition())) {
				applyDelivery.setWarehousePosition(blankStr);
			}
			// 自动生成提货单编号
			String applyNo = bsKeySequenceService.getNextKey(BasConstants.DEAL_APPLYNO, applyDelivery.getEnterpriseId());
			applyDelivery.setApplyNo(applyNo);

			// 新增交货单
			applyDelivery = this.save(applyDelivery);

			// 生成商品费用记录
			ctrProductFeeService.saveCtrProductFee(applyDelivery);
		}
	}

	@Override
	@ServerTransactional
	public void insertDeliveryByMatch(PmApprove approve, CtrContract ctr, List<ApplyProductDetail> list)
			throws ApplicationException {
		ApplyDelivery applyDelivery = new ApplyDelivery();
		applyDelivery.setPrintCount(0);
//		applyDelivery.setApproveNo(approve.getApproveNo());
//		applyDelivery.setOperation("未打印");
		applyDelivery.setEnterpriseId(approve.getEnterpriseId());
		applyDelivery.setDeliveryOutApplyId(approve.getId());

		applyDelivery.setContractId(ctr.getId());
//		applyDelivery.setDeliveryDate(ctr.getDeliveryDateTo());
//		applyDelivery.setBusinessNo(ctr.getBusinessNo());

//		applyDelivery.setContractNo(ctr.getContractNo());
		applyDelivery.setApproveId(approve.getId());
		// 配送方式
//		applyDelivery.setDeliveryType(ctr.getDeliveryType());
		// 交货方式
//		applyDelivery.setDeliveryMode(ctr.getDeliveryMode());

//		applyDelivery.setDeliveryPhone(ctr.getDeliveryPhone());
		// 已付金额
//		applyDelivery.setPayAmount(ctr.getDealedAmount());

		// 采购商名称
		applyDelivery.setCompanyName(ctr.getCompanyName());
		applyDelivery.setContactName(ctr.getContactName());
		applyDelivery.setContactPhone(ctr.getContactPhone());
		applyDelivery.setContactAddr(ctr.getContactAddr());
		String blankStr = "";
		if (StringUtils.isBlank(applyDelivery.getWarehouseBatchNo())) {
			applyDelivery.setWarehouseBatchNo(blankStr);
		}
		if (StringUtils.isBlank(applyDelivery.getWarehousePosition())) {
			applyDelivery.setWarehousePosition(blankStr);
		}

		// 新增交货单
		applyDelivery = this.save(applyDelivery);

//		for (ApplyProductDetail d : list) {
//			ApplyProductDetail ad = new ApplyProductDetail();
//			BeanUtils.copyProperties(d, ad);
//			ad.setApplyType(BasConstants.APPLY_TYPE_D);
//			ad.setApplyId(applyDelivery.getId());
//			ad.setCurNumber(d.getDealNumber());
//			ad.setId(null);
//			applyProductDetailDao.save(ad);
//		}
	}
	
	/** 提货单 */
	@Override
	public ApplyDelivery getApplyDeliveryEntity(ApplyDeliveryApplyIdVo vo) {
		ApplyDelivery entity = applyDeliveryDao.getApplyDeliveryEntity(vo.getId());
		Long stockDetailId = entity.getStockDetailId();
		ApplyDeliveryOut deliveryOut = applyDeliveryOutDao.findEntity(entity.getApproveId());
		List<BsWarehouse> warehouseList = bsWarehouseDao.findByWarehouseNameAndEnterpriseId(entity.getWarehouse(),
				entity.getEnterpriseId());
		if(deliveryOut!=null){
			vo.setWarehouseNo(deliveryOut.getWarehouseNo());
			vo.setWarehousePhone(deliveryOut.getWarehousePhone());
		}
		if(warehouseList!=null && warehouseList.size()>0){
			BsWarehouse bw = warehouseList.get(0);
			List<BsWarehouseAddr> list = bsWarehouseAddrDao.queryDefaultFlg(bw.getId(), true);
			if (list.size()>0) {
				vo.setWarehouseAddr(list.get(0).getWarehouseAddr());
			}
		}
		//提货单仓库地址
		if(null != entity.getDeliveryOutApplyId()){
			List<ApplyProductDetail> productDetails = applyProductDetailServiceImpl
					.findApplyDetail(entity.getDeliveryOutApplyId(), BasConstants.APPLY_TYPE_O);
			if (productDetails.size()>0) {
				vo.setWarehouseAddr(productDetails.get(0).getWarehouseAddr());
			}
		}
		BsTemplateConfig template = null;
		template = TemplateContentUtility.getTemplate("matchLading", "FMC_Bill_LADING", "CH", entity.getEnterpriseId());
		if (template != null && template.getContent() != null) {
			BeanUtils.copyProperties(entity, vo);
			// 数量转为大写
			BigDecimal dealNumber = entity.getDealNumber();
			String curNumberCn = RmbUtil.number2Chinese(dealNumber);
			vo.setCurNumberCn(curNumberCn);
			//我方抬头
			CtrContract contract = ctrContractDao.findOne(entity.getContractId());
			vo.setOurCompanyName(contract.getOurCompanyName());
			String blankStr = "";
			if (StringUtils.isBlank(vo.getWarehouseNo())) {
				vo.setWarehouseNo(blankStr);
			}
			if (StringUtils.isBlank(vo.getWarehouseBatchNo())) {
				vo.setWarehouseBatchNo(blankStr);
			}
			if (StringUtils.isBlank(vo.getWarehousePosition())) {
				vo.setWarehousePosition(blankStr);
			}
			if (stockDetailId != null) {
				StockDetail stockDetail = stockDetailDao.findOne(stockDetailId);
				if (stockDetail != null) {
					Long deliveryInApplyId = stockDetail.getDeliveryInApplyId();
					List<ApplyProductDetail> productDetails = applyProductDetailServiceImpl
							.findApplyDetail(deliveryInApplyId, BasConstants.APPLY_TYPE_I);
					if (productDetails != null && productDetails.size() > 0) {
						vo.setWarehouse(productDetails.get(0).getWarehouseName());
					}
				}
			}
			try {
				// 日期转字符串
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
				vo.setStrDate(sdf.format(new Date()));
				vo.setContent(contentMerge(template.getContent(), vo));
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			return vo;
		}
		return null;
	}
	
	/** 转货权单 */
	@Override
	public ApplyDelivery getApplyDeliveryInvoiceEntity(ApplyDeliveryApplyIdVo vo) {
		ApplyDelivery entity = applyDeliveryDao.getApplyDeliveryInvoiceEntity(vo.getId());
		Long stockDetailId = entity.getStockDetailId();
		ApplyDeliveryOut deliveryOut = applyDeliveryOutDao.findEntity(entity.getApproveId());
		List<BsWarehouse> warehouseList = bsWarehouseDao.findByWarehouseNameAndEnterpriseId(entity.getWarehouse(),
				entity.getEnterpriseId());
		if(deliveryOut!=null){
			vo.setWarehouseNo(deliveryOut.getWarehouseNo());
			vo.setWarehousePhone(deliveryOut.getWarehousePhone());
		}
		if(warehouseList!=null&&warehouseList.size()>0){
			BsWarehouse bw = warehouseList.get(0);
			List<BsWarehouseAddr> list = bsWarehouseAddrDao.queryDefaultFlg(bw.getId(), true);
			if (list.size()>0) {
				vo.setWarehouseAddr(list.get(0).getWarehouseAddr());
			}
		}
		if(null != entity.getDeliveryOutApplyId()){
			List<ApplyProductDetail> productDetails = applyProductDetailServiceImpl
					.findApplyDetail(entity.getDeliveryOutApplyId(), BasConstants.APPLY_TYPE_O);
			if (productDetails.size()>0) {
				vo.setWarehouseAddr(productDetails.get(0).getWarehouseAddr());
			}
		}
		BsTemplateConfig template = null;
		template = TemplateContentUtility.getTemplate("matchInvoice", "FMC_Bill_INVOICE", "CH",
				entity.getEnterpriseId());
		if (template != null && template.getContent() != null) {
			BeanUtils.copyProperties(entity, vo);
			// 数量转为大写
			BigDecimal dealNumber = entity.getDealNumber();
			String curNumberCn = RmbUtil.number2Chinese(dealNumber);
			vo.setCurNumberCn(curNumberCn);
			//我方抬头
			CtrContract contract = ctrContractDao.findOne(entity.getContractId());
			vo.setOurCompanyName(contract.getOurCompanyName());
			if (stockDetailId != null) {
				StockDetail stockDetail = stockDetailDao.findOne(stockDetailId);
				if (stockDetail != null) {
					Long deliveryInApplyId = stockDetail.getDeliveryInApplyId();
					List<ApplyProductDetail> productDetails = applyProductDetailServiceImpl
							.findApplyDetail(deliveryInApplyId, BasConstants.APPLY_TYPE_I);
					if (productDetails != null && productDetails.size() > 0) {
						vo.setWarehouse(productDetails.get(0).getWarehouseName());
					}
				}
			}
			try {
				// 日期转字符串
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
				vo.setStrDate(sdf.format(new Date()));
				entity.setContent(contentMerge(template.getContent(), vo));
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			return entity;
		}
		return null;
	}

	// 将合同内容填充至模板
	private String contentMerge(String content, ApplyDelivery entity) throws ApplicationException {
		Configuration cfg = new Configuration();
		StringWriter sw = new StringWriter();
		try {
			Template t = new freemarker.template.Template("", new StringReader(content), cfg);
			t.process(entity, sw);
			content = sw.toString();
		} catch (Exception e) {
			throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
		}
		return content;
	}

	@Override
	public List<ApplyDelivery> findByContractId(Long id) {
		return applyDeliveryDao.findByContractId(id);
	}

	@Override
	public Page<DeliveryDetailVo> findPageDetail(PageSearchVo searchVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		PageRequest pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
		Specification<ApplyDelivery> spe = WebUtil.buildSpecification(searchVo.getSearchParams());
		Page<ApplyDelivery> page = getBaseDao().findAll(spe, pageable);
		List<DeliveryDetailVo> voList = new ArrayList<>();
		BigDecimal totalNumber = BigDecimal.ZERO;
		for (ApplyDelivery applyDelivery : page.getContent()) {
			DeliveryDetailVo vo = new DeliveryDetailVo();
			BeanUtils.copyProperties(applyDelivery, vo);
			CtrContract ctrContract = ctrContractDao.findOne(vo.getContractId());
			totalNumber = ctrContract.getTotalNumber();
			vo.setTotalNumber(totalNumber);
			vo.setContractStatus(ctrContract.getContractStatus());
			voList.add(vo);
		}
		PageRequest pageRequest_new = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<DeliveryDetailVo> pageVo = new PageImpl<>(voList, pageRequest_new, page.getTotalElements());
		return pageVo;
	}
	
	/** 送柜单 */
	@Override
	public ApplyDelivery getApplyDeliverySendSingleEntity(ApplyDeliveryApplyIdVo vo) {
		ApplyDelivery entity = applyDeliveryDao.getApplyDeliveryEntity(vo.getId());
		ApplyDeliveryOut deliveryOut = applyDeliveryOutDao.findEntity(entity.getApproveId());
		List<BsWarehouse> warehouseList = bsWarehouseDao.findByWarehouseNameAndEnterpriseId(entity.getWarehouse(),
				entity.getEnterpriseId());
		if(deliveryOut!=null){
			vo.setWarehouseNo(deliveryOut.getWarehouseNo());
			vo.setWarehousePhone(deliveryOut.getWarehousePhone());
		}
		if(warehouseList!=null&&warehouseList.size()>0){
			BsWarehouse bw = warehouseList.get(0);
			List<BsWarehouseAddr> list = bsWarehouseAddrDao.queryDefaultFlg(bw.getId(), true);
			if (list.size()>0) {
				vo.setWarehouseAddr(list.get(0).getWarehouseAddr());
			}
		}
		//送柜地址
		vo.setDeliveryAddr(entity.getDeliveryAddr());
		BsTemplateConfig template = null;
		template = TemplateContentUtility.getTemplate("matchSendSingle ", "FMC_SEND_INVOICE", "CH",
				entity.getEnterpriseId());
		if (template != null && template.getContent() != null) {
			BeanUtils.copyProperties(entity, vo);
			// 数量转为大写
			BigDecimal dealNumber = entity.getDealNumber();
			String curNumberCn = RmbUtil.number2Chinese(dealNumber);
			vo.setCurNumberCn(curNumberCn);
			//我方抬头
			CtrContract contract = ctrContractDao.findOne(entity.getContractId());
			vo.setOurCompanyName(contract.getOurCompanyName());
			try {
				// 日期转字符串
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
				vo.setStrDate(sdf.format(new Date()));
				entity.setContent(contentMerge(template.getContent(), vo));
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			return entity;
		}
		return null;
	}
	
	/**
	 * 中信配送单
	 */
	@Override
	public ApplyDelivery getApplyDeliveryDistributionEntity(ApplyDeliveryApplyIdVo vo) {
		ApplyDelivery entity = applyDeliveryDao.getApplyDeliveryInvoiceEntity(vo.getId());
		ApplyDeliveryOut deliveryOut = applyDeliveryOutDao.findEntity(entity.getApproveId());
		List<BsWarehouse> warehouseList = bsWarehouseDao.findByWarehouseNameAndEnterpriseId(entity.getWarehouse(),
				entity.getEnterpriseId());
		if(deliveryOut!=null){
			vo.setWarehouseNo(deliveryOut.getWarehouseNo());
			vo.setWarehousePhone(deliveryOut.getWarehousePhone());
		}
		if(warehouseList!=null&&warehouseList.size()>0){
			BsWarehouse bw = warehouseList.get(0);
			List<BsWarehouseAddr> list = bsWarehouseAddrDao.queryDefaultFlg(bw.getId(), true);
			if (list.size()>0) {
				vo.setWarehouseAddr(list.get(0).getWarehouseAddr());
			}
		}
		BsTemplateConfig template = null;
		template = TemplateContentUtility.getTemplate("matchDistribution", "FMC_Bill_DISTRIB", "CH",
				entity.getEnterpriseId());
		if (template != null && template.getContent() != null) {
			BeanUtils.copyProperties(entity, vo);
			// 数量转为大写
			BigDecimal dealNumber = entity.getDealNumber();
			String curNumberCn = RmbUtil.number2Chinese(dealNumber);
			vo.setCurNumberCn(curNumberCn);
			//我方抬头
			CtrContract contract = ctrContractDao.findOne(entity.getContractId());
			vo.setOurCompanyName(contract.getOurCompanyName());
			vo.setContractNo(contract.getContractNo());
			try {
				// 日期转字符串
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
				vo.setStrDate(sdf.format(new Date()));
				entity.setContent(contentMerge(template.getContent(), vo));
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			return entity;
		}
		return null;
	}

	@Override
	@ServerTransactional
	public void saveDetail(ApplyDeliveryReportVo delivery) throws ApplicationException {
		Long applyDeliveryId = delivery.getApplyDeliveryId();
		ApplyDelivery entity = this.getEntity(applyDeliveryId);
		if (entity != null) {
//			if(StringUtils.isNotEmpty(delivery.getCountersNumber())){
//				Optional<ApplyDeliveryOut> applyDeliveryOut = applyDeliveryOutDao.findById(entity.getDeliveryOutApplyId());
//				applyDeliveryOut.get().setCountersNumber(delivery.getCountersNumber());
//			}
			entity.setContactPhone(delivery.getCompanyPhone());
			entity.setDriverName(delivery.getDriverName());
			entity.setDriverPhone(delivery.getDriverPhone());
			entity.setDriverCardNo(delivery.getDriverCardNo());
			entity.setPlateNumber(delivery.getPlateNumber());
			entity.setWarehousePosition(delivery.getWarehousePosition());
			entity.setWarehouseBatchNo(delivery.getWarehouseBatchNo());
			entity.setCountersNumber(delivery.getCountersNumber());
			entity.setDeliveryAddr(delivery.getDeliveryAddr());
			entity.setContactName(delivery.getContractName());
			entity.setRemark(delivery.getRemark());
			//entity.setId(applyDeliveryId);
			this.save(entity);
		}
		
	}
	
}
