package vn.fs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import vn.fs.commom.CustomOAuth2UserService;
import vn.fs.service.impl.UserDetailService;



/**
 * @author DongTHD
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private UserDetailService userDetailService;

	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
	}
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		
		// Admin page
		http.authorizeRequests().antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')");
		
		// If you are not logged in, you will be redirected to the /login page.
		http.authorizeRequests().antMatchers("/checkout").access("hasRole('ROLE_USER')");
		http.authorizeRequests().antMatchers("/addToCart").access("hasRole('ROLE_USER')");
		
		http.authorizeRequests()
		    .antMatchers("/oauth2/**").permitAll()
			.antMatchers("/**").permitAll()
			.anyRequest().authenticated()
			.and()
			.jee().mappableRoles("ROLE_USER", "ROLE_ADMIN");
		http.formLogin()
			.loginProcessingUrl("/doLogin")
			.loginPage("/login")
			.defaultSuccessUrl("/?login_success")
			.successHandler(new SuccessHandler()).failureUrl("/login?error=true")
			.failureUrl("/login?error=true")
			.permitAll()
			.and()
		.oauth2Login()
			.loginPage("/login")
			.userInfoEndpoint()
			.userService(oauth2UserService)
			.and()
			.successHandler(oauthLoginSuccessHandler)
			.and()
		.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.deleteCookies("JSESSIONID")
			.logoutSuccessUrl("/?logout_success")
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.deleteCookies("JSESSIONID")
			.permitAll()
			.and();
		http.sessionManagement().maximumSessions(1).expiredUrl("/login?sessionTimeout");
		 // remember-me
		http.rememberMe()
			.rememberMeParameter("remember");
		
	}
	@Autowired
    private CustomOAuth2UserService oauth2UserService;
     
    @Autowired
    private OAuthLoginSuccessHandler oauthLoginSuccessHandler;
     
    @Autowired
    private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
}
