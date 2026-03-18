package com.awsome.shop.auth.application.impl.service.user;

import com.awsome.shop.auth.application.api.dto.user.UserDTO;
import com.awsome.shop.auth.application.api.dto.user.request.GetUserRequest;
import com.awsome.shop.auth.application.api.dto.user.request.ListUserRequest;
import com.awsome.shop.auth.application.api.dto.user.request.UpdateUserRequest;
import com.awsome.shop.auth.common.dto.PageResult;
import com.awsome.shop.auth.common.exception.BusinessException;
import com.awsome.shop.auth.domain.model.user.Role;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.model.user.UserStatus;
import com.awsome.shop.auth.domain.service.user.UserDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceImplTest {

    @Mock
    private UserDomainService userDomainService;

    @InjectMocks
    private UserApplicationServiceImpl userApplicationService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("admin");
        user.setName("管理员");
        user.setEmployeeId("EMP000");
        user.setRole(Role.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
    }

    // ==================== getCurrentUser ====================

    @Test
    void getCurrentUser_returnsDTO() {
        when(userDomainService.getById(1L)).thenReturn(user);

        UserDTO result = userApplicationService.getCurrentUser(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getRole()).isEqualTo("ADMIN");
    }

    // ==================== getUser ====================

    @Test
    void getUser_returnsDTO() {
        when(userDomainService.getById(1L)).thenReturn(user);

        GetUserRequest request = new GetUserRequest();
        request.setId(1L);

        UserDTO result = userApplicationService.getUser(request);

        assertThat(result.getUsername()).isEqualTo("admin");
    }

    // ==================== listUsers ====================

    @Test
    void listUsers_returnsPaginatedResult() {
        when(userDomainService.listByPage(null, 1, 10)).thenReturn(List.of(user));
        when(userDomainService.countByKeyword(null)).thenReturn(1L);

        ListUserRequest request = new ListUserRequest();
        request.setPage(1);
        request.setSize(10);

        PageResult<UserDTO> result = userApplicationService.listUsers(request);

        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getPages()).isEqualTo(1L);
    }

    @Test
    void listUsers_pagesCalculation() {
        when(userDomainService.listByPage(null, 1, 2)).thenReturn(List.of(user));
        when(userDomainService.countByKeyword(null)).thenReturn(5L);

        ListUserRequest request = new ListUserRequest();
        request.setPage(1);
        request.setSize(2);

        PageResult<UserDTO> result = userApplicationService.listUsers(request);

        // 5 条数据 / 每页 2 条 = 3 页
        assertThat(result.getPages()).isEqualTo(3L);
    }

    // ==================== updateUser ====================

    @Test
    void updateUser_success() {
        when(userDomainService.updateUser(1L, 2L, "新名字", UserStatus.DISABLED)).thenReturn(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(1L);
        request.setOperatorId(2L);
        request.setName("新名字");
        request.setStatus("DISABLED");

        UserDTO result = userApplicationService.updateUser(request);

        assertThat(result).isNotNull();
        verify(userDomainService).updateUser(1L, 2L, "新名字", UserStatus.DISABLED);
    }

    @Test
    void updateUser_invalidStatus_throwsBadRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(1L);
        request.setOperatorId(1L);
        request.setStatus("INVALID_STATUS");

        assertThatThrownBy(() -> userApplicationService.updateUser(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo("BAD_REQUEST_001");
    }

    @Test
    void updateUser_nullOperatorIdAndStatus_passesNulls() {
        when(userDomainService.updateUser(1L, null, "name", null)).thenReturn(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(1L);
        // operatorId and status are null
        request.setName("name");

        userApplicationService.updateUser(request);

        verify(userDomainService).updateUser(1L, null, "name", null);
    }
}
