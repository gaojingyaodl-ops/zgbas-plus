package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasContractText;
import com.spt.bas.client.entity.BsTemplateConfig;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.TemplateMergeVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BasContractTextDao;
import com.spt.bas.server.service.IBasContractTextService;
import com.spt.bas.server.util.TemplateContentUtility;
import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

@Component
@Transactional(readOnly = true)
public class BasContractTextServiceImpl extends BaseService<BasContractText> implements IBasContractTextService {
	@Autowired
	private BasContractTextDao basContractTextDao;
	
	@Override
	public BaseDao<BasContractText> getBaseDao() {
		return basContractTextDao;
	}
	
	@Override
	public Class<BasContractText> getEntityClazz() {
		return BasContractText.class;
	}

	@Override
	@ServerTransactional
	public BasContractText saveContract(BasContract entity) {
		BasContractText contractText = null;
		BsTemplateConfig template = null;
		try {
			if(entity.getContractType().equals("S")){
				template = TemplateContentUtility.getTemplate("matchContract","FMC_SALE_CONTRACT","CH",entity.getEnterpriseId());
			}else if(entity.getContractType().equals("B")){
				template = TemplateContentUtility.getTemplate("matchContract","FMC_BUY_CONTRACT","CH",entity.getEnterpriseId());
			}
			if (template != null) {
				BasContractText contract = new BasContractText();
				contract.setContractId(entity.getId());
				contract.setTemplateId(template.getId());
				// 获取合并电子合同模板的参数（金额转大写，日期转字符串）
				TemplateMergeVo vo = new TemplateMergeVo();
				try {
					PropertyUtils.copyProperties(vo, entity);
				} catch (Exception e) {
					logger.warn("copyProperties", e);
				}
				// 总金额转为大写
				String dealAmountCn = RmbUtil.number2Chinese(entity.getDealAmount());
				vo.setDealAmountCn(dealAmountCn);
				// 日期转字符
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				vo.setContractTimeStr(sdf.format(entity.getContractTime()));
				vo.setDeliveryDateFromStr(sdf.format(entity.getDeliveryDateFrom()));
				vo.setPayTimeStr(sdf.format(entity.getPayTime()));
				if(entity.getDeliveryDateTo()!=null){
					vo.setDeliveryDateToStr(sdf.format(entity.getDeliveryDateTo()));
				}else{
					vo.setDeliveryDateToStr(vo.getDeliveryDateFromStr());
				}
				
				if(template.getContent()!=null){
					contract.setContent(contentMerge(template.getContent(), vo));
					contractText = basContractTextDao.save(contract);
				}else{
					return null;
				}
			}
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contractText;
	}
	
	//将合同内容填充至模板
	private String contentMerge(String content,BasContract entity) throws ApplicationException {
		Configuration  cfg = new Configuration();  
		StringWriter sw = new StringWriter();
		try {
			Template t  = new freemarker.template.Template("", new StringReader(content), cfg);
			t.process(entity, sw);
			//TODO 暂时去掉
//			content = sw.toString();
		}  catch (Exception e) {
			throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
		}
		return content;
	}

	@Override
	public BasContractText getContractTextById(Long contractTextId) {
		// TODO Auto-generated method stub
		return basContractTextDao.findOne(contractTextId);
	}
	
}

