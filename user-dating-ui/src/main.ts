import { createApp } from 'vue'
import App from './App.vue'
import router from './router.ts'
import 'vant/es/toast/style';
import 'vant/es/dialog/style';

const app = createApp(App);
app.use(router)
app.mount('#app');