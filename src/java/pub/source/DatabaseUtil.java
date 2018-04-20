package pub.source;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pub.dbDialectFactory.Dialect;
import pub.dbDialectFactory.DialectFactory;
import pub.servlet.ConfigInit;
import util.BaseRuntimeException;
import util.DateUtil;
import util.StringUtil;
/**
 * 
 * 更新说明:
 * 2012/3/28 gaotao 加updateByPrepareStatement updateByPrepareStatementBatch 两个方法,用法见方法注释
 * 2012/6/20 gaotao 查询方法，都加入了对Clob字段的特定读取方式，加入了对Clob,text的写入方式
 * 2012/7/28 pengjiewen 新增queryForFetch，fetchNext两个方法，实现分批抓取数据
 * 2012/9/26 qiaoqide 新增对查询结果做限制
 * 2012/10/26 zhanweibin 修改updateByPrepareStatementBatch()方法，使用getUpdateCount()获取更新的记录数
 * 2013/1/5 tangyj  在查询方法中，对oracle库的number字段的个整数位为0的小数进行处理，除开fetchNext1()方法，该方法没法获取数据库类型。
 * 2013/5/17 gaotao 各更新方法中，进行事务手动提交、回滚
 * 2013/5/21 gaotao 加入setObject方法，对字符串的设置进行特殊处理，不然oracle varchar2(4000),最多只能插666个中文，而实际应该可以插2000个才对
 * 2013/6/20 gaotao 加入getRealSql方法，并对所有含?号的SQL，写日志时，替换出真实值。封装了执行前、后日志。
 * 2013/9/7 gaotao 两个无list参数的查询方法，调用有list参数的方法，使代码进一步重用；加上一个新的查询分页方法;
 * 2014/4/7 gaotao insertByPrepareStatementBatch方法重构，1拆成3，为方便通用导入时的单一长连接处理
 */
public class DatabaseUtil {
	
	/**限制查询结果数据行*/
	private static final int TOTAL= StringUtil.toInteger(ConfigInit.Config.getProperty("DataBaseUtil_MaxResult", "10000"));
	
	/**
	 * 获取链接
	 * 
	 * @param domain 数据源
	 * @return 数据库连接对象
	 */
	public static synchronized Connection getConnection(String domain) {
		Connection connection = null;
		connection = StringUtil.checkStr(domain) ? DatabaseConnection
				.getConnection(domain) : DatabaseConnection.getConnection();
		return connection;
	}
	
	/**
	 * 简单SQL查询
	 * 
	 * @param sql 查询SQL
	 * @return 查询结果集
	 */
	public static List queryForList(String sql) {
		return queryForList(sql, "");
	}
	
	/**
	 * 简单SQL查询
	 * 
	 * @param sql 查询SQL	 
	 * @param domain 数据源
	 * @return 查询结果集
	 */
	public static List queryForList(String sql, String domain) {
		return queryForList(sql, null, domain);
	}
	
	/**
	 * PrepareStatement SQL查询
	 * 
	 * @param sql 查询SQL	
	 * @param list 查询参数 
	 * @param domain 数据源
	 * @return 查询结果集
	 */
	public static List queryForList(String sql, List list, String domain) {
		Map<String, String> params = before(sql, list, domain);
		
		List tempList = queryForList(sql, list, domain, params);
		
		after(params, tempList.size());
		
		return tempList;
	}
	
	
	/**
	 * 预处理方式查询，仅用于当前类和日志工具类调用, 不记jdbc日志，不为业务系统开放
	 * 
	 * @param sql 查询SQL	
	 * @param list 查询参数 
	 * @param domain 数据源
	 * @return 查询结果集
	 */
	protected static List queryForList(String sql, List list, String domain, Map<String, String> params) {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Map> tempList = new ArrayList<Map>();
		try {
			conn = getConnection(domain);
			st = conn.prepareStatement(sql);
			if(list != null && list.size() > 0){
				setObject(st, list, domain); //设置参数	
			}
			rs = st.executeQuery();
			resultToList(tempList, domain, rs, null);
		} catch (Exception e) {
			LogOperateUtil.logSQLError(e, domain, params != null ? params.get("sql1") : "",params != null ? params.get("path") : "");
		} finally {
			free(conn, st, rs);
		}
		return tempList;
	}
	
	
	/**
	 * 分页SQL查询
	 * 
	 * @param sql 查询SQL
	 * @param page 当前页
	 * @param limit 每页记录数
	 * @return 查询结果集
	 */
	public static List queryForListByPage(String sql, int page, int limit) {
		return queryForListByPage(sql, page, limit, "");
	}
	
	/**
	 * 分页SQL查询
	 * @param sql 查询SQL
	 * @param page 当前页
	 * @param limit 每页记录数
	 * @param domain 数据源
	 * @return 查询结果集
	 */
	public static List queryForListByPage(String sql, int page, int limit, String domain) {
		return queryForListByPage(sql, page,limit, null, domain);
	}
	
	/**
	 * PrepareStatement预处理分页查询，解决SQl注入问题
	 * @param sql 查询SQL
	 * @param page 当前页
	 * @param limit 每页记录数
	 * @param list 查询参数
	 * @param domain 数据源
	 * @return 查询结果集
	 */
	public static List queryForListByPage(String sql, int page, int limit, List list, String domain) {
		Map<String, String> params = before(sql, list, domain);
		Connection conn = null;
		ResultSet rs = null;
		List<Map> tempList = new ArrayList<Map>();
		conn = getConnection(domain);
		Statement st = null;
		int rowId = 0;
		try {
			//假如from前是一个换行符而不是一个空格，会导致sybase分页方言里报错
			sql = sql.replace("\n", " ").replace("\r", " ");
			// 方言
			Object objs[] = DialectFactory.getDialect(domain)
					.getDataByPageEoms(conn, sql, page, limit, list);
			if(objs != null){
				st = (Statement) objs[0];
				rs = (ResultSet) objs[1];
				rowId = resultToList(tempList, domain, rs, null);
			}
		} catch (Exception e) {
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, rs);
		}
		after(params, rowId);
		return tempList;
	}
	
	/**
	 * 分页查询：含union关键字，数据量又大的情况，可自己写完整的分页SQL，而不用方言类里的实现，
	 * 避免被方言类里实现的分页包装一层了查询后，影响性能。
	 * 此方法局限性：
	 *  1,此方法不适用于Sybase
	 *  2,由于参数中的SQL已经包含了分页逻辑，一旦数据库类型发生变化，对应的SQL，也要更改。
	 *   （解决办法：在自已的DAO里先判断好数据库类型，根据不同数据库类型，写几套不同的SQL，再传入此方法） 
	 * @param countSql 查询总记录数的SQL
	 * @param dataSql 查询某一页数据的SQL
	 * @param list SQL中?号对应的参数
	 * @param domain 数据源
	 * @return
	 */
	public static List queryForListByPage(String countSql, String dataSql, List list, String domain){
		Map<String, String> params = before(countSql, dataSql, list, domain);
		Connection conn = null;
		ResultSet rs = null;
		List<Map> tempList = new ArrayList<Map>();
		conn = getConnection(domain);
		PreparedStatement st = null;
		int rowId = 0;
		try {
			/**拿总记录数*/
			st = conn.prepareStatement(countSql);
			int size =0;
			if(list != null){
				size = list.size();
				for(int i=0; i<size; i++){
					st.setObject(i+1, list.get(i));
				}
			}
			String totalCount = "0";
			rs = st.executeQuery();
			if (rs != null && rs.next()) {
				totalCount = rs.getString(1);
			}
			free(null, st, rs); //先关闭已经无用的对象
			if("0".equals(totalCount)){ //没有记录数，不必继续查询数据结果集
				return tempList;
			}
			
			/**得数据结果集*/
			st =conn.prepareStatement(dataSql);
			if(list != null){
				for(int i=0; i<size; i++){
					st.setObject(i+1, list.get(i));
				}
			}
			rs = st.executeQuery();
			rowId = resultToList(tempList, domain, rs, totalCount);
		} catch (Exception e) {
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, rs);
		}
		after(params, rowId);
		return tempList;
	}
	
	/**
	 * 把结果集转变成 List<Map>对象
	 * @param tempList 用于装载结果的list
	 * @param domain 数据源
	 * @param rs 结果集对象
	 */
	public static int resultToList(List<Map> tempList, String domain, ResultSet rs, String totalCount) throws Exception{
		boolean hasTotalCount = StringUtil.checkStr(totalCount);
		int rowId = 0;
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();
		String colValue =null;
		String dbType = DialectFactory.getDialect(domain).getDBType();//conn.getMetaData().getDatabaseProductName();
		while (rs.next()) {
			rowId++;
			Map<String, String> map = new LinkedHashMap<String, String>();
			for (int i = 1; i <= columnCount; i++) {
				String colName = meta.getColumnLabel(i);
				colValue = ("CLOB".equalsIgnoreCase(meta.getColumnTypeName(i)))?readClob(rs.getObject(i)):rs.getString(i);
				colValue = formatNumberValue(meta.getColumnTypeName(i),colValue,dbType);
				map.put(colName, colValue);
			}
			if(map.containsKey("ROW_ID")){map.put("ROWID", map.get("ROW_ID"));}
			map.put("rowId", rowId + "");
			if(hasTotalCount){
				map.put("totalCount", totalCount); //装入总记录数
				map.put("iRecCount", totalCount); //装入总记录数
			}
			tempList.add(map);
			if(rowId>TOTAL)break;//如果数据超出指定的条数将强制退出循环
		}
		
		return rowId;
	}
	
	
	/**
	 * 更新数据库
	 * 
	 * @param sql 更新SQL
	 * @return 更新行数
	 */
	public static int updateDateBase(String sql, String domain) {
		Map<String, String> params = before(sql, null, domain);
		Connection conn = null;
		Statement st = null;
		int num = 0;
		conn = getConnection(domain);
		try {
			st = conn.createStatement();
			num = st.executeUpdate(sql);
		} catch (Exception e) {
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, null);
		}
		after(params, num);
		return num;
	}

	/**
	 * 更新数据库
	 * 
	 * @param sql 更新SQL
	 * @return 更新行数
	 */
	public static int updateDateBase(String sql) {
		return updateDateBase(sql, "");
	}

	/**
	 * 预处理方式更新,仅用于当前类存日志信息,不为业务系统开放
	 * @param sql 更新SQL
	 * @param data ？号参数集
	 * @param domain 数据源
	 * @return 更新行数
	 */
	protected static int updateDateBase(String sql, ArrayList data, String domain) {
		Connection conn = null;
		PreparedStatement st = null;
		int num = 0;
		conn = getConnection(domain);
		try {
			st = conn.prepareStatement(sql);
			setObject(st, data, domain);
			num = st.executeUpdate();
		} catch (Exception e) {
			LogOperateUtil.logSQLError(e, domain, sql, "存日志出错");
		} finally {
			free(conn, st, null);
		}
		return num;
	}

	/**
	 * 批量更新
	 * @param sqlArray 更新行数数组
	 * @param domain 数据源
	 * @return 成功与否 1成功，其它失败
	 */
	public static int updateBatchBase(String[] sqlArray, String domain) {
		int flg = 1;
		try{
			updateBatchBase1(sqlArray, domain);
		}catch(BaseRuntimeException ex){
			flg = 0;
			throw ex;
		}
		return flg;
	}
	
	public static int updateBatchBase(String[] sqlArray) {
		return updateBatchBase(sqlArray, "");
	}
	
	/**
	 * 批量更新
	 * 
	 * @param sqlArray 更新行数数组
	 * @param domain 数据源
	 * @return 更新行数
	 */
	public static int updateBatchBase1(String[] sqlArray, String domain) {
		Map<String, String> params = before(sqlArray[0], null, domain);
		Connection conn = null;
		Statement st = null;
		conn = getConnection(domain);
		int num = 0;
		try {
			st = conn.createStatement();
			conn.setAutoCommit(false);
			int length = sqlArray.length;
			for (int i = 0; i < length; i++) {
				st.addBatch(sqlArray[i]);
				// 一批最多100条
				if ((i + 1) % 100 == 0) {
					num += sum(st.executeBatch());
					st.clearBatch();
					conn.commit();
				}
			}
			if(length % 100 > 0){ //因为空数据执行会报错
				num += sum(st.executeBatch());
			}
			commit(conn);
		} catch (Exception e) {
			rollback(conn);
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, null);
		}
		after(params, num);
		return num;
	}
	
	/**
	 * 求和
	 * 
	 * @param arr 更新行数数组
	 * @return 总行数
	 */
	public static int sum(int[] arr) {
		int num = 0;
		for (int i = 0; i < arr.length; i++) {
			num += arr[i];
		}
		return num;
	}

	/**
	 * 批量更新
	 * 
	 * @param sqlArray SQL数组
	 * @return 更新行数
	 */
	public static int updateBatchBase1(String[] sqlArray) {
		return updateBatchBase1(sqlArray, "");
	}

	/**
	 * 执行SQL,把单行结果以Map类型返回
	 * 
	 * @param sql 查询SQL
	 * @return 数据结查
	 */
	public static Map<String, Object> queryForMap(String sql, String domain) {
		Map<String, String> params = before(sql, null, domain);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Map<String, Object> map = null;
		conn = getConnection(domain);
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			String dbType = conn.getMetaData().getDatabaseProductName();
			if (rs.next()) {
				ResultSetMetaData meta = rs.getMetaData();
				int columnCount = meta.getColumnCount();
				map = new LinkedHashMap<String, Object>();
				String colValue=null;
				for (int i = 1; i <= columnCount; i++) {
					String colName = meta.getColumnLabel(i);
					colValue = (meta.getColumnTypeName(i).equalsIgnoreCase("CLOB"))?readClob(rs.getObject(i)):rs.getString(i);
					colValue = formatNumberValue(meta.getColumnTypeName(i),colValue,dbType);
					map.put(colName, colValue);
				}
			}
		} catch (Exception e) {
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, rs);
		}
		after(params, 1);
		return map;
	}
	
	/**
	 * 执行SQL,把单行结果以Map类型返回
	 * 
	 * @param sql 查询SQL
	 * @return 数据结查
	 */
	public static Map<String, Object> queryForMap(String sql) {
		return queryForMap(sql, "");
	}

	/**
	 * 不存JDBC日志的取主键,此方法仅用于保存日志
	 * 
	 * @param domain 数原
	 * @param path 程序路径
	 * @param sql 执行SQL
	 * @return 主键
	 */
	public static String getKeyId(String domain, String path, String sql) {
		domain = StringUtil.checkStr(domain) ? domain : DialectFactory.getDefaultDatasrc();
		String key = DialectFactory.getPrimaryKeys(domain);// 从缓存取
		if (key == null) {//如果缓存内无值
			Connection conn = getConnection(domain);
			Statement st = null;
			ResultSet rs = null;
			try {
				int cachePrimaryKeys = Integer.parseInt(ConfigInit.Config.getProperty(
						"cachePrimaryKeys", "100"));//缓存主键个数
				Object objs[] = DialectFactory.getDialect(domain).getKeyId(
						conn,cachePrimaryKeys);// 方言
				st = (Statement) objs[0];
				rs = (ResultSet) objs[1];
				if (rs != null && rs.next()) {
					key = rs.getString(1);
				}
				DialectFactory.setPrimaryKeys(domain, key);// 重设缓存
			} catch (Exception e) {
				LogOperateUtil.logSQLError(e, domain, sql, path);
			} finally {
				free(conn, st, null);
			}
		}
		return key;
	}

	/**
	 * 获取主键
	 * tableName 是一个无用参数，为适应公司以前版本，方便集成，故保留此参数
	 * @param domain 数据源
	 * @return 主键
	 */
	public static String getKeyId(String domain, String tableName) {
		String sql = "{call SP_GET_ID_EX2('" + tableName + "')}";
		Map<String, String> params = before(sql, null, domain);
		String key = getKeyId(domain, params.get("path"), sql);
		after(params, 1);
		return key;
	}
	
	/**
	 * 获取主键
	 * tableName 是一个无用参数，为适应公司以前版本，方便集成，故保留此参数
	 * @param domain 数据源
	 * @return 主键
	 */
	public static String getKeyId(String tableName) {
		return getKeyId("", tableName);
	}
	
	/**获取一批主键
	 * @param numbers 要获取主键的个数
	 * @param domain 数据源，传null则以默认数据原为准
	 * @param tableName 是一个待实现参数，为适应公司以前版本，方便集成，故保留此参数，可以传null
	 * @return 主键数组
	 * */
	public static String[] getKeyIds(int numbers, String domain, String tableName){
		String sql = "批量获取"+numbers+"个主键{call SP_GET_ID_EX2('" + tableName + "')}";
		Map<String, String> params = before(sql, null, domain);
		
		String[] keys = new String[numbers];
		for(int i=0; i<numbers; i++){
			keys[i] = getKeyId(domain, params.get("path"), sql);
		}
		
		after(params, 1);
		return keys;
	}

	/**
	 * 获取表字段信息
	 * 
	 * @param sql 查询SQL
	 * @return 字段信息集合
	 */
	@Deprecated
	public static List queryColumnsForList(String sql ,String domain) {
		Map<String, String> params = before(sql, null, domain);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<Map> tempList = new ArrayList<Map>();
		
		int columnCount = 0;
		try {
			conn = getConnection(domain);
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			ResultSetMetaData meta = rs.getMetaData();
			columnCount = meta.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				Map<String, String> map = new LinkedHashMap<String, String>();
				String colName = meta.getColumnLabel(i);
				String colType = meta.getColumnTypeName(i);
				
				map.put("colName", colName);
				map.put("colType", colType);
				map.put("colSize", meta.getColumnDisplaySize(i)+"");
				map.put("colScale", meta.getScale(i) + "");
				map.put("colNull", meta.isNullable(i) + "");
				
				try{
					//某些数据库 meta.getPrecision(i) 取值超过int的范围，如：某些版本 oralce COLB字段返回值是 4294967295 
					map.put("colPrecision", meta.getPrecision(i) + ""); 
				}catch(Exception ex){
					map.put("colPrecision", "-1");  
				}
				
				/*if(!"CLOB".equalsIgnoreCase(colType)){
					map.put("colPrecision", meta.getPrecision(i) + ""); //某些版本的 oralce COLB字段返回值是 4294967295 ，超出int范围, 有些返回-1
				}else{
					map.put("colPrecision", "-1");  
				}*/
				tempList.add(map);
			}
		} catch (Exception e) {
			LogOperateUtil.logSQLError(e, null, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, rs);
		}
		after(params, columnCount);
		return tempList;
	}
	
	/**
	 * 获取表字段信息
	 * 
	 * @param sql 查询SQL
	 * @return 字段信息集合
	 */
	public static List queryColumnsForList(String sql) {
		return queryColumnsForList(sql, null);
	}
	
	/**
	 * 关闭连接资源
	 * @param conn 连接对象
	 * @param st 执行对象
	 * @param rs 结果集对象
	 */
	public static synchronized void free(Connection conn, Statement st, ResultSet rs) {
		try {
			if (rs != null) { //&& !rs.isClosed() jdk1.5还没有此方法
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			if (st != null) { //&& !st.isClosed() jdk1.5还没有此方法
				st.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 事务提交
	 * @param conn 数据连接对象
	 * @throws SQLException 抛出SQL异常
	 */
	public static void commit(Connection conn) throws SQLException{
		if(conn!=null){
			conn.commit();
			conn.setAutoCommit(true);
		}
	}
	
	/**
	 * 事务回滚
	 * @param conn 连接对象
	 */
	public static void rollback(Connection conn){
		try {
			if(conn != null){
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 用预处理方式进行更新
	 * @param sql 更新SQL语句
	 * @param data data里的数据类型与数据库里的数据类型对应起来
	 * @param domain 数据源
	 */
	public static int updateByPrepareStatement(String sql, List data, String domain){
		Map<String, String> params = before(sql, data, domain);
		Connection conn = null;
		PreparedStatement st = null;
		int num = 0;
		conn = getConnection(domain);
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(sql);
			setObject(st, data, domain);
			num = st.executeUpdate();
			commit(conn);
		} catch (Exception e) {
			rollback(conn);
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, null);
		}
		after(params, num);
		return num;
	}
	
	/**
	 * 用预处理方式进行"批量"更新
	 * @param sql 此时SQL只能是唯一的,这就要求，每个？号入参，都不能为空
     * @param datas 参数集合
     * @param domain 数据源
     * @return 更新的行数
	 */
	public static int updateByPrepareStatementBatch(String sql, List<List> datas, String domain){
		Map<String, String> params = before(sql, ((datas!=null&&datas.size()>0)?datas.get(0):null), domain);
		long timeBefore = System.currentTimeMillis();
		Connection conn = null;
		PreparedStatement st = null;
		int num = 0;
		conn = getConnection(domain);
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(sql);
			int size = datas.size();
			for(int i=0; i<size; i++){
				List data=datas.get(i);
				setObject(st, data, domain);
				
				/* was 环境下，sybase数据库会有异常风险
				st.addBatch();
				if ((i + 1) % 100 == 0) {//每100行执行一次
					st.executeBatch();
					num += st.getUpdateCount();//executeBatch()方法在oracle下每执行一条的返回值为-2。使用getUpdateCount()获取更新的记录数
					st.clearBatch();//清空
				}*/
				
				num+=st.executeUpdate();
				st.clearParameters();
			}
			
			/*
			if((size % 100) > 0){ //因为空数据执行会报错
				st.executeBatch();
				num += st.getUpdateCount();
			}*/
			commit(conn);
		} catch (Exception e) {
			rollback(conn);
			num=0;
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, null);
		}
		after(params, num);
		return num;
	}
	 
	/**
	 * 用预处理方式进行"批量"更新(非真正批处理)
	 * @param sql 此时SQL是唯一的,但会跟据数据是否为空，将对应的"?"号换为"null"
     * @param datas 参数集合
     * @param domain 数据源
     * @return 更新的行数
	 */
	public static int updateByPrepareStatementBatch2(String sql, List<List> datas, String domain){
		Map<String, String> params = before(sql, ((datas!=null&&datas.size()>0)?datas.get(0):null), domain);
		Connection conn = null;
		PreparedStatement st = null;
		int num = 0;
		conn = getConnection(domain);
		try {
			conn.setAutoCommit(false);
			int size1 = datas.size();
			for(int i=0; i<size1; i++){
				st=conn.prepareStatement(replaceSQL(sql,datas.get(i)));
				setObject(st, datas.get(i), domain);
				num+=st.executeUpdate();
				//for循环中，注意及时关闭
				if(st != null){
					st.close();
				}
			}
			commit(conn);
		} catch (Exception e) {
			rollback(conn);
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, null);
		}
		after(params, num);
		return num;
	}
	
	/**
     * 用预处理方式进行"批量"插入，作为updateByPrepareStatementBatch的补充，自动提交
     * @param sql 不得省略插入的列名
     * @param datas 参数集合
     * @param domain 数据源
     * @return 更新的行数
   */
    public static int insertByPrepareStatementBatch(String sql, List<List> datas, String domain){
		Map<String, String> params = before(sql, ((datas!=null&&datas.size()>0)?datas.get(0):null), domain);
		
        int updateCount =0;
        if(datas!=null && datas.size()>0 && datas.get(0).size()>0){
        	String sql2 = sql.substring(sql.indexOf("into "));
        	//表名
   		    String tableName = sql2.substring(sql2.indexOf("into ")+5, sql2.indexOf('(')).trim();
            //字段
   		    String selFields = sql2.substring(sql2.indexOf("(")+1, sql2.indexOf(')')).trim();
   		    //字段值
   		    String sql3 = sql.substring(sql.indexOf("values"));
		    String[] setfs = sql3.substring(sql3.indexOf('(') +1, sql3.lastIndexOf(')')).split(",");
		    
   		    //String[] setfs = sql.substring(sql.lastIndexOf('(') +1, sql.lastIndexOf(')')).split(",");//当有自增长字段时，是不能直接用sql.lastindexof("(")的，因为自增长数据库函数有(
            Map<Integer,Integer> fields = new LinkedHashMap<Integer,Integer>();
            Map<Integer,Integer> columnTypes = new LinkedHashMap<Integer,Integer>();
            //标识?号所在字段序号
            for(int i=0,j=0; i < setfs.length; i++){
                if(setfs[i].contains("?")){
                    fields.put(j++, i+1);
                }
            }
            
            Connection conn = getConnection(domain);
            PreparedStatement ps = null;
            ResultSet rs = null;
            try{
            	Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                String sql1 ="select "+selFields+" from "+tableName+" where 1<0";
                rs = st.executeQuery(sql1);
                ResultSetMetaData md = rs.getMetaData();
                for(int i=1;i<=md.getColumnCount();i++){
                    columnTypes.put(i, md.getColumnType(i));
                }
                free(conn, st, rs);

            	conn = getConnection(domain);
                int size = datas.size();
                for(int i=0;i<size;i++){
                   ps = conn.prepareStatement(sql);
                   List tList = datas.get(i);
                   int size1 = tList.size(); 
                   for (int j = 0; j < size1; j++) {
                       int ft = columnTypes.get(fields.get(j));
                       Object vle = tList.get(j);
                       if(vle!=null){
                            switch (ft) {
                                case java.sql.Types.VARCHAR: ;
                                case java.sql.Types.CHAR: ;
                                case -15: ;
                                case -9: ;
                                case java.sql.Types.LONGVARCHAR: ;
                                case -16: setString(ps, vle, j, domain); break;
                                case java.sql.Types.INTEGER: ps.setInt(j+1, Integer.valueOf(vle.toString())); break;
                                case java.sql.Types.SMALLINT: ps.setShort(j+1, Short.valueOf(vle.toString())); break;
                                case java.sql.Types.TINYINT: ps.setShort(j+1, Short.valueOf(vle.toString())); break;
                                case java.sql.Types.BIGINT: ps.setLong(j+1, Long.valueOf(vle.toString())); break;
                                case java.sql.Types.DOUBLE: ps.setDouble(j+1, Double.valueOf(vle.toString())); break;    
                                case java.sql.Types.DECIMAL: 
                                	if(vle instanceof java.math.BigDecimal){ 
                                		ps.setBigDecimal(j+1, (java.math.BigDecimal)vle);
                                	}else{
                                		ps.setDouble(j+1, Double.valueOf(vle.toString()));
                                	}
                                	break;
                                case java.sql.Types.FLOAT: ps.setFloat(j+1, Float.valueOf(vle.toString())); break;
                                case java.sql.Types.TIMESTAMP: ps.setTimestamp(j+1,new java.sql.Timestamp(((java.util.Date)vle).getTime())); break;
                                case java.sql.Types.DATE: ps.setDate(j+1, new java.sql.Date(((java.util.Date)vle).getTime())); break;
                                case java.sql.Types.NUMERIC: 
                                        if(vle instanceof Integer)
                                            ps.setInt(j+1, Integer.valueOf(vle.toString()));
                                        else if(vle instanceof Long)
                                            ps.setLong(j+1, Long.valueOf(vle.toString()));
                                        else {
                                        	if(vle instanceof java.math.BigDecimal) {
                                        		ps.setBigDecimal(j+1, (java.math.BigDecimal)vle);
											}else{
												ps.setDouble(j+1, Double.valueOf(vle.toString()));
											}
                                        }
                                        break;
                                default: ps.setObject(j+1, vle);
                            }
                       }else{
                           ps.setNull(j+1, ft);
                       }
                   }
                   
                   String tempSql = getRealSql(sql, tList, domain); 
                   try{
                	   updateCount += ps.executeUpdate();
                   }catch(Exception ex){
                	   LogOperateUtil.logException(ex, "导入入库时数据insert执行失败，SQL是:"+tempSql);
                   }finally{
                	   free(null, ps, null);
                   }
               }
    		} catch (Exception e) {
    			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
            }finally{
            	free(conn, ps, rs);
            }
        }
        after(params, updateCount);
        return updateCount;
    }
    
	/**
	 * sql中的"?"号，如果对应数据为null,则转换为"null",同时将数据集合中的null值删除
	 * @param sql SQL语句
	 * @param list 参数集合
	 * @return 去null的SQL
	 */
	public static String replaceSQL(String sql,List list){
		StringBuffer sb=new StringBuffer();
		String s[]=sql.split("\\?");
		if(list == null) return sql;
		int size = list.size();
		for(int i=size-1; i>=0; i--){
			if(list.get(i)==null){
				s[i]=s[i]+"null";
				list.remove(i);
			}else
				s[i]=s[i]+"?";
		}
		for(int i=0;i<s.length;i++)
			sb.append(s[i]);
		return sb.toString();
	}
	
	/**
	 * CLOB字段读取
	 * @param obj 数据对象
	 * @return 结果文本
	 */
	private static String readClob(Object obj){
		if(obj==null)return "";
		StringBuffer mystr=new StringBuffer(""); 
		String str = "";
		BufferedReader a=null;
		try{
			a=new BufferedReader(((Clob)obj).getCharacterStream()); //以字符流的方式读入BufferedReader 
			int flg = 0;
			while ((str = a.readLine()) != null) {
				if(flg > 0){
					mystr.append("\n");
				}
				mystr.append(str); 
				flg++;
			}
		}catch(Exception e){
			return null;
		}finally{
			try{
				if(a!=null)a.close();
			}catch(Exception ex){}
		}
		return mystr.toString();
	}
	
	/**
	 * ClOB字段写入
	 * @param tableName 表名
	 * @param where 更新时的where条件，确定要更新的行
	 * @param cols 字段名数组
	 * @param val 值数组
	 * @param domain 数据源
	 */
	public static void writeClobOrText(String tableName, String where, String cols[], String val[], String domain){
		String sql="更改"+tableName+"表的CLOB或Text字段";
		Map<String, String> params = before(sql, null, domain);
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs=null;
		conn = getConnection(domain);
		try {
			conn.setAutoCommit(false);
			Object objs[] = DialectFactory.getDialect(domain).writeClobOrText(conn, tableName,where,cols, val);
			st=(PreparedStatement)objs[0];
			rs=(ResultSet)objs[1];
			commit(conn);
		} catch (Exception e) {
			rollback(conn);
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, rs);
		}
		after(params, 1);;
	}
	
	/**
	 * ClOB字段批量写入
	 * @param list 数据集合
	 * @param domain 数据源
	 */
	public static void writeClobOrTextBatch(List list, String domain){
		String sql = "批量更改表的CLOB或Text字段";
		Map<String, String> params = before(sql, null, domain);
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		int size = list.size();
		conn = getConnection(domain);
		Dialect dia = DialectFactory.getDialect(domain);
		try {
			conn.setAutoCommit(false);
			for(int i = 0; i < size; i++){
				Map map = (Map) list.get(i);
				String tableName = StringUtil.toString(map.get("tableName"));
				String where = StringUtil.toString(map.get("where"));
				String[] cols = (String[])map.get("cols");
				String[] val = (String[])map.get("vals");
				Object objs[] = dia.writeClobOrText(conn, tableName,where,cols, val);
				st = (PreparedStatement)objs[0];
				rs = (ResultSet)objs[1];
				//for循环中，注意及时关闭
				if(rs !=null){
					rs.close();
				}
				if(st != null){
					st.close();
				}
			}
			commit(conn);
		} catch (Exception e) {
			rollback(conn);
			LogOperateUtil.logSQLError(e, domain, params.get("sql1"), params.get("path"));
		} finally {
			free(conn, st, rs);
		}
		after(params, size);
	}
	
	/**
	 * 分批抓取查询
	 * 如果使用此方法，记得在使用完后，一定要记得手动进行连接资源的关闭，通过rs得可得到连接对象
	 * 例如：DatabaseUtil.free(rs.getStatement().getConnection(),rs.getStatement(),rs);
	 * @param sql 查询SQL
	 * @param domain 数据源
	 * @param fs 一次最大抓取行数
	 * @return 结果集
	 */
	public static ResultSet queryForFetch(String sql, String domain, int fs) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		conn = getConnection(domain);
		try {
			conn.setAutoCommit(false);
			st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			//每次抓取数据量
			fs = fs > st.getMaxRows() ? fs : st.getMaxRows();
			st.setFetchSize(fs);
			st.setFetchDirection(ResultSet.FETCH_FORWARD);
			rs = st.executeQuery(sql);
			rs.setFetchSize(fs);
		} catch (Exception e) {
			free(conn, st, rs); 
			String path = LogOperateUtil.logCallStack();
			LogOperateUtil.logSQLError(e, domain, sql, path);
		} 
		return rs;
	}

	/**
	 * 分批抓取查询
	 * 如果使用此方法，记得在使用完后，一定要记得手动进行连接资源的关闭，通过rs得可得到连接对象
	 * 例如：DatabaseUtil.free(rs.getStatement().getConnection(),rs.getStatement(),rs);
	 * @param sql 查询SQL
	 * @param fs 一次最大抓取行数
	 * @return 结果集
	 */
	public static ResultSet queryForFetch(String sql, int fs){
		return queryForFetch(sql,"",fs);
	}

	/**
	 * 
	 * 分批抓取逐行加载
	 * @param rs 结果集
	 * @param rowId 行号
	 * @param sql 执行的SQL
	 * @param domain 数据源
	 * @return 一行结果
	 */
	public static Map fetchNext(ResultSet rs, int rowId, String sql, String domain){
		Map<String, String> map = null;
		String colValue =null;
		try {
			//有下一行时return行，没有则null
			if (rs.next()) {
				map = new LinkedHashMap<String, String>();
				map.put("rowId", rowId + "");
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					String colName = rs.getMetaData().getColumnLabel(i);
					colValue = (rs.getMetaData().getColumnTypeName(i).equalsIgnoreCase("CLOB"))?DatabaseUtil.readClob(rs.getObject(i)):rs.getString(i);
					map.put(colName, colValue);
				}
			}
		} catch (SQLException e) {
			String path = LogOperateUtil.logCallStack();
			LogOperateUtil.logSQLError(e, domain, sql, path);
		}
		return map;
	}
	
	/**
	 * 取UUID主键
	 * @return UUID主键
	 */
	public static String getUUID(){
		String uuId=java.util.UUID.randomUUID().toString().replaceAll("-", "");
		return uuId;
	}

	/**
	 * 根据参数获取in(?)语句
	 * @param param 逗号拼起来的参数串  
	 * @param list 转换后的参数将装入此集合
	 * @return 替换后的SQL,每个参数转成了?号
	 */
	public static String inParameterLoader(String param,List list){
		return inParameterLoader(param, list, null);
	}
	
	/**
	 * 根据参数获取in(?)语句
	 * @param param 逗号拼起来的参数串  
	 * @param list 转换后的参数将装入此集合
	 * @param filedType 数据类型 1是数值，否则为字符串 
	 * @return 替换后的SQL,每个参数转成了?号
	 */
	public static String inParameterLoader(String param,List list,String filedType){
		StringBuffer sql = new StringBuffer();
		sql.append(" in(");
		if(!param.equals("")){
			String[] params = param.split(",");
			for(int i=0;i<params.length;i++){
				if(i!=0)sql.append(",");
				sql.append("?");
				list.add(getTargetType(params[i], filedType));
			}
		}else{
			sql.append("null");
		}
		sql.append(")");
		return sql.toString();
	}
	
	/**
	 * 
	 * 判定字符串类型
	 * @param param 参数 
	 * @param fileType 数据类型 1或者空是数值，否则为字符串
	 * @return 替换后的参数
	 */
	public static Object getTargetType(String param, String fileType){
		Object obj;
		if(StringUtil.isInteger(param) && ("1".equals(fileType) || fileType == null)){ //整数 (long型最大19位数)
			obj = (param.length() >= 10) ? Long.parseLong(param) : Integer.parseInt(param);
		}else if(StringUtil.isNum(param) && ("1".equals(fileType) || fileType == null)){ //浮点数
			obj = (param.length() >= 7) ? new BigDecimal(param) : Double.parseDouble(param);
		}else{ //字符串
			obj = param.replace("'", "");
		}
		return obj;
	}
	
	/**
	 * 处理oracle数值型为小数时会省掉小数点前面的0的情况，在前面自动加上0 
	 * tangyj 2013-01-06
	 * @param columnTypeName  列类型名称
	 * @param columnValue 列值
	 * @param dbType 数据库类型
	 * @return 如：0.12
	 */
	private static String formatNumberValue(String columnTypeName,String columnValue,String dbType) {
		if("Oracle".equalsIgnoreCase(dbType)){
			if(columnValue !=null){
				columnValue = columnValue.trim();
				if(columnTypeName.equalsIgnoreCase("NUMBER") &&  columnValue !=null && (columnValue.startsWith(".") || columnValue.startsWith("-."))){
					columnValue=columnValue.replace(".", "0.");
				}
			}
		}
		return columnValue;
	}
	
	/**
	 * 预处理时，设置？号参数
	 * @param st PreparedStatement对象
	 * @param data 数据集合
	 * @throws Exception 抛出任何可能发生的异常
	 */
	public static void setObject(PreparedStatement st, List data, String domain)throws Exception{
		if(data != null){
			int size = data.size();
			
			for (int i = 0; i < size; i++) {
				Object obj = data.get(i);
				if (obj instanceof String){
					setString(st, obj, i, domain); 
				}else{
					st.setObject(i + 1, obj);
				}
			}
		}
	}
	
	/**
	 * 预处理时，设置String参数，解决问题:oracle varchar2(4000)只能插入666个中文，实际应该可插入2000个才正确
	 * @param st PreparedStatement对象
	 * @param obj 数据对象
	 * @param j ？号序号
	 * @throws Exception 抛出任何可能发生的异常
	 */
	public static void setString(PreparedStatement st, Object obj, int j, String domain)throws Exception{
		String s = (String)obj; //不能用StringUtil.toString(obj),因为会置空空串，导致clob字段初始插入时报错.
    	if("oracle".equals(DialectFactory.getDialect(domain).getDBType()))
    		st.setCharacterStream(j+1, new StringReader(s),s.length()); //sybase如果也用此名，碰到text字段，会报错，所以还是区分一下数据库类型
    	else
    		st.setObject(j+1, obj);
	}
	
	/**
	 * 针对?号传参的SQL，为确保打印日志时能打印真实值，所以这里进行?号替换
	 * @param sql 含?号的SQL
	 * @param list ?号对应的数据集合
	 * @param domain 数据库
	 * @return 替换后的SQL
	 */
	public static String getRealSql(String sql, List list, String domain){
		if(list == null || list.size()==0){
			return sql;
		}
		
		StringBuilder sb = new StringBuilder();
		Dialect dia = DialectFactory.getDialect(domain);
		String s[]=sql.split("\\?");
		int size = list.size()-1;
		for(int i=size; i>=0; i--){
			Object obj = list.get(i);
			if(obj == null){//空值
				s[i] = s [i] + "null";
			}else if(obj instanceof Number) { //数值类型
				s[i] = s[i] + obj;
			}else if(obj instanceof Date) { //时间类型
				s[i] = s[i] + dia.stringToDatetime(DateUtil.parseToString((Date)obj, "yyyy-MM-dd HH:mm:ss"));
			}else{ //字符串，太长则截取掉，SQL中不需要全量显示
				String str = obj.toString();
				s[i] = s[i] +"'" + (str.length() > 50 ? (str.substring(0, 50) + "...") : str) + "'"; 
			}
		}
		for(int i=0;i<s.length;i++){
			sb.append(s[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 执行前日志
	 * @param sql 执行的SQL
	 * @param list SQL？对应数据
	 * @param domain 数据源
	 * @return Map类型，执行后日志需要的参数
	 */
	public static Map<String,String> before(String sql, List list, String domain){
		String sMd5 = getUUID();//MD5Tool.getMD5String();
		String sql1 = (list == null)? sql : getRealSql(sql, list ,domain);
		String path = LogOperateUtil.logSQLBefor(sql1, sMd5, domain);
		long timeBefore = System.currentTimeMillis();
		Map<String, String> mp = new HashMap<String, String>();
		mp.put("sMd5", sMd5);
		mp.put("sql1", sql1);
		mp.put("path", path);
		mp.put("timeBefore", timeBefore+"");
		mp.put("domain", domain);
		return mp;
	}
	
	/**
	 * 执行前日志
	 * @param countSql 查总记录数SQL
	 * @param dataSql 查结果数据SQL
	 * @param list SQL？对应数据
	 * @param domain 数据源
	 * @return Map类型，执行后日志需要的参数
	 */
	public static Map<String,String> before(String countSql, String dataSql, List list, String domain){
		String sMd5 = getUUID();//MD5Tool.getMD5String();
		String sql1 = (list == null)? countSql : getRealSql(countSql, list ,domain);
		String sql2 = (list == null)? dataSql : getRealSql(dataSql, list ,domain);
		
		String path = LogOperateUtil.logSQLBefor(sql1, sMd5, domain);
		long timeBefore = System.currentTimeMillis();
		Map<String, String> mp = new HashMap<String, String>();
		mp.put("sMd5", sMd5);
		mp.put("sql1", sql1 + "\n" +sql2);
		mp.put("path", path);
		mp.put("timeBefore", timeBefore+"");
		mp.put("domain", domain);
		return mp;
	}
	
	/**
	 * 执行后日志
	 * @param params 记日志所需参数
	 * @param num 影响行数或结果行数
	 */
	public static void after(Map<String,String> params, int num){
		String sMd5 = params.get("sMd5");
		String sql1 = params.get("sql1");
		String path = params.get("path");
		String domain = params.get("domain");
		long timeBefore = Long.valueOf(params.get("timeBefore"));
		long iCostTime = LogOperateUtil.logSQLAfter(sql1, sMd5, timeBefore, num);
		//所有JDBC日志都不直接入库
		if(LogOperateUtil.isLogRuntime()){
			LogOperateUtil.logSQLToDb(sMd5, sql1, iCostTime, num, domain, path);
		}
	}

	/**
	 * 根据Data map插入数据
	 * @param table 表名
	 * @param map 字段名与数据
	 * @param pks 主键字段名，多个用逗号隔开。 此参给空时做insert操作，否则做update操作。
	 * @param domain 数据源
	 * @return 操作成功的记录数
	 */
	public static int saveByDataMap(String table, List<Data> map, String pks, String ... domain){
		if(map==null || map.size()==0){return 0;}
		
		String dm = checkArgs(domain, 0)? domain[0] : "";
		String sql = getSqlByDataTypeMap(table, map, pks, dm);
		List data = getDatasByDataTypeMap(map, pks, dm);
		return updateByPrepareStatement(sql, data, dm);
		//还需考虑oracle下的clob类型
	}
	
	/**
	 * 根据Data map 批量插入数据
	 * @param table 表名
	 * @param list 集合数据，集合内map放字段名与数据。(list内所有map的键必须一致，比如第1个map有A,B键，第二个map也得有A,B键，不能多也不能少。键对应的值可以给空。)
	 * @param pks 主键字段名，多个用逗号隔开。 此参给空时做insert操作，否则做update操作。
 	 * @param domain 数据源
	 * @return 操作成功的记录数
	 */
	public static int saveByDataMapList(String table, List<List<Data>> list, String pks, String ... domain){
		if(list==null || list.size()==0){return 0;}
		
		String dm = checkArgs(domain, 0)? domain[0] : "";
		String sql = getSqlByDataTypeMap(table, list.get(0), pks, dm);
		List<List> datas = new ArrayList<List>();
		for(List<Data> map : list){
			List data = getDatasByDataTypeMap(map, pks, dm);
			datas.add(data);
		}
		
		return updateByPrepareStatementBatch2(sql, datas, dm);
		//还需考虑oracle下的clob类型
	}
	
	/***
	 * 根据Data map拼操作语句
	 * @param table 表名
	 * @param map 字段名与数据
	 * @param pks 主键字段名，多个用逗号隔开。 此参数给值，则做update操作，否则做insert操作
	 * @param domain 数据源
	 * @return
	 */
	public static String getSqlByDataTypeMap(String table, List<Data> map, String pks, String domain){
		StringBuilder sb1 = new StringBuilder();
		
		Dialect dia = DialectFactory.getDialect(domain);
		if(StringUtil.checkStr(pks)){
			pks=","+pks+",";
			StringBuilder sb2 = new StringBuilder(" where 1=1");
			sb1.append("update ").append(table).append(" set ");
			
			for(Data entry : map){ 
				  String key = entry.getFiledName();
				  
				  if(pks.contains(","+key+",")){//说明是修改条件字段
					  sb2.append(" and ").append(key).append("= ? "); 
				  }else{
					  sb1.append(" ").append(key).append("= ");
					  if(entry.getDataType()==DataType.SYSDATE){ sb1.append(dia.getDate());}else{ sb1.append("?");} //取数据库系统时间
					  sb1.append(",");//","后边不要再打空格
				  }
			} 
			sb1.deleteCharAt(sb1.length()-1);
			sb1.append(sb2);
		}else{
			StringBuilder sb2 = new StringBuilder();
			sb1.append("insert into ").append(table).append("(");
			for(Data entry : map){ 
		          sb1.append(entry.getFiledName()).append(",");
		          if(entry.getDataType()==DataType.SYSDATE){ sb2.append(dia.getDate()).append(","); }else{ sb2.append("?,");} //取数据库系统时间
			} 
			sb1.deleteCharAt(sb1.length()-1);
			sb2.deleteCharAt(sb2.length()-1);
			
			sb1.append(")").append(" values (").append(sb2).append(")");
		}
		
		return sb1.toString();
	}
	
	/**
	 * 根据Data map 整理数据集合
	 * @param map 字段名与数据
	 * @param pks 主键字段名，多个用逗号隔开。 此参数给值，则做update操作，否则做insert操作
	 * @param domain 数据源
	 * @return
	 */
	public static List<Object> getDatasByDataTypeMap(List<Data> map, String pks, String domain){
		List<Object> datas = new ArrayList<Object>();
		if(StringUtil.checkStr(pks)){ //修改
			List<Object> datas2 = new ArrayList<Object>();
			pks=","+pks+",";

			for(Data entry : map){ 
				  Object val = entry.getVal();
				  if(entry.getDataType()==DataType.SYSDATE){continue;} //取数据库时间，跳过
				  String key = entry.getFiledName();
				  if(pks.contains(","+key+",")){//说明是修改条件字段
					  datas2.add(val);
				  }else{
					  datas.add(val);
				  }
			} 
			datas.addAll(datas2);
		}else{//新增
			for(Data entry : map){ 
				Object val = entry.getVal();
				if(entry.getDataType()==DataType.SYSDATE){continue;} //取数据库时间，跳过
				datas.add(val);
			} 
		}
		return datas;
	}
	
	/**检查数组中第n个参数是否为空*/
	public static boolean checkArgs(String args[], int index){
		return args!=null && args.length>index && StringUtil.checkStr(args[index]);
	}
	
	public static void main(String[] args) {
		System.out.println(queryForList("select * from A2 where F in ('ff  ')").size());
	}
}
