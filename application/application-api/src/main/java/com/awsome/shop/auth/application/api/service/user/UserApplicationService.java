package com.awsome.shop.auth.application.api.service.user;

import com.awsome.shop.auth.application.api.dto.user.UserDTO;
import com.awsome.shop.auth.application.api.dto.user.request.GetUserRequest;
import com.awsome.shop.auth.application.api.dto.user.request.ListUserRequest;
import com.awsome.shop.auth.application.api.dto.user.request.UpdateUserRequest;
import com.awsome.shop.auth.common.dto.PageResult;

/**
 * 用户应用服务接口
 */
public interface UserApplicationService {

    UserDTO getCurrentUser(Long operatorId);

    UserDTO getUser(GetUserRequest request);

    PageResult<UserDTO> listUsers(ListUserRequest request);

    UserDTO updateUser(UpdateUserRequest request);
}
