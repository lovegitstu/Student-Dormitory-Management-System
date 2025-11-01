package com.springboot.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.springboot.system.domain.SysLogininfor;

/**
 * 系统访问日志情况信息 数据层
 * 
 *
 */
public interface SysLogininforMapper
{
    /**
     * 新增系统登录日志
     * 
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininfor logininfor);

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    /**
     * 批量删除系统登录日志
     * 
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    public int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     * 
     * @return 结果
     */
    public int cleanLogininfor();

    /**
     * 统计指定时间范围内的登录记录数量
     */
    Long countLogininforByStatusBetween(@Param("status") String status,
                                        @Param("startTime") Date startTime,
                                        @Param("endTime") Date endTime);

    /**
     * 查询按日聚合的登录成功/失败趋势
     */
    List<Map<String, Object>> selectDailyLoginTrend(@Param("startTime") Date startTime,
                                                    @Param("endTime") Date endTime);

    /**
     * 查询最近的失败登录记录
     */
    List<SysLogininfor> selectRecentFailedLogin(@Param("limit") int limit);
}
