---
plan_id: 06-03
phase: 6
wave: 2
title: Service 迁入 III — Wave 1 消费者(BuyEnquiry/BuyQuote/Contract/EweChatApi)
status: complete
commits: [06-03-task1, 06-03-task2, 06-03-task3]
requirements: [WX-SERVICE-01, WX-BFF-03]
---

# 06-03 Summary

**What was built:** 4 Wave-1 consumers migrated. BuyEnquiryServiceImpl (extends BaseService<BuyEnquiry> → IBuyMessageService), BuyQuoteServiceImpl (plain → IBuyMessageService), ContractServiceImpl (plain → IApplyService), and EweChatApi (P5-deferred, @Component → IBuyMessageService loop now closes).

## Tasks Completed

| Task | Result |
|------|--------|
| Task 1 — BuyEnquiry | ✓ `extends BaseService<BuyEnquiry>`, getBaseDao()=BuyEnquiryDao+BuyQuoteDao (WX pkg), @Autowired IBuyMessageService (06-01). |
| Task 2 — BuyQuote + Contract | ✓ both plain impls; BuyQuote→IBuyMessageService, Contract→IApplyService + Feign self-loop. |
| Task 3 — EweChatApi | ✓ `@Component` HTTP wrapper into `ewechat/` (dir created). @Autowired IBuyMessageService closes P5-deferred loop; EweChatConfig/RedisCache/TemplateCardMessage (P5 承托). |

## Key Files

- created: `service/{IBuyEnquiryService,IBuyQuoteService,IContractService}.java`
- created: `service/impl/{BuyEnquiryServiceImpl,BuyQuoteServiceImpl,ContractServiceImpl}.java`
- created: `ewechat/EweChatApi.java` (new subdir)

## Decisions Honored

- 方案1: main-domain calls via Feign self-loop (not direct injection).
- D-16/17: 企业微信 HTTP boundary maintained.

## Deviations / Notes

- `ewechat/` subdir did not exist in enclave — created it (`mkdir -p`).
- Cross-plan: IContractService feeds Wave 3 SuccessContract (06-04) + Wave 4 UserInfo.

## Self-Check: PASSED
