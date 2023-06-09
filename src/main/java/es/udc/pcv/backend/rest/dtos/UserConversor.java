package es.udc.pcv.backend.rest.dtos;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConversor {
  @Named("roleToString")
  String roleToString(User.RoleType role);

  @Mapping(source = "user.role", target = "role", qualifiedByName = "roleToString")
  @Mapping(source = "user.id", target = "id")
  UserDto toUserDto(User user, Volunteer volunteer);

  @Mapping(target = "hasHarassmentFile", source = "hasHarassmentFile")
  @Mapping(target = "hasCertFile", source = "hasCertFile")
  @Mapping(target = "isVerified", source = "volunteer.verified")
  @Mapping(source = "volunteer.id", target = "volunteerId")
  @Mapping(source = "user.email", target = "email")
  @Mapping(target = "hasDniFile", source = "hasDniFile")
  @Mapping(target = "hasPhotoFile", source = "hasPhotoFile")
  VolunteerSummaryDto toUserSummaryDto(User user, Volunteer volunteer, Boolean hasHarassmentFile,
                                       Boolean hasCertFile, Boolean hasDniFile, Boolean hasPhotoFile);

  @Mapping(target = "isVerified", ignore = true)
  @Mapping(target = "hasHarassmentFile", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "hasCertFile", ignore = true)
  @Mapping(target = "hasPhotoFile", ignore = true)
  @Mapping(target = "hasDniFile", ignore = true)
  @Mapping(source = "id", target = "volunteerId")
  VolunteerSummaryDto toVolunteerSummary(Volunteer volunteer);

  Block<VolunteerSummaryDto> toVolunteerSummaryBlockDto(Block<Volunteer> volunteerBlock);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "password", target = "password")
  @Mapping(source = "email", target = "email")
  User toUser(UserDto userDto);

  Volunteer toVolunteer(UserDto userDto);

  Volunteer toVolunteer(VolunteerDataDto volunteerDataDto);

  UserDto toUserDto(User user);

  @Mapping(source = "user", target = "userDto")
  @Mapping(source = "serviceToken", target = "serviceToken")
  AuthenticatedUserDto toAuthenticatedUserDto(String serviceToken, User user);

  UserWithRepresentative userWithRepresentative(RepresentativeDto representativeDto);

}
