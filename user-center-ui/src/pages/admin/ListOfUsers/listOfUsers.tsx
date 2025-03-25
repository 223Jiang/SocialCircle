import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';
import { useRef } from 'react';
import { Button, message } from 'antd';
import request from 'umi-request';
import "./listOfUsers.less";

export const waitTimePromise = async (time: number = 100) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true);
    }, time);
  });
};

export const waitTime = async (time: number = 100) => {
  await waitTimePromise(time);
};

const columns: ProColumns<API.UserInformation>[] = [
  {
    dataIndex: 'index',
    valueType: 'indexBorder',
    width: 48,
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    copyable: true,
    ellipsis: true,
    formItemProps: {
      rules: [
        {
          required: true,
          message: '此项为必填项',
        },
      ],
    },
  },
  {
    title: '账户',
    dataIndex: 'userCount',
    copyable: true,
    search: false,
    ellipsis: true,
    formItemProps: {
      rules: [
        {
          required: true,
          message: '此项为必填项',
        },
      ],
    },
  },
  {
    title: '邮箱',
    dataIndex: 'userEmail',
    copyable: true,
    ellipsis: true,
    search: false,
  },
  {
    disable: true,
    title: '性别',
    dataIndex: 'sex',
    filters: true,
    onFilter: true,
    search: false,
    ellipsis: true,
    valueType: 'select',
    valueEnum: {
      1: {
        text: '女',
      },
      0: {
        text: '男',
      },
    },
  },
  {
    title: '手机号',
    dataIndex: 'userPhone',
    search: false,
    copyable: true,
    ellipsis: true,
  },
  {
    title: '用户头像',
    dataIndex: 'imageUrl',
    search: false,
    copyable: true,
    ellipsis: true,
    render: (_, record) => (
      <img
        src={record.imageUrl || 'https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=defaultAvatar.png&version_id=null'}
        alt="avatar"
        style={{
          width: 60,
          height: 60,
          borderRadius: '50%',
          objectFit: 'cover',
        }}
        onError={(e) => {
          // @ts-ignore
          e.target.src = 'https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=defaultAvatar.png&version_id=null';
        }}
      />
    ),
    width: 80,
    align: 'center',
  },
  {
    disable: true,
    title: '用户状态',
    dataIndex: 'userStatus',
    filters: true,
    onFilter: true,
    ellipsis: true,
    valueType: 'select',
    valueEnum: {
      1: {
        text: '禁用',
        status: 'Error',
      },
      0: {
        text: '正常',
        status: 'Success',
      },
    },
  },
  {
    disable: true,
    title: '用户角色',
    dataIndex: 'isAdmin',
    filters: true,
    onFilter: true,
    ellipsis: true,
    valueType: 'select',
    valueEnum: {
      1: {
        text: '管理员',
      },
      0: {
        text: '普通用户',
      },
    },
  },
  {
    title: '创建时间',
    key: 'showTime',
    dataIndex: 'createTime',
    valueType: 'date',
    hideInSearch: true,
  },
  {
    title: '操作',
    valueType: 'option',
    key: 'option',
    align: 'center',
    // 在操作列中替换 Popconfirm
    render: (text, record, _, action) => [
      // 禁用/启用按钮
      <Button
        key="toggleStatus"
        type={record.userStatus === 0 ? 'primary' : 'primary'}
        danger={record.userStatus === 0}
        shape="round"
        disabled = {record.isAdmin == 1}
        onClick={() => {
          if (window.confirm('确定执行此操作？')) {
            const newStatus = record.userStatus === 0 ? 1 : 0;
            request('/api/user/updateUserStatus', {
              method: 'POST',
              data: { userId: record.userId, userStatus: newStatus },
            }).then(res => {
              if (res.code === 200) {
                message.success('操作成功');
                action?.reload();
              }
            });
          }
        }}
      >
        {record.userStatus === 0 ? '禁用' : '启用'}
      </Button>,

      // 删除按钮
      <Button
        key="delete"
        type="primary"
        danger
        shape="round"
        disabled = {record.isAdmin == 1}
        onClick={() => {
          if (window.confirm('确定删除该用户？')) {
            request(`/api/user/deleteUser/${record.userId}`, {
              method: 'POST',
            }).then(res => {
              if (res.code === 200) {
                message.success('删除成功');
                action?.reload();
              }
            });
          }
        }}
      >
        删除
      </Button>,
    ],
  },
];

export default () => {
  const actionRef = useRef<ActionType>();
  return (
    <ProTable<API.UserInformation>
      columns={columns}
      actionRef={actionRef}
      rowClassName={() => 'custom-row-height'}
      cardBordered
      request={async (params, sort, filter) => {
        console.log('请求参数:', params, sort, filter);

        // 发送 POST 请求
        const response = await request('/api/user/searchUsers', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json', // 设置请求头为 JSON
          },
          data:{ ...params, sort, filter }, // 使用 data 字段传递请求体
        });

        // 转换后端响应为 ProTable 兼容格式
        return {
          data: response.data.records, // 映射 records 到 data
          total: response.data.total,  // 映射 total
          success: response.code === 200 // 可选，根据业务逻辑调整
        };
      }}
      editable={{
        type: 'multiple',
      }}
      columnsState={{
        persistenceKey: 'pro-table-singe-demos',
        persistenceType: 'localStorage',
        defaultValue: {
          option: { fixed: 'right', disable: true },
        },
        onChange(value) {
          console.log('value: ', value);
        },
      }}
      rowKey="id"
      search={{
        labelWidth: 'auto',
      }}
      options={{
        setting: {
          // @ts-ignore
          listsHeight: 400,
        },
      }}
      form={{
        // 由于配置了 transform，提交的参数与定义的不同这里需要转化一下
        syncToUrl: (values, type) => {
          if (type === 'get') {
            return {
              ...values,
            };
          }
          return values;
        },
      }}
      pagination={{
        pageSize: 5,
        onChange: (page) => console.log(page),
      }}
      dateFormatter="string"

    />
  );
};
