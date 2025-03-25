package org.wei.usercenterweb.utile;

/**
 * user-center-web
 *
 * @author： 江维伟
 * @date： 2025/3/2
 * @version： V5.0.0
 */
public class AccountValidatorUtil {
    /**
     * 账号校验规则
     * 1. 长度在 6 到 20 个字符之间
     * 2. 只能包含字母和数字
     *
     * @param account 用户账号
     * @return 校验通过返回 true，否则返回 false
     */
    public static boolean validateAccount(String account) {
        if (account == null || account.length() < 6 || account.length() > 20) {
            return false;
        }
        return account.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * 密码校验规则
     * 1. 长度在 8 到 20 个字符之间
     * 2. 必须包含至少一个大写字母、一个小写字母和一个数字
     *
     * @param password 用户账号
     * @return 校验通过返回 true，否则返回 false
     */
    public static boolean validateAccountAndPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) {
            return false;
        }
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
    }
}
