import { ProForm, ProFormSelect, ProFormText } from '@ant-design/pro-components';
import { Avatar, Button, Descriptions, message, Upload } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { updateUserProfile, uploadAvatar } from '@/services/ant-design-pro/api';
import { useModel } from 'umi';
import moment from 'moment';
import { useEffect } from 'react';

const AccountSettings = () => {
  const { initialState, setInitialState } = useModel('@@initialState');
  const [form] = ProForm.useForm();
  const user = initialState?.userInformation || {};

  // 性别选项映射
  const genderOptions = [
    { label: '男', value: 0 },
    { label: '女', value: 1 },
  ];

  // 角色显示映射
  const roleMap = {
    0: '普通用户',
    1: '管理员',
  };

  const statusMap = {
    0: '正常',
    1: '封禁',
  };

  // 处理表单提交
  const handleSubmit = async (values: API.UserInformation) => {
    try {
      // 进行用户id赋值
      values.userId = user.userId;
      // 调用更新接口（示例）
      await updateUserProfile(values);

      // 更新全局状态
      setInitialState({
        ...initialState,
        userInformation: { ...user, ...values },
      });
      message.success('保存成功');
    } catch (error) {
      message.error('保存失败');
    }
  };

  // 初始化表单数据
  useEffect(() => {
    if (user) {
      form.setFieldsValue({
        userName: user.userName,
        sex: user.sex,
        userEmail: user.userEmail,
        userPhone: user.userPhone,
      });
    }
  }, [user, form]);

  // 修改Upload组件
  const handleAvatarUpload = async (file?: any) => {
    console.log(file);

    const formData = new FormData();
    formData.append('file', file);

    try {
      /*const response = await fetch('/api/user/uploadAvatar', {
        method: 'POST',
        body: formData,
      });*/

      const response = await uploadAvatar(formData);

      // 更新用户信息
      const updatedUser = { ...user, imageUrl: response.data };

      // 更新全局状态
      setInitialState({
        ...initialState,
        userInformation: updatedUser,
      });

      if (response.code == 200) {
        message.success('头像更新成功');
      }
      return true;
    } catch (error) {
      console.log(error);
      return false;
    }
  };

  return (
    <PageContainer title="个人设置" ghost={false}>
      {/* 头像区域 */}
      <div style={{textAlign: 'center', marginBottom: 40}}>
        <Avatar
          size={120}
          src={user.imageUrl || 'https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=defaultAvatar.png&version_id=null'}
          alt="用户头像"
          style={{marginBottom: 16}}
        />
        <Upload
          showUploadList={false}
          beforeUpload={() => false} // 阻止自动上传
          onChange={(info) => handleAvatarUpload(info.file)}
        >
          <Button type="primary">更换头像</Button>
        </Upload>
      </div>

      {/* 基础信息展示 */}
      <Descriptions
        title="基础信息"
        bordered
        column={2}
        style={{marginBottom: 40}}
      >
        <Descriptions.Item label="账户">{user.userCount}</Descriptions.Item>
        <Descriptions.Item label="角色">{user.isAdmin !== undefined ? roleMap[user.isAdmin] : ''}</Descriptions.Item>
        <Descriptions.Item label="状态">{user.userStatus !== undefined ? statusMap[user.userStatus] : ''}</Descriptions.Item>
        <Descriptions.Item label="创建时间">
          {user.createTime ? moment(user.createTime).format('YYYY-MM-DD') : '-'}
        </Descriptions.Item>
      </Descriptions>

      {/* 可编辑表单 */}
      <ProForm
        form={form}
        onFinish={handleSubmit}
        style={{maxWidth: 768, margin: '0 auto'}}
      >
        <ProFormText
          name="userName"
          label="用户名"
          rules={[{required: true, message: '请输入用户名'}]}
          fieldProps={{
            style: {width: '100%'},
          }}
        />

        <ProFormSelect
          name="sex"
          label="性别"
          options={genderOptions}
          rules={[{required: true, message: '请选择性别'}]}
          fieldProps={{
            style: {width: '100%'},
          }}
        />

        <ProFormText
          name="userEmail"
          label="邮箱"
          rules={[
            {type: 'email', message: '请输入有效的邮箱地址'},
            {max: 50, message: '不超过50个字符'},
          ]}
          fieldProps={{style: {width: '100%'}}}
        />

        <ProFormText
          name="userPhone"
          label="手机号"
          rules={[
            {pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号'},
            {max: 11, message: '手机号为11位数字'},
          ]}
          fieldProps={{
            style: {width: '100%'},
            maxLength: 11,
          }}
        />
      </ProForm>
    </PageContainer>
  );
};

export default AccountSettings;
