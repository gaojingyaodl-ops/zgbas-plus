package com.spt.tools.http.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoteBase {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	protected IRemoteService remoteService;
	
}
