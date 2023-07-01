package es.udc.pcv.backend.test.model.services;

import es.udc.pcv.backend.model.entities.CollaborationArea;
import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.Ods;
import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.RegisteredHours;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.exceptions.AlreadyParticipatingException;
import es.udc.pcv.backend.model.exceptions.ParticipationIsInDateException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.exceptions.ProjectIsPausedException;
import es.udc.pcv.backend.model.services.AdminService;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.services.VolunteerService;
import es.udc.pcv.backend.model.to.EntityData;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.RegisteredHoursDto;
import java.lang.reflect.Executable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

	@Autowired
	private AdminService adminService;

	@Autowired
	private RepresentativeService representativeService;

	@Autowired
	private VolunteerService volunteerService;
	
	private User createUser(String email) {
		return new User("password",email);
	}

	private Representative createRepresentative() {
		Entidad entidad = adminService.createEntity(new EntityData("Cáritas","Description","caritas.es","rua","mail@caritas.es",null,null,null));
		return new Representative("name","surname","600999999", entidad);
	}

	private Volunteer createVolunteer() {
		return new Volunteer("password","st","st","st,",
				LocalDate.of(2000,11,9));
	}

	private ProjectDto createProjectDto(String name, List<String> task, List<Long> ods,long entityId, long areaId){
		ProjectDto projectDto = new ProjectDto(null,name,"Descripción corta","Detalle de las tareas","A Coruña","Lunes y Martes de 10:30 a 12:30",
				10,"Sin preferencias",false,true,false,task,ods,entityId,areaId);
		return projectDto;
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

	@Test
	public void testCreateRepresentativeAndLoginFromId() throws DuplicateInstanceException, InstanceNotFoundException {

		User user = createUser("representante6@gmail.com");
		Representative representative = createRepresentative();
		long userId = userService.createRepresentative(new UserWithRepresentative(user.getPassword(),user.getEmail(),representative.getName(),
				representative.getSurname(),representative.getPhone(),representative.getEntity().getId())).getId();

		User loggedInUser = userService.loginFromId(userId);

		assertEquals(user.getEmail(), loggedInUser.getEmail());
		assertEquals(User.RoleType.REPRESENTATIVE, loggedInUser.getRole());

	}

	@Test
	public void testCreateRepresentativeDuplicatedEmail()
			throws DuplicateInstanceException, InstanceNotFoundException {

		User user = createUser("representante6@gmail.com");
		Representative representative = createRepresentative();
		userService.createRepresentative(new UserWithRepresentative(user.getPassword(),user.getEmail(),representative.getName(),
				representative.getSurname(),representative.getPhone(),representative.getEntity().getId()));
		assertThrows(DuplicateInstanceException.class, () -> userService.createRepresentative(new UserWithRepresentative(user.getPassword(),user.getEmail(),representative.getName(),
				representative.getSurname(),representative.getPhone(),representative.getEntity().getId())));

	}

	@Test
	public void createParticipation()
			throws InstanceNotFoundException, DuplicateInstanceException, IncorrectLoginException,
			AlreadyParticipatingException, PermissionException, ProjectIsPausedException {
		User userBasic = createUser("user@gmail.com");
		String clearPassword = userBasic.getPassword();

		Volunteer volunteer = createVolunteer();
		userService.signUp(new UserWithVolunteer(userBasic,volunteer));

		User loggedInUser = userService.login(userBasic.getEmail(), clearPassword);

		User user = createUser("representante6@gmail.com");
		Representative representative = createRepresentative();
		long userId = userService.createRepresentative(new UserWithRepresentative(user.getPassword(),user.getEmail(),representative.getName(),
				representative.getSurname(),representative.getPhone(),representative.getEntity().getId())).getId();
		List<Ods> odsList = representativeService.getAllOds();
		List<CollaborationArea> collaborationAreas = representativeService.getAllCollaborationArea();
		List<String> tasks = new ArrayList<>();
		List<Long> itemIds = odsList.stream()
				.filter(item -> (item.getNumber() == 2) || (item.getNumber() == 1))
				.map(Ods::getId)
				.collect(Collectors.toList());
		tasks.add("Task 1");
		tasks.add("Cocinar y preparar comidas");
		tasks.add("Envasar y distribuir alimentos a personas necesitadas");
		ProjectDto projectDto = createProjectDto("Proyecto X",tasks,itemIds,representative.getEntity().getId(),collaborationAreas.get(0).getId());
		Project project = representativeService.createProject(projectDto,userId);
		ParticipationDto participationDto = new ParticipationDto(false,project.getId(),loggedInUser.getId());
		Participation participation = volunteerService.createMyParticipation(participationDto,loggedInUser.getId());
		assertEquals(participation.getProject(),project);
		assertEquals(participation.getVolunteer().getUser().getId(),loggedInUser.getId());
		assertFalse(participation.isRecommended());
	}

	@Test
	public void createRegisteredHoursParticipationIsInDateException()
			throws InstanceNotFoundException, DuplicateInstanceException, IncorrectLoginException,
			AlreadyParticipatingException, PermissionException, ProjectIsPausedException,
			ParticipationIsInDateException {
		User userBasic = createUser("user@gmail.com");
		String clearPassword = userBasic.getPassword();

		Volunteer volunteer = createVolunteer();
		userService.signUp(new UserWithVolunteer(userBasic,volunteer));

		User loggedInUser = userService.login(userBasic.getEmail(), clearPassword);

		User user = createUser("representante6@gmail.com");
		Representative representative = createRepresentative();
		long userId = userService.createRepresentative(new UserWithRepresentative(user.getPassword(),user.getEmail(),representative.getName(),
				representative.getSurname(),representative.getPhone(),representative.getEntity().getId())).getId();
		List<Ods> odsList = representativeService.getAllOds();
		List<CollaborationArea> collaborationAreas = representativeService.getAllCollaborationArea();
		List<String> tasks = new ArrayList<>();
		List<Long> itemIds = odsList.stream()
				.filter(item -> (item.getNumber() == 2) || (item.getNumber() == 1))
				.map(Ods::getId)
				.collect(Collectors.toList());
		tasks.add("Task 1");
		tasks.add("Cocinar y preparar comidas");
		tasks.add("Envasar y distribuir alimentos a personas necesitadas");
		ProjectDto projectDto = createProjectDto("Proyecto X",tasks,itemIds,representative.getEntity().getId(),collaborationAreas.get(0).getId());
		Project project = representativeService.createProject(projectDto,userId);
		ParticipationDto participationDto = new ParticipationDto(false,project.getId(),loggedInUser.getId());
		Participation participation = volunteerService.createMyParticipation(participationDto,loggedInUser.getId());
		RegisteredHours participationHour1 = representativeService.createHourRegister(userId,new RegisteredHoursDto(null,
				participation.getId(), 2, LocalDate.of(2022,6,30)));
		assertEquals(participationHour1.getParticipation(), participation);
		assertThrows(ParticipationIsInDateException.class, () -> representativeService.createHourRegister(userId,new RegisteredHoursDto(null,
				participation.getId(), 3, LocalDate.of(2022,6,30))));
	}

}
