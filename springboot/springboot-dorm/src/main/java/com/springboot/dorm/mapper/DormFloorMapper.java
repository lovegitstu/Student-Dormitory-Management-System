package com.springboot.dorm.mapper;

import java.util.List;
import java.util.Map;
import com.springboot.dorm.domain.DormFloor;

/**
 * 宿舍楼管理Mapper接口
 * 
 *
 * @date 2025-09-16
 */
public interface DormFloorMapper 
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
     * 删除宿舍楼管理
     * 
     * @param fId 宿舍楼管理主键
     * @return 结果
     */
    public int deleteDormFloorByFId(Long fId);

    /**
     * 批量删除宿舍楼管理
     * 
     * @param fIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormFloorByFIds(Long[] fIds);

    /**
     * 统计楼层总数
     * 
     * @return 楼层总数
     */
    public Integer countTotalFloors();

    /**
     * 根据管理员ID查询楼层ID列表
     * 
     * @param managerId 管理员ID
     * @return 楼层ID列表
     */
    public List<Long> selectFloorIdsByManagerId(Long managerId);

    /**
     * 查询各楼层入住率统计
     * 
     * @return 楼层入住率统计
     */
    public List<Map<String, Object>> selectOccupancyByFloor();
}
