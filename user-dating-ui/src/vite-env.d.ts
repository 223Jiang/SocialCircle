/// <reference types="vite/client" />
interface ResponseResult<T> {
    code: number;
    msg?: string;
    data?: T;
    details?: string;
}

interface UserInformation {
    userId?: string;
    userName?: string;
    userCount?: string;
    userEmail?: string;
    sex?: number;
    userPhone?: string;
    imageUrl?: string;
    userStatus?: number;
    isAdmin?: number;
    createTime?: string;
    tags?: string;
    userDescription?: string;
}

interface PaginationData<T> {
    records: T[];
    total: number;
    current: number;
    size: number;
    pages: number;
}

interface Team {
    id?: string;
    name?: string;
    description?: string;
    num?: number;
    maxNum?: number;
    expireTime?: string;
    leaderId?: string;
    leaderName?: string;
    status?: number;
    createTime?: string;
    updateTime?: string;
    users?: UserInformation[];
}

interface TeamSearchRequest {
    nameKeyword?: string;
    status?: number;
    members?: number;
    joinable?: boolean;
}