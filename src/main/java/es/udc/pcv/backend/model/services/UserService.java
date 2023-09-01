package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.IncorrectLoginException;
import es.udc.pcv.backend.model.exceptions.IncorrectPasswordException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.rest.dtos.UserDto;
import es.udc.pcv.backend.rest.dtos.VolunteerEntityFilesDto;
import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
	
	void signUp(UserWithVolunteer userWithVolunteer) throws DuplicateInstanceException;

	Volunteer createVolunteer(Long representativeId, Volunteer volunteer, MultipartFile cert) throws InstanceNotFoundException, IOException;

	boolean deleteUser(Long userId) throws InstanceNotFoundException;

	boolean deleteVolunteerByDNI(String DNI) throws InstanceNotFoundException;
	
	User login(String userName, String password) throws IncorrectLoginException;
	
	User loginFromId(Long id) throws InstanceNotFoundException;

	UserDto updateProfile(Long id, String firstName, String lastName, String email, String phone) throws InstanceNotFoundException;

	UserWithVolunteer updateVolunteerProfile(Long representativeId, Long volunteerId, UserDto newUserData)
			throws
			PermissionException, InstanceNotFoundException, DuplicateInstanceException;
	
	void changePassword(Long id, String oldPassword, String newPassword)
		throws InstanceNotFoundException, IncorrectPasswordException;

	Representative createRepresentative(UserWithRepresentative userWithRepresentative)
        throws DuplicateInstanceException, InstanceNotFoundException;

	void sendEmailWithToken(User user, String token);

	boolean checkIfUserIsDeleted(Long userId);

	Optional<User> findByEmail(String email);

	User addNewPassword(Long id, String newPassword) throws InstanceNotFoundException;

	UserWithVolunteer getSummaryProfile(Long representativeId, Long userId) throws InstanceNotFoundException;

	UserWithVolunteer getMySummaryProfile(Long userId) throws InstanceNotFoundException;

	Representative getMySummaryProfileRep(Long userId) throws InstanceNotFoundException;

  	VolunteerEntityFilesDto findVolunteerEntityFiles(Long representativeId, Long id) throws InstanceNotFoundException;

	File updateDNI(Long userId, MultipartFile dni) throws IOException, InstanceNotFoundException;

	File updateHarassmentCert(Long userId, MultipartFile harassmentCert)
			throws InstanceNotFoundException, IOException;

	File updateAgreementFile(Long representativeId, Long volunteerId, MultipartFile agreementFile) throws InstanceNotFoundException, IOException;
}
