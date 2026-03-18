package com.awsome.shop.auth.infrastructure.security.crypto;

import com.awsome.shop.auth.infrastructure.security.api.service.PasswordService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * bcrypt 密码服务实现
 */
@Service
public class BcryptPasswordServiceImpl implements PasswordService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
