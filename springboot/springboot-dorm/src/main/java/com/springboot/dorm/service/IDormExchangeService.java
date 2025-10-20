package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormExchange;

/**
 * 换宿申请Service接口
 * 
 *
 * @date 2025-09-27
 */
public interface IDormExchangeService 
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
     * 批量删除换宿申请
     * 
     * @param ids 需要删除的换宿申请主键集合
     * @return 结果
     */
    public int deleteDormExchangeByIds(Long[] ids);

    /**
     * 删除换宿申请信息
     * 
     * @param id 换宿申请主键
     * @return 结果
     */
    public int deleteDormExchangeById(Long id);

    /**
     * 审批换宿申请
     * 
     * @param dormExchange 换宿申请
     * @return 结果
     */
    public int approveDormExchange(DormExchange dormExchange);

    /**
     * 批量审批换宿申请
     * 
     * @param dormExchanges 换宿申请集合
     * @return 结果
     */
    public int batchApproveDormExchange(List<DormExchange> dormExchanges);
}
