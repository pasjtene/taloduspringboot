package com.talodu.taloduspringboot.configuration;

import com.talodu.taloduspringboot.exception.AccessDeniedExceptionFilter;
import com.talodu.taloduspringboot.exception.CustomAccessDeniedHandler;
import com.talodu.taloduspringboot.jwtutil.JwtRequestFilter;
import com.talodu.taloduspringboot.services.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;


import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    RestAuthenticationFailureHandler restAuthenticationFailureHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
              // "/api/users",
                "/api/register",
                "/api/authenticate",
                "/api/server/**"
                //"/talodu/api/users",
                //"/talodu/api/register",
               //"/api/token/refresh"
               // "/talodu/api/uploadfile",
                //"/api/uploadfile"
        );
    }

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AccessDeniedExceptionFilter accessDeniedExceptionFilter;

    @Autowired
    private CustomAuthorizationFilter customAuthorizationFilter;

    //@Autowired
    //private final   PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private  MyUserDetailService myUserDetailService;



    @Override
    protected void configure(HttpSecurity http) throws Exception {
     //CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
              //

       //customAuthenticationFilter.setFilterProcessesUrl("/api/authenticate");

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000","http://51.68.196.188"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        http.csrf().disable();


        http.cors().configurationSource(request -> corsConfiguration)
                .and().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/authenticate").permitAll()
                .antMatchers(HttpMethod.POST, "/api/register").permitAll()
                .antMatchers(HttpMethod.POST, "/talodu/api/register").permitAll()
                .antMatchers(HttpMethod.GET, "/api/token/refresh").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
       // http.addFilterAfter(accessDeniedExceptionFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        //http.addFilter(accessDeniedExceptionFilter);


        //http.addFilter(customAuthenticationFilter);

        //http.addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);



    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://51.68.196.188",
                "http://localhost:4200"
                ));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        //return PlainTextPasswordEncoder.getInstance();
        //return
        return new BCryptPasswordEncoder();
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
        //return configuration.getAuthenticationManager();
    }

}
