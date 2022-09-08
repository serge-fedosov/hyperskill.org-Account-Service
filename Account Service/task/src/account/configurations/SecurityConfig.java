package account.configurations;

import account.exceptions.AccessDeniedHandlerImpl;
import account.exceptions.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthenticationEntryPoint unauthorizedHandler;
    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, CustomAuthenticationEntryPoint unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic()
            .authenticationEntryPoint(unauthorizedHandler) // !!!
            .and()
            .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
            .and()
            .authorizeRequests()
            .antMatchers("/h2-console/**").permitAll()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()

            // ADMINISTRATOR
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/api/admin/user").hasAnyRole("ADMINISTRATOR")
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasAnyRole("ADMINISTRATOR")
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.PUT, "/api/admin/user/role", "/api/admin/user/access").hasAnyRole("ADMINISTRATOR")

            // AUDITOR
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/api/security/events").hasAnyRole("AUDITOR")

            // ACCOUNTANT
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.POST, "/api/acct/payments").hasAnyRole("ACCOUNTANT")
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.PUT, "/api/acct/payments").hasAnyRole("ACCOUNTANT")

            // USER, ACCOUNTANT
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")

            // USER, ACCOUNTANT, ADMINISTRATOR
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.POST, "api/auth/changepass").hasAnyRole("USER", "ACCOUNTANT", "ADMINISTRATOR")

            // ANONYMOUS, USER, ACCOUNTANT, ADMINISTRATOR
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()

            .and()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session

            .and()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }
}
