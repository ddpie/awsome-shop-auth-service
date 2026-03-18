package com.awsome.shop.auth.repository.mysql.impl.user;

import com.awsome.shop.auth.common.exception.SystemException;
import com.awsome.shop.auth.domain.model.user.Role;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.model.user.UserStatus;
import com.awsome.shop.auth.repository.mysql.mapper.user.UserMapper;
import com.awsome.shop.auth.repository.mysql.po.user.UserPO;
import com.awsome.shop.auth.repository.user.UserRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户仓储实现
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public Optional<UserEntity> findById(Long id) {
        UserPO po = userMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUsername, username);
        UserPO po = userMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public Optional<UserEntity> findByEmployeeId(String employeeId) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getEmployeeId, employeeId);
        UserPO po = userMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByEmployeeId(String employeeId) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getEmployeeId, employeeId);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public UserEntity save(UserEntity user) {
        UserPO po = toPO(user);
        userMapper.insert(po);
        user.setId(po.getId());
        return user;
    }

    @Override
    public UserEntity update(UserEntity user) {
        UserPO po = toPO(user);
        userMapper.updateById(po);
        return user;
    }

    @Override
    public List<UserEntity> findByPage(String keyword, int page, int size) {
        IPage<UserPO> result = userMapper.selectPageByKeyword(new Page<>(page, size), keyword);
        return result.getRecords().stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public long countByKeyword(String keyword) {
        return userMapper.countByKeyword(keyword);
    }

    private UserEntity toEntity(UserPO po) {
        UserEntity entity = new UserEntity();
        entity.setId(po.getId());
        entity.setUsername(po.getUsername());
        entity.setPassword(po.getPassword());
        entity.setName(po.getName());
        entity.setEmployeeId(po.getEmployeeId());
        try {
            entity.setRole(Role.valueOf(po.getRole()));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SystemException("SYS_001",
                    "数据库用户ID=" + po.getId() + " 的 role 值无效: " + po.getRole(), e);
        }
        try {
            entity.setStatus(UserStatus.valueOf(po.getStatus()));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SystemException("SYS_001",
                    "数据库用户ID=" + po.getId() + " 的 status 值无效: " + po.getStatus(), e);
        }
        entity.setCreatedAt(po.getCreatedAt());
        entity.setUpdatedAt(po.getUpdatedAt());
        return entity;
    }

    private UserPO toPO(UserEntity entity) {
        UserPO po = new UserPO();
        po.setId(entity.getId());
        po.setUsername(entity.getUsername());
        po.setPassword(entity.getPassword());
        po.setName(entity.getName());
        po.setEmployeeId(entity.getEmployeeId());
        po.setRole(entity.getRole() != null ? entity.getRole().name() : null);
        po.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        return po;
    }
}
