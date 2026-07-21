# 02-01 SUMMARY — TemplateHelperConfig + thymeleaf-extras-shiro 依赖

**Phase:** 02-quartz-log-ui-and-thymeleaf-support
**Plan:** 02-01
**Status:** complete
**Commits:** 2

## What Was Built

为 Thymeleaf 模板渲染补齐三个辅助能力（QTZ-06 + QTZ-08）：

1. **TemplateHelperConfig.java** — 新建 `@Configuration` 类，含三个 bean：
   - `@Component("dict")` DictTemplateHelper：委派 auth-sdk `DictUtil.getListByCategory()`，提供 `getType()`/`getLabel()` 方法，返回 `SysDictDataSdk`（dictLabel/dictValue）
   - `@Component("permission")` PermissionTemplateHelper：委派 `ShiroUtil.isPermitted()`，提供 `hasPermi()`/`lacksPermi()` 方法
   - `@Bean ShiroDialect`：注册 `at.pollux.thymeleaf.shiro.dialect.ShiroDialect` 让 `shiro:hasPermission` 属性生效

2. **zgbas-admin/pom.xml** — 新增 `thymeleaf-extras-shiro:2.1.0` 依赖

## Key Decisions Honored

- D-03: @dict 委派 auth-sdk DictUtil（非 BsDictUtil），使用 SysDictDataSdk.dictLabel/dictValue
- D-04: @permission 独立 bean，委派 ShiroUtil（非 spt-auth PermissionService）
- D-05: 双轨支持 — @permission.hasPermi() + ShiroDialect 同时生效
- D-06: 不改现有模板文件

## Key Files

| File | Action | Lines |
|------|--------|-------|
| `zgbas-admin/src/main/java/com/spt/bas/web/config/TemplateHelperConfig.java` | created | 187 |
| `zgbas-admin/pom.xml` | modified | +5 (1 dep block) |

## Verification

- [x] `@Component("dict")` exists + delegates to auth-sdk DictUtil (no BsDictUtil)
- [x] `@Component("permission")` exists + delegates to ShiroUtil.isPermitted() (no Spring Security API)
- [x] `@Bean ShiroDialect` exists with `new ShiroDialect()`
- [x] thymeleaf-extras-shiro:2.1.0 in admin pom.xml
- [x] parent pom.xml NOT modified
- [x] `mvn compile -pl zgbas-admin -am` exit 0

## Commits

1. `e7a16db` feat(02-01): add thymeleaf-extras-shiro:2.1.0 dependency to admin pom
2. `8ea2009` feat(02-01): create TemplateHelperConfig with @dict/@permission/ShiroDialect beans

## Self-Check: PASSED