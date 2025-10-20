package com.springboot.dorm.service.impl;

import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormContestMapper;
import com.springboot.dorm.domain.DormContest;
import com.springboot.dorm.service.IDormContestService;

/**
 * 宿舍评分Service业务层处理
 * 
 *
 * @date 2025-09-18
 */
@Service
public class DormContestServiceImpl implements IDormContestService 
{
    @Autowired
    private DormContestMapper dormContestMapper;

    /**
     * 查询宿舍评分
     * 
     * @param conId 宿舍评分主键
     * @return 宿舍评分
     */
    @Override
    public DormContest selectDormContestByConId(Integer conId)
    {
        return dormContestMapper.selectDormContestByConId(conId);
    }

    /**
     * 查询宿舍评分列表
     * 
     * @param dormContest 宿舍评分
     * @return 宿舍评分
     */
    @Override
    public List<DormContest> selectDormContestList(DormContest dormContest)
    {
        return dormContestMapper.selectDormContestList(dormContest);
    }

    /**
     * 新增宿舍评分
     * 
     * @param dormContest 宿舍评分
     * @return 结果
     */
    @Override
    public int insertDormContest(DormContest dormContest)
    {
        dormContest.setCreateTime(DateUtils.getNowDate());
        return dormContestMapper.insertDormContest(dormContest);
    }

    /**
     * 修改宿舍评分
     * 
     * @param dormContest 宿舍评分
     * @return 结果
     */
    @Override
    public int updateDormContest(DormContest dormContest)
    {
        dormContest.setUpdateTime(DateUtils.getNowDate());
        return dormContestMapper.updateDormContest(dormContest);
    }

    /**
     * 批量删除宿舍评分
     * 
     * @param conIds 需要删除的宿舍评分主键
     * @return 结果
     */
    @Override
    public int deleteDormContestByConIds(Integer[] conIds)
    {
        return dormContestMapper.deleteDormContestByConIds(conIds);
    }

    /**
     * 删除宿舍评分信息
     * 
     * @param conId 宿舍评分主键
     * @return 结果
     */
    @Override
    public int deleteDormContestByConId(Integer conId)
    {
        return dormContestMapper.deleteDormContestByConId(conId);
    }
}
