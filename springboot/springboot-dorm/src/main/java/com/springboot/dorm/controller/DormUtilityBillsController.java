package com.springboot.dorm.controller;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import com.springboot.common.utils.SecurityUtils;
import com.springboot.dorm.domain.DormUtilityBills;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.service.IDormUtilityBillsService;
import com.springboot.dorm.service.IDormStudentService;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;
import com.springboot.dorm.algorithm.UtilityBillCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 水电费管理Controller
 * 
 * 
 * @date 2025-09-20
 */
@RestController
@RequestMapping("/dormitory/bills")
public class DormUtilityBillsController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(DormUtilityBillsController.class);
    
    @Autowired
    private IDormUtilityBillsService dormUtilityBillsService;
    
    @Autowired
    private IDormStudentService dormStudentService;

    /**
     * 查询水电费管理列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormUtilityBills dormUtilityBills)
    {
        // 检查当前用户是否为学生角色
        boolean isStudent = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleKey()));
        
        // 如果是学生角色，只能查询自己寝室的水电费
        if (isStudent) {
            Long currentUserId = SecurityUtils.getUserId();
            DormStudent dormStudent = dormStudentService.selectDormStudentByUserId(currentUserId);
            
            if (dormStudent != null) {
                // 设置查询条件为学生所在的宿舍楼和宿舍
                dormUtilityBills.setfId(dormStudent.getfId());
                // 类型转换：DormStudent的dorId是Long类型，DormUtilityBills的dorId是Integer类型
                if (dormStudent.getDorId() != null) {
                    dormUtilityBills.setDorId(dormStudent.getDorId().intValue());
                }
            } else {
                // 如果找不到学生信息，返回空结果
                logger.warn("学生用户ID {} 未找到对应的学生信息", currentUserId);
                return getDataTable(new ArrayList<>());
            }
        }
        
        // 检查当前用户是否为宿管角色
        boolean isDormManager = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "man".equals(role.getRoleKey()));
        
        startPage();
        List<DormUtilityBills> list = dormUtilityBillsService.selectDormUtilityBillsList(dormUtilityBills);
        return getDataTable(list);
    }

    /**
     * 导出水电费管理列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:export')")
    @Log(title = "水电费管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormUtilityBills dormUtilityBills)
    {
        List<DormUtilityBills> list = dormUtilityBillsService.selectDormUtilityBillsList(dormUtilityBills);
        ExcelUtil<DormUtilityBills> util = new ExcelUtil<DormUtilityBills>(DormUtilityBills.class);
        util.exportExcel(response, list, "水电费管理数据");
    }

    /**
     * 获取水电费管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:query')")
    @GetMapping(value = "/{ubId}")
    public AjaxResult getInfo(@PathVariable("ubId") Integer ubId)
    {
        return success(dormUtilityBillsService.selectDormUtilityBillsByUbId(ubId));
    }

    /**
     * 新增水电费管理
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:add')")
    @Log(title = "水电费管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormUtilityBills dormUtilityBills)
    {
        // 检查当前用户是否为超级管理员、系统管理员或宿管，这些角色都拥有新增权限
        boolean isAdmin = SecurityUtils.getLoginUser().getUser().isAdmin();
        boolean isSubAdmin = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "subadmin".equals(role.getRoleKey()));
        boolean isDormManager = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "man".equals(role.getRoleKey()));
        
        if (isAdmin || isSubAdmin || isDormManager) {
            return toAjax(dormUtilityBillsService.insertDormUtilityBills(dormUtilityBills));
        }
        
        // 检查当前用户是否为学生角色，学生不能新增水电费
        boolean isStudent = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleKey()));
        
        if (isStudent) {
            return AjaxResult.error("学生无权限新增水电费记录");
        }
        
        return AjaxResult.error("无权限新增水电费记录");
    }

    /**
     * 修改水电费管理
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:edit')")
    @Log(title = "水电费管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormUtilityBills dormUtilityBills)
    {
        // 检查当前用户是否为超级管理员、系统管理员或宿管，这些角色都拥有修改权限
        boolean isAdmin = SecurityUtils.getLoginUser().getUser().isAdmin();
        boolean isSubAdmin = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "subadmin".equals(role.getRoleKey()));
        boolean isDormManager = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "man".equals(role.getRoleKey()));
        
        if (isAdmin || isSubAdmin || isDormManager) {
            return toAjax(dormUtilityBillsService.updateDormUtilityBills(dormUtilityBills));
        }
        
        // 检查当前用户是否为学生角色，学生不能修改水电费
        boolean isStudent = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleKey()));
        
        if (isStudent) {
            return AjaxResult.error("学生无权限修改水电费记录");
        }
        
        return AjaxResult.error("无权限修改水电费记录");
    }

    /**
     * 删除水电费管理
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:remove')")
    @Log(title = "水电费管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ubIds}")
    public AjaxResult remove(@PathVariable Integer[] ubIds)
    {
        // 检查当前用户是否为超级管理员、系统管理员或宿管，这些角色都拥有删除权限
        boolean isAdmin = SecurityUtils.getLoginUser().getUser().isAdmin();
        boolean isSubAdmin = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "subadmin".equals(role.getRoleKey()));
        boolean isDormManager = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "man".equals(role.getRoleKey()));
        
        if (isAdmin || isSubAdmin || isDormManager) {
            return toAjax(dormUtilityBillsService.deleteDormUtilityBillsByUbIds(ubIds));
        }
        
        // 检查当前用户是否为学生角色，学生不能删除水电费
        boolean isStudent = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleKey()));
        
        if (isStudent) {
            return AjaxResult.error("学生无权限删除水电费记录");
        }
        
        return AjaxResult.error("无权限删除水电费记录");
    }

    /**
     * 学生充值功能 - 指定账单ID充值
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:recharge')")
    @PostMapping("/recharge/{ubId}")
    public AjaxResult rechargeBills(@PathVariable("ubId") Integer ubId, @RequestBody java.util.Map<String, Object> requestBody)
    {
        logger.info("=== 指定账单充值接口调用开始 ===");
        logger.info("当前登录用户: {}", SecurityUtils.getUsername());
        logger.info("当前用户ID: {}", SecurityUtils.getUserId());
        logger.info("账单ID: {}", ubId);
        
        // 检查当前用户是否为学生角色
        boolean isStudent = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleKey()));
        
        if (!isStudent) {
            logger.warn("非学生用户尝试充值");
            return AjaxResult.error("只有学生可以进行充值操作");
        }
        
        // 获取充值金额
        Double rechargeAmount = null;
        if (requestBody != null && requestBody.containsKey("amount")) {
            Object amountObj = requestBody.get("amount");
            if (amountObj instanceof Number) {
                rechargeAmount = ((Number) amountObj).doubleValue();
            }
        }
        
        if (rechargeAmount == null || rechargeAmount <= 0) {
            logger.warn("充值金额无效: {}", rechargeAmount);
            return AjaxResult.error("请输入有效的充值金额");
        }
        
        logger.info("充值金额: {}", rechargeAmount);
        
        // 查询指定的水电费记录
        DormUtilityBills dormUtilityBills = dormUtilityBillsService.selectDormUtilityBillsByUbId(ubId);
        
        if (dormUtilityBills == null) {
            logger.warn("未找到指定的水电费记录，账单ID: {}", ubId);
            return AjaxResult.error("未找到指定的水电费记录");
        }
        
        logger.info("找到水电费信息: 账单ID={}, 当前余额={}, 总费用={}", 
                   ubId, dormUtilityBills.getUbBalance(), dormUtilityBills.getUbTotalCost());
        
        // 更新余额
        java.math.BigDecimal currentBalance = dormUtilityBills.getUbBalance() != null ? 
                                            dormUtilityBills.getUbBalance() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal newBalance = currentBalance.add(java.math.BigDecimal.valueOf(rechargeAmount));
        
        logger.info("=== 充值前状态 ===");
        logger.info("充值前余额: {}", currentBalance);
        logger.info("充值金额: {}", rechargeAmount);
        logger.info("充值后余额: {}", newBalance);
        logger.info("账单总费用: {}", dormUtilityBills.getUbTotalCost());
        logger.info("充值前缴费状态: {}", dormUtilityBills.getUbPaymentStatus());
        
        dormUtilityBills.setUbBalance(newBalance);
    dormUtilityBills.setSkipAutoPayment(Boolean.TRUE);
        
        logger.info("=== 开始更新数据库 ===");
        // 更新数据库（会触发自动扣费逻辑）
        int result = dormUtilityBillsService.updateDormUtilityBills(dormUtilityBills);
        logger.info("数据库更新结果: {}", result > 0 ? "成功" : "失败");
        
        if (result > 0) {
            logger.info("=== 数据库更新成功，重新查询最新状态 ===");
            // 重新查询数据库获取最新状态（包括自动扣费后的状态）
            DormUtilityBills updatedBills = dormUtilityBillsService.selectDormUtilityBillsByUbId(ubId);
            
            logger.info("=== 充值后最终状态 ===");
            logger.info("最终余额: {}", updatedBills.getUbBalance());
            logger.info("最终缴费状态: {}", updatedBills.getUbPaymentStatus());
            logger.info("账单总费用: {}", updatedBills.getUbTotalCost());
            
            logger.info("充值成功: 账单ID={}, 充值金额={}, 最终余额={}, 缴费状态={}", 
                       ubId, rechargeAmount, updatedBills.getUbBalance(), updatedBills.getUbPaymentStatus());
            
            // 生成付款码图片URL
            String paymentCodeUrl = generatePaymentCode(updatedBills);
            
            // 创建返回数据（使用更新后的数据）
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("paymentCode", paymentCodeUrl);
            data.put("amount", rechargeAmount);
            data.put("newBalance", updatedBills.getUbBalance());
            data.put("totalCost", updatedBills.getUbTotalCost());
            data.put("paymentStatus", updatedBills.getUbPaymentStatus());
            data.put("ubId", ubId);
            
            logger.info("=== 指定账单充值接口调用结束 ===");
            return AjaxResult.success("充值成功", data);
        } else {
            logger.error("充值失败: 数据库更新失败");
            return AjaxResult.error("充值失败，请重试");
        }
    }

    /**
     * 学生充值功能 - 自动识别寝室
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:recharge')")
    @PostMapping("/rechargeMyDorm")
    public AjaxResult rechargeMyDorm(@RequestBody java.util.Map<String, Object> requestBody)
    {
        logger.info("=== 自动识别寝室充值接口调用开始 ===");
        logger.info("当前登录用户: {}", SecurityUtils.getUsername());
        logger.info("当前用户ID: {}", SecurityUtils.getUserId());
        
        // 检查当前用户是否为学生角色
        boolean isStudent = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleKey()));
        
        if (!isStudent) {
            logger.warn("非学生用户尝试充值");
            return AjaxResult.error("只有学生可以进行充值操作");
        }
        
        Long currentUserId = SecurityUtils.getUserId();
        DormStudent dormStudent = dormStudentService.selectDormStudentByUserId(currentUserId);
        
        if (dormStudent == null) {
            logger.warn("未找到学生信息，用户ID: {}", currentUserId);
            return AjaxResult.error("未找到学生信息，请联系管理员");
        }
        
        if (dormStudent.getDorId() == null) {
            logger.warn("学生未分配寝室，学生ID: {}", dormStudent.getStuId());
            return AjaxResult.error("您还未分配寝室，无法进行充值");
        }
        
        // 获取充值金额
        Double rechargeAmount = null;
        if (requestBody != null && requestBody.containsKey("amount")) {
            Object amountObj = requestBody.get("amount");
            if (amountObj instanceof Number) {
                rechargeAmount = ((Number) amountObj).doubleValue();
            }
        }
        
        if (rechargeAmount == null || rechargeAmount <= 0) {
            logger.warn("充值金额无效: {}", rechargeAmount);
            return AjaxResult.error("请输入有效的充值金额");
        }
        
        logger.info("充值金额: {}", rechargeAmount);
        
        // 查询该寝室的水电费信息
        DormUtilityBills queryBills = new DormUtilityBills();
        queryBills.setfId(dormStudent.getfId());
        queryBills.setDorId(dormStudent.getDorId().intValue());
        
        List<DormUtilityBills> billsList = dormUtilityBillsService.selectDormUtilityBillsList(queryBills);
        
        if (billsList.isEmpty()) {
            logger.warn("未找到寝室水电费记录，寝室ID: {}", dormStudent.getDorId());
            return AjaxResult.error("未找到您寝室的水电费记录，请联系管理员");
        }
        
        // 获取最新的水电费记录
        DormUtilityBills dormUtilityBills = billsList.get(0);
        
        logger.info("找到水电费信息: 寝室ID={}, 当前余额={}, 总费用={}", 
                   dormUtilityBills.getDorId(), dormUtilityBills.getUbBalance(), dormUtilityBills.getUbTotalCost());
        
        // 更新余额
        java.math.BigDecimal currentBalance = dormUtilityBills.getUbBalance() != null ? 
                                            dormUtilityBills.getUbBalance() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal newBalance = currentBalance.add(java.math.BigDecimal.valueOf(rechargeAmount));
        
        logger.info("=== 充值前状态 ===");
        logger.info("充值前余额: {}", currentBalance);
        logger.info("充值金额: {}", rechargeAmount);
        logger.info("充值后余额: {}", newBalance);
        logger.info("账单总费用: {}", dormUtilityBills.getUbTotalCost());
        logger.info("充值前缴费状态: {}", dormUtilityBills.getUbPaymentStatus());
        
        dormUtilityBills.setUbBalance(newBalance);
    dormUtilityBills.setSkipAutoPayment(Boolean.TRUE);
        
        logger.info("=== 开始更新数据库 ===");
        // 更新数据库（会触发自动扣费逻辑）
        int result = dormUtilityBillsService.updateDormUtilityBills(dormUtilityBills);
        logger.info("数据库更新结果: {}", result > 0 ? "成功" : "失败");
        
        if (result > 0) {
            logger.info("=== 数据库更新成功，重新查询最新状态 ===");
            // 重新查询数据库获取最新状态（包括自动扣费后的状态）
            DormUtilityBills updatedBills = dormUtilityBillsService.selectDormUtilityBillsByUbId(dormUtilityBills.getUbId());
            
            logger.info("=== 充值后最终状态 ===");
            logger.info("最终余额: {}", updatedBills.getUbBalance());
            logger.info("最终缴费状态: {}", updatedBills.getUbPaymentStatus());
            logger.info("账单总费用: {}", updatedBills.getUbTotalCost());
            
            // 生成付款码图片URL（这里可以集成真实的支付系统）
            String paymentCodeUrl = generatePaymentCode(updatedBills);
            logger.info("生成的付款码URL: {}", paymentCodeUrl);
            
            // 创建数据对象（使用更新后的数据）
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("paymentCode", paymentCodeUrl);  // 付款码内容
            data.put("amount", rechargeAmount);  // 使用用户选择的充值金额
            data.put("currentBalance", updatedBills.getUbBalance());  // 更新后的余额
            data.put("totalCost", updatedBills.getUbTotalCost());  // 总费用
            data.put("dormName", dormStudent.getDormDormitory() != null ? dormStudent.getDormDormitory().getDorName() : "未知寝室");
            data.put("floorName", dormStudent.getDormFloor() != null ? dormStudent.getDormFloor().getfName() : "未知楼层");
            data.put("ubId", updatedBills.getUbId());  // 添加订单ID
            data.put("paymentStatus", updatedBills.getUbPaymentStatus());  // 添加缴费状态
            
            logger.info("充值成功: 寝室ID={}, 充值金额={}, 最终余额={}, 缴费状态={}", 
                       dormUtilityBills.getDorId(), rechargeAmount, updatedBills.getUbBalance(), updatedBills.getUbPaymentStatus());
            logger.info("返回数据: paymentCode={}, amount={}, currentBalance={}, totalCost={}, dormName={}, ubId={}", 
                       paymentCodeUrl, rechargeAmount, updatedBills.getUbBalance(), updatedBills.getUbTotalCost(),
                       data.get("dormName"), updatedBills.getUbId());
            logger.info("=== 自动识别寝室充值接口调用结束 ===");
            
            return AjaxResult.success("充值成功", data);
        } else {
            logger.error("充值失败: 数据库更新失败");
            return AjaxResult.error("充值失败，请重试");
        }
    }

    /**
     * 学生直接缴费功能 - 从宿舍余额扣除费用
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:recharge')")
    @PostMapping("/payBill/{ubId}")
    public AjaxResult payBill(@PathVariable("ubId") Integer ubId)
    {
        logger.info("=== 学生直接缴费接口调用开始 ===");
        logger.info("当前登录用户: {}", SecurityUtils.getUsername());
        logger.info("当前用户ID: {}", SecurityUtils.getUserId());
        logger.info("账单ID: {}", ubId);
        
        // 检查当前用户是否为学生角色
        boolean isStudent = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleKey()));
        
        if (!isStudent) {
            logger.warn("非学生用户尝试缴费");
            return AjaxResult.error("只有学生可以进行缴费操作");
        }
        
        // 查询指定的水电费记录
        DormUtilityBills dormUtilityBills = dormUtilityBillsService.selectDormUtilityBillsByUbId(ubId);
        
        if (dormUtilityBills == null) {
            logger.warn("未找到指定的水电费记录，账单ID: {}", ubId);
            return AjaxResult.error("未找到指定的水电费记录");
        }
        
        // 检查是否已经缴费
        if (dormUtilityBills.getUbPaymentStatus() != null && dormUtilityBills.getUbPaymentStatus() == 1) {
            logger.warn("账单已缴费，账单ID: {}", ubId);
            return AjaxResult.error("该账单已缴费，无需重复缴费");
        }
        
        logger.info("找到水电费信息: 账单ID={}, 当前余额={}, 总费用={}", 
                   ubId, dormUtilityBills.getUbBalance(), dormUtilityBills.getUbTotalCost());
        
        // 检查余额是否充足
        java.math.BigDecimal currentBalance = dormUtilityBills.getUbBalance() != null ? 
                                            dormUtilityBills.getUbBalance() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalCost = dormUtilityBills.getUbTotalCost() != null ? 
                                        dormUtilityBills.getUbTotalCost() : java.math.BigDecimal.ZERO;
        
        if (currentBalance.compareTo(totalCost) < 0) {
            // 余额不足，返回提示信息
            java.math.BigDecimal shortfall = totalCost.subtract(currentBalance);
            logger.warn("余额不足，账单ID: {}, 当前余额: {}, 总费用: {}, 缺少: {}", 
                       ubId, currentBalance, totalCost, shortfall);
            
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("currentBalance", currentBalance);
            data.put("totalCost", totalCost);
            data.put("shortfall", shortfall);
            data.put("ubId", ubId);
            
            return AjaxResult.error("余额不足，还需充值 " + shortfall + " 元", data);
        }
        
        // 余额充足，直接扣费
        java.math.BigDecimal newBalance = currentBalance.subtract(totalCost);
        dormUtilityBills.setUbBalance(newBalance);
        dormUtilityBills.setUbPaymentStatus(1); // 设置为已缴费
    dormUtilityBills.setSkipAutoPayment(Boolean.TRUE);
        
        // 更新数据库
        int result = dormUtilityBillsService.updateDormUtilityBills(dormUtilityBills);
        
        if (result > 0) {
            logger.info("缴费成功: 账单ID={}, 扣费金额={}, 剩余余额={}", 
                       ubId, totalCost, newBalance);
            
            // 创建返回数据
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("ubId", ubId);
            data.put("paidAmount", totalCost);
            data.put("newBalance", newBalance);
            data.put("paymentStatus", 1);
            
            logger.info("=== 学生直接缴费接口调用结束 ===");
            return AjaxResult.success("缴费成功", data);
        } else {
            logger.error("缴费失败: 数据库更新失败");
            return AjaxResult.error("缴费失败，请重试");
        }
    }

    /**
     * 生成付款码图片URL
     */
    private String generatePaymentCode(DormUtilityBills dormUtilityBills) {
        // 返回静态付款码图片URL（前端运行在9527，后端运行在9528）
        return "http://localhost:9527/static/images/payment_code.jpg";
    }

    /**
     * 获取当前用户寝室的水电费余额信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:query')")
    @GetMapping("/myDormBalance")
    public AjaxResult getMyDormBalance()
    {
        logger.info("=== 获取当前用户寝室余额接口调用开始 ===");
        logger.info("当前登录用户: {}", SecurityUtils.getUsername());
        logger.info("当前用户ID: {}", SecurityUtils.getUserId());
        
        // 检查当前用户是否为学生角色
        boolean isStudent = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleKey()));
        
        if (!isStudent) {
            logger.warn("非学生用户尝试查询寝室余额");
            return AjaxResult.error("只有学生可以查询寝室余额");
        }
        
        Long currentUserId = SecurityUtils.getUserId();
        DormStudent dormStudent = dormStudentService.selectDormStudentByUserId(currentUserId);
        
        if (dormStudent == null) {
            logger.warn("未找到学生信息，用户ID: {}", currentUserId);
            return AjaxResult.error("未找到学生信息，请联系管理员");
        }
        
        if (dormStudent.getDorId() == null) {
            logger.warn("学生未分配寝室，学生ID: {}", dormStudent.getStuId());
            return AjaxResult.error("您还未分配寝室，无法查询余额");
        }
        
        // 查询该寝室的水电费信息
        DormUtilityBills queryBills = new DormUtilityBills();
        queryBills.setfId(dormStudent.getfId());
        queryBills.setDorId(dormStudent.getDorId().intValue());
        
        List<DormUtilityBills> billsList = dormUtilityBillsService.selectDormUtilityBillsList(queryBills);
        
        if (billsList.isEmpty()) {
            logger.info("未找到寝室水电费记录，寝室ID: {}", dormStudent.getDorId());
            return AjaxResult.error("未找到您寝室的水电费记录");
        }
        
        // 获取最新的水电费记录（假设按创建时间排序，取第一条）
        DormUtilityBills latestBill = billsList.get(0);
        
        // 创建返回数据
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("ubId", latestBill.getUbId());
        data.put("dormId", dormStudent.getDorId()); // 添加宿舍ID
        data.put("dormName", dormStudent.getDormDormitory() != null ? dormStudent.getDormDormitory().getDorName() : "未知寝室");
        data.put("floorName", dormStudent.getDormFloor() != null ? dormStudent.getDormFloor().getfName() : "未知楼层");
        data.put("balance", latestBill.getUbBalance());
        data.put("totalCost", latestBill.getUbTotalCost());
        data.put("usageDate", latestBill.getUbUsageDate());
        data.put("electricUsage", latestBill.getUbElectricUsage());
        data.put("waterUsage", latestBill.getUbWaterUsage());
        data.put("electricCost", latestBill.getUbElectricCost());
        data.put("waterCost", latestBill.getUbWaterCost());
        
        logger.info("成功获取寝室余额信息: 寝室={}, 余额={}", 
                   data.get("dormName"), latestBill.getUbBalance());
        logger.info("=== 获取当前用户寝室余额接口调用结束 ===");
        
        return AjaxResult.success("获取寝室余额成功", data);
    }
    
    /**
     * 计算阶梯水电费
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bills:query')")
    @PostMapping("/calculateTiered")
    public AjaxResult calculateTieredBill(@RequestBody java.util.Map<String, Object> requestBody)
    {
        logger.info("=== 计算阶梯水电费接口调用开始 ===");
        
        try {
            Long dormId = Long.valueOf(requestBody.get("dormId").toString());
            BigDecimal electricityUsage = new BigDecimal(requestBody.get("electricityUsage").toString());
            BigDecimal waterUsage = new BigDecimal(requestBody.get("waterUsage").toString());
            LocalDate billingStartDate = LocalDate.parse(requestBody.get("billingStartDate").toString());
            LocalDate billingEndDate = LocalDate.parse(requestBody.get("billingEndDate").toString());
            String splitMethodStr = requestBody.get("splitMethod").toString();
            
            UtilityBillCalculator.SplitMethod splitMethod = 
                UtilityBillCalculator.SplitMethod.valueOf(splitMethodStr.toUpperCase());
            
            UtilityBillCalculator.BillCalculationResult result = 
                dormUtilityBillsService.calculateTieredBill(
                    dormId, electricityUsage, waterUsage, 
                    billingStartDate, billingEndDate, splitMethod);
            
            logger.info("阶梯计费计算成功: 宿舍ID={}, 总费用={}", dormId, result.getTotalCost());
            logger.info("=== 计算阶梯水电费接口调用结束 ===");
            
            return AjaxResult.success("计算成功", result);
            
        } catch (Exception e) {
            logger.error("计算阶梯水电费失败: {}", e.getMessage(), e);
            return AjaxResult.error("计算失败: " + e.getMessage());
        }
    }
}
