package util.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

import org.apache.commons.collections.MapUtils;

import pub.servlet.ConfigInit;

/**
 * redis内存级二级缓存
 * @author zhangzhiqiang
 * @date 2016年1月16日 下午12:58:19
 */
public class RedisSecondLevelCacheHandler {
	/** 缓存名称 **/
	private static final String SECOND_LEVEL_CACHE_NAME = "SECOND_LEVEL_CACHE_FOR_REDIS";
	/** 二级缓存管理器 **/
	private static CacheManager cacheManager = null;
	/** 二级缓存实例 **/
	private final static RedisSecondLevelCacheHandler handler = new RedisSecondLevelCacheHandler();
	
	/**
	 * 获取二级缓存实例
	 */
	public static RedisSecondLevelCacheHandler getHandler() {
		return handler;
	}
	
	/**
	 * 初始化
	 */
	public void initCacheManager(){
		int idleTime = MapUtils.getIntValue(ConfigInit.Config, "redis_SecondLevelCacheIdleTime", 300);
		int liveTime = MapUtils.getIntValue(ConfigInit.Config, "redis_SecondLevelCacheLiveTime", 600);
		
		//定义默认缓存配置
		CacheConfiguration defaultCacheConfig = new CacheConfiguration();
		defaultCacheConfig.setOverflowToDisk(false);
		defaultCacheConfig.eternal(false);
		
		//定义二级缓存
		CacheConfiguration cacheConfig = new CacheConfiguration();
		cacheConfig.setName(SECOND_LEVEL_CACHE_NAME);
		cacheConfig.setMaxElementsInMemory(20);
		cacheConfig.setOverflowToDisk(false);
		cacheConfig.setOverflowToOffHeap(false);
		cacheConfig.eternal(false);
		cacheConfig.setTimeToIdleSeconds(idleTime);
		cacheConfig.setTimeToLiveSeconds(liveTime);
		
		//添加二级缓存
		Configuration config = new Configuration();
		config.addDefaultCache(defaultCacheConfig);
		config.addCache(cacheConfig);
		
		//创建缓存管理器
		cacheManager = CacheManager.create(config);
	}
	
	/**
	 * 获取缓存
	 * @return
	 */
	public Cache getCache(){
		return cacheManager.getCache(SECOND_LEVEL_CACHE_NAME);
	}
	
	/**
	 * put
	 * @param Key
	 * @param value
	 */
	public void put(String key,Object value){
		getCache().put(new Element(key, value));
	}
	
	/**
	 * get
	 * @param key
	 * @return
	 */
	public Object get(String key){
		Element e = getCache().get(key);
		if(e!=null)
			return e.getObjectValue();
		else
			return null;
	}
	
	/**
	 * contains
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key){
		return getCache().isKeyInCache(key);
	}
	
	/**
	 * removeKey
	 * @param key
	 * @return
	 */
	public boolean removeKey(String key){
		return getCache().remove(key);
	}
}
