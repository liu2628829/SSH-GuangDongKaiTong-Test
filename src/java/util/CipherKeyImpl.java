package util;

public class CipherKeyImpl implements CipherKey{

    public byte[] getCipherKey() {
        try {
        	//此key可根据项目内的方式进行获取，如电信要求从密钥服务器获取
        	String key = "cattsoft";
            return key.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
