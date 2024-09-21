package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.User;
import com.bristotartur.cedupscore_api.dtos.request.RequestUserDto;
import com.bristotartur.cedupscore_api.dtos.response.ResponseUserDto;
import com.bristotartur.cedupscore_api.enums.RoleType;
import com.bristotartur.cedupscore_api.exceptions.BadRequestException;
import com.bristotartur.cedupscore_api.exceptions.ConflictException;
import com.bristotartur.cedupscore_api.exceptions.NotFoundException;
import com.bristotartur.cedupscore_api.mappers.UserMapper;
import com.bristotartur.cedupscore_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> findUsersByRole(RoleType role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    public User findUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    public User findUserByEmail(String email) {
        this.validateEmail(email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    public ResponseUserDto createUserResponseDto(User user) {
        return userMapper.toUserResponseDto(user);
    }

    public User signupUser(RequestUserDto dto) {
        this.validateEmail(dto.email());

        var isEmailInUse = userRepository.findByEmail(dto.email()).isPresent();

        if (isEmailInUse) {
            throw new ConflictException("Email já está em uso.");
        }
        var password = passwordEncoder.encode(dto.password());

        return userRepository.save(userMapper.toNewUser(dto, password));
    }

    public User replaceUser(Long id, RequestUserDto dto) {
        this.findUserById(id);
        this.validateEmail(dto.email());

        var userWithEmail = userRepository.findByEmail(dto.email());

        if (userWithEmail.isPresent() && !id.equals(userWithEmail.get().getId())) {
            throw new ConflictException("Email já está em uso.");
        }
        var password = passwordEncoder.encode(dto.password());

        return userRepository.save(userMapper.toExistingUser(id, dto, password));
    }

    private void validateEmail(String email) {
        var pattern = Pattern.compile(EMAIL_REGEX);
        var matcher = pattern.matcher(email);

        if (!matcher.matches()) throw new BadRequestException("Email inválido.");
    }

}
