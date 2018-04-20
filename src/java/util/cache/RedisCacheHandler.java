package util.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import pub.servlet.ConfigInit;
import pub.source.LogOperateUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import util.CacheUtil;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * redis client handler
 * 
 * @author zhangzhiqiang
 * @date 2015年12月8日 下午6:34:09
 */
public class RedisCacheHandler extends CacheUtil {
	/** 类型前缀 **/
	private static final String PREFIX_CLASS_TYPE = "!!$#CLSTP#$!!";
	/** 命名空间前缀 **/
	private static final String SPLIT_NAME_SPACE = "!!$#NM-SP#$!!";
	/** 命名空间key set **/
	private static final String ALL_NAME_SPACE = "!!$#ALL_NAME_SPACE#$!!";
	/** 缓存Handler实例 **/
	private static final Map<String, RedisCacheHandler> CACHE_SPACE = new HashMap<String, RedisCacheHandler>();
	/** redis client类 **/
	private static JedisCluster jedisClusterClient;
	/** 命名空间前缀 **/
	private String namespacePrefix;
	/** 所有当前命名空间的key的集合的key **/
	private String allKeysSetKey;
	/** 是否默认使用二级缓存 **/
	private static boolean isDefualtUseSecondLevelCache = true;
	/** java提供商 **/
	private static final String JAVA_VENDOR = System.getProperty("java.vendor");
	/** IBM J9 vm **/
	private static final boolean IS_IBM_JAVA_VENDOR = JAVA_VENDOR.contains("IBM");
	/** 是否适用IBM jvm序列化方式 **/
	private boolean isIBMSerializeType = false;
	/**
	 * 获取Redis缓存实例
	 * 
	 * @return
	 */
	public final static RedisCacheHandler getHandler(String namespace) {
		String handlerKey = namespace;
		if (namespace == null) {
			handlerKey = "$#GLOBAL_NAME_SPACE#$";
		}

		RedisCacheHandler handler = CACHE_SPACE.get(handlerKey);
		if (handler == null) {
			synchronized (CACHE_SPACE) {
				handler = CACHE_SPACE.get(handlerKey);
				if (handler == null) {
					handler = new RedisCacheHandler(namespace);
					CACHE_SPACE.put(handlerKey, handler);
					jedisClusterClient.sadd(ALL_NAME_SPACE, handlerKey);
				}
			}
		}
		return handler;
	}

	/**
	 * constructor
	 */
	private RedisCacheHandler(String namespace) {
		if (jedisClusterClient == null) {
			synchronized (RedisCacheHandler.class) {
				if (jedisClusterClient == null) {
					init();
				}
			}
		}

		if (namespace == null || "".equals(namespace)) {
			this.namespacePrefix = null;
			this.allKeysSetKey = "!!$#GLOBAL_KEY_SET#$!!";
		} else {
			this.namespacePrefix = namespace + SPLIT_NAME_SPACE;
			this.allKeysSetKey = "!!$#ALL_KEYS_SET#$!!" + namespace;
		}

	}

	/**
	 * 初始化缓存环境
	 */
	private void init() {
		try {
			Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
			String nodes = ConfigInit.Config.getProperty("redis_Nodes");
			for (String node : nodes.split(",")) {
				String[] addr = node.split(":");
				jedisClusterNodes.add(new HostAndPort(addr[0], Integer.parseInt(addr[1])));
			}
			isIBMSerializeType  = MapUtils.getBooleanValue(ConfigInit.Config, "redis_UseIBMVendor",false);
			
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			//最大连接数, 默认8个
			config.setMaxTotal(MapUtils.getIntValue(ConfigInit.Config, "redis_MaxTotal", 100));
			//最大空闲连接数, 默认8个
			config.setMaxIdle(MapUtils.getIntValue(ConfigInit.Config, "redis_MaxIdle", 20));
			//最小空闲连接数, 默认0
			config.setMinIdle(MapUtils.getIntValue(ConfigInit.Config, "redis_MinIdle", 2));
			//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
			config.setMinEvictableIdleTimeMillis(MapUtils.getIntValue(ConfigInit.Config, "redis_MinEvictableIdleTimeMillis", 300000));
			//对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)   
			config.setSoftMinEvictableIdleTimeMillis(MapUtils.getIntValue(ConfigInit.Config, "redis_SoftMinEvictableIdleTimeMillis", 300000));
			//逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
			config.setTimeBetweenEvictionRunsMillis(MapUtils.getIntValue(ConfigInit.Config, "redis_TimeBetweenEvictionRunsMillis", 2000));
			//连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
			config.setBlockWhenExhausted(MapUtils.getBooleanValue(ConfigInit.Config, "redis_BlockWhenExhausted", false));
			//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常,
			//小于零:阻塞不确定的时间,  默认-1
			config.setMaxWaitMillis(MapUtils.getIntValue(ConfigInit.Config, "redis_MaxWaitMillis", 1000));

			config.setTestWhileIdle(true);
			config.setTestOnCreate(true);
			config.setTestOnReturn(true);
			config.setTestOnBorrow(true);

			//连接超时时间
			int connectTimeout = MapUtils.getIntValue(ConfigInit.Config, "redis_ConnectTimeout", 1000);
			//socket超时时间
			int socketTimeout = MapUtils.getIntValue(ConfigInit.Config, "redis_SocketTimeout", 2000);

			jedisClusterClient = new JedisCluster(jedisClusterNodes, connectTimeout, socketTimeout, config);
		} catch (Exception e) {
			logError(e, "！！！严重错误:redis缓存初始化失败！");
		}

		try {
			isDefualtUseSecondLevelCache = MapUtils.getIntValue(ConfigInit.Config, "redis_DefualtUseSecondLevelCache", 1) != 0;
			if (isDefualtUseSecondLevelCache) {
				RedisSecondLevelCacheHandler.getHandler().initCacheManager();
			}
		} catch (Exception e) {
			logError(e, "！！！严重错误:二级缓存初始化失败！");
		}

		LogOperateUtil.log("redis 缓存初始化成功！！！");
	}

	/**
	 * 清理缓存资源
	 *
	 */
	public void close() {
		try {
			if (jedisClusterClient != null) {
				synchronized (jedisClusterClient) {
					if (jedisClusterClient != null) {
						jedisClusterClient.close();
						jedisClusterClient = null;
					}
				}
			}
		} catch (IOException e) {
			logError(e, null);
		}
	}

	/**
	 * put
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#put(String, Object)
	 */
	public void put(String key, Object value) {
		if (value == null) {
			remove(key);
			return;
		}
		String nkey = namespacePrefix == null ? key : (namespacePrefix + key);
		jedisClusterClient.set(nkey.getBytes(), serialize(value));
		jedisClusterClient.set(PREFIX_CLASS_TYPE + nkey, value.getClass().getName());
		jedisClusterClient.sadd(allKeysSetKey, key);

		if (isDefualtUseSecondLevelCache) {
			RedisSecondLevelCacheHandler.getHandler().put(nkey, value);
		}
	}

	/**
	 * get
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#get(String)
	 */
	public Object get(String key) {
		return get(key, isDefualtUseSecondLevelCache);
	}

	/**
	 * get
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#get(String)
	 */
	public Object get(String key, boolean useLocalCache) {
		try {
			String nkey = namespacePrefix == null ? key : (namespacePrefix + key);
			Object value = null;
			if (isDefualtUseSecondLevelCache && useLocalCache) {
				value = RedisSecondLevelCacheHandler.getHandler().get(nkey);
				if (value != null)
					return value;
			}

			String classType = jedisClusterClient.get(PREFIX_CLASS_TYPE + nkey);
			if (classType == null)
				return null;
			value = deserialize(jedisClusterClient.get(nkey.getBytes()), Class.forName(classType));

			if (isDefualtUseSecondLevelCache) {
				RedisSecondLevelCacheHandler.getHandler().put(nkey, value);
			}
			return value;
		} catch (Exception e) {
			logError(e, null);
		}
		return null;
	}

	/**
	 * 是否包含key
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#containsKey(String)
	 */
	public boolean containsKey(String key) {
		return containsKey(key, isDefualtUseSecondLevelCache);
	}
	
	/**
	 * 是否包含key
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#containsKey(String)
	 */
	public boolean containsKey(String key, boolean useLocalCache) {
		String nkey = namespacePrefix == null ? key : (namespacePrefix + key);
		if (useLocalCache) {
			if (isDefualtUseSecondLevelCache && RedisSecondLevelCacheHandler.getHandler().containsKey(nkey))
				return true;
		}
		return jedisClusterClient.exists(PREFIX_CLASS_TYPE + nkey) && jedisClusterClient.exists(nkey.getBytes());
	}

	/**
	 * 移除key
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#remove(String)
	 */
	public void remove(String key) {
		String nkey = namespacePrefix == null ? key : (namespacePrefix + key);
		if (isDefualtUseSecondLevelCache) {
			RedisSecondLevelCacheHandler.getHandler().removeKey(nkey);
		}
		jedisClusterClient.del(nkey.getBytes());
		jedisClusterClient.del(PREFIX_CLASS_TYPE + nkey);
		jedisClusterClient.srem(allKeysSetKey, key);
	}

	/**
	 * 移除所有
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#removeAll()
	 */
	public void removeAll() {
		Set<String> keys = jedisClusterClient.smembers(allKeysSetKey);
		for (String key : keys) {
			remove(key);
		}
	}

	/**
	 * 获取所有key
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#getKeys()
	 */
	public Set<String> getKeys() {
		return jedisClusterClient.smembers(allKeysSetKey);
	}

	/**
	 * 移除like
	 */
	public void removeKeyLike(String like) {
		Set<String> keys = getKeys();
		for (String key : keys) {
			if (key != null && key.indexOf(like) >= 0) {
				remove(key);
			}
		}
	}

	/**
	 * 加入集合的值到cache
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#putAll(Map)
	 */
	public void putAll(Map<String, ?> map) {
		for (Entry<String, ?> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 获取缓存中多个值
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#getAll(List)
	 */
	public Map<String, Object> getAll(Collection<String> keys) {
		return getAll(keys, isDefualtUseSecondLevelCache);
	}

	/**
	 * 获取缓存中多个值
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#getAll(List)
	 */
	public Map<String, Object> getAll(Collection<String> keys, boolean useLocalCache) {
		Map<String, Object> res = new HashMap<String, Object>();
		for (String key : keys) {
			res.put(key, get(key));
		}
		return res;
	}

	/**
	 * 获取所有缓存命名空间
	 * 
	 * @return
	 */
	public Set<String> getCacheNames() {
		return jedisClusterClient.smembers(ALL_NAME_SPACE);
	}

	/**
	 * 序列化
	 * 
	 * @param obj
	 * @return
	 */
	private byte[] serialize(Object obj) {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		Output output = new Output(bs);
		try {
			Kryo kryo = new Kryo();
			if(IS_IBM_JAVA_VENDOR || isIBMSerializeType){
				kryo.setAsmEnabled(true);
				kryo.setReferences(false);
			}
			output.setOutputStream(bs);
			kryo.writeObject(output, obj);
		} finally {
			output.flush();
			output.close();
		}
		return bs.toByteArray();
	}

	/**
	 * 反序列化
	 * 
	 * @param ba
	 * @param clazz
	 * @return
	 */
	private <T> T deserialize(byte[] ba, Class<T> clazz) {
		Input input = new Input();
		try {
			Kryo kryo = new Kryo();
			if(IS_IBM_JAVA_VENDOR || isIBMSerializeType){
				kryo.setAsmEnabled(true);
				kryo.setReferences(false);
			}
			input.setBuffer(ba);
			return kryo.readObject(input, clazz);
		} finally {
			input.close();
		}
	}

	/**
	 * 记录异常日志
	 * 
	 * @param e
	 * @param msg
	 */
	private void logError(Exception e, String msg) {
		LogOperateUtil.logException(e, msg);
	}
	
	
	public static void main(String[] args) {
		CacheUtil.getInstance().put("aa", "b");
		CacheUtil.getInstance("cc").put("aa", "d");
		System.out.println(CacheUtil.getInstance().get("aa"));
		System.out.println(CacheUtil.getInstance("cc").get("aa"));
	}
}
