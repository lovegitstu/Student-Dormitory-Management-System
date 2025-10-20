package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormKeepBack;

/**
 * 留宿申请Service接口
 * 
 * 
 * @date 2025-09-26
 */
public interface IDormKeepBackService 
{
    /**
     * 查询留宿申请
     * 
     * @param kbId 留宿申请主键
     * @return 留宿申请
     */
    public DormKeepBack selectDormKeepBackByKbId(Integer kbId);

    /**
     * 查询留宿申请列表
     * 
     * @param dormKeepBack 留宿申请
     * @return 留宿申请集合
     */
    public List<DormKeepBack> selectDormKeepBackList(DormKeepBack dormKeepBack);

    /**
     * 新增留宿申请
     * 
     * @param dormKeepBack 留宿申请
     * @return 结果
     */
    public int insertDormKeepBack(DormKeepBack dormKeepBack);

    /**
     * 修改留宿申请
     * 
     * @param dormKeepBack 留宿申请
     * @return 结果
     */
    public int updateDormKeepBack(DormKeepBack dormKeepBack);

    /**
     * 批量删除留宿申请
     * 
     * @param kbIds 需要删除的留宿申请主键集合
     * @return 结果
     */
    public int deleteDormKeepBackByKbIds(Integer[] kbIds);

    /**
     * 删除留宿申请信息
     * 
     * @param kbId 留宿申请主键
     * @return 结果
     */
    public int deleteDormKeepBackByKbId(Integer kbId);
}
