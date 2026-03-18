package com.awsome.shop.auth.domain.service.user;

import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.model.user.UserStatus;

import java.util.List;

/**
 * 用户领域服务接口
 */
public interface UserDomainService {

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    UserEntity getById(Long id);

    /**
     * 分页查询用户列表
     *
     * @param keyword 搜索关键词
     * @param page    页码（从1开始）
     * @param size    每页大小
     * @return 用户列表
     */
    List<UserEntity> listByPage(String keyword, int page, int size);

    /**
     * 统计符合条件的用户总数
     *
     * @param keyword 搜索关键词
     * @return 用户总数
     */
    long countByKeyword(String keyword);

    /**
     * 更新用户信息（管理员操作）
     *
     * @param id         目标用户ID
     * @param operatorId 操作人ID（用于防止禁用自己）
     * @param name       姓名（可为null表示不更新）
     * @param status     状态（可为null表示不更新）
     */
    UserEntity updateUser(Long id, Long operatorId, String name, UserStatus status);
}
