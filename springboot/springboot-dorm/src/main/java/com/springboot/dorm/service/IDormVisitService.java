package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormVisit;

/**
 * 来访人员登记Service接口
 * 
 *
 * @date 2025-09-20
 */
public interface IDormVisitService 
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
     * 批量删除来访人员登记
     * 
     * @param visIds 需要删除的来访人员登记主键集合
     * @return 结果
     */
    public int deleteDormVisitByVisIds(Long[] visIds);

    /**
     * 删除来访人员登记信息
     * 
     * @param visId 来访人员登记主键
     * @return 结果
     */
    public int deleteDormVisitByVisId(Long visId);
}
