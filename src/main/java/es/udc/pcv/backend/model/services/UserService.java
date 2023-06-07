package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.IncorrectLoginException;
import es.udc.pcv.backend.model.exceptions.IncorrectPasswordException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.rest.dtos.VolunteerEntityFilesDto;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
	
	void signUp(UserWithVolunteer userWithVolunteer) throws DuplicateInstanceException;
	
	User login(String userName, String password) throws IncorrectLoginException;
	
	User loginFromId(Long id) throws InstanceNotFoundException;

	UserWithVolunteer updateProfile(Long id, String firstName, String lastName, String email) throws InstanceNotFoundException;
	
	void changePassword(Long id, String oldPassword, String newPassword)
		throws InstanceNotFoundException, IncorrectPasswordException;

	Representative createRepresentative(UserWithRepresentative userWithRepresentative)
        throws DuplicateInstanceException, InstanceNotFoundException;

	void sendEmailWithToken(User user, String token);

	User addNewPassword(Long id, String newPassword) throws InstanceNotFoundException;

	UserWithVolunteer getSummaryProfile(Long representativeId, Long userId) throws InstanceNotFoundException;

  	VolunteerEntityFilesDto findVolunteerEntityFiles(Long representativeId, Long id) throws InstanceNotFoundException;

	File updateDNI(Long userId, MultipartFile dni) throws IOException, InstanceNotFoundException;

	File updateHarassmentCert(Long userId, MultipartFile harassmentCert)
			throws InstanceNotFoundException, IOException;
}
