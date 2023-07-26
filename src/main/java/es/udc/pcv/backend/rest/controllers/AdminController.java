package es.udc.pcv.backend.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.services.AdminService;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.services.UserService;
import es.udc.pcv.backend.model.to.EntityData;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.rest.common.JwtGenerator;
import es.udc.pcv.backend.rest.common.JwtInfo;
import es.udc.pcv.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.pcv.backend.rest.dtos.EntityConversor;
import es.udc.pcv.backend.rest.dtos.EntityDto;
import es.udc.pcv.backend.rest.dtos.MessageDTO;
import es.udc.pcv.backend.rest.dtos.RepresentativeDto;
import es.udc.pcv.backend.rest.dtos.UserConversor;
import es.udc.pcv.backend.rest.dtos.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@Tag(name = "admin")
public class AdminController {

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private JwtGenerator jwtGenerator;

  @Autowired
  private UserService userService;

  @Autowired
  private AdminService adminService;

  @Autowired
  private RepresentativeService representativeService;

  @Autowired
  private UserConversor userConversor;

  @Autowired
  private EntityConversor entityConversor;

  @Autowired
  private ObjectMapper objectMapper;

  private String generateServiceToken(Representative user) {

    JwtInfo jwtInfo = new JwtInfo(user.getId(), user.getEmail(), user.getRole().toString());

    return jwtGenerator.generateLowExpiration(jwtInfo);

  }


  @Operation(summary = "create representative")
  @PostMapping("/createRepresentative")
  public ResponseEntity<MessageDTO> createRepresentative(
      @Validated({RepresentativeDto.AllValidations.class}) @RequestBody RepresentativeDto representativeDto)
      throws
      DuplicateInstanceException, InstanceNotFoundException {

    UserWithRepresentative userWithRepresentative = userConversor.userWithRepresentative(representativeDto);
    Representative representative = userService.createRepresentative(userWithRepresentative);
    String token = generateServiceToken(representative);
    userService.sendEmailWithToken(representative,token);


    MessageDTO message = new MessageDTO();
    message.setMessage("Se ha enviado el e-mail de confirmación con éxito a "+representative.getEmail());
    return ResponseEntity.ok(message);

  }

  @GetMapping("/getEntities")
  public Block<EntityDto> getEntities(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Block<Entidad> entidadBlock = adminService.getEntities(page,size);
    return new Block<>(entityConversor.toEntityListDto(entidadBlock.getItems()),entidadBlock.getExistMoreItems());
  }

  @GetMapping("/getMyEntity")
  public EntityDto getMyEntity(@RequestAttribute long userId){
    return entityConversor.toEntityDto(representativeService.getMyEntity(userId));
  }

  @Operation(summary = "create representative")
  @RequestMapping(value = "/createEntity", method = RequestMethod.POST,  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public EntityDto createEntity(
    @RequestParam String entityDto, @RequestPart(name="cert",required = false)
      MultipartFile cert, @RequestPart(name="logo",required = false) MultipartFile logo)
      throws IOException {
    EntityDto realEntityDto = objectMapper.readValue(entityDto, EntityDto.class);
    EntityData entityData = entityConversor.toEntity(realEntityDto);
    Entidad entidad = adminService.createEntity(entityData);
    File logoFile = null;
    if(logo!= null){
      logoFile = adminService.updateEntityLogo(logo,entidad.getId());
    }
    File certFile = null;
    if(cert!=null){
       certFile = adminService.updateEntityCert(cert,entidad.getId());
    }
    EntityDto entityDtoReturn = entityConversor.toEntityDto(entidad);
    entityDtoReturn.setLogoName(logoFile!=null ? logoFile.getOriginalName() : null);
    entityDtoReturn.setCertName(certFile!=null ? certFile.getOriginalName() : null);
    return entityDtoReturn;

  }

  @Operation(summary = "updates a project with a new ods list")
  @PatchMapping(value = "/update/project/{projectId}/ods")
  public boolean updateProjectOds(@PathVariable Long projectId, @RequestBody @NotEmpty List<Long> ods){
    return adminService.updateProjectOds(projectId,ods);
  }

}
