package com.springboot.dorm.service.impl;

import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormTurnOutMapper;
import com.springboot.dorm.domain.DormTurnOut;
import com.springboot.dorm.service.IDormTurnOutService;

/**
 * 离校登记Service业务层处理
 * 
 * 
 * @date 2025-09-26
 */
@Service
public class DormTurnOutServiceImpl implements IDormTurnOutService 
{
    @Autowired
    private DormTurnOutMapper dormTurnOutMapper;

    /**
     * 查询离校登记
     * 
     * @param id 离校登记主键
     * @return 离校登记
     */
    @Override
    public DormTurnOut selectDormTurnOutById(Long id)
    {
        return dormTurnOutMapper.selectDormTurnOutById(id);
    }

    /**
     * 查询离校登记列表
     * 
     * @param dormTurnOut 离校登记
     * @return 离校登记
     */
    @Override
    public List<DormTurnOut> selectDormTurnOutList(DormTurnOut dormTurnOut)
    {
        return dormTurnOutMapper.selectDormTurnOutList(dormTurnOut);
    }

    /**
     * 新增离校登记
     * 
     * @param dormTurnOut 离校登记
     * @return 结果
     */
    @Override
    public int insertDormTurnOut(DormTurnOut dormTurnOut)
    {
        dormTurnOut.setCreateTime(DateUtils.getNowDate());
        return dormTurnOutMapper.insertDormTurnOut(dormTurnOut);
    }

    /**
     * 修改离校登记
     * 
     * @param dormTurnOut 离校登记
     * @return 结果
     */
    @Override
    public int updateDormTurnOut(DormTurnOut dormTurnOut)
    {
        dormTurnOut.setUpdateTime(DateUtils.getNowDate());
        return dormTurnOutMapper.updateDormTurnOut(dormTurnOut);
    }

    /**
     * 批量删除离校登记
     * 
     * @param ids 需要删除的离校登记主键
     * @return 结果
     */
    @Override
    public int deleteDormTurnOutByIds(Long[] ids)
    {
        return dormTurnOutMapper.deleteDormTurnOutByIds(ids);
    }

    /**
     * 删除离校登记信息
     * 
     * @param id 离校登记主键
     * @return 结果
     */
    @Override
    public int deleteDormTurnOutById(Long id)
    {
        return dormTurnOutMapper.deleteDormTurnOutById(id);
    }
}
