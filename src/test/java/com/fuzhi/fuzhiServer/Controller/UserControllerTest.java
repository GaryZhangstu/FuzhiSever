package com.fuzhi.fuzhiServer.Controller;

import com.fuzhi.fuzhiServer.DTO.ApiResponse;
import com.fuzhi.fuzhiServer.DTO.RegisterRequestDTO;
import com.fuzhi.fuzhiServer.Model.User;
import com.fuzhi.fuzhiServer.Repository.UserRepository;
import com.fuzhi.fuzhiServer.Service.CommunicationService;
import com.fuzhi.fuzhiServer.Service.PasswordService;
import com.fuzhi.fuzhiServer.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private CommunicationService communicationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;
    private RegisterRequestDTO registerRequest;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("test@example.com");
        user.setPwd("hashedPassword");

        registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPwd("password");
    }

    @Test
    public void testDoLogin() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(user);
        when(passwordService.checkPassword(anyString(), anyString())).thenReturn(true);

        ResponseEntity<ApiResponse<SaTokenInfo>> response = userController.doLogin("test@example.com", "password");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findUserByEmail(anyString());
        verify(passwordService, times(1)).checkPassword(anyString(), anyString());
    }

    @Test
    public void testRegister() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);
        when(modelMapper.map(any(), any())).thenReturn(user);
        when(passwordService.hashPassword(anyString())).thenReturn("hashedPassword");

        ResponseEntity<ApiResponse<String>> response = userController.register(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepository, times(1)).findUserByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUploadAvatar() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        when(file.isEmpty()).thenReturn(false);
        when(userService.findUserById(anyString())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse<String>> response = userController.uploadAvatar(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(communicationService, times(1)).uploadFileToS3(any(), anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
