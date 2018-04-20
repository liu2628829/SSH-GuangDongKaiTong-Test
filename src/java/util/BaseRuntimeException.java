package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * ϵͳ�Զ����쳣
 * @author gaotao
 * @date 2014/05/27
 */
public class BaseRuntimeException extends RuntimeException {
	/**�Զ����쳣key��ʶ*/
	public static final String MSG = "exceptionMessage";
	
	/**i18n key ��ʶ*/
	public static final String I18N = "i18n";
	 
	/**ԭʼ�쳣����*/
	private Throwable cause;
	
	/**
	 * �Զ����쳣��Ϣ������������췽��˵����
	 * key-value�Զ��壬ϵͳ����key��5��[sPath,sDataSource,sSql,exceptionMessage,i18n]
	 * */
	private Map<String, String> mArgs;
	
	/**
	 * i18nKey��ͨ���˼���conf/i18n_zh_CN.propertiesȡ�쳣���������ص�ҳ�档
	 * ��mArgs���д�i18n keyʱ����mArgs���Ϊ׼��
	 * */
	@Deprecated
	private String i18nKey;
	
	/**���ʻ������ļ��Զ��������У�ռλ����Ӧ������ֵ��*/
	private String[] sArgs;
	
	/**
	 * �˹����Ѿ����ڣ���Ϊ���ݾ���Ŀ��������������ʹ��
	 * */
	@Deprecated
	public BaseRuntimeException() {
		super();
	}
	
	/**
	 * �˹����Ѿ����ڣ���Ϊ���ݾ���Ŀ��������������ʹ��
	 * @param mKey i18n key
	 * */
	@Deprecated
	public BaseRuntimeException(String mKey) {
		this(null, null, mKey, null);
	}
	
	/**
	 * �˹����Ѿ����ڣ���Ϊ���ݾ���Ŀ��������������ʹ��
	 * @param cause ԭʼ�쳣����
	 * */
	@Deprecated
	public BaseRuntimeException(Throwable cause) {
		this(null, cause);
	}
	/**
	 * ʹ��i18n���õĴ�������
	 * @param cause ԭʼ�쳣����
	 * @param mKey i18n key
	 * @param args ���ʻ������ļ��Զ��������У�ռλ����Ӧ������ֵ
	 * */
	public BaseRuntimeException(Throwable cause, String mKey, String[] args ) {
		this(cause, null, mKey, args);
	}
	
	/**
	 * �˹����Ѿ����ڣ���Ϊ���ݾ���Ŀ��������������ʹ��
	 */
	@Deprecated
	public BaseRuntimeException(Throwable cause, Map mArgs, String mKey, String sArgs[]) {
		this(cause, mArgs);
		this.i18nKey = mKey;
		this.sArgs = sArgs;
	}
	
	/**
	 * ����ʾ���ͻ��˵��쳣��������i18n���ʻ��ļ�����ռλ�������ô˹��췽��
	 * @param cause ԭʼ�쳣����
	 * @param mArgs ˵����:2�ι��췽��
	 * @param sArgs ���ʻ������ļ��Զ��������У�ռλ����Ӧ������ֵ
	 */
	public BaseRuntimeException(Throwable cause, Map mArgs, String sArgs[]) {
		this(cause, mArgs);
		this.sArgs = sArgs;
	}
	
	/**
	 * ����ֻ��Щ���췽��
	 * @param cause ԭʼ�쳣����
	 * @param args �Զ����쳣��Ϣ��args��������keyֵ���ǳ����ʶ��ģ�
	 *   sPath:�쳣ʱ����·��ִ��·�������������ֵ����ӡ��־ʱ��������Զ���ȡ����������Զ����Ϊ׼����
	 *   sDataSource:�쳣����ʱ������Դ����������ݲ��������쳣���Ż���ֵ����
	 *   sSql:�쳣����ʱִ�е�SQL��䣨��������ݲ��������쳣���Ż���ֵ����
	 *   exceptionMessage:�Զ�����쳣�����������web���򣬴������ǿ�����ʾ���ͻ��˵ģ�����һ������ֵ��һ��Ҫ�û��ɶ���������
	 *   i18n:�����Ϊ�����ʻ�Ԥ������:���ʻ��ļ����key����src/conf/i18n_zh_CN.properties���"default.error"��
	 *        ����д�ֵ�����ҹ��ʻ��ļ���Ҳ����Ӧ�ķ����ı����ͻ����ҵ��ķ����ı���ʾ�����ˣ�������exceptionMessageΪ׼��
	 *        i18n,exceptionMessage������û�У�����i18n��"default.error"Ϊ׼��
	 *        ����i18n�߼���Exception.jsp���д���
	 *   ����:����������չ�����Զ����key-value������
	 *       ��չ�����������ջ����쳣��־��"�쳣����"�У�ȫ����ʾ�������쳣�����е������Ƿ������Ա�Ŵ�ġ�
	 */
	public BaseRuntimeException(Throwable cause, Map args) {
		this.keepTheSourceException(cause);
		this.merge(args);
	}

	/**
	 * �Զ����쳣�׳����ͻ����ܿ����Զ���Ĵ�����ʾ
	 * @param exceptionMessage �Զ�����쳣������Ҳ������i18n key�������web���򣬴������ǿ�����ʾ���ͻ��˵ģ�����һ������ֵ��һ��Ҫ�û��ɶ���������
	 * @param cause ԭʼ�쳣����
	 */
	public BaseRuntimeException(String exceptionMessage, Throwable cause){
		Map<String, String> mr = new HashMap<String, String>(); 
		mr.put(MSG, exceptionMessage);
		this.i18nKey = exceptionMessage;
		this.keepTheSourceException(cause);
		this.merge(mr);
	}
	
	/**
	 * ������ԭʼ�쳣����
	 * @param cause ��ǰ�쳣����
	 */
	private void keepTheSourceException(Throwable cause){
		//���cause�������һ��BaseRuntimeException������ץ�����ڲ���ԭʼ�쳣���� 
		if (cause != null && (cause instanceof BaseRuntimeException)) {
			BaseRuntimeException c = (BaseRuntimeException)cause;
			this.cause = c.cause;
			this.mArgs = c.mArgs;
		} else {//����ֱ�Ӽ�¼
			this.cause = cause;
			//�����ǰ�ڵ㲻�Ǹ��ڵ㣬��Ҫ�ݹ�ȡ������ڵ�
			while(this.cause != null && this.cause.getCause() != null){
				 this.cause = this.cause.getCause();
			}
		}
	}
	
	/**
	 * ���µ��Զ�����Ϣ�쳣������Ϣ������оɵĽ��кϲ�
	 * @param args �µ��Զ����쳣����
	 */
	private void merge(Map args){
		if (this.mArgs != null && args != null) {//��������Ϊnull���Ž��кϲ�
			this.mArgs.putAll(mArgs);
			Iterator<String> it = this.mArgs.keySet().iterator();
			for(; it.hasNext();){
				String key = it.next();
				if(args.containsKey(key)){
					this.mArgs.put(key, this.mArgs.get(key) + " | " + args.get(key)); //ƴ�������������ǽ��µĸ��Ǿɵ�
					args.remove(key); //ƴ��һ��ɾһ��
				}
			}
			this.mArgs.putAll(args);//ʣ����ڲ�ͬkeyֵ���Զ�����Ϣ��ֱ��ȫ���ϲ�
		} else if(this.mArgs == null && args != null){//��ǰΪnull������Ĳ�Ϊnull������ֱ�Ӹ��赱ǰ
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

	/**������쳣�����Թ��췽���Ƿ��ܴ����Զ����쳣������ԭʼ�쳣����*/
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
		System.out.println("ԭʼ�쳣����:" + b3.getCause().getClass().getName());
		System.out.println("ԭʼ�쳣����:" + b3.getCause().getMessage());
		System.out.println("�Զ����쳣�������飩:" + m4.size());
		System.out.println("��������:");
		for(Iterator<String> it = m4.keySet().iterator(); it.hasNext();){
			String key = it.next();
			System.out.println("   " + key + " : " + m4.get(key));
		}
	}
	
}
