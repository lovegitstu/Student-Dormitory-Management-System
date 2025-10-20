package com.springboot.dorm.service;

import java.util.Map;

/**
 * 宿舍统计Service接口
 * 
 *
 * @date 2025-10-30
 */
public interface IDormStatisticsService 
{
    /**
     * 获取系统管理员统计数据
     * 
     * @return 统计数据
     */
    Map<String, Object> getAdminStatistics();

    /**
     * 获取宿管统计数据
     * 
     * @param userId 用户ID
     * @return 统计数据
     */
    Map<String, Object> getManagerStatistics(Long userId);

    /**
     * 获取学生统计数据
     * 
     * @param userId 用户ID
     * @return 统计数据
     */
    Map<String, Object> getStudentStatistics(Long userId);

    /**
     * 获取宿舍入住率统计
     * 
     * @return 统计数据
     */
    Map<String, Object> getOccupancyStatistics();

    /**
     * 获取申请处理统计
     * 
     * @return 统计数据
     */
    Map<String, Object> getApplicationStatistics();

    /**
     * 获取报修处理统计
     * 
     * @return 统计数据
     */
    Map<String, Object> getRepairStatistics();

    /**
     * 获取水电费统计
     * 
     * @return 统计数据
     */
    Map<String, Object> getBillsStatistics();
}