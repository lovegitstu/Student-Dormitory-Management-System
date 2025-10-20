package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormStudent;

/**
 * 学生信息Service接口
 * 
 * 
 * @date 2025-09-16
 */
public interface IDormStudentService 
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
     * 批量删除学生信息
     * 
     * @param stuIds 需要删除的学生信息主键集合
     * @return 结果
     */
    public int deleteDormStudentByStuIds(Long[] stuIds);

    /**
     * 删除学生信息信息
     * 
     * @param stuId 学生信息主键
     * @return 结果
     */
    public int deleteDormStudentByStuId(Long stuId);
}
