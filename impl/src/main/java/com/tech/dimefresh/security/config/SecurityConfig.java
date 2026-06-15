package com.tech.dimefresh.security.config;

import com.tech.dimefresh.security.filter.SessionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SessionFilter sessionFilter;


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Security configured");
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler))
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(getPermittedResources()).permitAll()
                        .requestMatchers("/api/s3/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint()))
                .addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public String[] getPermittedResources() {
        return new String[]{
                "/error",
                "/",
                "/auth/login",
                "/auth/register",
                "/css/**",
                "/api/oauth2/authorize",
                "/api/oauth2/callback"
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> map = new LinkedHashMap<>();

        map.put(PathPatternRequestMatcher.withDefaults().matcher("/api/**"), new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        map.put(PathPatternRequestMatcher.withDefaults().matcher("/auth/info"), new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        map.put(PathPatternRequestMatcher.withDefaults().matcher("/chat/**"), new LoginUrlAuthenticationEntryPoint("/auth/login"));
        map.put(PathPatternRequestMatcher.withDefaults().matcher("/profile/**"), new LoginUrlAuthenticationEntryPoint("/auth/login"));

        DelegatingAuthenticationEntryPoint delegating = new DelegatingAuthenticationEntryPoint(map);
        delegating.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint("/auth/login"));
        return delegating;
    }


}
