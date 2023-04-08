package es.udc.pcv.backend.rest.dtos;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.rest.dtos.UserDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserConversor {
  @Named("roleToString")
  String roleToString(User.RoleType role);

  @Mapping(source = "user.role", target = "role", qualifiedByName = "roleToString")
  @Mapping(source = "user.id", target = "id")
  UserDto toUserDto(User user, Volunteer volunteer);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "password", target = "password")
  @Mapping(source = "email", target = "email")
  User toUser(UserDto userDto);

  Volunteer toVolunteer(UserDto userDto);

  UserDto toUserDto(User user);

  @Mapping(source = "user", target = "userDto")
  @Mapping(source = "serviceToken", target = "serviceToken")
  AuthenticatedUserDto toAuthenticatedUserDto(String serviceToken, User user);

}
