package es.udc.pcv.backend.rest.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private JwtGenerator jwtGenerator;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.requiresChannel()
			.requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
			.requiresSecure()
			.and()
			.cors().and().csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.addFilter(new JwtFilter(authenticationManager(), jwtGenerator))
			.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/healthz").permitAll()
			.antMatchers(HttpMethod.POST, "/users").permitAll()
			.antMatchers(HttpMethod.POST, "/users/login").permitAll()
			.antMatchers(HttpMethod.OPTIONS, "/users/login").permitAll()
			.antMatchers(HttpMethod.GET, "/users/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST, "/users/loginFromServiceToken").permitAll()
			.antMatchers(HttpMethod.POST, "/users/newPasswordByTemporallyToken").permitAll()
			.antMatchers(HttpMethod.POST,"/users/recoveryEmail").permitAll()
			.antMatchers(HttpMethod.PUT, "/users/*").hasAnyRole("ADMIN","REPRESENTATIVE","USER")
			.antMatchers(HttpMethod.PUT,"/users/*/volunteer").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/users/*/volunteer").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST, "/users/*/password").hasRole("USER")
			.antMatchers(HttpMethod.POST, "/admin/representatives").hasRole("ADMIN")
			.antMatchers(HttpMethod.POST, "/admin/entities").hasRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/admin/entities").hasAnyRole("ADMIN","REPRESENTATIVE","USER")
			.antMatchers(HttpMethod.PATCH,"/admin/projects/*/ods").hasRole("ADMIN")
			.antMatchers(HttpMethod.DELETE,"/admin/users").hasRole("ADMIN")
			.antMatchers(HttpMethod.DELETE,"/admin/users/*").hasRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/admin/entities/myEntity").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/projects").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.PUT,"/projects").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/projects/summaryOdsAndCollaborationArea").permitAll()
			.antMatchers(HttpMethod.GET,"/projects").permitAll()
			.antMatchers(HttpMethod.GET,"/projects/*").permitAll()
			.antMatchers(HttpMethod.DELETE,"/projects/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/entities/*/logo").permitAll()
			.antMatchers(HttpMethod.GET,"/entities/*/agreementFile").hasAnyRole("ADMIN","REPRESENTATIVE","USER")
			.antMatchers(HttpMethod.GET,"/entities/*").permitAll()
			.antMatchers(HttpMethod.GET,"/entities/entitiesSelector").permitAll()
			.antMatchers(HttpMethod.POST,"/entities/*/update").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/projects/participation").hasAnyRole("USER")
			.antMatchers(HttpMethod.POST,"/projects/representative/participation").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET, "/participation/my").hasAnyRole("ADMIN","REPRESENTATIVE","USER")
			.antMatchers(HttpMethod.GET,"/projects/myEntityProjects").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/projects/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/pendingParticipation").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/projects").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/participation/hourRegisters").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.DELETE,"/participation/hourRegisters/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/hourRegisters").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/projects/*/participation").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.PATCH, "/participation/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/participation/certFiles").hasRole("USER")
			.antMatchers(HttpMethod.POST,"/participation/certFiles/representative").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/projects/*/totalHours/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/users/*/volunteerDoc").hasRole("USER")
			.antMatchers(HttpMethod.POST,"/users/*/representative/volunteerDoc").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/users/*/representative/volunteerDoc").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/users/volunteer").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET, "/users/representative/myVolunteers").hasAnyRole("ADMIN","REPRESENTATIVE")
			.anyRequest().denyAll(); // permitAll allows OpenAPI generated docs access

	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
		CorsConfiguration config = new CorsConfiguration();
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		config.setAllowCredentials(true);
	    config.setAllowedOriginPatterns(Arrays.asList("*"));
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("*");
		config.addExposedHeader("Content-Disposition");

	    source.registerCorsConfiguration("/**", config);
	    
	    return source;
	    
	 }

}
