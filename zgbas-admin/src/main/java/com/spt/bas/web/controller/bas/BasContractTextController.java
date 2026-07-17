package com.spt.bas.web.controller.bas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spt.bas.client.remote.IBasContractTextClient;

@Controller
@RequestMapping(value = "/bas/contractText")
public class BasContractTextController {
	@Autowired
	private IBasContractTextClient contractTextClient;
	
	@RequestMapping(value = "getContractTextById/{id}")
	public String getContractTextById(@PathVariable("id") Long id,Model model){
		model.addAttribute("contractContent", contractTextClient.getContractTextById(id).getContent());
		return "bas/contractContent";
	}
}
