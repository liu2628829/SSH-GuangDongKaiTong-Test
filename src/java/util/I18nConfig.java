package util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * ȡ�����쳣��Ϣ�����ļ����ã�δ�������ù��ʻ�����
 * @author gaotao
 */
public class I18nConfig {
	
	/** �����ļ����� */
	private static  Properties props = null ;
	
	/** �������� */
	private static I18nConfig i18n = null;
	
	/**
	 * Ĭ������ģʽ����
	 */
	public I18nConfig(){
		loadConfig("2");
	}
	
	/**
	 * ϵͳģʽ�����ⲿ����
	 * @param systemRunMode ϵͳ��ǰģʽ��1:����ģʽ;��������ģʽ
	 */
	public I18nConfig(String systemRunMode){
		loadConfig(systemRunMode);
	}
	
	/**
	 * ȡ�������󣬿���ģʽ�£������¼����쳣�����ļ�
	 * @param systemRunMode ϵͳ��ǰģʽ��1:����ģʽ;��������ģʽ
	 * */
	public static I18nConfig getInstance(String systemRunMode){
		if(i18n == null){ 
			i18n = new I18nConfig();
		}
		if("1".equals(systemRunMode)){
			i18n.loadConfig(systemRunMode);
		}
		return i18n;
	}
	
	/**
	 * ���������ļ�,����ģʽ��ÿ���ؼ����ļ�����������ģʽ��ֻ�е�һ��ʱ�ż����ļ�
	 * @param systemRunMode ϵͳ��ǰģʽ��1:����ģʽ;��������ģʽ
	 */
	private void loadConfig(String systemRunMode) {
		if(props == null || "1".equals(systemRunMode)){ 
			try {
				props = new Properties();
				props.load(this.getClass().getResourceAsStream("/i18n_zh_CN.properties"));
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��ȡ���õ��쳣����
	 * @param key i18nKey
	 * @param args ռλ���滻ֵ
	 * @param defaultDesc Ĭ������
	 * @return �����ı�
	 */
	public String getValue(String key, String args[], String defaultDesc){
		String value = StringUtil.checkStr(key) ? props.getProperty(key) : null;
		if(StringUtil.checkStr(value)){
			value = tranEncod(value);
			value = megreValue(value, args);
		}
		value = StringUtil.checkStr(value) ? value : defaultDesc;
		value = StringUtil.checkStr(value) ? value : tranEncod(props.getProperty("default.error"));
		return value;
	}
	
	/**
	 * ��ȡ���õ��쳣����
	 * @param key i18nKey
	 * @param args ռλ���滻ֵ
	 * @return �����ı�
	 */
	public String getValue(String key, String[] args) {
		String value;
		if(key == null || (value = props.getProperty(key)) == null){
			return tranEncod(props.getProperty("default.error"));
		}
		value = tranEncod(value);
		return megreValue(value, args);
	}
	
	/**
	 * �滻�ַ����е�ռλ��:{?}��?�Ŵ�0��ʼ������
	 * @param value ���滻���ı�
	 * @param args �滻ֵ�������е�ֵ��?��һһ��Ӧ
	 * @return �滻��Ľ��
	 */
	private String megreValue(String value, String[] args) {
		if(args != null) {
			for(int i = 0; i < args.length; i++) {
				value = value.replace("{" + i + "}", args[i]);
			}
		}
		return value;
	}
	
	/**
	 * ��������
	 * @param value Ҫ�紦�����ֵ
	 * @return ������Ľ��
	 */
	private String tranEncod(String value) {
		try {
			return new String(value.getBytes("ISO-8859-1"), "GBK").toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public static void main(String[] args) {
		System.out.println(new I18nConfig().getValue("catt.test.error", new String[]{"11", "22"}));
	}
}
