package util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 取配置异常信息，此文件暂用，未来考虑用国际化处理
 * @author gaotao
 */
public class I18nConfig {
	
	/** 属性文件对象 */
	private static  Properties props = null ;
	
	/** 单例对象 */
	private static I18nConfig i18n = null;
	
	/**
	 * 默认生产模式构造
	 */
	public I18nConfig(){
		loadConfig("2");
	}
	
	/**
	 * 系统模式，由外部决定
	 * @param systemRunMode 系统当前模式。1:开发模式;否则生产模式
	 */
	public I18nConfig(String systemRunMode){
		loadConfig(systemRunMode);
	}
	
	/**
	 * 取单例对象，开发模式下，会重新加载异常配置文件
	 * @param systemRunMode 系统当前模式。1:开发模式;否则生产模式
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
	 * 加载配置文件,开发模式，每次重加载文件，否则生产模式，只有第一次时才加载文件
	 * @param systemRunMode 系统当前模式。1:开发模式;否则生产模式
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
	 * 获取配置的异常描述
	 * @param key i18nKey
	 * @param args 占位符替换值
	 * @param defaultDesc 默认描述
	 * @return 描述文本
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
	 * 获取配置的异常描述
	 * @param key i18nKey
	 * @param args 占位符替换值
	 * @return 描述文本
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
	 * 替换字符串中的占位符:{?}，?号从0开始的数字
	 * @param value 被替换的文本
	 * @param args 替换值，数组中的值与?号一一对应
	 * @return 替换后的结果
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
	 * 处理乱码
	 * @param value 要如处乱码的值
	 * @return 处理完的结果
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
