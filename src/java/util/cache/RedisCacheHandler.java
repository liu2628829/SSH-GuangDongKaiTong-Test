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
 * @date 2015��12��8�� ����6:34:09
 */
public class RedisCacheHandler extends CacheUtil {
	/** ����ǰ׺ **/
	private static final String PREFIX_CLASS_TYPE = "!!$#CLSTP#$!!";
	/** �����ռ�ǰ׺ **/
	private static final String SPLIT_NAME_SPACE = "!!$#NM-SP#$!!";
	/** �����ռ�key set **/
	private static final String ALL_NAME_SPACE = "!!$#ALL_NAME_SPACE#$!!";
	/** ����Handlerʵ�� **/
	private static final Map<String, RedisCacheHandler> CACHE_SPACE = new HashMap<String, RedisCacheHandler>();
	/** redis client�� **/
	private static JedisCluster jedisClusterClient;
	/** �����ռ�ǰ׺ **/
	private String namespacePrefix;
	/** ���е�ǰ�����ռ��key�ļ��ϵ�key **/
	private String allKeysSetKey;
	/** �Ƿ�Ĭ��ʹ�ö������� **/
	private static boolean isDefualtUseSecondLevelCache = true;
	/** java�ṩ�� **/
	private static final String JAVA_VENDOR = System.getProperty("java.vendor");
	/** IBM J9 vm **/
	private static final boolean IS_IBM_JAVA_VENDOR = JAVA_VENDOR.contains("IBM");
	/** �Ƿ�����IBM jvm���л���ʽ **/
	private boolean isIBMSerializeType = false;
	/**
	 * ��ȡRedis����ʵ��
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
	 * ��ʼ�����滷��
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
			//���������, Ĭ��8��
			config.setMaxTotal(MapUtils.getIntValue(ConfigInit.Config, "redis_MaxTotal", 100));
			//������������, Ĭ��8��
			config.setMaxIdle(MapUtils.getIntValue(ConfigInit.Config, "redis_MaxIdle", 20));
			//��С����������, Ĭ��0
			config.setMinIdle(MapUtils.getIntValue(ConfigInit.Config, "redis_MinIdle", 2));
			//������ӵ���С����ʱ�� Ĭ��1800000����(30����)
			config.setMinEvictableIdleTimeMillis(MapUtils.getIntValue(ConfigInit.Config, "redis_MinEvictableIdleTimeMillis", 300000));
			//������ж�ú����, ������ʱ��>��ֵ �� ��������>�������� ʱֱ�����,���ٸ���MinEvictableIdleTimeMillis�ж�  (Ĭ���������)   
			config.setSoftMinEvictableIdleTimeMillis(MapUtils.getIntValue(ConfigInit.Config, "redis_SoftMinEvictableIdleTimeMillis", 300000));
			//���ɨ���ʱ����(����) ���Ϊ����,����������߳�, Ĭ��-1
			config.setTimeBetweenEvictionRunsMillis(MapUtils.getIntValue(ConfigInit.Config, "redis_TimeBetweenEvictionRunsMillis", 2000));
			//���Ӻľ�ʱ�Ƿ�����, false���쳣,ture����ֱ����ʱ, Ĭ��true
			config.setBlockWhenExhausted(MapUtils.getBooleanValue(ConfigInit.Config, "redis_BlockWhenExhausted", false));
			//��ȡ����ʱ�����ȴ�������(�������Ϊ����ʱBlockWhenExhausted),�����ʱ�����쳣,
			//С����:������ȷ����ʱ��,  Ĭ��-1
			config.setMaxWaitMillis(MapUtils.getIntValue(ConfigInit.Config, "redis_MaxWaitMillis", 1000));

			config.setTestWhileIdle(true);
			config.setTestOnCreate(true);
			config.setTestOnReturn(true);
			config.setTestOnBorrow(true);

			//���ӳ�ʱʱ��
			int connectTimeout = MapUtils.getIntValue(ConfigInit.Config, "redis_ConnectTimeout", 1000);
			//socket��ʱʱ��
			int socketTimeout = MapUtils.getIntValue(ConfigInit.Config, "redis_SocketTimeout", 2000);

			jedisClusterClient = new JedisCluster(jedisClusterNodes, connectTimeout, socketTimeout, config);
		} catch (Exception e) {
			logError(e, "���������ش���:redis�����ʼ��ʧ�ܣ�");
		}

		try {
			isDefualtUseSecondLevelCache = MapUtils.getIntValue(ConfigInit.Config, "redis_DefualtUseSecondLevelCache", 1) != 0;
			if (isDefualtUseSecondLevelCache) {
				RedisSecondLevelCacheHandler.getHandler().initCacheManager();
			}
		} catch (Exception e) {
			logError(e, "���������ش���:���������ʼ��ʧ�ܣ�");
		}

		LogOperateUtil.log("redis �����ʼ���ɹ�������");
	}

	/**
	 * ��������Դ
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
	 * �Ƿ����key
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#containsKey(String)
	 */
	public boolean containsKey(String key) {
		return containsKey(key, isDefualtUseSecondLevelCache);
	}
	
	/**
	 * �Ƿ����key
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
	 * �Ƴ�key
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
	 * �Ƴ�����
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
	 * ��ȡ����key
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#getKeys()
	 */
	public Set<String> getKeys() {
		return jedisClusterClient.smembers(allKeysSetKey);
	}

	/**
	 * �Ƴ�like
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
	 * ���뼯�ϵ�ֵ��cache
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#putAll(Map)
	 */
	public void putAll(Map<String, ?> map) {
		for (Entry<String, ?> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * ��ȡ�����ж��ֵ
	 * 
	 * @see com.cck.demo.redis_cluster.CacheUtil#getAll(List)
	 */
	public Map<String, Object> getAll(Collection<String> keys) {
		return getAll(keys, isDefualtUseSecondLevelCache);
	}

	/**
	 * ��ȡ�����ж��ֵ
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
	 * ��ȡ���л��������ռ�
	 * 
	 * @return
	 */
	public Set<String> getCacheNames() {
		return jedisClusterClient.smembers(ALL_NAME_SPACE);
	}

	/**
	 * ���л�
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
	 * �����л�
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
	 * ��¼�쳣��־
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
