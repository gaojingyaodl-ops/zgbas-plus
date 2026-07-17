/**
 * 
 */
package com.spt.bas.web.controller.bas;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.JSONException;
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
import com.spt.bas.client.entity.BasDelivery;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBasDeliveryClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsTemplateConfigClient;
import com.spt.bas.client.vo.DictDataVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.TemplateQueryVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * 货物交割信息
 * 
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/bas/delivery")
public class BasDeliveryController extends PageController<BasDelivery, BaseVo> {

	@Autowired
	private IBasDeliveryClient deliveryClient;
	@Autowired
	private IBsCompanyClient companyClient;
	@Autowired
	private IBsTemplateConfigClient templateConfigClient;

	@Override
	public BaseClient<BasDelivery> getService() {
		return deliveryClient;
	}

	@RequestMapping(value = "")
	public String index(Model model) {
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		List<BsCompany> lstCompany = companyClient.findAll();
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		return "bas/delivery";
	}

	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		BasDelivery entity = getEntity(id);
		model.addAttribute("entity", entity);
		return "bas/delivery-detail";
	}

	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, Model model) {
		BasDelivery entity = getEntity(id);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		List<BsCompany> lstCompany = companyClient.findAll();
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		model.addAttribute("entity", entity);
		return "bas/delivery-content";
	}
	
	/** 审批模板内容  入库申请 */
	@RequestMapping(value = "contentIn/{id}", method = RequestMethod.GET)
	public String contentIn(@PathVariable("id") Long id, Model model) {
		BasDelivery entity = getEntity(id);
		entity.setDeliveryType("I");
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		List<BsCompany> lstCompany = companyClient.findAll();		
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		model.addAttribute("entity", entity);
		return "bas/delivery-content";
	}
	
	/** 审批模板内容   出库申请*/
	@RequestMapping(value = "contentOut/{id}", method = RequestMethod.GET)
	public String contentOut(@PathVariable("id") Long id, Model model) {
		BasDelivery entity = getEntity(id);
		entity.setDeliveryType("O");
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		List<BsCompany> lstCompany = companyClient.findAll();		
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		model.addAttribute("entity", entity);
		return "bas/delivery-content";
	}
	

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public void save(@Valid @ModelAttribute("preload") BasDelivery entity, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			//entity = getService().save(entity);
			RenderUtil.renderSuccess(JsonUtil.obj2Json(entity), response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
	
	@RequestMapping(value = "template/{typeCd}", method = RequestMethod.GET)
	public void getTemplate(@PathVariable("typeCd") String typeCd,HttpServletResponse response) throws JSONException{
		TemplateQueryVo queryVo = new TemplateQueryVo();
		queryVo.setTypeCd(typeCd);
		queryVo.setDictCd(BasConstants.TEMPLATE_CONTENT_WAREHOUSE);
		List<DictDataVo> list = this.templateConfigClient.getTemplateByDictCd(queryVo);
		JsonEasyUI.renderListJson(response, list);
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
		try {
			deliveryClient.updateFileId(vo);
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
	public BasDelivery getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				BasDelivery entity = new BasDelivery();
				entity.setId(0l);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}
		}
		return null;
	}
}
