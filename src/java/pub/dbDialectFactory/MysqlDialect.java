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
 * Mysql 方言
 * 
 * @author gaotao 2011-08-09
 */
class MysqlDialect implements Dialect {
	/**
	 * 存储过程翻页方法
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
	 * preparestatement 分页查询
	 */
	public Object[] getDataByPageEoms(Connection conn, String sql, int page,
			int limit, List list) throws SQLException {
		
		/**得总记录数*/
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
		DialectTool.free(null, sta, rs); //先关闭已经无用的对象
		if("0".equals(rows)){ //没有记录数，不必继续查询数据结果集
			return null;
		}
		
		/**得数据结果集*/
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
	 * 生成分页SQL
	 * */
	private String getPageSql(String rows, String sql, int page, int limit){
		String tempSQL = sql.toLowerCase();
		StringBuilder v_sql = new StringBuilder();
		
		if(tempSQL.contains(" union ")){ //如果有union ，则按老方式直接包一层
			
			v_sql.append("select ").append(rows);
			v_sql.append(" as \"iRecCount\", ");
			v_sql.append(rows);
			v_sql.append(" as \"totalCount\",tm2.* from (");
			v_sql.append(sql);
			v_sql.append(" ) tm2 limit ");
			v_sql.append((page-1)*limit);
			v_sql.append(",");
			v_sql.append(limit);
			
		}else{ //没有union，则解析SQL
			
			v_sql.append(sql);
			//总记录数部分
			int index = tempSQL.indexOf(" from ");
			String part = (", "+ rows +" as \"iRecCount\", "+ rows + " as \"totalCount\" ");
			v_sql.insert(index, part);
			//where部分，加分页参数
			part = " limit "+((page-1)*limit) +"," +limit;
			v_sql.append(part);
			
		}
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
	public synchronized Object[] getKeyId(Connection conn,int cachePrimaryKeys) throws SQLException {
		Statement sta = conn.prepareCall("{call SP_GET_ID_EX2(?)}");
		CallableStatement callsta = (CallableStatement) sta;
		callsta.setInt(1, cachePrimaryKeys);
		ResultSet rs = callsta.executeQuery();
		return new Object[] { sta, rs };
	}
	
	/**
	 * 修改Clob或text大文本字段
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
	 * 加入了yyyy-MM-dd格式处理
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
	 * 返回数据库的字符串的拼接结果 sybase 或 oracle 'a'||'b' = 'ab' mysql
	 * concat函数：concat('a','b') = 'ab'
	 * 
	 * @author 谭健文
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
	 * 返回数据库的字符串的拼接结果
	 * @param splitChar 拼接时，中间加自定义的间隔符
	 * @param strs 多个字段名，相当于一个数组
	 * @return 拼接串的结果
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
	 * 数据库的数字类型转字符串类型
	 * 
	 * @param 需要转换的数字
	 * @author 谭健文
	 * @return
	 */
	public String convertNumberToString(String number) {
		return "CONCAT(" + number + ", '')"; //注意不能是convert(number,varchar)
	}
	
	public String cancelOrderBy(String sql){
		return sql;
	}

	/**
	 * 优先从应用缓存取主键，oracle,DB2不会在应用内缓存主键，所以要有所区分,故加此方法
	 * @param domain 数据源ID
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
			if(cnt<cachePrimaryKeys){//如果还未到缓存的最大个数
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
	 * 数据库的字符串类型转换为整型类型
	 * 
	 * @param 需要转换的字段
	 * @autho Zhanweibin 2011-12-27
	 * @return
	 */
	public String convertStringToInt(String column) {
		return "CAST(" + column + " AS SIGNED)";
	}

	/**
	 * 得到数据库的类型
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return 数据库小写名称
	 */
	public String getDBType() {
		return "mysql";
	}
	
	/**
	 * 获取数据库用户表信息
	 * @author qiaoqide
	 * @return 用户表信息查询语句
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
		//如: CREATE TABLE A_temp SELECT *  FROM A WHERE  1=2 
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
	 * 复制表结构,创建临时表,可同时带上数据
	 * @param fromTable 源表表名
	 * @param toTable 新表表名
	 * @param fields 需要复制的字段，多个字段用逗号隔开，如果没有，所复制所有字段
	 * @param where 有给条件，则按条件复制数据；没给条件，则仅复制表结构
	 */
	public String copyTempTableStructure(String fromTable, String toTable,String fields,String where) {
		//如: CREATE TABLE A_temp SELECT *  FROM A WHERE  1=2 
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
	 * 获取检查是否存在相同的表名称的sql语句
	 */
	public String getCheckTableSql(String tableName, String ... args) {
		String sql="";
		sql = "select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME='"+tableName+"'";
		return sql;
	}
	
	/**
	 * 获取检查是否存在相同的表名称的sql语句,后面pin in语句
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
	 * 翻译字段类型,库表使用
	 * @param ftype 数据类型
	 * @param flen 字段长度
	 * @param decpos 小数位长度
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
	 * 修改列
	 * @param tableName 表名
	 * @param dataList 列数据
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
	 * 新增列
	 * @param tableName 表名
	 * @param dataList 列数据
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
	 * 拿到修改列名sql
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
	 * 两个日期字段间相差的秒数
	 * @param beginColumnName 开始时间
	 * @param endColumnName 结束时间
	 * @return 相差秒数
	 * @author tangxiaolong
	 * @version 2013-11-1
	 */
	public String getSecondsBetweenDays(String beginColumnName, String endColumnName){
		return "(UNIX_TIMESTAMP(" + endColumnName + ") - UNIX_TIMESTAMP(" + beginColumnName + "))";
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
		
		return "date_add(" + col + ", interval " + num + " " + format + ")";
	}
}
