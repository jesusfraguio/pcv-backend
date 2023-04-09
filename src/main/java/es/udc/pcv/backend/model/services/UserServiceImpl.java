package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.RepresentativeDao;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.entities.VolunteerDao;
import java.util.Optional;

import es.udc.pcv.backend.model.exceptions.IncorrectLoginException;
import es.udc.pcv.backend.model.exceptions.IncorrectPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.entities.UserDao;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private PermissionChecker permissionChecker;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private VolunteerDao volunteerDao;

	@Autowired
	private RepresentativeDao representativeDao;
	
	@Override
	public void signUp(UserWithVolunteer userWithVolunteer) throws DuplicateInstanceException {
		User user = userWithVolunteer.getUser();
		Volunteer volunteer = userWithVolunteer.getVolunteer();
		if (userDao.existsByEmail(user.getEmail())) {
			throw new DuplicateInstanceException("project.entities.user", user.getEmail());
		}
			
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(User.RoleType.USER);
		
		User created = userDao.save(user);
		volunteer.setUser(created);
		volunteerDao.save(volunteer);
		
	}

	@Override
	@Transactional(readOnly=true)
	public User login(String email, String password) throws IncorrectLoginException {
		
		Optional<User> user = userDao.findByEmail(email);
		
		if (!user.isPresent()) {
			throw new IncorrectLoginException(email, password);
		}
		
		if (!passwordEncoder.matches(password, user.get().getPassword())) {
			throw new IncorrectLoginException(email, password);
		}
		
		return user.get();
		
	}
	
	@Override
	@Transactional(readOnly=true)
	public User loginFromId(Long id) throws InstanceNotFoundException {
		return permissionChecker.checkUser(id);
	}

	@Override
	public UserWithVolunteer updateProfile(Long id, String name, String surname, String email) throws InstanceNotFoundException {
		
		User user = permissionChecker.checkUser(id);
		user.setEmail(email);

		Optional<Volunteer> volunteerOpt = volunteerDao.findByUserId(user.getId());
		Volunteer volunteer = null;
		if(volunteerOpt.isPresent()){
			volunteer = volunteerOpt.get();
			volunteer.setName(name);
			volunteer.setSurname(surname);

		}
		return new UserWithVolunteer(user,volunteer);

	}

	@Override
	public void changePassword(Long id, String oldPassword, String newPassword)
		throws InstanceNotFoundException, IncorrectPasswordException {
		
		User user = permissionChecker.checkUser(id);
		
		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new IncorrectPasswordException();
		} else {
			user.setPassword(passwordEncoder.encode(newPassword));
		}
		
	}

	@Override
	public Representative createRepresentative(UserWithRepresentative userWithRepresentative)
			throws DuplicateInstanceException {

		if (userDao.existsByEmail(userWithRepresentative.getEmail())) {
			throw new DuplicateInstanceException("project.entities.user", userWithRepresentative.getEmail());
		}

		User user = new User(userWithRepresentative.getPassword(),userWithRepresentative.getEmail());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(User.RoleType.REPRESENTATIVE);

		Representative representative = new Representative(user,userWithRepresentative.getName(),
				userWithRepresentative.getSurname(),userWithRepresentative.getPhone());
		return representativeDao.save(representative);
	}

}
