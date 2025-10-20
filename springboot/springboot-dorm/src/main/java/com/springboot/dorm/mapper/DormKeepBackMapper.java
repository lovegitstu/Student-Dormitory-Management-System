package com.springboot.dorm.mapper;

import java.util.List;
import com.springboot.dorm.domain.DormKeepBack;

/**
 * 留宿申请Mapper接口
 * 
 *
 * @date 2025-09-26
 */
public interface DormKeepBackMapper 
{
    /**
     * 查询留宿申请
     * 
     * @param kbId 留宿申请主键
     * @return 留宿申请
     */
    public DormKeepBack selectDormKeepBackByKbId(Integer kbId);

    /**
     * 查询留宿申请列表
     * 
     * @param dormKeepBack 留宿申请
     * @return 留宿申请集合
     */
    public List<DormKeepBack> selectDormKeepBackList(DormKeepBack dormKeepBack);

    /**
     * 新增留宿申请
     * 
     * @param dormKeepBack 留宿申请
     * @return 结果
     */
    public int insertDormKeepBack(DormKeepBack dormKeepBack);

    /**
     * 修改留宿申请
     * 
     * @param dormKeepBack 留宿申请
     * @return 结果
     */
    public int updateDormKeepBack(DormKeepBack dormKeepBack);

    /**
     * 删除留宿申请
     * 
     * @param kbId 留宿申请主键
     * @return 结果
     */
    public int deleteDormKeepBackByKbId(Integer kbId);

    /**
     * 批量删除留宿申请
     * 
     * @param kbIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormKeepBackByKbIds(Integer[] kbIds);

    /**
     * 根据状态统计留宿申请数量
     * 
     * @param status 状态
     * @return 数量
     */
    public Integer countByStatus(String status);

    /**
     * 根据学生ID统计留宿申请数量
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
