package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormFloor;

/**
 * 宿舍楼管理Service接口
 * 
 *
 * @date 2025-09-16
 */
public interface IDormFloorService 
{
    /**
     * 查询宿舍楼管理
     * 
     * @param fId 宿舍楼管理主键
     * @return 宿舍楼管理
     */
    public DormFloor selectDormFloorByFId(Long fId);

    /**
     * 查询宿舍楼管理列表
     * 
     * @param dormFloor 宿舍楼管理
     * @return 宿舍楼管理集合
     */
    public List<DormFloor> selectDormFloorList(DormFloor dormFloor);

    /**
     * 新增宿舍楼管理
     * 
     * @param dormFloor 宿舍楼管理
     * @return 结果
     */
    public int insertDormFloor(DormFloor dormFloor);

    /**
     * 修改宿舍楼管理
     * 
     * @param dormFloor 宿舍楼管理
     * @return 结果
     */
    public int updateDormFloor(DormFloor dormFloor);

    /**
     * 批量删除宿舍楼管理
     * 
     * @param fIds 需要删除的宿舍楼管理主键集合
     * @return 结果
     */
    public int deleteDormFloorByFIds(Long[] fIds);

    /**
     * 删除宿舍楼管理信息
     * 
     * @param fId 宿舍楼管理主键
     * @return 结果
     */
    public int deleteDormFloorByFId(Long fId);
}
