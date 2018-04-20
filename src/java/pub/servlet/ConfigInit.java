package pub.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import pub.source.DatabaseUtil;
import pub.source.LogOperateUtil;
import util.CacheUtil;
import util.RequestUtil;
import util.StringUtil;

/**
 * ��ʼ�������ò���
 * 
 * @author gaotao 2011-01-15
 */
@SuppressWarnings("serial")
public class ConfigInit extends HttpServlet {
	/** properties */
	public static Properties Config = new Properties();
	/** ��������action������Ӧ�Ĺ�������*/
	public static Map<String, Long> actionRel = new HashMap<String, Long>();
	/** ��������Ȩ�޲˵�ֵ*/
	public static List<Map<String, String>> rightMenuList = 
		new ArrayList<Map<String, String>>();

	public void init(ServletConfig config) throws ServletException {
		
		//initCacheData();
	}

	/**ϵͳ��������*/
	public static void initCacheData(){
		//��ʼʱ����Ȩ�޻����������ݳ�ʼ��
		
		//....���ټ�������ʼ������
		loadConfig();
	}
	 
	/**
	 * ����action��ģ���ϵ����
	 */
	public static void initActionRel() {
		actionRel = LogOperateUtil.initActionRel();
	}
	
	/**
	 * ����action��ģ���ϵ����
	 */
	public static void initRightMenuList() {
		rightMenuList = LogOperateUtil.getRightMenuList();
	}
	
	/*
     * ֱ�ӻ�ȡconfig.properties���������ÿ�ζ�ȡConfig
     */
    public static String getProperty(String key) {
    	return Config.getProperty(key);
    }
    
    public static void setProperty(String key,String value) {
    	Config.setProperty(key,value);
    }
    
    /*
     * ֱ�ӻ�ȡconfig.properties���������ÿ�ζ�ȡConfig
     */
    public static String getProperty(String key, String defaultValue) {
    	return Config.getProperty(key, defaultValue);
    }
    
    /*******************************��ʼ����������*****************************************/
	
	  /**
   * �л�������Ե�ʱ����Ҫ����װ�ػ������ݣ�1���������ݿ�ö�٣�2����ȫƽ̨��session�ļ�������3�������ļ��ĵ�ö�����ݣ�
   */
  public static void initCacheFromDB(){
//		//����1�����ݿ��ö������
//		ComminEnumUtil.loadEnumData();
//		//����2�������ļ��ĵ�ö������
//		SysConfig.loadConfig();
//		//����3����ȫƽ̨��session�ļ�
//		initCacheFromSession();
//		//���¼��ع�����������
//		PubTranslateDao.initCache();
	  
	  System.out.println("-----------------");
  }
  
	/**
	 * 3���������ļ�
	 */
	public static void loadConfig() {
		
		try {
			/*CacheUtil cacheMap =  CacheUtil.getInstance();
			String key = "SysConfig";*/
			Map<String, String> tempMap = new HashMap<String, String>();
			StringBuffer sql = new StringBuffer();
			sql.append(" select t.S_ITEM_KEY,t.S_ITEM_VALUE ");
			sql.append(" from T_SYS_CONFIG_FILE_ITEM t left join T_SYS_CONFIG_FILE f ");
			sql.append(" on f.I_FILE_ID=t.I_FILE_ID ");
			sql.append(" where 1=1 AND t.I_DELETE_FLAG!=1 AND f.I_DELETE_FLAG!=1 AND t.I_ITEM_STATUS=1 ");
			sql.append(" and f.S_FILE_NAME in ('sysconfig.properties', 'zymm.properties') ");
			List list = DatabaseUtil.queryForList(sql.toString());
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Map<String,String> o = (Map<String,String>) iterator.next();
				String k = StringUtil.toString(o.get("S_ITEM_KEY"));
				String v = StringUtil.toString(o.get("S_ITEM_VALUE"));
				tempMap.put(k, v);
				
				//add by gaotao 20160829 ����SSH3��һЩ�����������
				ConfigInit.setProperty(k, v);
			}
			//cacheMap.put(key, tempMap);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
  
  /**
   * ����2����ȫƽ̨��session�ļ�
   */
  public static void initCacheFromSession(){
  	CacheUtil cache = CacheUtil.getInstance();
		//����2����ȫƽ̨��session�ļ�
		cache.put("post", new HashMap<String, Map>());
		cache.put("role", new HashMap<String, Map>());
		cache.put("right", new HashMap<String, Map>());
		cache.put("allMenu", new ArrayList());
		cache.put("domain", new HashMap<String, Map>());
		cache.put("dept", new HashMap<String, Map>());
		cache.put("accountIP", new HashMap<String, Map>());// �ʺ���IP������
		cache.put("rightIP", new HashMap<String, Map>());// Ȩ����IP������

      StringBuffer sql = new StringBuffer();
		// ����IP�ڰ�������Ϣ
		sql.append("select a.iIPId as \"iIPId\", a.iObjType as \"iObjType\", ")
		   .append("a.iObjId as \"iObjId\", b.sURL as \"sURL\", a.iListType as \"iListType\", ")
		   .append("a.sIP as \"sIP\", a.sRemark as \"sRemark\" ")
		   .append("from tbOsIPRight a left join tbOsRight b on a.iObjType = 2 and a.iObjId = b.iRight ");
		List<Map> list = DatabaseUtil.queryForList(sql.toString());
		Map<String, List<Map>> accountIP = (Map)cache.get("accountIP");
		Map<String, Map> rightIP = (Map)cache.get("rightIP");
		for (Map m : list) {
			if ("1".equals(m.get("iObjType"))) {
			    List l = accountIP.get(m.get("iObjId"));
			    if(!StringUtil.checkObj(l)){
			        l = new ArrayList();
			    }
			    l.add(m);
			    accountIP.put(String.valueOf(m.get("iObjId")), l);
			} else if ("2".equals(m.get("iObjType"))) {
				rightIP.put(String.valueOf(m.get("iObjId")), m);
			}
		}
  }
}