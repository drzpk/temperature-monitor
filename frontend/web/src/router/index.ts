import Overview from '@/views/Overview.vue';
import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        component: Overview
    }
];

const router = new VueRouter({
    routes
});

export default router
