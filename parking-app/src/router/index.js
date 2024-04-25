import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
// import Home from '../views/Home.vue'

import store from '../store'
import Wrapper from "@/components/layouts/Wrapper";
import MakeReservation from "@/views/MakeReservation";
import AdminWrapper from "@/components/layouts/AdminWrapper";
import AdminHome from "@/views/admin/Home";
import Reservations from "@/views/Reservations";
import Register from "@/views/Register";
import Entries from "@/views/admin/Entries";



Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    component: Register
  },
  {
    path: '',
    meta: {
      requiresAuth: true
    },
    component: Wrapper,
    children: [
      {
        path: '',
        name: 'home',
        component: Reservations,
        meta: {
          title: "Reservations",
          showButton: true
        },
      },
      // {
      //   path: 'reservations',
      //   name: 'reservations',
      //   component: Reservations,
      //   meta: {
      //     title: "Reservations"
      //   },
      // },
      {
        path: 'make-reservation',
        name: 'make-reservation',
        component: MakeReservation,
        meta: {
          title: "Make a reservation"
        },
      },
    ]
  },
  {
    path: '/admin',
    meta: {
      requiresAuth: true,
      admin: true
    },
    component: AdminWrapper,
    children: [
      {
        path: '',
        name: 'admin-home',
        component: AdminHome,
        meta: {
          title: "All Reservations"
        },
      },
      {
        path: 'entries',
        name: 'entries',
        component: Entries,
        meta: {
          title: "All Entries"
        },
      },
      {
        path: 'make-reservation',
        name: 'admin-make-reservation',
        component: MakeReservation,
        meta: {
          title: "Make a reservation"
        },
      },
    ]
  },
  {
    path: '*',
    redirect: ''
  }
];

const router = new VueRouter({
  // mode: 'history',
  // base: process.env.BASE_URL,
  routes
});
router.beforeEach((to, from, next) => {
  if(to.matched.some(record => record.meta.requiresAuth)) {
    store.dispatch("auth/_loggedIn").then(()=>{

      if(to.matched.some(record => record.meta.admin)) {
        if (store.state.auth.user.role === "ADMIN") {
          next();
        }
        else {
          next({
            path: '/',
          })
        }
      } else {
        next();
      }
    }).catch(()=>{
      next({
        path: '/login',
      })
    });
  }
  else {
    next()
  }
});
export default router
