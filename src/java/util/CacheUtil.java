package util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import pub.servlet.ConfigInit;
import util.cache.EhcacheHandler;
import util.cache.RedisCacheHandler;

/**
 * cache����������
 */
public abstract class CacheUtil {

	/**
	 * ��ȡʵ��
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
	 * ��ȡʵ��
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
	 * �첽���뼯�ϵ�ֵ��cache
	 * 
	 * @param map �����cache�ļ�ֵ�ԣ�keyΪString����
	 */
	public abstract void putAll(final Map<String, ?> map);
	
	/**
	 * ��ȡ�����ж��ֵ
	 * 
	 * @param keys �����key������Զ��ŷָ�
	 * @return Map��ֵ��
	 */
	public abstract Map<String, Object> getAll(Collection<String> keys);
	
	/**
	 * ��ȡ�����ж��ֵ
	 * 
	 * @param keys �����key������Զ��ŷָ�
	 * @return Map��ֵ��
	 */
	public abstract Map<String, Object> getAll(Collection<String> keys,boolean useLocalCache);

	/**
	 * �Ƿ����key
	 * 
	 * @return boolean
	 */
	public abstract boolean containsKey(String key);
	
	/**
	 * �Ƿ����key
	 * 
	 * @return boolean
	 */
	public abstract boolean containsKey(String key,boolean useLocalCache);

	/**
	 * �Ƴ�key
	 */
	public abstract void remove(String key);

	/**
	 * �Ƴ�����
	 */
	public abstract void removeAll();
	
	/**
	 * �Ƴ�like
	 */
	public abstract void removeKeyLike(String like);

	/**
	 * ��ȡ����key
	 * @return
	 */
	public abstract Set<String> getKeys();
	
	/**
	 * ��ȡ���л��������ռ�
	 * @return
	 */
	public abstract Set<String> getCacheNames();

	/**
	 * ��������Դ
	 */
	public abstract void close();

}
