import Placeholder from '@/views/Placeholder.vue';
import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        component: Placeholder
    }
];

const router = new VueRouter({
    routes
});

export default router
