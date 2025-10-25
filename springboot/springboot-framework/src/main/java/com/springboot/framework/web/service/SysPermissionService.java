package com.springboot.framework.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.springboot.common.core.domain.entity.SysRole;
import com.springboot.common.core.domain.entity.SysUser;
import com.springboot.system.service.ISysMenuService;
import com.springboot.system.service.ISysRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户权限处理
 * 
 * 
 */
@Component
public class SysPermissionService
{
    private static final Logger logger = LoggerFactory.getLogger(SysPermissionService.class);
    
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取角色数据权限
     * 
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user)
    {
        logger.info("=== 获取用户角色权限开始 ===");
        logger.info("用户ID: {}, 用户名: {}", user.getUserId(), user.getUserName());
        
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            logger.info("用户是管理员，添加admin角色");
            roles.add("admin");
        }
        else
        {
            logger.info("用户不是管理员，从数据库查询角色权限");
            Set<String> rolePermissions = roleService.selectRolePermissionByUserId(user.getUserId());
            logger.info("从数据库查询到的角色权限: {}", rolePermissions);
            roles.addAll(rolePermissions);
        }
        
        logger.info("最终角色权限集合: {}", roles);
        logger.info("=== 获取用户角色权限结束 ===");
        return roles;
    }

    /**
     * 获取菜单数据权限
     * 
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user)
    {
        logger.info("=== 获取用户菜单权限开始 ===");
        logger.info("用户ID: {}, 用户名: {}", user.getUserId(), user.getUserName());
        
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            logger.info("用户是管理员，添加*:*:*权限");
            perms.add("*:*:*");
        }
        else
        {
            logger.info("用户不是管理员，查询具体权限");
            List<SysRole> roles = user.getRoles();
            logger.info("用户角色列表: {}", roles != null ? roles.size() : 0);
            
            if (!CollectionUtils.isEmpty(roles))
            {
                logger.info("通过角色查询权限");
                // 多角色设置permissions属性，以便数据权限匹配权限
                for (SysRole role : roles)
                {
                    logger.info("处理角色: ID={}, Key={}, Name={}", role.getRoleId(), role.getRoleKey(), role.getRoleName());
                    Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId());
                    logger.info("角色 {} 的权限: {}", role.getRoleKey(), rolePerms);
                    logger.info("角色 {} 权限中是否包含dormitory:out:confirmReturn: {}", role.getRoleKey(), rolePerms.contains("dormitory:out:confirmReturn"));
                    role.setPermissions(rolePerms);
                    perms.addAll(rolePerms);
                }
            }
            else
            {
                logger.info("用户没有角色，直接通过用户ID查询权限");
                Set<String> userPerms = menuService.selectMenuPermsByUserId(user.getUserId());
                logger.info("用户直接权限: {}", userPerms);
                logger.info("用户直接权限中是否包含dormitory:out:confirmReturn: {}", userPerms.contains("dormitory:out:confirmReturn"));
                perms.addAll(userPerms);
            }
        }
        
        logger.info("最终菜单权限集合: {}", perms);
        logger.info("权限集合中是否包含dormitory:out:confirmReturn: {}", perms.contains("dormitory:out:confirmReturn"));
        logger.info("权限集合中是否包含dormitory:student:query: {}", perms.contains("dormitory:student:query"));
        logger.info("=== 获取用户菜单权限结束 ===");
        return perms;
    }
}
