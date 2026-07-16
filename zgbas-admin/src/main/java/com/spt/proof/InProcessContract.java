package com.spt.proof;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Pure contract interface proving D-P2-10: an interface carrying Spring MVC mapping
 * annotations is satisfied by a local bean implementing it.
 *
 * <p>NO {@code @FeignClient} here — this is a standalone trivial contract. A caller
 * {@code @Autowired}s this interface and receives the local {@code InProcessContractImpl}
 * bean. Spring MVC honors {@link GetMapping} declared on the interface when the
 * implementing {@code @RestController} registers as a bean. This is the exact pattern
 * Phase 4 applies to 295 internal {@code @FeignClient} interfaces (D-P2-10/D-P2-11).
 */
public interface InProcessContract {

    @GetMapping("/proof/echo")
    String echo(@RequestParam("msg") String msg);
}
