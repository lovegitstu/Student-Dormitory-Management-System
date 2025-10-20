package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormCome;

/**
 * 回校申请Service接口
 * 
 *
 * @date 2025-09-25
 */
public interface IDormComeService 
{
    /**
     * 查询回校申请
     * 
     * @param id 回校申请主键
     * @return 回校申请
     */
    public DormCome selectDormComeById(Long id);

    /**
     * 查询回校申请列表
     * 
     * @param dormCome 回校申请
     * @return 回校申请集合
     */
    public List<DormCome> selectDormComeList(DormCome dormCome);

    /**
     * 新增回校申请
     * 
     * @param dormCome 回校申请
     * @return 结果
     */
    public int insertDormCome(DormCome dormCome);

    /**
     * 修改回校申请
     * 
     * @param dormCome 回校申请
     * @return 结果
     */
    public int updateDormCome(DormCome dormCome);

    /**
     * 批量删除回校申请
     * 
     * @param ids 需要删除的回校申请主键集合
     * @return 结果
     */
    public int deleteDormComeByIds(Long[] ids);

    /**
     * 删除回校申请信息
     * 
     * @param id 回校申请主键
     * @return 结果
     */
    public int deleteDormComeById(Long id);
}
