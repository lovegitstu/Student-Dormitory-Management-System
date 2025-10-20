package com.springboot.dorm.service.impl;

import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormKeepBackMapper;
import com.springboot.dorm.domain.DormKeepBack;
import com.springboot.dorm.service.IDormKeepBackService;

/**
 * 留宿申请Service业务层处理
 * 
 * 
 * @date 2025-09-26
 */
@Service
public class DormKeepBackServiceImpl implements IDormKeepBackService 
{
    @Autowired
    private DormKeepBackMapper dormKeepBackMapper;

    /**
     * 查询留宿申请
     * 
     * @param kbId 留宿申请主键
     * @return 留宿申请
     */
    @Override
    public DormKeepBack selectDormKeepBackByKbId(Integer kbId)
    {
        return dormKeepBackMapper.selectDormKeepBackByKbId(kbId);
    }

    /**
     * 查询留宿申请列表
     * 
     * @param dormKeepBack 留宿申请
     * @return 留宿申请
     */
    @Override
    public List<DormKeepBack> selectDormKeepBackList(DormKeepBack dormKeepBack)
    {
        return dormKeepBackMapper.selectDormKeepBackList(dormKeepBack);
    }

    /**
     * 新增留宿申请
     * 
     * @param dormKeepBack 留宿申请
     * @return 结果
     */
    @Override
    public int insertDormKeepBack(DormKeepBack dormKeepBack)
    {
        dormKeepBack.setCreateTime(DateUtils.getNowDate());
        return dormKeepBackMapper.insertDormKeepBack(dormKeepBack);
    }

    /**
     * 修改留宿申请
     * 
     * @param dormKeepBack 留宿申请
     * @return 结果
     */
    @Override
    public int updateDormKeepBack(DormKeepBack dormKeepBack)
    {
        dormKeepBack.setUpdateTime(DateUtils.getNowDate());
        return dormKeepBackMapper.updateDormKeepBack(dormKeepBack);
    }

    /**
     * 批量删除留宿申请
     * 
     * @param kbIds 需要删除的留宿申请主键
     * @return 结果
     */
    @Override
    public int deleteDormKeepBackByKbIds(Integer[] kbIds)
    {
        return dormKeepBackMapper.deleteDormKeepBackByKbIds(kbIds);
    }

    /**
     * 删除留宿申请信息
     * 
     * @param kbId 留宿申请主键
     * @return 结果
     */
    @Override
    public int deleteDormKeepBackByKbId(Integer kbId)
    {
        return dormKeepBackMapper.deleteDormKeepBackByKbId(kbId);
    }
}
