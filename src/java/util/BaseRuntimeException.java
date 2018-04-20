package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * 系统自定义异常
 * @author gaotao
 * @date 2014/05/27
 */
public class BaseRuntimeException extends RuntimeException {
	/**自定义异常key标识*/
	public static final String MSG = "exceptionMessage";
	
	/**i18n key 标识*/
	public static final String I18N = "i18n";
	 
	/**原始异常对象*/
	private Throwable cause;
	
	/**
	 * 自定义异常信息，作用请见构造方法说明。
	 * key-value自定义，系统已用key有5个[sPath,sDataSource,sSql,exceptionMessage,i18n]
	 * */
	private Map<String, String> mArgs;
	
	/**
	 * i18nKey，通过此键从conf/i18n_zh_CN.properties取异常描述，返回到页面。
	 * 当mArgs中有此i18n key时，以mArgs里的为准。
	 * */
	@Deprecated
	private String i18nKey;
	
	/**国际化配置文件自定义描述中，占位符对应的描述值。*/
	private String[] sArgs;
	
	/**
	 * 此构造已经过期，仅为兼容旧项目而保留，请勿再使用
	 * */
	@Deprecated
	public BaseRuntimeException() {
		super();
	}
	
	/**
	 * 此构造已经过期，仅为兼容旧项目而保留，请勿再使用
	 * @param mKey i18n key
	 * */
	@Deprecated
	public BaseRuntimeException(String mKey) {
		this(null, null, mKey, null);
	}
	
	/**
	 * 此构造已经过期，仅为兼容旧项目而保留，请勿再使用
	 * @param cause 原始异常对象
	 * */
	@Deprecated
	public BaseRuntimeException(Throwable cause) {
		this(null, cause);
	}
	/**
	 * 使用i18n配置的错误描述
	 * @param cause 原始异常对象
	 * @param mKey i18n key
	 * @param args 国际化配置文件自定义描述中，占位符对应的描述值
	 * */
	public BaseRuntimeException(Throwable cause, String mKey, String[] args ) {
		this(cause, null, mKey, args);
	}
	
	/**
	 * 此构造已经过期，仅为兼容旧项目而保留，请勿再使用
	 */
	@Deprecated
	public BaseRuntimeException(Throwable cause, Map mArgs, String mKey, String sArgs[]) {
		this(cause, mArgs);
		this.i18nKey = mKey;
		this.sArgs = sArgs;
	}
	
	/**
	 * 当提示到客户端的异常描述，在i18n国际化文件中有占位符，就用此构造方法
	 * @param cause 原始异常对象
	 * @param mArgs 说明见:2参构造方法
	 * @param sArgs 国际化配置文件自定义描述中，占位符对应的描述值
	 */
	public BaseRuntimeException(Throwable cause, Map mArgs, String sArgs[]) {
		this(cause, mArgs);
		this.sArgs = sArgs;
	}
	
	/**
	 * 建议只用些构造方法
	 * @param cause 原始异常对象
	 * @param args 自定义异常信息，args现有以下key值，是程序可识别的，
	 *   sPath:异常时程序路径执行路径（如果不给此值，打印日志时，程序会自动获取，否则就以自定义的为准）。
	 *   sDataSource:异常发生时的数据源（如果是数据操作层抛异常，才会有值）。
	 *   sSql:异常发生时执行的SQL语句（如果是数据操作层抛异常，才会有值）。
	 *   exceptionMessage:自定义的异常描述，如果是web程序，此描述是可以提示到客户端的，所以一旦给此值，一定要用户可懂的描述。
	 *   i18n:这个是为做国际化预留，即:国际化文件里的key，如src/conf/i18n_zh_CN.properties里的"default.error"。
	 *        如果有此值，并且国际化文件里也有相应的翻译文本，就会以找到的翻译文本提示到户端，否则以exceptionMessage为准。
	 *        i18n,exceptionMessage两个都没有，则以i18n的"default.error"为准。
	 *        以上i18n逻辑在Exception.jsp中有处理。
	 *   其它:可以无限扩展其它自定义的key-value描述。
	 *       扩展的描述，最终会在异常日志的"异常详情"中，全部显示出来，异常详情中的描述是方便程序员排错的。
	 */
	public BaseRuntimeException(Throwable cause, Map args) {
		this.keepTheSourceException(cause);
		this.merge(args);
	}

	/**
	 * 自定义异常抛出，客户端能看到自定义的错误提示
	 * @param exceptionMessage 自定义的异常描述，也可以是i18n key，如果是web程序，此描述是可以提示到客户端的，所以一旦给此值，一定要用户可懂的描述。
	 * @param cause 原始异常对象
	 */
	public BaseRuntimeException(String exceptionMessage, Throwable cause){
		Map<String, String> mr = new HashMap<String, String>(); 
		mr.put(MSG, exceptionMessage);
		this.i18nKey = exceptionMessage;
		this.keepTheSourceException(cause);
		this.merge(mr);
	}
	
	/**
	 * 保留最原始异常对象
	 * @param cause 当前异常对象
	 */
	private void keepTheSourceException(Throwable cause){
		//如果cause本身就是一个BaseRuntimeException对象，则抓出其内部的原始异常对象 
		if (cause != null && (cause instanceof BaseRuntimeException)) {
			BaseRuntimeException c = (BaseRuntimeException)cause;
			this.cause = c.cause;
			this.mArgs = c.mArgs;
		} else {//否则直接记录
			this.cause = cause;
			//如果当前节点不是根节点，需要递归取到最根节点
			while(this.cause != null && this.cause.getCause() != null){
				 this.cause = this.cause.getCause();
			}
		}
	}
	
	/**
	 * 把新的自定义信息异常描述信息与过程中旧的进行合并
	 * @param args 新的自定义异常描述
	 */
	private void merge(Map args){
		if (this.mArgs != null && args != null) {//两个都不为null，才进行合并
			this.mArgs.putAll(mArgs);
			Iterator<String> it = this.mArgs.keySet().iterator();
			for(; it.hasNext();){
				String key = it.next();
				if(args.containsKey(key)){
					this.mArgs.put(key, this.mArgs.get(key) + " | " + args.get(key)); //拼接起来，而不是将新的覆盖旧的
					args.remove(key); //拼完一个删一个
				}
			}
			this.mArgs.putAll(args);//剩余存在不同key值的自定义信息，直接全量合并
		} else if(this.mArgs == null && args != null){//当前为null，传入的不为null：传入直接赋予当前
			this.mArgs = args;
		}
	}
	
	public String[] getSArgs() {
		return sArgs;
	}

	public void setSArgs(String[] args) {
		sArgs = args;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public Map getMArgs() {
		return mArgs;
	}

	public void setMArgs(Map args) {
		mArgs = args;
	}

	public String getI18nKey() {
		return i18nKey;
	}

	public void setI18nKey(String key) {
		i18nKey = key;
	}

	/**多层抛异常，测试构造方法是否能传承自定义异常描述和原始异常对象*/
	 public static void main(String[] args) {
		Exception e  = new Exception("aaaaa");
		Map m1 = new HashMap<String, String>();
		m1.put("path", "a/b/c");
		m1.put("datasource", "developer");
		m1.put("SQL", "select * from tbosRight");
		m1.put("msg", "my message1");
		
		BaseRuntimeException b1 = new BaseRuntimeException(e, m1);
		
		Map m2 = new HashMap<String, String>();
		m2.put("path", "d/e/f/");
		m2.put("msg", "my message2");
		
		BaseRuntimeException b2 = new BaseRuntimeException(b1, m2);
		
		Map m3 = new HashMap<String, String>();
		m3.put("path", "h/i/j/k/");
		m3.put("msg", "my message3");
		
		BaseRuntimeException b3 = new BaseRuntimeException(b2, m3);
		
		Map m4 = b3.getMArgs();
		System.out.println("原始异常名称:" + b3.getCause().getClass().getName());
		System.out.println("原始异常描述:" + b3.getCause().getMessage());
		System.out.println("自定义异常描述（组）:" + m4.size());
		System.out.println("具体描述:");
		for(Iterator<String> it = m4.keySet().iterator(); it.hasNext();){
			String key = it.next();
			System.out.println("   " + key + " : " + m4.get(key));
		}
	}
	
}
