package com.springboot.dorm.mapper;

import java.util.List;
import com.springboot.dorm.domain.DormBed;

/**
 * 床位管理Mapper接口
 * 
 *
 * @date 2025-09-15
 */
public interface DormBedMapper 
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
     * 删除床位管理
     * 
     * @param bedId 床位管理主键
     * @return 结果
     */
    public int deleteDormBedByBedId(Long bedId);

    /**
     * 批量删除床位管理
     * 
     * @param bedIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormBedByBedIds(Long[] bedIds);

    /**
     * 统计床位总数
     * 
     * @return 床位总数
     */
    public Integer countTotalBeds();

    /**
     * 统计已占用床位数
     * 
     * @return 已占用床位数
     */
    public Integer countOccupiedBeds();

    /**
     * 根据楼层ID统计床位数
     * 
     * @param floorIds 楼层ID列表
     * @return 床位数
     */
    public Integer countByFloorIds(List<Long> floorIds);

    /**
     * 根据楼层ID统计已占用床位数
     * 
     * @param floorIds 楼层ID列表
     * @return 已占用床位数
     */
    public Integer countOccupiedByFloorIds(List<Long> floorIds);

    /**
     * 根据日期获取入住率
     * 
     * @param date 日期
     * @return 入住率
     */
    public Double getOccupancyRateByDate(String date);

    /**
     * 根据楼层ID统计床位数
     * 
     * @param floorId 楼层ID
     * @return 床位数
     */
    public Integer countByFloorId(Long floorId);

    /**
     * 根据楼层ID统计已占用床位数
     * 
     * @param floorId 楼层ID
     * @return 已占用床位数
     */
    public Integer countOccupiedByFloorId(Long floorId);
}
