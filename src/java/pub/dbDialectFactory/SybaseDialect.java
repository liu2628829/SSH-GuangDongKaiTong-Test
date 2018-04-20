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
import java.util.UUID;
import pub.servlet.ConfigInit;
import util.StringUtil;

/**
 * Sybase 方言
 * 
 * @author gaotao 2011-08-09
 */
class SybaseDialect implements Dialect {
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
		
        /*  表名添加时间戳, 因为出现过临时表名冲突的问题
         	因sybase ase12.5 只以临时表前12位作为唯一标识，所以用时间戳依然出现了冲突
        	String tempTableName = "#Paging"
                + DateUtil.parseToString(new Date(), "yyyyMMddHHmmssSSSS")
                + Math.round(Math.random() * 100);*/
		/*改用UUID,由于表名不能超30位，取*/
		String tempTableName = "#P"+UUID.randomUUID().toString().replace("-", "").substring(0,27);
		
		sql = sql.replaceAll("\\s{2,}", " ");
		int index=sql.toUpperCase().indexOf(" FROM ");
		StringBuffer sb=new StringBuffer(sql).insert(index, (" INTO " + tempTableName));
		
		//如果有distinct
		index=sb.toString().toUpperCase().indexOf("SELECT DISTINCT");
		if(index>=0){
			sb.insert(index+15, " iRowId =identity(10), ");
		}else {
			index=sb.toString().toUpperCase().indexOf("SELECT");
			sb.insert(index+7, "iRowId =identity(10), ");
		}
		
		//如果有top
		String sql2 = sb.toString();
		sql = sql2.toUpperCase();
		index = sql.indexOf(" TOP ");
		if(index >= 0){ 
		  	int index2 = sql.indexOf("TOP ")+5;
		  	index2 = sql.indexOf(" ",index2);
		  	String top = sql.substring(index, index2);
		  	String temp = sql2.substring(0,index)+ sql2.substring(index2);
		  	temp = temp.replace(" iRowId =", top+" iRowId =");
			sb = new StringBuffer(temp);
		}

		sb.insert(0," declare @RecCount  int ").insert(0, " set rowcount 50000 ");
		sb.append(" select @RecCount = @@rowcount ");
		sb.append(" SELECT @RecCount as iRecCount,@RecCount as totalCount, * FROM ").append(tempTableName).append("  WHERE iRowId  > ").append((page - 1) * limit).append(" and iRowId<").append((page) * limit+1);
		sb.append(" set rowcount 0 ");
		sb.append(" drop table ").append(tempTableName);

		PreparedStatement sta=conn.prepareStatement(sb.toString());
		if(list != null){
			int size = list.size();
			for(int i=0; i<size; i++){
				sta.setObject(i+1, list.get(i));
			}
		}
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		logger.info("sql查询前："+sdf.format(new Date()));
		logger.info("sql语句："+sb.toString());
		logger.info("sql查询后："+sdf.format(new Date()));*/
		ResultSet rs = sta.executeQuery();
		return new Object[] {sta, rs };
	}
	
	/**
	 * 取主键
	 * 不同数据库，取主键方法不一致，sybase,mysql的需用存储过程，oracle,DB2用序列
	 * 同分页方法一样，Statment对象必须在必方言实现类内实例化
	 * 同分页方法一样，为了保证能够再DataBaseUtil统一关闭数据库操作对象，故也返回一个对象数组
	 * 数组有2个元素，第一个元素是Statement，第二个Resultset
	 * cachePrimaryKeys:缓存主键的个数，此参数针对仅mysql,sybase有效
	 */
	public synchronized Object[] getKeyId(Connection conn,int cachePrimaryKeys)
			throws SQLException {
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
		return "getdate()";
	}

	public String isNull(String property, String o) {
		return "isnull(" + property + "," + o + ")";
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
		return "datediff(ss," + dateColumnName + ",getdate())";
	}

	public String getUpperFunction(String columnName) {
		return "UPPER(" + columnName + ")";
	}

	public String getDateValueHour(String col) {
		return "substring(convert(varchar," + col + ",108),1,2)+':00'";
	}

	public String getGroupByHour(String col) {
		return "substring(convert(varchar," + col
				+ ",108),1,2),convert(varchar," + col + ",111)";
	}

	public String getDateValueDay(String col) {
		return "convert(varchar," + col + ",111)";
	}

	public String getGroupByDay(String col) {
		return "convert(varchar," + col + ",111)";
	}

	public String[] getIdentityKey(String primaryKeyCol, String domain) {
		// return new String[]{primaryKeyCol,DatabaseUtil.getKeyId(domain, "")};
		return new String[] { "", "" };
	}

	/**
	 * update by Zhanweibin 2011-12-28
	 * 加入了yyyy-MM-dd格式处理
	 */
	public String datetimeTostring(String col, String format) {
		if (StringUtil.checkStr(format)) {
			if("yyyy-MM-dd".equals(format)){
				format = "str_replace(convert(varchar, " + col + ", 102), '.', '-')";
			//add by liuchaobiao 2012/9/14
			}else if ("yyyy-MM".equals(format)) {
				format="(convert(varchar,datepart(yy," + col + "))+'-'+convert(varchar,datepart(mm," + col + ")))";
			}else if("HH:mm:ss".equals(format)){
				format ="(convert(varchar,"+ col + ",108))";
			}else if("MM-dd".equals(format)){
				format="(convert(varchar,datepart(mm," + col + "))+'-'+convert(varchar,datepart(dd," + col + ")))";
			}else if("HH:mm".equals(format)){
				format="(convert(varchar,datepart(hh," + col + "))+'-'+convert(varchar,datepart(mi," + col + ")))";
			}else if("mm:ss".equals(format)){
				format="(convert(varchar,datepart(mi," + col + "))+'-'+convert(varchar,datepart(ss," + col + ")))";
			}else if("yyyy".equals(format)){
				format = "(convert(varchar,datepart(yy," + col + ")))";
			}else if("MM".equals(format)){
				format = "(convert(varchar,datepart(mm," + col + ")))";
			}else if("dd".equals(format)){
				format = "(convert(varchar,datepart(dd," + col + ")))";
			}else if("HH".equals(format)){
				format = "(convert(varchar,datepart(hh," + col + ")))";
			}else if("mm".equals(format)){
				format = "(convert(varchar,datepart(mi," + col + "))";
			}else if("ss".equals(format)){
				format = "(convert(varchar,datepart(ss," + col + ")))";
			}else if("yyyy-MM-dd HH:mm:ss.ms".equals(format)){
				format = "str_replace(convert(varchar," + col + ",102),'.','-')+' '+(convert(varchar," + col + ",108))+'.'+(convert(varchar,datepart(ms," + col + ")))";
			}else {
				format = "str_replace(convert(varchar," + col + ",102),'.','-')+' '+(convert(varchar," + col + ",108))";
			}
		} else {
			format = "str_replace(convert(varchar," + col + ",102),'.','-')+' '+(convert(varchar," + col + ",108))";
		} 

		return format;
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
		return "convert(varchar," + number + ")";
	}
	
	public String cancelOrderBy(String sql){
		return sql.replace("order ", "-- order ").replace("ORDER ", "-- order ")+" \n";
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
		return "convert(numeric(18,0), " + column + ")";
	}
	
	/**
	 * 得到数据库的类型
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return 数据库小写名称
	 */
	public String getDBType() {
		return "sybase";
	}
	
	/**
	 * 获取数据库用户表信息
	 * @author Zhanweibin
	 * @return 用户表信息查询语句
	 */
	public String getUserTableInfo(String tableNames, String ... args) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("select obj.name \"sTableName\", col.name \"sFieldName\", ");
		sql.append("isnull(xtyp.xtname, isnull(get_xtypename(col.xtype, col.xdbid), typ.name)) \"iFieldType\", ");
		sql.append("case isnull(xtyp.xtname, isnull(get_xtypename(col.xtype, col.xdbid), typ.name)) when 'decimal' then convert(varchar(22), col.prec) else convert(varchar(22), col.length) end \"iFieldLength\", ");
		sql.append("col.prec \"iFieldPrec\", col.scale \"iFieldScale\", case col.status when 8 then 'Y' ELSE 'N' end iRequired, isnull((select isnull(ltrim(rtrim(str_replace(text, 'DEFAULT', ''))), '') ");
		sql.append("from syscomments comm, sysprocedures pro where comm.id = col.cdefault and pro.id = comm.id ");
		sql.append("and pro.sequence = 0 and pro.status & 4096=4096),'') sDefaultValue, ");
		sql.append("isnull((select 'P' from (select object_name(id) tabname, index_col(object_name(id), indid, 1) columnname from sysindexes where status & 2048=2048 ");
		sql.append("union all select object_name(id), index_col(object_name(id), indid, 2) from sysindexes where status & 2048=2048 ");
		sql.append("union all select object_name(id), index_col(object_name(id), indid, 3) from sysindexes where status & 2048=2048 ");
		sql.append("union all select object_name(id), index_col(object_name(id), indid, 4) from sysindexes where status & 2048=2048 ");
		sql.append("union all select object_name(id), index_col(object_name(id), indid, 5) from sysindexes where status & 2048=2048 ");
		sql.append("union all select object_name(id), index_col(object_name(id), indid, 6) from sysindexes where status & 2048=2048 ) pk ");
		sql.append("where columnname is not null and tabname = obj.name and columnname = col.name ), '') \"sConstraintType\", ");
		sql.append("NULL sTableComment, NULL sColumnComment ");
		sql.append("from syscolumns col, systypes typ, sysxtypes xtyp, sysobjects obj ");
		sql.append("where 1=1 and col.usertype *= typ.usertype and col.xtype *= xtyp.xtid and col.id = obj.id and obj.type = 'U' ");
		
		if (StringUtil.checkStr(tableNames)) {
			StringBuffer tblNames = new StringBuffer();
			for (String tableName : tableNames.split(",")) {
				if (tblNames.toString().length() > 0) tblNames.append(",");
				tblNames.append("'").append(tableName).append("'");
			}
			sql.append("and obj.name in(").append(tblNames.toString()).append(") ");
		}
		sql.append("order by sTableName, sFieldName");
		return sql.toString();
	}
	
	/**
	 * 根据条件查询系统表
	 */
	public String selectSystemTable(Map map) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("select obj.name as \"sTableName\", NULL as \"sComment\" ");
		sql.append("from sysobjects obj where 1=1 and obj.type = 'U' ");//U为用户表
		if (StringUtil.checkObj(map.get("sTableName")))
			sql.append("and obj.name like '%").append(map.get("sTableName")).append("%' ");
		sql.append("and not exists(select 1 from tbFdTable where sTableName = obj.name) ");
		sql.append("order by obj.name");
		return sql.toString();
	}
	
	/**
	 * 传入数据库字段类型获取字段公共类型
	 * (整数1、小数2、字符串3、日期时间4、日期5、时间6、什么都不是0)
	 * @author tanjianwen
	 * @param fieldType 字段数据库类型
	 * @param precNum 字段精度
	 * @return 
	 */
	public String getDBTblFieldType(String fieldType, String precNum) {
		if ("integer".equals(fieldType) || "smallint".equals(fieldType) ||
				(("numeric".equals(fieldType) || "decimal".equals(fieldType)) && ("0".equals(precNum) || "null".equals(precNum)))) {
			return "1";
		} else if ((("numeric".equals(fieldType) || "decimal".equals(fieldType)) 
				|| "float".equals(fieldType) || "real".equals(fieldType))) {
			return "2";
		} else if ("char".equals(fieldType) || "varchar".equals(fieldType) 
				|| "nchar".equals(fieldType) || "nvarchar".equals(fieldType)) {
			return "3";
		} else if ("datetime".equals(fieldType)) {
			return "4";
		} else {
			return "0";
		}
	}
	
	/**
	 * 创建表
	 */
	public String createTable(String tableName, List<Map> list) {
		StringBuffer sql = new StringBuffer();
		int index = 0;
		sql.append("create table ").append(tableName).append(" (");
		for (Map m : list) {
			if ("1".equals(m.get("iIsPrimaryKey"))) {
				sql.append(StringUtil.toString(m.get("sFieldName"))).append(" ").append(getDataType(m)).append(" primary key");
				index++;
				break;
			}
		}
		for (Map m : list) {
			if ("1".equals(m.get("iIsPrimaryKey"))) {
				continue;
			}
			if(index > 0)sql.append(", ");
			sql.append(StringUtil.toString(m.get("sFieldName"))).append(" ").append(getDataType(m)).append(Integer.parseInt(String.valueOf(m.get("iRequired"))) == 0 ? " null" : " not null");
		}
		sql.append(")");
		return sql.toString();
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
		StringBuffer cols = new StringBuffer();
		List<String> sql = new ArrayList<String>();
		for (Map<String,String> m : dataList) {
			if (!"D".equals(m.get("flag"))) continue;
			sql.add("alter table "+tableName+" drop "+StringUtil.toString(m.get("sFieldName"))+"");
		}
		
		return sql.toArray(new String[sql.size()]);
	}
	
	/**
	 * 获取数据类型
	 * @param m
	 * @return
	 */
	private String getDataType(Map<String,String> m) {
		String dataType = "";
		if ("1".equals(m.get("iFieldType"))) {//整数
			if (StringUtil.checkObj(m.get("iFieldLength"))) {
				dataType = "numeric(" + String.valueOf(m.get("iFieldLength")) + ")";
			} else {
				dataType = "numeric(15)";
			}
		} else if("2".equals(m.get("iFieldType"))) {//小数
			if (StringUtil.checkObj(m.get("iFieldLength")) && StringUtil.checkObj(m.get("iFieldPrec"))) {
				dataType = "numeric(" + String.valueOf(m.get("iFieldLength")) + "," + String.valueOf(m.get("iFieldPrec")) + ")";
			} else if (StringUtil.checkObj(m.get("iFieldLength"))) {
				dataType = "numeric(" + String.valueOf(m.get("iFieldLength")) + ",2)";
			} else if (StringUtil.checkObj(m.get("iFieldPrec"))) {
				dataType = "numeric(15," + String.valueOf(m.get("iFieldPrec")) + ")";
			} else {
				dataType = "numeric(15,2)";
			}
		} else if("3".equals(m.get("iFieldType"))) {
			if (StringUtil.checkObj(m.get("iFieldLength")) && Integer.parseInt(m.get("iFieldLength")) <= 4000) {
				dataType = "varchar(" + String.valueOf(m.get("iFieldLength")) + ")";
			} else if(StringUtil.checkObj(m.get("iFieldLength")) && Integer.parseInt(m.get("iFieldLength")) > 4000){
				dataType = "varchar(" + String.valueOf(m.get("iFieldLength")) + ")";
			} else {
				dataType = "varchar(200)";
			}
		} else {
			dataType = "datetime";
		}
		return dataType;
	}
	
	/**
	 * 复制表结构,创建新表,可同时带上数据
	 * @param fromTable 源表表名
	 * @param toTable 新表表名
	 * @param fields 需要复制的字段，多个字段用逗号隔开，如果没有，所复制所有字段
	 * @param where 有给条件，则按条件复制数据；没给条件，则仅复制表结构
	 */
	public String copyTableStructure(String fromTable, String toTable,String fields,String where) {
		//如: select *  into AA_temp from AA where 1=2
		StringBuffer sql=new StringBuffer("select ");
		sql.append(StringUtil.checkStr(fields)?fields:"*")
		.append(" into ")
		.append(toTable)
		.append(" from ")
		.append(fromTable)
		.append(" where ")
		.append(StringUtil.checkStr(where)?where:" 1=2 ");
		return sql.toString();
	}
	
	/**
	 * 复制表结构,创建新表,可同时带上数据
	 * @param fromTable 源表表名
	 * @param toTable 新表表名(Sybase要以#为前缀)
	 * @param fields 需要复制的字段，多个字段用逗号隔开，如果没有，所复制所有字段
	 * @param where 有给条件，则按条件复制数据；没给条件，则仅复制表结构
	 */
	public String copyTempTableStructure(String fromTable, String toTable,String fields,String where) {
		//如: select *  into AA_temp from AA where 1=2
		StringBuffer sql=new StringBuffer("select ");
		sql.append(StringUtil.checkStr(fields)?fields:"*")
		.append(" into ")
		.append(toTable)
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
		sql = "select 1 from sysobjects t where t.name = '"
			+ tableName + "'";
		return sql;
	}
	
	/**
	 * 获取检查是否存在相同的表名称的sql语句,后面pin in语句
	 */
	public String getCheckTablesSql() {
		String sql="";
		sql = "select name as \"tableName\" from sysobjects t where t.name ";
		return sql;
	}
	
	public String indexOf(String column, String str) {
		return "charIndex(" + str + "," + column + ")";
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
			fieldType=" numeric("+flen+","+decpos+") ";
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
		}
		else if(ftype.equals("7")){
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
	
	public String charIndex(String str,String str1){
		return "charindex("+str1+","+str+")";
	}
	
	public String dataLength(String str) {
		return "datalength("+str+")";
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
		String sql = "sp_rename  '"+tableName+"."+oldName+"', "+newName;
		return sql;
	}
	
	/**
	 * insert 语句拼自增长字段，取值SQL部分，此方法被通用表单2.0所用
	 * @param tableName 表名
	 * @return 数组包含3个元系，具体见实现类的实现 
	 */
	public String[] getIncrementSQL(String tableName) {
		return new String []{
				"declare @a numeric(15) begin execute @a=SP_GET_ID_EX '"+tableName+"' ",
				"@a",
				"end "
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
			fieldLen = (flen == 0 && decpos == 0)? 9 : flen;
		}
		return fieldLen;
		
	}
	
	public String getSecondsBetween(String dateColumnName1, String dateColumnName2) {
		return "abs(datediff(ss," + dateColumnName1 + "," + dateColumnName2 + "))";
	}
	
	public String[] replaceSpecialCharForLike(String exp, String value){
		value = value.replace("[", "[[]").replace("_", "[_]").replace("%", "[%]");
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
		return "datediff(ss, " + beginColumnName + ", " + endColumnName + ")";
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
		if("yyyy".equalsIgnoreCase(format) || "year".equalsIgnoreCase(format)){
			format = "yy";
		}else if("month".equalsIgnoreCase(format)){
			format = "MM";
		}else if("day".equalsIgnoreCase(format)){
			format = "dd";
		}else if("hh24".equalsIgnoreCase(format) || "hour".equalsIgnoreCase(format)){
			format = "hh";
		}else if("mm".equals(format) || "minute".equalsIgnoreCase(format)){
			format = "mi";
		}else if("second".equalsIgnoreCase(format)){
			format = "ss";
		}
		
		return "dateadd(" + format + ", " + num + ", " + col + ")";
	}
}
