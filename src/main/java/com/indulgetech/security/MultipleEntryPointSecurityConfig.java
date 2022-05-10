package com.indulgetech.security;


import com.indulgetech.security.admin.AdminUserDetailsService;
import com.indulgetech.security.client.ClientUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class MultipleEntryPointSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * For client login
     */
    @Configuration
    @Order(1)
    public static class ClientWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        String path = "/api/v1/client-users";

        @Autowired
        private JwtFilter jwtFilter;
        
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        ClientUserDetailsService userDetailsService;

        private final String[] AUTH_WHITELIST = {
                path+"/login",
                path+"/sign-up",
                path+"/create-password/**",
                path+"/refresh-token"
        };

        @Override
        public void configure(WebSecurity web) {
        }

        public ClientWebSecurityConfigurerAdapter() {
            super();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors();
            http.csrf().disable().antMatcher(path+"/**").authorizeRequests()
                    .antMatchers(AUTH_WHITELIST).permitAll()
                    .anyRequest().authenticated().and().exceptionHandling().and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }

        @Bean("clientAuthenticationManager")
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        @Autowired
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }
    }
    
    /*
     * For admin login
     */
    @Configuration
    @Order(2)
    public static class AdminWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        String path = "/api/v1/admin-users";

        @Autowired
        private JwtFilter jwtFilter;
        
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        AdminUserDetailsService userDetailsService;


        private final String[] AUTH_WHITELIST = {
                path+"/login",
                path+"/refresh-token"
        };

        @Override
        public void configure(WebSecurity web) {

        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors();
            http.csrf().disable().antMatcher(path+"/**").authorizeRequests()
                    .antMatchers(AUTH_WHITELIST).permitAll()
                    .anyRequest().authenticated().and().exceptionHandling().and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }

        @Bean("adminAuthenticationManager")
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        @Autowired
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }

    }

    /*
     * For all routes
     */
    @Configuration
    @Order(3)
    public static class OtherWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

//        @Value("${api.basepath}")
        String path = "/api/v1";

        @Autowired
        private JwtFilter jwtFilter;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        ClientUserDetailsService userDetailsService;

        private final String[] AUTH_WHITELIST = {
              //  "/uploads/**",
                path+"/forms/**",
                path+"/forgot-password/**",
                path+"/reset-password/**",
                path+"/reference/**",
//                path+"/engineerProfile/**",
                path+"/health/**",
//                path+"/login",
//                path+"/client-users/sign-up"
                // "/static/**",
               // "/images/**",
               // "/manifest.json"
        };

        @Override
        public void configure(WebSecurity web) {
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors();
            http.csrf().disable().antMatcher(path+"/**").authorizeRequests()
                    .antMatchers(AUTH_WHITELIST).permitAll()
                    .anyRequest().authenticated().and().exceptionHandling().and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }

        @Bean("defaultAuthenticationManager")
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        @Autowired
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }
    }



    @Configuration
    @Order(5)
    public static class ApiDocWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser("user").password(passwordEncoder.encode("fellowship#")).roles();
        }

        private static final String[] AUTH_LIST = {
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"
        };

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests().antMatchers(AUTH_LIST).authenticated()
                    .and()
                    .httpBasic().authenticationEntryPoint(swaggerAuthenticationEntryPoint())
                    .and()
                    .csrf().disable();
        }

        @Bean
        public BasicAuthenticationEntryPoint swaggerAuthenticationEntryPoint() {
            BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
            entryPoint.setRealmName("Swagger Realm");
            return entryPoint;
        }
    }
}