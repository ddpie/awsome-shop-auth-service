package com.awsome.shop.auth.domain.impl.service.user;

import com.awsome.shop.auth.common.exception.BusinessException;
import com.awsome.shop.auth.domain.model.user.Role;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.model.user.UserStatus;
import com.awsome.shop.auth.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDomainServiceImpl userDomainService;

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

    // ==================== getById ====================

    @Test
    void getById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserEntity result = userDomainService.getById(1L);

        assertThat(result.getUsername()).isEqualTo("admin");
    }

    @Test
    void getById_notFound_throwsNotFound001() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDomainService.getById(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo("NOT_FOUND_001");
    }

    // ==================== listByPage ====================

    @Test
    void listByPage_delegatesToRepository() {
        when(userRepository.findByPage("admin", 1, 10)).thenReturn(List.of(user));

        List<UserEntity> result = userDomainService.listByPage("admin", 1, 10);

        assertThat(result).hasSize(1);
        verify(userRepository).findByPage("admin", 1, 10);
    }

    // ==================== updateUser ====================

    @Test
    void updateUser_updateName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        UserEntity result = userDomainService.updateUser(1L, 2L, "新名字", null);

        assertThat(result.getName()).isEqualTo("新名字");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE); // 未改变
    }

    @Test
    void updateUser_disableOther() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        UserEntity result = userDomainService.updateUser(1L, 2L, null, UserStatus.DISABLED);

        assertThat(result.getStatus()).isEqualTo(UserStatus.DISABLED);
    }

    @Test
    void updateUser_disableSelf_throwsBadRequest002() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userDomainService.updateUser(1L, 1L, null, UserStatus.DISABLED))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo("BAD_REQUEST_002");
    }

    @Test
    void updateUser_activateSelf_allowed() {
        // 激活自己是允许的，只有禁用自己才报错
        user.setStatus(UserStatus.DISABLED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        UserEntity result = userDomainService.updateUser(1L, 1L, null, UserStatus.ACTIVE);

        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
