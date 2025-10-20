package com.springboot.dorm.mapper;

import java.util.List;
import java.util.Map;
import com.springboot.dorm.domain.DormVisit;

/**
 * 来访人员登记Mapper接口
 * 
 *
 * @date 2025-09-20
 */
public interface DormVisitMapper 
{
    /**
     * 查询来访人员登记
     * 
     * @param visId 来访人员登记主键
     * @return 来访人员登记
     */
    public DormVisit selectDormVisitByVisId(Long visId);

    /**
     * 查询来访人员登记列表
     * 
     * @param dormVisit 来访人员登记
     * @return 来访人员登记集合
     */
    public List<DormVisit> selectDormVisitList(DormVisit dormVisit);

    /**
     * 新增来访人员登记
     * 
     * @param dormVisit 来访人员登记
     * @return 结果
     */
    public int insertDormVisit(DormVisit dormVisit);

    /**
     * 修改来访人员登记
     * 
     * @param dormVisit 来访人员登记
     * @return 结果
     */
    public int updateDormVisit(DormVisit dormVisit);

    /**
     * 删除来访人员登记
     * 
     * @param visId 来访人员登记主键
     * @return 结果
     */
    public int deleteDormVisitByVisId(Long visId);

    /**
     * 批量删除来访人员登记
     * 
     * @param visIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormVisitByVisIds(Long[] visIds);

    /**
     * 根据楼层ID和状态统计来访登记数量
     * 
     * @param floorIds 楼层ID列表
     * @param status 状态
     * @return 数量
     */
    public Integer countByFloorIdsAndStatus(List<Long> floorIds, String status);

    /**
     * 根据学生ID统计来访登记数量
     * 
     * @param studentId 学生ID
     * @return 数量
     */
    public Integer countByStudentId(Long studentId);

    /**
     * 根据楼层ID统计本月处理的来访登记数
     * 
     * @param floorIds 楼层ID列表
     * @return 处理的来访登记数
     */
    public Integer countProcessedByFloorIdsThisMonth(List<Long> floorIds);

    /**
     * 根据学生ID获取近期访客记录
     * 
     * @param studentId 学生ID
     * @return 近期访客记录
     */
    public List<Map<String, Object>> getRecentVisitsByStudentId(Long studentId);
}
