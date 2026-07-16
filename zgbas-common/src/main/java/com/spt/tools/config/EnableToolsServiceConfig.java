package com.spt.tools.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import com.spt.tools.aop.config.EnableToolsAopConfig;
import com.spt.tools.aop.config.ToolsAopConfig;
import com.spt.tools.core.config.EnableToolsCoreConfig;
import com.spt.tools.core.config.ToolsCoreConfig;
import com.spt.tools.http.config.EnableToolsHttpConfig;
import com.spt.tools.http.config.ToolsHttpConfig;
import com.spt.tools.jpa.config.EnableToolsJpaConfig;
import com.spt.tools.jpa.config.ToolsJpaConfig;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableToolsCoreConfig
@EnableToolsJpaConfig
@EnableToolsHttpConfig
@EnableToolsAopConfig
@ConditionalOnClass({ ToolsCoreConfig.class, ToolsJpaConfig.class, ToolsHttpConfig.class, ToolsAopConfig.class, })
public @interface EnableToolsServiceConfig {

}
