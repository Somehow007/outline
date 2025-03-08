import axios from 'axios';

const service = axios.create({
    // process.env.NODE_ENV === 'development' ,
    headers: {
        "Access-Control-Allow-Origin": "http://localhost:80",
        "Access-Control-Allow-Methods": "GET, POST, PATCH, PUT, DELETE, OPTIONS",
        "Access-Control-Allow-Headers": "Origin, Content-Type, X-Auth-Token" },
    withCredentials: true ,
    // baseURL: 'http://202.195.168.13:8888',
    // baseURL: 'https://www.wjy321.cn:443',
    baseURL: 'http://localhost:80',
    timeout: 10000
});

//解决跨域cookies丢失
axios.defaults.withCredentials=true
//设置超时时间
axios.defaults.timeout = 10000;

service.interceptors.request.use(
    config => {
        return config;
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
);

service.interceptors.response.use(
    response => {
        if (response.status === 200) {
            return response;
        }
        else{
            Promise.reject();
        }
    },
    error => {
        console.log(error);
        if (error.response.status === 401) {
            localStorage.removeItem("errorMsg");
            localStorage.setItem("errorMsg",error.response.data.msg);
        }
        return Promise.reject();
    }
);

export default service;
