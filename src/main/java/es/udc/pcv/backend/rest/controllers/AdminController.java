package es.udc.pcv.backend.rest.controllers;

import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.services.UserService;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.rest.common.JwtGenerator;
import es.udc.pcv.backend.rest.common.JwtInfo;
import es.udc.pcv.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.pcv.backend.rest.dtos.MessageDTO;
import es.udc.pcv.backend.rest.dtos.RepresentativeDto;
import es.udc.pcv.backend.rest.dtos.UserConversor;
import es.udc.pcv.backend.rest.dtos.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
  private UserConversor userConversor;

  private String generateServiceToken(Representative user) {

    JwtInfo jwtInfo = new JwtInfo(user.getId(), user.getEmail(), user.getRole().toString());

    return jwtGenerator.generateLowExpiration(jwtInfo);

  }

  @Operation(summary = "create representative")
  @PostMapping("/createRepresentative")
  public ResponseEntity<MessageDTO> createRepresentative(
      @Validated({RepresentativeDto.AllValidations.class}) @RequestBody RepresentativeDto representativeDto) throws
      DuplicateInstanceException {

    UserWithRepresentative userWithRepresentative = userConversor.userWithRepresentative(representativeDto);
    Representative representative = userService.createRepresentative(userWithRepresentative);
    String token = generateServiceToken(representative);
    userService.sendEmailWithToken(representative,token);


    MessageDTO message = new MessageDTO();
    message.setMessage("Se ha enviado el e-mail de confirmación con éxito a "+representative.getEmail());
    return ResponseEntity.ok(message);

  }

}
