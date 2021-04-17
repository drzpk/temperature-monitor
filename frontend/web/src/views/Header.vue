<template>
    <header>
        <div>
            <div>
                <font-awesome-icon icon="thermometer-half"/>
                <h1>Temperature monitor</h1>
            </div>
        </div>
    </header>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class Header extends Vue {

    }
</script>

<style lang="scss">
    header {
        height: 4em;
        background-color: powderblue;
        text-align: center;

        > div {
            display: inline-block;
            height: 100%;
            font-weight: bold;

            > div {
                display: flex;
                height: 100%;
                align-items: center;
                font-size: 1.6em;

                > h1 {
                    margin: 0 0.4em;
                    font-size: inherit;
                }
            }
        }
    }
</style>