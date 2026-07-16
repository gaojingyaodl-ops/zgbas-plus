/**
 * 
 */
package com.spt.tools.core.task;

/**
 * @author huangjian
 *
 */
public interface ITask extends Runnable {

	void run();

	String getTaskName();
}
