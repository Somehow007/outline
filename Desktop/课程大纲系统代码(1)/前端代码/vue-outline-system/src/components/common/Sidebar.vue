
<template>
    <div class="sidebar">
        <el-menu
            class="sidebar-el-menu"
            :default-active="onRoutes"
            :collapse="collapse"
            background-color="#324157"
            text-color="#bfcbd9"
            unique-opened
            router
        >
            <template v-for="item in items">
                <template v-if="item.subs">
                    <el-submenu :index="item.index" :key="item.index">
                        <template slot="title">
                            <i :class="item.icon"></i>
                            <span slot="title">{{ item.title }}</span>
                        </template>
                        <template v-for="subItem in item.subs">
                            <el-submenu
                                v-if="subItem.subs"
                                :index="subItem.index"
                                :key="subItem.index"
                            >
                                <template slot="title">{{ subItem.title }}</template>
                                <el-menu-item
                                    v-for="(threeItem,i) in subItem.subs"
                                    :key="i"
                                    :index="threeItem.index"
                                >{{ threeItem.title }}</el-menu-item>
                            </el-submenu>
                            <el-menu-item
                                v-else
                                :index="subItem.index"
                                :key="subItem.index"
                            >{{ subItem.title }}</el-menu-item>
                        </template>
                    </el-submenu>
                </template>
                <template v-else>
                    <el-menu-item :index="item.index" :key="item.index">
                        <i :class="item.icon"></i>
                        <span slot="title">{{ item.title }}</span>
                    </el-menu-item>
                </template>
            </template>
        </el-menu>
    </div>
</template>

<script>
import bus from '../common/bus';
import {getPersonalMessage} from "@/api";
export default {
    data() {
        return {
            collapse: false,
            items: [
              {
                icon: 'el-icon-lx-news',
                index: 'college',
                title: '学院信息'
              },
              {
                icon: 'el-icon-lx-news',
                index: 'major',
                title: '专业信息'
              },
              {
                icon: 'el-icon-lx-read',
                index: 'course',
                title: '课程信息'
              },
              {
                icon: 'el-icon-lx-cascades',
                index: 'outline',
                title: '大纲信息'
              }
            ],
          mainItems: [
            {
              icon: 'el-icon-lx-people',
              index: 'user',
              title: '用户信息'
            },
            {
              icon: 'el-icon-lx-news',
              index: 'college',
              title: '学院信息'
            },
            {
              icon: 'el-icon-lx-news',
              index: 'major',
              title: '专业信息'
            },
            {
              icon: 'el-icon-lx-read',
              index: 'course',
              title: '课程信息'
            },
            {
              icon: 'el-icon-lx-cascades',
              index: 'outline',
              title: '大纲信息'
            }
          ]
        };
    },
    computed: {
        onRoutes() {
            return this.$route.path.replace('/', '');
        }
    },
    created() {
      getPersonalMessage().then(res => {
        if (res.status === 200) {
          this.items=this.mainItems;
        }
      });
        // 通过 Event Bus 进行组件间通信，来折叠侧边栏
        bus.$on('collapse', msg => {
            this.collapse = msg;
            bus.$emit('collapse-content', msg);
        });
    }
};
</script>

<style scoped>
.sidebar {
    display: block;
    position: absolute;
    left: 0;
    top: 70px;
    bottom: 0;
    overflow-y: scroll;
}
.sidebar::-webkit-scrollbar {
    width: 0;
}
.sidebar-el-menu:not(.el-menu--collapse) {
    width: 250px;
}
.sidebar > ul {
    height: 100%;
}
</style>
