package com.spt.proof;

import org.springframework.web.bind.annotation.RestController;

/**
 * Local bean registered as {@link RestController}. Spring MVC honors {@link
 * InProcessContract#echo} 's {@code @GetMapping} on the interface. Callers
 * {@code @Autowired InProcessContract} resolve to this local bean — proving the
 * in-process contract pattern (D-P2-10) that Phase 4 reuses for 295 internal clients.
 */
@RestController
public class InProcessContractImpl implements InProcessContract {

    @Override
    public String echo(String msg) {
        return "echo:" + msg;
    }
}
