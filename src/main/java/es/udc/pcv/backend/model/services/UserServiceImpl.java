package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.RepresentativeDao;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.entities.VolunteerDao;
import java.security.SecureRandom;
import java.util.Optional;

import es.udc.pcv.backend.model.exceptions.IncorrectLoginException;
import es.udc.pcv.backend.model.exceptions.IncorrectPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
	private JavaMailSender javaMailSender;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private VolunteerDao volunteerDao;

	@Autowired
	private RepresentativeDao representativeDao;

	@Value("${spring.mail.username}")
	private String originEmail;

	private static final SecureRandom RANDOM = new SecureRandom();

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

		StringBuilder sb = new StringBuilder(10);
		for (int i = 0; i < 10; i++) {
			char randomChar = (char) (RANDOM.nextInt(95) + 32); //contraseña con caracteres printeables ASCII 32-126
			sb.append(randomChar);
		}
		String randomPassword = sb.toString();
		User user = new User(randomPassword,userWithRepresentative.getEmail());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(User.RoleType.REPRESENTATIVE);

		Representative representative = new Representative(user,userWithRepresentative.getName(),
				userWithRepresentative.getSurname(),userWithRepresentative.getPhone());
		return representativeDao.save(representative);
	}

	@Override
	public void sendEmailWithToken(User user, String token) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(originEmail);
		String fullPath = "https://localhost:3000/users/validate/registerToken/"+token;
		String bodyMessage = "Para darse de alta en PlataformaCoruñesaDeVoluntariado introduzca una nueva contraseña en el siguiente enlace\n"+
				"Este enlace tendrá una validez de 1 hora.\n"+fullPath+"\n" +
				"Si ha recibido por error este mensaje ignorélo por favor";
		message.setTo(user.getEmail());
		message.setSubject("Registro en Plataforma Coruñesa de Voluntariado");
		message.setText(bodyMessage);
		javaMailSender.send(message);
	}

	@Override
	public User addNewPassword(Long id, String newPassword) throws InstanceNotFoundException {
		User user = permissionChecker.checkUser(id);
		user.setPassword(passwordEncoder.encode(newPassword));
		return user;
	}

}
