package es.udc.pcv.backend.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.IncorrectLoginException;
import es.udc.pcv.backend.model.exceptions.IncorrectPasswordException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.exceptions.UnauthorizedException;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.services.UserService;
import es.udc.pcv.backend.model.to.ResourceWithType;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.rest.common.ErrorsDto;
import es.udc.pcv.backend.rest.common.JwtGenerator;
import es.udc.pcv.backend.rest.common.JwtInfo;
import es.udc.pcv.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.pcv.backend.rest.dtos.ChangePasswordParamsDto;
import es.udc.pcv.backend.rest.dtos.EmailDto;
import es.udc.pcv.backend.rest.dtos.LoginParamsDto;
import es.udc.pcv.backend.rest.dtos.NewPasswordParamsDto;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.UserConversor;
import es.udc.pcv.backend.rest.dtos.UserDto;
import es.udc.pcv.backend.rest.dtos.VolunteerDataDto;
import es.udc.pcv.backend.rest.dtos.VolunteerEntityFilesDto;
import es.udc.pcv.backend.rest.dtos.VolunteerSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	private RepresentativeService representativeService;

	@Autowired
	private UserConversor userConversor;

	@Autowired
	private ObjectMapper objectMapper;
	
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

	@Operation(summary = "sign up as volunteer")
	@PostMapping("")
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

	@Operation(summary = "create a volunteer by a representative")
	@RequestMapping(value = "/volunteer", method = RequestMethod.POST,  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Long> createVolunteer(@RequestAttribute Long userId,
			@RequestParam String volunteerDataDto, @RequestPart(name="dni",required = false)
			MultipartFile dni, @RequestPart(name="harassmentCert",required = false) MultipartFile harassmentCert,
			@RequestPart(name="cert",required = true) MultipartFile cert)
			throws IOException, InstanceNotFoundException {

		Volunteer volunteerData = userConversor.toVolunteer(objectMapper.readValue(volunteerDataDto,VolunteerDataDto.class));
		Volunteer volunteer = userService.createVolunteer(userId, volunteerData, cert);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(volunteer.getId()).toUri();

		if(dni != null){
			representativeService.updateVolunteerDNI(userId,volunteer.getId(),dni);
		}
		if(harassmentCert != null){
			representativeService.updateVolunteerHarassmentCert(userId,volunteer.getId(),harassmentCert);
		}

		return ResponseEntity.created(location).body(volunteer.getId());

	}

	@Operation(summary = "representative might upload any volunteer doc")
	@RequestMapping(value = "{id}/representative/volunteerDoc", method = RequestMethod.POST,  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Boolean> updateMyVolunteer(@RequestAttribute Long userId, @PathVariable Long id,
													 @RequestPart(name="dni",required = false) MultipartFile dni,
													 @RequestPart(name="harassmentCert",required = false) MultipartFile harassmentCert,
													 @RequestPart(name="photo",required = false) MultipartFile photo)
			throws IOException, InstanceNotFoundException{

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(id).toUri();

		if(dni != null){
			representativeService.updateVolunteerDNI(userId,id,dni);
		}
		if(harassmentCert != null){
			representativeService.updateVolunteerHarassmentCert(userId,id,harassmentCert);
		}
		if (photo != null){
			representativeService.uploadVolunteerPhoto(userId,id,photo);
		}

		return ResponseEntity.created(location).body(true);

	}

	@Operation(summary = "representative might download any volunteer doc",description = "Request param fileType (string) is required with one of the next values: HARASSMENT_CERT, DNI, AGREEMENT_FILE_SIGNED_BY_BOTH, PHOTO")
	@GetMapping(value = "/{id}/representative/volunteerDoc")
	public ResponseEntity<Resource> downloadVolunteerFile(@RequestAttribute Long userId, @PathVariable Long id, @RequestParam(name = "fileType") String fileType)
		throws InstanceNotFoundException {

		ResourceWithType resource = representativeService.getVolunteerFile(userId,id,fileType);
		MediaType mediaType;
		if(resource.getExtension().equals("pdf")){
			mediaType = MediaType.APPLICATION_PDF;
		}
		else if(resource.getExtension().equals("png")){
			mediaType = MediaType.IMAGE_PNG;
		}
		else if(resource.getExtension().equals("gif")){
			mediaType = MediaType.IMAGE_GIF;
		}
		else mediaType = MediaType.IMAGE_JPEG;
		return ResponseEntity.ok()
				.contentType(mediaType)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getResource().getFilename() + "\"")
				.body(resource.getResource());

	}

	@Operation(summary = "login")
	@PostMapping("/login")
	public AuthenticatedUserDto login(@Validated @RequestBody LoginParamsDto params)
			throws IncorrectLoginException, InstanceNotFoundException {
		
		User user = userService.login(params.getUserName(), params.getPassword());
		if(user.getRole()== User.RoleType.USER){
			UserWithVolunteer profile = userService.getMySummaryProfile(user.getId());
			return userConversor.toAuthenticatedUserDto(generateServiceToken(user), user, profile.getVolunteer());
		}
		else{
			Representative representative = userService.getMySummaryProfileRep(user.getId());
			return userConversor.toAuthenticatedUserDto(generateServiceToken(user), user, representative);
		}
		
	}
	
	@PostMapping("/loginFromServiceToken")
	public AuthenticatedUserDto loginFromServiceToken(@RequestAttribute Long userId, 
		@RequestAttribute String serviceToken) throws InstanceNotFoundException {
		
		User user = userService.loginFromId(userId);
		if(user.getRole()== User.RoleType.USER){
			UserWithVolunteer profile = userService.getMySummaryProfile(user.getId());
			return userConversor.toAuthenticatedUserDto(generateServiceToken(user), user, profile.getVolunteer());
		}
		else{
			Representative representative = userService.getMySummaryProfileRep(user.getId());
			return userConversor.toAuthenticatedUserDto(serviceToken, user, representative);
		}
		
	}

	@PutMapping("/{id}")
	public UserDto updateProfile(@RequestAttribute Long userId, @PathVariable Long id,
		@Validated({UserDto.UpdateValidations.class}) @RequestBody UserDto userDto) 
		throws InstanceNotFoundException, PermissionException {
				
		if (!id.equals(userId)) {
			throw new PermissionException();
		}
		return userService.updateProfile(id, userDto.getName(), userDto.getSurname(),
				userDto.getEmail(), userDto.getPhone());
		
	}
	@Operation(summary = "updates volunteer's data that are collaborating with my entity")
	@PutMapping("/{id}/volunteer")
	public UserDto updateVolunteerProfile(@RequestAttribute Long userId, @PathVariable Long id,
								 @Validated({UserDto.UpdateValidations.class}) @RequestBody UserDto userDto)
			throws InstanceNotFoundException, PermissionException, DuplicateInstanceException {

		UserWithVolunteer userWithVolunteer = userService.updateVolunteerProfile(userId, id, userDto);
		return userConversor.toUserDto(userWithVolunteer.getUser(),userWithVolunteer.getVolunteer());

	}
	@Operation(summary = "Uploads a new doc file of volunteer (user)")
	@RequestMapping(value = "/{id}/volunteerDoc", method = RequestMethod.POST,  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
		if(harassment!=null){
			userService.updateHarassmentCert(userId,harassment);
		}
		return true;

	}

	@GetMapping("/{id}")
	public VolunteerSummaryDto getUserSummaryProfile(@RequestAttribute Long userId, @PathVariable Long id) throws InstanceNotFoundException{
		UserWithVolunteer userWithVolunteer = userService.getSummaryProfile(userId,id);
		VolunteerEntityFilesDto hasFiles = userService.findVolunteerEntityFiles(userId, id);
		return userConversor.toUserSummaryDto(userWithVolunteer.getUser(),userWithVolunteer.getVolunteer(), hasFiles.isHasHarassmentFile(),
				hasFiles.isHasCertFile(), hasFiles.isHasDniFile(), hasFiles.isHasPhoto());
	}

	@Operation(summary = "gets full volunteer's data susceptible of being modified")
	@GetMapping("/{id}/volunteer")
	public UserDto getUserFullProfile(@RequestAttribute Long userId, @PathVariable Long id) throws InstanceNotFoundException{
		UserWithVolunteer userWithVolunteer = userService.getSummaryProfile(userId,id);
		return userConversor.toUserFullDto(userWithVolunteer.getUser(),userWithVolunteer.getVolunteer());
	}

	@Operation(summary = "Get a block of entity's volunteers")
	@GetMapping("/representative/myVolunteers")
	public Block<VolunteerSummaryDto> findMyVolunteers(@RequestAttribute Long userId,
													   @RequestParam(defaultValue = "0") int page,
													   @RequestParam(defaultValue = "10") int size,
													   @RequestParam(required = false) String sortValue,
													   @RequestParam(required = false) String sortOrder)
			throws InstanceNotFoundException {
		PageableDto pageableDto = new PageableDto(page,size,sortValue,sortOrder);
		return userConversor.toVolunteerSummaryBlockDto(representativeService.findMyEntityVolunteers(userId,pageableDto));
	}
	
	@PostMapping("/{id}/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(@RequestAttribute Long userId, @PathVariable Long id,
		@Validated @RequestBody ChangePasswordParamsDto params)
		throws PermissionException, InstanceNotFoundException, IncorrectPasswordException {
		
		if (!id.equals(userId)) {
			throw new PermissionException();
		}
		
		userService.changePassword(id, params.getOldPassword(), params.getNewPassword());
		
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Void> handleUnauthorizedException() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	@PostMapping("/newPasswordByTemporallyToken")
	public AuthenticatedUserDto createPasswordByToken(@RequestAttribute Long userId, @Validated @RequestBody
			NewPasswordParamsDto params)
			throws InstanceNotFoundException {
		if (userService.checkIfUserIsDeleted(userId)) {
			throw new UnauthorizedException();
		}
		User user = userService.addNewPassword(userId,params.getNewPassword());

		return userConversor.toAuthenticatedUserDto(generateServiceToken(user), user);

	}

	@Operation(summary = "sends e-mail with recovery token if it is registered")
	@PostMapping("/recoveryEmail")
	public boolean sendRecoverEmail( @RequestBody @Validated EmailDto emailDto) {
		userService.findByEmail(emailDto.getEmail()).map(user -> {
			if (userService.checkIfUserIsDeleted(user.getId())) {
				return true;
			}
			String token = generateServiceToken(user);
			userService.sendEmailWithToken(user, token);
			return true;
		});
		return true; // for security measures to don't leak if an email is registered, true is returned as well
	//TODO If you want that the most advanced hacker don't know if an account exists an async thread should be opened because of time sending email requires
	}

	private String generateServiceToken(User user) {
		
		JwtInfo jwtInfo = new JwtInfo(user.getId(), user.getEmail(), user.getRole().toString());
		
		return jwtGenerator.generate(jwtInfo);
		
	}
	
}
