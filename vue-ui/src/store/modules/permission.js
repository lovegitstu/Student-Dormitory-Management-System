import auth from '@/plugins/auth'
import router, { constantRoutes, dynamicRoutes } from '@/router'
import { getRouters } from '@/api/menu'
import Layout from '@/layout/index'
import ParentView from '@/components/ParentView'
import InnerLink from '@/layout/components/InnerLink'

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    defaultRoutes: [],
    topbarRouters: [],
    sidebarRouters: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    },
    SET_DEFAULT_ROUTES: (state, routes) => {
      state.defaultRoutes = constantRoutes.concat(routes)
    },
    SET_TOPBAR_ROUTES: (state, routes) => {
      state.topbarRouters = routes
    },
    SET_SIDEBAR_ROUTERS: (state, routes) => {
      state.sidebarRouters = routes
    },
  },
  actions: {
    // 生成路由
    GenerateRoutes({ commit }) {
      return new Promise(resolve => {
        console.log('=== 开始生成路由 ===')
        // 向后端请求路由数据
        getRouters().then(res => {
          console.log('后端路由数据响应:', res)
          const sdata = JSON.parse(JSON.stringify(res.data))
          const rdata = JSON.parse(JSON.stringify(res.data))
          const sidebarRoutes = filterAsyncRouter(sdata)
          const rewriteRoutes = filterAsyncRouter(rdata, false, true)
          console.log('处理后的侧边栏路由:', sidebarRoutes)
          console.log('处理后的重写路由:', rewriteRoutes)
          
          const asyncRoutes = filterDynamicRoutes(dynamicRoutes);
          console.log('过滤后的动态路由:', asyncRoutes)
          
          rewriteRoutes.push({ path: '*', redirect: '/404', hidden: true })
          router.addRoutes(asyncRoutes);
          console.log('已添加动态路由到router')
          
          commit('SET_ROUTES', rewriteRoutes)
          commit('SET_SIDEBAR_ROUTERS', constantRoutes.concat(sidebarRoutes))
          commit('SET_DEFAULT_ROUTES', sidebarRoutes)
          commit('SET_TOPBAR_ROUTES', sidebarRoutes)
          console.log('=== 路由生成完成 ===')
          resolve(rewriteRoutes)
        }).catch(error => {
          console.error('获取后端路由数据失败:', error)
          reject(error)
        })
      })
    }
  }
}

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  return asyncRouterMap.filter(route => {
    if (type && route.children) {
      route.children = filterChildren(route.children)
    }
    if (route.component) {
      // Layout ParentView 组件特殊处理
      if (route.component === 'Layout') {
        route.component = Layout
      } else if (route.component === 'ParentView') {
        route.component = ParentView
      } else if (route.component === 'InnerLink') {
        route.component = InnerLink
      } else {
        route.component = loadView(route.component)
      }
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type)
    } else {
      delete route['children']
      delete route['redirect']
    }
    return true
  })
}

function filterChildren(childrenMap, lastRouter = false) {
  var children = []
  childrenMap.forEach((el, index) => {
    if (el.children && el.children.length) {
      if (el.component === 'ParentView' && !lastRouter) {
        el.children.forEach(c => {
          c.path = el.path + '/' + c.path
          if (c.children && c.children.length) {
            children = children.concat(filterChildren(c.children, c))
            return
          }
          children.push(c)
        })
        return
      }
    }
    if (lastRouter) {
      el.path = lastRouter.path + '/' + el.path
    }
    children = children.concat(el)
  })
  return children
}

// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes) {
  console.log('=== 开始过滤动态路由 ===')
  console.log('待过滤的路由数量:', routes.length)
  
  const res = []
  routes.forEach(route => {
    console.log('检查路由:', route.path)
    console.log('路由权限配置:', route.permissions)
    console.log('路由角色配置:', route.roles)
    
    if (route.permissions) {
      const hasPermission = auth.hasPermiOr(route.permissions)
      console.log('权限验证结果:', hasPermission)
      if (hasPermission) {
        res.push(route)
        console.log('✓ 路由通过权限验证:', route.path)
      } else {
        console.log('✗ 路由未通过权限验证:', route.path)
      }
    } else if (route.roles) {
      const hasRole = auth.hasRoleOr(route.roles)
      console.log('角色验证结果:', hasRole)
      if (hasRole) {
        res.push(route)
        console.log('✓ 路由通过角色验证:', route.path)
      } else {
        console.log('✗ 路由未通过角色验证:', route.path)
      }
    } else {
      console.log('⚠ 路由没有权限或角色配置，被跳过:', route.path)
    }
  })
  
  console.log('=== 动态路由过滤完成 ===')
  console.log('通过验证的路由数量:', res.length)
  console.log('通过验证的路由:', res.map(r => r.path))
  
  return res
}

export const loadView = (view) => {
  if (process.env.NODE_ENV === 'development') {
    return (resolve) => require([`@/views/${view}`], resolve)
  } else {
    // 使用 import 实现生产环境的路由懒加载
    return () => import(`@/views/${view}`)
  }
}

export default permission
