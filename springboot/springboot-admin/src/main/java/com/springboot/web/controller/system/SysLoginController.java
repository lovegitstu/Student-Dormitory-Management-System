package com.springboot.web.controller.system;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.common.constant.Constants;
import com.springboot.common.core.domain.AjaxResult;
import com.springboot.common.core.domain.entity.SysMenu;
import com.springboot.common.core.domain.entity.SysUser;
import com.springboot.common.core.domain.model.LoginBody;
import com.springboot.common.utils.SecurityUtils;
import com.springboot.framework.web.service.SysLoginService;
import com.springboot.framework.web.service.SysPermissionService;
import com.springboot.system.service.ISysMenuService;
import com.springboot.dorm.service.IDormStudentService;
import com.springboot.dorm.domain.DormStudent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录验证
 *
 */
@RestController
public class SysLoginController
{
    private static final Logger logger = LoggerFactory.getLogger(SysLoginController.class);
    
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private IDormStudentService dormStudentService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        logger.info("=== 获取用户信息开始 ===");
        
        SysUser user = SecurityUtils.getLoginUser().getUser();
        logger.info("当前登录用户: {}, 用户ID: {}", user.getUserName(), user.getUserId());
        
        // 查询用户对应的学生信息，获取宿舍信息
        DormStudent dormStudent = dormStudentService.selectDormStudentByUserId(user.getUserId());
        if (dormStudent != null && dormStudent.getDormDormitory() != null) {
            // 设置宿舍名称到用户对象中
            user.setDorName(dormStudent.getDormDormitory().getDorName());
            // 设置宿舍ID和宿舍楼ID
            user.setDorId(dormStudent.getDorId() != null ? dormStudent.getDorId().intValue() : null);
            user.setfId(dormStudent.getfId());
            logger.info("用户宿舍信息: 宿舍名称={}, 宿舍ID={}", user.getDorName(), user.getDorId());
        }
        
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        logger.info("用户角色集合: {}", roles);
        
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        logger.info("用户权限集合: {}", permissions);
        logger.info("权限集合中是否包含dormitory:bills:recharge: {}", permissions.contains("dormitory:bills:recharge"));
        
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        
        logger.info("=== 获取用户信息结束 ===");
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
