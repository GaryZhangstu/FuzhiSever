package com.fuzhi.fuzhiServer.Service;

import com.fuzhi.fuzhiServer.Model.User;
import com.fuzhi.fuzhiServer.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //寻找用户通过id
    @Cacheable(value = "userInfo", key = "#userId")
    public Optional<User> findUserById(String userId) {
        return userRepository.findById(userId);
    }
}
