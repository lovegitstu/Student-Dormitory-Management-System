const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  device: state => state.app.device,
  dict: state => state.dict.dict,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  introduction: state => state.user.introduction,
  roles: state => state.user.roles,
  permissions: state => state.user.permissions,
  permission_routes: state => state.permission.routes,
  topbarRouters:state => state.permission.topbarRouters,
  defaultRoutes:state => state.permission.defaultRoutes,
  sidebarRouters:state => state.permission.sidebarRouters,
  // 添加userInfo getter，包含完整的用户信息
  userInfo: state => ({
    userId: state.user.userId,
    userName: state.user.name,
    nickName: state.user.nickName,
    avatar: state.user.avatar,
    roles: state.user.roles,
    permissions: state.user.permissions,
    fId: state.user.fId,
    dorId: state.user.dorId
  })
}
export default getters
