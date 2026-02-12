package com.awsome.shop.auth.domain.service.user;

import com.awsome.shop.auth.common.dto.PageResult;
import com.awsome.shop.auth.domain.model.user.UserEntity;

/**
 * 用户领域服务接口
 */
public interface UserDomainService {

    PageResult<UserEntity> page(int page, int size, String username, String role, String status);
}
