package util;

/**
 * 密钥加载接口
 * @author pengjiewen
 * @version Jul 18, 2013
 */
public interface CipherKey {
    /**
     * 取密钥方法
     * @return 返回byte数组的密钥，要求UTF-8编码格式
     */
    public byte[] getCipherKey();
}
