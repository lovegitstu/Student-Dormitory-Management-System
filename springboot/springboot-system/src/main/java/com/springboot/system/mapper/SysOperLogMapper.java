package com.springboot.system.mapper;

import java.util.List;
import java.util.Map;
import java.util.Date;
import org.apache.ibatis.annotations.Param;
import com.springboot.system.domain.SysOperLog;

/**
 * 操作日志 数据层
 * 
 *
 */
public interface SysOperLogMapper
{
    /**
     * 新增操作日志
     * 
     * @param operLog 操作日志对象
     */
    public void insertOperlog(SysOperLog operLog);

    /**
     * 查询系统操作日志集合
     * 
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    public List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 批量删除系统操作日志
     * 
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteOperLogByIds(Long[] operIds);

    /**
     * 查询操作日志详细
     * 
     * @param operId 操作ID
     * @return 操作日志对象
     */
    public SysOperLog selectOperLogById(Long operId);

    /**
     * 清空操作日志
     */
    public void cleanOperLog();

    /**
     * 统计指定时间范围内的操作日志数量
     */
    Long countOperLogsBetween(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 统计指定时间范围内按状态过滤的操作日志数量
     */
    Long countOperLogsByStatusBetween(@Param("status") String status,
                                      @Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime);

    /**
     * 统计指定时间范围内操作日志的平均耗时（毫秒）
     */
    Double selectAverageCostTimeBetween(@Param("startTime") Date startTime,
                                        @Param("endTime") Date endTime);

    /**
     * 统计指定时间范围内活跃操作用户数量
     */
    Long countDistinctOperatorsBetween(@Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime);

    /**
     * 查询按天聚合的请求趋势数据
     */
    List<Map<String, Object>> selectDailyRequestTrend(@Param("startTime") Date startTime,
                                                      @Param("endTime") Date endTime);

    /**
     * 查询请求量排名靠前的模块/接口
     */
    List<Map<String, Object>> selectTopModules(@Param("startTime") Date startTime,
                                               @Param("endTime") Date endTime,
                                               @Param("limit") int limit);

    /**
     * 查询近期的异常操作日志
     */
    List<SysOperLog> selectRecentErrorLogs(@Param("limit") int limit);
}
