# Phase 5: 报表迁移 - Pattern Map

**Mapped:** 2026-07-17
**Files analyzed:** ~487 net-new across 6 waves (W0 wiring: ~265 reportClient inline + 4 config edits + 1 new class / W1-W4: 53 Mapper + 53 XML + 53 service iface + 53 service impl + 2 util / W5: 54 api / W6: stub-port upgrade + 4 test probes)
**Analogs found:** 8 / 8 file-groups (all mapped — Phase 5 is bulk verbatim copy + 3 config edits, so analogs are prior-phase copy precedents + Phase 4's identical `BasFeignPathConfig` template)

> **照搬保包名 (D-P2-07) 是本期元模式。** Phase 5 是 Phase 4 的「同型再执行」——同样的 verbatim bulk-copy + 逐层 compile 绿灯 + Wave 4 已实证的 path-prefix 范式。每个 analog 不是「风格参考」，而是「上一次同型操作的执行蓝图」。planner 应直接复用 task-shape、verify 命令、acceptance 数字。

> **Phase 4 已就位的 Wave 0 资产（planner 不要重做）：**
> - `spt.bas.report.url: http://localhost:8080` 在 `application-dev.yml:49-50` 已就位
> - `spt.bas.report.url: ${SPT_BAS_REPORT_URL}` 在 `application-prod.yml:35-36` 已就位
> - `@EnableFeignClients` 已在 `ZgbasApplication.java:122` 放宽扫 `com.spt.bas.report.client.remote`
> - `ReportClientConfig` bean 已在 Phase 4 04-05 component-scanned（lazy URL resolution）—— 但源码不在单体，本期 W0 内联后才能"真实"生效
>
> **本期 Wave 0 实际需要做的只有 5 件事：** 内联 265 reportClient java + 移除 `report-client:2.0.1-SNAPSHOT` pom dep + 放宽 `@MapperScan` + 追加 `type-aliases-package` + 新建 `ReportFeignPathConfig`（仿 `BasFeignPathConfig`）。

---

## File Classification

| New/Modified File Group | Role | Data Flow | Closest Analog | Match Quality |
|---|---|---|---|---|
| **W0** `zgbas-system/com/spt/bas/report/client/**` (~265 net-new: entity 83 + vo 119 + remote 54 + config 1 + constant 2 + payload 3 + utils 2 + 1 pkg-info) | model / feign-contract / config-bean | passive (data carrier) + request-response (Feign) | Phase 4 basClient inline (`com.spt.bas.client.*` in `zgbas-system` — `BasClientConfig.java` + `IBsCompanyOurClient.java`) | **exact** (同型 verbatim 内联保包名 D-P2-07，对齐 Phase 4 Wave 1) |
| **W0** `zgbas-system/com/spt/bas/client/config/ReportFeignPathConfig.java` (1 NEW) | config-bean (WebMvcConfigurer) | bean-wiring | `zgbas-system/.../config/BasFeignPathConfig.java` (Phase 4 Wave 4 path-prefix 范式) | **exact** (verbatim template — 仅改 prefix + basePackage) |
| **W0** `zgbas-framework/.../ZgbasMybatisConfig.java` (EDIT 1 line) | config-bean (@MapperScan) | bean-wiring | 同文件 Phase 2 现状（`basePackages = "com.spt.bas.system.dao"` 单串） | **exact** (改单串为数组) |
| **W0** `zgbas-admin/src/main/resources/application.yml` (EDIT 1 key) | config | bean-wiring | 同文件 Phase 2 现状（`type-aliases-package: com.spt.bas.client.entity`） | **exact** (追加 report 包) |
| **W0** `zgbas-system/pom.xml` (REMOVE dep block) | config | bean-wiring | Phase 4 04-05 已声明此 dep（types-only）—— 本期移除 | **exact** (W0 内联后此 jar dep 即过时) |
| **W1-W4** `zgbas-system/com/spt/bas/report/server/dao/**` (53 `Rpt*Mapper` + 1 pkg-info) | model (mybatis Mapper iface) | CRUD (mybatis) | Phase 2 SampleMapper iface（`@MyBatisDao` marker，`zgbas-system/.../system/dao/SampleMapper.java`） | **role-match** (同 `@MyBatisDao` 标注，仅 basePackages 不同) |
| **W1-W4** `zgbas-system/src/main/resources/mybatis/mappers/Rpt*Mapper.xml` (53) | persistence (mybatis XML) | CRUD (复杂 SQL) | `zgbas-system/.../mybatis/mappers/SampleMapper.xml` (Phase 2 sample) | **exact** (同位 drop，namespace = Mapper FQN，FQN resultType) |
| **W1-W4** `zgbas-system/com/spt/bas/report/server/service/**` (53 iface + 1 pkg-info) | service (iface) | CRUD + domain-logic | Phase 4 basServer service iface（`com.spt.bas.server.service.I*`） | **role-match** (报表 iface 不 extends BaseDataService，更简单 POJO shape) |
| **W1-W4** `zgbas-system/com/spt/bas/report/server/service/impl/**` (53 impl + 1 pkg-info) | service (impl) | CRUD + domain-logic | Phase 4 `02-05-PLAN` Task 1 + 04-PATTERNS Wave 2b（533 service impl bulk-copy + ERROR_COUNT gate） | **role-match** (同型 per-layer bulk copy) |
| **W1-W4** `zgbas-system/com/spt/bas/report/server/util/**` (2: MyBigDecimalUtils + ReportCalculateUtil) | utility | passive | Phase 4 basServer util 包（`com.spt.bas.server.util`，照搬保包名） | **role-match** (service impl 引用，运行依赖) |
| **W5** `zgbas-admin/com/spt/bas/report/server/api/**` (54 `@RestController` + 1 pkg-info) | controller (前端直连 edge) | request-response | Phase 4 basServer api（`zgbas-system/.../bas/server/api/*` 223 controllers bulk-copy + `BasFeignPathConfig` path-prefix）；源 `RptFundReceivableStatisticsApi.java`（类签名 shape） | **role-match** (同型 bulk-copy，但落 admin 非 system，path-prefix 落 ReportFeignPathConfig) |
| **W5** `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` (EDIT excludeFilters) | boot (ComponentScan) | bean-wiring | 同文件 Phase 4 Wave 3/4 excludeFilters 范式（`BasicErrorController.class` 排除） | **exact** (Rule 3 同型，追加 `com.spt.bas.report.server.config.BasicErrorController.class`) |
| **W6** BFF stub-port 升级 (`MyIndexController` + `BusinessOverviewController` + `WorkBenchCache` + 9 basServer service impl 字段) | controller + service (field stub) | request-response (un-degrade) | Phase 3 `03-02-PLAN` Task 2 stub-port 升级（`@Autowired(required=false)` → 真实 bean + null 守卫移除） | **exact** (D-P4-02 stub-port 反向操作：本期解除 report 契约降级) |
| **W6** `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` (4 NEW test methods) | test | request-response (HTTP proof + bean-resolution) | Phase 4 `feignSelfLoopbackWiring_probe`（同文件 `ZgbasApplicationTest.java:223-236`） | **exact** (沿用 TestRestTemplate + ApplicationContext bean 抽样风格) |

---

## Pattern Assignments

### W0 — 接线 wave（5 个原子操作，无 Mapper 迁移）

#### 0a. `zgbas-system/com/spt/bas/report/client/**` 内联 265 java（D-P5-02）

**Analog:** Phase 4 basClient 内联进 `zgbas-system/com/spt/bas/client/`（04-PATTERNS Wave 1，D-P2-07 照搬保包名）。`BasClientConfig`（产 `basServerConfig` bean）+ `IBsCompanyOurClient`（`@FeignClient extends BaseClient`）已就位。

**源 `ReportClientConfig.java:1-20` 全文（照搬目标原型，产 `reportServerConfig` bean）：**
```java
package com.spt.bas.report.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.tools.core.bean.LocalServerConfig;

@Configuration
public class ReportClientConfig {
    @DependsOn({"propertiesUtil"})
    @Bean(ReportConstant.SERVER_BEAN_NAME)            // bean name = "reportServerConfig"
    public LocalServerConfig localServerConfig() {
        LocalServerConfig conf = new LocalServerConfig();
        conf.setUrlKey(ReportConstant.SERVER_URL_KEY); // 读 yml: spt.bas.report.url
        return conf;
    }
}
```

**源 `ReportConstant.java:6-15` 关键常量（与 BasConstants 同构）：**
```java
public interface ReportConstant {
    String SERVER_NAME = "spt-bas-report";
    String SERVER_BEAN_NAME = "reportServerConfig";
    String SERVER_URL = "#{" + SERVER_BEAN_NAME + ".url}";  // SpEL: "#{reportServerConfig.url}"
    String SERVER_URL_KEY = "spt.bas.report.url";
    // ...
}
```

→ 53 报表 Feign 契约的 `url = ReportConstant.SERVER_URL` 解析到 `reportServerConfig` bean；该 bean 已在 Phase 4 04-05 component-scanned（但因源码不在单体，本期 W0 内联后才"真实"产出 bean 读取 yml `spt.bas.report.url`）。

**源 `IRptFundReceivableStatisticsClient.java:1-16` 全文（54 契约之一，对齐 Phase 4 `IBsCompanyOurClient` shape）：**
```java
package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ReportConstant.SERVER_NAME,
             path = ReportConstant.SERVER_NAME + "/rpt/fundReceivableStatistics",  // ⚠ "spt-bas-report/rpt/..." 前缀
             url = ReportConstant.SERVER_URL,                                         // "#{reportServerConfig.url}"
             configuration = FeignConfig.class)
public interface IRptFundReceivableStatisticsClient {
    @PostMapping("findPage")
    public PageDown<RptFundReceivableStatistics> findPage(@RequestBody RptFundReceivableStatisticsVo searchVo);
}
```

**Task-shape：**
```
read:    /Users/alan/WorkSpace/IDEA/zgbas/basReport/reportClient/src/main/java/com/spt/bas/report/client/
copy:    recursive → zgbas-system/src/main/java/com/spt/bas/report/client/  (保包名 com.spt.bas.report.client.*)
exclude: 无（去重在 XML resultType 层处理；Pitfall 5 已 de-risk — XML 全用 FQN 不用 alias）
pom:     zgbas-system/pom.xml:137-141 移除 <artifactId>report-client</artifactId> 整块
compile: mvn -pl zgbas-system -am compile；grep `^\[ERROR\]` locale 无关 → 0
accept:  REPORTCLIENT_COUNT = find zgbas-system/.../report/client -name '*.java' | wc -l ≥ 263 (含 pkg-info)
         FEIGNCLIENT_COUNT = grep -rl '@FeignClient' zgbas-system/.../report/client/remote | wc -l ≥ 53
         REPORT_JAR_ABSENT = grep -c 'report-client' zgbas-system/pom.xml == 0  ⬅ 必须 0
```

---

#### 0b. `zgbas-system/.../bas/client/config/ReportFeignPathConfig.java` (1 NEW，仿 BasFeignPathConfig)

**Analog (直接模板):** `zgbas-system/src/main/java/com/spt/bas/client/config/BasFeignPathConfig.java`（Phase 4 Wave 4 path-prefix 范式）。**本文件是本期最 critical 的 verbatim 模板** —— 仅改两处常量。

**`BasFeignPathConfig.java:44-60` 全文（照搬目标，**仅替换两行**）：**
```java
@Configuration
public class BasFeignPathConfig implements WebMvcConfigurer {

    private static final String API_PATH_PREFIX = "/spt-bas-server";   // ← 改为 "/spt-bas-report"

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PATH_PREFIX,
            HandlerTypePredicate.forBasePackage(
                "com.spt.bas.server.api",     // ← 删
                "com.spt.pm.api"));           // ← 删
                                          // ← 加 "com.spt.bas.report.server.api"
    }
}
```

**目标 `ReportFeignPathConfig.java`（完整文件，新建）：**
```java
package com.spt.bas.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * D-P5-03 path-prefix for report Feign self-loopback. Mirrors Phase 4 BasFeignPathConfig
 * verbatim. Adds "/spt-bas-report" prefix to all 54 @RequestMapping paths in
 * com.spt.bas.report.server.api so that:
 *   (1) Feign contract path = "spt-bas-report" + "/rpt/..." resolves to the ported api
 *       (D-P4-01 方案 A self-loopback closes for report contracts).
 *   (2) 14 BFF controllers in zgbas-admin that share literal paths with report api
 *       (e.g. /rpt/fundReceivableStatistics, /business/manager/workbench, /bs/company)
 *       no longer collide — report api is namespaced under /spt-bas-report/*, BFF stays at root.
 *
 * Scope: ONLY @RestController classes whose package starts with
 * com.spt.bas.report.server.api. basServer api (com.spt.bas.server.api) and PM api
 * (com.spt.pm.api) keep their own /spt-bas-server prefix via BasFeignPathConfig.
 *
 * Phase 3 AUTH-03 preserved — Shiro root filter chain untouched (prefix applies ONLY to
 * report api @RestController, not web @Controller).
 */
@Configuration
public class ReportFeignPathConfig implements WebMvcConfigurer {

    private static final String API_PATH_PREFIX = "/spt-bas-report";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PATH_PREFIX,
            HandlerTypePredicate.forBasePackage("com.spt.bas.report.server.api"));
    }
}
```

**关键作用：**
1. 解 D-P5-03 path diverge（`path = SERVER_NAME + "/rpt/..."` vs `@RequestMapping("/rpt/...")`）
2. 防 14 处 `AmbiguousMappingException`（BFF vs report api 字面冲突，已 grep 确认：`/bs/company`, `/budget/settlementTotal`, `/business/manager/workbench`, `/business/overview`, `/evaluate/total`, `/rpt/fundReceivableStatistics` 等 14+）

---

#### 0c. `zgbas-framework/.../ZgbasMybatisConfig.java` 放宽 @MapperScan（D-P5-07 part 2）

**Analog:** 同文件 Phase 2 现状。

**当前（`ZgbasMybatisConfig.java:23-25`）：**
```java
@Configuration
@MapperScan(basePackages = "com.spt.bas.system.dao", annotationClass = MyBatisDao.class)
public class ZgbasMybatisConfig {
    // ...
}
```

**目标（改单串为数组，**仅 1 行**）：**
```java
@Configuration
@MapperScan(
    basePackages = {
        "com.spt.bas.system.dao",            // Phase 2 (SampleMapper)
        "com.spt.bas.report.server.dao"      // ← NEW Phase 5 (53 Rpt*Mapper)
    },
    annotationClass = MyBatisDao.class
)
public class ZgbasMybatisConfig { ... }
```

**验证 53 Mapper 全部 `@MyBatisDao`**：`grep -l '@MyBatisDao' /源/reportServer/.../dao/Rpt*Mapper.java | wc -l == 53`。

---

#### 0d. `zgbas-admin/src/main/resources/application.yml` 追加 type-aliases-package

**Analog:** 同文件 Phase 2 现状（line 46-48）。

**当前：**
```yaml
mybatis-plus:
  mapper-locations: classpath:/mybatis/mappers/*Mapper.xml
  type-aliases-package: com.spt.bas.client.entity      # Pitfall 4 defense
```

**目标（追加 report 包，**仅 1 key**）：**
```yaml
mybatis-plus:
  mapper-locations: classpath:/mybatis/mappers/*Mapper.xml   # unchanged — 53 report XML 同位 drop 即被覆盖
  type-aliases-package: >-
    com.spt.bas.client.entity,
    com.spt.bas.report.client.entity,
    com.spt.bas.report.client.vo
```

**注意：** `mapper-locations` 不变 —— 53 report XML 落 `zgbas-system/src/main/resources/mybatis/mappers/` 即被现有配置覆盖（同 SampleMapper.xml 同位）。源 `myBatisConfig.xml` 丢弃（仅 `logImpl=STDOUT`/`lazyLoadingEnabled=false`，typeAliases 全注释 —— 依赖 Phase 2 mybatis-plus auto-config，D-P2-09）。

---

#### 0e. `zgbas-system/pom.xml` 移除 report-client jar dep（D-P5-02 收尾）

**Analog:** Phase 4 04-05 Rule 3 cascade pom 添加此 dep 的反向操作。

**当前（`zgbas-system/pom.xml:137-141`）：**
```xml
<dependency>
    <groupId>com.spt.bas</groupId>
    <artifactId>report-client</artifactId>
    <version>2.0.1-SNAPSHOT</version>
</dependency>
```

**目标：** 整块移除（W0 内联 265 java 后此 types-only dep 已无意义）。⚠ **必须与 0a 同 wave**，否则 classpath 上有两份 `com.spt.bas.report.client.*`（jar + 内联源码）→ 不可预测行为（Pitfall 7）。

---

### W1-W4 — 53 套报表按业务域分批（D-P5-04）

#### 1a. `zgbas-system/.../bas/report/server/dao/Rpt*Mapper.java` (53 Mapper iface + 1 pkg-info)

**Analog (类签名 shape):** Phase 2 SampleMapper（`@MyBatisDao` marker）—— 但报表 Mapper **不 extends BaseDao**（纯 mybatis，无 JPA）。源 `RptFundReceivableStatisticsMapper.java:1-12` 全文：
```java
package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptFundReceivableStatisticsMapper {
    List<RptFundReceivableStatistics> findPage(RptFundReceivableStatisticsVo searchVo);
}
```

**Task-shape：**
```
read:    /Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/java/com/spt/bas/report/server/dao/
copy:    → zgbas-system/src/main/java/com/spt/bas/report/server/dao/  (保包名)
compile: mvn -pl zgbas-system -am compile；grep `^\[ERROR\]` → 0  ⬅ 每 wave 绿灯
accept:  DAO_COUNT = find dao -name 'Rpt*Mapper.java' | wc -l == 53
         MYBATISDAO = grep -l '@MyBatisDao' dao/Rpt*Mapper.java | wc -l == 53
```

---

#### 1b. `zgbas-system/src/main/resources/mybatis/mappers/Rpt*Mapper.xml` (53 XML)

**Analog:** `zgbas-system/src/main/resources/mybatis/mappers/SampleMapper.xml`（Phase 2 sample —— 同位 drop，namespace = Mapper FQN）。

**源 `RptFundReceivableStatisticsMapper.xml:1-5` head（验证 namespace + FQN resultType）：**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.spt.bas.report.server.dao.RptFundReceivableStatisticsMapper">
    <select id="findPage" resultType="com.spt.bas.report.client.entity.RptFundReceivableStatistics">
        <!-- 复杂 SQL + 动态 <if> + <where>，含 ${} 拼接（不改写，行为等价优先）-->
    </select>
</mapper>
```

**Task-shape：**
```
read:    /Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/resources/mybatis/mappers/
copy:    → zgbas-system/src/main/resources/mybatis/mappers/  (与 SampleMapper.xml 同位)
compile: 无（XML 不参与 javac，仅启动期 binding）
accept:  XML_COUNT = find mappers/Rpt*Mapper.xml | wc -l == 53
         FQN_RESULTTYPE = grep -c 'resultType="com.spt.bas.report.client' Rpt*Mapper.xml ≥ 50  ⬅ Pitfall 5 验证
binding proof: 启动期无 BindingException（W6 allReportMappersResolve 断言）
```

**Pitfall 5 checkpoint（planner 可加）：** W1 第一批后 grep 全 XML `resultType|parameterType` 应全用 FQN（`com.spt.bas.report.client.*`），无 simple-name alias —— 防 `RptBaseCostVo` 同名歧义。

---

#### 1c. `zgbas-system/.../bas/report/server/service/IRpt*Service.java` (53 iface + 1 pkg-info)

**Analog:** Phase 4 basServer service iface（但报表 iface **不 extends BaseDataService**，更简单）。源文件机械照搬即可。

**Task-shape：** 同 1a，目录换为 `service/`。

---

#### 1d. `zgbas-system/.../bas/report/server/service/impl/Rpt*ServiceImpl.java` (53 impl + 1 pkg-info)

**Analog (bulk-copy task-shape):** Phase 4 `04-PATTERNS Wave 2b`（533 service impl bulk-copy + ERROR_COUNT gate）。**这是本期最直接的「同型再执行」** —— 仅层从 basServer 换到 reportServer。

**源 `RptFundReceivableStatisticsServiceImpl.java:1-28` 全文（标准模式：@Autowired Mapper + 内存 PageImpl 包装）：**
```java
package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.bas.report.server.dao.RptFundReceivableStatisticsMapper;
import com.spt.bas.report.server.service.IRptFundReceivableStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RptFundReceivableStatisticsServiceImpl implements IRptFundReceivableStatisticsService {
    @Autowired
    private RptFundReceivableStatisticsMapper fundReceivableStatisticsMapper;

    @Override
    public Page<RptFundReceivableStatistics> findPage(RptFundReceivableStatisticsVo searchVo) {
        List<RptFundReceivableStatistics> fundReceivableStatisticsList =
            fundReceivableStatisticsMapper.findPage(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptFundReceivableStatistics> pageVo =
            new PageImpl<>(fundReceivableStatisticsList, pageable, searchVo.getCount());  // ← 内存包装，非 SQL count
        return pageVo;
    }
}
```

**注意 `PageImpl` 包装：** `count` 从 Vo 取，非 SQL count 查询 —— PROJECT.md「PageHelper 是结果包装器」一致。分页**性能**不优化，分页**正确性** 1:1 等价（D-P5-06）。

**Cross-service dep gotcha（Pitfall 4）：** `RptCtrContractReportServiceImpl` 引用 `IRptSummaryRoiService` + `IRptUserRoiService` —— W3 必须先于 W4（Ctr* family 15 mapper）。

**Task-shape：** 同 Phase 4 Wave 2b 模板（见 Repeatable Task-Shape 段）。

---

#### 1e. `zgbas-system/.../bas/report/server/util/{MyBigDecimalUtils,ReportCalculateUtil}.java` (2 util)

**Analog:** Phase 4 basServer util 包（照搬保包名）。`ReportCalculateUtil` 被 `RptCtrContractReportServiceImpl` 等 import（运行依赖），必须与 service impl 同波或更早。

**Task-shape：** 同 1a，目录换为 `util/`，accept `UTIL_COUNT == 2`。

---

### W5 — 54 报表 api controllers 落 `zgbas-admin`（D-P5-01）

**Analog (类签名 shape):** 源 `RptFundReceivableStatisticsApi.java:1-23`（前端直连 `@RestController`，**不 extends BaseApi**，区别于 basServer api）。

**源 `RptFundReceivableStatisticsApi.java` 全文：**
```java
package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.bas.report.server.service.IRptFundReceivableStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rpt/fundReceivableStatistics")  // ⚠ 无 /spt-bas-report 前缀（ReportFeignPathConfig 加）
public class RptFundReceivableStatisticsApi {
    @Autowired
    private IRptFundReceivableStatisticsService fundReceivableStatisticsService;

    @PostMapping("findPage")
    public Page<RptFundReceivableStatistics> findRptContractSettlementPage(
            @RequestBody RptFundReceivableStatisticsVo searchVo) {
        return fundReceivableStatisticsService.findPage(searchVo);
    }
}
```

**Analog (bulk-copy task-shape + path-prefix):** Phase 4 basServer api（`zgbas-system/.../bas/server/api/*` 223 controllers bulk-copy + `BasFeignPathConfig` 加 `/spt-bas-server` 前缀 —— Wave 4 D-P4-01a 修正）。本期 report api **落 zgbas-admin**（D-P5-01：报表 api 是前端直连 edge controller，非 Feign 服务 API，落 admin 与前端模板同模块）。

**关键约束：**
1. **54 api 文件 1:1 照搬，零修改**（D-P2-07）。
2. **path 前缀由 `ReportFeignPathConfig`（W0 已建）注入** —— 不在 api 类上写前缀（`@RequestMapping("/rpt/...")` 保留原值，运行期由 `addPathPrefix` 自动加 `/spt-bas-report/`）。
3. **`BasicErrorController` 冲突**（Rule 3）：源 `reportServer/config/BasicErrorController.java` 与 Phase 4 已 exclude 的 3 个同 simple-name controller 冲突 —— `ZgbasApplication.java:85-91` excludeFilters 追加 `com.spt.bas.report.server.config.BasicErrorController.class`（同 Phase 4 Wave 3/4 范式）。

**Task-shape：**
```
read:    /Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/java/com/spt/bas/report/server/api/
copy:    → zgbas-admin/src/main/java/com/spt/bas/report/server/api/  (保包名 D-P5-01)
compile: mvn -pl zgbas-admin -am compile；grep `^\[ERROR\]` → 0
accept:  API_COUNT = find admin/.../report/server/api -name 'Rpt*Api.java' | wc -l == 54
         RESTCONTROLLER = grep -l '@RestController' api/Rpt*Api.java | wc -l == 54
         BASICERROR_EXCLUDED = grep -c 'com.spt.bas.report.server.config.BasicErrorController' \
             zgbas-admin/src/main/java/com/spt/ZgbasApplication.java == 1  ⬅ 必须 1
```

---

### W6 — BFF stub-port 升级（D-P5-03 解除 D-P4-02 stub 降级）+ 验证 proof

**Analog:** Phase 3 `03-02-PLAN` Task 2 stub-port 模式的**反向操作**（本期从 `@Autowired(required=false)` 升级回真实 bean）。

**升级目标（来自 RESEARCH §Pitfall 9）：** 9 个 Phase 4 basServer service impl + 多个 BFF controller 的 `IRpt*Client` 字段当前是 D-P4-02 lazy-degradation 态（自回环 404）。本期 path-prefix + api 落位后 → 字段升级回真实 `@Autowired`（移除 `required=false` + null 守卫）。

**9 svc refs 清单（来自 RESEARCH §Pattern 1）：**
- `CtrContractUpdateServiceImpl` / `BsCompanyApi` / `WeChatWorkServiceImpl` / `ApplyDeliveryOutServiceImpl` / `RptBaseCostServiceImpl` / `CtrContractSettlementServiceImpl` / `PushContractServiceImpl` / `BsCompanyServiceImpl` / `PerformanceCommissionUserServiceImpl`

**BFF refs：** `MyIndexController`（Phase 3 P5-deferred 缺口本期关闭）/ `BusinessOverviewController` / `BusinessManagerWorkbenchController` / `WorkBenchCache`（Phase 4 已迁 admin）

**Task-shape：**
```
1. grep -rln '@Autowired(required = false)' zgbas-admin zgbas-system | \
     xargs grep -l 'IRpt.*Client'   # 列出 report 契约降级字段
2. 对每个字段：移除 `(required = false)`（恢复默认 required=true）
3. 删除关联 null 守卫（参考 Phase 4 IndexController:120-126 反向操作）
4. compile: mvn -pl zgbas-admin -am compile；grep `^\[ERROR\]` → 0
accept: REPORT_STUB_COUNT = grep -rln 'IRpt.*Client' <upgraded files> | \
          xargs grep -c 'required = false' == 0   # 全部解除降级
```

---

### W6 验证 probe 扩展（4 NEW test methods）

**Analog:** Phase 4 `feignSelfLoopbackWiring_probe`（`ZgbasApplicationTest.java:223-236` —— bean 存在性 + path-prefix WebMvcConfigurer 验证）。

**Phase 4 probe 全文（直接模板）：**
```java
@Test
void feignSelfLoopbackWiring_probe() {
    // (1) BasFeignPathConfig registered as a bean — proves the @Configuration is picked up
    assertThat(context.getBean(
        com.spt.bas.client.config.BasFeignPathConfig.class)).isNotNull();

    // (2) IBsCompanyOurClient Feign proxy resolves — proves:
    //   - widened @EnableFeignClients includes com.spt.bas.client.remote;
    //   - basServerConfig LocalServerConfig bean registered;
    //   - SpEL "#{basServerConfig.url}" resolved.
    assertThat(context.getBean(IBsCompanyOurClient.class)).isNotNull();
}
```

**W0 新增 `reportFeignSelfLoopbackWiring_probe`（仿上，改 report 侧）：**
```java
@Test
public void reportFeignSelfLoopbackWiring_probe() {
    // D-P5-03 fail-fast: IRpt*Client proxy + reportServerConfig bean + ReportFeignPathConfig
    assertThat(context.getBean(
        com.spt.bas.client.config.ReportFeignPathConfig.class)).isNotNull();
    assertThat(context.getBean(
        com.spt.bas.report.client.remote.IRptFundReceivableStatisticsClient.class)).isNotNull();
    com.spt.tools.core.bean.LocalServerConfig cfg = context.getBean(
        "reportServerConfig", com.spt.tools.core.bean.LocalServerConfig.class);
    assertThat(cfg.getUrl()).contains("localhost:8080");
}
```

**W5 新增 `allReportMappersResolve` + `reportApiPathPrefixWiring_probe`：**
```java
@Test
public void allReportMappersResolve(org.apache.ibatis.session.SqlSessionFactory sf) {
    // 53 report mapper namespaces registered (sample one + grep 全 53 by planner)
    assertThat(sf.getConfiguration().hasStatement(
        "com.spt.bas.report.server.dao.RptFundReceivableStatisticsMapper.findPage")).isTrue();
}

@Test
public void reportApiPathPrefixWiring_probe(ApplicationContext ctx) throws Exception {
    // path-prefix applied: /spt-bas-report/rpt/... maps to RptFundReceivableStatisticsApi
    RequestMappingHandlerMapping mapping = ctx.getBean(RequestMappingHandlerMapping.class);
    HandlerExecutionChain chain = mapping.getHandler(new MockHttpServletRequest("POST",
        "/spt-bas-report/rpt/fundReceivableStatistics/findPage"));
    assertThat(chain).as("report api path-prefix wiring (D-P5-03)").isNotNull();
}
```

**W6 新增 `sampleReportQuery_proof`（`@Disabled` 默认，启动 proof 时激活）：**
```java
@Test
@Disabled("D-P5-08 sample query proof — activate manually with real DB")
public void sampleReportQuery_proof(/* @Autowired sample mapper */) {
    // 跑 RptFundReceivableStatisticsMapper.findPage + RptCtrContractReportMapper.findRptContractPage
    // + RptBusinessOverviewMapper —— 断言返回非空 + Page shape 正确
}
```

**非 hermetic 沿用 D-P3-13 Option 4 + Phase 4 明文密钥决定**（dev profile 明文密钥已就位，启动测试不需 export `DB_PASSWORD`/`SPT_APP_SECRET`）。

---

## Shared Patterns（跨 wave 共享，所有 plan 必须引用）

### 1. 照搬保包名 (D-P2-07) — 本期元模式

**Source:** Phase 2 `02-CONTEXT.md` D-P2-07 + Phase 4 `04-PATTERNS.md` Shared Pattern 1。
**Apply to:** 全部 6 个 wave 的所有源码文件。
**约束:**
- 包名 verbatim：`com.spt.bas.report.server.*` / `com.spt.bas.report.client.*`
- 零 import 改动（除 W6 BFF stub 字段解除 `required=false`）
- 零类名 rename
- 复制命令：`cp -r /源/.../<subpkg>/ /目标/.../<subpkg>/`（保目录结构）

### 2. 逐层 compile 绿灯 (D-P5-04) — Phase 1 gotcha 级联教训

**Source:** Phase 1 01-CONTEXT D-08 + Phase 4 `04-PATTERNS.md` Shared Pattern 2。
**Apply to:** 每个 wave / sub-wave 结束。
**统一验证命令（locale 无关）：**
```bash
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-pl <module> -am compile 2>&1 | tee /tmp/p5-wN.log | tail -5
echo "ERROR_COUNT=$(grep -c '^\[ERROR\]' /tmp/p5-wN.log)"          # 必须 == 0
echo "CANNOT_FIND=$(grep -cE 'cannot find symbol|找不到符号' /tmp/p5-wN.log)"  # locale 无关双保险
```
**测序铁律（来自 RESEARCH §Pattern 6）：**
```
W0 (265 reportClient + 4 config edits + ReportFeignPathConfig + pom 移除) → compile green
  ↓
W1-W3 (按业务域分批 Mapper + XML + service) → 每 wave compile green
  ↓ ⚠ W3 (Summary/UserRoi) 必须先于 W4 (Ctr* family 15 mapper) — Pitfall 4
W4 (Ctr* + Fact/Pm/Index/Print/Credit/WxCtr, 21 mapper) → compile green
  ↓
W5 (54 api controllers 落 zgbas-admin + BasicErrorController excludeFilter) → compile green
  ↓
W6 (BFF stub-port 升级 + 4 验证 probe) → 启动 + 抽样 proof 绿灯
```
**gotcha 处理原则：** ERROR > 50 时停止机械修复，回查是否漏抄上游层（Phase 1 教训：40→30→20→0 级联）。

### 3. 同位 drop（mybatis XML）— mapper-locations 不变

**Source:** Phase 2 D-P2-04 SampleMapper + RESEARCH §Pattern 2。
**Apply to:** W1-W4 53 XML。
**约束:** 53 XML 落 `zgbas-system/src/main/resources/mybatis/mappers/`（与 SampleMapper.xml 同位）即被现有 `mapper-locations=classpath:/mybatis/mappers/*Mapper.xml` 覆盖。**不改扫描路径。**

### 4. @MapperScan 放宽（D-P5-07 part 2）

**Source:** `ZgbasMybatisConfig.java:23-25` Phase 2 现状 + RESEARCH §Pattern 3。
**Apply to:** W0c 单一编辑（见 Pattern Assignment 0c）。
**约束:** 53 report Mapper 全部 `@MyBatisDao`（grep 计数 53/53），与 SampleMapper 同 annotationClass —— 仅放宽 basePackages 数组加一个串。

### 5. path-prefix 范式（D-P5-03）— Phase 4 Wave 4 同构

**Source:** Phase 4 `BasFeignPathConfig.java` + 04-PATTERNS Wave 3 §D-P4-01a + RESEARCH §Pattern 1。
**Apply to:** W0b 新建 `ReportFeignPathConfig.java`。
**约束:**
- `addPathPrefix("/spt-bas-report", forBasePackage("com.spt.bas.report.server.api"))`
- 仅作用于 report api `@RestController`，不影响 BFF `@Controller`（保 Phase 3 AUTH-03 Shiro 根）
- 加前缀后：Feign 契约路径（`path = "spt-bas-report" + "/rpt/..."`）天然匹配 api 实际暴露路径（`/spt-bas-report/rpt/...`）→ 自回环不再 404

### 6. framework vs admin 边界（D-P2-06 + D-08 + D-P5-01）

**Source:** Phase 2 D-P2-06 + Phase 5 D-P5-01。
**Apply to:** W0-W4 落 `zgbas-system`（reportClient + Mapper + XML + service + util + ReportFeignPathConfig），W5 落 `zgbas-admin`（54 api）。
**约束:** 报表 Mapper/XML/service/reportClient → `zgbas-system`；54 报表 Controller → `zgbas-admin`（D-P5-01：edge controller 与前端模板同模块）。`ZgbasApplication @ComponentScan(basePackages="com.spt")` 自动扫全包。

### 7. ddl-auto=none + 仅修运行阻塞（D-P4-05）

**Source:** Phase 4 D-P4-05。
**Apply to:** W0a 内联 53 report 实体。
**约束:** 53 report 实体 **0 个**带 `javax.persistence.@Entity`（grep 验证，唯一 `RptWxBrandFollow` 用 mybatis-plus `@TableName`，非 JPA）—— 无 JPA 误拾风险，Pitfall 6 已 de-risk。`@EntityScan` 已限定 `com.spt.bas.client.entity` + `com.spt.pm.entity`，不含 `com.spt.bas.report.client.entity`。

### 8. Feign 自回环（D-P4-01 方案 A）— report 侧本期落地

**Source:** Phase 4 D-P4-01 + 04-PATTERNS §D-P4-01 wiring + RESEARCH §Pattern 1。
**Apply to:** W0a reportClient 内联后天然生效（无需改 `@EnableFeignClients`，Phase 4 已放宽）。
**约束:**
- `@EnableFeignClients` 已含 `com.spt.bas.report.client.remote`（`ZgbasApplication.java:122`，Phase 4 已就位）
- `spt.bas.report.url: http://localhost:8080` 已在 `application-dev.yml:49-50`（dev 明文）+ `application-prod.yml:35-36`（`${SPT_BAS_REPORT_URL}` 占位）
- `ReportClientConfig` 产 `reportServerConfig` bean 读取 yml key —— Phase 4 已 component-scanned，本期 W0 内联后 bean "真实"产出

---

## No Analog Found

| File / Pattern | Role | Data Flow | Reason | Fallback |
|---|---|---|---|---|
| （无）—— 本期所有文件组均有 Phase 4 直接同型 analog 或 Phase 2 sample Mapper analog | — | — | — | — |

**说明：** Phase 5 是 Phase 4 的「同型再执行」+ Phase 2 sample Mapper 的「同型扩展」。所有 8 个文件组（含 W0 接线 5 操作、W1-W4 报表层 5 类、W5 api、W6 验证）均有 [VERIFIED] analog。`ReportFeignPathConfig` 是唯一"新建"文件，但它是 `BasFeignPathConfig` 的 verbatim 克隆（仅改 2 常量）—— 不算 greenfield 设计。

---

## Repeatable Task-Shape（每个 bulk-copy wave 直接复用）

**所有 W1-W4 的每个 sub-wave 都是同型操作。planner 直接套用以下模板（提炼自 `02-05-PLAN` Task 1 + `04-PATTERNS` Repeatable Task-Shape）：**

```
<task type="auto">
  <name>Task N: Wave X — 照搬 <业务域> 报表套件 (~<mapper_count> 套)</name>
  <files><目标 glob></files>
  <read_first>
    - /Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/java/com/spt/bas/report/server/{dao,service,service/impl}/  (源目录树)
    - /Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/resources/mybatis/mappers/Rpt<Domain>*Mapper.{java,xml}  (代表性源文件)
    - .planning/phases/05-report-migration/05-PATTERNS.md §Wave X（本文件的 task-shape + accept 数字）
    - .planning/phases/05-report-migration/05-RESEARCH.md §Pattern 6（wave 测序约束）
  </read_first>
  <action>
    照搬保包名（D-P2-07）：3 目录 + XML 同位 drop
      cp -r /源/.../server/dao/Rpt<Domain>*Mapper.java      → zgbas-system/.../bas/report/server/dao/
      cp -r /源/.../server/service/IRpt<Domain>*Service.java → zgbas-system/.../bas/report/server/service/
      cp -r /源/.../server/service/impl/Rpt<Domain>*ServiceImpl.java → zgbas-system/.../bas/report/server/service/impl/
      cp -r /源/.../resources/mybatis/mappers/Rpt<Domain>*Mapper.xml → zgbas-system/.../resources/mybatis/mappers/
    不改源码（XML 的 ${} / 动态 SQL 不改写，D-P5 Claude's Discretion）。
    [dependency] 若本 wave 是 W4 Ctr* family，确认 W3 Summary/UserRoi 已迁（Pitfall 4）。
  </action>
  <verify>
    <automated>
cd /Users/alan/WorkSpace/IDEA/zgbas-plus && \
echo "MAPPER_COUNT=$(find zgbas-system/src/main/java/com/spt/bas/report/server/dao -name 'Rpt<Domain>*Mapper.java' | wc -l)" && \
echo "XML_COUNT=$(find zgbas-system/src/main/resources/mybatis/mappers -name 'Rpt<Domain>*Mapper.xml' | wc -l)" && \
echo "SERVICE_IFACE=$(find zgbas-system/src/main/java/com/spt/bas/report/server/service -maxdepth 1 -name 'IRpt<Domain>*Service.java' | wc -l)" && \
echo "SERVICE_IMPL=$(find zgbas-system/src/main/java/com/spt/bas/report/server/service/impl -name 'Rpt<Domain>*ServiceImpl.java' | wc -l)" && \
echo "FQN_RESULTTYPE=$(grep -c 'resultType="com.spt.bas.report.client' zgbas-system/src/main/resources/mybatis/mappers/Rpt<Domain>*Mapper.xml)" && \
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-pl zgbas-system -am compile 2>&1 | tee /tmp/p5-wN.log | tail -5 && \
echo "ERROR_COUNT=$(grep -c '^\[ERROR\]' /tmp/p5-wN.log)" && \
echo "CANNOT_FIND=$(grep -cE 'cannot find symbol|找不到符号' /tmp/p5-wN.log)"
    </automated>
  </verify>
  <done>
    - MAPPER_COUNT == XML_COUNT == SERVICE_IFACE == SERVICE_IMPL (≈ 预期 wave 内同套数)
    - FQN_RESULTTYPE ≥ MAPPER_COUNT  ⬅ Pitfall 5 验证（全 FQN，无 simple-name alias）
    - ERROR_COUNT == 0 且 CANNOT_FIND == 0
    - 包名 verbatim（grep 确认无 rename）
  </done>
</task>
```

---

## Metadata

**Analog search scope:**
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/`（当前单体：Phase 2 SampleMapper + SampleMapper.xml + ZgbasMybatisConfig + ZgbasApplication + ZgbasApplicationTest + Phase 3 IndexController stub-port + Phase 4 basClient/basServer/web 资产 + BasClientConfig + BasFeignPathConfig + IBsCompanyOurClient + application{,-dev,-prod}.yml + zgbas-system pom.xml + 223 basServer api controllers + StockContractApi 样本）
- `/Users/alan/WorkSpace/IDEA/zgbas/basReport/` 分支 `feat-系统重构v5.0`（源：reportClient/config/ReportClientConfig + constant/ReportConstant + remote/IRptFundReceivableStatisticsClient + reportServer/{dao,service,service/impl,api}/RptFundReceivableStatistics* + resources/mybatis/mappers/RptFundReceivableStatisticsMapper.xml）
- `.planning/phases/04-core-business/04-PATTERNS.md`（Phase 4 Wave 1-4 + D-P4-01/01a/02 蓝本）
- `.planning/phases/05-report-migration/05-{CONTEXT,RESEARCH}.md`（8 locked decisions + 9 pitfall 证据）

**Files scanned:** 8 analog 文件/文件组（BasFeignPathConfig + ZgbasMybatisConfig + SampleMapper.xml + ZgbasApplicationTest + BasClientConfig + IBsCompanyOurClient + StockContractApi + 源 ReportClientConfig/ReportConstant/IRptFundReceivableStatisticsClient/RptFundReceivableStatistics{Mapper,ServiceImpl,Api,XML}）+ 2 yml + 1 pom + 1 ZgbasApplication + grep 验证（14 BFF 冲突清单 + 53 Mapper @MyBatisDao 计数 + reportClient 7 子目录）。

**Pattern extraction date:** 2026-07-17

**关键交付（planner 必读）：**
1. **W0 接线 5 操作 + 已就位资产**（spt.bas.report.url + @EnableFeignClients + ReportClientConfig bean 扫描 已在 Phase 4 就位，本期 W0 仅 5 个原子操作）
2. **W3 必须先于 W4**（service 跨域依赖：`RptCtrContractReportServiceImpl` → `IRptSummaryRoiService` + `IRptUserRoiService`，Pitfall 4）
3. **`ReportFeignPathConfig` 是 `BasFeignPathConfig` verbatim 克隆**（仅改 prefix + basePackage 两行）—— 防 14 处 BFF/api 路径冲突 + 解 D-P5-03 path diverge
4. **`@MapperScan` 放宽 1 行**（单串 → 双串数组，加 `com.spt.bas.report.server.dao`）
5. **`type-aliases-package` 追加 report 包**（application.yml 单 key 改动）
6. **W5 落 `zgbas-admin`**（D-P5-01：报表 api 是前端直连 edge controller，区别于 Phase 4 basServer api 落 system）
7. **W6 stub-port 反向升级**（解除 D-P4-02 report 契约降级，关闭 Phase 3 MyIndexController 缺口）+ 4 验证 probe（仿 Phase 4 `feignSelfLoopbackWiring_probe`）
8. **53 实体 0 个 `@Entity`**（Pitfall 6 已 de-risk —— 无 JPA 误拾，无需特殊处理）

## PATTERN MAPPING COMPLETE

**Phase:** 5 - 报表迁移
**Files classified:** 8 file-groups (~487 net-new + 5 config edits + 4 test probes)
**Analogs found:** 8 / 8

### Coverage
- Files with exact analog: 7（W0a reportClient inline / W0b ReportFeignPathConfig / W0c @MapperScan / W0d type-aliases / W0e pom 移除 / W1b XML / W6 验证 probe —— 全部有 Phase 2/4 直接同型模板）
- Files with role-match analog: 1（W1a/c/d/e Mapper+service+util —— Phase 4 basServer 同型 bulk-copy，层换到 report）
- Files with no analog: 0

### Key Patterns Identified
- **照搬保包名 + 逐层 compile 绿灯**（D-P2-07 + D-P5-04）—— 本期元模式，每个 task 套用 Repeatable Task-Shape
- **path-prefix 范式**（D-P5-03）—— `BasFeignPathConfig` verbatim 克隆为 `ReportFeignPathConfig`，解 14 处 BFF 路径冲突 + Feign 契约 path diverge
- **@MapperScan 放宽**（D-P5-07）—— 单串 → 双串数组，加 `com.spt.bas.report.server.dao`
- **W3 先于 W4**（Pitfall 4）—— service 跨域依赖测序铁律
- **W5 落 zgbas-admin**（D-P5-01）—— 报表 api 是前端直连 edge controller，区别于 Phase 4 basServer api 落 system
- **W6 stub-port 反向升级**（D-P5-03）—— 解除 D-P4-02 report 契约降级，关闭 Phase 3 MyIndexController 缺口

### File Created
`/Users/alan/WorkSpace/IDEA/zgbas-plus/.planning/phases/05-report-migration/05-PATTERNS.md`

### Ready for Planning
Pattern mapping complete. Planner can now reference analog patterns in PLAN.md files. Suggested 6-wave structure (W0 接线 / W1-W4 按业务域 / W5 api / W6 验证)，约束：W0 必先 / W3 必先于 W4（service dep）/ W5 最后（api 依赖 service）/ W6 验收。
