package com.springboot.dorm.service.impl;

import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormFloorMapper;
import com.springboot.dorm.domain.DormFloor;
import com.springboot.dorm.service.IDormFloorService;

/**
 * 宿舍楼管理Service业务层处理
 * 
 * 
 * @date 2025-09-16
 */
@Service
public class DormFloorServiceImpl implements IDormFloorService 
{
    @Autowired
    private DormFloorMapper dormFloorMapper;

    /**
     * 查询宿舍楼管理
     * 
     * @param fId 宿舍楼管理主键
     * @return 宿舍楼管理
     */
    @Override
    public DormFloor selectDormFloorByFId(Long fId)
    {
        return dormFloorMapper.selectDormFloorByFId(fId);
    }

    /**
     * 查询宿舍楼管理列表
     * 
     * @param dormFloor 宿舍楼管理
     * @return 宿舍楼管理
     */
    @Override
    public List<DormFloor> selectDormFloorList(DormFloor dormFloor)
    {
        return dormFloorMapper.selectDormFloorList(dormFloor);
    }

    /**
     * 新增宿舍楼管理
     * 
     * @param dormFloor 宿舍楼管理
     * @return 结果
     */
    @Override
    public int insertDormFloor(DormFloor dormFloor)
    {
        dormFloor.setCreateTime(DateUtils.getNowDate());
        return dormFloorMapper.insertDormFloor(dormFloor);
    }

    /**
     * 修改宿舍楼管理
     * 
     * @param dormFloor 宿舍楼管理
     * @return 结果
     */
    @Override
    public int updateDormFloor(DormFloor dormFloor)
    {
        dormFloor.setUpdateTime(DateUtils.getNowDate());
        return dormFloorMapper.updateDormFloor(dormFloor);
    }

    /**
     * 批量删除宿舍楼管理
     * 
     * @param fIds 需要删除的宿舍楼管理主键
     * @return 结果
     */
    @Override
    public int deleteDormFloorByFIds(Long[] fIds)
    {
        return dormFloorMapper.deleteDormFloorByFIds(fIds);
    }

    /**
     * 删除宿舍楼管理信息
     * 
     * @param fId 宿舍楼管理主键
     * @return 结果
     */
    @Override
    public int deleteDormFloorByFId(Long fId)
    {
        return dormFloorMapper.deleteDormFloorByFId(fId);
    }
}
