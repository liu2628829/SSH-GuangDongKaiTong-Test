package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import pub.servlet.ConfigInit;

/**
 * DES���ܹ���
 * ���ļ���Ҫ����Կ���ȱ���>=8λ�����Ǵ˳����ڶ���Կ������8λ���ȵĽ�ȡ������jdk DES�����㷨�ڲ�Ĭ��ֻȡ8λ
 * @author pengjiewen
 * @version Jul 9, 2013
 */
public class DESTool {
	
	/** ���� �����㷨,���� DES,DESede(����DES����),Blowfish	 */
	private static final String ALGORITHM = "DES"; 
	
	/** ����MAP */
	private static Map<String, String> map = new HashMap<String, String>();
	
	/** ͳһ����*/
	private static final String CHARCODE = "UTF-8";
	
    /** �������ܵ���Կ */
    private static String codeKey = "${&`~m.';@#}";
	
	static {
		map.put("0", "E");
		map.put("1", "6");
		map.put("2", "C");
		map.put("3", "9");
		map.put("4", "5");
		map.put("5", "O");
		map.put("6", "Y");
		map.put("7", "M");
		map.put("8", "G");
		map.put("9", "S");
		map.put("A", "A");
		map.put("B", "T");
		map.put("C", "3");
		map.put("D", "S");
		map.put("E", "V");
		map.put("F", "Q");
	}
	
	public static String getCodeKey(){
	    return codeKey;
	}
	
    /**
     * ������Կ��������
     * @param keybyte �ֽ�������Կ,Ҫ��UTF-8�����ʽ
     * @param src ����
     * @return ���ܺ������,����ʧ�ܷ���null
     */
    public static String decrypt(byte[] keybyte, String src) {
        String de = decryStrMode(keybyte, src);
        return de;
    }
    
    /**
     * ���������ļ������õ���Կ�ӿ��࣬�õ���Կ���н���
     * @param key ��Կ
     * @param src ����
     * @return ���ؽ��ܺ������,�������ʧ�ܷ���null
     */
    public static String decrypt(String key, String src) {
        return decrypt(getBytes(key), src);
    }
    
    /**
     * ���������ļ������õ���Կ�ӿ��࣬�õ���Կ���н���
     * @param src ����
     * @return ���ؽ��ܺ������,�������ʧ�ܷ���null
     */
    public static String decrypt(String src) {
        return decrypt(getCipherKey(), src);
    }
    
    /**
     * ָ��CipherKeyʵ�����ȡ��Կ����
     * @param encryptClass CipherKeyʵ������·��
     * @param src ����
     * @return ���ܺ������,����ʧ�ܷ���null
     */
    public static String decrypt(Class encryptClass, String src) {
        return decrypt(getCipherKey(encryptClass.getName()), src);
    }
	
    /**
     * ������Կ���ܻ�ȡ����
     * @param key ��Կ
     * @param src ��������
     * @return ����
     */
    public static String encrypt(String key, String src) {
		return encryptStrMode(getBytes(key), getBytes(src));
    }
    
    /**
     * ������Կ���ܻ�ȡ����
     * @param keybyte �ֽ�������Կ,Ҫ��UTF-8�����ʽ
     * @param src ��������
     * @return ����
     */
    public static String encrypt(byte[] keybyte, String src) {
        return encryptStrMode(keybyte, getBytes(src));
    }
    
    /**
     * ���������ļ������õ���Կ�ӿ��࣬�õ���Կ���м���
     * @param src ��������
     * @return ����
     */
    public static String encrypt(String src) {
        return encrypt(getCipherKey(), src);
    }
    
    /**
     * ָ��util.CipherKey�ӿڵ�ʵ�����ȡ��Կ����
     * @param encryptClass ��util.CipherKey�ӿڵ�ʵ������
     * @param src ��������
     * @return ����
     */
    public static String encrypt(Class encryptClass, String src) {
        return encrypt(getCipherKey(encryptClass.getName()), src);
    }
    
	/**
	 * ���ܷ���
	 * @param keybyte Ϊ������Կ
	 * @param src Ϊ�����ܵ����ݻ�����
	 * @return ���ܺ������
	 */
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
	    
		try {
	        //DES�㷨Ҫ����һ�������ε������Դ 
	        SecureRandom sr2 = new SecureRandom();
	        //��ԭʼ�ܳ����ݴ���DESKeySpec���� 
	        DESKeySpec dks = new DESKeySpec(keybyte);
	        //����һ���ܳ׹�����Ȼ��������DESKeySpecת����һ��SecretKey���� 
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
	        SecretKey securekey = keyFactory.generateSecret(dks);
	        //Cipher����ʵ����ɼ��ܲ��� 
	        Cipher cipher = Cipher.getInstance("DES/ECB/NOPADDING");
	        //���ܳ׳�ʼ��Cipher���� 
	        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr2);
	        //��ʽִ�м��ܲ��� 
	        return cipher.doFinal(src);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * ���ܷ���������Դ�ַ�����ҪΪ8�ı������˷��������Զ���ȫ�ַ������ȣ�
     * @param keybyte Ϊ������Կ
     * @param src Ϊ�����ܵ����ݻ�����
     * @return ���ܺ������
	 */
	private static String encryptStrMode(byte[] keybyte, byte[] src){
	    String s = "";
		int m = src.length / 8;
		int n = src.length % 8;
		if(m > 0){
			//����3�䲻���� new String(src,0,m*8,"utf-8").getBytes()���棬��Ϊ�õ���byte[]���Ȳ�һ����8�ı���������8�ı����������쳣
			int cnt = m * 8;
			byte[] bys = new byte[cnt]; //������8�ı���
			for(int i = 0; i<cnt; i++){
				bys[i]=src[i];
			}
			s = byte2hex(encryptMode(keybyte, bys));
		}
		if(n != 0){ //���������8�ı�������0
		    byte[] bys = new byte[8];
		    int i = 0;
		    for(; i < n; i++){
		        bys[i] = src[m * 8 + i];
		    }
		    while(i < 8){
		        bys[i++] = 0;
		    }
		    s = s + byte2hex(encryptMode(keybyte, bys));
		}
        return s;
	}
	
	/**
	 * ��ȡ��Կ(���������ļ���ȡ����ʵ�ַ���)
	 * ConfigInit�࣬�ӿں�̨����ĳ���Ӧ����û�У���ô�ӿں�̨���棬Ҫôע�͵��������뵱ǰ������صķ�����Ҫôʵ��һ��ConfigInit������ȡ�����ò�����Ҫô����˷���
	 * @return 2������Կ����
	*/ 
	private static byte[] getCipherKey(){
	    String encryptClass = ConfigInit.Config.getProperty("encryptClass",
	            "util.CipherKeyImpl");
	    return getCipherKey(encryptClass);
	}
	
	/**
	 * ����CipherKeyʵ��������ȡ��Կ
	 * @param encryptClass CipherKeyʵ����������·��
	 * @return 2������Կ����
	 */
	private static byte[] getCipherKey(String encryptClass){
	    CipherKey ck = null;
        try {
            ck = (CipherKey)Class.forName(encryptClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("ʵ������Կ��" + encryptClass + "ʧ�ܣ�,"
                    + "������������encryptClass�Ƿ���ȷ��");
        }
        return ck.getCipherKey();
	}
	
	/**
	 * ���ܷ���
	 * @param keybyte Ϊ������Կ
	 * @param src Ϊ���ܺ�Ļ�����
	 * @return ���ܺ������,����ʧ�ܷ���null
	 */
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
	    
	    try {
	       //DES�㷨Ҫ����һ�������ε������Դ 
            SecureRandom sr = new SecureRandom();
            //��ԭʼ�ܳ����ݴ���һ��DESKeySpec���� 
            DESKeySpec dks = new DESKeySpec(keybyte);
            //����һ���ܳ׹�����Ȼ��������DESKeySpec����ת����һ��SecretKey���� 
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey securekey = keyFactory.generateSecret(dks);
            //Cipher����ʵ����ɽ��ܲ��� 
            Cipher cipher = Cipher.getInstance("DES/ECB/NOPADDING");
            //���ܳ׳�ʼ��Cipher���� 
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
            //��ʽִ�н��ܲ��� 
            return cipher.doFinal(src);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ���ܷ�����ȥ������ʱΪ��ȫ����Ϊ8�ı������ӵ�0��
     * @param keybyte Ϊ������Կ
     * @param src Ϊ���ܺ�Ļ�����
     * @return ���ܺ������,����ʧ�ܷ���null
	 */
	private static String decryStrMode(byte[] keybyte, String src){
        try {
            byte[] b = decryptMode(keybyte , hex2byte(src));
            int i = 0;
            //ȥ������� byte[0]
            while(i < b.length && b[b.length - 1 - i] == 0){
                i++;
            }
            return new String(b, 0, b.length - i, CHARCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}

	
   /**
    * 2��������ת����16�����ַ���
    * @param b 2��������
    * @return 16�����ַ���
    */
	public static String byte2hex(byte[] b) {
	    // ת��16�����ַ���
	    String hs = "";
	    String stmp = "";
	    for (int n = 0; n < b.length; n++) {
	        // ����ת��ʮ�����Ʊ�ʾ
	        stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
	        if (stmp.length() == 1){
	            hs = hs + "0" + stmp;
	        }else{
	            hs = hs + stmp;
	        }
	    }
	    return hs.toUpperCase(); // ת�ɴ�д
	}
	
	/**
	 * 16�����ַ���ת����2��������
	 * @param hexStr 16�����ַ���
	 * @return 2��������
	 */
	public static byte[] hex2byte(String hexStr) throws Exception{
	    byte[] b = getBytes(hexStr);
	    if ((b.length % 2) != 0){
	        throw new IllegalArgumentException("���Ȳ���ż��");
	    }
	    byte[] b2 = new byte[b.length / 2];
	    for (int n = 0; n < b.length; n += 2) {
	        String item = new String(b, n, 2, CHARCODE);
	        // ��λһ�飬��ʾһ���ֽ�,��������ʾ��16�����ַ�������ԭ��һ�������ֽ�
	        b2[n / 2] = (byte) Integer.parseInt(item, 16);
	    }
	    return b2;
	}
	
	/**
	 * �ַ���תbyte[]
	 * @param str ��ת���ַ���
	 */
	public static byte[] getBytes(String str){
		try {
			byte[] keybyte = str.getBytes(CHARCODE); 
			return keybyte;
		} catch (UnsupportedEncodingException e) {
			/*String msg ="DESTool �ж��ַ���תbyte[]ʱ����";
			throw new BaseRuntimeException(e, msg);*/
			throw new RuntimeException(e);
		}
	}
	
	/**���·�����DES�޹أ���������ʷԭ���Ѿ�����������ã����Բ�����������Ĺ�����**/
	/**
	 * ����
	 * @param str �������
	 * @return �������
	 */
	public static String confusion(String str) {
		StringBuffer result = new StringBuffer();
		for (char chr : str.toCharArray()) {
			result.append(map.get(chr));
		}
		return result.toString();
	}
	
	/**
	 * ������
	 * @param str ���������
	 * @return ��������
	 */
	public static String unConfusion(String str) {
		Set<Entry<String, String>> set = map.entrySet();
		StringBuffer result = new StringBuffer();
		for (char chr : str.toCharArray()) {
			Iterator<Entry<String, String>> iter = set.iterator();
			while(iter.hasNext()) {
				Entry entry = iter.next();
				if (entry.getValue().equals(String.valueOf(chr))) {
					result.append(entry.getKey());
				}
			}
		}
		return result.toString();
	}
	
    /**
     * url���ܷ��������ܷ�����js�У������������Ǽ�������ʱ����null
     * @param param url���Ĳ���
     * @return ��������
     */
    public static String decryptURL(String param) {
        String out = "";
        String decrypt = null;
        try{
            int numIn;
            for (int i = 0; i < param.length(); i += 3) {
                String s = param.substring(i, i + 3);
                String n = "";
                for (int j = 0; j < s.length(); j++) {
                    if (Character.isDigit(s.charAt(j))) {
                        n += s.charAt(j);
                    }
                }
                numIn = Integer.parseInt(n) + 23;
                out += unescape("%" + Integer.toHexString(numIn));
            }
            decrypt = unescape(out);
        }catch(Exception e){
            return null;//�����޷����ܣ�Ϊ�Ǽ��ܲ���
        }
        if(decrypt.startsWith(getCodeKey())){//�Ƿ�����Կ
            return decrypt.substring(getCodeKey().length());
        }else{
            return null;//�����������ܣ���û����Կ��Ϊ�Ǽ��ܲ���
        }
    }

    /**
     * ģ��js��escape���ܷ���
     * @param �����ܵ��ַ���
     */
    private static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }
	
    /**
     * ����ת��map�ڲ���
     */
    public static void decryptForMap(Map<String, String> params){
        for(String key : params.keySet()){
            String value = ((String)(params.get(key)));
            String v = decryptURL(value);
            if(v != null){//Ϊ���ܲ���
                try{
                    value = URLDecoder.decode(v, "UTF-8");
                }catch(Exception e){
                }
            }else{
                continue;
            }
            params.put(key, value);
        }
    }
    
	/**
	 * ����
	 * @param args �ַ����������
	 */
	public static void main(String[] args)
    {
        String encrypt = DESTool.encrypt("cattsoft", "develop4");//����
        String decrypt = DESTool.decrypt("cattsoft", encrypt); //����
        System.out.println(encrypt + "====" + decrypt ); 
        
        String encrypt1 = DESTool.encrypt("cattsoft", "�㶫��ͨ�������");//����
        String decrypt1 = DESTool.decrypt("cattsoft", encrypt1); //����
        System.out.println(encrypt1 + "====" + decrypt1 ); 
    }
}
