package com.fuzhi.fuzhisever.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.fuzhi.fuzhisever.DTO.RegisterRequestDTO;
import com.fuzhi.fuzhisever.Model.User;
import com.fuzhi.fuzhisever.Repository.UserRepository;
import com.fuzhi.fuzhisever.Service.PasswordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    @PostMapping("/doLogin")
    public ResponseEntity<SaResult> doLogin(@RequestParam String email, @RequestParam String pwd) {
        try {
            User user = userRepository.findUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(401).body(SaResult.error("用户不存在"));
            }
            if (passwordService.checkPassword(pwd, user.getPwd())) {
                StpUtil.login(user.getId());
                return ResponseEntity.ok(SaResult.ok("登录成功"));
            }
            return ResponseEntity.status(401).body(SaResult.error("登录失败"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(SaResult.error("登录过程中发生错误: " + e.getMessage()));
        }
    }


    @GetMapping("/isLogin")
    public ResponseEntity<SaResult> isLogin() {
        try {
            boolean isLoggedIn = StpUtil.isLogin();
            return ResponseEntity.ok(SaResult.ok("是否登录：" + isLoggedIn));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(SaResult.error("Failed to check login status: " + e.getMessage()));
        }
    }


    @GetMapping("/tokenInfo")
    @SaCheckLogin
    public ResponseEntity<SaResult> tokenInfo() {
        try {
            return ResponseEntity.ok(SaResult.data(StpUtil.getTokenInfo()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(SaResult.error("Failed to get token info: " + e.getMessage()));
        }
    }


    @PostMapping("/logout")
    @SaCheckLogin
    public ResponseEntity<SaResult> logout() {
        try {
            StpUtil.logout();
            return ResponseEntity.ok(SaResult.ok());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(SaResult.error("Logout failed: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<SaResult> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {

        if (userRepository.findUserByEmail(registerRequest.getEmail()) != null) {
            return new ResponseEntity<>(SaResult.error("用户已存在"), HttpStatus.BAD_REQUEST);
        }


        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPwd(passwordService.hashPassword(registerRequest.getPwd()));
        newUser.setGender(registerRequest.getGender());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setName(registerRequest.getName());
        newUser.setAge(registerRequest.getAge());


        userRepository.save(newUser);

        return new ResponseEntity<>(SaResult.ok("注册成功"), HttpStatus.CREATED);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
