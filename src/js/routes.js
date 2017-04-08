import Dashboard from "./components/Dashboard";
import Main from "./components/Main";
import Test from "./components/Test";
import Login from "./components/Login";
import User2 from "./components/city/User";
import UserAdd2 from "./components/city/UserAdd";
import UserEdit2 from "./components/city/UserEdit";
import Team from "./components/city/Team";
import Buyer from "./components/city/Buyer";
import Problem2 from "./components/city/Problem";

export default {
  path: '/',
  component: Main,
  indexRoute: {component: Login},
  childRoutes: [
    { path: 'dashboard2', component: Dashboard},
    { path: 'user2', component: User2},
    { path: 'user2/add', component: UserAdd2},
    { path: 'user2/edit', component: UserEdit2},
    { path: 'team', component: Team},
    { path: 'buyer', component: Buyer},
    { path: 'problem2', component: Problem2},
    { path: 'test', component: Test}
  ]
};