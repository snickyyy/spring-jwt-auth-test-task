package sc.snicky.springbootjwtauth.api.v1.admin.mappers;

import org.mapstruct.Mapper;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.UserDTO;
import sc.snicky.springbootjwtauth.api.v1.domain.models.User;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    UserDTO toUserDTO(User user);
}
