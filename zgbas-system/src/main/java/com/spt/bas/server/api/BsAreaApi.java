package com.spt.bas.server.api;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsArea;
import com.spt.bas.client.vo.CompanyAreaVo;
import com.spt.bas.server.service.IBsAreaService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/area")
public class BsAreaApi extends BaseApi<BsArea> {
	@Autowired
	private IBsAreaService bsAreaService;
	
	@Override
	public IBaseService<BsArea> getService() {
		return bsAreaService;
	}
	
	@PostMapping("findTopLevel")
	List<BsArea> findTopLevel(){
		return bsAreaService.findTopLevel();
	}
	
	@PostMapping("findByParentId")
	List<BsArea> findByparentId(@RequestBody String pid){
		return bsAreaService.findByparentId(pid);
	}
	
	@PostMapping("findByCode")
	List<BsArea> findByCode(@RequestBody String code){
		return bsAreaService.findByCode(code);
	}
	
	@PostMapping("getAreaVo")
	public CompanyAreaVo getAreaVo(@RequestBody Long id){
		return bsAreaService.getAreaVo(id);
	}

	@PostMapping("getAllArea")
	List<BsArea> getAllArea(){
		return bsAreaService.getAllArea();
	}
}

