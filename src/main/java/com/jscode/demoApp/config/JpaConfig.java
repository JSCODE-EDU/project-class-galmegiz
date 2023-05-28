package com.jscode.demoApp.config;

import com.jscode.demoApp.dto.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String id = null;
                if(authentication.getPrincipal() instanceof UserPrincipal){
                    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
                    id = principal.getId().toString();
                }

               return Optional.ofNullable(id);
            }
        };
    }
}
