package org.wei.usercenterweb.utile;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptEncryption {
    /**
     * 对密码进行加密
     * @param plainPassword 明文密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码是否匹配
     * @param plainPassword 明文密码
     * @param hashedPassword 加密后的密码
     * @return 如果匹配返回 true，否则返回 false
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}