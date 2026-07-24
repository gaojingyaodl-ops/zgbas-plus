---
plan: 08-03
phase: 8
wave: 3
status: in_progress  # blocked at autonomous=false checkpoint — WX/basServer DAO duplication wall
sc: [SC#2 BLOCKED, SC#3/#4 evidence PENDING user re-run]
requirements: [WX-ALIGN-02, WX-ALIGN-03]
decisions: [D-P8-01, D-P8-02]
committed: 35897a8 (runtime fixes), 2f7adc9 (probes, 08-01)
---

# 08-03: 独立复跑 mvn test + runtime fix-to-GREEN —— **进行中,checkpoint 阻塞**

## Status

**SC#1(WX-ALIGN-01,编译门)= GREEN**(08-02)。
**SC#2/3/4(WX-ALIGN-02/03,runtime)= BLOCKED** —— 启动复跑暴露 basWx verbatim 合并的 runtime 冲突,已最小修复 5 处(bean 名/类型/名注入),**仍阻塞于更深的 WX/basServer DAO 层重复**(JPA 每域类型仅 1 repository)。该 DAO 调和超出 D-P8-01 最小修复边界(❶-B 膨胀/撞业务),`autonomous=false` checkpoint 待用户决策。

⚠️ **未达 GREEN,executor 自报不作证据(D-P8-02)。** 当前状态:编译绿 / 启动仍未过(阻塞于 DAO 调和)。

## Step 0 — dev DB 前置 ✓

`sptbasdb_pd`(47.104.15.98:3306)TCP 可达;Redis(6379)可达;`application-dev.yml` 明文密钥 D-P4(无需 export)。Hibernate 日志确认 239 实体(含 BsCompany)入 PersistenceUnit。

## 已完成 runtime 最小修复(5 处,commit 35897a8)

均为 basWx verbatim 迁入与 basServer 同名类的合并冲突(源用模块隔离扫描;单体 `com.spt` 广扫必然撞):

| # | 类 | 冲突 | 最小修复 | 行为等价依据 |
|---|---|---|---|---|
| 1 | `FileController`(WX @RestController `/wx/file/*`) | bean 名 `fileController` 撞 web `@Controller` `/file/*` | `@RestController("wxFileController")` | 路由空间不相交,两 bean 皆活;镜像 P5 `RptBaseCostApi` 先例 |
| 2 | `BsDictService`(WX @Component) | bean 名 `bsDictService` 撞 basServer | `@Component("wxBsDictService")` | 实现不同 `IBsDictService` 接口(basServer vs WX),类型注入不歧义 |
| 3 | `SMSUtils`(WX @Component) | bean 名 `SMSUtils` 撞 basServer | `@Component("wxSMSUtils")` | 静态自代理(`smsUtils=this`)不受影响;`UserService` 仅静态调用 `SMSUtils.isMobile` |
| 4 | `DeptUtils`(WX @Component) | bean 名 `deptUtils` 撞 basServer | `@Component("wxDeptUtils")` | — |
| 5 | `UserInfoService @Resource deptUtils` | WX 唯一名注入调用方 | `@Resource(name="wxDeptUtils")` | 指向重命名后的 WX bean |

每处标根因 + 行为等价。这 5 处**无论 DAO 调和如何决策都必需**(bean 名冲突不解则启动不过)。

## ⛔ 阻塞点:WX/basServer DAO 层重复(JPA 域类型唯一约束)

5 处修复后,启动推进到 WX service 装配,暴露:

```
NoSuchBeanDefinitionException: No qualifying bean of type
'com.spt.bas.purchase.wx.server.dao.BsCompanyDao'  (userService 依赖,共 5 个 WX Dao)
```

**根因:** 4 个 WX Dao 与 basServer Dao **同名同实体**(`extends BaseDao<SameEntity>`):

| WX Dao | basServer Dao | 实体 |
|---|---|---|
| `bas.purchase.wx.server.dao.BsCompanyDao` | `bas.server.dao.BsCompanyDao` | `BsCompany` |
| `bas.purchase.wx.server.dao.BsDictDataDao` | `bas.server.dao.BsDictDataDao` | `BsDictData` |
| `bas.purchase.wx.server.dao.BsDictTypeDao` | `bas.server.dao.BsDictTypeDao` | `BsDictType` |
| `bas.purchase.wx.server.dao.FeedbackDao` | `bas.server.dao.FeedbackDao` | `Feedback` |

Spring Data JPA **每域类型仅允许 1 个 repository**;两 `BaseDao<BsCompany>` 不能并存(不同于 `@Component` bean 可重命名消歧)。结果:仅 basServer Dao 注册为 bean,WX 类型 Dao bean 不存在 → WX service 装配失败。`userService` 依赖 5 个 WX Dao(CompanyUserDao/WxSmsCheckCodeDao/BsCompanyDao/UserDetailDao/WxSessionDao)。

**为何超出 D-P8-01 边界:** 调和需(a)逐 Dao 比 WX 特有方法 vs basServer 方法覆盖,(b)WX service 改指 basServer Dao 或合并方法,(c)删 WX 重复 Dao —— 触及 WX 业务 DAO 语义,**结构性决策 + 行为等价风险**,属 D-P8-01 ❶-B(膨胀/撞业务)范畴,非"补缺 bean/适配签名"最小修复。`autonomous=false` checkpoint 待用户定调和路径。

## Pending(decision needed)

DAO 层重复的调和路径(详见 checkpoint 提问):
- A. **调和**:WX 重复 Dao 的特有方法并入 basServer Dao,WX service 改指 basServer Dao,删 WX 重复 Dao(单一事实源,正确但工作量+行为风险)
- B. **`@NoRepositoryBean` + 手写**:WX Dao 不作 JPA repo,手写 impl(保留 WX 类型,绕过 JPA 唯一约束;但失去 Spring Data 自动代理)
- C. **Defer WX service 启动**:本里程碑不要求 WX service 全装配,调整 always-on 测试范围,DAO 调和留 v1.3(但 SC#2 启动门需 WX bean 装配,可能仍过不了)
- D. 用户其它路径

## What's verified so far
- dev DB/Redis 可达 ✓
- 5 处 bean 冲突最小修复(类型/接口不相交,行为等价)✓
- DAO 域类型重复 = 结构性墙(已根因定位)✓

## Not yet
- SC#2 启动 GREEN(阻塞 DAO)
- SC#3/4 probe GREEN evidence(需启动过)
- 用户本地独立复跑确认(D-P8-02)
