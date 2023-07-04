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
			.antMatchers(HttpMethod.POST, "/users/signUp").permitAll()
			.antMatchers(HttpMethod.POST, "/users/login").permitAll()
			.antMatchers(HttpMethod.OPTIONS, "/users/login").permitAll()
			.antMatchers(HttpMethod.GET, "/users/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST, "/users/loginFromServiceToken").permitAll()
			.antMatchers(HttpMethod.POST, "/users/newPasswordByTemporallyToken").permitAll()
			.antMatchers(HttpMethod.PUT, "/users/*").hasRole("USER")
			.antMatchers(HttpMethod.POST, "/users/*/changePassword").hasRole("USER")
			.antMatchers(HttpMethod.POST, "/admin/createRepresentative").hasRole("ADMIN")
			.antMatchers(HttpMethod.POST, "/admin/createEntity").hasRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/admin/getEntities").hasAnyRole("ADMIN","REPRESENTATIVE","USER")
			.antMatchers(HttpMethod.POST,"/projects/createProject").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/projects/updateProject").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/projects/getSummaryOdsAndCollaborationArea").permitAll()
			.antMatchers(HttpMethod.GET,"/projects/searchProjectsBy").permitAll()
			.antMatchers(HttpMethod.GET,"/projects/project/*").permitAll()
			.antMatchers(HttpMethod.GET,"/projects/getLogo").permitAll()
			.antMatchers(HttpMethod.GET,"/projects/getAgreementFile/*").hasAnyRole("ADMIN","REPRESENTATIVE","USER")
			.antMatchers(HttpMethod.POST,"/projects/createMyParticipation").hasAnyRole("USER")
			.antMatchers(HttpMethod.POST,"/projects/representative/createParticipation").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET, "/admin/getMyEntity").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET, "/participation/my").hasAnyRole("ADMIN","REPRESENTATIVE","USER")
			.antMatchers(HttpMethod.GET,"/projects/myEntityProjects").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/getAllProjectParticipations/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/getAllPendingParticipations").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/all-my-projects").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/participation/createHourRegister").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/getAllRegisteredHours").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/participation/project/*/participation").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.PATCH, "/participation/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/participation/addCertFile").hasRole("USER")
			.antMatchers(HttpMethod.POST,"/participation/representative/addCertFile").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/users/update-my-doc/*").hasRole("USER")
			.antMatchers(HttpMethod.POST,"/users/representative/updateVolunteerDoc/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET,"/users/representative/downloadVolunteerDoc/*").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.POST,"/users/createVolunteer").hasAnyRole("ADMIN","REPRESENTATIVE")
			.antMatchers(HttpMethod.GET, "/users/representative/findMyVolunteers").hasAnyRole("ADMIN","REPRESENTATIVE")
			.anyRequest().denyAll();

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
