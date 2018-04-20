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
 * ����˵��:
 * 2012/3/28 gaotao ��updateByPrepareStatement updateByPrepareStatementBatch ��������,�÷�������ע��
 * 2012/6/20 gaotao ��ѯ�������������˶�Clob�ֶε��ض���ȡ��ʽ�������˶�Clob,text��д�뷽ʽ
 * 2012/7/28 pengjiewen ����queryForFetch��fetchNext����������ʵ�ַ���ץȡ����
 * 2012/9/26 qiaoqide �����Բ�ѯ���������
 * 2012/10/26 zhanweibin �޸�updateByPrepareStatementBatch()������ʹ��getUpdateCount()��ȡ���µļ�¼��
 * 2013/1/5 tangyj  �ڲ�ѯ�����У���oracle���number�ֶεĸ�����λΪ0��С�����д�������fetchNext1()�������÷���û����ȡ���ݿ����͡�
 * 2013/5/17 gaotao �����·����У����������ֶ��ύ���ع�
 * 2013/5/21 gaotao ����setObject���������ַ��������ý������⴦����Ȼoracle varchar2(4000),���ֻ�ܲ�666�����ģ���ʵ��Ӧ�ÿ��Բ�2000���Ŷ�
 * 2013/6/20 gaotao ����getRealSql�������������к�?�ŵ�SQL��д��־ʱ���滻����ʵֵ����װ��ִ��ǰ������־��
 * 2013/9/7 gaotao ������list�����Ĳ�ѯ������������list�����ķ�����ʹ�����һ�����ã�����һ���µĲ�ѯ��ҳ����;
 * 2014/4/7 gaotao insertByPrepareStatementBatch�����ع���1���3��Ϊ����ͨ�õ���ʱ�ĵ�һ�����Ӵ���
 */
public class DatabaseUtil {
	
	/**���Ʋ�ѯ���������*/
	private static final int TOTAL= StringUtil.toInteger(ConfigInit.Config.getProperty("DataBaseUtil_MaxResult", "10000"));
	
	/**
	 * ��ȡ����
	 * 
	 * @param domain ����Դ
	 * @return ���ݿ����Ӷ���
	 */
	public static synchronized Connection getConnection(String domain) {
		Connection connection = null;
		connection = StringUtil.checkStr(domain) ? DatabaseConnection
				.getConnection(domain) : DatabaseConnection.getConnection();
		return connection;
	}
	
	/**
	 * ��SQL��ѯ
	 * 
	 * @param sql ��ѯSQL
	 * @return ��ѯ�����
	 */
	public static List queryForList(String sql) {
		return queryForList(sql, "");
	}
	
	/**
	 * ��SQL��ѯ
	 * 
	 * @param sql ��ѯSQL	 
	 * @param domain ����Դ
	 * @return ��ѯ�����
	 */
	public static List queryForList(String sql, String domain) {
		return queryForList(sql, null, domain);
	}
	
	/**
	 * PrepareStatement SQL��ѯ
	 * 
	 * @param sql ��ѯSQL	
	 * @param list ��ѯ���� 
	 * @param domain ����Դ
	 * @return ��ѯ�����
	 */
	public static List queryForList(String sql, List list, String domain) {
		Map<String, String> params = before(sql, list, domain);
		
		List tempList = queryForList(sql, list, domain, params);
		
		after(params, tempList.size());
		
		return tempList;
	}
	
	
	/**
	 * Ԥ����ʽ��ѯ�������ڵ�ǰ�����־���������, ����jdbc��־����Ϊҵ��ϵͳ����
	 * 
	 * @param sql ��ѯSQL	
	 * @param list ��ѯ���� 
	 * @param domain ����Դ
	 * @return ��ѯ�����
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
				setObject(st, list, domain); //���ò���	
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
	 * ��ҳSQL��ѯ
	 * 
	 * @param sql ��ѯSQL
	 * @param page ��ǰҳ
	 * @param limit ÿҳ��¼��
	 * @return ��ѯ�����
	 */
	public static List queryForListByPage(String sql, int page, int limit) {
		return queryForListByPage(sql, page, limit, "");
	}
	
	/**
	 * ��ҳSQL��ѯ
	 * @param sql ��ѯSQL
	 * @param page ��ǰҳ
	 * @param limit ÿҳ��¼��
	 * @param domain ����Դ
	 * @return ��ѯ�����
	 */
	public static List queryForListByPage(String sql, int page, int limit, String domain) {
		return queryForListByPage(sql, page,limit, null, domain);
	}
	
	/**
	 * PrepareStatementԤ�����ҳ��ѯ�����SQlע������
	 * @param sql ��ѯSQL
	 * @param page ��ǰҳ
	 * @param limit ÿҳ��¼��
	 * @param list ��ѯ����
	 * @param domain ����Դ
	 * @return ��ѯ�����
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
			//����fromǰ��һ�����з�������һ���ո񣬻ᵼ��sybase��ҳ�����ﱨ��
			sql = sql.replace("\n", " ").replace("\r", " ");
			// ����
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
	 * ��ҳ��ѯ����union�ؼ��֣��������ִ����������Լ�д�����ķ�ҳSQL�������÷��������ʵ�֣�
	 * ���ⱻ��������ʵ�ֵķ�ҳ��װһ���˲�ѯ��Ӱ�����ܡ�
	 * �˷��������ԣ�
	 *  1,�˷�����������Sybase
	 *  2,���ڲ����е�SQL�Ѿ������˷�ҳ�߼���һ�����ݿ����ͷ����仯����Ӧ��SQL��ҲҪ���ġ�
	 *   ������취�������ѵ�DAO�����жϺ����ݿ����ͣ����ݲ�ͬ���ݿ����ͣ�д���ײ�ͬ��SQL���ٴ���˷����� 
	 * @param countSql ��ѯ�ܼ�¼����SQL
	 * @param dataSql ��ѯĳһҳ���ݵ�SQL
	 * @param list SQL��?�Ŷ�Ӧ�Ĳ���
	 * @param domain ����Դ
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
			/**���ܼ�¼��*/
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
			free(null, st, rs); //�ȹر��Ѿ����õĶ���
			if("0".equals(totalCount)){ //û�м�¼�������ؼ�����ѯ���ݽ����
				return tempList;
			}
			
			/**�����ݽ����*/
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
	 * �ѽ����ת��� List<Map>����
	 * @param tempList ����װ�ؽ����list
	 * @param domain ����Դ
	 * @param rs ���������
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
				map.put("totalCount", totalCount); //װ���ܼ�¼��
				map.put("iRecCount", totalCount); //װ���ܼ�¼��
			}
			tempList.add(map);
			if(rowId>TOTAL)break;//������ݳ���ָ����������ǿ���˳�ѭ��
		}
		
		return rowId;
	}
	
	
	/**
	 * �������ݿ�
	 * 
	 * @param sql ����SQL
	 * @return ��������
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
	 * �������ݿ�
	 * 
	 * @param sql ����SQL
	 * @return ��������
	 */
	public static int updateDateBase(String sql) {
		return updateDateBase(sql, "");
	}

	/**
	 * Ԥ����ʽ����,�����ڵ�ǰ�����־��Ϣ,��Ϊҵ��ϵͳ����
	 * @param sql ����SQL
	 * @param data ���Ų�����
	 * @param domain ����Դ
	 * @return ��������
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
			LogOperateUtil.logSQLError(e, domain, sql, "����־����");
		} finally {
			free(conn, st, null);
		}
		return num;
	}

	/**
	 * ��������
	 * @param sqlArray ������������
	 * @param domain ����Դ
	 * @return �ɹ���� 1�ɹ�������ʧ��
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
	 * ��������
	 * 
	 * @param sqlArray ������������
	 * @param domain ����Դ
	 * @return ��������
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
				// һ�����100��
				if ((i + 1) % 100 == 0) {
					num += sum(st.executeBatch());
					st.clearBatch();
					conn.commit();
				}
			}
			if(length % 100 > 0){ //��Ϊ������ִ�лᱨ��
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
	 * ���
	 * 
	 * @param arr ������������
	 * @return ������
	 */
	public static int sum(int[] arr) {
		int num = 0;
		for (int i = 0; i < arr.length; i++) {
			num += arr[i];
		}
		return num;
	}

	/**
	 * ��������
	 * 
	 * @param sqlArray SQL����
	 * @return ��������
	 */
	public static int updateBatchBase1(String[] sqlArray) {
		return updateBatchBase1(sqlArray, "");
	}

	/**
	 * ִ��SQL,�ѵ��н����Map���ͷ���
	 * 
	 * @param sql ��ѯSQL
	 * @return ���ݽ��
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
	 * ִ��SQL,�ѵ��н����Map���ͷ���
	 * 
	 * @param sql ��ѯSQL
	 * @return ���ݽ��
	 */
	public static Map<String, Object> queryForMap(String sql) {
		return queryForMap(sql, "");
	}

	/**
	 * ����JDBC��־��ȡ����,�˷��������ڱ�����־
	 * 
	 * @param domain ��ԭ
	 * @param path ����·��
	 * @param sql ִ��SQL
	 * @return ����
	 */
	public static String getKeyId(String domain, String path, String sql) {
		domain = StringUtil.checkStr(domain) ? domain : DialectFactory.getDefaultDatasrc();
		String key = DialectFactory.getPrimaryKeys(domain);// �ӻ���ȡ
		if (key == null) {//�����������ֵ
			Connection conn = getConnection(domain);
			Statement st = null;
			ResultSet rs = null;
			try {
				int cachePrimaryKeys = Integer.parseInt(ConfigInit.Config.getProperty(
						"cachePrimaryKeys", "100"));//������������
				Object objs[] = DialectFactory.getDialect(domain).getKeyId(
						conn,cachePrimaryKeys);// ����
				st = (Statement) objs[0];
				rs = (ResultSet) objs[1];
				if (rs != null && rs.next()) {
					key = rs.getString(1);
				}
				DialectFactory.setPrimaryKeys(domain, key);// ���軺��
			} catch (Exception e) {
				LogOperateUtil.logSQLError(e, domain, sql, path);
			} finally {
				free(conn, st, null);
			}
		}
		return key;
	}

	/**
	 * ��ȡ����
	 * tableName ��һ�����ò�����Ϊ��Ӧ��˾��ǰ�汾�����㼯�ɣ��ʱ����˲���
	 * @param domain ����Դ
	 * @return ����
	 */
	public static String getKeyId(String domain, String tableName) {
		String sql = "{call SP_GET_ID_EX2('" + tableName + "')}";
		Map<String, String> params = before(sql, null, domain);
		String key = getKeyId(domain, params.get("path"), sql);
		after(params, 1);
		return key;
	}
	
	/**
	 * ��ȡ����
	 * tableName ��һ�����ò�����Ϊ��Ӧ��˾��ǰ�汾�����㼯�ɣ��ʱ����˲���
	 * @param domain ����Դ
	 * @return ����
	 */
	public static String getKeyId(String tableName) {
		return getKeyId("", tableName);
	}
	
	/**��ȡһ������
	 * @param numbers Ҫ��ȡ�����ĸ���
	 * @param domain ����Դ����null����Ĭ������ԭΪ׼
	 * @param tableName ��һ����ʵ�ֲ�����Ϊ��Ӧ��˾��ǰ�汾�����㼯�ɣ��ʱ����˲��������Դ�null
	 * @return ��������
	 * */
	public static String[] getKeyIds(int numbers, String domain, String tableName){
		String sql = "������ȡ"+numbers+"������{call SP_GET_ID_EX2('" + tableName + "')}";
		Map<String, String> params = before(sql, null, domain);
		
		String[] keys = new String[numbers];
		for(int i=0; i<numbers; i++){
			keys[i] = getKeyId(domain, params.get("path"), sql);
		}
		
		after(params, 1);
		return keys;
	}

	/**
	 * ��ȡ���ֶ���Ϣ
	 * 
	 * @param sql ��ѯSQL
	 * @return �ֶ���Ϣ����
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
					//ĳЩ���ݿ� meta.getPrecision(i) ȡֵ����int�ķ�Χ���磺ĳЩ�汾 oralce COLB�ֶη���ֵ�� 4294967295 
					map.put("colPrecision", meta.getPrecision(i) + ""); 
				}catch(Exception ex){
					map.put("colPrecision", "-1");  
				}
				
				/*if(!"CLOB".equalsIgnoreCase(colType)){
					map.put("colPrecision", meta.getPrecision(i) + ""); //ĳЩ�汾�� oralce COLB�ֶη���ֵ�� 4294967295 ������int��Χ, ��Щ����-1
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
	 * ��ȡ���ֶ���Ϣ
	 * 
	 * @param sql ��ѯSQL
	 * @return �ֶ���Ϣ����
	 */
	public static List queryColumnsForList(String sql) {
		return queryColumnsForList(sql, null);
	}
	
	/**
	 * �ر�������Դ
	 * @param conn ���Ӷ���
	 * @param st ִ�ж���
	 * @param rs ���������
	 */
	public static synchronized void free(Connection conn, Statement st, ResultSet rs) {
		try {
			if (rs != null) { //&& !rs.isClosed() jdk1.5��û�д˷���
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			if (st != null) { //&& !st.isClosed() jdk1.5��û�д˷���
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
	 * �����ύ
	 * @param conn �������Ӷ���
	 * @throws SQLException �׳�SQL�쳣
	 */
	public static void commit(Connection conn) throws SQLException{
		if(conn!=null){
			conn.commit();
			conn.setAutoCommit(true);
		}
	}
	
	/**
	 * ����ع�
	 * @param conn ���Ӷ���
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
	 * ��Ԥ����ʽ���и���
	 * @param sql ����SQL���
	 * @param data data����������������ݿ�����������Ͷ�Ӧ����
	 * @param domain ����Դ
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
	 * ��Ԥ����ʽ����"����"����
	 * @param sql ��ʱSQLֻ����Ψһ��,���Ҫ��ÿ��������Σ�������Ϊ��
     * @param datas ��������
     * @param domain ����Դ
     * @return ���µ�����
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
				
				/* was �����£�sybase���ݿ�����쳣����
				st.addBatch();
				if ((i + 1) % 100 == 0) {//ÿ100��ִ��һ��
					st.executeBatch();
					num += st.getUpdateCount();//executeBatch()������oracle��ÿִ��һ���ķ���ֵΪ-2��ʹ��getUpdateCount()��ȡ���µļ�¼��
					st.clearBatch();//���
				}*/
				
				num+=st.executeUpdate();
				st.clearParameters();
			}
			
			/*
			if((size % 100) > 0){ //��Ϊ������ִ�лᱨ��
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
	 * ��Ԥ����ʽ����"����"����(������������)
	 * @param sql ��ʱSQL��Ψһ��,������������Ƿ�Ϊ�գ�����Ӧ��"?"�Ż�Ϊ"null"
     * @param datas ��������
     * @param domain ����Դ
     * @return ���µ�����
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
				//forѭ���У�ע�⼰ʱ�ر�
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
     * ��Ԥ����ʽ����"����"���룬��ΪupdateByPrepareStatementBatch�Ĳ��䣬�Զ��ύ
     * @param sql ����ʡ�Բ��������
     * @param datas ��������
     * @param domain ����Դ
     * @return ���µ�����
   */
    public static int insertByPrepareStatementBatch(String sql, List<List> datas, String domain){
		Map<String, String> params = before(sql, ((datas!=null&&datas.size()>0)?datas.get(0):null), domain);
		
        int updateCount =0;
        if(datas!=null && datas.size()>0 && datas.get(0).size()>0){
        	String sql2 = sql.substring(sql.indexOf("into "));
        	//����
   		    String tableName = sql2.substring(sql2.indexOf("into ")+5, sql2.indexOf('(')).trim();
            //�ֶ�
   		    String selFields = sql2.substring(sql2.indexOf("(")+1, sql2.indexOf(')')).trim();
   		    //�ֶ�ֵ
   		    String sql3 = sql.substring(sql.indexOf("values"));
		    String[] setfs = sql3.substring(sql3.indexOf('(') +1, sql3.lastIndexOf(')')).split(",");
		    
   		    //String[] setfs = sql.substring(sql.lastIndexOf('(') +1, sql.lastIndexOf(')')).split(",");//�����������ֶ�ʱ���ǲ���ֱ����sql.lastindexof("(")�ģ���Ϊ���������ݿ⺯����(
            Map<Integer,Integer> fields = new LinkedHashMap<Integer,Integer>();
            Map<Integer,Integer> columnTypes = new LinkedHashMap<Integer,Integer>();
            //��ʶ?�������ֶ����
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
                	   LogOperateUtil.logException(ex, "�������ʱ����insertִ��ʧ�ܣ�SQL��:"+tempSql);
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
	 * sql�е�"?"�ţ������Ӧ����Ϊnull,��ת��Ϊ"null",ͬʱ�����ݼ����е�nullֵɾ��
	 * @param sql SQL���
	 * @param list ��������
	 * @return ȥnull��SQL
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
	 * CLOB�ֶζ�ȡ
	 * @param obj ���ݶ���
	 * @return ����ı�
	 */
	private static String readClob(Object obj){
		if(obj==null)return "";
		StringBuffer mystr=new StringBuffer(""); 
		String str = "";
		BufferedReader a=null;
		try{
			a=new BufferedReader(((Clob)obj).getCharacterStream()); //���ַ����ķ�ʽ����BufferedReader 
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
	 * ClOB�ֶ�д��
	 * @param tableName ����
	 * @param where ����ʱ��where������ȷ��Ҫ���µ���
	 * @param cols �ֶ�������
	 * @param val ֵ����
	 * @param domain ����Դ
	 */
	public static void writeClobOrText(String tableName, String where, String cols[], String val[], String domain){
		String sql="����"+tableName+"���CLOB��Text�ֶ�";
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
	 * ClOB�ֶ�����д��
	 * @param list ���ݼ���
	 * @param domain ����Դ
	 */
	public static void writeClobOrTextBatch(List list, String domain){
		String sql = "�������ı��CLOB��Text�ֶ�";
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
				//forѭ���У�ע�⼰ʱ�ر�
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
	 * ����ץȡ��ѯ
	 * ���ʹ�ô˷������ǵ���ʹ�����һ��Ҫ�ǵ��ֶ�����������Դ�Ĺرգ�ͨ��rs�ÿɵõ����Ӷ���
	 * ���磺DatabaseUtil.free(rs.getStatement().getConnection(),rs.getStatement(),rs);
	 * @param sql ��ѯSQL
	 * @param domain ����Դ
	 * @param fs һ�����ץȡ����
	 * @return �����
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
			//ÿ��ץȡ������
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
	 * ����ץȡ��ѯ
	 * ���ʹ�ô˷������ǵ���ʹ�����һ��Ҫ�ǵ��ֶ�����������Դ�Ĺرգ�ͨ��rs�ÿɵõ����Ӷ���
	 * ���磺DatabaseUtil.free(rs.getStatement().getConnection(),rs.getStatement(),rs);
	 * @param sql ��ѯSQL
	 * @param fs һ�����ץȡ����
	 * @return �����
	 */
	public static ResultSet queryForFetch(String sql, int fs){
		return queryForFetch(sql,"",fs);
	}

	/**
	 * 
	 * ����ץȡ���м���
	 * @param rs �����
	 * @param rowId �к�
	 * @param sql ִ�е�SQL
	 * @param domain ����Դ
	 * @return һ�н��
	 */
	public static Map fetchNext(ResultSet rs, int rowId, String sql, String domain){
		Map<String, String> map = null;
		String colValue =null;
		try {
			//����һ��ʱreturn�У�û����null
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
	 * ȡUUID����
	 * @return UUID����
	 */
	public static String getUUID(){
		String uuId=java.util.UUID.randomUUID().toString().replaceAll("-", "");
		return uuId;
	}

	/**
	 * ���ݲ�����ȡin(?)���
	 * @param param ����ƴ�����Ĳ�����  
	 * @param list ת����Ĳ�����װ��˼���
	 * @return �滻���SQL,ÿ������ת����?��
	 */
	public static String inParameterLoader(String param,List list){
		return inParameterLoader(param, list, null);
	}
	
	/**
	 * ���ݲ�����ȡin(?)���
	 * @param param ����ƴ�����Ĳ�����  
	 * @param list ת����Ĳ�����װ��˼���
	 * @param filedType �������� 1����ֵ������Ϊ�ַ��� 
	 * @return �滻���SQL,ÿ������ת����?��
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
	 * �ж��ַ�������
	 * @param param ���� 
	 * @param fileType �������� 1���߿�����ֵ������Ϊ�ַ���
	 * @return �滻��Ĳ���
	 */
	public static Object getTargetType(String param, String fileType){
		Object obj;
		if(StringUtil.isInteger(param) && ("1".equals(fileType) || fileType == null)){ //���� (long�����19λ��)
			obj = (param.length() >= 10) ? Long.parseLong(param) : Integer.parseInt(param);
		}else if(StringUtil.isNum(param) && ("1".equals(fileType) || fileType == null)){ //������
			obj = (param.length() >= 7) ? new BigDecimal(param) : Double.parseDouble(param);
		}else{ //�ַ���
			obj = param.replace("'", "");
		}
		return obj;
	}
	
	/**
	 * ����oracle��ֵ��ΪС��ʱ��ʡ��С����ǰ���0���������ǰ���Զ�����0 
	 * tangyj 2013-01-06
	 * @param columnTypeName  ����������
	 * @param columnValue ��ֵ
	 * @param dbType ���ݿ�����
	 * @return �磺0.12
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
	 * Ԥ����ʱ�����ã��Ų���
	 * @param st PreparedStatement����
	 * @param data ���ݼ���
	 * @throws Exception �׳��κο��ܷ������쳣
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
	 * Ԥ����ʱ������String�������������:oracle varchar2(4000)ֻ�ܲ���666�����ģ�ʵ��Ӧ�ÿɲ���2000������ȷ
	 * @param st PreparedStatement����
	 * @param obj ���ݶ���
	 * @param j �������
	 * @throws Exception �׳��κο��ܷ������쳣
	 */
	public static void setString(PreparedStatement st, Object obj, int j, String domain)throws Exception{
		String s = (String)obj; //������StringUtil.toString(obj),��Ϊ���ÿտմ�������clob�ֶγ�ʼ����ʱ����.
    	if("oracle".equals(DialectFactory.getDialect(domain).getDBType()))
    		st.setCharacterStream(j+1, new StringReader(s),s.length()); //sybase���Ҳ�ô���������text�ֶΣ��ᱨ�����Ի�������һ�����ݿ�����
    	else
    		st.setObject(j+1, obj);
	}
	
	/**
	 * ���?�Ŵ��ε�SQL��Ϊȷ����ӡ��־ʱ�ܴ�ӡ��ʵֵ�������������?���滻
	 * @param sql ��?�ŵ�SQL
	 * @param list ?�Ŷ�Ӧ�����ݼ���
	 * @param domain ���ݿ�
	 * @return �滻���SQL
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
			if(obj == null){//��ֵ
				s[i] = s [i] + "null";
			}else if(obj instanceof Number) { //��ֵ����
				s[i] = s[i] + obj;
			}else if(obj instanceof Date) { //ʱ������
				s[i] = s[i] + dia.stringToDatetime(DateUtil.parseToString((Date)obj, "yyyy-MM-dd HH:mm:ss"));
			}else{ //�ַ�����̫�����ȡ����SQL�в���Ҫȫ����ʾ
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
	 * ִ��ǰ��־
	 * @param sql ִ�е�SQL
	 * @param list SQL����Ӧ����
	 * @param domain ����Դ
	 * @return Map���ͣ�ִ�к���־��Ҫ�Ĳ���
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
	 * ִ��ǰ��־
	 * @param countSql ���ܼ�¼��SQL
	 * @param dataSql ��������SQL
	 * @param list SQL����Ӧ����
	 * @param domain ����Դ
	 * @return Map���ͣ�ִ�к���־��Ҫ�Ĳ���
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
	 * ִ�к���־
	 * @param params ����־�������
	 * @param num Ӱ��������������
	 */
	public static void after(Map<String,String> params, int num){
		String sMd5 = params.get("sMd5");
		String sql1 = params.get("sql1");
		String path = params.get("path");
		String domain = params.get("domain");
		long timeBefore = Long.valueOf(params.get("timeBefore"));
		long iCostTime = LogOperateUtil.logSQLAfter(sql1, sMd5, timeBefore, num);
		//����JDBC��־����ֱ�����
		if(LogOperateUtil.isLogRuntime()){
			LogOperateUtil.logSQLToDb(sMd5, sql1, iCostTime, num, domain, path);
		}
	}

	/**
	 * ����Data map��������
	 * @param table ����
	 * @param map �ֶ���������
	 * @param pks �����ֶ���������ö��Ÿ����� �˲θ���ʱ��insert������������update������
	 * @param domain ����Դ
	 * @return �����ɹ��ļ�¼��
	 */
	public static int saveByDataMap(String table, List<Data> map, String pks, String ... domain){
		if(map==null || map.size()==0){return 0;}
		
		String dm = checkArgs(domain, 0)? domain[0] : "";
		String sql = getSqlByDataTypeMap(table, map, pks, dm);
		List data = getDatasByDataTypeMap(map, pks, dm);
		return updateByPrepareStatement(sql, data, dm);
		//���迼��oracle�µ�clob����
	}
	
	/**
	 * ����Data map ������������
	 * @param table ����
	 * @param list �������ݣ�������map���ֶ��������ݡ�(list������map�ļ�����һ�£������1��map��A,B�����ڶ���mapҲ����A,B�������ܶ�Ҳ�����١�����Ӧ��ֵ���Ը��ա�)
	 * @param pks �����ֶ���������ö��Ÿ����� �˲θ���ʱ��insert������������update������
 	 * @param domain ����Դ
	 * @return �����ɹ��ļ�¼��
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
		//���迼��oracle�µ�clob����
	}
	
	/***
	 * ����Data mapƴ�������
	 * @param table ����
	 * @param map �ֶ���������
	 * @param pks �����ֶ���������ö��Ÿ����� �˲�����ֵ������update������������insert����
	 * @param domain ����Դ
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
				  
				  if(pks.contains(","+key+",")){//˵�����޸������ֶ�
					  sb2.append(" and ").append(key).append("= ? "); 
				  }else{
					  sb1.append(" ").append(key).append("= ");
					  if(entry.getDataType()==DataType.SYSDATE){ sb1.append(dia.getDate());}else{ sb1.append("?");} //ȡ���ݿ�ϵͳʱ��
					  sb1.append(",");//","��߲�Ҫ�ٴ�ո�
				  }
			} 
			sb1.deleteCharAt(sb1.length()-1);
			sb1.append(sb2);
		}else{
			StringBuilder sb2 = new StringBuilder();
			sb1.append("insert into ").append(table).append("(");
			for(Data entry : map){ 
		          sb1.append(entry.getFiledName()).append(",");
		          if(entry.getDataType()==DataType.SYSDATE){ sb2.append(dia.getDate()).append(","); }else{ sb2.append("?,");} //ȡ���ݿ�ϵͳʱ��
			} 
			sb1.deleteCharAt(sb1.length()-1);
			sb2.deleteCharAt(sb2.length()-1);
			
			sb1.append(")").append(" values (").append(sb2).append(")");
		}
		
		return sb1.toString();
	}
	
	/**
	 * ����Data map �������ݼ���
	 * @param map �ֶ���������
	 * @param pks �����ֶ���������ö��Ÿ����� �˲�����ֵ������update������������insert����
	 * @param domain ����Դ
	 * @return
	 */
	public static List<Object> getDatasByDataTypeMap(List<Data> map, String pks, String domain){
		List<Object> datas = new ArrayList<Object>();
		if(StringUtil.checkStr(pks)){ //�޸�
			List<Object> datas2 = new ArrayList<Object>();
			pks=","+pks+",";

			for(Data entry : map){ 
				  Object val = entry.getVal();
				  if(entry.getDataType()==DataType.SYSDATE){continue;} //ȡ���ݿ�ʱ�䣬����
				  String key = entry.getFiledName();
				  if(pks.contains(","+key+",")){//˵�����޸������ֶ�
					  datas2.add(val);
				  }else{
					  datas.add(val);
				  }
			} 
			datas.addAll(datas2);
		}else{//����
			for(Data entry : map){ 
				Object val = entry.getVal();
				if(entry.getDataType()==DataType.SYSDATE){continue;} //ȡ���ݿ�ʱ�䣬����
				datas.add(val);
			} 
		}
		return datas;
	}
	
	/**��������е�n�������Ƿ�Ϊ��*/
	public static boolean checkArgs(String args[], int index){
		return args!=null && args.length>index && StringUtil.checkStr(args[index]);
	}
	
	public static void main(String[] args) {
		System.out.println(queryForList("select * from A2 where F in ('ff  ')").size());
	}
}
