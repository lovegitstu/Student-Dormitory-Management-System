package com.springboot.dorm.mapper;

import java.util.List;
import com.springboot.dorm.domain.DormCome;

/**
 * 回校申请Mapper接口
 * 
 * 
 * @date 2025-09-25
 */
public interface DormComeMapper 
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
     * 删除回校申请
     * 
     * @param id 回校申请主键
     * @return 结果
     */
    public int deleteDormComeById(Long id);

    /**
     * 批量删除回校申请
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormComeByIds(Long[] ids);

    /**
     * 根据状态统计回校申请数量
     * 
     * @param status 状态
     * @return 数量
     */
    public Integer countByStatus(String status);

    /**
     * 根据学生ID统计回校申请数量
     * 
     * @param studentId 学生ID
     * @return 数量
     */
    public Integer countByStudentId(Long studentId);

    /**
     * 获取平均处理天数
     * 
     * @return 平均处理天数
     */
    public Double getAvgProcessingDays();
}
