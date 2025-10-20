package com.springboot.dorm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormComeMapper;
import com.springboot.dorm.domain.DormCome;
import com.springboot.dorm.service.IDormComeService;

/**
 * 回校申请Service业务层处理
 * 
 * 
 * @date 2025-09-25
 */
@Service
public class DormComeServiceImpl implements IDormComeService 
{
    @Autowired
    private DormComeMapper dormComeMapper;

    /**
     * 查询回校申请
     * 
     * @param id 回校申请主键
     * @return 回校申请
     */
    @Override
    public DormCome selectDormComeById(Long id)
    {
        return dormComeMapper.selectDormComeById(id);
    }

    /**
     * 查询回校申请列表
     * 
     * @param dormCome 回校申请
     * @return 回校申请
     */
    @Override
    public List<DormCome> selectDormComeList(DormCome dormCome)
    {
        return dormComeMapper.selectDormComeList(dormCome);
    }

    /**
     * 新增回校申请
     * 
     * @param dormCome 回校申请
     * @return 结果
     */
    @Override
    public int insertDormCome(DormCome dormCome)
    {
        return dormComeMapper.insertDormCome(dormCome);
    }

    /**
     * 修改回校申请
     * 
     * @param dormCome 回校申请
     * @return 结果
     */
    @Override
    public int updateDormCome(DormCome dormCome)
    {
        return dormComeMapper.updateDormCome(dormCome);
    }

    /**
     * 批量删除回校申请
     * 
     * @param ids 需要删除的回校申请主键
     * @return 结果
     */
    @Override
    public int deleteDormComeByIds(Long[] ids)
    {
        return dormComeMapper.deleteDormComeByIds(ids);
    }

    /**
     * 删除回校申请信息
     * 
     * @param id 回校申请主键
     * @return 结果
     */
    @Override
    public int deleteDormComeById(Long id)
    {
        return dormComeMapper.deleteDormComeById(id);
    }
}
