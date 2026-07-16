/**
 * 
 */
package com.spt.tools.shiro.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import com.spt.tools.shiro.util.ShiroConstants;

/**
 * @author huangjian
 * 
 */
public class MyPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {
	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws IOException {
		Subject subject = getSubject(request, response);
		String[] perms = (String[]) mappedValue;

		boolean isPermitted = false;
		if (subject.hasRole(ShiroConstants.ADMIN)) {
			isPermitted = true;
		} else if (perms != null && perms.length > 0) {
			if (perms.length == 1) {
				if (subject.isPermitted(perms[0])) {
					isPermitted = true;
				}
			} else {
				for (String perm : perms) {
					if (subject.isPermitted(perm)) {
						isPermitted = true;
						break;
					}
				}
			}
		}

		return isPermitted;
	}
}
