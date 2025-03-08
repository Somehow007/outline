import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            redirect: '/course'
        },
        {
            path: '/',
            component: () => import(/* webpackChunkName: "home" */ '../components/common/Home.vue'),
            meta: { title: '自述文件' },
            children: [
                {
                    path: '/user',
                    name: 'user',
                    component: () => import(/* webpackChunkName: "table" */ '../components/mypage/User.vue'),
                    meta: { title: '用户信息' }
                },
                {
                    path: '/course',
                    name: 'course',
                    component: () => import(/* webpackChunkName: "table" */ '../components/mypage/Course.vue'),
                    meta: { title: '课程信息' }
                },
                {
                    path: '/outline',
                    name: 'outline',
                    component: () => import(/* webpackChunkName: "table" */ '../components/mypage/Outline.vue'),
                    meta: { title: '大纲信息' }
                },
                {
                    path: '/college',
                    name: 'college',
                    component: () => import(/* webpackChunkName: "table" */ '../components/mypage/College.vue'),
                    meta: { title: '学院信息' }
                },
                {
                    path: '/major',
                    name: 'major',
                    component: () => import(/* webpackChunkName: "table" */ '../components/mypage/Major.vue'),
                    meta: { title: '专业信息' }
                }
            ]
        },
        {
            path: '/login',
            component: () => import(/* webpackChunkName: "login" */ '../components/mypage/Login.vue'),
            meta: { title: '登录' }
        },
        {
            path: '*',
            redirect: '/404'
        }
    ]
});
