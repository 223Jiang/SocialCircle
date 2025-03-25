import { Settings as LayoutSettings } from '@ant-design/pro-components';

const Settings: LayoutSettings & {
  pwa?: boolean;
  logo?: string;
} = {
  navTheme: 'light',
  // 拂晓蓝
  primaryColor: '#1890ff',
  layout: 'mix',
  contentWidth: 'Fluid',
  fixedHeader: false,
  fixSiderbar: true,
  colorWeak: false,
  title: 'Smart User Platform',
  pwa: false,
  logo: 'https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=logo.png&version_id=null',
  iconfontUrl: '',
};

export default Settings;
