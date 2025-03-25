// @ts-ignore
import {createMemoryHistory, createRouter, createWebHistory} from 'vue-router'
import Search from './pages/Search.vue'
import DatingIndex from "./pages/DatingIndex.vue";
//@ts-ignore
import Friends from "./pages/Friends.vue";
import Individual from "./pages/Individual.vue";
import SearchResults from "./pages/SearchResults.vue";
import EditField from "./pages/EditField.vue";
import Login from "./pages/Login.vue";
import ListOfCircles from "./pages/ListOfCircles.vue";
//@ts-ignore
import TeamDetail from "./pages/TeamDetail.vue";

const routes = [
    {
        path: '/',
        component: DatingIndex,
        meta: { title: '社交主页' }
    },
    {
        path: '/friends',
        component: Friends,
        meta: { title: '圈子列表' }
    },
    {
        path: '/individual',
        component: Individual,
        meta: { title: '个人中心' }
    },
    {
        path: '/search',
        component: Search,
        meta: { title: '搜索用户' }
    },
    {
        path: '/results',
        component: SearchResults,
        meta: { title: '用户搜索结果' }
    },
    {
        path: '/edit',
        component: EditField,
        meta: { title: '个人编辑' }
    },
    {
        path: '/login',
        component: Login,
        meta: { title: '登录' }
    },
    {
        path: '/listOfCircles',
        component: ListOfCircles,
        meta: { title: '圈子搜索结果' }
    },
    {
        path: '/team/:id',
        component: TeamDetail,
        meta: { title: '队伍详情' }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

// @ts-ignore
router.beforeEach((to, from, next) => {
    const defaultTitle = '我的社交App'
    let title = to.meta.title ? to.meta.title.toString() : defaultTitle

    // 处理动态参数
    if (title.includes('{') && to.query) {
        Object.entries(to.query).forEach(([key, value]) => {
            title = title.replace(`{${key}}`, value as string)
        })
    }

    document.title = title
    next()
})

export default router