package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormBed;

/**
 * 床位管理Service接口
 * 
 *
 * @date 2025-09-15
 */
public interface IDormBedService 
{
    /**
     * 查询床位管理
     * 
     * @param bedId 床位管理主键
     * @return 床位管理
     */
    public DormBed selectDormBedByBedId(Long bedId);

    /**
     * 查询床位管理列表
     * 
     * @param dormBed 床位管理
     * @return 床位管理集合
     */
    public List<DormBed> selectDormBedList(DormBed dormBed);

    /**
     * 新增床位管理
     * 
     * @param dormBed 床位管理
     * @return 结果
     */
    public int insertDormBed(DormBed dormBed);

    /**
     * 修改床位管理
     * 
     * @param dormBed 床位管理
     * @return 结果
     */
    public int updateDormBed(DormBed dormBed);

    /**
     * 批量删除床位管理
     * 
     * @param bedIds 需要删除的床位管理主键集合
     * @return 结果
     */
    public int deleteDormBedByBedIds(Long[] bedIds);

    /**
     * 删除床位管理信息
     * 
     * @param bedId 床位管理主键
     * @return 结果
     */
    public int deleteDormBedByBedId(Long bedId);
}
