package pub.dbDialectFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import util.StringUtil;

/**
 * DB2 ����
 * 
 * @author gaotao 2011-08-09
 */
class DB2Dialect implements Dialect {
	/**
	 * �洢���̷�ҳ����
	 */
	public Object[] getDataByCallableStatement(Connection conn, String sql,
			int page, int limit) throws SQLException {
		ResultSet rs = null;
		int rows = 0;
		Statement sta = conn.createStatement();
		rs = sta.executeQuery("select count(*)  from ( " + sql + ")");
		if (rs != null && rs.next()) {
			rows = rs.getInt(1);
		}
		
		DialectTool.free(null, sta, rs); //�ȹر��Ѿ����õĶ���
		
		String v_sql = "select tem.*,"
				+ rows
				+ " as \"iRecCount\","
				+ rows
				+ " as \"totalCount\" from (select t.*,row_number() over() as row from ("
				+ sql + ") t) tem where tem.row between "
				+ ((page * limit) - limit + 1) + " and " + (page * limit);
		sta = conn.createStatement();
		rs = sta.executeQuery(v_sql);
		return new Object[] { sta, rs };
	}

	/**
	 * preparestatement ��ҳ��ѯ
	 */
	public Object[] getDataByPageEoms(Connection conn, String sql, int page,
			int limit, List list) throws SQLException {
		
		/**���ܼ�¼��*/
		String tempSql = DialectTool.getCountSql(sql);
		PreparedStatement sta = conn.prepareStatement(tempSql);
		int size = 0;
		if(list!=null){
			size = list.size();
			for(int i=0; i<size; i++){
				sta.setObject(i+1, list.get(i));
			}
		}
		String rows = "0";
		ResultSet rs = sta.executeQuery();
		if (rs != null && rs.next()) {
			rows = rs.getString(1);
		}
		DialectTool.free(null, sta, rs); //�ȹر��Ѿ����õĶ���
		if("0".equals(rows)){ //û�м�¼�������ؼ�����ѯ���ݽ����
			return null;
		}
		
		/**�����ݽ����*/
		tempSql = getPageSql(rows, sql, page, limit);
		sta =conn.prepareStatement(tempSql.toString());
		if(list!=null){
			for(int i=0; i<size; i++){
				sta.setObject(i+1, list.get(i));
			}
		}
		rs = sta.executeQuery();
		return new Object[] { sta, rs };
	}
	
	/**
	 * ���ɷ�ҳSQL
	 * */
	private String getPageSql(String rows, String sql, int page, int limit){
		
		StringBuilder v_sql = new StringBuilder();
		v_sql.append("select ").append(rows);
		v_sql.append(" as \"iRecCount\", ");
		v_sql.append(rows);
		v_sql.append(" as \"totalCount\", tm2.* from (select rownum as rn, tm1.* from (");
		v_sql.append(sql);
		v_sql.append(" ) tm1 where rownum <="); //���кŹ��˷����ڲ��Ӳ�ѯ���ܽ�һ�������Ӳ�ѯ������������
		v_sql.append(page*limit);
		v_sql.append(") tm2 ");
		v_sql.append(" where rn >= "); //����������ܷŵ��ڲ㣬��Ȼ�鲻������
		v_sql.append((page-1)*limit+1);
		
		return v_sql.toString();
	}

	/**
	 * ȡ����
	 * ��ͬ���ݿ⣬ȡ����������һ�£�sybase,mysql�����ô洢���̣�oracle,DB2������
	 * ͬ��ҳ����һ����Statment��������ڱط���ʵ������ʵ����
	 * ͬ��ҳ����һ����Ϊ�˱�֤�ܹ���DataBaseUtilͳһ�ر����ݿ�������󣬹�Ҳ����һ����������
	 * ������2��Ԫ�أ���һ��Ԫ����Statement���ڶ���Resultset
	 * cachePrimaryKeys:���������ĸ������˲�����Խ�mysql,sybase��Ч
	 */
	public synchronized Object[] getKeyId(Connection conn, int cachePrimaryKeys)
			throws SQLException {
		String vSql = " select GLOBALSEQ.nextVal from GLOBALTABLE ";
		Statement sta = conn.createStatement();
		ResultSet rs = sta.executeQuery(vSql);
		return new Object[] { sta, rs };
	}

	/**
	 * �޸�Clob��text���ı��ֶ�
	 */
	public Object[] writeClobOrText(Connection conn,String tableName,String where,String cols[],String val[])throws Exception{
		
		return null;
	}
	
	public String getDate() {
		return " CURRENT TIMESTAMP ";
	}

	public String getSecondsBetweenDays(String dateColumnName) {
		return "timestampdiff(2,char(CURRENT TIMESTAMP-timestamp("
				+ dateColumnName + ")))";
	}

	public String getUpperFunction(String columnName) {
		return "UPPER(" + columnName + ")";
	}

	public String isNull(String property, String o) {
		return "COALESCE(" + property + "," + o + ")";
	}

	public String stringToDate(String str) {
		return "'" + str + "'";
	}

	public String stringToDatetime(String str) {
		return "'" + str + "'";
	}

	public String subString(String str, int startIndex, int endIndex) {
		return "substr(" + str + "," + new Integer(startIndex).toString() + ","
				+ new Integer(endIndex).toString() + ")";
	}

	public String getDateValueDay(String col) {
		return "DATE(" + col + ")";
	}

	public String getDateValueHour(String col) {
		return "concat(HOUR(" + col + "),':00')";
	}

	public String getGroupByDay(String col) {
		return "DATE(" + col + ")";
	}

	public String getGroupByHour(String col) {
		return "HOUR(" + col + ")," + "DATE(" + col + ")";
	}

	public String[] getIdentityKey(String primaryKeyCol, String domain) {
		return new String[] { "", "" };
	}

	// ��ʱδʵ��
	public String datetimeTostring(String col, String format) {
		return "";
	}

	/**
	 * �������ݿ���ַ�����ƴ�ӽ�� sybase �� oracle 'a'||'b' = 'ab' mysql
	 * concat������concat('a','b') = 'ab'
	 * 
	 * @author ̷����
	 * @return
	 */
	public String getConcatResult(String... strs) {
		String result = "";
		for (int i = 0; i < strs.length; i++) {
			if (i > 0) {
				result += "||";
			}
			result += strs[i];
		}
		return result;
	}

	/**
	 * �������ݿ���ַ�����ƴ�ӽ��
	 * @param splitChar ƴ��ʱ���м���Զ���ļ����
	 * @param strs ����ֶ������൱��һ������
	 * @return ƴ�Ӵ��Ľ��
	 */
	public String getConcatResult2(String splitChar, String... strs) {
		String result = "";
		for (int i = 0; i < strs.length; i++) {
			if (i > 0) {
				result += ("||'" + splitChar + "'||");
			}
			result += strs[i];
		}
		return result;
	}
	
	/**
	 * ���ݿ����������ת�ַ�������
	 * 
	 * @param ��Ҫת��������
	 * @author ̷����
	 * @return
	 */
	public String convertNumberToString(String number) {
		return "char(" + number + ")";
	}

	public String cancelOrderBy(String sql) {
		return sql;
	}

	/**
	 * ���ȴ�Ӧ�û���ȡ������oracle,DB2������Ӧ���ڻ�������������Ҫ��������,�ʼӴ˷���
	 * @param domain ����ԴID
	 * @return
	 */
	public String getInitPrimaryKeys(String domain) {
		return null;
	}

	/**
	 * ���ݿ���ַ�������ת��Ϊ��������
	 * 
	 * INT�����������ͳ����е����֡��ַ����������ڡ�ʱ���������ʾ
	 * 
	 * @param ��Ҫת�����ֶ�
	 * @autho Zhanweibin 2011-12-27
	 * @return
	 */
	public String convertStringToInt(String column) {
		return "INT('" + column + "')";
	}

	/**
	 * �õ����ݿ������
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return ���ݿ�Сд����
	 */
	public String getDBType() {
		return "db2";
	}
	
	/**
	 * ��ȡ���ݿ��û�����Ϣ
	 * @author tanjianwen
	 * @return �û�����Ϣ��ѯ���
	 */
	public String getUserTableInfo(String tableNames,  String ... args) {
		return "";
	}
	
	public String selectSystemTable(Map map) {
		return "";
	}
	
	public String getDBTblFieldType(String fieldType, String precNum) {
		return "";
	}
	
	public String createTable(String tableName, List<Map> list) {
		return "";
	}
	
	/**
	 * �����
	 * @param tableName ����
	 * @param data ������
	 * @return
	 */
	public String addField(String tableName, List<Map> dataList) {
		return "";
	}
	
	/**
	 * �޸���
	 * @param tableName ����
	 * @param dataList ������
	 * @return
	 */
	public String[] editField(String tableName, List<Map> dataList) {
		return new String[1];
	}
	
	/**
	 * ɾ��Ҫ�޸ĵ���
	 * @param tableName
	 * @param dataList
	 * @return
	 */
	public String[] deleteEditField(String tableName, List<Map> dataList) {
		return new String[0];
	}
	
	/**
	 * ɾ����
	 * @param tableName
	 * @param dataList
	 * @return
	 */
	public String[] deleteField(String tableName, List<Map> dataList) {
		return new String[0];
	}
	
	/**
	 * ���Ʊ�ṹ,�����±�,��ͬʱ��������
	 * @param fromTable Դ�����
	 * @param toTable �±����
	 * @param fields ��Ҫ���Ƶ��ֶΣ�����ֶ��ö��Ÿ��������û�У������������ֶ�
	 * @param where �и��������������������ݣ�û��������������Ʊ�ṹ
	 */
	public String copyTableStructure(String fromTable, String toTable,String fields,String where) {
		//��: create table table_name_new as select * from table_name_old where 1=2
		StringBuffer sql=new StringBuffer("create table ");
		sql.append(toTable)
		.append(" as ")
		.append(" select ")
		.append(StringUtil.checkStr(fields)?fields:"*")
		.append(" from ")
		.append(fromTable)
		.append(" where ")
		.append(StringUtil.checkStr(where)?where:" 1=2 ");
		return sql.toString();
	}
	
	/**
	 * ���Ʊ�ṹ,�����±�,��ͬʱ��������
	 * @param fromTable Դ�����
	 * @param toTable �±����
	 * @param fields ��Ҫ���Ƶ��ֶΣ�����ֶ��ö��Ÿ��������û�У������������ֶ�
	 * @param where �и��������������������ݣ�û��������������Ʊ�ṹ
	 */
	public String copyTempTableStructure(String fromTable, String toTable,String fields,String where) {
		//��: create table table_name_new as select * from table_name_old where 1=2
		StringBuffer sql=new StringBuffer("create GLOBAL TEMPORARY table ");
		sql.append(toTable)
		.append(" as ")
		.append(" select ")
		.append(StringUtil.checkStr(fields)?fields:"*")
		.append(" from ")
		.append(fromTable)
		.append(" where ")
		.append(StringUtil.checkStr(where)?where:" 1=2 ");
		return sql.toString();
	}
	
	/**
	 * ��ȡ����Ƿ������ͬ�ı����Ƶ�sql���
	 */
	public String getCheckTableSql(String tableName, String ... args) {
		String sql="";
/*		sql = "select 1 from sysobjects t where t.name = '"
			+ tableName + "'";*/
		return sql;
	}
	
	/**
	 * ��ȡ����Ƿ������ͬ�ı����Ƶ�sql���,����pin in���
	 */
	public String getCheckTablesSql() {
		String sql="";
		//sql = "select name as \"tableName\" from sysobjects t where t.name ";
		return sql;
	}
	
	public String indexOf(String column, String str) {
		// TODO Auto-generated method stub
		return null;
	}

	public String subString(String column, String startExp, String endExp) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * �����ֶ�����,���ʹ��
	 * @param ftype ��������
	 * @param flen �ֶγ���
	 * @param decpos С��λ����
	 * @return
	 */
	public String parseType(String ftype,int flen,int decpos){
		return "";
	}
	
	/**
	 * �޸���
	 * @param tableName ����
	 * @param dataList ������
	 * @return
	 */
	public String[] modifyField(String tableName, List<Map> dataList) {
		return null;
	}
	/**
	 * ������
	 * @param tableName ����
	 * @param dataList ������
	 * @return
	 */
	public String[] insertField(String tableName, List<Map> dataList) {
		return null;
	}
	
	public String charIndex(String str,String str1){
		return "instr("+str1+","+str+")";
	}
	
	public String dataLength(String str) {
		return "length("+str+")";
	}
	
	/**
	 * �õ��޸�����sql
	 * @param map
	 * @return
	 */
	public String getRenameColumnSql(Map map) {
		return "";
	}

	/**
	 * insert ���ƴ�������ֶΣ�ȡֵSQL���֣��˷�����ͨ�ñ�2.0����
	 * @param tableName ����
	 * @return �������3��Ԫϵ�������ʵ�����ʵ�� 
	 */
	public String[] getIncrementSQL(String tableName) {
		return new String []{
				"",//"begin declare a number(15); begin a:= SP_GET_INCREMENT('"+tableName+"'); ",
				"SP_GET_INCREMENT('"+tableName+"')",
				""//"; end; end;"
			};
	}
	
	/**
	 * ��ȡ����ֶζ���Ĭ�ϳ���
	 * @param ftype
	 * @param flen
	 * @param decpos
	 * @return
	 */
	public int getDgtDefaultFieldsLen(String ftype,int flen,int decpos){
		int fieldLen = flen;
		if(ftype.equals("1")){
			fieldLen = (flen == 0 && decpos == 0)? 22 : flen;
		}
		return fieldLen;
		
	}

	public String getSecondsBetween(String dateColumnName1, String dateColumnName2) {
		return "abs(timestampdiff(2,char(timestamp("+dateColumnName1+")-timestamp("
				+ dateColumnName2 + "))))";
	}
	
	public String[] replaceSpecialCharForLike(String exp, String value){
		value = value.replace("_", "\\_").replace("%", "\\%");
		return new String[]{exp.replace("?", value), " escape '\\' "};
	}
	
	/**
	 * ���������ֶμ���������
	 * @param beginColumnName ��ʼʱ��
	 * @param endColumnName ����ʱ��
	 * @return �������
	 * @author tangxiaolong
	 * @version 2013-11-1
	 */
	public String getSecondsBetweenDays(String beginColumnName, String endColumnName){
		return "timestampdiff(2, char(timestamp(" + endColumnName 
					+ ") - timestamp(" + beginColumnName + ")))";
	}
	
	/**
	 * ��format��Ӧ��ʱ�䵥λ��������Ӧ��ֵ���õ�һ���µ�ʱ�䡣
	 * ��oracle��3�������ֱ���� "sysdate"��"10","year", �����ڵ�ǰʱ��Ļ����ϼ�10��
	 * @param col �����ֶ�����
	 * @param num ������ֵ
	 * @param format ʱ�䵥λ (��������ֵ�� year, month, day, hour, minute, second)
	 * @return ������
	 */
	public String addDateTime(String col, String num, String format){
		if("yyyy".equalsIgnoreCase(format) || "yy".equalsIgnoreCase(format)){
			format = "year";
		}else if("MM".equals(format)){
			format = "month";
		}else if("dd".equalsIgnoreCase(format)){
			format = "day";
		}else if("hh24".equalsIgnoreCase(format) || "hh".equalsIgnoreCase(format)){
			format = "hour";
		}else if("mm".equals(format) || "mi".equalsIgnoreCase(format)){
			format = "minute";
		}else if("ss".equalsIgnoreCase(format)){
			format = "second";
		}
		
		String exp;
		if("year".equalsIgnoreCase(format) || "month".equalsIgnoreCase(format)){
			exp = col + " + numtoyminterval(" + num + ", '" + format + "')"; 
		}else{
			exp = col + " + numtodsinterval(" + num + ", '" + format + "')";
		}
		return exp;
	}
}
