<template>
    <div id="overview">
        <DeviceSummaryPanel/>
    </div>
</template>

<script>
    import {Component, Vue} from "vue-property-decorator";
    import DeviceSummaryPanel from "@/views/summary/DeviceSummaryPanel";

    @Component({
        components: {
            DeviceSummaryPanel
        }
    })
    export default class Overview extends Vue {

    }
</script>

<style lang="scss">
    #overview {
        margin: 10em;
        width: 30%;
    }
</style>