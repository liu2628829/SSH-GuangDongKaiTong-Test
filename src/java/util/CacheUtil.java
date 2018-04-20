package util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import pub.servlet.ConfigInit;
import util.cache.EhcacheHandler;
import util.cache.RedisCacheHandler;

/**
 * cache操作工具类
 */
public abstract class CacheUtil {

	/**
	 * 获取实例
	 * 
	 * @return CacheUtil
	 */
	public static CacheUtil getInstance() {
		String cacheType = ConfigInit.getProperty("cache_type","ehcache");
		if("ehcache".equals(cacheType)){
			return EhcacheHandler.getHandler();
		}else{
			return util.cache.RedisCacheHandler.getHandler(null);
		}
	}

	/**
	 * 获取实例
	 * 
	 * @return CacheUtil
	 */
	public static CacheUtil getInstance(String namespace) {
		String cacheType = ConfigInit.getProperty("cache_type","ehcache");
		if("ehcache".equals(cacheType)){
			return EhcacheHandler.getHandler(namespace);
		}else{
			return RedisCacheHandler.getHandler(namespace);
		}
		
	}

	/**
	 * put
	 */
	public abstract void put(String key, Object value);

	/**
	 * get
	 * 
	 * @return Object
	 */
	public abstract Object get(String key);
	
	/**
	 * get
	 * 
	 * @return Object
	 */
	public abstract Object get(String key,boolean useLocalCache);

	/**
	 * 异步加入集合的值到cache
	 * 
	 * @param map 需加入cache的键值对，key为String类型
	 */
	public abstract void putAll(final Map<String, ?> map);
	
	/**
	 * 获取缓存中多个值
	 * 
	 * @param keys 缓存的key，多个以逗号分隔
	 * @return Map键值对
	 */
	public abstract Map<String, Object> getAll(Collection<String> keys);
	
	/**
	 * 获取缓存中多个值
	 * 
	 * @param keys 缓存的key，多个以逗号分隔
	 * @return Map键值对
	 */
	public abstract Map<String, Object> getAll(Collection<String> keys,boolean useLocalCache);

	/**
	 * 是否包含key
	 * 
	 * @return boolean
	 */
	public abstract boolean containsKey(String key);
	
	/**
	 * 是否包含key
	 * 
	 * @return boolean
	 */
	public abstract boolean containsKey(String key,boolean useLocalCache);

	/**
	 * 移除key
	 */
	public abstract void remove(String key);

	/**
	 * 移除所有
	 */
	public abstract void removeAll();
	
	/**
	 * 移除like
	 */
	public abstract void removeKeyLike(String like);

	/**
	 * 获取所有key
	 * @return
	 */
	public abstract Set<String> getKeys();
	
	/**
	 * 获取所有缓存命名空间
	 * @return
	 */
	public abstract Set<String> getCacheNames();

	/**
	 * 清理缓存资源
	 */
	public abstract void close();

}
