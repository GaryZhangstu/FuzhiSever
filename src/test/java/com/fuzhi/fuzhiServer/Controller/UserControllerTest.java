package com.fuzhi.fuzhiServer.Controller;

import com.fuzhi.fuzhiServer.DTO.ApiResponse;
import com.fuzhi.fuzhiServer.DTO.RegisterRequestDTO;
import com.fuzhi.fuzhiServer.DTO.UserDTO;
import com.fuzhi.fuzhiServer.Exception.BusinessException;
import com.fuzhi.fuzhiServer.Exception.ErrorCode;
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

import static org.junit.jupiter.api.Assertions.*;
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
    private RegisterRequestDTO registerRequestDTO;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("test@example.com");
        user.setPwd("hashedPassword");

        registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setEmail("test@example.com");
        registerRequestDTO.setPwd("password");
    }

    @Test
    public void testDoLogin_Success() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(user);
        when(passwordService.checkPassword(anyString(), anyString())).thenReturn(true);

        ResponseEntity<ApiResponse<SaTokenInfo>> response = userController.doLogin("test@example.com", "password");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testDoLogin_UserNotFound() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userController.doLogin("test@example.com", "password");
        });

        assertEquals(ErrorCode.USERNAME_OR_PASSWORD_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void testDoLogin_PasswordMismatch() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(user);
        when(passwordService.checkPassword(anyString(), anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userController.doLogin("test@example.com", "password");
        });

        assertEquals(ErrorCode.USERNAME_OR_PASSWORD_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void testRegister_Success() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);
        when(modelMapper.map(any(RegisterRequestDTO.class), any(Class.class))).thenReturn(user);
        when(passwordService.hashPassword(anyString())).thenReturn("hashedPassword");

        ResponseEntity<ApiResponse<String>> response = userController.register(registerRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(user);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userController.register(registerRequestDTO);
        });

        assertEquals(ErrorCode.USER_ALREADY_EXISTS.getCode(), exception.getCode());
    }

    @Test
    public void testUploadAvatar_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        when(file.isEmpty()).thenReturn(false);
        when(userService.findUserById(anyString())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse<String>> response = userController.uploadAvatar(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testUploadAvatar_FileEmpty() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userController.uploadAvatar(file);
        });

        assertEquals(ErrorCode.FILE_EMPTY.getCode(), exception.getCode());
    }

    @Test
    public void testUploadAvatar_UserNotFound() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        when(file.isEmpty()).thenReturn(false);
        when(userService.findUserById(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userController.uploadAvatar(file);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    public void testUpdatePassword_Success() {
        when(userService.findUserById(anyString())).thenReturn(Optional.of(user));
        when(passwordService.checkPassword(anyString(), anyString())).thenReturn(true);
        when(passwordService.hashPassword(anyString())).thenReturn("newHashedPassword");

        ResponseEntity<ApiResponse<String>> response = userController.updatePassword("oldPassword", "newPassword");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testUpdatePassword_UserNotFound() {
        when(userService.findUserById(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userController.updatePassword("oldPassword", "newPassword");
        });

        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    public void testUpdatePassword_PasswordMismatch() {
        when(userService.findUserById(anyString())).thenReturn(Optional.of(user));
        when(passwordService.checkPassword(anyString(), anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userController.updatePassword("oldPassword", "newPassword");
        });

        assertEquals(ErrorCode.WRONG_PASSWORD.getCode(), exception.getCode());
    }

    @Test
    public void testGetUserInfo_Success() {
        when(userService.findUserById(anyString())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), any(Class.class))).thenReturn(new UserDTO());

        ResponseEntity<ApiResponse<UserDTO>> response = userController.getUserInfo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testGetUserInfo_UserNotFound() {
        when(userService.findUserById(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userController.getUserInfo();
        });

        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }
}
