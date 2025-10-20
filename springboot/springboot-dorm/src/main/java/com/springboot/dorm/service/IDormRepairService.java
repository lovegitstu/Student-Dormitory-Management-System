package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormRepair;

/**
 * 维修工单Service接口
 * 
 *
 * @date 2025-09-20
 */
public interface IDormRepairService 
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
     * 批量删除维修工单
     * 
     * @param repairIds 需要删除的维修工单主键集合
     * @return 结果
     */
    public int deleteDormRepairByRepairIds(Integer[] repairIds);

    /**
     * 删除维修工单信息
     * 
     * @param repairId 维修工单主键
     * @return 结果
     */
    public int deleteDormRepairByRepairId(Integer repairId);
}
