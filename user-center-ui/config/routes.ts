export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        name: 'login',
        path: '/user/login',
        component: './user/Login',
      },
      {
        name: '注册',
        path: '/user/registerUser',
        component: './user/RegisterUser/registerUser.tsx',
      },

      {
        component: './404',
      },
    ],
  },
  {
    path: '/account',
    name: '账户管理',
    icon: 'UserOutlined',
    routes: [
      {
        path: '/account/center',
        name: '个人中心',
        component: './account/Center',
      },
      {
        path: '/account/settings',
        name: '个人设置',
        component: './account/Settings',
      },
    ],
  },
  {
    path: '/admin',
    name: 'admin',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      {
        path: '/admin/listOfUsers',
        name: '用户列表',
        icon: 'smile',
        component: './admin/ListOfUsers/listOfUsers.tsx',
      },
      {
        component: './404',
      },
    ],
  },
  {
    path: '/',
    redirect: '/account/center',
  },
  {
    component: './404',
  },
];
