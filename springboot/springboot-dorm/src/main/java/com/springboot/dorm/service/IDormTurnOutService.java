package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormTurnOut;

/**
 * 离校登记Service接口
 * 
 * 
 * @date 2025-09-26
 */
public interface IDormTurnOutService 
{
    /**
     * 查询离校登记
     * 
     * @param id 离校登记主键
     * @return 离校登记
     */
    public DormTurnOut selectDormTurnOutById(Long id);

    /**
     * 查询离校登记列表
     * 
     * @param dormTurnOut 离校登记
     * @return 离校登记集合
     */
    public List<DormTurnOut> selectDormTurnOutList(DormTurnOut dormTurnOut);

    /**
     * 新增离校登记
     * 
     * @param dormTurnOut 离校登记
     * @return 结果
     */
    public int insertDormTurnOut(DormTurnOut dormTurnOut);

    /**
     * 修改离校登记
     * 
     * @param dormTurnOut 离校登记
     * @return 结果
     */
    public int updateDormTurnOut(DormTurnOut dormTurnOut);

    /**
     * 批量删除离校登记
     * 
     * @param ids 需要删除的离校登记主键集合
     * @return 结果
     */
    public int deleteDormTurnOutByIds(Long[] ids);

    /**
     * 删除离校登记信息
     * 
     * @param id 离校登记主键
     * @return 结果
     */
    public int deleteDormTurnOutById(Long id);
}
