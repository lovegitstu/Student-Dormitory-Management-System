package com.springboot.dorm.service.impl;

import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormRepairMapper;
import com.springboot.dorm.domain.DormRepair;
import com.springboot.dorm.service.IDormRepairService;

/**
 * 维修工单Service业务层处理
 * 
 *
 * @date 2025-09-20
 */
@Service
public class DormRepairServiceImpl implements IDormRepairService 
{
    @Autowired
    private DormRepairMapper dormRepairMapper;

    /**
     * 查询维修工单
     * 
     * @param repairId 维修工单主键
     * @return 维修工单
     */
    @Override
    public DormRepair selectDormRepairByRepairId(Integer repairId)
    {
        return dormRepairMapper.selectDormRepairByRepairId(repairId);
    }

    /**
     * 查询维修工单列表
     * 
     * @param dormRepair 维修工单
     * @return 维修工单
     */
    @Override
    public List<DormRepair> selectDormRepairList(DormRepair dormRepair)
    {
        return dormRepairMapper.selectDormRepairList(dormRepair);
    }

    /**
     * 新增维修工单
     * 
     * @param dormRepair 维修工单
     * @return 结果
     */
    @Override
    public int insertDormRepair(DormRepair dormRepair)
    {
        dormRepair.setCreateTime(DateUtils.getNowDate());
        return dormRepairMapper.insertDormRepair(dormRepair);
    }

    /**
     * 修改维修工单
     * 
     * @param dormRepair 维修工单
     * @return 结果
     */
    @Override
    public int updateDormRepair(DormRepair dormRepair)
    {
        dormRepair.setUpdateTime(DateUtils.getNowDate());
        return dormRepairMapper.updateDormRepair(dormRepair);
    }

    /**
     * 批量删除维修工单
     * 
     * @param repairIds 需要删除的维修工单主键
     * @return 结果
     */
    @Override
    public int deleteDormRepairByRepairIds(Integer[] repairIds)
    {
        return dormRepairMapper.deleteDormRepairByRepairIds(repairIds);
    }

    /**
     * 删除维修工单信息
     * 
     * @param repairId 维修工单主键
     * @return 结果
     */
    @Override
    public int deleteDormRepairByRepairId(Integer repairId)
    {
        return dormRepairMapper.deleteDormRepairByRepairId(repairId);
    }
}
