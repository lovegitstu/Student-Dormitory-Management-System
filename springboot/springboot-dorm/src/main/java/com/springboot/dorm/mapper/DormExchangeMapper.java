package com.springboot.dorm.mapper;

import java.util.List;
import java.util.Map;
import com.springboot.dorm.domain.DormExchange;

/**
 * 换宿申请Mapper接口
 * 
 *
 * @date 2025-09-27
 */
public interface DormExchangeMapper 
{
    /**
     * 查询换宿申请
     * 
     * @param id 换宿申请主键
     * @return 换宿申请
     */
    public DormExchange selectDormExchangeById(Long id);

    /**
     * 查询换宿申请列表
     * 
     * @param dormExchange 换宿申请
     * @return 换宿申请集合
     */
    public List<DormExchange> selectDormExchangeList(DormExchange dormExchange);

    /**
     * 新增换宿申请
     * 
     * @param dormExchange 换宿申请
     * @return 结果
     */
    public int insertDormExchange(DormExchange dormExchange);

    /**
     * 修改换宿申请
     * 
     * @param dormExchange 换宿申请
     * @return 结果
     */
    public int updateDormExchange(DormExchange dormExchange);

    /**
     * 删除换宿申请
     * 
     * @param id 换宿申请主键
     * @return 结果
     */
    public int deleteDormExchangeById(Long id);

    /**
     * 批量删除换宿申请
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormExchangeByIds(Long[] ids);

    /**
     * 根据状态统计换宿申请数量
     * 
     * @param status 状态
     * @return 数量
     */
    public Integer countByStatus(String status);

    /**
     * 根据学生ID统计换宿申请数量
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

    /**
     * 根据学生ID获取近期换宿申请记录
     * 
     * @param studentId 学生ID
     * @return 近期换宿申请记录
     */
    public List<Map<String, Object>> getRecentExchangesByStudentId(Long studentId);
}
