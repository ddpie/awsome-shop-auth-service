package com.awsome.shop.auth.application.impl.service.user;

import com.awsome.shop.auth.application.api.dto.user.UserDTO;
import com.awsome.shop.auth.application.api.dto.user.request.GetUserRequest;
import com.awsome.shop.auth.application.api.dto.user.request.ListUserRequest;
import com.awsome.shop.auth.application.api.dto.user.request.UpdateUserRequest;
import com.awsome.shop.auth.application.api.client.PointsServiceClient;
import com.awsome.shop.auth.application.api.service.user.UserApplicationService;
import com.awsome.shop.auth.common.dto.PageResult;
import com.awsome.shop.auth.common.enums.AuthErrorCode;
import com.awsome.shop.auth.common.exception.BusinessException;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.model.user.UserStatus;
import com.awsome.shop.auth.domain.service.user.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户应用服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserDomainService userDomainService;
    private final PointsServiceClient pointsServiceClient;

    @Override
    public UserDTO getCurrentUser(Long operatorId) {
        UserEntity user = userDomainService.getById(operatorId);
        UserDTO dto = toDTO(user);
        try {
            dto.setPointBalance(pointsServiceClient.getBalance(operatorId));
        } catch (Exception e) {
            log.warn("查询用户积分失败, userId={}", operatorId, e);
        }
        return dto;
    }

    @Override
    public UserDTO getUser(GetUserRequest request) {
        UserEntity user = userDomainService.getById(request.getId());
        UserDTO dto = toDTO(user);
        try {
            dto.setPointBalance(pointsServiceClient.getBalance(request.getId()));
        } catch (Exception e) {
            log.warn("查询用户积分失败, userId={}", request.getId(), e);
        }
        return dto;
    }

    @Override
    public PageResult<UserDTO> listUsers(ListUserRequest request) {
        List<UserEntity> users = userDomainService.listByPage(
                request.getKeyword(), request.getPage(), request.getSize());
        long total = userDomainService.countByKeyword(request.getKeyword());

        List<UserDTO> dtos = users.stream().map(this::toDTO).collect(Collectors.toList());
        // 批量填充积分余额（降级处理：查询失败不影响用户列表）
        for (UserDTO dto : dtos) {
            try {
                dto.setPointBalance(pointsServiceClient.getBalance(dto.getId()));
            } catch (Exception e) {
                log.warn("查询用户积分失败, userId={}", dto.getId(), e);
            }
        }

        PageResult<UserDTO> result = new PageResult<>();
        result.setCurrentPage(Long.valueOf(request.getPage()));
        result.setSize(Long.valueOf(request.getSize()));
        result.setTotalElements(total);
        result.setTotalPages((total + request.getSize() - 1) / request.getSize());
        result.setContent(dtos);
        return result;
    }

    @Override
    public UserDTO updateUser(UpdateUserRequest request) {
        UserStatus status = parseUserStatus(request.getStatus());

        UserEntity user = userDomainService.updateUser(
                request.getId(), request.getOperatorId(), request.getName(), status);
        return toDTO(user);
    }

    private UserStatus parseUserStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return UserStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(AuthErrorCode.BAD_REQUEST_001,
                    "无效的状态值: " + status + "，允许值: ACTIVE, DISABLED");
        }
    }

    private UserDTO toDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setName(entity.getName());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setRole(entity.getRole().name());
        dto.setStatus(entity.getStatus().name());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
