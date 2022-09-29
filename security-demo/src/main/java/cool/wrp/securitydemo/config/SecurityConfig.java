package cool.wrp.securitydemo.config;

import cool.wrp.securitydemo.component.ApiAccessVoter;
import cool.wrp.securitydemo.component.PermissionLessAccessDeniedHandler;
import cool.wrp.securitydemo.component.UnLoginAccessDeniedHandler;
import cool.wrp.securitydemo.filter.JwtAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 配置
 *
 * @author 码小瑞
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationTokenFilter jwtFilter;
    private final ApiAccessVoter apiAccessVoter;
    private final AuthenticationEntryPoint unLoginAccessDeniedHandler;
    private final AccessDeniedHandler permissionLessAccessDeniedHandler;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity.authorizeRequests()
                // 放行所有OPTIONS请求、登录方法
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/api/auth/login").permitAll()
                // 其他请求都需要认证后才能访问
                .anyRequest().authenticated()
                // 自定义 AccessDecisionManager
                .accessDecisionManager(adm())
                // 未登录与权限不足异常处理器
                .and()
                .exceptionHandling()
                .accessDeniedHandler(permissionLessAccessDeniedHandler)
                .authenticationEntryPoint(unLoginAccessDeniedHandler)
                // 将自定义的JWT过滤器放到过滤链中
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // 打开Spring Security的跨域
                .cors()
                .and()
                // 关闭CSRF
                .csrf().disable()
                // 关闭Session机制
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).disable()
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager am() {
        final DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoProvider);
    }

    @Bean
    AccessDecisionManager adm() {
        final List<AccessDecisionVoter<?>> voters = new ArrayList<>();
        voters.add(new WebExpressionVoter());
        voters.add(apiAccessVoter);
        return new UnanimousBased(voters);
    }
}
