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

    /**
     * 根据用户ID查找用户信息。
     * 该方法会尝试从缓存中获取用户信息，如果缓存中不存在，则从数据库中查找。
     * 如果用户未找到，会记录错误日志并抛出自定义业务异常。
     *
     * @param userId 用户的唯一标识符
     * @return 找到的 User 对象
     * @throws BusinessException 如果用户未找到，抛出此异常
     */
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


    /**
     * 保存或更新用户信息。
     * 该方法会将用户信息保存到数据库中，并清除相应的缓存。
     *
     * @param user 要保存或更新的 User 对象
     */
    @CacheEvict(value = "userInfo", key = "#user.id")
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
