package com.fuzhi.fuzhiServer.Service;

import com.fuzhi.fuzhiServer.Exception.BusinessException;
import com.fuzhi.fuzhiServer.Exception.ErrorCode;
import com.fuzhi.fuzhiServer.Model.User;
import com.fuzhi.fuzhiServer.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {
    private final UserRepository userRepository;

    //寻找用户通过id
    @Cacheable(value = "userInfo", key = "#userId")
    public User findUserById(String userId) {
    // 尝试从数据库中查找用户
    return userRepository.findById(userId)
        .orElseThrow(() -> {
            // 如果用户未找到，记录错误日志
            log.error("User not found for ID: {}", userId);
            // 抛出自定义业务异常
            return new BusinessException(ErrorCode.USER_NOT_FOUND);
        });
}


    //更新用户信息
    @CacheEvict(value = "userInfo", key = "#user.id")
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
