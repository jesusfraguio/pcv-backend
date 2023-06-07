package es.udc.pcv.backend.rest.controllers;

import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.rest.dtos.NewPasswordParamsDto;
import es.udc.pcv.backend.rest.dtos.UserConversor;
import es.udc.pcv.backend.rest.dtos.VolunteerEntityFilesDto;
import es.udc.pcv.backend.rest.dtos.VolunteerSummaryDto;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.exceptions.IncorrectLoginException;
import es.udc.pcv.backend.model.exceptions.IncorrectPasswordException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.services.UserService;
import es.udc.pcv.backend.rest.common.ErrorsDto;
import es.udc.pcv.backend.rest.common.JwtGenerator;
import es.udc.pcv.backend.rest.common.JwtInfo;
import es.udc.pcv.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.pcv.backend.rest.dtos.ChangePasswordParamsDto;
import es.udc.pcv.backend.rest.dtos.LoginParamsDto;
import es.udc.pcv.backend.rest.dtos.UserDto;

@RestController
@RequestMapping("/users")
@Tag(name = "users")
public class UserController {
	
	private final static String INCORRECT_LOGIN_EXCEPTION_CODE = "project.exceptions.IncorrectLoginException";
	private final static String INCORRECT_PASSWORD_EXCEPTION_CODE = "project.exceptions.IncorrectPasswordException";
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private JwtGenerator jwtGenerator;
	
	@Autowired
	private UserService userService;

	@Autowired
	private UserConversor userConversor;
	
	@ExceptionHandler(IncorrectLoginException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorsDto handleIncorrectLoginException(IncorrectLoginException exception, Locale locale) {
		
		String errorMessage = messageSource.getMessage(INCORRECT_LOGIN_EXCEPTION_CODE, null,
				INCORRECT_LOGIN_EXCEPTION_CODE, locale);

		return new ErrorsDto(errorMessage);
		
	}
	
	@ExceptionHandler(IncorrectPasswordException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorsDto handleIncorrectPasswordException(IncorrectPasswordException exception, Locale locale) {
		
		String errorMessage = messageSource.getMessage(INCORRECT_PASSWORD_EXCEPTION_CODE, null,
				INCORRECT_PASSWORD_EXCEPTION_CODE, locale);

		return new ErrorsDto(errorMessage);
		
	}

	@Operation(summary = "sign up")
	@PostMapping("/signUp")
	public ResponseEntity<AuthenticatedUserDto> signUp(
		@Validated({UserDto.AllValidations.class}) @RequestBody UserDto userDto) throws DuplicateInstanceException {
		
		User user = userConversor.toUser(userDto);
		Volunteer volunteerData = userConversor.toVolunteer(userDto);
		UserWithVolunteer userWithVolunteer = new UserWithVolunteer(user,volunteerData);
		userService.signUp(userWithVolunteer);

		URI location = ServletUriComponentsBuilder
			.fromCurrentRequest().path("/{id}")
			.buildAndExpand(user.getId()).toUri();
	
		return ResponseEntity.created(location).body(userConversor.toAuthenticatedUserDto(generateServiceToken(user), user));

	}
	@Operation(summary = "login")
	@PostMapping("/login")
	public AuthenticatedUserDto login(@Validated @RequestBody LoginParamsDto params)
		throws IncorrectLoginException {
		
		User user = userService.login(params.getUserName(), params.getPassword());
			
		return userConversor.toAuthenticatedUserDto(generateServiceToken(user), user);
		
	}
	
	@PostMapping("/loginFromServiceToken")
	public AuthenticatedUserDto loginFromServiceToken(@RequestAttribute Long userId, 
		@RequestAttribute String serviceToken) throws InstanceNotFoundException {
		
		User user = userService.loginFromId(userId);
		
		return userConversor.toAuthenticatedUserDto(serviceToken, user);
		
	}

	@PutMapping("/{id}")
	public UserDto updateProfile(@RequestAttribute Long userId, @PathVariable Long id,
		@Validated({UserDto.UpdateValidations.class}) @RequestBody UserDto userDto) 
		throws InstanceNotFoundException, PermissionException {
				
		if (!id.equals(userId)) {
			throw new PermissionException();
		}
		UserWithVolunteer userWithVolunteer = userService.updateProfile(id, userDto.getName(), userDto.getSurname(),
				userDto.getEmail());
		return userConversor.toUserDto(userWithVolunteer.getUser(),userWithVolunteer.getVolunteer());
		
	}
	@Operation(summary = "Update my certificate file (user)")
	@RequestMapping(value = "/update-my-doc/{id}", method = RequestMethod.POST,  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public boolean addCertFile(@RequestAttribute Long userId, @PathVariable Long id, @RequestPart(name="dni",required = false)
									   MultipartFile dni, @RequestPart(name="harassmentCert", required = false)
										   MultipartFile harassment)
			throws IOException, PermissionException, InstanceNotFoundException {

		if (!id.equals(userId)) {
			throw new PermissionException();
		}

		if(dni!= null){
			userService.updateDNI(userId,dni);
		}
		else if(harassment!=null){
			userService.updateHarassmentCert(userId,harassment);
		}
		return true;

	}

	@GetMapping("/{id}")
	public VolunteerSummaryDto getUserSummaryProfile(@RequestAttribute Long userId, @PathVariable Long id) throws InstanceNotFoundException{
		UserWithVolunteer userWithVolunteer = userService.getSummaryProfile(userId,id);
		VolunteerEntityFilesDto hasFiles = userService.findVolunteerEntityFiles(userId, id);
		return userConversor.toUserSummaryDto(userWithVolunteer.getUser(),userWithVolunteer.getVolunteer(), hasFiles.isHasHarassmentFile(), hasFiles.isHasCertFile());
	}
	
	@PostMapping("/{id}/changePassword")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(@RequestAttribute Long userId, @PathVariable Long id,
		@Validated @RequestBody ChangePasswordParamsDto params)
		throws PermissionException, InstanceNotFoundException, IncorrectPasswordException {
		
		if (!id.equals(userId)) {
			throw new PermissionException();
		}
		
		userService.changePassword(id, params.getOldPassword(), params.getNewPassword());
		
	}
	@PostMapping("/newPasswordByTemporallyToken")
	public AuthenticatedUserDto createPasswordByToken(@RequestAttribute Long userId, @Validated @RequestBody
			NewPasswordParamsDto params)
			throws InstanceNotFoundException {

		User user = userService.addNewPassword(userId,params.getNewPassword());

		return userConversor.toAuthenticatedUserDto(generateServiceToken(user), user);

	}

	private String generateServiceToken(User user) {
		
		JwtInfo jwtInfo = new JwtInfo(user.getId(), user.getEmail(), user.getRole().toString());
		
		return jwtGenerator.generate(jwtInfo);
		
	}
	
}
