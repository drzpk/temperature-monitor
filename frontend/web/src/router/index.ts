import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'
import Devices from "@/views/Devices.vue";
import EditDevice from "@/views/EditDevice.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        redirect: '/devices'
    },
    {
        path: '/devices',
        component: Devices
    },
    {
        path: '/devices/add',
        component: EditDevice
    },
    {
        path: '/devices/:id/edit',
        component: EditDevice
    }
];

const router = new VueRouter({
    routes
});

export default router
