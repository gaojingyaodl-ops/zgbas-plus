package com.spt.bas.web.config;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.bas.web.shiro.ShiroUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Thymeleaf template helper beans for monitor pages (QTZ-06 + QTZ-08).
 *
 * <p>Provides three Spring beans that Thymeleaf templates resolve via
 * {@code ${@dict.getType(...)}} / {@code ${@permission.hasPermi(...)}} SpEL expressions,
 * plus a {@link at.pollux.thymeleaf.shiro.dialect.ShiroDialect} bean so
 * {@code shiro:hasPermission="..."} attributes are processed by the Thymeleaf engine.
 *
 * <p><b>Key decisions (per 02-CONTEXT D-03/D-04/D-05):</b>
 * <ul>
 *   <li><b>D-03:</b> {@code @dict} is a thin facade over auth-sdk {@link DictUtil} —
 *       not the project's server/client dict cache helpers. Templates use
 *       {@code dictLabel}/{@code dictValue} (RuoYi field names);
 *       {@link SysDictDataSdk} has both
 *       {@code getDictLabel()}/{@code getDictValue()} and {@code getDictCd()}/{@code getDictName()},
 *       while the project's legacy dict entity only has {@code dictCd}/{@code dictName}.</li>
 *   <li><b>D-04:</b> {@code @permission} is a separate bean delegating to
 *       {@link ShiroUtil#isPermitted(String)} — not spt-auth {@code PermissionService}
 *       (which depends on Spring Security's {@code SecurityUtils.getLoginUser()},
 *       incompatible with this Shiro-based project).</li>
 *   <li><b>D-05:</b> ShiroDialect bean registered manually —
 *       thymeleaf-extras-shiro 2.1.0 has no {@code spring.factories} auto-configuration,
 *       so the {@code @Bean} is required. Thymeleaf's {@code SpringTemplateEngine}
 *       auto-discovers all {@code IDialect} beans in the application context.</li>
 * </ul>
 *
 * <p>The auth-sdk dict cache is initialized at startup via
 * {@code DictUtil.init(appCode)} in {@code ApplicationStartup} (zgbas-system),
 * so {@code @dict} methods do not need to trigger initialization.
 *
 * @author zgbas-plus
 * @see com.spt.auth.sdk.cache.DictUtil
 * @see com.spt.bas.web.shiro.ShiroUtil
 * @see at.pollux.thymeleaf.shiro.dialect.ShiroDialect
 */
@Configuration
public class TemplateHelperConfig {

    /**
     * Register the thymeleaf-extras-shiro {@link at.pollux.thymeleaf.shiro.dialect.ShiroDialect}
     * so that {@code shiro:hasPermission}, {@code shiro:lacksPermission},
     * {@code shiro:hasRole}, etc. attributes in Thymeleaf templates are processed
     * instead of silently ignored.
     *
     * <p>thymeleaf-extras-shiro 2.1.0 has no {@code spring.factories} auto-configuration,
     * so this bean must be declared manually. Thymeleaf's {@code SpringTemplateEngine}
     * auto-discovers all {@code org.thymeleaf.dialect.IExpressionEnhancingDialect} /
     * {@code org.thymeleaf.dialect.IPostProcessorDialect} beans in the context.
     *
     * <p>Security: ShiroDialect delegates to {@code Subject.hasPermission()} —
     * the same Shiro authorization chain as {@link PermissionTemplateHelper#hasPermi(String)},
     * so no privilege bypass is introduced (T-02-03 mitigated).
     *
     * @return a new {@link at.pollux.thymeleaf.shiro.dialect.ShiroDialect} instance
     */
    @Bean
    public at.pollux.thymeleaf.shiro.dialect.ShiroDialect shiroDialect() {
        return new at.pollux.thymeleaf.shiro.dialect.ShiroDialect();
    }

    /**
     * Thymeleaf-facing dict helper bean (QTZ-06).
     *
     * <p>Registered as Spring component named {@code "dict"} so templates can call
     * {@code ${@dict.getType('sys_job_group')}} and {@code ${@dict.getLabel('sys_common_status','0')}}.
     *
     * <p>Delegates to auth-sdk {@link DictUtil#getListByCategory(String)} which returns
     * {@link SysDictDataSdk} objects with both RuoYi-style field names
     * ({@code getDictLabel()}/{@code getDictValue()}) and legacy field names
     * ({@code getDictCd()}/{@code getDictName()}). Templates use the RuoYi names.
     *
     * <p><b>Do NOT use the project's legacy dict cache helpers</b> — they return entities
     * with {@code dictCd}/{@code dictName} only, causing empty dropdowns and formatters
     * (RESEARCH Pitfall 1).
     */
    @Component("dict")
    public static class DictTemplateHelper {

        /**
         * Get all dict data entries for a given type.
         *
         * <p>Used in templates: {@code th:with="type=${@dict.getType('sys_job_status')}"}
         * followed by {@code th:each="dict : ${type}"} referencing
         * {@code ${dict.dictLabel}} and {@code ${dict.dictValue}}.
         *
         * @param dictType the dict category code (e.g. {@code "sys_job_group"})
         * @return the list of {@link SysDictDataSdk} entries, or {@code null} if the
         *         cache has no entry for the type
         */
        public List<SysDictDataSdk> getType(String dictType) {
            return DictUtil.getListByCategory(dictType);
        }

        /**
         * Get dict label by type and value.
         *
         * <p>Used in templates: {@code ${@dict.getLabel('sys_oper_type', operLog.businessType)}}
         * Returns the human-readable label for a given dict value within a type.
         *
         * <p>Null/empty inputs safely return an empty string — no NPE on template render
         * (RESEARCH Pitfall 5 defense).
         *
         * @param dictType  the dict category code
         * @param dictValue the dict value to match against {@link SysDictDataSdk#getDictValue()}
         * @return the matching {@link SysDictDataSdk#getDictLabel()}, or {@code ""} if
         *         no match found or either input is null
         */
        public String getLabel(String dictType, String dictValue) {
            if (dictType == null || dictValue == null) {
                return "";
            }
            List<SysDictDataSdk> datas = DictUtil.getListByCategory(dictType);
            if (datas != null) {
                for (SysDictDataSdk dict : datas) {
                    if (dictValue.equals(dict.getDictValue())) {
                        return dict.getDictLabel();
                    }
                }
            }
            return "";
        }
    }

    /**
     * Thymeleaf-facing permission helper bean (QTZ-06).
     *
     * <p>Registered as Spring component named {@code "permission"} so templates can call
     * {@code [[${@permission.hasPermi('monitor:job:edit')}]]} for inline CSS-class logic
     * controlling button visibility.
     *
     * <p>Delegates to {@link ShiroUtil#isPermitted(String)} which calls
     * {@code org.apache.shiro.SecurityUtils.getSubject().isPermitted(permCode)} —
     * the Shiro-native permission check via {@code ShiroDbRealm} authorization cache.
     *
     * <p><b>Do NOT use the spt-auth permission service bean</b> — it calls
     * Spring Security's login-user utility which does not exist in this Shiro-based
     * project, causing {@code NoClassDefFoundError} at template render time
     * (RESEARCH Pitfall 2).
     *
     * <p>Security: null/empty permission inputs return {@code false} (not {@code true}),
     * so a null/empty permission code never grants access (T-02-01 mitigated).
     */
    @Component("permission")
    public static class PermissionTemplateHelper {

        /**
         * Check if the current user has a permission.
         *
         * <p>Used in templates: {@code [[${@permission.hasPermi('monitor:job:edit')}]]}
         * Typically returns a boolean used to conditionally add a CSS class
         * (e.g. {@code "btn-success"} or {@code ""}) for button visibility.
         *
         * @param permission the permission code (e.g. {@code "monitor:job:list"});
         *                    null or empty returns {@code false}
         * @return {@code true} if the current Shiro subject has the permission,
         *         {@code false} for null/empty input or if not permitted
         */
        public boolean hasPermi(String permission) {
            if (permission == null || permission.isEmpty()) {
                return false;
            }
            return ShiroUtil.isPermitted(permission);
        }

        /**
         * Check if the current user lacks a permission (inverse of {@link #hasPermi}).
         *
         * @param permission the permission code; null or empty returns {@code true}
         *                    (lacking nothing = true)
         * @return {@code true} if the subject does NOT have the permission
         */
        public boolean lacksPermi(String permission) {
            return !hasPermi(permission);
        }
    }
}
