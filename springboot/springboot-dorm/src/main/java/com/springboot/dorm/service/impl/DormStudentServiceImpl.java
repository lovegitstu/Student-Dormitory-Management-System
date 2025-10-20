package com.springboot.dorm.service.impl;

import java.util.List;
import com.springboot.common.utils.DateUtils;
import com.springboot.common.core.domain.entity.SysUser;
import com.springboot.system.mapper.SysUserMapper;
import com.springboot.dorm.mapper.DormDormitoryMapper;
import com.springboot.dorm.domain.DormDormitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springboot.dorm.mapper.DormStudentMapper;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.service.IDormStudentService;

/**
 * 学生信息Service业务层处理
 *
 *
 * @date 2025-09-16
 */
@Service
public class DormStudentServiceImpl implements IDormStudentService
{
    @Autowired
    private DormStudentMapper dormStudentMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private DormDormitoryMapper dormDormitoryMapper;

    /**
     * 查询学生信息
     *
     * @param stuId 学生信息主键
     * @return 学生信息
     */
    @Override
    public DormStudent selectDormStudentByStuId(Long stuId)
    {
        return dormStudentMapper.selectDormStudentByStuId(stuId);
    }

    /**
     * 通过用户ID查询学生信息
     *
     * @param userId 用户ID
     * @return 学生信息
     */
    @Override
    public DormStudent selectDormStudentByUserId(Long userId)
    {
        return dormStudentMapper.selectDormStudentByUserId(userId);
    }

    /**
     * 查询学生信息列表
     *
     * @param dormStudent 学生信息
     * @return 学生信息
     */
    @Override
    public List<DormStudent> selectDormStudentList(DormStudent dormStudent)
    {
        return dormStudentMapper.selectDormStudentList(dormStudent);
    }

    /**
     * 新增学生信息
     *
     * @param dormStudent 学生信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDormStudent(DormStudent dormStudent)
    {
        dormStudent.setCreateTime(DateUtils.getNowDate());
        int rows = dormStudentMapper.insertDormStudent(dormStudent);

        // 同步更新用户表信息
        syncUserInfo(dormStudent);

        return rows;
    }

    /**
     * 修改学生信息
     *
     * @param dormStudent 学生信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDormStudent(DormStudent dormStudent)
    {
        dormStudent.setUpdateTime(DateUtils.getNowDate());
        int rows = dormStudentMapper.updateDormStudent(dormStudent);

        // 同步更新用户表信息
        syncUserInfo(dormStudent);

        return rows;
    }

    /**
     * 批量删除学生信息
     *
     * @param stuIds 需要删除的学生信息主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDormStudentByStuIds(Long[] stuIds)
    {
        // 删除前清空关联用户的宿舍信息
        for (Long stuId : stuIds) {
            DormStudent student = dormStudentMapper.selectDormStudentByStuId(stuId);
            if (student != null && student.getUserId() != null) {
                clearUserDormInfo(student.getUserId());
            }
        }

        return dormStudentMapper.deleteDormStudentByStuIds(stuIds);
    }

    /**
     * 删除学生信息信息
     *
     * @param stuId 学生信息主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDormStudentByStuId(Long stuId)
    {
        // 删除前清空关联用户的宿舍信息
        DormStudent student = dormStudentMapper.selectDormStudentByStuId(stuId);
        if (student != null && student.getUserId() != null) {
            clearUserDormInfo(student.getUserId());
        }

        return dormStudentMapper.deleteDormStudentByStuId(stuId);
    }

    /**
     * 同步学生信息到用户表
     *
     * @param dormStudent 学生信息
     */
    private void syncUserInfo(DormStudent dormStudent) {
        // 如果学生信息关联了用户，则同步更新用户表
        if (dormStudent.getUserId() != null) {
            SysUser user = new SysUser();
            user.setUserId(dormStudent.getUserId());

            // 设置宿舍ID（注意类型转换：DormStudent的dorId是Long，SysUser的dorId是Integer）
            if (dormStudent.getDorId() != null) {
                user.setDorId(dormStudent.getDorId().intValue());
            }

            // 设置宿舍楼ID
            if (dormStudent.getfId() != null) {
                user.setfId(dormStudent.getfId());
            }

            // 设置学号
            if (dormStudent.getStuCode() != null) {
                user.setStuCode(String.valueOf(dormStudent.getStuCode()));
            }

            // 查询并设置宿舍名称
            if (dormStudent.getDorId() != null) {
                DormDormitory dormitory = dormDormitoryMapper.selectDormDormitoryByDorId(dormStudent.getDorId());
                if (dormitory != null) {
                    user.setDorName(dormitory.getDorName());
                }
            }

            // 更新用户信息
            sysUserMapper.updateUser(user);
        }
    }

    /**
     * 清空用户的宿舍信息
     *
     * @param userId 用户ID
     */
    private void clearUserDormInfo(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setDorId(null);
        user.setDorName(null);
        user.setfId(null);
        user.setStuCode(null);

        sysUserMapper.updateUser(user);
    }
}