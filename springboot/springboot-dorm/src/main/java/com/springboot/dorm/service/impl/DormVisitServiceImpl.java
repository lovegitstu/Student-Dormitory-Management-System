package com.springboot.dorm.service.impl;

import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormVisitMapper;
import com.springboot.dorm.domain.DormVisit;
import com.springboot.dorm.service.IDormVisitService;

/**
 * 来访人员登记Service业务层处理
 * 
 *
 * @date 2025-09-20
 */
@Service
public class DormVisitServiceImpl implements IDormVisitService 
{
    @Autowired
    private DormVisitMapper dormVisitMapper;

    /**
     * 查询来访人员登记
     * 
     * @param visId 来访人员登记主键
     * @return 来访人员登记
     */
    @Override
    public DormVisit selectDormVisitByVisId(Long visId)
    {
        return dormVisitMapper.selectDormVisitByVisId(visId);
    }

    /**
     * 查询来访人员登记列表
     * 
     * @param dormVisit 来访人员登记
     * @return 来访人员登记
     */
    @Override
    public List<DormVisit> selectDormVisitList(DormVisit dormVisit)
    {
        return dormVisitMapper.selectDormVisitList(dormVisit);
    }

    /**
     * 新增来访人员登记
     * 
     * @param dormVisit 来访人员登记
     * @return 结果
     */
    @Override
    public int insertDormVisit(DormVisit dormVisit)
    {
        dormVisit.setCreateTime(DateUtils.getNowDate());
        return dormVisitMapper.insertDormVisit(dormVisit);
    }

    /**
     * 修改来访人员登记
     * 
     * @param dormVisit 来访人员登记
     * @return 结果
     */
    @Override
    public int updateDormVisit(DormVisit dormVisit)
    {
        dormVisit.setUpdateTime(DateUtils.getNowDate());
        return dormVisitMapper.updateDormVisit(dormVisit);
    }

    /**
     * 批量删除来访人员登记
     * 
     * @param visIds 需要删除的来访人员登记主键
     * @return 结果
     */
    @Override
    public int deleteDormVisitByVisIds(Long[] visIds)
    {
        return dormVisitMapper.deleteDormVisitByVisIds(visIds);
    }

    /**
     * 删除来访人员登记信息
     * 
     * @param visId 来访人员登记主键
     * @return 结果
     */
    @Override
    public int deleteDormVisitByVisId(Long visId)
    {
        return dormVisitMapper.deleteDormVisitByVisId(visId);
    }
}
