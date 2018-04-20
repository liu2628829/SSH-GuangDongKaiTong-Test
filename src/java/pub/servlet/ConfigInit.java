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
 * 初始基础配置参数
 * 
 * @author gaotao 2011-01-15
 */
@SuppressWarnings("serial")
public class ConfigInit extends HttpServlet {
	/** properties */
	public static Properties Config = new Properties();
	/** 缓存所有action方法对应的关联类型*/
	public static Map<String, Long> actionRel = new HashMap<String, Long>();
	/** 缓存所有权限菜单值*/
	public static List<Map<String, String>> rightMenuList = 
		new ArrayList<Map<String, String>>();

	public void init(ServletConfig config) throws ServletException {
		
		//initCacheData();
	}

	/**系统启动缓存*/
	public static void initCacheData(){
		//初始时进行权限缓存对象或数据初始化
		
		//....可再加其它初始启动项
		loadConfig();
	}
	 
	/**
	 * 缓存action与模块关系数据
	 */
	public static void initActionRel() {
		actionRel = LogOperateUtil.initActionRel();
	}
	
	/**
	 * 缓存action与模块关系数据
	 */
	public static void initRightMenuList() {
		rightMenuList = LogOperateUtil.getRightMenuList();
	}
	
	/*
     * 直接获取config.properties配置项，不用每次都取Config
     */
    public static String getProperty(String key) {
    	return Config.getProperty(key);
    }
    
    public static void setProperty(String key,String value) {
    	Config.setProperty(key,value);
    }
    
    /*
     * 直接获取config.properties配置项，不用每次都取Config
     */
    public static String getProperty(String key, String defaultValue) {
    	return Config.getProperty(key, defaultValue);
    }
    
    /*******************************初始化缓存数据*****************************************/
	
	  /**
   * 切换缓存策略的时候，需要重新装载缓存数据（1、来自数据库枚举，2、安全平台中session的键，加载3、配置文件的的枚举数据）
   */
  public static void initCacheFromDB(){
//		//加载1、数据库的枚举数据
//		ComminEnumUtil.loadEnumData();
//		//加载2、配置文件的的枚举数据
//		SysConfig.loadConfig();
//		//加载3、安全平台中session的键
//		initCacheFromSession();
//		//重新加载公共翻译数据
//		PubTranslateDao.initCache();
	  
	  System.out.println("-----------------");
  }
  
	/**
	 * 3加载配置文件
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
				
				//add by gaotao 20160829 方便SSH3的一些公共代码兼容
				ConfigInit.setProperty(k, v);
			}
			//cacheMap.put(key, tempMap);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
  
  /**
   * 加载2、安全平台中session的键
   */
  public static void initCacheFromSession(){
  	CacheUtil cache = CacheUtil.getInstance();
		//加载2、安全平台中session的键
		cache.put("post", new HashMap<String, Map>());
		cache.put("role", new HashMap<String, Map>());
		cache.put("right", new HashMap<String, Map>());
		cache.put("allMenu", new ArrayList());
		cache.put("domain", new HashMap<String, Map>());
		cache.put("dept", new HashMap<String, Map>());
		cache.put("accountIP", new HashMap<String, Map>());// 帐号与IP绑定名单
		cache.put("rightIP", new HashMap<String, Map>());// 权限与IP绑定名单

      StringBuffer sql = new StringBuffer();
		// 缓存IP黑白名单信息
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