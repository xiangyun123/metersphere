import Vue from "vue";
import VueRouter from 'vue-router'
import RouterSidebar from "./RouterSidebar";
import Setting from "../settings/Setting";
import Workspace from "../settings/Workspace";
import User from "../settings/User";
import CreateTestPlan from "../testPlan/CreateTestPlan";

Vue.use(VueRouter);

const router = new VueRouter({
  routes: [
    {
      path: "/sidebar", components: {
        sidebar: RouterSidebar
      }
    },
    {
      path: "/content", components: {
        content: Setting
      }, children: [
        {
          path: 'workspace',
          component: Workspace
        },
        {
          path: 'user',
          component: User
        }
      ]
    },
    {
      path: "/createTest", components: {
        content: CreateTestPlan
      }
    },]
});

export default router
