package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.EntidadDao;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.FileDao;
import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.entities.ParticipationDao;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.RepresentativeDao;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.entities.VolunteerDao;
import es.udc.pcv.backend.rest.dtos.UserDto;
import es.udc.pcv.backend.rest.dtos.VolunteerEntityFilesDto;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import es.udc.pcv.backend.model.exceptions.IncorrectLoginException;
import es.udc.pcv.backend.model.exceptions.IncorrectPasswordException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.Tika;
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
import org.springframework.web.multipart.MultipartFile;

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
	private FileDao fileDao;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private EntidadDao entityDao;

	@Autowired
	private VolunteerDao volunteerDao;

	@Autowired
	private RepresentativeDao representativeDao;

	@Autowired
	private ParticipationDao participationDao;

	@Value("${spring.mail.username}")
	private String originEmail;

	@Value("${file.base-path}")
	private String basePath;

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
	public Volunteer createVolunteer(Long representativeId, Volunteer volunteer, MultipartFile cert)
			throws InstanceNotFoundException, IOException {
		Optional<Volunteer> emptyVolunteer = volunteerDao.findByDni(volunteer.getDni());

		if(!emptyVolunteer.isPresent()){
			Volunteer ret = volunteerDao.save(new Volunteer(volunteer.getName(),volunteer.getSurname(),
					volunteer.getDni(),volunteer.getDniExpiration(),volunteer.getLocality(),volunteer.getPhone(),volunteer.getBirth()));
			updateAgreementFile(representativeId,ret.getId(),cert);
			return ret;
		}
		//For privacy reasons if volunteer is already in the system this entity won't know and it will show like he has been registered, updating new data
		// because recent data must be accurate
		else{
			updateVolunteer(volunteer,emptyVolunteer.get());
			updateAgreementFile(representativeId,emptyVolunteer.get().getId(),cert);
			return emptyVolunteer.get();
		}
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
	public UserDto updateProfile(Long id, String name, String surname, String email, String phone) throws InstanceNotFoundException {
		
		User user = permissionChecker.checkUser(id);
		user.setEmail(email);
		if(user.getRole()== User.RoleType.USER){
			Optional<Volunteer> volunteerOpt = volunteerDao.findByUserId(user.getId());
			Volunteer volunteer = null;
			if(volunteerOpt.isPresent()){
				volunteer = volunteerOpt.get();
				volunteer.setName(name);
				volunteer.setSurname(surname);
				volunteer.setPhone(phone);
			}
			UserDto newUserDto = new UserDto();
			newUserDto.setId(user.getId());
			newUserDto.setRole(user.getRole().toString());
			newUserDto.setEmail(user.getEmail());
			newUserDto.setName(volunteer.getName());
			newUserDto.setSurname(volunteer.getSurname());
			newUserDto.setPhone(volunteer.getPhone());
			return newUserDto;
		}
		else{
			Optional<Representative> representativeOpt = representativeDao.findById(user.getId());
			Representative representative = null;
			if(representativeOpt.isPresent()){
				representative = representativeOpt.get();
				representative.setName(name);
				representative.setSurname(surname);
				representative.setPhone(phone);
			}
			UserDto newUserDto = new UserDto();
			newUserDto.setId(user.getId());
			newUserDto.setRole(user.getRole().toString());
			newUserDto.setEmail(user.getEmail());
			newUserDto.setName(representative.getName());
			newUserDto.setSurname(representative.getSurname());
			newUserDto.setPhone(representative.getPhone());
			return newUserDto;
		}
	}

	@Override
	public UserWithVolunteer updateVolunteerProfile(Long representativeId, Long volunteerId,
													UserDto newUserData)
			throws PermissionException, InstanceNotFoundException, DuplicateInstanceException {
		if(!Objects.equals(volunteerId, newUserData.getId())){
			throw new PermissionException();
		}
		Optional<Representative> representative = representativeDao.findById(representativeId);
		if(!representative.isPresent()){
			throw new InstanceNotFoundException("project.entities.representative",representativeId);
		}
		Optional<Volunteer> volunteer = volunteerDao.findById(volunteerId);
		if(!volunteer.isPresent()){
			throw new InstanceNotFoundException("project.entities.volunteer",volunteerId);
		}
		if(!fileDao.existsByVolunteerAndFileTypeAndEntidad(volunteer.get(),
				File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH, representative.get().getEntity())){
			throw new PermissionException();
		}
		if (newUserData.getEmail()!=null && userDao.existsByEmail(newUserData.getEmail())) {
			throw new DuplicateInstanceException("project.entities.user", newUserData.getEmail());
		}
		if(volunteer.get().getUser()==null && newUserData.getEmail()!=null){
			User user = new User();
			user.setPassword(passwordEncoder.encode(newUserData.getPassword()!=null ? newUserData.getPassword() : generateRandomPassword()));
			user.setEmail(newUserData.getEmail());
			user.setRole(User.RoleType.USER);
			User created = userDao.save(user);
			volunteer.get().setUser(created);
			//TODO log representativeId with timestamp and volunteerId when email is modified for security reasons
		} // user is not null
		else if(newUserData.getEmail()!=null){
			volunteer.get().getUser().setEmail(newUserData.getEmail());
		}
		volunteer = volunteerDao.findById(volunteerId);
		Volunteer newVolunteer =  new Volunteer(newUserData.getName(), newUserData.getSurname(), newUserData.getDni(), newUserData.getDniExpiration(),newUserData.getLocality(),
				newUserData.getPhone(), newUserData.getBirth());
		Volunteer alreadyUpdated = updateVolunteer(newVolunteer,volunteer.get()); //no need check
		return new UserWithVolunteer(alreadyUpdated.getUser(),alreadyUpdated);

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
			throws DuplicateInstanceException, InstanceNotFoundException {

		if (userDao.existsByEmail(userWithRepresentative.getEmail())) {
			throw new DuplicateInstanceException("project.entities.user", userWithRepresentative.getEmail());
		}

		if (!entityDao.existsById(userWithRepresentative.getEntityId())){
			throw new InstanceNotFoundException("project.entities.entidad",userWithRepresentative.getEntityId());
		}
		String randomPassword = generateRandomPassword();
		User user = new User(randomPassword,userWithRepresentative.getEmail());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(User.RoleType.REPRESENTATIVE);

		Representative representative = new Representative(user,userWithRepresentative.getName(),
				userWithRepresentative.getSurname(),userWithRepresentative.getPhone(),entityDao.findById(
				userWithRepresentative.getEntityId()).get());
		return representativeDao.save(representative);
	}

	@Override
	public void sendEmailWithToken(User user, String token) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(originEmail);
		String fullPath = "https://pcv-front.onrender.com/users/validate/registerToken/"+token;
		String bodyMessage = "Para darse de alta en Plataforma Coruñesa De Voluntariado introduzca una nueva contraseña en el siguiente enlace.\n"+
				"Este enlace tendrá una validez de 1 hora.\n"+fullPath+"\n" +
				"Si ha recibido por error este mensaje ignórelo por favor";
		message.setTo(user.getEmail());
		message.setSubject("Registro en Plataforma Coruñesa de Voluntariado");
		message.setText(bodyMessage);
		javaMailSender.send(message);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userDao.findByEmail(email);
	}

	@Override
	public User addNewPassword(Long id, String newPassword) throws InstanceNotFoundException {
		User user = permissionChecker.checkUser(id);
		user.setPassword(passwordEncoder.encode(newPassword));
		return user;
	}

	@Override
	public UserWithVolunteer getSummaryProfile(Long representativeId, Long userId)
			throws InstanceNotFoundException {
		Entidad entidad = representativeDao.findById(representativeId).get().getEntity();
		Optional<Volunteer> volunteer = volunteerDao.findById(userId);
		if(!volunteer.isPresent()){
			throw new InstanceNotFoundException("project.entities.volunteer",userId);
		}
		//no relationship beetween entity and volunteer
		if(!(participationDao.existsByProjectEntityIdAndVolunteerId(entidad.getId(),userId) || fileDao.existsByVolunteerAndFileTypeAndEntidad(volunteer.get(),
				File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH, entidad))){
			throw new InstanceNotFoundException("project.entities.volunteer",userId);
		}
		User user = volunteer.get().getUser();
		return new UserWithVolunteer(user,volunteer.get());
	}

	@Override
	public UserWithVolunteer getMySummaryProfile(Long userId) throws InstanceNotFoundException {
		Optional<Volunteer> volunteer = volunteerDao.findByUserId(userId);
		if(!volunteer.isPresent()){
			throw new InstanceNotFoundException("project.entities.volunteer",userId);
		}
		//for performance on login not getting user data since it was already fetched in controller
		return new UserWithVolunteer(null,volunteer.get());
	}

	@Override
	public Representative getMySummaryProfileRep(Long userId) throws InstanceNotFoundException {
		Optional<Representative> representative = representativeDao.findById(userId);
		if(!representative.isPresent()){
			throw new InstanceNotFoundException("project.entities.representative",userId);
		}
		return representative.get();
	}


	@Override
	public VolunteerEntityFilesDto findVolunteerEntityFiles(Long representativeId, Long userId)
			throws InstanceNotFoundException {
		Entidad entidad = representativeDao.findById(representativeId).get().getEntity();
		Optional<Volunteer> volunteer = volunteerDao.findById(userId);
		if(!volunteer.isPresent()){
			throw new InstanceNotFoundException("project.entities.volunteer",userId);
		}
		//relationship beetween entity and volunteer (already done in getSummary)
		Optional<File> certFile = fileDao.findByEntidadAndVolunteerAndFileType(entidad,volunteer.get(),
				File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH);
		Optional<File> harassmentFile = fileDao.findByVolunteerAndFileType(volunteer.get(),
				File.FileType.HARASSMENT_CERT);
		Optional<File> dniFile = fileDao.findByVolunteerAndFileType(volunteer.get(), File.FileType.DNI);
		Optional<File> photoFile = fileDao.findByVolunteerAndFileType(volunteer.get(), File.FileType.PHOTO);
		return new VolunteerEntityFilesDto(certFile.isPresent(), harassmentFile.isPresent(),
				dniFile.isPresent(), photoFile.isPresent());


	}

	@Override
	public File updateDNI(Long userId, MultipartFile dni)
			throws IOException, InstanceNotFoundException {
		String uploadDir = basePath+"users/dni/";
		java.io.File dir = new java.io.File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		Optional<Volunteer> volunteer = volunteerDao.findByUserId(userId);
		if(!volunteer.isPresent()){
			throw new InstanceNotFoundException("project.entities.volunteer",userId);
		}
		InputStream inputStream = dni.getInputStream();
		Tika tika = new Tika();
		String mimeType = tika.detect(inputStream);
		String extension = mimeType.split("/")[1];
		UUID randomUIID = UUID.randomUUID();
		String fileName = randomUIID.toString();
		Path filePath = Paths.get(uploadDir + fileName + "." + extension);
		if (Files.exists(filePath)) {
			throw new IOException("File already exists: " + filePath);
		}
		Files.copy(dni.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		Optional<File> oldFile = fileDao.findByVolunteerAndFileType(volunteer.get(), File.FileType.DNI);
		if (oldFile.isPresent()) {
			File newFile = oldFile.get();
			Path path = Paths.get(basePath+"users/dni/" + newFile.getId().toString() + "." + newFile.getExtension());
			fileDao.delete(newFile);
			try {
				Files.delete(path);
			}catch (Exception e){
				//If there is no old file in disk because it was already deleted (low chances) app will keep going right
			}
		}
		return fileDao.save(new File(randomUIID,new Date(),dni.getOriginalFilename(),File.FileType.DNI,
				extension,null,volunteer.get()));
	}

	@Override
	public File updateHarassmentCert(Long userId, MultipartFile harassmentCert)
			throws InstanceNotFoundException, IOException {
		String uploadDir = basePath+"users/harassmentCert/";
		java.io.File dir = new java.io.File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		Optional<Volunteer> volunteer = volunteerDao.findByUserId(userId);
		if(!volunteer.isPresent()){
			throw new InstanceNotFoundException("project.entities.volunteer",userId);
		}
		InputStream inputStream = harassmentCert.getInputStream();
		Tika tika = new Tika();
		String mimeType = tika.detect(inputStream);
		String extension = mimeType.split("/")[1];
		UUID randomUIID = UUID.randomUUID();
		String fileName = randomUIID.toString();
		Path filePath = Paths.get(uploadDir + fileName + "." + extension);
		if (Files.exists(filePath)) {
			throw new IOException("File already exists: " + filePath);
		}
		Files.copy(harassmentCert.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		Optional<File> oldFile = fileDao.findByVolunteerAndFileType(volunteer.get(), File.FileType.HARASSMENT_CERT);
		if (oldFile.isPresent()) {
			File newFile = oldFile.get();
			Path path = Paths.get(basePath+"users/harassmentCert/" + newFile.getId().toString() + "." + newFile.getExtension());
			fileDao.delete(newFile);
			try {
				Files.delete(path);
			}catch (Exception e){
				//If there is no old file in disk because it was already deleted (low chances) app will keep going right
			}
		}
		return fileDao.save(new File(randomUIID,new Date(),harassmentCert.getOriginalFilename(),File.FileType.HARASSMENT_CERT,
				extension,null,volunteer.get()));
	}

	@Override
	public File updateAgreementFile(Long representativeId, Long volunteerId, MultipartFile multipartFile)
			throws InstanceNotFoundException, IOException {
		Optional<Representative> representative = representativeDao.findById(representativeId);
		if(!representative.isPresent()){
			throw new InstanceNotFoundException("project.entities.representative",representativeId);
		}
		Optional<Volunteer> volunteer = volunteerDao.findById(volunteerId);
		if(!volunteer.isPresent()){
			throw new InstanceNotFoundException("project.entities.volunteer",volunteerId);
		}
		String uploadDir = basePath+"participations/certFiles/";
		java.io.File dir = new java.io.File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		InputStream inputStream = multipartFile.getInputStream();
		Tika tika = new Tika();
		String mimeType = tika.detect(inputStream);
		String extension = mimeType.split("/")[1];
		UUID randomUIID = UUID.randomUUID();
		String fileName = randomUIID.toString();
		Path filePath = Paths.get(uploadDir + fileName + "." + extension);
		if (Files.exists(filePath)) {
			throw new IOException("File already exists: " + filePath);
		}
		Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		Optional<File> oldFile = fileDao.findByEntidadAndVolunteerAndFileType(representative.get().getEntity(),volunteer.get(), File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH);
		if (oldFile.isPresent()) {
			File newFile = oldFile.get();
			Path path = Paths.get(basePath+"participations/certFiles/" + newFile.getId().toString() + "." + newFile.getExtension());
			fileDao.delete(newFile);
			try {
				Files.delete(path);
			}catch (Exception e){
				//If there is no old file in disk because it was already deleted (low chances) app will keep going right
			}
		}
		return fileDao.save(new File(randomUIID,new Date(),multipartFile.getOriginalFilename(),File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH,
				extension,representative.get().getEntity(),volunteer.get()));


	}

	private Volunteer updateVolunteer(Volunteer updatedVolunteer, Volunteer existingEntity) {
		if (existingEntity != null && updatedVolunteer != null) {
			if (updatedVolunteer.getName() != null) {
				existingEntity.setName(updatedVolunteer.getName());
			}
			if (updatedVolunteer.getSurname() != null) {
				existingEntity.setSurname(updatedVolunteer.getSurname());
			}
			if (updatedVolunteer.getDni() != null) {
				existingEntity.setDni(updatedVolunteer.getDni());
			}
			if (updatedVolunteer.getDniExpiration() != null) {
				existingEntity.setDniExpiration(updatedVolunteer.getDniExpiration());
			}
			if (updatedVolunteer.getLocality() != null) {
				existingEntity.setLocality(updatedVolunteer.getLocality());
			}
			if (updatedVolunteer.getPhone() != null) {
				existingEntity.setPhone(updatedVolunteer.getPhone());
			}
			if (updatedVolunteer.getBirth() != null) {
				existingEntity.setBirth(updatedVolunteer.getBirth());
			}
		}
		return existingEntity;
	}

	private String generateRandomPassword(){
		StringBuilder sb = new StringBuilder(10);
		for (int i = 0; i < 10; i++) {
			char randomChar = (char) (RANDOM.nextInt(95) + 32); //contraseña con caracteres printeables ASCII 32-126
			sb.append(randomChar);
		}
		return sb.toString();
	}

}
