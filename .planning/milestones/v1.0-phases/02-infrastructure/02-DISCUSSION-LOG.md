# Phase 2: 基础设施 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-16
**Phase:** 2-基础设施
**Areas discussed:** 持久层范围, spt-tools 内联范围与归宿, Feign 进程内化策略, 配置收敛 + 密钥外置

---

## 持久层范围 (PERSIST-01/03/04)

| Option | Description | Selected |
|--------|-------------|----------|
| 全量迁入 | Phase 2 全量迁 259/~250 实体 + 254 Dao（纯数据层，无业务逻辑），Phase 4 只加 Service/Controller | ✓ |
| 基建+代表验证 | Phase 2 只搭双 ORM 基建 + IdEntity/BaseDao + 代表性实体验证，bulk 推 Phase 4 | |
| 核心子集先行 | Phase 2 迁 Phase 4 核心业务所需实体子集，其余推迟 | |

**User's choice:** 全量迁入
**Notes:** 实体/Dao 是机械搬运（全继承 IdEntity / BaseDao），持久层作完整地基一次到位，Phase 4 更轻。

| Option | Description | Selected |
|--------|-------------|----------|
| 禁用 Flyway + validate | 指向现有 sptbasdb_pd，Hibernate ddl-auto=validate 只校验不建表 | ✓ |
| 保留 Flyway | 迁移历史 migration 脚本，启动 baseline+校验 | |
| 禁用 Flyway + none | ddl-auto=none，完全不校验 | |

**User's choice:** 禁用 Flyway + validate
**Notes:** schema 已存在，validate 暴露实体/表偏差即可，不碰生产 schema。

| Option | Description | Selected |
|--------|-------------|----------|
| 集成测试+独立测试库 | @SpringBootTest 连独立 test schema 验证 JPA+mybatis+审计 | |
| 启动验证为主 | 单进程启动成功即视为 infra 可用，不写查询测试 | ✓ |
| 集成测试+真实库只读 | 连真实 sptbasdb_pd 只读验证 | |

**User's choice:** 启动验证为主
**Notes:** 查询正确性留 Phase 4 业务接入时验。

| Option | Description | Selected |
|--------|-------------|----------|
| infra + sample Mapper | mybatis-plus infra + trivial sample Mapper 证明同源可执行，53 报表留 Phase 5 | ✓ |
| 只配 starter 占位 | 仅配 mybatis-plus starter + MapperScan 占位，不跑查询 | |
| 迁 1-2 套代表性报表 | 迁 1-2 套报表 Mapper+XML 证明真实查询 | |

**User's choice:** infra + sample Mapper
**Notes:** 满足 PERSIST-03 成功标准"双 ORM 可同时执行查询"。

| Option | Description | Selected |
|--------|-------------|----------|
| 仅 basServer | 只迁 basServer ~250 实体+Dao，排除 basWx(~11, v2) | ✓ |
| basServer + basWx 全量 | 迁全部 JPA 实体含 purchase | |

**User's choice:** 仅 basServer
**Notes:** 对齐 #14 basWx 第一阶段不迁。

---

## spt-tools 内联范围与归宿 (INLINE-01..04)

> **范围由 scout 数据确认（非灰色地带）**：16 模块中 10 个被引用（core835/data790/jpa1226/http319/web238/mybatis55/file51/shiro4/aop1/config3），5 个零引用（elastic/redis/kafka/sdkutil/wechat4j）跳过。10 模块 0 内部反向引用 5 个不内联模块 → 照搬不编译断裂。

| Option | Description | Selected |
|--------|-------------|----------|
| 全部落 common | 10 模块全内联进 common，framework 留 zgbas 自有 infra 接线 | ✓ |
| 按性质拆 common/framework | 纯工具落 common，infra 性质(data/web/shiro)落 framework | |

**User's choice:** 全部落 common
**Notes:** 对齐 INLINE 字面 + 内联单元完整，spt-tools 跨模块依赖不跨边界。

| Option | Description | Selected |
|--------|-------------|----------|
| 源码照搬+保留包名 | .java 原样复制，保留 com.spt.tools.* verbatim | ✓ |
| 重新组织/裁剪 | 重组包名或裁剪未用类 | |

**User's choice:** 源码照搬+保留包名
**Notes:** Phase 4 业务 2000+ 引用 1:1 映射、零 import 改动。

| Option | Description | Selected |
|--------|-------------|----------|
| pin 旧版本 | 根 pom dependencyManagement pin 旧项目原版本 | ✓ |
| Spring Boot 优先+旧版补 | Spring Boot 管能管的，其余 pin 旧版 | |
| 旧版+高危库升级 | pin 旧版同时升 fastjson 等高危库 | |

**User's choice:** pin 旧版本
**Notes:** 行为等价优先（符合"Spring 尽量不变"）。

| Option | Description | Selected |
|--------|-------------|----------|
| mybatis-plus | 随 spt-tools-mybatis 内联，mybatis 侧用 mybatis-plus | ✓ |
| plain mybatis | 改用 mybatis-spring-boot-starter | |

**User's choice:** mybatis-plus
**Notes:** 与旧 ReportServer 栈一致，报表 XML/分页包装器可直接搬。

---

## Feign 进程内化策略 (INFRA-02)

> **依赖耦合（scout 发现）**：295 FeignClient 全 url= 直连（无 nacos 路由），分布 basClient 238 / basReport 54 / basWx 3(越界) + cfcaSignClient 外部。目标 impl 在 Phase 4/5 → Phase 2 只能定机制+删 nacos+trivial proof。

| Option | Description | Selected |
|--------|-------------|----------|
| 接口即契约+impl满足接口 | 保留 FeignClient 接口作契约，目标 Controller/Service 实现接口注册本地 bean，调用方零改动 | ✓ |
| 删接口+注入 Service | 删 FeignClient 接口，调用方改注入 Service 直调 | |
| 接口保留+delegating bean | 每接口写本地转发 bean | |

**User's choice:** 接口即契约+impl满足接口
**Notes:** RuoYi 风格，调用方零改动、行为等价，bulk 转换随 Phase 4/5 天然完成。

| Option | Description | Selected |
|--------|-------------|----------|
| 机制+删nacos+trivial proof | 建机制+删 nacos+删 Feign 发现+1 个 trivial proof，保留 OpenFeign 供 cfca | ✓ |
| 只删nacos+Feign发现 | 不建机制不 proof，全推 Phase 4/5 | |
| 机制+删nacos+硬转部分业务 | 额外硬转一部分 basClient（需临时搬 impl） | |

**User's choice:** 机制+删nacos+trivial proof
**Notes:** 295 真实转换推 Phase 4/5（impl 耦合）。

| Option | Description | Selected |
|--------|-------------|----------|
| 收窄到仅sign包 | @EnableFeignClients 收窄到 com.spt.sign.client.remote | ✓ |
| 全扫描+localhost回环 | 全包扫描 + 295 接口 url 指向 localhost | |
| 去OpenFeign改RestTemplate | 完全去 OpenFeign，cfca 改 RestTemplate | |

**User's choice:** 收窄到仅sign包
**Notes:** 避免 double-bean 冲突，295 内部接口作纯契约由本地 impl 满足。

---

## 配置收敛 + 密钥外置 (INFRA-04)

> **scout 发现**：jdbc.properties 用 `bas.datasource.*` 连公网生产 IP 47.104.15.98/sptbasdb_pd（明文密码技术债）；application.properties 有 `spring.flyway.enabled=true`（须改 false）/ nacos.discovery / ehcache 二级缓存 / xxl.accessToken（密文也在 git）。

| Option | Description | Selected |
|--------|-------------|----------|
| 环境变量占位+轮换 | 敏感项改 ${DB_PASSWORD:} 占位，真实值不入 git，轮换生产密码 | ✓ |
| 独立prod文件不入git | 敏感项放 application-prod.properties 加 .gitignore | |
| 外部secrets文件+import | spring.config.import 指向部署机密钥文件 | |

**User's choice:** 环境变量占位+轮换
**Notes:** 12-factor、适配 fat jar 部署。

| Option | Description | Selected |
|--------|-------------|----------|
| dev + prod | application.yml + application-dev.yml + application-prod.yml，uat/test 弃用 | ✓ |
| 保留全 4 profile | dev/uat/prod/test 对齐旧环境 | |
| 无profile全环境变量 | 只留 application.yml + 全环境变量 | |

**User's choice:** dev + prod
**Notes:** 单体部署只需 dev/prod。

> **数据源前缀张力（scout 消解）**：`bas.datasource` 绑定在 basServer `FrameworkConfig.java`（非 spt-tools），故统一到 `spring.datasource.*` 不违反照搬；spt-tools-data `DataSourceCreator` 前缀无关。→ D-P2-15 由 scout 定，无需用户选。

| Option | Description | Selected |
|--------|-------------|----------|
| 8080 + 根 / | 端口 8080 + 无 context-path（Phase 1 默认） | ✓ |
| 80 + 根 / | 对齐旧 web BFF 入口 | |
| 带 context-path | 保留 context-path 隔离 | |

**User's choice:** 8080 + 根 /
**Notes:** 单体标准。

---

## Claude's Discretion

- 实体/Dao 落 `zgbas-system`，包名对齐源 `com.spt.bas.*` verbatim。
- spt-tools 内联分层节奏：core→(data,http,file)→(jpa,web,mybatis,shiro,aop,config)，每层 compile 绿灯再下一层。
- JPA 二级缓存 / show-sql / Druid 池参数：照搬旧 application.properties。
- 外部 URL 配置（spt.qxb/auth/push/file server.url）：迁入 application.yml 作占位。
- xxl.job 配置键：Phase 2 配置收敛时移除（handler 迁移见 Phase 6）。
- actuator/health：Phase 2 不加。

## Deferred Ideas

- Shiro 认证/首页 → Phase 3
- 业务 Service/Controller/BFF → Phase 4（Phase 2 只搬数据层）
- 53 报表 Mapper+XML → Phase 5
- 295 FeignClient bulk 真实转换 → Phase 4/5
- 64 xxl-job handler 迁 quartz → Phase 6
- basWx 实体 → v2
- 报表物理分页改造 / 补审计字段 → 不在本期（Out of Scope）
