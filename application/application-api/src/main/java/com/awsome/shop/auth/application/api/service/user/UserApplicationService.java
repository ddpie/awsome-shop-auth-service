package com.awsome.shop.auth.application.api.service.user;

import com.awsome.shop.auth.application.api.dto.user.UserDTO;
import com.awsome.shop.auth.application.api.dto.user.request.ListUserRequest;
import com.awsome.shop.auth.common.dto.PageResult;

/**
 * 用户管理应用服务接口
 */
public interface UserApplicationService {

    PageResult<UserDTO> list(ListUserRequest request);
}
