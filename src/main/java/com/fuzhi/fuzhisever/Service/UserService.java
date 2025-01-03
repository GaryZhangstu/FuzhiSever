package com.fuzhi.fuzhisever.Service;

import com.fuzhi.fuzhisever.Model.User;
import com.fuzhi.fuzhisever.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //寻找用户通过id
    @Cacheable(value = "userInfo", key = "#id")
    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }
}
