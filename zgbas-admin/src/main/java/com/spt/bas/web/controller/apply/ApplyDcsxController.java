package com.spt.bas.web.controller.apply;

import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



/**
 * 代采赊销
 */
@Controller
@RequestMapping(value = "/apply/dcsx")
public class ApplyDcsxController extends PageController<ApplyCtrDCSX, BaseVo>{
	@Autowired
	private IApplyCtrDcsxClinent applyCtrDcsxClinent;

	@Override
	public BaseClient<ApplyCtrDCSX> getService() {
		// TODO Auto-generated method stub
		return applyCtrDcsxClinent;
	}



}
