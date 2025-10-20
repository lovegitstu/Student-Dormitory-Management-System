package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormUtilityBills;

/**
 * 水电费管理Service接口
 * 
 *
 * @date 2025-09-20
 */
public interface IDormUtilityBillsService 
{
    /**
     * 查询水电费管理
     * 
     * @param ubId 水电费管理主键
     * @return 水电费管理
     */
    public DormUtilityBills selectDormUtilityBillsByUbId(Integer ubId);

    /**
     * 查询水电费管理列表
     * 
     * @param dormUtilityBills 水电费管理
     * @return 水电费管理集合
     */
    public List<DormUtilityBills> selectDormUtilityBillsList(DormUtilityBills dormUtilityBills);

    /**
     * 新增水电费管理
     * 
     * @param dormUtilityBills 水电费管理
     * @return 结果
     */
    public int insertDormUtilityBills(DormUtilityBills dormUtilityBills);

    /**
     * 修改水电费管理
     * 
     * @param dormUtilityBills 水电费管理
     * @return 结果
     */
    public int updateDormUtilityBills(DormUtilityBills dormUtilityBills);

    /**
     * 批量删除水电费管理
     * 
     * @param ubIds 需要删除的水电费管理主键集合
     * @return 结果
     */
    public int deleteDormUtilityBillsByUbIds(Integer[] ubIds);

    /**
     * 删除水电费管理信息
     * 
     * @param ubId 水电费管理主键
     * @return 结果
     */
    public int deleteDormUtilityBillsByUbId(Integer ubId);
}
