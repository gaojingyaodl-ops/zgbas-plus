# Phase 4: 核心业务迁移 - Pattern Map

**Mapped:** 2026-07-17
**Files analyzed:** ~1356 net-new across 4 waves (Wave 1: 249 / Wave 2: 615 [2a 82 + 2b 533] / Wave 3: 224 / Wave 4: 267)
**Analogs found:** 9 / 9 file-groups (all mapped — Phase 4 is bulk verbatim copy, so analogs are prior-phase copy precedents + already-inlined Phase 2/3 assets, not greenfield designs)

> **照搬保包名 (D-P2-07) 是本期元模式。** 本期 100% 是机械搬运 + 逐层 compile 绿灯，无新代码设计。因此每个 analog 不是「风格参考」，而是「上一次同型操作的执行蓝图」——planner 应直接复用其 task-shape、verify 命令、acceptance 数字。

---

## File Classification

| New/Modified File Group | Role | Data Flow | Closest Analog | Match Quality |
|---|---|---|---|---|
| **Wave 1** `zgbas-system/com/spt/bas/client/remote/**` (~234 net-new `I*Client`) | feign-contract (interface) | request-response | `zgbas-system/.../remote/IBsDictClient.java` (Phase 2) + `IBsCompanyOurClient.java` | **exact** (same `@FeignClient extends BaseClient` shape) |
| **Wave 1** `zgbas-system/com/spt/bas/client/{dto,util,common,riskScore}/**` (~14) | model / utility | passive (data carrier) | Phase 2 inlined `com.spt.bas.client.{vo,constant,cache}` (02-05-PLAN Task 1) | **role-match** (verbatim copy of POJO/utility) |
| **Wave 1** `zgbas-system/com/spt/bas/client/config/BasClientConfig.java` (1) | config-bean | bean-wiring | 源 `basClient/.../config/BasClientConfig.java` + Phase 2 `LocalServerConfig` bean 模式 | **exact** (源文件照搬) |
| **Wave 2a** `zgbas-system/com/spt/bas/server/{cache,util,enums,annotation,filter,listener,command,event,rocketmq,config}/**` (~82) | infra / config / event-driven (rocketmq) | mixed (util + event-driven) | Phase 2 `02-03-PLAN` (spt-tools-jpa/web/shiro/aop inline + compile-gate) + `02-05-PLAN` Task 1 (bulk-copy + ERROR_COUNT gate) | **role-match** (infra 包 verbatim + pom 加 dep) |
| **Wave 2a** config dedup (FrameworkConfig/BasJobConfig) | config | bean-wiring | Phase 2 `ZgbasDataSourceConfig.java` (@Primary 击败 ToolsJpaConfig) + `ZgbasMybatisConfig.java` | **exact** (同型 dedup 机制) |
| **Wave 2b** `zgbas-system/com/spt/bas/server/service/**` + `service/impl/**` + `ctr/logistics/performance/rt/stock/**` (~533) | service + impl (business logic) | CRUD + domain-logic | Phase 2 `02-05-PLAN` Task 1 (239 entity + 240 Dao bulk verbatim copy + compile-gate) | **role-match** (per-layer bulk copy) |
| **Wave 3** `zgbas-system/com/spt/bas/server/api/**` (224 `@RestController extends BaseApi`) | controller (REST endpoint) | request-response | 源 `basServer/api/ApplyBrandApi.java` + Phase 3 `03-02-PLAN` Task 1 (LoginController clean copy) + Phase 2 `02-05-PLAN` (bulk copy task-shape) | **exact** (照搬保包名 + extends BaseApi shape) |
| **Wave 4** `zgbas-admin/com/spt/bas/web/controller/**` (267 BFF) | controller (BFF) | request-response | Phase 3 `03-02-PLAN` Task 1 (LoginController/UserOpenController clean copy) + Task 2 (IndexController stub-port for ~5 D-P4-02 contracts) | **exact** (同型 web 照搬 + stub 变体) |
| **D-P4-01 wiring** `ZgbasApplication.java` `@EnableFeignClients` + `application-{dev,prod}.yml` (self-loopback) | config / boot | bean-wiring | 当前 `ZgbasApplication.java` + 源 `BasClientConfig` + `BasConstants.SERVER_URL` | **exact** (放宽 basePackages 一行) |
| **D-P4-02 stub** ~5-15 no-impl 契约 + BFF 字段 `@Autowired(required=false)` | stub-port | request-response (degraded) | Phase 3 `03-02-PLAN` Task 2 (`IPmProcessClient`/`IApproveWaitDealClient` stub + IndexController required=false + `WebParamUtils` stub) | **exact** (D-P3-10 同型) |
| **WR-02 验收** `ZgbasApplicationTest.java` 扩 3-5 `I*Client` 端点 HTTP reachability | test | request-response (HTTP proof) | 当前 `ZgbasApplicationTest.java`（14 @Test, TestRestTemplate endpoint reachability） | **exact** (沿用 TestRestTemplate 风格) |

---

## Pattern Assignments

### Wave 1 — `basClient` 数据载体 (~249 net-new)

#### 1a. `zgbas-system/com/spt/bas/client/remote/**` (~234 net-new `I*Client`)

**Analog:** `zgbas-system/src/main/java/com/spt/bas/client/remote/IBsDictClient.java`（Phase 2 已内联）+ `IBsCompanyOurClient.java`

**Imports + 类签名 pattern**（来自 `IBsDictClient.java:1-25`）：
```java
package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictType;        // entity 引用 → Phase 2 已就位
import com.spt.tools.data.service.BaseClient;       // spt-tools-data，Phase 2 已内联
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = BasConstants.SERVER_NAME,
             path = BasConstants.SERVER_NAME + "/api/dict",
             url = BasConstants.SERVER_URL,                  // "#{basServerConfig.url}"
             configuration = FeignConfig.class)
public interface IBsDictClient extends BaseClient<BsDictType> {
    @PostMapping(value = "loadDatasByTypeCd")
    public List<BsDictData> loadDatasByTypeCd(@RequestBody String dictTypeCd, @RequestParam("enterpriseId") Long enterpriseId);
    // ...
}
```

**关键常量（来自源 `BasConstants.java:15-23`，Phase 2 已内联）：**
```java
String SERVER_NAME = "spt-bas-server";
String SERVER_BEAN_NAME = "basServerConfig";
String SERVER_URL = "#{" + SERVER_BEAN_NAME + ".url}";   // SpEL 读 basServerConfig bean 的 url 字段
String SERVER_URL_KEY = "spt.bas.server.url";
```
→ 契约的 `url = BasConstants.SERVER_URL` 是 SpEL 表达式，运行期由 `BasClientConfig` 产出的 `basServerConfig` bean 解析（见 Wave 1c）。**方案 A 自回环只需在 yml 设 `spt.bas.server.url=http://localhost:8080`，无需改任何契约。**

**Task-shape（照搬保包名，对应 `02-05-PLAN` Task 1 验证命令）：**
```
read:    /Users/alan/WorkSpace/IDEA/zgbas/basCore/basClient/src/main/java/com/spt/bas/client/remote/
copy:    recursive → zgbas-system/src/main/java/com/spt/bas/client/remote/
exclude: IBsDictClient / IBsCompanyOurClient / IApproveWaitDealClient (stub) / IPmProcessClient (stub) — 已存在
         （后两个 stub 由 Wave 1 用真实源文件覆盖，见 D-P4-02 stub-port 升级）
compile: mvn -pl zgbas-system -am compile；grep `^\[ERROR\]` locale 无关 → 0
accept:  REMOTE_COUNT = 239（238 top-level + basTrade 子包 + package-info）
         FEIGNCLIENT_COUNT = `grep -rl '@FeignClient' remote/ | wc -l` ≥ 235
         EXTENDS_BASECLIENT = `grep -rl 'extends BaseClient' remote/ | wc -l` ≥ 230
```

---

#### 1b. `zgbas-system/com/spt/bas/client/{dto,util,common,riskScore}/**` (~14)

**Analog:** Phase 2 已内联的 `com.spt.bas.client.{vo,constant,cache}`（数据载体 + 静态常量 + 缓存工具，同型 POJO/utility 包，照搬保包名）+ `02-05-PLAN` Task 1 的 entity/Dao bulk-copy 任务模式。

**Task-shape**（同 1a，目录换为 dto/util/common/riskScore）：
```
copy:   dto(2) / util(6) / common(3) / riskScore(3) → com/spt/bas/client/<subpkg>/
compile: mvn -pl zgbas-system -am compile；grep `^\[ERROR\]` → 0
accept: 14 文件就位，包名 `com.spt.bas.client.{dto,util,common,riskScore}.*` verbatim
```

---

#### 1c. `zgbas-system/com/spt/bas/client/config/BasClientConfig.java` (1)

**Analog:** 源 `basCore/basClient/src/main/java/com/spt/bas/client/config/BasClientConfig.java`（19 行，直接照搬）+ Phase 2 已内联的 `com.spt.tools.core.bean.LocalServerConfig`（依赖目标）。

**源文件全文（`BasClientConfig.java:1-19`）：**
```java
package com.spt.bas.client.config;

import com.spt.bas.client.constant.BasConstants;
import com.spt.tools.core.bean.LocalServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class BasClientConfig {

    @DependsOn({"propertiesUtil"})
    @Bean(BasConstants.SERVER_BEAN_NAME)              // bean name = "basServerConfig"
    public LocalServerConfig localServerConfig() {
        LocalServerConfig conf = new LocalServerConfig();
        conf.setUrlKey(BasConstants.SERVER_URL_KEY);  // 读 yml: spt.bas.server.url
        return conf;
    }
}
```

**此 bean 是 D-P4-01 方案 A 自回环的运行期锚点：** 238 契约的 `url = BasConstants.SERVER_URL = "#{basServerConfig.url}"` 全部解析到此 bean 的 `url` 字段；Wave 0 在 `application-{dev,prod}.yml` 加 `spt.bas.server.url=http://localhost:8080` 即可让所有 Feign proxy 自回环到本进程 8080。**零契约改动。**

---

### Wave 2a — infra (~82) + Wave 2a config dedup

**Analog (bulk-copy 任务模式):** Phase 2 `02-03-PLAN` Task 1（spt-tools-jpa/web/mybatis/shiro/aop 5 目录 verbatim 内联 + compile-gate 迭代）+ `02-05-PLAN` Task 1（ERROR_COUNT 验证命令）。

**Analog (config dedup 机制):** Phase 2 `zgbas-framework/.../ZgbasDataSourceConfig.java`（@Primary 击败 ToolsJpaConfig 的 `@Bean("datasource") @ConditionalOnMissingBean`）+ `ZgbasMybatisConfig.java`（只补 framework-specific bean，不重复 DataSource）。

**ZgbasDataSourceConfig dedup 蓝本（`ZgbasDataSourceConfig.java:29-53`）：**
```java
@Bean
@ConfigurationProperties(prefix = "spring.datasource.druid")
public DataSourceConfig dataSourceConfig() { return new DataSourceConfig(); }

@Bean("datasource")
@Primary                                                // ← 击败 spt-tools 的两个 @ConditionalOnMissingBean
public DataSource dataSource(DataSourceConfig config) {
    return DataSourceCreator.createDataSource(config);
}
```

**Wave 2a infra 分类 + 处置表（来自源 `basServer/{cache,util,...,config}/` ls 实测）：**

| 子包 | 源文件数 | 处置 | 备注 |
|---|---|---|---|
| `cache` | 7 | ✅ 全照搬 | service 运行期依赖 |
| `util` | 34 | ✅ 全照搬 | |
| `enums` | 2 | ✅ 全照搬 | |
| `annotation` | 1 | ✅ 全照搬 | |
| `filter` | **6** (3 接口 + `filter/impl/` 3 impl) | ✅ 全照搬 | CONTEXT 误写 3，实测 6（含 impl） |
| `listener` | 1 | ✅ 全照搬 | |
| `command` | 2 | ✅ 全照搬 | `BasCommandExecutor` import rocketmq，必须与 rocketmq 同波 |
| `event` | 1 | ✅ 全照搬 | |
| `rocketmq` | **22** | ✅ 全照搬 + pom 加 `rocketmq-spring-boot-starter:2.2.2` | CONTEXT 误写 2；实测 22（producer 1 + customProperties 1 + dto 2 + listener 1 + util 3 + tags 5 + task 8 + send 1）。3 个非 rocketmq 文件 import 此包 → 必照搬 |
| `config` | 8 | ⚠️ **选择性 6** | 见下表 |
| ❌ `task` | 23 | **EXCLUDE → Phase 6** | xxl-job handler（INFRA-03） |

**Wave 2a config 8 文件分类（源 `basServer/config/` ls 实测，对应 `RESEARCH §D-P4-04`）：**

| 文件 | 类型 | 处置 | 蓝本依据 |
|---|---|---|---|
| `FrameworkConfig.java` | `bas.datasource` DataSource + 3 SDK bean | **DEDUP** 不抄 | 与 Phase 2 `ZgbasDataSourceConfig` (@Primary) + EXT-01..03 重复 bean 冲突（同 `ToolsJpaConfig` 击败模式） |
| `BasJobConfig.java` | `@Bean XxlJobSpringExecutor` | **EXCLUDE → Phase 6** | 引入 xxl-job 依赖（INFRA-03），同 `task` 包 |
| `WebAppConfig.java` | Servlet filter + multipart | ✅ COPY | 业务必需 web 配置 |
| `BasicErrorController.java` | Error controller | ✅ COPY | |
| `RtConfig.java` | `@ConfigurationProperties(prefix="rt.config")` | ✅ COPY | 被 `rt/RtApi` 引用 |
| `GuTuConfig.java` | `@ConfigurationProperties(prefix="gutu.config")` | ✅ COPY | |
| `BasPiccConfig.java` | `@ConfigurationProperties(prefix="picc.config")` | ✅ COPY | |
| `ScheduleConfig.java` | `@EnableScheduling` + `ScheduledTaskRegistrar` | ✅ COPY | **注意**：Spring 内置 `@Scheduled`，非 xxl-job（与 `BasJobConfig` 不同） |

**Task-shape（Wave 2a infra，复用 02-03-PLAN Task 1 模式）：**
```
read:    /Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/{cache,util,enums,annotation,filter,listener,command,event,rocketmq,config}/
copy:    → zgbas-system/src/main/java/com/spt/bas/server/<subpkg>/  （保包名）
exclude: config/FrameworkConfig.java（DEDUP）、config/BasJobConfig.java（P6）、task/**（P6）
pom:     zgbas-system/pom.xml (or root dependencyManagement) 加 rocketmq-spring-boot-starter:2.2.2
yml:     application-dev.yml 加 rocketmq.name-server=47.104.66.178:9876 + producer.group（源 application-dev.properties:22）
         application-prod.yml 加 ${ROCKETMQ_NAMESERVER} / ${ROCKETMQ_AK} / ${ROCKETMQ_SK} 占位（D-P2-13 外置）
compile: mvn -pl zgbas-system -am compile；grep `^\[ERROR\]` → 0  ⬅ **2a 绿灯后才能开 2b**
accept:  INFRA_COUNT = find server/{cache,util,enums,annotation,filter,listener,command,event,rocketmq} -name '*.java' | wc -l ≈ 76
         CONFIG_COUNT = find server/config -name '*.java' | wc -l == 6（去 FrameworkConfig + BasJobConfig）
         ROCKETMQ_STARTER = grep -c 'rocketmq-spring-boot-starter' <module>/pom.xml ≥ 1
         XXLJOB_ABSENT = grep -rl 'XxlJobSpringExecutor\|xxl-job' zgbas-system/src/main/java | wc -l == 0
```

---

### Wave 2b — service + impl + 域子包 (~533)

**Analog:** Phase 2 `02-05-PLAN` Task 1（239 entity + 240 Dao verbatim bulk-copy + per-layer compile gate + ERROR_COUNT 验证）。这是本期最直接的「同型再执行」——只是层从 entity/Dao 换到 service/impl。

**关键 task-shape（提炼自 `02-05-PLAN` Task 1 action + verify）：**
```
read:    /Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/service/  (顶-level 241)
         /.../service/impl/   (248)
         /.../ctr/            (9)
         /.../logistics/      (6)
         /.../performance/    (4)
         /.../rt/             (1)
         /.../stock/          (24)
copy:    → zgbas-system/src/main/java/com/spt/bas/server/<subpkg>/  （保包名 D-P2-07）
compile: mvn -pl zgbas-system -am compile  ⬅ gotcha 级联迭代至零错
verify:  ERROR_COUNT=$(grep -c '^\[ERROR\]' /tmp/p4-w2b.log) == 0
         SERVICE_IFACE = find service -maxdepth 1 -name 'I*.java' | wc -l ≈ 241
         SERVICE_IMPL  = find service/impl -name '*.java' | wc -l ≈ 248
accept:  所有 service extends BaseDataService<...>/IDatasource<...>（来自 spt-tools-data，Phase 2 已内联）
         所有 @Service impl 可被 @Autowired 解析（运行期由 ZgbasApplication 扫 com.spt.* 自动接线）
```

**预期 gotcha（源自 02-05-PLAN 经验 + RESEARCH Pitfall 5）：**
- `@Query` 引用未抄实体 → 已被 Phase 2 全量 239 实体覆盖，风险低
- 引用 `com.spt.bas.server.{cache,util,...,rocketmq}` → 必须在 2a 绿灯后开 2b
- 引用未抄 basClient remote 契约 → 必须 Wave 1 绿灯后开 Wave 2
- 若 ERROR > 50，**停止机械修复**，回查是否漏抄了某个 2a infra 子包（Phase 1 gotcha 级联教训）

---

### Wave 3 — `basServer/api/**` (224 `@RestController extends BaseApi`)

**Analog (类签名 shape):** 源 `basCore/basServer/src/main/java/com/spt/bas/server/api/ApplyBrandApi.java`（实测 224 文件全部同型）。

**ApplyBrandApi 全文（`ApplyBrandApi.java:1-19`，照搬目标原型）：**
```java
package com.spt.bas.server.api;

import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.server.service.IApplyBrandService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "apply/brand")              // ⚠ 无 leading slash；源 context-path=/spt-bas-server 加前缀
public class ApplyBrandApi extends BaseApi<BasBrand> {   // ⚠ 未 implements IApplyBrandClient（D-P4-01 Critical Finding）

    @Autowired
    private IApplyBrandService applyBrandService;

    @Override
    public IDataService<BasBrand> getService() {
        return applyBrandService;
    }
}
```

**Analog (bulk-copy task-shape):** Phase 2 `02-05-PLAN` Task 1（同型 verbatim bulk-copy + compile-gate）+ Phase 3 `03-02-PLAN` Task 1（Controller 类照搬的 read_first → copy → verify 模板）。

**关键约束（D-P4-01 修正方案 A，必须编码进 plan）：**
1. **224 api 文件 1:1 照搬，零修改** — 不加 `implements I*Client`（签名不兼容：`BaseApi.findPage` 返 `Page<T>` vs `BaseClient` 返 `PageDown<T>`，反协变；详见 RESEARCH §D-P4-01）。
2. **path 前缀处理（D-P4-01a 子决策）** — 源 `server.servlet.context-path=/spt-bas-server` 让 `@RequestMapping("apply/brand")` 实际暴露在 `/spt-bas-server/apply/brand/*`；单体 D-P2-16 根 `/`。**优先 Feign path 覆盖**（yml `feign.client.config` 或契约级 `path=` 覆盖）去掉前缀；**不设单体 context-path**（会破坏 Phase 3 已验的 Shiro `/login` / `/index` 根路径，AUTH-03 回归风险）。若 Feign path 覆盖不可行（RESEARCH A3），planner 作 `checkpoint:human-verify` 上报，不擅自改 238 契约。

**Task-shape：**
```
read:    /Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/api/
copy:    → zgbas-system/src/main/java/com/spt/bas/server/api/  （含子目录 basData/basTrade/fund/sign）
compile: mvn -pl zgbas-system -am compile；grep `^\[ERROR\]` → 0
accept:  API_COUNT = find api -name '*.java' | wc -l == 224
         EXTENDS_BASEAPI = grep -rl 'extends BaseApi' api/ | wc -l ≥ 217
         IMPLEMENTS_ICLIENT = grep -rln 'implements.*I[A-Z][a-zA-Z]*Client' api/ | wc -l == 0  ⬅ 必须为 0（验证未误加 implements）
```

---

### Wave 4 — `web/controller/**` (267 BFF)

**Analog (干净照搬):** Phase 3 `03-02-PLAN` Task 1 — `LoginController` + `UserOpenController`（干净 verbatim，无修改）。

**LoginController 照搬蓝本（`zgbas-admin/.../web/controller/LoginController.java:1-45`，Phase 3 已照搬）：**
```java
package com.spt.bas.web.controller;        // 保包名 com.spt.bas.web.*

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.spt.bas.web.shiro.ShiroUtil;

@Controller
@RequestMapping(value = "/login")
public class LoginController { /* 照搬不变 */ }
```

**Analog (stub-port 变体):** Phase 3 `03-02-PLAN` Task 2 — `IndexController` 的 3 个 `@Autowired(required=false)` + null 守卫。本期 Wave 4 中 **~5-15 个** 引用 D-P4-02 无实现契约的 BFF 字段复用此模式。

**IndexController stub-port 蓝本（`zgbas-admin/.../web/controller/IndexController.java:59-66`）：**
```java
// STUB (D-P3-10): Phase-4 FeignClient contract not yet migrated. required=false so
// startup succeeds without a bean; null-guards degrade business-data calls.
@Autowired(required = false)
private IPmProcessClient processClient;
@Autowired(required = false)
private IApproveWaitDealClient waitDealClient;
@Autowired(required = false)
private WebParamUtils webParamUtils;
```

**IndexController null 守卫蓝本（`IndexController.java:120-126` + `294-295`）：**
```java
// STUB guard (D-P3-10): waitDealClient/webParamUtils absent until Phase 4
if (waitDealClient != null) {
    Long userWaitDealNum = waitDealClient.getUserWaitDealNum(...);
    mmap.put("waitDealNum", webParamUtils != null ? webParamUtils.formatterWaitDealNum(userWaitDealNum) : "0");
} else {
    mmap.put("waitDealNum", "0");
}
// ...
List<PmProcess> processList = processClient != null ? processClient.findAccess(searchVo) : Collections.emptyList();
```

**Task-shape（Wave 4 BFF，复用 03-02-PLAN Task 1 + Task 2 模式）：**
```
read:    /Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/controller/  (267 files)
copy:    → zgbas-admin/src/main/java/com/spt/bas/web/controller/  （保包名 com.spt.bas.web.controller.*）
         已存在的 LoginController / IndexController 跳过（Phase 3 已就位）
stub:    对 ~5-15 个 D-P4-02 契约（ApplyCompanyOnline/ApplyDeposit/ApplyPromoteVip/BsInvestigateInfo/
           CtrOutInLedger/RiskApply/SignFileApi + PM 域 13 簇默认 stub）的 @Autowired 字段
         → 改 @Autowired(required=false) + null 守卫（参考 IndexController:59-66 + 120-126）
         ⚠ 仅当契约确无本地 api 实现时才 stub；命名变体匹配（BaiduMap/LogisticsCompany×2/MQ/ApplyDcsx）按真 api 走
compile: mvn -pl zgbas-admin -am compile；grep `^\[ERROR\]` → 0
accept:  BFF_COUNT = find admin/.../web/controller -name '*.java' | wc -l == 269 (267 + LoginController + IndexController 已存在)
         BFF_REFERENCES_ICLIENT = grep -rl 'I[A-Z][a-zA-Z]*Client' admin/.../web/controller | wc -l ≈ 252
```

---

### D-P4-01 Feign 自回环 wiring（Wave 0/1 落地）

**Analog:** 当前 `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java:66`（Phase 2 收窄 basePackages）+ 源 `BasClientConfig.java`（bean 锚点）+ `BasConstants.SERVER_URL`（SpEL）。

**当前 ZgbasApplication 收窄态（`ZgbasApplication.java:60-67`）：**
```java
@ComponentScan(
    basePackages = "com.spt",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { com.spt.tools.http.feign.FeignConfig.class,
                    com.spt.sign.client.config.FeignConfig.class }
    )
)
@EnableFeignClients(basePackages = "com.spt.sign.client.remote")   // ← Phase 2 收窄（D-P2-12）
@EntityScan(basePackageClasses = IdEntity.class,
            basePackages = {"com.spt.bas.client.entity", "com.spt.pm.entity"})
@EnableJpaRepositories(basePackages = {"com.spt.bas.server.dao"})
public class ZgbasApplication { ... }
```

**方案 A 修正（放宽 basePackages 一行，注释说明为何不破坏 D-P2-12 收窄意图）：**
```java
// Phase 4 D-P4-01 (corrected 2026-07-17, 方案 A self-loopback):
// 放宽扫 com.spt.bas.client.remote → 238 bas 契约生成 Feign proxy，url=#{basServerConfig.url}
// 由 BasClientConfig 产的 basServerConfig bean 解析为 http://localhost:8080 → 自回环到本进程
// api 端点（同进程 HTTP 跳，无跨进程跳；行为等价，照搬保真）。原 D-P2-12 收窄意图（避免双 bean
// 冲突）由 api 不 implements I*Client 保证——api 与契约无 implements 关系，无本地 bean 冲突。
@EnableFeignClients(basePackages = {
    "com.spt.sign.client.remote",   // EXT-03 cfca，Phase 2 已就位
    "com.spt.bas.client.remote"     // Phase 4 新增：bas 契约自回环
})
```

**application-{dev,prod}.yml 新增（保 Phase 2 D-P2-13 占位模式）：**
```yaml
spt:
  bas:
    server:
      url: http://localhost:8080    # 单体自回环；BasClientConfig bean 读此 key
rocketmq:
  name-server: 47.104.66.178:9876   # 照搬源 application-dev.properties:22
  producer:
    group: contract_producer_group  # 照搬源 application.properties:66
    access-key: ${ROCKETMQ_AK:zgrocketmq}      # D-P2-13 外置（源明文 → 占位）
    secret-key: ${ROCKETMQ_SK:zg12345678}      # 同上
```

**注意 path 前缀（D-P4-01a，planner 必须在 Wave 1 锁定）：**
- 优先：yml `feign.client.config` 或契约级 `path=` 覆盖，去掉 `spt-bas-server` 前缀
- 不行：`checkpoint:human-verify` 上报（RESEARCH A3 风险），不擅自改 238 契约

---

### D-P4-02 Stub-Port 升级（Wave 1 + Wave 4）

**Analog:** Phase 3 `03-02-PLAN` Task 2 stub-port 全套（3 个产物）。

**stub 契约接口蓝本（`IPmProcessClient.java:1-18`，Phase 3 已创建）：**
```java
// STUB — Phase 4 ports the real interface from spt-bas-client. Temporary for Phase 3 IndexController compilation.
package com.spt.bas.client.remote;

import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import java.util.List;

public interface IPmProcessClient {
    List<PmProcess> findAccess(PmProcessSearchVo searchVo);
}
```

**stub utility 蓝本（`WebParamUtils.java:1-27`，Phase 3 已创建）：**
```java
// STUB — Phase 4 ports the real WebParamUtils. Methods return empty/null per D-P3-10.
package com.spt.bas.web.util;

public class WebParamUtils {
    public String formatterWaitDealNum(Long num) { return num == null ? "0" : String.valueOf(num); }
    public BsCompanyDcsx queryFundCompany() { return null; }
}
```

**Wave 1 升级动作：**
- `IPmProcessClient.java` / `IApproveWaitDealClient.java` 的 stub **被源 `basClient/remote/` 真实 `@FeignClient extends BaseClient` 接口覆盖**（Wave 1a 照搬包含这两个文件 → 自然覆盖）
- 若 Phase 3 stub 接口签名与源不兼容（如返回类型差），保留源版（保包名 + 保签名），同步调整 IndexController 调用点
- 若某些契约（PM 域 13 簇疑 spt-auth 外部）确实无源接口，**保留 stub**，按 D-P4-02 处理

**Wave 4 BFF 字段 stub（~5-15 处）：**
- 引用 D-P4-02 enumeration 中的契约（ApplyCompanyOnline / ApplyDeposit / ApplyPromoteVip / BsInvestigateInfo / CtrOutInLedger / RiskApply / SignFileApi / PM 域簇）的 BFF `@Autowired IXxxClient` 字段
- 改为 `@Autowired(required=false)` + null 守卫（参考 IndexController:59-66 + 120-126）
- 业务降级裸 404 / 空数据，后续 Phase 5/7 接通

---

### WR-02 验收扩展（Wave 0 / 验收阶段）

**Analog:** 当前 `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java`（14 @Test，`@SpringBootTest(RANDOM_PORT)` + `TestRestTemplate` + endpoint reachability 断言）。

**endpoint reachability 蓝本（`ZgbasApplicationTest.java:117-130`）：**
```java
@Test
void loginEndpointReachable() {                    // AUTH-01 D-P3-13
    ResponseEntity<String> response = restTemplate.getForEntity("/login", String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()
        || response.getStatusCode().is3xxRedirection()).isTrue();
}

@Test
void indexEndpointReachable() {                    // AUTH-02 D-P3-13
    // Not logged in → Shiro user filter redirects to /login (302) — correct behavior.
    ResponseEntity<String> response = restTemplate.getForEntity("/index", String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()
        || response.getStatusCode().is3xxRedirection()).isTrue();
}
```

**bean 存在性抽样蓝本（`ZgbasApplicationTest.java:100-113`）：**
```java
@Test
void loginControllerRegistered() {                 // AUTH-01 D-P3-03
    assertThat(context.containsBean("loginController")).isTrue();
}

@Test
void indexControllerRegistered() {                 // AUTH-02 D-P3-10
    assertThat(context.containsBean("indexController")).isTrue();
}
```

**WR-02 扩展（D-P4-06）—— 加 3-5 真实 `I*Client` 端点 HTTP reachability + BFF bean 存在性抽样：**
```java
// 例 1：合同域 IApplyBrandClient 经 Feign 自回环 HTTP 可达
@Test
void basContractEndpointReachable_applyBrand_findAll() {
    ResponseEntity<String> response = restTemplate.postForEntity(
        "/apply/brand/findAll", null, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()
        || response.getStatusCode().is3xxRedirection()
        || response.getStatusCodeValue() == 401).isTrue();   // Shiro 未登录可能 401/302
}

// 例 2-4：授信/库存/放款各选一代表性 I*Client 端点（合同/授信/库存/放款四个域各一）

// 例 5：BFF controller bean 抽样（覆盖 BIZ-02）
@Test
void bffControllersRegistered_sample() {
    // 选 3-5 个代表性 BFF（如 applyBrandController / ctrContractController / stockDetailController）
    assertThat(context.containsBean("applyBrandController")).isTrue();
    // ...
}
```

**断言语义：** `is2xxSuccessful || is3xxRedirection || 401` — 证明端点已注册 + Spring MVC 经 `@GetMapping`/`@PostMapping` over HTTP 可达（WR-02 满足）。

**非 hermetic 契约（沿用 D-P3-13 Option 4，必须编码进 plan）：**
```
前置:    export DB_PASSWORD=<dev 密码>
         export SPT_APP_SECRET=<dev secret>
run:     JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
         /Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
         -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest
accept:  "local-export = passing" 语义（同 Phase 3）；CI hermetic 化不在本期
```

---

## Shared Patterns（跨 wave 共享，所有 plan 必须引用）

### 1. 照搬保包名 (D-P2-07) — 本期元模式

**Source:** Phase 2 `02-CONTEXT.md` D-P2-07 + `02-05-PLAN` Task 1 action。
**Apply to:** 全部 4 个 wave 的所有源码文件。
**约束:**
- 包名 verbatim：`com.spt.bas.server.*` / `com.spt.bas.client.*` / `com.spt.bas.web.*`
- 零 import 改动（除 D-P4-02 BFF stub 字段的 `@Autowired(required=false)`）
- 零类名 rename（rename 级联：Phase 4 的 1226+ import 引用会爆）
- 复制命令：`cp -r /源/.../<subpkg>/ /目标/.../<subpkg>/`（保目录结构）

### 2. 逐层 compile 绿灯 (D-P4-03) — Phase 1 gotcha 级联教训

**Source:** Phase 1 01-CONTEXT D-08 + Phase 2 `02-05-PLAN` verify 段。
**Apply to:** 每个 wave / sub-wave 结束。
**统一验证命令（locale 无关）：**
```bash
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-pl <module> -am compile 2>&1 | tee /tmp/p4-wN.log | tail -5
echo "ERROR_COUNT=$(grep -c '^\[ERROR\]' /tmp/p4-wN.log)"   # 必须 == 0
echo "CANNOT_FIND=$(grep -cE 'cannot find symbol|找不到符号' /tmp/p4-wN.log)"  # locale 无关双保险
```
**测序铁律（来自 RESEARCH Pitfall 5）：**
```
Wave 1 (249) → compile green
  ↓
Wave 2a infra (82) → compile green   ⬅ 必须先于 2b
  ↓
Wave 2b service+impl+域子包 (533) → compile green
  ↓
Wave 3 api (224) → compile green
  ↓
Wave 4 BFF (267) → compile green
  ↓
验收: ZgbasApplicationTest（含 WR-02 扩展）启动 + 全 context bean 解析
```
**gotcha 处理原则：** ERROR > 50 时停止机械修复，回查是否漏抄上游层（Phase 1 教训：一处错掩一处，级联从 40→30→20→0）。

### 3. 密钥环境变量外置 (D-P2-13)

**Source:** Phase 2 D-P2-13 + `02-VERIFICATION.md` 非 hermetic 契约。
**Apply to:** `application-{dev,prod}.yml` 的新增配置（rocketmq.* / spt.bas.server.url）。
**蓝本:**
```yaml
# 占位 + 默认值（dev 可默认，prod 必须 ${ENV}）
rocketmq:
  name-server: ${ROCKETMQ_NAMESERVER:47.104.66.178:9876}
  producer:
    access-key: ${ROCKETMQ_AK:zgrocketmq}      # 源明文 → 占位
    secret-key: ${ROCKETMQ_SK:zg12345678}      # ⚠ 源为明文，必须外置
```

### 4. 依赖 pin 旧版本 (D-P2-08)

**Source:** Phase 2 D-P2-08。
**Apply to:** 唯一新依赖 `rocketmq-spring-boot-starter:2.2.2`。
**约束:** 版本号 verbatim 取自源 `basCore/pom.xml:25` `<rocketmq-version>2.2.2</rocketmq-version>`，不升级到 2.3.x。

### 5. 事务边界 (Phase 2 JpaTransactionManager @Primary)

**Source:** Phase 2 D-P2-03 + `ZgbasDataSourceConfig.java` @Primary DataSource + 隐式 @Primary JpaTransactionManager。
**Apply to:** Wave 2b 所有 `@Transactional` service impl。
**约束:** 沿用 Phase 2 `JpaTransactionManager @Primary`，业务 `@Transactional` 无需额外设计（单 DataSource，无 JTA）。

### 6. framework vs admin 边界 (D-P2-06 + D-08)

**Source:** Phase 2 D-P2-06 + Phase 1 D-08。
**Apply to:** Wave 1/2/3 落 `zgbas-system`，Wave 4 落 `zgbas-admin`。
**约束:** 业务 Service + REST 实现 → `zgbas-system`；web BFF controller → `zgbas-admin`。ZgbasApplication `@ComponentScan(basePackages="com.spt")` 自动扫全包。

---

## No Analog Found

| File / Pattern | Role | Data Flow | Reason | Fallback |
|---|---|---|---|---|
| D-P4-01a Feign path 前缀覆盖（`feign.client.config` yml 或契约级 `path=` 覆盖去 `spt-bas-server` 前缀） | config | bean-wiring | 当前单体无 Feign path 覆盖先例（Phase 2 收窄到 cfca 单包，未处理 path 前缀问题） | RESEARCH A3 假设 + `checkpoint:human-verify`；若不可行上报不擅改 238 契约 |
| PM 域 13 簇契约归属（PmApplySet/PmApprove×5/PmProcess×7） | feign-contract | request-response | 源 zgbas grep 不到 implements；疑 spt-auth 外部，未确认 | 默认按 D-P4-02 stub；若验收发现 BFF 真依赖 PM 服务则改按外部 HTTP 接入 |

---

## Repeatable Task-Shape（每个 bulk-copy wave 直接复用）

**所有 4 个 wave 的每个 sub-wave 都是同型操作。planner 直接套用以下模板（提炼自 `02-05-PLAN` Task 1 + `02-03-PLAN` Task 1 + `03-02-PLAN` Task 1）：**

```
<task type="auto">
  <name>Task N: Wave X — 照搬 <子包描述> (~<count> 文件)</name>
  <files><目标 glob></files>
  <read_first>
    - /Users/alan/WorkSpace/IDEA/zgbas/<源模块>/src/main/java/com/spt/<源子包>/  （源目录树）
    - <代表性源文件>（确认 extends/inherits 关系 + import 链）
    - .planning/phases/04-core-business/04-PATTERNS.md §Wave X（本文件的 task-shape + accept 数字）
    - .planning/phases/04-core-business/04-RESEARCH.md §<对应 wave>（实测计数 + gotcha）
  </read_first>
  <action>
    照搬保包名（D-P2-07）：`cp -r /源/.../<subpkg>/ /目标/.../<subpkg>/`（保目录结构，零 rename）。
    [exclude] 段列出排除项（已存在 / D-P4-02 stub 保留 / Phase 6 延后）。
    [pom]    若引入新依赖（仅 rocketmq），加 <dependency>。
    [yml]    若新增配置（spt.bas.server.url / rocketmq.*），加占位（D-P2-13）。
    不改源码（除 D-P4-02 BFF 字段的 @Autowired(required=false) + null 守卫）。
  </action>
  <verify>
    <automated>
cd /Users/alan/WorkSpace/IDEA/zgbas-plus && \
echo "COUNT=$(find <目标 glob> -name '*.java' | wc -l)" && \
echo "EXTENDS=$(grep -rl '<extends 模式>' <目标 glob> | wc -l)" && \
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-pl <module> -am compile 2>&1 | tee /tmp/p4-wN.log | tail -5 && \
echo "ERROR_COUNT=$(grep -c '^\[ERROR\]' /tmp/p4-wN.log)" && \
echo "CANNOT_FIND=$(grep -cE 'cannot find symbol|找不到符号' /tmp/p4-wN.log)"
    </automated>
  </verify>
  <done>
    - COUNT == 预期值（±2）
    - EXTENDS ≥ 预期阈值
    - ERROR_COUNT == 0 且 CANNOT_FIND == 0
    - 包名 verbatim（grep 确认无 rename）
    - [exclude] 项确认未抄
  </done>
</task>
```

---

## Metadata

**Analog search scope:**
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/`（当前单体：Phase 2/3 资产 + ZgbasApplication + ZgbasApplicationTest + 4 inlined remote 契约 + IndexController stub-port + framework config dedup）
- `/Users/alan/WorkSpace/IDEA/zgbas/` 分支 `feat-系统重构v5.0`（源：basClient/config/BasClientConfig + BasConstants + basServer/api/ApplyBrandApi + basServer/config/* 8 文件 + basServer/rocketmq/* 22 文件）
- `.planning/phases/02-infrastructure/02-{03,05}-PLAN.md`（Phase 2 bulk-copy + infra inline + compile-gate 蓝本）
- `.planning/phases/03-auth-homepage/03-02-PLAN.md`（Phase 3 web controller 干净照搬 + stub-port 蓝本）

**Files scanned:** 9 analog 文件/文件组（4 inlined 契约 + InProcessContract proof + IndexController stub-port + LoginController clean copy + WebParamUtils stub + ZgbasApplicationTest + ZgbasDataSourceConfig + ZgbasMybatisConfig + 源 BasClientConfig/BasConstants/ApplyBrandApi + 源 config 8 + 源 rocketmq 22）+ 3 PLAN 蓝本（02-03 / 02-05 / 03-02）

**Pattern extraction date:** 2026-07-17

**关键交付（planner 必读）：**
1. **Wave 测序铁律 + 2a/2b 拆分** — RESEARCH Pitfall 5 gotcha 级联教训
2. **D-P4-01 方案 A 修正** — 224 api 零修改 + Feign 自回环（放宽 basePackages + spt.bas.server.url）
3. **D-P4-01a path 前缀** — 优先 Feign path 覆盖，不设单体 context-path（保 Phase 3 AUTH-03）
4. **D-P4-02 stub 升级** — Phase 3 stub-port 模式复用，~5-15 契约降级
5. **照搬保包名 + 逐层 compile 绿灯** — 本期元模式，每个 task 套用 Repeatable Task-Shape
6. **WR-02 验收** — TestRestTemplate endpoint reachability + BFF bean 抽样（非 hermetic 沿用 D-P3-13 Option 4）
