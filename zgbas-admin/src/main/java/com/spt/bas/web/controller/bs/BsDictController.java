/**
 * 
 */
package com.spt.bas.web.controller.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.BsDictType;
import com.spt.bas.client.remote.IBsDictClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * @author huangjian
 * 
 */
@Controller
@RequestMapping(value = "/bs/dict")
public class BsDictController extends PageController<BsDictType, BaseVo> {
	@Autowired
	private IBsDictClient dictService;

	@RequestMapping(value = "")
	public String index(Model model) {
		
		model.addAttribute("enableFlgs",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
		return "bs/dict";
	}

	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		BsDictType dictType = getEntity(id);
		model.addAttribute("dictType", dictType);
		return "bs/dict-detail";
	}

	@RequestMapping(value = "isTypeExists")
	public void isTypeExists(HttpServletRequest req, HttpServletResponse resp) {
		String newDictTypeCd = req.getParameter("dictTypeCd").trim();
		String oldDictTypeCd = req.getParameter("oldDictTypeCd").trim();

		if (dictService.existDictTypeCd(newDictTypeCd, oldDictTypeCd,ShiroUtil.getEnterpriseId())) {
			RenderUtil.renderText("false", resp);
		} else {
			RenderUtil.renderText("true", resp);
		}
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

	
	@RequestMapping(value = "listData/{id}")
	public void listData(@PathVariable("id") Long id, HttpServletResponse response) {
		if (id != null && id > 0) {
			BsDictType dictType = dictService.getEntity(id);
			JsonEasyUI.renderListJson(response, dictType.getDictDatas());
		} else {
			JsonEasyUI.renderListJson(response, new ArrayList<>(0));
		}
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@Valid @ModelAttribute("preload") BsDictType dictType, HttpServletRequest request,
			HttpServletResponse response) {
		String result = "success";
		dictType.setEnterpriseId(ShiroUtil.getEnterpriseId());
		dictType = dictService.save(dictType);
		saveDatas(request, dictType.getId());
		result = result + ":" + String.valueOf(dictType.getId());
		RenderUtil.renderText(result, response);
		return null;
	}

	@RequestMapping(value = "deleteData/{id}")
	public String deleteData(@PathVariable("id") Long id, HttpServletResponse response) {
		dictService.deleteData(id);
		RenderUtil.renderSuccess("保存成功", response);
		return null;
	}

	private void saveDatas(HttpServletRequest request, Long dictTypeId) {
		try {
			List<BsDictData> lstDeleted = JsonEasyUI.getDeletedRecords(BsDictData.class, request);
			List<BsDictData> lstInsert = JsonEasyUI.getInsertRecords(BsDictData.class, request);
			List<BsDictData> lstUpdated = JsonEasyUI.getUpdatedRecords(BsDictData.class, request);
			BatchSaveVo<BsDictData> batchSaveVo =new BatchSaveVo<>();
			batchSaveVo.setDeletedRecords(lstDeleted);
			batchSaveVo.setInsertedRecords(lstInsert);
			batchSaveVo.setUpdatedRecords(lstUpdated);
			dictService.saveDatas(batchSaveVo,dictTypeId);
		} catch (Exception e) {
			logger.error("dictData save error!", e);
		}
	}

	@Override
	public IBsDictClient getService() {
		return dictService;
	}

	/**
	 * 使用@ModelAttribute, 实现Struts2
	 * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("preload")
	public BsDictType getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return dictService.getEntity(id);
			else {
				BsDictType dictType = new BsDictType();
				dictType.setEnableFlg(true);
				dictType.setId(0l);
				return dictType;
			}
		}
		return null;
	}
}
