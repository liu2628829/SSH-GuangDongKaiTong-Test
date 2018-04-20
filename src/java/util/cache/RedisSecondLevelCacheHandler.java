package util.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

import org.apache.commons.collections.MapUtils;

import pub.servlet.ConfigInit;

/**
 * redis�ڴ漶��������
 * @author zhangzhiqiang
 * @date 2016��1��16�� ����12:58:19
 */
public class RedisSecondLevelCacheHandler {
	/** �������� **/
	private static final String SECOND_LEVEL_CACHE_NAME = "SECOND_LEVEL_CACHE_FOR_REDIS";
	/** ������������� **/
	private static CacheManager cacheManager = null;
	/** ��������ʵ�� **/
	private final static RedisSecondLevelCacheHandler handler = new RedisSecondLevelCacheHandler();
	
	/**
	 * ��ȡ��������ʵ��
	 */
	public static RedisSecondLevelCacheHandler getHandler() {
		return handler;
	}
	
	/**
	 * ��ʼ��
	 */
	public void initCacheManager(){
		int idleTime = MapUtils.getIntValue(ConfigInit.Config, "redis_SecondLevelCacheIdleTime", 300);
		int liveTime = MapUtils.getIntValue(ConfigInit.Config, "redis_SecondLevelCacheLiveTime", 600);
		
		//����Ĭ�ϻ�������
		CacheConfiguration defaultCacheConfig = new CacheConfiguration();
		defaultCacheConfig.setOverflowToDisk(false);
		defaultCacheConfig.eternal(false);
		
		//�����������
		CacheConfiguration cacheConfig = new CacheConfiguration();
		cacheConfig.setName(SECOND_LEVEL_CACHE_NAME);
		cacheConfig.setMaxElementsInMemory(20);
		cacheConfig.setOverflowToDisk(false);
		cacheConfig.setOverflowToOffHeap(false);
		cacheConfig.eternal(false);
		cacheConfig.setTimeToIdleSeconds(idleTime);
		cacheConfig.setTimeToLiveSeconds(liveTime);
		
		//��Ӷ�������
		Configuration config = new Configuration();
		config.addDefaultCache(defaultCacheConfig);
		config.addCache(cacheConfig);
		
		//�������������
		cacheManager = CacheManager.create(config);
	}
	
	/**
	 * ��ȡ����
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
