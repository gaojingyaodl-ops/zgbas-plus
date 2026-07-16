/**
 * 
 */
package com.spt.tools.mybatis.vo;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.data.vo.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author wlddh
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public abstract class BaseEntity extends DataEntity {

	private static final long serialVersionUID = -8685407442324595444L;
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	@Getter(onMethod_= {@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")})
	@TableField(fill = FieldFill.INSERT)
	private Date createdDate;
	@Getter(onMethod_= {@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")})
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updatedDate;
}
