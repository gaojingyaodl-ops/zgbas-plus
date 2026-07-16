package com.spt.tools.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import com.spt.tools.core.config.EnableToolsCoreConfig;
import com.spt.tools.core.config.ToolsCoreConfig;
import com.spt.tools.http.config.EnableToolsHttpConfig;
import com.spt.tools.http.config.ToolsHttpConfig;
import com.spt.tools.shiro.config.EnableToolsShiroConfig;
import com.spt.tools.shiro.config.ToolsShiroConfig;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableToolsCoreConfig
@EnableToolsHttpConfig
@EnableToolsShiroConfig
@ConditionalOnClass({ ToolsCoreConfig.class, ToolsHttpConfig.class, ToolsShiroConfig.class })
public @interface EnableToolsWebConfig {

}
