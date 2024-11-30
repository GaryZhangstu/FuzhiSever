package com.fuzhi.fuzhisever.Service;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    public String hashPassword(String plainTextPassword) {
        // 生成哈希值
        return BCrypt.hashpw(plainTextPassword);
    }

    public boolean checkPassword(String plainTextPassword, String hashedPassword) {
        // 验证密码
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
