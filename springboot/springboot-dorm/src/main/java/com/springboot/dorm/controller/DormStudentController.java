package com.springboot.dorm.controller;

import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.common.annotation.Log;
import com.springboot.common.core.controller.BaseController;
import com.springboot.common.core.domain.AjaxResult;
import com.springboot.common.enums.BusinessType;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.service.IDormStudentService;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;
import com.springboot.common.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.springboot.common.utils.SecurityUtils;

/**
 * 学生信息Controller
 * 
 *
 * @date 2025-09-16
 */
@RestController
@RequestMapping("/dormitory/student")
public class DormStudentController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(DormStudentController.class);
    
    @Autowired
    private IDormStudentService dormStudentService;

    /**
     * 查询学生信息列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:student:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormStudent dormStudent)
    {
        if (isCurrentUserStudent()) {
            DormStudent currentStudent = getCurrentStudent();
            if (currentStudent == null) {
                logger.warn("学生角色未找到关联的学生信息，返回空列表");
                return getDataTable(new ArrayList<>());
            }
            dormStudent.setStuId(currentStudent.getStuId());
            dormStudent.setStuCode(currentStudent.getStuCode());
            dormStudent.setStuName(currentStudent.getStuName());
        }
        
        startPage();
        List<DormStudent> list = dormStudentService.selectDormStudentList(dormStudent);
        return getDataTable(list);
    }

    /**
     * 导出学生信息列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:student:export')")
    @Log(title = "学生信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormStudent dormStudent)
    {
        if (isCurrentUserStudent()) {
            DormStudent currentStudent = getCurrentStudent();
            if (currentStudent == null) {
                logger.warn("学生角色未找到关联的学生信息，阻止导出");
                ExcelUtil<DormStudent> util = new ExcelUtil<DormStudent>(DormStudent.class);
                util.exportExcel(response, new ArrayList<>(), "学生信息数据");
                return;
            }
            dormStudent.setStuId(currentStudent.getStuId());
            dormStudent.setStuCode(currentStudent.getStuCode());
            dormStudent.setStuName(currentStudent.getStuName());
        }
        List<DormStudent> list = dormStudentService.selectDormStudentList(dormStudent);
        ExcelUtil<DormStudent> util = new ExcelUtil<DormStudent>(DormStudent.class);
        util.exportExcel(response, list, "学生信息数据");
    }

    /**
     * 获取学生信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:student:query')")
    @GetMapping(value = "/{stuId}")
    public AjaxResult getInfo(@PathVariable("stuId") Long stuId)
    {
        if (isCurrentUserStudent()) {
            DormStudent currentStudent = getCurrentStudent();
            if (currentStudent == null || !currentStudent.getStuId().equals(stuId)) {
                return AjaxResult.error("无权查看其他学生的信息");
            }
        }
        return success(dormStudentService.selectDormStudentByStuId(stuId));
    }

    /**
     * 通过用户ID获取学生信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:student:query')")
    @GetMapping(value = "/user/{userId}")
    public AjaxResult getInfoByUserId(@PathVariable("userId") Long userId)
    {
        if (isCurrentUserStudent() && !SecurityUtils.getUserId().equals(userId)) {
            return AjaxResult.error("无权查看其他学生的信息");
        }
        logger.info("=== 通过用户ID获取学生信息开始 ===");
        logger.info("请求的用户ID: {}", userId);
        logger.info("当前登录用户: {}", SecurityUtils.getUsername());
        logger.info("当前登录用户ID: {}", SecurityUtils.getUserId());
        
        // 记录权限检查信息
        try {
            logger.info("权限检查通过，开始查询学生信息");
            DormStudent student = dormStudentService.selectDormStudentByUserId(userId);
            
            if (student != null) {
                logger.info("查询到学生信息: 学生ID={}, 学生姓名={}, 学号={}", 
                    student.getStuId(), student.getStuName(), student.getStuCode());
            } else {
                logger.warn("未找到用户ID {} 对应的学生信息", userId);
            }
            
            logger.info("=== 通过用户ID获取学生信息结束 ===");
            return success(student);
        } catch (Exception e) {
            logger.error("查询学生信息时发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 新增学生信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:student:add')")
    @Log(title = "学生信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormStudent dormStudent)
    {
        return toAjax(dormStudentService.insertDormStudent(dormStudent));
    }

    /**
     * 修改学生信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:student:edit')")
    @Log(title = "学生信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormStudent dormStudent)
    {
        // 检查当前用户是否为学生角色
        if (isCurrentUserStudent()) {
            DormStudent currentStudent = getCurrentStudent();
            if (currentStudent == null) {
                return AjaxResult.error("未找到学生信息");
            }
            if (dormStudent.getStuId() != null && !dormStudent.getStuId().equals(currentStudent.getStuId())) {
                return AjaxResult.error("学生只能修改自己的信息");
            }
            dormStudent.setStuId(currentStudent.getStuId());
            dormStudent.setStuCode(currentStudent.getStuCode());
        }
        
        return toAjax(dormStudentService.updateDormStudent(dormStudent));
    }

    /**
     * 删除学生信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:student:remove')")
    @Log(title = "学生信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{stuIds}")
    public AjaxResult remove(@PathVariable Long[] stuIds)
    {
        return toAjax(dormStudentService.deleteDormStudentByStuIds(stuIds));
    }

    private boolean isCurrentUserStudent()
    {
        try {
            return SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleKey()) || "student".equals(role.getRoleName()));
        } catch (Exception ex) {
            return false;
        }
    }

    private DormStudent getCurrentStudent()
    {
        try {
            return dormStudentService.selectDormStudentByUserId(SecurityUtils.getUserId());
        } catch (Exception ex) {
            logger.error("获取当前学生信息失败", ex);
            return null;
        }
    }
}
