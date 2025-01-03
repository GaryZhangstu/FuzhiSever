package com.fuzhi.fuzhisever.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;

import com.fuzhi.fuzhisever.DTO.ApiResponse;
import com.fuzhi.fuzhisever.DTO.RegisterRequestDTO;
import com.fuzhi.fuzhisever.DTO.UserDTO;
import com.fuzhi.fuzhisever.Exception.BusinessException;
import com.fuzhi.fuzhisever.Exception.ErrorCode;
import com.fuzhi.fuzhisever.Model.User;
import com.fuzhi.fuzhisever.Repository.UserRepository;
import com.fuzhi.fuzhisever.Service.CommunicationService;
import com.fuzhi.fuzhisever.Service.PasswordService;
import com.fuzhi.fuzhisever.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final ModelMapper modelMapper;

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final CommunicationService communicationService;
    private final UserService userService;
    @PostMapping("/doLogin")
    @SaIgnore
    public ResponseEntity<ApiResponse<?>> doLogin(@RequestParam String email, @RequestParam String pwd) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!passwordService.checkPassword(pwd, user.getPwd())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD);
        }

        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ResponseEntity.ok(ApiResponse.success(tokenInfo));
    }


    @GetMapping("/isLogin")
    @SaIgnore
    public ResponseEntity<ApiResponse<?>> isLogin() {
        boolean isLoggedIn = StpUtil.isLogin();
        return ResponseEntity.ok(ApiResponse.success(isLoggedIn));
    }


    @GetMapping("/tokenInfo")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<?>> tokenInfo() {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ResponseEntity.ok(ApiResponse.success(tokenInfo));
    }


    @PostMapping("/logout")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<?>> logout() {
        StpUtil.logout();
        return ResponseEntity.ok(ApiResponse.success("登出成功"));
    }

    @PostMapping("/register")
    @SaIgnore
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        if (userRepository.findUserByEmail(registerRequest.getEmail()) != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User newUser = modelMapper.map(registerRequest, User.class);
        newUser.setPwd(passwordService.hashPassword(registerRequest.getPwd()));
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("注册成功"));
    }


    @PostMapping("/uploadAvatar")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<?>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        String userId = StpUtil.getLoginId().toString();
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UUID uuid = UUID.randomUUID();
        String key = "avatar/" + userId + "/" + uuid + file.getOriginalFilename();

        communicationService.uploadFileToS3(file.getInputStream(), key);
        user.setAvatar(key);
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("头像上传成功"));
    }

    @PutMapping("/updatePassword")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<?>> updatePassword(@RequestParam String oldPwd, @RequestParam String newPwd) {
        String userId = StpUtil.getLoginId().toString();
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordService.checkPassword(oldPwd, user.getPwd())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD);
        }

        user.setPwd(passwordService.hashPassword(newPwd));
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("密码更新成功"));
    }

    @GetMapping("/userInfo")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<?>> getUserInfo() {
        String userId = StpUtil.getLoginId().toString();
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(ApiResponse.success(userDTO));
    }
}
