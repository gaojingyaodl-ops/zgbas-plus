/**
 * 
 */
package com.spt.bas.web.controller.bas;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasPay;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBasContractClient;
import com.spt.bas.client.remote.IBasPayClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.vo.BasPayTicketVo;
import com.spt.bas.client.vo.BsCompanySearchVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;

/**
 * 付款信息
 * 
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/bas/pay")
public class BasPayController extends PageController<BasPay, BaseVo> {

	@Autowired
	private IBasPayClient payClient;
	@Autowired
	private IBsCompanyClient companyClient;
	@Autowired
	private IBasContractClient contractClient;

	@Override
	public BaseClient<BasPay> getService() {
		return payClient;
	}

	@RequestMapping(value = "")
	public String index(Model model) {
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		BsCompanySearchVo queryVo =new BsCompanySearchVo();
		queryVo.setRows(1000);
		queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		queryVo.setUserId(ShiroUtil.getCurrentUserId());
		queryVo.setMode(BasConstants.COMPANY_SEARCH_MODE_MY);
		PageDown<BsCompany> pageCompany= companyClient.findPageCompnay(queryVo);
		model.addAttribute("companyJson", JsonUtil.obj2Json(pageCompany.getContent()));
		return "bas/pay";
	}

	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		BasPay entity = getEntity(id);
		model.addAttribute("entity", entity);
		return "bas/pay-detail";
	}
	
	//收票
	@RequestMapping(value = "detailTicket/{id}", method = RequestMethod.GET)
	public String detailTicket(@PathVariable("id") Long id, Model model) {
		BasPay entity = getEntity(id);
		model.addAttribute("entity", entity);
		return "bas/pay-detail-ticket";
	}
	

	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, Model model) {
		BasPay entity = getEntity(id);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("payTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PAYTYPE)));
		List<BsCompany> lstCompany = companyClient.findAll();
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		model.addAttribute("entity", entity);
		if (entity.getContractId()!=null && entity.getContractId()>0) {
			BasContract contract = contractClient.getEntity(entity.getContractId());
			model.addAttribute("contract", contract);
		}
		return "bas/pay-content";
	}

	@RequestMapping(value = "saveTicket", method = RequestMethod.POST)
	public void saveTicket(BasPayTicketVo vo, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			vo.setCreateUserId(ShiroUtil.getCurrentUserId());
			vo.setCreateUserName(ShiroUtil.getCurrentUserName());
			BasPay entity =	payClient.saveTicket(vo);
			RenderUtil.renderSuccess(JsonUtil.obj2Json(entity), response);
		} catch (Exception e) {
			logger.error("saveTicket:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
	
	
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public void save(@Valid @ModelAttribute("preload") BasPay entity, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			entity = getService().save(entity);			
			RenderUtil.renderSuccess(JsonUtil.obj2Json(entity), response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
		try {
			payClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	/**
	 * 使用@ModelAttribute, 实现Struts2
	 * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("preload")
	public BasPay getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				BasPay entity = new BasPay();
				entity.setId(0l);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setPayDate(new Date());
				return entity;
			}
		}
		return null;
	}
}
