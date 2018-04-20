package util;

import java.util.Iterator;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/*
 * ehcache工具类
 */
public  class EhCacheUtil {
	
	private static CacheManager manager = CacheManager.create();
	private Cache cache = null;	
	
	public static EhCacheUtil getInstance(){
		return new EhCacheUtil("ehcache0");
	}
	/*
	 * 单独配置一个缓存块，在ehcache.xml里需要配置
	 */
	public static EhCacheUtil getInstance(String cacheName){
		return new EhCacheUtil(cacheName);
	}
	
	private EhCacheUtil(String cacheName){
		cache = manager.getCache(cacheName);
		if(cache==null){
			cache=new Cache(cacheName, 1000, false, false, 0, 0);
			manager.addCache(cache);
		}
	}
	
	public  void put(String key, Object value){
		cache.put(new Element(key, value));
	}

	public  Object get(String key){
		Object resultVal  = null;
		Element ele = cache.get(key);
		if(StringUtil.checkObj(ele)){
			Object val = ele.getObjectValue();
			if(StringUtil.checkObj(val)){
				resultVal  =  val;
			}
		}
		return resultVal;
	}
	
	public boolean containsKey(final String key){
		return cache.isKeyInCache(key);
	}
	
	public void remove(String key){
		if(cache.isKeyInCache(key))
			cache.remove(key);
	}
	
	public void removeAll(){
		cache.removeAll();
	}
	
	public void removeKeyLike(String keyLike){
		Iterator<String> keyIterator = cache.getKeys().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			if(StringUtil.checkStr(key) && key.indexOf(keyLike) != -1){
				cache.remove(key);
			}
		}
	}	
	public static void main(String[] args) {
		try{
			/*EhCacheUtil instance = EhCacheUtil.getInstance();
//			instance.removeKeyLike("obj");
			TbFeedback fb = new TbFeedback();
			fb.setIFeedbackId(1l);
			fb.setSFeedBackContent("哈哈哈");
			instance.put("obj",fb);
			
			TbFeedback fb1 = (TbFeedback)instance.get("obj");
			System.out.println(fb1.getSFeedBackContent());*/
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
