package com.ta.llmbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private JwtAuthFilter jwtAuthFilter;

        @Bean
        public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/**")
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors()
                                .and()
                                .authorizeHttpRequests(auth -> {
                                        // <=============== Uncomment to revoke security ===============>
                                        // auth.requestMatchers("/**").permitAll();

                                        // })

                                        // <=============== Uncomment to invoke security ===============>

                                        // User endpoints
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/user").permitAll();
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/user/auth").permitAll();
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/user").hasAuthority("0");
                                        auth.requestMatchers(HttpMethod.GET,
                                                        "/api/v1/user?role=*").hasAuthority("0");
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/user/*").permitAll();
                                        auth.requestMatchers(HttpMethod.PUT, "/api/v1/user/*/update")
                                                        .hasAnyAuthority("0");
                                        auth.requestMatchers(HttpMethod.DELETE, "/api/v1/user/*/delete").permitAll();

                                        // PDF Utilities
                                        auth.requestMatchers("/api/v1/upload_pdf").permitAll();
                                        auth.requestMatchers("/pdf_context/*").permitAll();

                                        // Quiz Test
                                        auth.requestMatchers("/quiz/generate").permitAll();

                                        // Quiz
                                        auth.requestMatchers(HttpMethod.POST,
                                                        "/api/v1/quiz/assign").hasAuthority("0");
                                        auth.requestMatchers(HttpMethod.POST,
                                                        "/api/v1/quiz/start").permitAll();

                                        // Quiz Package
                                        auth.requestMatchers(HttpMethod.POST,
                                                        "/api/v1/quiz/package").hasAuthority("0");
                                        auth.requestMatchers(HttpMethod.GET,
                                                        "/api/v1/quiz/package").hasAuthority("0");
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/quiz/package/*").permitAll();
                                        auth.requestMatchers(HttpMethod.GET,
                                                        "/api/v1/quiz/package/available/*").permitAll();
                                        auth.requestMatchers(HttpMethod.PUT,
                                                        "/api/v1/quiz/*/update").hasAuthority("0");
                                        auth.requestMatchers(HttpMethod.DELETE,
                                                        "/api/v1/quiz/package/*/delete").hasAuthority("0");

                                        // Quiz Activity
                                        auth.requestMatchers(HttpMethod.POST,
                                                        "/api/v1/quiz/activity/*/restart").permitAll();
                                        auth.requestMatchers(HttpMethod.GET,
                                                        "/api/v1/quiz/activity").hasAuthority("0");
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/quiz/activity/*").permitAll();
                                        auth.requestMatchers(HttpMethod.GET,
                                                        "/api/v1/quiz/activity/available/*").permitAll();
                                        auth.requestMatchers(HttpMethod.DELETE,
                                                        "/api/v1/quiz/activity/*/delete").hasAuthority("0");

                                        // Question
                                        auth.requestMatchers(HttpMethod.POST,
                                                        "/api/v1/quiz/question").hasAuthority("0");
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/quiz/question/**").permitAll();
                                        auth.requestMatchers(HttpMethod.GET,
                                                        "/api/v1/quiz/package/*/question").permitAll();
                                        auth.requestMatchers(HttpMethod.PUT,
                                                        "/api/v1/quiz/question/*/update").hasAuthority("0");
                                        auth.requestMatchers(HttpMethod.DELETE,
                                                        "/api/v1/quiz/question/*/delete").hasAuthority("0");

                                        // Evaluation
                                        auth.requestMatchers(HttpMethod.POST,
                                                        "/api/v1/evaluation/multichoice").permitAll();
                                        auth.requestMatchers(HttpMethod.POST,
                                                        "/api/v1/evaluation/essay").permitAll();
                                        auth.requestMatchers(HttpMethod.GET,
                                                        "/api/v1/evaluation/quiz/*").permitAll();
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/evaluation/*").permitAll();
                                })

                                .sessionManagement(sessionAuthenticationStrategy -> sessionAuthenticationStrategy
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .httpBasic(Customizer.withDefaults());
                return http.build();
        }

        @Autowired
        public void confAuthentication(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder)
                        throws Exception {
                auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }

}
