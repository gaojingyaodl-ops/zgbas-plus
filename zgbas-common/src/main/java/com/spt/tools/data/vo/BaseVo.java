package com.spt.tools.data.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 统一定义id的entity基类.
 * 
 * 基类统一定义id的属性名称、数据类型、列名映射及生成策略.
 * Oracle需要每个Entity独立定义id的SEQUCENCE时，不继承于本类而改为实现一个Idable的接口。
 * 
 * @author calvin
 */
// JPA 基类的标识
@Data
@Accessors(chain = true)
public abstract class BaseVo implements Serializable {
	private static final long serialVersionUID = -7228339089230494857L;
	protected Long id;
	@Getter(onMethod_= {@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")})
	private Date createdDate;
	@Getter(onMethod_= {@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")})
	private Date updatedDate;

}
