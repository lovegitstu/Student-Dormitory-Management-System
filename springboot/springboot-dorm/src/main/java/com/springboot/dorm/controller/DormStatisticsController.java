package com.springboot.dorm.controller;

import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.common.core.controller.BaseController;
import com.springboot.common.core.domain.AjaxResult;
import com.springboot.dorm.service.IDormStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 宿舍统计Controller
 * 
 * 
 * @date 2025-10-30
 */
@RestController
@RequestMapping("/dormitory/statistics")
public class DormStatisticsController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(DormStatisticsController.class);
    
    @Autowired
    private IDormStatisticsService dormStatisticsService;

    /**
     * 获取系统管理员统计数据
     */
    @PreAuthorize("@ss.hasPermi('dormitory:statistics:admin')")
    @GetMapping("/admin")
    public AjaxResult getAdminStatistics()
    {
        logger.info("获取系统管理员统计数据");
        Map<String, Object> statistics = dormStatisticsService.getAdminStatistics();
        return success(statistics);
    }

    /**
     * 获取宿管统计数据
     */
    @PreAuthorize("@ss.hasPermi('dormitory:statistics:manager')")
    @GetMapping("/manager")
    public AjaxResult getManagerStatistics()
    {
        logger.info("获取宿管统计数据，用户ID: {}", getUserId());
        Map<String, Object> statistics = dormStatisticsService.getManagerStatistics(getUserId());
        return success(statistics);
    }

    /**
     * 获取学生统计数据
     */
    @PreAuthorize("@ss.hasPermi('dormitory:statistics:student')")
    @GetMapping("/student")
    public AjaxResult getStudentStatistics()
    {
        logger.info("获取学生统计数据，用户ID: {}", getUserId());
        Map<String, Object> statistics = dormStatisticsService.getStudentStatistics(getUserId());
        return success(statistics);
    }

    /**
     * 获取宿舍入住率统计
     */
    @PreAuthorize("@ss.hasPermi('dormitory:statistics:occupancy')")
    @GetMapping("/occupancy")
    public AjaxResult getOccupancyStatistics()
    {
        logger.info("获取宿舍入住率统计");
        Map<String, Object> statistics = dormStatisticsService.getOccupancyStatistics();
        return success(statistics);
    }

    /**
     * 获取申请处理统计
     */
    @PreAuthorize("@ss.hasPermi('dormitory:statistics:application')")
    @GetMapping("/application")
    public AjaxResult getApplicationStatistics()
    {
        logger.info("获取申请处理统计");
        Map<String, Object> statistics = dormStatisticsService.getApplicationStatistics();
        return success(statistics);
    }

    /**
     * 获取报修处理统计
     */
    @PreAuthorize("@ss.hasPermi('dormitory:statistics:repair')")
    @GetMapping("/repair")
    public AjaxResult getRepairStatistics()
    {
        logger.info("获取报修处理统计");
        Map<String, Object> statistics = dormStatisticsService.getRepairStatistics();
        return success(statistics);
    }

    /**
     * 获取水电费统计
     */
    @PreAuthorize("@ss.hasPermi('dormitory:statistics:bills')")
    @GetMapping("/bills")
    public AjaxResult getBillsStatistics()
    {
        logger.info("获取水电费统计");
        Map<String, Object> statistics = dormStatisticsService.getBillsStatistics();
        return success(statistics);
    }
}