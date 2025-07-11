package com.nguyenhan.maddemo1.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("customAuditorAware")
public class CustomAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return Optional.of("SYSTEM ADMIN");
        }else{
            return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }
}
