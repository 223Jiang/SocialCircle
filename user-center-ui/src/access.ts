/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { userInformation?: API.UserInformation } | undefined) {
  const { userInformation } = initialState ?? {};
  return {
    canAdmin: userInformation && userInformation.isAdmin == 1,
  };
}
