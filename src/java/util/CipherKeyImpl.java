package util;

public class CipherKeyImpl implements CipherKey{

    public byte[] getCipherKey() {
        try {
        	//��key�ɸ�����Ŀ�ڵķ�ʽ���л�ȡ�������Ҫ�����Կ��������ȡ
        	String key = "cattsoft";
            return key.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
