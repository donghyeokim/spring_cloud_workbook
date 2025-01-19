package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        // 1. userId 세팅 (UUID)
        userDto.setUserId(UUID.randomUUID().toString());

        // 2. DTO -> Entity 변환
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        // 3. 비밀번호 암호화
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        // 4. 엔티티 영속화
        userRepository.save(userEntity);
        return mapper.map(userEntity, UserDto.class);
    }
}
