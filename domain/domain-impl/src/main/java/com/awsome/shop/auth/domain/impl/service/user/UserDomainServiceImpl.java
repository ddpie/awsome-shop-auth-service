package com.awsome.shop.auth.domain.impl.service.user;

import com.awsome.shop.auth.common.dto.PageResult;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.service.user.UserDomainService;
import com.awsome.shop.auth.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户领域服务实现
 */
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;

    @Override
    public PageResult<UserEntity> page(int page, int size, String username, String role, String status) {
        return userRepository.page(page, size, username, role, status);
    }
}
