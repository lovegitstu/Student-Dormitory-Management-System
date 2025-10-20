package com.springboot.dorm.mapper;

import java.util.List;
import com.springboot.dorm.domain.DormScore;

/**
 * 宿舍评分Mapper接口
 * 
 *
 * @date 2025-09-18
 */
public interface DormScoreMapper 
{
    /**
     * 查询宿舍评分
     * 
     * @param scoreId 宿舍评分主键
     * @return 宿舍评分
     */
    public DormScore selectDormScoreByScoreId(Long scoreId);

    /**
     * 查询宿舍评分列表
     * 
     * @param dormScore 宿舍评分
     * @return 宿舍评分集合
     */
    public List<DormScore> selectDormScoreList(DormScore dormScore);

    /**
     * 新增宿舍评分
     * 
     * @param dormScore 宿舍评分
     * @return 结果
     */
    public int insertDormScore(DormScore dormScore);

    /**
     * 修改宿舍评分
     * 
     * @param dormScore 宿舍评分
     * @return 结果
     */
    public int updateDormScore(DormScore dormScore);

    /**
     * 删除宿舍评分
     * 
     * @param scoreId 宿舍评分主键
     * @return 结果
     */
    public int deleteDormScoreByScoreId(Long scoreId);

    /**
     * 批量删除宿舍评分
     * 
     * @param scoreIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormScoreByScoreIds(Long[] scoreIds);

    /**
     * 根据学生ID查询最近的评分记录
     * 
     * @param studentId 学生ID
     * @return 最近的评分记录
     */
    List<DormScore> selectRecentScoresByStudentId(Long studentId);

    /**
     * 根据学生ID查询最近的评分记录（用于统计服务）
     * 
     * @param studentId 学生ID
     * @return 最近的评分记录
     */
    List<DormScore> getRecentScoresByStudentId(Long studentId);

    /**
     * 根据学生ID和月份查询评分记录
     * 
     * @param studentId 学生ID
     * @param month 月份（格式：YYYY-MM）
     * @return 月度评分记录
     */
    List<DormScore> getMonthlyScoresByStudentId(Long studentId, String month);
}