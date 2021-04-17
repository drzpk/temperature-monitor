import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'
import Devices from "@/views/Devices.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        redirect: '/devices'
    },
    {
        path: '/devices',
        component: Devices
    }
];

const router = new VueRouter({
    routes
});

export default router
