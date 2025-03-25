import { useModel } from 'umi';
import { Avatar, Descriptions } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';

const AccountCenter = () => {
  const { initialState } = useModel('@@initialState');
  const { userInformation } = initialState || {};

  return (
    <PageContainer title="个人中心">
      <div style={{ maxWidth: 800, margin: '0 auto' }}>
        {/* 头像区域 */}
        <div style={{ textAlign: 'center', marginBottom: 40 }}>
          <Avatar
            size={120}
            icon={<UserOutlined />}
            src={userInformation?.imageUrl || 'https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=defaultAvatar.png&version_id=null'}
            style={{ marginBottom: 16 }}
          />
        </div>

        {/* 基本信息 */}
        <Descriptions bordered column={1}>
          <Descriptions.Item label="账户">{userInformation?.userCount}</Descriptions.Item>
          <Descriptions.Item label="用户名">{userInformation?.userName}</Descriptions.Item>
          <Descriptions.Item label="性别">{userInformation?.sex == undefined ? '' : userInformation?.sex == 1 ? '女' : '男'}</Descriptions.Item>
          <Descriptions.Item label="角色">{userInformation?.isAdmin == undefined ? '' : userInformation?.isAdmin == 1 ? '管理员' : '普通用户'}</Descriptions.Item>
          <Descriptions.Item label="邮箱">{userInformation?.userEmail}</Descriptions.Item>
          <Descriptions.Item label="手机号">{userInformation?.userPhone}</Descriptions.Item>
          <Descriptions.Item label="注册时间">{userInformation?.createTime}</Descriptions.Item>
        </Descriptions>
      </div>
    </PageContainer>
  );
};

export default AccountCenter;
