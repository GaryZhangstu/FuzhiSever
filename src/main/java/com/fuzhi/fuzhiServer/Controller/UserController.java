package com.fuzhi.fuzhiServer.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final CommunicationService communicationService;
    private final UserService userService;

    @PostMapping("/doLogin")
    @SaIgnore
    public ResponseEntity<ApiResponse<SaTokenInfo>> doLogin(@RequestParam String email, @RequestParam String pwd) {
        log.info("Received login request for email: {}", email);

        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            log.error("User not found for email: {}", email);
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (passwordService.checkPassword(pwd, user.getPwd())) {
            log.error("Password mismatch for email: {}", email);
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        log.info("User logged in successfully, token info: {}", tokenInfo);
        return ResponseEntity.ok(ApiResponse.success(tokenInfo));
    }

    @GetMapping("/isLogin")
    @SaIgnore
    public ResponseEntity<ApiResponse<Boolean>> isLogin() {
        boolean isLoggedIn = StpUtil.isLogin();
        log.info("Checking if user is logged in: {}", isLoggedIn);
        return ResponseEntity.ok(ApiResponse.success(isLoggedIn));
    }

    @GetMapping("/tokenInfo")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<SaTokenInfo>> tokenInfo() {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        log.info("Returning token info: {}", tokenInfo);
        return ResponseEntity.ok(ApiResponse.success(tokenInfo));
    }

    @PostMapping("/logout")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<String>> logout() {
        StpUtil.logout();
        log.info("User logged out successfully");
        return ResponseEntity.ok(ApiResponse.success("登出成功"));
    }

    @PostMapping("/register")
    @SaIgnore
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        log.info("Received registration request for email: {}", registerRequest.getEmail());

        if (userRepository.findUserByEmail(registerRequest.getEmail()) != null) {
            log.error("User already exists for email: {}", registerRequest.getEmail());
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User newUser = modelMapper.map(registerRequest, User.class);
        newUser.setPwd(passwordService.hashPassword(registerRequest.getPwd()));
        userService.saveUser(newUser);
        log.info("User registered successfully, email: {}", registerRequest.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("注册成功"));
    }

    @PostMapping("/uploadAvatar")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<String>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Received avatar upload request with file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            log.error("File is empty: {}", file.getOriginalFilename());
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        String userId = StpUtil.getLoginId().toString();
        User user = userService.findUserById(userId);


        UUID uuid = UUID.randomUUID();
        String key = "avatar/" + userId + "/" + uuid + file.getOriginalFilename();

        communicationService.uploadFileToS3(file.getInputStream(), key);
        user.setAvatar(key);
        userService.saveUser(user);
        log.info("Avatar uploaded successfully for user ID: {}", userId);

        return ResponseEntity.ok(ApiResponse.success("头像上传成功"));
    }

    @PutMapping("/updatePassword")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<String>> updatePassword(@RequestParam String oldPwd, @RequestParam String newPwd) {
        log.info("Received password update request");

        String userId = StpUtil.getLoginId().toString();
        User user = userService.findUserById(userId);


        if (passwordService.checkPassword(oldPwd, user.getPwd())) {
            log.info("Password mismatch for user ID: {}", userId);
            throw new BusinessException(ErrorCode.WRONG_PASSWORD);
        }

        user.setPwd(passwordService.hashPassword(newPwd));
        userService.saveUser(user);
        log.info("Password updated successfully for user ID: {}", userId);

        return ResponseEntity.ok(ApiResponse.success("密码更新成功"));
    }

    @GetMapping("/userInfo")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<UserDTO>> getUserInfo() {
        log.info("Received request to get user info");

        String userId = StpUtil.getLoginId().toString();
        User user = userService.findUserById(userId);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        log.info("Returning user info: {}", userDTO);
        return ResponseEntity.ok(ApiResponse.success(userDTO));
    }

    //更新用户信息
    @PutMapping("/updateUserInfo")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<String>> updateUserInfo(@RequestBody UserDTO userDTO) {
        log.info("Received request to update user info: {}", userDTO);
        String userId = StpUtil.getLoginId().toString();
        User user = userService.findUserById(userId);
        user.setName(userDTO.getName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());
        user.setGender(userDTO.getGender());

        userService.saveUser(user);
        log.info("User info updated successfully for user ID: {}", userId);
        return ResponseEntity.ok(ApiResponse.success("用户信息更新成功"));
    }

}

