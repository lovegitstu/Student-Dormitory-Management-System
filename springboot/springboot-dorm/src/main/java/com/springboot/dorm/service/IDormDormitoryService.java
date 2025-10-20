package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormDormitory;

/**
 * 宿舍信息Service接口
 * 
 * 
 * @date 2025-09-16
 */
public interface IDormDormitoryService 
{
    /**
     * 查询宿舍信息
     * 
     * @param dorId 宿舍信息主键
     * @return 宿舍信息
     */
    public DormDormitory selectDormDormitoryByDorId(Long dorId);

    /**
     * 查询宿舍信息列表
     * 
     * @param dormDormitory 宿舍信息
     * @return 宿舍信息集合
     */
    public List<DormDormitory> selectDormDormitoryList(DormDormitory dormDormitory);

    /**
     * 新增宿舍信息
     * 
     * @param dormDormitory 宿舍信息
     * @return 结果
     */
    public int insertDormDormitory(DormDormitory dormDormitory);

    /**
     * 修改宿舍信息
     * 
     * @param dormDormitory 宿舍信息
     * @return 结果
     */
    public int updateDormDormitory(DormDormitory dormDormitory);

    /**
     * 批量删除宿舍信息
     * 
     * @param dorIds 需要删除的宿舍信息主键集合
     * @return 结果
     */
    public int deleteDormDormitoryByDorIds(Long[] dorIds);

    /**
     * 删除宿舍信息信息
     * 
     * @param dorId 宿舍信息主键
     * @return 结果
     */
    public int deleteDormDormitoryByDorId(Long dorId);
}
