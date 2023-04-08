package es.udc.pcv.backend.test.model.services;

import es.udc.pcv.backend.model.entities.UserWithVolunteer;
import es.udc.pcv.backend.model.entities.Volunteer;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.exceptions.IncorrectLoginException;
import es.udc.pcv.backend.model.exceptions.IncorrectPasswordException;
import es.udc.pcv.backend.model.services.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {
	
	private final Long NON_EXISTENT_ID = Long.valueOf(-1);
	
	@Autowired
	private UserService userService;
	
	private User createUser(String email) {
		return new User("password",email);
	}

	private Volunteer createVolunteer() {
		return new Volunteer("password","st","st","st,",
				LocalDate.of(2000,11,9));
	}
	
	@Test
	public void testSignUpAndLoginFromId() throws DuplicateInstanceException, InstanceNotFoundException {
		
		User user = createUser("user@gmail.com");
		Volunteer volunteer = createVolunteer();
		userService.signUp(new UserWithVolunteer(user,volunteer));
		
		User loggedInUser = userService.loginFromId(user.getId());
		
		assertEquals(user, loggedInUser);
		assertEquals(User.RoleType.USER, user.getRole());
		
	}
	
	@Test
	public void testSignUpDuplicatedUserName() throws DuplicateInstanceException {
		
		User user = createUser("user@gmail.com");

		Volunteer volunteer = createVolunteer();
		userService.signUp(new UserWithVolunteer(user,volunteer));
		assertThrows(DuplicateInstanceException.class, () -> userService.signUp(new UserWithVolunteer(user,volunteer)));
		
	}
	
	@Test
	public void testLoginFromNonExistentId() {
		assertThrows(InstanceNotFoundException.class, () -> userService.loginFromId(NON_EXISTENT_ID));
	}
	
	@Test
	public void testLogin() throws DuplicateInstanceException, IncorrectLoginException {
		
		User user = createUser("user@gmail.com");
		String clearPassword = user.getPassword();

		Volunteer volunteer = createVolunteer();
		userService.signUp(new UserWithVolunteer(user,volunteer));
		
		User loggedInUser = userService.login(user.getEmail(), clearPassword);
		
		assertEquals(user, loggedInUser);
		
	}
	
	@Test
	public void testLoginWithIncorrectPassword() throws DuplicateInstanceException {
		
		User user = createUser("user@gmail.com");
		String clearPassword = user.getPassword();

		Volunteer volunteer = createVolunteer();
		userService.signUp(new UserWithVolunteer(user,volunteer));
		assertThrows(IncorrectLoginException.class, () ->
			userService.login(user.getEmail(), 'X' + clearPassword));
		
	}
	
	@Test
	public void testLoginWithNonExistentUserName() {
		assertThrows(IncorrectLoginException.class, () -> userService.login("X", "Y"));
	}
	/*
	@Test
	public void testUpdateProfile() throws InstanceNotFoundException, DuplicateInstanceException {
		
		User user = createUser("user");
		
		userService.signUp(user);
		
		//user.setFirstName('X' + user.getFirstName());
		//user.setLastName('X' + user.getLastName());
		user.setEmail('X' + user.getEmail());
		
		userService.updateProfile(user.getId(), 'X' + user.getFirstName(), 'X' + user.getLastName(),
			'X' + user.getEmail());
		
		User updatedUser = userService.loginFromId(user.getId());
		
		assertEquals(user, updatedUser);
		
	}
	*/
	@Test
	public void testUpdateProfileWithNonExistentId() {
		assertThrows(InstanceNotFoundException.class, () ->
			userService.updateProfile(NON_EXISTENT_ID, "X", "X", "X"));
	}
	
	@Test
	public void testChangePassword() throws DuplicateInstanceException, InstanceNotFoundException,
		IncorrectPasswordException, IncorrectLoginException {
		
		User user = createUser("user@gmail.com");
		String oldPassword = user.getPassword();
		String newPassword = 'X' + oldPassword;

		Volunteer volunteer = createVolunteer();
		userService.signUp(new UserWithVolunteer(user,volunteer));
		userService.changePassword(user.getId(), oldPassword, newPassword);
		userService.login(user.getEmail(), newPassword);
		
	}
	
	@Test
	public void testChangePasswordWithNonExistentId() {
		assertThrows(InstanceNotFoundException.class, () ->
			userService.changePassword(NON_EXISTENT_ID, "X", "Y"));
	}
	
	@Test
	public void testChangePasswordWithIncorrectPassword() throws DuplicateInstanceException {
		
		User user = createUser("user@gmail.com");
		String oldPassword = user.getPassword();
		String newPassword = 'X' + oldPassword;

		Volunteer volunteer = createVolunteer();
		userService.signUp(new UserWithVolunteer(user,volunteer));
		assertThrows(IncorrectPasswordException.class, () ->
			userService.changePassword(user.getId(), 'Y' + oldPassword, newPassword));
		
	}

}
