package com.awsome.shop.auth.domain.impl.service.user;

import com.awsome.shop.auth.common.enums.AuthErrorCode;
import com.awsome.shop.auth.common.exception.BusinessException;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.model.user.UserStatus;
import com.awsome.shop.auth.domain.service.user.UserDomainService;
import com.awsome.shop.auth.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户领域服务实现
 */
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;

    @Override
    public UserEntity getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.NOT_FOUND_001));
    }

    @Override
    public List<UserEntity> listByPage(String keyword, int page, int size) {
        return userRepository.findByPage(keyword, page, size);
    }

    @Override
    public long countByKeyword(String keyword) {
        return userRepository.countByKeyword(keyword);
    }

    @Override
    @Transactional
    public UserEntity updateUser(Long id, Long operatorId, String name, UserStatus status) {
        UserEntity user = getById(id);

        // 不能禁用自己
        if (status == UserStatus.DISABLED && id.equals(operatorId)) {
            throw new BusinessException(AuthErrorCode.BAD_REQUEST_002);
        }

        user.updateInfo(name, status);
        return userRepository.update(user);
    }
}
