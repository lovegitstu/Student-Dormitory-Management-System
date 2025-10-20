package com.springboot.dorm.mapper;

import java.util.List;
import com.springboot.dorm.domain.DormStudent;

/**
 * 学生信息Mapper接口
 * 
 *
 * @date 2025-09-16
 */
public interface DormStudentMapper 
{
    /**
     * 查询学生信息
     * 
     * @param stuId 学生信息主键
     * @return 学生信息
     */
    public DormStudent selectDormStudentByStuId(Long stuId);

    /**
     * 通过用户ID查询学生信息
     * 
     * @param userId 用户ID
     * @return 学生信息
     */
    public DormStudent selectDormStudentByUserId(Long userId);

    /**
     * 查询学生信息列表
     * 
     * @param dormStudent 学生信息
     * @return 学生信息集合
     */
    public List<DormStudent> selectDormStudentList(DormStudent dormStudent);

    /**
     * 新增学生信息
     * 
     * @param dormStudent 学生信息
     * @return 结果
     */
    public int insertDormStudent(DormStudent dormStudent);

    /**
     * 修改学生信息
     * 
     * @param dormStudent 学生信息
     * @return 结果
     */
    public int updateDormStudent(DormStudent dormStudent);

    /**
     * 删除学生信息
     * 
     * @param stuId 学生信息主键
     * @return 结果
     */
    public int deleteDormStudentByStuId(Long stuId);

    /**
     * 批量删除学生信息
     * 
     * @param stuIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormStudentByStuIds(Long[] stuIds);

    /**
     * 统计学生总数
     * 
     * @return 学生总数
     */
    public Integer countTotalStudents();

    /**
     * 根据学生ID查询学生信息
     * 
     * @param userId 用户ID
     * @return 学生信息
     */
    public DormStudent selectStudentInfoByUserId(Long userId);

    /**
     * 根据学生ID统计申请数量
     * 
     * @param studentId 学生ID
     * @return 申请数量
     */
    public Integer countByStudentId(Long studentId);

    /**
     * 根据日期统计新增学生数
     * 
     * @param date 日期
     * @return 新增学生数
     */
    public Integer countNewStudentsByDate(String date);

    /**
     * 根据楼层ID统计本月新增学生数
     * 
     * @param floorIds 楼层ID列表
     * @return 新增学生数
     */
    public Integer countNewByFloorIdsThisMonth(List<Long> floorIds);

    /**
     * 根据学生ID获取室友信息
     * 
     * @param studentId 学生ID
     * @return 室友信息列表
     */
    public List<DormStudent> getRoommatesByStudentId(Long studentId);

    /**
     * 根据学生ID查询学生信息
     * 
     * @param studentId 学生ID
     * @return 学生信息
     */
    public DormStudent selectDormStudentById(Long studentId);
}
