package com.awsome.shop.auth.repository.user;

import com.awsome.shop.auth.domain.model.user.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口（Port）
 */
public interface UserRepository {

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmployeeId(String employeeId);

    boolean existsByUsername(String username);

    boolean existsByEmployeeId(String employeeId);

    UserEntity save(UserEntity user);

    UserEntity update(UserEntity user);

    /**
     * 分页查询用户列表
     *
     * @param keyword 搜索关键词（模糊匹配 username/name/employeeId），可为 null
     * @param page    页码（从 1 开始）
     * @param size    每页大小
     * @return 用户列表
     */
    List<UserEntity> findByPage(String keyword, int page, int size);

    /**
     * 统计符合条件的用户总数
     */
    long countByKeyword(String keyword);
}
