---
plan: 08-03
phase: 8
wave: 3
status: green-in-executor  # awaiting user independent re-run confirmation (D-P8-02)
sc: [SC#2 GREEN(executor), SC#3 GREEN(executor), SC#4 GREEN(executor)]
requirements: [WX-ALIGN-02, WX-ALIGN-03]
decisions: [D-P8-01, D-P8-02, user Option A]
committed: [35897a8 bean fixes, 9d9c2ac DAO reconcile+shadow+placeholders]
---

# 08-03: 独立复跑 mvn test + runtime fix-to-GREEN —— **executor 复跑 GREEN,待用户独立确认**

## Result(executor 复跑)

```
Tests run: 37, Failures: 0, Errors: 0, Skipped: 8
Started ZgbasApplicationTest in 38.878 seconds
BUILD SUCCESS
```

- SC#2(WX-ALIGN-02,启动 GREEN)= **GREEN(executor)** —— ApplicationContext 启动,含 Druid/239 实体/53 sys_job/WX bean 装配,日志无 FAILED/启动异常(2 处 post-startup WARN = report reachability probe POST null body 的 HttpMessageNotReadableException,handler 解析为非404,符合 probe 判据,非失败)
- SC#3(WX-ALIGN-03,WX 端点非404)= **GREEN(executor)** —— `wxEndpointsReachable_proof` 通过(4 端点: /wx/user/login · /ewechat/buyEnquiry/getProductTree · /axq/doSuccessContract · /purchase/user/saveApplyOnLineData,均 !=404)
- SC#4(WX-ALIGN-03,WX 自回环 proof)= **GREEN(executor)** —— `wxPurchaseFeignSelfLoopbackWiring_probe` 通过(purchaseWxServerConfig bean + IWxUserDetailClient/ISaveTempClient proxy + url localhost:8080 三段)
- 8 skipped = 既有 @Disabled 真跑/写类(按 D-P8-02 不计 SC#2 硬闸门)

⚠️ **D-P8-02 防假阳性:GREEN 必须用户本地独立复跑证实。executor 自报不作唯一证据(P6 executor 自报假绿教训)。**

## Step 0 — dev DB 前置 ✓
`sptbasdb_pd`(47.104.15.98:3306)TCP 可达;Redis 可达;dev yml 明文密钥(D-P4,无需 export)。Hibernate 日志确认 239 实体入 PersistenceUnit,53 sys_job 载入(schedulerLoadAllJobs_proof GREEN)。

## Runtime fix-to-GREEN inventory(D-P8-01 最小修复,逐处标根因+行为等价)

共 3 轮 9 处修复(均 basWx verbatim 合并的 runtime 暴露,源用模块隔离扫描,单体 `com.spt` 广扫必然撞):

### 轮 1 — bean 名/类型冲突(4 类 + 1 名注入,commit 35897a8)
| 类 | 冲突 | 修复 |
|---|---|---|
| FileController(WX @RestController /wx/file/*) | bean 名撞 web @Controller /file/* | `@RestController("wxFileController")` |
| BsDictService(WX @Component) | bean 名撞 basServer(不同 IBsDictService 接口) | `@Component("wxBsDictService")` |
| SMSUtils(WX @Component) | bean 名撞 basServer | `@Component("wxSMSUtils")` |
| DeptUtils(WX @Component) | bean 名撞 basServer | `@Component("wxDeptUtils")` |
| UserInfoService @Resource deptUtils | WX 唯一名注入调用方 | `@Resource(name="wxDeptUtils")` |

### 轮 2 — DAO 层调和(用户 Option A,commit 9d9c2ac)
4 WX Dao 与 basServer 同名同实体 → JPA 每域类型仅 1 repo,不能并存:
- basServer `BsCompanyDao` 并入 4 WX finder 方法
- WX 调用方(ApplyServiceImpl/UserService/UserInfoService)改指 basServer Dao(显式 import,单类型优先于通配)
- 删 4 WX Dao(BsCompanyDao/BsDictDataDao/BsDictTypeDao/FeedbackDao;后 3 verbatim 同或空,BsDictTypeDao 无调用方)
- 注:BsCompanyService/BsDictService/FeedbackService 等已由 Phase 6 指向 basServer Dao,本轮补齐剩余 3 调用方

### 轮 2 续 — BsCompanyService bean 影子(commit 9d9c2ac)
WX `BsCompanyService`(默认 bean `bsCompanyService`)遮蔽 35 处 basServer `@Resource bsCompanyService` 字段名注入(期望 basServer IBsCompanyService,impl=BsCompanyServiceImpl)。WX 无名注入调用方 → `@Component("wxBsCompanyService")` 安全。

### 轮 2 续 — WX subscribe-msg 占位符(commit 9d9c2ac)
`buyMessageServiceImpl` 的 `wx.miniapp.msg.template_quote/deal/system` `@Value` 占位符缺失 → 加 dev yml(verbatim 源值 `../zgbas/basWx/.../application.properties:91-93`,仅 WX 消息推送 runtime 消费,非启动)。

## key-files.modified
- `zgbas-admin/src/main/resources/application-dev.yml`(WX msg 模板占位符)
- `zgbas-system/.../bas/server/dao/BsCompanyDao.java`(+4 finder)
- `zgbas-system/.../bas/purchase/wx/server/service/impl/{FileController→controller/,BsDictService,SMSUtils,DeptUtils,BsCompanyService,UserInfoService,UserService,ApplyServiceImpl}.java`(bean 名/import)
- 删 4 `zgbas-system/.../bas/purchase/wx/server/dao/{BsCompanyDao,BsDictDataDao,BsDictTypeDao,FeedbackDao}.java`

## Decisions applied
- **D-P8-01** 最小修复边界:每处仅"使四层闸门 GREEN 所必需",标根因+行为等价;未顺手根治(深层业务正确性/WX 端到端 defer v1.3/手动 UAT)
- **D-P8-02** 防假阳性:GREEN 待用户独立复跑证实
- **用户 Option A**(DAO 调和 → 单一 basServer Dao):已执行,4 WX Dao 合入 basServer

## Self-Check: PASSED(executor 复跑层)
- `Tests run: 37, Failures: 0, Errors: 0, Skipped: 8` ✓
- 两新 probe wxEndpointsReachable_proof / wxPurchaseFeignSelfLoopbackWiring_probe 通过(surefire XML testcase self-closing,非 skip)✓
- 启动日志无 FAILED/APPLICATION FAILED TO START/NoSuchBean/AmbiguousMapping/ConflictingBean ✓
- post-startup 2 处 WARN(report reachability POST null body)属预期,非失败 ✓

## Gaps / Deferred
- ⚠️ **用户独立复跑确认(D-P8-02)**:executor 复跑 GREEN 非最终权威,需用户本地跑同一命令证实
- 深层行为等价(WX 端点业务正确性 / 多步流程 / WX 小程序端到端)→ 手动 UAT / v1.3,非本验证 scope
- DAO 合并的 4 finder 方法 findBy...AndEnableFlgTrue 仅编译期+启动期证 GREEN;实际查询语义(返回正确公司)需运行时/UAT 抽样(v1.3)

## 用户独立复跑命令(D-P8-02)
```bash
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
  /Users/alan/App/apache-maven-3.8.6/bin/mvn test \
  -pl zgbas-admin -am -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
  -Dtest=ZgbasApplicationTest -DfailIfNoTests=false
```
(注:`-DfailIfNoTests=false` 必需 —— 上游模块无 ZgbasApplicationTest,否则 surefire "No tests were executed")
```
