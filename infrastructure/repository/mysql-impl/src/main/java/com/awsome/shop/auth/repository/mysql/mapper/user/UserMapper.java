package com.awsome.shop.auth.repository.mysql.mapper.user;

import com.awsome.shop.auth.repository.mysql.po.user.UserPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {

    /**
     * 分页查询用户列表
     *
     * @param page    MyBatis-Plus 分页对象
     * @param keyword 搜索关键词（模糊匹配 username/name/employeeId），可为 null
     * @return 分页结果
     */
    IPage<UserPO> selectPageByKeyword(IPage<UserPO> page, @Param("keyword") String keyword);

    /**
     * 统计符合条件的用户总数
     *
     * @param keyword 搜索关键词，可为 null
     * @return 用户总数
     */
    long countByKeyword(@Param("keyword") String keyword);
}
