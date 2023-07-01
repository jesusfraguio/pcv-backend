package es.udc.pcv.backend.rest.common;

import es.udc.pcv.backend.model.exceptions.AlreadyParticipatingException;
import es.udc.pcv.backend.model.exceptions.IncorrectLoginException;
import es.udc.pcv.backend.model.exceptions.InvalidStatusTransitionException;
import es.udc.pcv.backend.model.exceptions.ParticipationIsInDateException;
import es.udc.pcv.backend.model.exceptions.ProjectIsPausedException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.PermissionException;

@ControllerAdvice
public class CommonControllerAdvice {
	
	private final static String INSTANCE_NOT_FOUND_EXCEPTION_CODE = "project.exceptions.InstanceNotFoundException";
	private final static String DUPLICATE_INSTANCE_EXCEPTION_CODE = "project.exceptions.DuplicateInstanceException";
	private final static String PERMISSION_EXCEPTION_CODE = "project.exceptions.PermissionException";
	private final static String ALREADY_PARTICIPATING_EXCEPTION_CODE = "project.exceptions.AlreadyParticipatingException";
	private final static String PROJECT_IS_PAUSED_EXCEPTION_CODE = "project.exceptions.ProjectIsPausedException";
	private final static String PARTICIPATION_IS_IN_DATE_EXCEPTION = "project.exceptions.ParticipationIsInDateException";
	private final static String INVALID_STATUS_TRANSITION_EXCEPTION_CODE ="project.exceptions.InvalidStatusTransitionException";
	private final static String INVALID_STATUS_TRANSITION_BECAUSE_REQUIRED_FILE_EXCEPTION_CODE ="project.exceptions.InvalidStatusTransitionRequiredFileException";
	
	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(InvalidStatusTransitionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorsDto handleInvalidStatusTransitionException(InvalidStatusTransitionException exception, Locale locale) {
		String errorMessage;
		if(exception.getRequiredFileName() != null){
			errorMessage = messageSource.getMessage(INVALID_STATUS_TRANSITION_BECAUSE_REQUIRED_FILE_EXCEPTION_CODE,
					new Object[] { exception.getRequiredFileName() },
					INVALID_STATUS_TRANSITION_BECAUSE_REQUIRED_FILE_EXCEPTION_CODE, locale
				);
		}
		else {
			errorMessage = messageSource.getMessage(INVALID_STATUS_TRANSITION_EXCEPTION_CODE, null,
					INVALID_STATUS_TRANSITION_EXCEPTION_CODE, locale);
		}

		return new ErrorsDto(errorMessage);

	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorsDto handleIllegalArgumentException(IllegalArgumentException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(exception.getMessage(), null, exception.getMessage(), locale);

		return new ErrorsDto(errorMessage);

	}

	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorsDto handleIOException(IOException exception, Locale locale) {
		String errorMessage = messageSource.getMessage("io.exception", null, "An I/O error occurred", locale);
		return new ErrorsDto(errorMessage);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorsDto handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
				
		List<FieldErrorDto> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
			.map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage())).collect(Collectors.toList());
		
		return new ErrorsDto(fieldErrors);
	    
	}
	
	@ExceptionHandler(InstanceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorsDto handleInstanceNotFoundException(InstanceNotFoundException exception, Locale locale) {

		String nameMessage = messageSource.getMessage(exception.getName(), null, exception.getName(), locale);
		String errorMessage = messageSource.getMessage(INSTANCE_NOT_FOUND_EXCEPTION_CODE,
				new Object[] {nameMessage, exception.getKey().toString()}, INSTANCE_NOT_FOUND_EXCEPTION_CODE, locale);

		return new ErrorsDto(errorMessage);
		
	}
	
	@ExceptionHandler(DuplicateInstanceException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorsDto handleDuplicateInstanceException(DuplicateInstanceException exception, Locale locale) {
		
		String nameMessage = messageSource.getMessage(exception.getName(), null, exception.getName(), locale);
		String errorMessage = messageSource.getMessage(DUPLICATE_INSTANCE_EXCEPTION_CODE, 
				new Object[] {nameMessage, exception.getKey().toString()}, DUPLICATE_INSTANCE_EXCEPTION_CODE, locale);

		return new ErrorsDto(errorMessage);
		
	}
	
	@ExceptionHandler(PermissionException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorsDto handlePermissionException(PermissionException exception, Locale locale) {
		
		String errorMessage = messageSource.getMessage(PERMISSION_EXCEPTION_CODE, null, PERMISSION_EXCEPTION_CODE,
			locale);

		return new ErrorsDto(errorMessage);
		
	}

	@ExceptionHandler(AlreadyParticipatingException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public ErrorsDto handleAlreadyParticipatingException(AlreadyParticipatingException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(ALREADY_PARTICIPATING_EXCEPTION_CODE, null, ALREADY_PARTICIPATING_EXCEPTION_CODE,
				locale);

		return new ErrorsDto(errorMessage);

	}

	@ExceptionHandler(ProjectIsPausedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public ErrorsDto handleProjectIsPausedException(ProjectIsPausedException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(PROJECT_IS_PAUSED_EXCEPTION_CODE, null, PROJECT_IS_PAUSED_EXCEPTION_CODE,
				locale);

		return new ErrorsDto(errorMessage);

	}

	@ExceptionHandler(ParticipationIsInDateException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public ErrorsDto handleParticipationIsInDateException(ParticipationIsInDateException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(PARTICIPATION_IS_IN_DATE_EXCEPTION, null, PARTICIPATION_IS_IN_DATE_EXCEPTION,
				locale);

		return new ErrorsDto(errorMessage);

	}

}
