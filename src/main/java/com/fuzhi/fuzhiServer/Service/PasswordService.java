package com.fuzhi.fuzhiServer.Service;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    /**
     * 对明文密码进行哈希处理。
     *
     * @param plainTextPassword 明文密码
     * @return 哈希后的密码
     */
    public String hashPassword(String plainTextPassword) {
        // 生成哈希值
        return BCrypt.hashpw(plainTextPassword);
    }

    /**
     * 验证明文密码是否与哈希密码匹配。
     *
     * @param plainTextPassword 明文密码
     * @param hashedPassword    哈希后的密码
     * @return 如果密码匹配，返回 true；否则返回 false
     */
    public boolean checkPassword(String plainTextPassword, String hashedPassword) {
        // 验证密码
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
