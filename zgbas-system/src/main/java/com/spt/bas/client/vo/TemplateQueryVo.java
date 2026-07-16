package com.spt.bas.client.vo;

import java.util.List;

public class TemplateQueryVo {

	private String typeCd;
	private String templateCat="matchFormConfig";
	private String lang = "CH";//默认中文
	private List<String> dictCdList;//content字段对应要取出的数据字典
	private String dictCd;//content字段对应的数据字典，只取一个
	
	public String getTypeCd() {
		return typeCd;
	}
	public void setTypeCd(String typeCd) {
		this.typeCd = typeCd;
	}
	public String getTemplateCat() {
		return templateCat;
	}
	public void setTemplateCat(String templateCat) {
		this.templateCat = templateCat;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public List<String> getDictCdList() {
		return dictCdList;
	}
	public void setDictCdList(List<String> dictCdList) {
		this.dictCdList = dictCdList;
	}
	public String getDictCd() {
		return dictCd;
	}
	public void setDictCd(String dictCd) {
		this.dictCd = dictCd;
	}
	
}
