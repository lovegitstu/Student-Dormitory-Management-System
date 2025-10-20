package com.springboot.dorm.mapper;

import java.util.List;
import java.util.Map;
import com.springboot.dorm.domain.DormRepair;

/**
 * 维修工单Mapper接口
 * 
 *
 * @date 2025-09-20
 */
public interface DormRepairMapper 
{
    /**
     * 查询维修工单
     * 
     * @param repairId 维修工单主键
     * @return 维修工单
     */
    public DormRepair selectDormRepairByRepairId(Integer repairId);

    /**
     * 查询维修工单列表
     * 
     * @param dormRepair 维修工单
     * @return 维修工单集合
     */
    public List<DormRepair> selectDormRepairList(DormRepair dormRepair);

    /**
     * 新增维修工单
     * 
     * @param dormRepair 维修工单
     * @return 结果
     */
    public int insertDormRepair(DormRepair dormRepair);

    /**
     * 修改维修工单
     * 
     * @param dormRepair 维修工单
     * @return 结果
     */
    public int updateDormRepair(DormRepair dormRepair);

    /**
     * 删除维修工单
     * 
     * @param repairId 维修工单主键
     * @return 结果
     */
    public int deleteDormRepairByRepairId(Integer repairId);

    /**
     * 批量删除维修工单
     * 
     * @param repairIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormRepairByRepairIds(Integer[] repairIds);

    /**
     * 根据状态统计维修工单数量
     * 
     * @param status 状态
     * @return 数量
     */
    public Integer countByStatus(String status);

    /**
     * 根据楼层ID和状态统计维修工单数量
     * 
     * @param floorIds 楼层ID列表
     * @param status 状态
     * @return 数量
     */
    public Integer countByFloorIdsAndStatus(List<Long> floorIds, String status);

    /**
     * 根据学生ID统计维修工单数量
     * 
     * @param studentId 学生ID
     * @return 数量
     */
    public Integer countByStudentId(Long studentId);

    /**
     * 查询维修类型统计
     * 
     * @return 维修类型统计
     */
    public List<Map<String, Object>> selectRepairTypeStats();

    /**
     * 根据日期统计新增维修工单数
     * 
     * @param date 日期
     * @return 新增维修工单数
     */
    public Integer countNewRepairsByDate(String date);

    /**
     * 根据楼层ID统计本月完成的维修工单数
     * 
     * @param floorIds 楼层ID列表
     * @return 完成的维修工单数
     */
    public Integer countCompletedByFloorIdsThisMonth(List<Long> floorIds);

    /**
     * 获取平均处理时间（小时）
     * 
     * @return 平均处理时间
     */
    public Double getAvgProcessingHours();

    /**
     * 统计24小时内处理完成的工单数
     * 
     * @return 24小时内处理完成的工单数
     */
    public Integer countProcessedWithin24Hours();

    /**
     * 统计超过48小时处理的工单数
     * 
     * @return 超过48小时处理的工单数
     */
    public Integer countProcessedOver48Hours();

    /**
     * 根据日期统计完成的维修工单数
     * 
     * @param date 日期
     * @return 完成的维修工单数
     */
    public Integer countCompletedRepairsByDate(String date);
}
