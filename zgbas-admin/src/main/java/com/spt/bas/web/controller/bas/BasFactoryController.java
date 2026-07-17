package com.spt.bas.web.controller.bas;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.client.remote.IBsFactoryClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.RenderUtil;

/**
 * 厂商管理
 * @author wanjie
 *
 */
@Controller
@RequestMapping("/bas/factory")
public class BasFactoryController extends SingleCrudControll<BsFactory, BaseVo>{

	@Autowired
	private IBsFactoryClient bsFactoryClient;
	
	@Override
	public BaseClient<BsFactory> getService() {
		return bsFactoryClient;
	}
	@Override
	protected void preInsert(BsFactory e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
	
	/**
	 * 厂商列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value="")
	public String findFactory(Model model){
		model.addAttribute("enableFlgs",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
		return "bas/factory";
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		try {
			BsFactory entity = getService().getEntity(id);
			entity.setEnableFlg(false);
			bsFactoryClient.save(entity);
			RenderUtil.renderSuccess("删除成功", response);
		} catch (Exception e) {
			RenderUtil.renderFailure("操作错误，请联系管理员", response);
		}
		return null;
	}
	
	@RequestMapping(value = "validateFactory")
	public void validateFactory(BsFactory factory,HttpServletRequest request, HttpServletResponse response){
		if(factory!=null){
			factory.setEnterpriseId(ShiroUtil.getEnterpriseId());
			Long count = this.bsFactoryClient.countFactory(factory);
			if(count>0l){
				RenderUtil.renderText("false", response);
				return;
			}
		}
		RenderUtil.renderText("true", response);
	}
	
	@RequestMapping("/save")
	public void saveFactory(@RequestParam("param") String param,HttpServletResponse response){
		try {
			List<BsFactory> list=JSON.parseArray(param, BsFactory.class);
			for(BsFactory entity:list){
				entity.setEnterpriseId(ShiroUtil.getEnterpriseId());
				bsFactoryClient.save(entity);
			}
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			e.printStackTrace();
			RenderUtil.renderFailure("fail", response);
		}
	}
}
