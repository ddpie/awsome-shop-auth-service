package com.awsome.shop.auth.application.api.client;

/**
 * 积分服务客户端接口（跨服务调用）
 */
public interface PointsServiceClient {

    /**
     * 查询用户积分余额
     *
     * @param userId 用户ID
     * @return 积分余额，失败返回 null
     */
    Integer getBalance(Long userId);

    /**
     * 初始化用户积分余额
     *
     * @param userId         用户ID
     * @param initialBalance 初始积分
     */
    void initPoints(Long userId, int initialBalance);
}
