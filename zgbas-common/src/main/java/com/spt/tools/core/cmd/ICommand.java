/**
 * 
 */
package com.spt.tools.core.cmd;

/**
 * @author huangjian
 *
 */
public interface ICommand {
	/**
	 * 执行命令行指令
	 * 
	 * @param commandline
	 * @return
	 */
	boolean executeCommand(String commandline) throws Exception;

}
