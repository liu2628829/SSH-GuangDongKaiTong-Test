package pub.dbDialectFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.servlet.ConfigInit;
import util.StringUtil;

/**
 * Mysql ����
 * 
 * @author gaotao 2011-08-09
 */
class MysqlDialect implements Dialect {
	/**
	 * �洢���̷�ҳ����
	 */
	public Object[] getDataByCallableStatement(Connection conn, String sql,
			int page, int limit) throws SQLException {
		Statement sta = conn.prepareCall("{call GetDataByPageEoms(?,?,?)}");
		CallableStatement callsta = (CallableStatement) sta;
		callsta.setString(1, sql);
		callsta.setInt(2, (page - 1) * limit);
		callsta.setInt(3, limit);
		ResultSet rs = callsta.executeQuery();
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
		if(list != null){
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
		sta =conn.prepareStatement(tempSql);
		if(list != null){
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
		String tempSQL = sql.toLowerCase();
		StringBuilder v_sql = new StringBuilder();
		
		if(tempSQL.contains(" union ")){ //�����union �����Ϸ�ʽֱ�Ӱ�һ��
			
			v_sql.append("select ").append(rows);
			v_sql.append(" as \"iRecCount\", ");
			v_sql.append(rows);
			v_sql.append(" as \"totalCount\",tm2.* from (");
			v_sql.append(sql);
			v_sql.append(" ) tm2 limit ");
			v_sql.append((page-1)*limit);
			v_sql.append(",");
			v_sql.append(limit);
			
		}else{ //û��union�������SQL
			
			v_sql.append(sql);
			//�ܼ�¼������
			int index = tempSQL.indexOf(" from ");
			String part = (", "+ rows +" as \"iRecCount\", "+ rows + " as \"totalCount\" ");
			v_sql.insert(index, part);
			//where���֣��ӷ�ҳ����
			part = " limit "+((page-1)*limit) +"," +limit;
			v_sql.append(part);
			
		}
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
	public synchronized Object[] getKeyId(Connection conn,int cachePrimaryKeys) throws SQLException {
		Statement sta = conn.prepareCall("{call SP_GET_ID_EX2(?)}");
		CallableStatement callsta = (CallableStatement) sta;
		callsta.setInt(1, cachePrimaryKeys);
		ResultSet rs = callsta.executeQuery();
		return new Object[] { sta, rs };
	}
	
	/**
	 * �޸�Clob��text���ı��ֶ�
	 */
	public Object[] writeClobOrText(Connection conn,String tableName,String where,String cols[],String val[])throws Exception{
		StringBuffer sql=new StringBuffer("update ");
		sql.append(tableName).append(" set ");
		for(int i=0;i<cols.length;i++){
			sql.append(cols[i]).append(" = ?");
			if(i!=cols.length-1)sql.append(",");
		}
		sql.append(" where ").append(where);
		
		PreparedStatement st= conn.prepareStatement(sql.toString()); 
		for(int i=0;i<val.length;i++){
			st.setString(i+1, val[i]);
		}
		st.executeUpdate();
		return new Object[] { st, null };
	}
	
	public String getDate() {
		return "now()";
	}

	public String isNull(String property, String o) {
		return "IFNULL(" + property + "," + o + ")";
	}

	public String subString(String str, int startIndex, int endIndex) {
		return "subString(" + str + "," + new Integer(startIndex).toString()
				+ "," + new Integer(endIndex - startIndex + 1).toString() + ")";
	}

	public String stringToDatetime(String str) {
		return "'" + str + "'";
	}

	public String stringToDate(String str) {
		return "'" + str + "'";
	}

	public String getSecondsBetweenDays(String dateColumnName) {
		return "(UNIX_TIMESTAMP( now() ) - UNIX_TIMESTAMP(" + dateColumnName
				+ "))";
	}

	public String getUpperFunction(String columnName) {
		return "UPPER(" + columnName + ")";
	}

	public String getDateValueHour(String col) {
		return "concat(hour(" + col + "),':00')";
	}

	public String getGroupByHour(String col) {
		return "hour(" + col + "),DATE_FORMAT(" + col + ",'%Y/%m/%d')";
	}

	public String getDateValueDay(String col) {
		return "DATE_FORMAT(" + col + ",'%Y/%m/%d')";
	}

	public String getGroupByDay(String col) {
		return "DATE_FORMAT(" + col + ",'%Y/%m/%d')";
	}

	public String[] getIdentityKey(String primaryKeyCol, String domain) {
		return new String[] { "", "" };
	}

	/**
	 * update by Zhanweibin 2011-12-28
	 * ������yyyy-MM-dd��ʽ����
	 */
	public String datetimeTostring(String col, String format) {
		
		if (StringUtil.checkStr(format)) {
			if("yyyy-MM-dd".equals(format))
				format = "%Y-%m-%d";
			//add by liuchaobiao 2012/9/14
			else if ("yyyy-MM".equals(format)) {
				format = "%Y-%m";
			}else if ("HH:mm:ss".equals(format)){
				format = "%H:%i:%s";
			}else if("MM-dd".equals(format)){
				format = "%m-%d";
			}else if("HH:mm".equals(format)){
				format = "%H:%i";
			}else if("mm:ss".equals(format)){
				format = "%i:%s";
			}else if("yyyy".equals(format)){
				format = "%Y";
			}else if("MM".equals(format)){
				format = "%m";
			}else if("dd".equals(format)){
				format = "%d";
			}else if("HH".equals(format)){
				format = "%H";
			}else if("mm".equals(format)){
				format = "%i";
			}else if("ss".equals(format)){
				format = "%s";
			}else if("yyyy-MM-dd HH:mm:ss.ms".equals(format)){
				format = "%Y-%m-%d %H:%i:%s.%f";
			}else{
				format = "%Y-%m-%d %H:%i:%s";
			}
		}else {
			format = "%Y-%m-%d %H:%i:%s";
		}
		//format = StringUtil.checkStr(format) ? format : "'%Y-%m-%d %H:%i:%s'";
		return "DATE_FORMAT(" + col + ", '" + format + "')";
	}

	/**
	 * �������ݿ���ַ�����ƴ�ӽ�� sybase �� oracle 'a'||'b' = 'ab' mysql
	 * concat������concat('a','b') = 'ab'
	 * 
	 * @author ̷����
	 * @return
	 */
	public String getConcatResult(String... strs) {
		String result = "concat(";
		for (int i = 0; i < strs.length; i++) {
			if (i > 0) {
				result += ",";
			}
			result += strs[i];
		}
		result += ")";
		return result;
	}

	/**
	 * �������ݿ���ַ�����ƴ�ӽ��
	 * @param splitChar ƴ��ʱ���м���Զ���ļ����
	 * @param strs ����ֶ������൱��һ������
	 * @return ƴ�Ӵ��Ľ��
	 */
	public String getConcatResult2(String splitChar, String... strs) {
		String result = "concat(";
		for (int i = 0; i < strs.length; i++) {
			if (i > 0) {
				result += ",'"+splitChar+"',";
			}
			result += strs[i];
		}
		result += ")";
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
		return "CONCAT(" + number + ", '')"; //ע�ⲻ����convert(number,varchar)
	}
	
	public String cancelOrderBy(String sql){
		return sql;
	}

	/**
	 * ���ȴ�Ӧ�û���ȡ������oracle,DB2������Ӧ���ڻ�������������Ҫ��������,�ʼӴ˷���
	 * @param domain ����ԴID
	 * @return
	 */
	public String getInitPrimaryKeys(String domain) {
		int cachePrimaryKeys=Integer.parseInt(ConfigInit.Config.getProperty("cachePrimaryKeys","100"));
		Map<String,Long> temp=DialectCacheObjects.primaryKeys.get(domain);
		long cnt=0;
		long curKey=-1;
		String key=null;
		if(temp!=null){
			cnt=temp.get("cnt");
			curKey=temp.get("curKey");
			if(cnt<cachePrimaryKeys){//�����δ�������������
				key=(++curKey)+"";
				temp.put("cnt",++cnt);
				temp.put("curKey", curKey);
			}
		}else{
			temp=new HashMap<String, Long>();
		}
		DialectCacheObjects.primaryKeys.put(domain,temp);
		return key;
	}

	/**
	 * ���ݿ���ַ�������ת��Ϊ��������
	 * 
	 * @param ��Ҫת�����ֶ�
	 * @autho Zhanweibin 2011-12-27
	 * @return
	 */
	public String convertStringToInt(String column) {
		return "CAST(" + column + " AS SIGNED)";
	}

	/**
	 * �õ����ݿ������
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return ���ݿ�Сд����
	 */
	public String getDBType() {
		return "mysql";
	}
	
	/**
	 * ��ȡ���ݿ��û�����Ϣ
	 * @author qiaoqide
	 * @return �û�����Ϣ��ѯ���
	 */
	public String getUserTableInfo(String tableNames, String ... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("select t1.TABLE_NAME as \"sTableName\",t2.TABLE_COMMENT as \"sTableComment\", ");
		sql.append("t1.COLUMN_NAME as \"sFieldName\", t1.COLUMN_COMMENT as \"sColumnComment\", ");
		sql.append("(case t1.IS_NULLABLE when 'NO' then 'N' else 'Y' end) as \"iRequired\",t1.DATA_TYPE as \"iFieldType\", ");
		sql.append("(case when t1.CHARACTER_MAXIMUM_LENGTH is null then 0 else t1.CHARACTER_MAXIMUM_LENGTH end) as \"iFieldLength\", ");
		sql.append("(case when t1.NUMERIC_PRECISION is null then 0 else t1.NUMERIC_PRECISION end) as \"iFieldPrec\", ");
		sql.append("t1.NUMERIC_SCALE as \"iFieldScale\", (case t1.COLUMN_KEY when 'PRI' then 'P' else '' end) as \"sConstraintType\", ");
		sql.append("t1.COLUMN_DEFAULT as \"sDefaultValue\" ");
		sql.append("from INFORMATION_SCHEMA.COLUMNS t1,INFORMATION_SCHEMA.TABLES t2 ");
		sql.append("where t1.TABLE_NAME = t2.TABLE_NAME and t1.TABLE_SCHEMA=t2.TABLE_SCHEMA and t1.TABLE_SCHEMA=DATABASE() ");
		if (StringUtil.checkStr(tableNames)) {
			StringBuffer tblNames = new StringBuffer();
			for (String tableName : tableNames.split(",")) {
				if (tblNames.toString().length() > 0) tblNames.append(",");
				tblNames.append("'").append(tableName.toUpperCase()).append("'");
			}
			sql.append(" and t1.TABLE_NAME in (").append(tblNames.toString()).append(")");
		}
		sql.append(" order by t1.TABLE_NAME, t1.COLUMN_NAME");
		return sql.toString();
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
		//��: CREATE TABLE A_temp SELECT *  FROM A WHERE  1=2 
		StringBuffer sql=new StringBuffer("CREATE TABLE ");
		sql.append(toTable)
		.append(" select ")
		.append(StringUtil.checkStr(fields) ? fields : "*")
		.append(" from ")
		.append(fromTable)
		.append(" where ")
		.append(StringUtil.checkStr(where) ? where : " 1=2 ");
		return sql.toString();
	}
	
	/**
	 * ���Ʊ�ṹ,������ʱ��,��ͬʱ��������
	 * @param fromTable Դ�����
	 * @param toTable �±����
	 * @param fields ��Ҫ���Ƶ��ֶΣ�����ֶ��ö��Ÿ��������û�У������������ֶ�
	 * @param where �и��������������������ݣ�û��������������Ʊ�ṹ
	 */
	public String copyTempTableStructure(String fromTable, String toTable,String fields,String where) {
		//��: CREATE TABLE A_temp SELECT *  FROM A WHERE  1=2 
		StringBuffer sql=new StringBuffer("CREATE temporary TABLE ");
		sql.append(toTable)
		.append(" select ")
		.append(StringUtil.checkStr(fields) ? fields : "*")
		.append(" from ")
		.append(fromTable)
		.append(" where ")
		.append(StringUtil.checkStr(where) ? where : " 1=2 ");
		return sql.toString();
	}
	
	/**
	 * ��ȡ����Ƿ������ͬ�ı����Ƶ�sql���
	 */
	public String getCheckTableSql(String tableName, String ... args) {
		String sql="";
		sql = "select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME='"+tableName+"'";
		return sql;
	}
	
	/**
	 * ��ȡ����Ƿ������ͬ�ı����Ƶ�sql���,����pin in���
	 */
	public String getCheckTablesSql() {
		String sql="";
		sql = "select TABLE_NAME as \"tableName\" from INFORMATION_SCHEMA.TABLES where TABLE_NAME ";
		return sql;
	}
	
	public String indexOf(String column, String str) {
		return "inStr(" + column + "," + str + ")";
	}
	public String subString(String column, String startExp, String endExp) {
		return "subString(" + column + "," + startExp + "," + endExp + ")";
	}
	
	/**
	 * �����ֶ�����,���ʹ��
	 * @param ftype ��������
	 * @param flen �ֶγ���
	 * @param decpos С��λ����
	 * @return
	 */
	public String parseType(String ftype,int flen,int decpos){
		String fieldType = "";
		if(ftype.equals("1")){
			fieldType=" decimal("+flen+","+decpos+") ";
		}else if(ftype.equals("2")){
			fieldType=" int ";
		}else if(ftype.equals("3")){
			if(flen>4000)flen=4000;
			fieldType=" varchar("+flen+") ";
		}else if(ftype.equals("4")){
			fieldType=" date ";
		}else if(ftype.equals("5")){
			fieldType=" text ";
		}else if(ftype.equals("6")){
			if(flen>2000)flen=2000;
			fieldType=" char("+flen+") ";
		}else if(ftype.equals("7")){
			fieldType=" datetime ";
		}
		return fieldType;
	}
	
	/**
	 * �޸���
	 * @param tableName ����
	 * @param dataList ������
	 * @return
	 */
	public String[] modifyField(String tableName, List<Map> dataList) {
		List<String> list = new ArrayList<String>();
		StringBuffer sql = null;
		for (Map<String,String> m : dataList) {
			sql = new StringBuffer();
			sql.append("alter table ").append(tableName).append(" modify ");
			sql.append(m.get("sFieldName")).append(" ").append(m.get("fieldType")).append(" ");
			list.add(sql.toString());
		}
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * ������
	 * @param tableName ����
	 * @param dataList ������
	 * @return
	 */
	public String[] insertField(String tableName, List<Map> dataList) {
		List<String> list = new ArrayList<String>();
		StringBuffer sql = null;
		for (Map<String,String> m : dataList) {
			sql = new StringBuffer();
			sql.append("alter table ").append(tableName).append(" add ");
			sql.append(m.get("sFieldName")).append(" ").append(m.get("fieldType")).append(" ").append("null");
			list.add(sql.toString());
		}
		return list.toArray(new String[list.size()]);
	}
	
	public String charIndex(String str, String str1) {
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
		String tableName = StringUtil.toString(map.get("tableName"));
		String oldName = StringUtil.toString(map.get("oldFieldName"));
		String newName = StringUtil.toString(map.get("newFieldName"));
		String fieldType = StringUtil.toString(map.get("fieldTypeLen"));
		String sql = "ALTER TABLE "+tableName+" change "+oldName+" "+newName+" "+fieldType;
		return sql;
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
			fieldLen = (flen == 0 && decpos == 0)? 0 : flen;
		}
		return fieldLen;
	}
	
	public String getSecondsBetween(String dateColumnName1, String dateColumnName2) {
		return "ABS((UNIX_TIMESTAMP(" + dateColumnName1 + " )- UNIX_TIMESTAMP(" + dateColumnName2 + ")))";
	}
	
	public String[] replaceSpecialCharForLike(String exp, String value){
		value = value.replace("_", "\\_").replace("%", "\\%");
		return new String[]{exp.replace("?", value), ""};
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
		return "(UNIX_TIMESTAMP(" + endColumnName + ") - UNIX_TIMESTAMP(" + beginColumnName + "))";
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
		
		return "date_add(" + col + ", interval " + num + " " + format + ")";
	}
}
