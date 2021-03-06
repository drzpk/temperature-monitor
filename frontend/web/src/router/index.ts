import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'
import Devices from "@/views/Devices.vue";
import EditDevice from "@/views/settings/EditDevice.vue";
import DeviceDetails from "@/views/details/DeviceDetails.vue";
import Settings from "@/views/settings/Settings.vue";
import EditLogger from "@/views/settings/EditLogger.vue";

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
    },
    {
        path: '/devices/:id/details',
        component: DeviceDetails
    },
    {
        path: '/loggers/add',
        component: EditLogger
    },
    {
        path: '/loggers/:id/edit',
        component: EditLogger
    },
    {
        path: '/settings',
        component: Settings
    }
];

const router = new VueRouter({
    routes
});

export default router
