/**
 * 
 */
package com.spt.tools.core.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author wlddh
 *
 */
@Data
public class ErrorResp {

	private int errorId;
	private String message;
	private int status;
	private String path;
	private String error;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;

	public ErrorResp(int errorId, String message) {
		this.errorId = errorId;
		this.message = message;
	}

	public ErrorResp() {

	}

}