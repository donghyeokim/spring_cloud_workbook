package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        // 1. userId 세팅 (UUID)
        userDto.setUserId(UUID.randomUUID().toString());

        // 2. DTO -> Entity 변환
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        // 3. 비밀번호 암호화
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        // 4. 엔티티 영속화
        userRepository.save(userEntity);
        return mapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        // 1. 유저 조회
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // 2. 엔티티 -> dto 변환
        UserDto userDto = mapper.map(userEntity, UserDto.class);
        // 3. 주문 세팅
        List<ResponseOrder> orders = new ArrayList<>();
        userDto.setOrders(orders);
        return userDto;
    }

    @Override
    public Iterable<UserDto> getUserByAll() {
        Iterable<UserEntity> userEntities = userRepository.findAll();
        return StreamSupport.stream(userEntities.spliterator(), false)
                .map(userEntity -> mapper.map(userEntity, UserDto.class))
                .collect(Collectors.toList());
    }
}
