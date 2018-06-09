package pub.dbDialectFactory;

import util.StringUtil;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * DB2 方言
 * 
 * @author gaotao 2011-08-09
 */
class DB2Dialect implements Dialect {
	/**
	 * 存储过程翻页方法
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
		
		DialectTool.free(null, sta, rs); //先关闭已经无用的对象
		
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
	 * preparestatement 分页查询
	 */
	public Object[] getDataByPageEoms(Connection conn, String sql, int page,
			int limit, List list) throws SQLException {
		
		/**得总记录数*/
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
		DialectTool.free(null, sta, rs); //先关闭已经无用的对象
		if("0".equals(rows)){ //没有记录数，不必继续查询数据结果集
			return null;
		}
		
		/**得数据结果集*/
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
	 * 生成分页SQL
	 * */
	private String getPageSql(String rows, String sql, int page, int limit){
		
		StringBuilder v_sql = new StringBuilder();
		v_sql.append("select ").append(rows);
		v_sql.append(" as \"iRecCount\", ");
		v_sql.append(rows);
		v_sql.append(" as \"totalCount\", tm2.* from (select rownum as rn, tm1.* from (");
		v_sql.append(sql);
		v_sql.append(" ) tm1 where rownum <="); //把行号过滤放在内层子查询，能进一步减少子查询检索的数据量
		v_sql.append(page*limit);
		v_sql.append(") tm2 ");
		v_sql.append(" where rn >= "); //这个条件不能放到内层，不然查不出数据
		v_sql.append((page-1)*limit+1);
		
		return v_sql.toString();
	}

	/**
	 * 取主键
	 * 不同数据库，取主键方法不一致，sybase,mysql的需用存储过程，oracle,DB2用序列
	 * 同分页方法一样，Statment对象必须在必方言实现类内实例化
	 * 同分页方法一样，为了保证能够再DataBaseUtil统一关闭数据库操作对象，故也返回一个对象数组
	 * 数组有2个元素，第一个元素是Statement，第二个Resultset
	 * cachePrimaryKeys:缓存主键的个数，此参数针对仅mysql,sybase有效
	 */
	public synchronized Object[] getKeyId(Connection conn, int cachePrimaryKeys)
			throws SQLException {
		String vSql = " select GLOBALSEQ.nextVal from GLOBALTABLE ";
		Statement sta = conn.createStatement();
		ResultSet rs = sta.executeQuery(vSql);
		return new Object[] { sta, rs };
	}

	/**
	 * 修改Clob或text大文本字段
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

	// 暂时未实行
	public String datetimeTostring(String col, String format) {
		return "";
	}

	/**
	 * 返回数据库的字符串的拼接结果 sybase 或 oracle 'a'||'b' = 'ab' mysql
	 * concat函数：concat('a','b') = 'ab'
	 * 
	 * @author 谭健文
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
	 * 返回数据库的字符串的拼接结果
	 * @param splitChar 拼接时，中间加自定义的间隔符
	 * @param strs 多个字段名，相当于一个数组
	 * @return 拼接串的结果
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
	 * 数据库的数字类型转字符串类型
	 * 
	 * @param 需要转换的数字
	 * @author 谭健文
	 * @return
	 */
	public String convertNumberToString(String number) {
		return "char(" + number + ")";
	}

	public String cancelOrderBy(String sql) {
		return sql;
	}

	/**
	 * 优先从应用缓存取主键，oracle,DB2不会在应用内缓存主键，所以要有所区分,故加此方法
	 * @param domain 数据源ID
	 * @return
	 */
	public String getInitPrimaryKeys(String domain) {
		return null;
	}

	/**
	 * 数据库的字符串类型转换为整型类型
	 * 
	 * INT函数返回整型常量中的数字、字符串或者日期、时间的整数表示
	 * 
	 * @param 需要转换的字段
	 * @autho Zhanweibin 2011-12-27
	 * @return
	 */
	public String convertStringToInt(String column) {
		return "INT('" + column + "')";
	}

	/**
	 * 得到数据库的类型
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return 数据库小写名称
	 */
	public String getDBType() {
		return "db2";
	}
	
	/**
	 * 获取数据库用户表信息
	 * @author tanjianwen
	 * @return 用户表信息查询语句
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
	 * 添加列
	 * @param tableName 表名
	 * @param data 列数据
	 * @return
	 */
	public String addField(String tableName, List<Map> dataList) {
		return "";
	}
	
	/**
	 * 修改列
	 * @param tableName 表明
	 * @param dataList 列数据
	 * @return
	 */
	public String[] editField(String tableName, List<Map> dataList) {
		return new String[1];
	}
	
	/**
	 * 删除要修改的列
	 * @param tableName
	 * @param dataList
	 * @return
	 */
	public String[] deleteEditField(String tableName, List<Map> dataList) {
		return new String[0];
	}
	
	/**
	 * 删除列
	 * @param tableName
	 * @param dataList
	 * @return
	 */
	public String[] deleteField(String tableName, List<Map> dataList) {
		return new String[0];
	}
	
	/**
	 * 复制表结构,创建新表,可同时带上数据
	 * @param fromTable 源表表名
	 * @param toTable 新表表名
	 * @param fields 需要复制的字段，多个字段用逗号隔开，如果没有，所复制所有字段
	 * @param where 有给条件，则按条件复制数据；没给条件，则仅复制表结构
	 */
	public String copyTableStructure(String fromTable, String toTable,String fields,String where) {
		//如: create table table_name_new as select * from table_name_old where 1=2
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
	 * 复制表结构,创建新表,可同时带上数据
	 * @param fromTable 源表表名
	 * @param toTable 新表表名
	 * @param fields 需要复制的字段，多个字段用逗号隔开，如果没有，所复制所有字段
	 * @param where 有给条件，则按条件复制数据；没给条件，则仅复制表结构
	 */
	public String copyTempTableStructure(String fromTable, String toTable,String fields,String where) {
		//如: create table table_name_new as select * from table_name_old where 1=2
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
	 * 获取检查是否存在相同的表名称的sql语句
	 */
	public String getCheckTableSql(String tableName, String ... args) {
		String sql="";
/*		sql = "select 1 from sysobjects t where t.name = '"
			+ tableName + "'";*/
		return sql;
	}
	
	/**
	 * 获取检查是否存在相同的表名称的sql语句,后面pin in语句
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
	 * 翻译字段类型,库表使用
	 * @param ftype 数据类型
	 * @param flen 字段长度
	 * @param decpos 小数位长度
	 * @return
	 */
	public String parseType(String ftype,int flen,int decpos){
		return "";
	}
	
	/**
	 * 修改列
	 * @param tableName 表名
	 * @param dataList 列数据
	 * @return
	 */
	public String[] modifyField(String tableName, List<Map> dataList) {
		return null;
	}
	/**
	 * 新增列
	 * @param tableName 表名
	 * @param dataList 列数据
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
	 * 拿到修改列名sql
	 * @param map
	 * @return
	 */
	public String getRenameColumnSql(Map map) {
		return "";
	}

	/**
	 * insert 语句拼自增长字段，取值SQL部分，此方法被通用表单2.0所用
	 * @param tableName 表名
	 * @return 数组包含3个元系，具体见实现类的实现 
	 */
	public String[] getIncrementSQL(String tableName) {
		return new String []{
				"",//"begin declare a number(15); begin a:= SP_GET_INCREMENT('"+tableName+"'); ",
				"SP_GET_INCREMENT('"+tableName+"')",
				""//"; end; end;"
			};
	}
	
	/**
	 * 获取库表字段定义默认长度
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
	 * 两个日期字段间相差的秒数
	 * @param beginColumnName 开始时间
	 * @param endColumnName 结束时间
	 * @return 相差秒数
	 * @author tangxiaolong
	 * @version 2013-11-1
	 */
	public String getSecondsBetweenDays(String beginColumnName, String endColumnName){
		return "timestampdiff(2, char(timestamp(" + endColumnName 
					+ ") - timestamp(" + beginColumnName + ")))";
	}
	
	/**
	 * 在format对应的时间单位，增加相应的值，得到一个新的时间。
	 * 如oracle下3个参数分别给： "sysdate"，"10","year", 代表在当前时间的基础上加10年
	 * @param col 日期字段名称
	 * @param num 增减数值
	 * @param format 时间单位 (可有如下值： year, month, day, hour, minute, second)
	 * @return 函数串
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
