package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
@Component
@ComponentScan
public class MyConfigurations  {

	
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }


	@Bean
	public UserDetailsService userDetailsService() { 
		return new CustomeUserDetailsService();
		}


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
//
//	@Bean
//	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//		return  config.getAuthenticationManager();
//	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests()

		.requestMatchers("/admin/**")
		.hasRole("ADMIN")

		.requestMatchers("/user/**")
		.hasRole("USER")

		.requestMatchers("/**").permitAll().and().formLogin()
		.loginPage("/userlogin")
		.loginProcessingUrl("//signin")
		.defaultSuccessUrl("/user/index")
//		.failureUrl("/login-fail")
		.and().csrf().disable();

        return http.build();
	}

}
