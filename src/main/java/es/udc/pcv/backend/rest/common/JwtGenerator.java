package es.udc.pcv.backend.rest.common;

public interface JwtGenerator {

	String generateLowExpiration(JwtInfo info);

	String generate(JwtInfo info);
	
	JwtInfo getInfo(String token);

}
