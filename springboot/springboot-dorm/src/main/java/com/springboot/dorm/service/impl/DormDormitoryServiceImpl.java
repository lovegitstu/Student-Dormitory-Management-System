package com.springboot.dorm.service.impl;

import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormDormitoryMapper;
import com.springboot.dorm.domain.DormDormitory;
import com.springboot.dorm.service.IDormDormitoryService;

/**
 * 宿舍信息Service业务层处理
 * 
 *
 * @date 2025-09-16
 */
@Service
public class DormDormitoryServiceImpl implements IDormDormitoryService 
{
    @Autowired
    private DormDormitoryMapper dormDormitoryMapper;

    /**
     * 查询宿舍信息
     * 
     * @param dorId 宿舍信息主键
     * @return 宿舍信息
     */
    @Override
    public DormDormitory selectDormDormitoryByDorId(Long dorId)
    {
        return dormDormitoryMapper.selectDormDormitoryByDorId(dorId);
    }

    /**
     * 查询宿舍信息列表
     * 
     * @param dormDormitory 宿舍信息
     * @return 宿舍信息
     */
    @Override
    public List<DormDormitory> selectDormDormitoryList(DormDormitory dormDormitory)
    {
        return dormDormitoryMapper.selectDormDormitoryList(dormDormitory);
    }

    /**
     * 新增宿舍信息
     * 
     * @param dormDormitory 宿舍信息
     * @return 结果
     */
    @Override
    public int insertDormDormitory(DormDormitory dormDormitory)
    {
        dormDormitory.setCreateTime(DateUtils.getNowDate());
        return dormDormitoryMapper.insertDormDormitory(dormDormitory);
    }

    /**
     * 修改宿舍信息
     * 
     * @param dormDormitory 宿舍信息
     * @return 结果
     */
    @Override
    public int updateDormDormitory(DormDormitory dormDormitory)
    {
        dormDormitory.setUpdateTime(DateUtils.getNowDate());
        return dormDormitoryMapper.updateDormDormitory(dormDormitory);
    }

    /**
     * 批量删除宿舍信息
     * 
     * @param dorIds 需要删除的宿舍信息主键
     * @return 结果
     */
    @Override
    public int deleteDormDormitoryByDorIds(Long[] dorIds)
    {
        return dormDormitoryMapper.deleteDormDormitoryByDorIds(dorIds);
    }

    /**
     * 删除宿舍信息信息
     * 
     * @param dorId 宿舍信息主键
     * @return 结果
     */
    @Override
    public int deleteDormDormitoryByDorId(Long dorId)
    {
        return dormDormitoryMapper.deleteDormDormitoryByDorId(dorId);
    }
}
