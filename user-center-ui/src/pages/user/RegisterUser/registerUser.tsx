import Footer from '@/components/Footer';
import {register} from '@/services/ant-design-pro/api';
import {
  LockOutlined,
  UserOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormText,
} from '@ant-design/pro-components';
import { Alert, message, Tabs } from 'antd';
import React, { useState } from 'react';
import {history, SelectLang, useIntl} from 'umi';
import styles from './registerUser.less';

const loginPath = '/user/login';

const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => (
  <Alert
    style={{
      marginBottom: 24,
    }}
    message={content}
    type="error"
    showIcon
  />
);

const Login: React.FC = () => {
  const [userRegisterState, setuserRegisterState] = useState<API.ResponseResult<string>>({
    code: 200,
    data: undefined,
    details: "",
    msg: ""
  });
  const intl = useIntl();

  const handleSubmit = async (values: API.LoginParams) => {
    try {
      // 注册
      const msg = await register({ ...values });

      if (msg.code == 200) {
        const defaultLoginSuccessMessage = intl.formatMessage({
          id: 'pages.register.registerSuccess',
          defaultMessage: '注册成功！',
        });
        message.success(defaultLoginSuccessMessage);
        /** 此方法会跳转到 redirect 参数所在的位置 */
        if (!history) return;
        /*const { query } = history.location;
        const { redirect } = query as { redirect: string };
        history.push(redirect || '/');*/
        history.push(loginPath);
        return;
      }
      console.log(msg);
      setuserRegisterState(msg);
    } catch (error) {
      const defaultLoginFailureMessage = intl.formatMessage({
        defaultMessage: '注册成功，请重试！',
      });
      message.error(defaultLoginFailureMessage);
    }
  };
  const { code} = userRegisterState;

  return (
    <div className={styles.container}>
      <div className={styles.lang} data-lang>
        {SelectLang && <SelectLang />}
      </div>
      <div className={styles.content}>
        <LoginForm
          logo={<img alt="logo" src="https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=logo.png&version_id=null" />}
          title="Smart User Platform"
          subTitle={intl.formatMessage({ id: 'pages.layouts.userLayout.title' })}
          initialValues={{
            autoLogin: true,
          }}
          onFinish={async (values) => {
            await handleSubmit(values as API.LoginParams);
          }}
          submitter={{
            searchConfig: { submitText: '注册' }
          }}
        >
          <Tabs>
            <Tabs.TabPane
              key="account"
              tab={intl.formatMessage({
                id: 'pages.register.registerAccount',
                defaultMessage: '注册账户',
              })}
            />
          </Tabs>

          {code != 200 && (
            <LoginMessage
              content={intl.formatMessage({
                id: 'pages.register.registerError',
                defaultMessage: '注册失败！',
              })}
            />
          )}

          {(
            <>
              <ProFormText
                name="userCount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon} />,
                }}
                placeholder= "输入您的用户名"
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon} />,
                }}
                placeholder="输入您的密码"
              />
              <ProFormText.Password
                name="userCheckPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon} />,
                }}
                placeholder="再次输入您的密码"
              />
            </>
          )}

        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
