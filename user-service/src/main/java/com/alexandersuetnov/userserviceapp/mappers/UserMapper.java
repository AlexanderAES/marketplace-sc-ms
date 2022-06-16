package com.alexandersuetnov.userserviceapp.mappers;

import com.alexandersuetnov.userserviceapp.dto.UserDTO;
import com.alexandersuetnov.userserviceapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "user.products",target = "productsDTOList")
    UserDTO UserToUserDTO(User user);
}
