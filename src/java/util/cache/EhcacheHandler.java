package util.cache;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterNode;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.cluster.ClusterTopologyListener;

import org.apache.log4j.Logger;

import pub.servlet.ConfigInit;
import util.CacheUtil;
import util.StringUtil;

/*
 * ehcache工具类
 */
public  class EhcacheHandler extends CacheUtil{
	/**
	 * logger
	 */
	public static final Logger LOG = Logger.getLogger("CacheUtil.class");
	
	/**
	 * 执行缓存相关操作线程池
	 */
	private static ExecutorService exec = Executors.newCachedThreadPool();
	
	/**
	 * ehcache交互端口
	 */
	public static final String EHCACHE_PORT = 
		ConfigInit.Config.getProperty("EHCACHE_RMI_PORT");
	
	/**
	 * ehcache交互IP地址
	 */
	public static final String RMI_RULS = 
		ConfigInit.Config.getProperty("EHCACHE_RMI_URLS");
	
	/**
	 * 请求TC缓存超时毫秒数
	 */
	public static final long TIME_OUTS = 
		StringUtil.toInt(ConfigInit.Config.getProperty("TC_TIMEOUTS", "60000"));
	
	/**
	 * manager
	 */
	private static CacheManager manager = setDefaultManager();
	
	
	/**
	 * 本地缓存标识
	 */
	public static boolean is_local_cache = 
		ConfigInit.Config.getProperty(
				"SYSTEM_EHCACHE_ON", "1").equals("1");
	
	/**
	 * cache名称
	 */
	private String cacheName;
	
	/**
	 * 根据name从manager中取cache
	 * @return Cache
	 */
	private Cache getCache(){
		return manager.getCache(cacheName);
	}

	/**
	 * setter
	 * @param cacheName
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	/**
	 * 外部接口，添加缓存至ehcache0
	 * @return CacheUtil
	 */
	
	public static CacheUtil getHandler(){
		return getInstance("ehcache0");//与ehcache.xml配置的缓存名称一致
	}
	
	/**
	 * 单独配置一个缓存块，在ehcache.xml里需要配置
	 */
	public static CacheUtil getHandler(String cacheName){
		return new EhcacheHandler(cacheName);
	}
	
	private EhcacheHandler(final String cacheName){
		
		Callable call = new Callable(){
			
			public Object call(){
				
				setCacheName(cacheName);
				
				Cache cache = manager.getCache(cacheName);
				
				if(cache == null){
					cache = new Cache(cacheName, 1000, false, false, 0, 0);
					manager.addCache(cache);
				}
				
				return null;
			}
			
		};
		
		execOperator(call);
	}
	
	public void put(final String key, final Object value){
		Callable call = new Callable(){
			
			public Object call(){
				getCache().put(new Element(key, value));
				return null;
			}
			
		};
		
		execOperator(call);
	}
	
	/**
	 * 线程内加入集合的值到cache
	 * @param map 需加入cache的键值对，key为String类型
	 */
	public void putAll(final Map<String, ?> map) {
		if (!StringUtil.checkObj(map)) {
			return;
		}
		
		Callable call = new Callable(){

			public Object call() {
				Cache curr = getCache();
				
				for (Object obj : map.entrySet()) {
					Entry entry = (Entry) obj; 
					curr.put(new Element(entry.getKey(), entry.getValue()));
				}
				
				return null;
			}
			
		};
		
		execOperator(call);
	}

	/**
	 * get
	 * @return Object
	 */
	public Object get(final String key){
		Callable call = new Callable(){
			
			public Object call(){
				if(!getCache().isKeyInCache(key))
					return null;
				return	getCache().get(key).getValue();
			}
			
		};
		
		return execOperator(call);
	}
	
	/**
	 * 线程内获取缓存中多个值
	 * @param keys 缓存的key，多个以逗号分隔
	 * @return Map键值对
	 */
	public Map getAll(final String keys) {
		if (!StringUtil.checkStr(keys)) {
			return null;
		}
		
		Callable call = new Callable(){

			public Object call() {
				Map result = new HashMap();
				
				Cache curr = getCache();
				String[] arr = keys.split(",");
				
				for (int i = 0; i < arr.length; i++) {
					String key = arr[i];
					if (curr.isKeyInCache(key)) {
						result.put(key, curr.get(key).getValue());
					}
				}
				
				return result;
			}
			
		};
		
		return (Map) execOperator(call);
	}
	
	/**
	 * @see util.CacheUtil#getAll(java.util.Collection)
	 */
	@Override
	public Map<String, Object> getAll(final Collection<String> keys) {
		Callable call = new Callable(){

			public Object call() {
				Map result = new HashMap();
				
				Cache curr = getCache();
				for (String key : keys) {
					if (curr.isKeyInCache(key)) {
						result.put(key, curr.get(key).getValue());
					}
				}
				return result;
			}
			
		};
		
		return (Map) execOperator(call);
	}
	
	/**
	 * containsKey
	 * @return boolean
	 */
	public boolean containsKey(final String key){
		Callable call = new Callable(){
			public Object call(){
				return getCache().isKeyInCache(key);
			}
		};
		
		return (Boolean)execOperator(call);
		
	}
	
	/**
	 * remove
	 */
	public void remove(final String key){
		Callable call = new Callable(){
			public Object call(){
				if(getCache().isKeyInCache(key)){
					getCache().remove(key);
				}
				return null;
			}
		};
		
		execOperator(call);
	}
	
	/**
	 * removeAll
	 */
	public void removeAll(){
		Callable call = new Callable(){
			public Object call(){
				getCache().removeAll();
				return null;
			}
		};
		
		execOperator(call);
	}
	
	/**
	 * flush
	 *
	 */
	public void flush(){
		Callable call = new Callable(){
			public Object call(){
				getCache().flush();
				return null;
			}
		};
		
		execOperator(call);
	}
	
	/**
	 * close
	 *
	 */
	public void close(){
		Callable call = new Callable(){
			public Object call(){
				manager.shutdown();
				return null;
			}
		};
		
		execOperator(call);
		
	}
	
	/**
	 * getKeys
	 * @return List
	 */
	public Set<String> getKeys(){
		Callable call = new Callable(){
			public Object call(){
				return getCache().getKeys();
			}
		};
		
		return new HashSet<String>((List) execOperator(call));
	}
	
	public void removeKeyLike(String keyLike){
//		for (String key : cache.keySet()) {
//			System.out.println("key "+key);
//			if(StringUtil.checkStr(key) && key.contains(keyLike)){
//			  cache.remove(key);
//			}
//		}
		for (Iterator<String> keyIterator = getKeys().iterator();
				keyIterator.hasNext();) {
			
			String key = keyIterator.next();
			
			//使用Iterator的remove()方法
			if(StringUtil.checkStr(key) && key.indexOf(keyLike) >= 0){
				getCache().remove(key);
			}
		}
	}
	
	/*
	public void flush(){
		
	}
	
	public void close(){
		
	}*/
	/**
	 * getCacheNames
	 * @return String[]
	 */
	public Set<String> getCacheNames(){
		Callable call = new Callable(){
			public Object call(){
				return manager.getCacheNames();
			}
		};
		
		return new HashSet<String>(Arrays.asList((String[]) execOperator(call)));
	}

	/********************************************************************************
	 * 缓存启动和切换用到的方法，根据系统配置设置默认的CacheManager，
	 * SYSTEM_EHCACHE_ON=1表示开发环境单独使用ehcache，其他值为使用TC的集群缓存策略
	 * 罗德成 2012-9-25
	 */
    public static CacheManager setDefaultManager(){
    	CacheManager initManager = null;
    	//1:表示开发环境单独使用ehcache，其他值为使用TC的集群缓存策略
		if(ConfigInit.Config.getProperty("SYSTEM_EHCACHE_ON", "1").equals("1")){
			LOG.info("********************设置缓存容器CacheManager,开发环境单独使用ehcache*************************");
			initManager = CacheManager.create();
		}else{
			LOG.info("********************设置缓存容器CacheManager,使用TC的集群缓存策略*************************");
			initManager = getTcManager();
		}
    	return initManager;
    }	
    
    /**
     * 加载TC缓存
     * @return CacheManager for TC
     */
    private static CacheManager getTcManager(){
    	CacheManager tc = new CacheManager(
				CacheUtil.class.getResourceAsStream("/ehcache-tc.xml"));
		CacheCluster cluster = tc.getCluster(ClusterScheme.TERRACOTTA);
		
		cluster.addTopologyListener(new ClusterTopologyListener() {
			
			public void clusterOffline(ClusterNode arg0) {
			}
			
			public void clusterOnline(ClusterNode node) {
				if (is_local_cache) {
					setCacheManagerTC();
				}
			}
			
			public void nodeJoined(ClusterNode clus) {
			}
			
			public void nodeLeft(ClusterNode clus) {
			}
			
		});
		
		return tc;
    }
	
	/**
	 * 切换到单独使用ehcache的开发模式
	 */
    public static void setCacheManager(){
		if (manager != null) {
			manager.shutdown();
		}
		manager = CacheManager.create();//默认读取/ehcache.xml
		ConfigInit.initCacheData();
    }
    
    /**
	 * 切换到使用本地ehcache的模式
	 * 
	 */
    public static void setCacheManagerLocal(){
    	LOG.info("*************切换至本地缓存************");
    	
    	String path = Thread.currentThread().getContextClassLoader()
				.getResource("/ehcache-local.xml").getFile();
		String inputStr = getEhcacheStr(path.replaceAll("%20", " "));
		ByteArrayInputStream in = new ByteArrayInputStream(inputStr.getBytes());
		
		manager = CacheManager.create(in);
		
		is_local_cache = true;
		
		ConfigInit.initCacheData();
    }
    
    /**
     * 切换到使用TC集群的生产模式
     * 
     */
    public static void setCacheManagerTC(){
    	if (!is_local_cache) {
    		return;
    	}
    	
    	LOG.info("*************切换至TC缓存************");
    	
		if (manager != null) {
			manager.shutdown();
		}
		manager = getTcManager();
		
		is_local_cache = false;
		
		ConfigInit.initCacheData();
    }
    
    
    /**
	 * 调用TC缓存操作超时则切换至本地缓存
	 * @param call
	 * @return Object
	 */
	private static Object execOperator(Callable call) {
		Object obj = null;
		
		if (is_local_cache) {
			try {
				obj = call.call();
			} catch (Exception e) {
				LOG.error(" ***call exception*** ", e);
			}
		} else {
			try {
				Future future = exec.submit(call);
				obj = future.get(TIME_OUTS, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				//切换至本地缓存
				setCacheManagerLocal();
				
				return execOperator(call);
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			}
		}
		
		return obj;
	}
	
	/**
	 * 转换ehcache-local.xml中相关配置
	 * @param fileName 
	 * @return ehcache-local.xml
	 */
	private static String getEhcacheStr(String fileName){
    	
    	StringBuffer sb = new StringBuffer();
    	BufferedReader re;
    	
		try {
			re = new BufferedReader(new FileReader(fileName));
			String rmi = getEhcacheRmiUrls();
			String line = "";
		
			while((line = re.readLine()) != null){
				if (line.contains("port")) {
					line = line.replace("{T}", EHCACHE_PORT);
				} else if (line.contains("rmiUrls")) {
					line = line.replace("{T}", rmi);
				} else if (line.contains("socketTimeoutMillis")) {
					line = line.replace("{T}", String.valueOf(TIME_OUTS));
				}
				sb.append(line).append("\n");
			}
		} catch (FileNotFoundException e) {
			LOG.error("**********file:" + fileName + "not found**********", e);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();

    }
    
	/**
	 * 构造ehcache相互通信的rmi url
	 * @return RmiUrls
	 */
    private static String getEhcacheRmiUrls(){
    	String ipAddr = "";
    	
    	try {
    		InetAddress add = InetAddress.getLocalHost();
    		ipAddr = add.getHostAddress();
		} catch (UnknownHostException e) {
			LOG.error("*****unknown host*****", e);
		}
		
    	StringBuffer buffer = new StringBuffer();
    	String[] urls = RMI_RULS.split(",");
    	
    	for (String str : urls) {
    		if(ipAddr.equals(str)){
    			continue;
    		}
    		if (buffer.length() > 0) {
    			buffer.append("|");
    		}
    		buffer.append("//").append(str).append(":").append(EHCACHE_PORT);
    		buffer.append("/ehcache0");
    		buffer.append("|//").append(str).append(":").append(EHCACHE_PORT);
    		buffer.append("/sessionCache");
    	}
    	
    	return buffer.toString();
    }

	/**
	 * @see util.CacheUtil#get(java.lang.String, boolean)
	 */
	@Override
	public Object get(String key, boolean useLocalCache) {
		return get(key);
	}

	/**
	 * @see util.CacheUtil#getAll(java.util.Collection, boolean)
	 */
	@Override
	public Map<String, Object> getAll(Collection<String> keys, boolean useLocalCache) {
		return getAll(keys);
	}

	/**
	 * @see util.CacheUtil#containsKey(java.lang.String, boolean)
	 */
	@Override
	public boolean containsKey(String key, boolean useLocalCache) {
		return containsKey(key);
	}

}
