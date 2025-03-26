import axios from 'axios';
import type { AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import {showDialog, showFailToast, showSuccessToast} from 'vant';
import router from "../router.ts"; // 引入样式

// Define the structure of the API response
// 创建 Axios 实例
const instance = axios.create({
    baseURL: 'http://localhost:8081/api', // 全局基础路径，可以根据环境变量动态设置
    timeout: 20000, // 请求超时时间
    withCredentials: true, // 允许携带凭证（重要）
});

// 请求拦截器
instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        return config;
    },
    (error) => {
        // 处理请求错误
        console.error('请求错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
instance.interceptors.response.use(
    (response: AxiosResponse<ResponseResult<any>>) => {
        const { data } = response;

        // 检查响应状态码
        if (data.code === 200) {
            if (data.details != null) {
                showSuccessToast(data.details as string);
            }
            return {
                ...response,
                data: data.data,
            };
        } else {
            if (data.code === 401) {
                showDialog({
                    title: '提示',
                    message: '您还未登录，是否前往登录页面？',
                    confirmButtonText: '去登录',
                    cancelButtonText: '取消',
                }).then(() => {
                    // 点击确认跳转登录页
                    router.push('/login');
                })
                .catch(() => {
                    // 点击取消或遮罩层关闭
                });
            } else {
                showFailToast(data.details as string);
            }

            // 根据后端返回的状态码处理错误
            console.error(`请求失败: ${data.details}`);
            return Promise.reject(data);
        }
    },
    (error) => {
        // 处理响应错误
        console.error('响应错误:', error);
        if (error.response) {
            const { data } = error.response;
            const code = data.code;
            switch (code) {
                case 401:
                    console.error('未授权，请登录');
                    break;
                case 403:
                    console.error('禁止访问');
                    break;
                case 404:
                    console.error('资源未找到');
                    break;
                default:
                    console.error(`服务器错误: ${code}`);
            }
        }
        return Promise.reject(error);
    }
);

export default instance;