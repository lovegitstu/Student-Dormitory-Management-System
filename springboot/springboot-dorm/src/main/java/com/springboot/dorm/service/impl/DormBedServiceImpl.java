package com.springboot.dorm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.common.utils.DateUtils;
import com.springboot.dorm.mapper.DormBedMapper;
import com.springboot.dorm.mapper.DormDormitoryMapper;
import com.springboot.dorm.domain.DormBed;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.domain.DormDormitory;
import com.springboot.dorm.service.IDormBedService;
import com.springboot.dorm.service.IDormStudentService;

/**
 * 床位管理Service业务层处理
 * 
 *
 * @date 2025-09-15
 */
@Service
public class DormBedServiceImpl implements IDormBedService 
{
    @Autowired
    private DormBedMapper dormBedMapper;
    
    @Autowired
    private DormDormitoryMapper dormDormitoryMapper;
    
    @Autowired
    private IDormStudentService dormStudentService;

    /**
     * 查询床位管理
     * 
     * @param bedId 床位管理主键
     * @return 床位管理
     */
    @Override
    public DormBed selectDormBedByBedId(Long bedId)
    {
        return dormBedMapper.selectDormBedByBedId(bedId);
    }

    /**
     * 查询床位管理列表
     * 
     * @param dormBed 床位管理
     * @return 床位管理
     */
    @Override
    public List<DormBed> selectDormBedList(DormBed dormBed)
    {
        System.out.println("=== DormBedServiceImpl.selectDormBedList 开始 ===");
        System.out.println("查询参数 - stuId: " + dormBed.getStuId() + ", bedStatus: " + dormBed.getBedStatus());
        System.out.println("查询参数 - dorId: " + dormBed.getDorId() + ", bedCode: " + dormBed.getBedCode());
        
        List<DormBed> result = dormBedMapper.selectDormBedList(dormBed);
        
        System.out.println("查询结果数量: " + (result != null ? result.size() : "null"));
        if (result != null && !result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                DormBed bed = result.get(i);
                System.out.println("结果[" + i + "] - bedId: " + bed.getBedId() + 
                                 ", dorId: " + bed.getDorId() + 
                                 ", stuId: " + bed.getStuId() + 
                                 ", stuName: " + bed.getStuName() + 
                                 ", bedCode: " + bed.getBedCode() + 
                                 ", bedStatus: " + bed.getBedStatus());
            }
        }
        System.out.println("=== DormBedServiceImpl.selectDormBedList 结束 ===");
        
        return result;
    }

    /**
     * 新增床位管理
     * 
     * @param dormBed 床位管理
     * @return 结果
     */
    @Override
    public int insertDormBed(DormBed dormBed)
    {
        dormBed.setCreateTime(DateUtils.getNowDate());
        return dormBedMapper.insertDormBed(dormBed);
    }

    /**
     * 修改床位管理
     * 
     * @param dormBed 床位管理
     * @return 结果
     */
    @Override
    public int updateDormBed(DormBed dormBed)
    {
        dormBed.setUpdateTime(DateUtils.getNowDate());
        
        // 获取原床位信息
        DormBed oldBed = dormBedMapper.selectDormBedByBedId(dormBed.getBedId());
        
        // 更新床位信息
        int result = dormBedMapper.updateDormBed(dormBed);
        
        // 如果床位分配发生变化，需要同步更新学生表
        if (result > 0) {
            // 处理原学生的宿舍信息清空
            if (oldBed != null && oldBed.getStuId() != null && 
                (dormBed.getStuId() == null || !oldBed.getStuId().equals(dormBed.getStuId()))) {
                clearStudentDormInfo(oldBed.getStuId());
            }
            
            // 处理新学生的宿舍信息更新
            if (dormBed.getStuId() != null) {
                updateStudentDormInfo(dormBed.getStuId(), dormBed.getDorId());
            }
        }
        
        return result;
    }

    /**
     * 批量删除床位管理
     * 
     * @param bedIds 需要删除的床位管理主键
     * @return 结果
     */
    @Override
    public int deleteDormBedByBedIds(Long[] bedIds)
    {
        return dormBedMapper.deleteDormBedByBedIds(bedIds);
    }

    /**
     * 删除床位管理信息
     * 
     * @param bedId 床位管理主键
     * @return 结果
     */
    @Override
    public int deleteDormBedByBedId(Long bedId)
    {
        // 获取床位信息
        DormBed bed = dormBedMapper.selectDormBedByBedId(bedId);
        
        // 如果床位有学生，先清空学生的宿舍信息
        if (bed != null && bed.getStuId() != null) {
            clearStudentDormInfo(bed.getStuId());
        }
        
        return dormBedMapper.deleteDormBedByBedId(bedId);
    }
    
    /**
     * 更新学生的宿舍信息
     * 
     * @param stuId 学生ID
     * @param dorId 宿舍ID
     */
    private void updateStudentDormInfo(Long stuId, Long dorId) {
        if (stuId == null || dorId == null) {
            return;
        }
        
        // 获取宿舍信息
        DormDormitory dormitory = dormDormitoryMapper.selectDormDormitoryByDorId(dorId);
        if (dormitory == null) {
            return;
        }
        
        // 获取学生信息
        DormStudent student = dormStudentService.selectDormStudentByStuId(stuId);
        if (student == null) {
            return;
        }
        
        // 创建一个新的学生对象，只包含需要更新的字段
        DormStudent updateStudent = new DormStudent();
        updateStudent.setStuId(stuId);
        updateStudent.setDorId(dorId);
        updateStudent.setfId(dormitory.getfId());
        updateStudent.setStuStatus("1"); // 入住后激活学生状态
        
        // 保存更新 - 更新宿舍相关信息和学生状态
        dormStudentService.updateDormStudent(updateStudent);
    }
    
    /**
     * 清空学生的宿舍信息
     * 
     * @param stuId 学生ID
     */
    private void clearStudentDormInfo(Long stuId) {
        if (stuId == null) {
            return;
        }
        
        // 获取学生信息
        DormStudent student = dormStudentService.selectDormStudentByStuId(stuId);
        if (student == null) {
            return;
        }
        
        // 创建一个新的学生对象，只包含需要更新的字段
        DormStudent updateStudent = new DormStudent();
        updateStudent.setStuId(stuId);
        updateStudent.setDorId(null);
        updateStudent.setfId(null);
        updateStudent.setStuStatus("0"); // 退宿后设置为未激活状态
        
        // 保存更新 - 清空宿舍相关信息并更新学生状态
        dormStudentService.updateDormStudent(updateStudent);
    }
}
