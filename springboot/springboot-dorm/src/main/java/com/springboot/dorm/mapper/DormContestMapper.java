package com.springboot.dorm.mapper;

import java.util.List;
import com.springboot.dorm.domain.DormContest;

/**
 * 宿舍评分Mapper接口
 * 
 *
 * @date 2025-09-18
 */
public interface DormContestMapper 
{
    /**
     * 查询宿舍评分
     * 
     * @param conId 宿舍评分主键
     * @return 宿舍评分
     */
    public DormContest selectDormContestByConId(Integer conId);

    /**
     * 查询宿舍评分列表
     * 
     * @param dormContest 宿舍评分
     * @return 宿舍评分集合
     */
    public List<DormContest> selectDormContestList(DormContest dormContest);

    /**
     * 新增宿舍评分
     * 
     * @param dormContest 宿舍评分
     * @return 结果
     */
    public int insertDormContest(DormContest dormContest);

    /**
     * 修改宿舍评分
     * 
     * @param dormContest 宿舍评分
     * @return 结果
     */
    public int updateDormContest(DormContest dormContest);

    /**
     * 删除宿舍评分
     * 
     * @param conId 宿舍评分主键
     * @return 结果
     */
    public int deleteDormContestByConId(Integer conId);

    /**
     * 批量删除宿舍评分
     * 
     * @param conIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormContestByConIds(Integer[] conIds);
}
