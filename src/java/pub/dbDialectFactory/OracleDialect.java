package pub.dbDialectFactory;

import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oracle.sql.CLOB;

import util.StringUtil;

/**
 * Oracle 方言
 * 
 * @author gaotao 2011-08-09
 */
class OracleDialect implements Dialect {
	/**
	 * 存储过程翻页方法
	 */
	public Object[] getDataByCallableStatement(Connection conn, String sql,
			int page, int limit) throws SQLException {
		Statement sta = conn.prepareCall("{call GetDataByPageEoms(?,?,?,?)}");
		CallableStatement callsta = (CallableStatement) sta;
		callsta.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
		callsta.setString(2, sql);
		callsta.setInt(3, limit);
		callsta.setInt(4, page);
		callsta.execute();
		ResultSet rs = (ResultSet) callsta.getObject(1);
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
		v_sql.append(" as \"totalCount\", tm2.* from (select * from ("); //再加一层，排序时数据才不会出错
		v_sql.append(" select rownum as rn, m1.* from (");
		v_sql.append(sql);
		v_sql.append(" ) m1 ) tm1 where tm1.rn <="); //把行号过滤放在内层子查询，能进一步减少子查询检索的数据量
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
	public synchronized Object[] getKeyId(Connection conn,int cachePrimaryKeys) throws SQLException {
		String sql = "select SEQ_COMMON_ID.NEXTVAL from dual";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		return new Object[] { st, rs };
	}

	/**
	 * 修改Clob或text大文本字段
	 */
	public Object[] writeClobOrText(Connection conn,String tableName,String where,String cols[],String val[])throws Exception{
		StringBuffer sql=new StringBuffer("select ");
		for(int i=0;i<cols.length;i++){
			sql.append(cols[i]);
			if(i!=cols.length-1)sql.append(",");
		}
		sql.append(" from ").append(tableName).append(" where ").append(where).append(" for update");
		PreparedStatement st= conn.prepareStatement(sql.toString()); 
		ResultSet rs = st.executeQuery();  
		if(rs.next()){   
			for(int i=0;i<val.length;i++){
				CLOB clob = (CLOB)rs.getClob(i+1);
				Writer writer = clob.getCharacterOutputStream();  
				
				String va = val[i];
				String[] arrx = this.pagedClobStream(va);//要解决内存溢出异常，必须把绝对大的byte[]进行分页
	                if (arrx != null){
	                    for (int j = 0; j < arrx.length; j++) {
	                    	writer.write(arrx[j]);
	                    	writer.flush();//要解决内存溢出异常,必须一页一页的flush()到数据库
	                    }
	                }else{
	                	writer.write("");
	                }
	                writer.close();
                /*CLOB clob = (CLOB)rs.getClob(i+1);
				Writer writer = clob.getCharacterOutputStream();  
					
				writer.write(val[i]);  
				writer.flush();   
				writer.close();*/ 
			}
		}
		return new Object[] { st, rs };
	}
	
	/**将大字分串分成n个数组*/
	private String[] pagedClobStream(String value){
		 value = StringUtil.checkStr(value) ? value : "";
		 int PAGE_SIZE = 1024 * 10 * 1;
		 byte[] dataes;
		 int length;
		 int pageCount;
		 dataes = value.getBytes();
	     length = dataes.length;
		 pageCount = (length % PAGE_SIZE == 0) ? (length / PAGE_SIZE) : (length / PAGE_SIZE) + 1;
		
		String[] arr = new String[pageCount];
		for (int i = 1; i <= pageCount; i++) {
             int sheYuByte = length - (PAGE_SIZE * (i - 1));
            byte[] b = null;
             if (sheYuByte > PAGE_SIZE){
                 b = new byte[PAGE_SIZE];
             } 
            else {
                 b = new byte[sheYuByte];
             }
             for (int j = 0; j < b.length; j++){
                 b[j] = dataes[(i - 1) * PAGE_SIZE + j];
             }
             arr[i - 1] = new String(b);
         }
         
         return arr;
	}
	
	public String getDate() {
		return "sysdate";
	}

	public String isNull(String property, String o) {
		return "nvl(" + property + "," + o + ")";
	}

	public String subString(String str, int startIndex, int endIndex) {
		return "subStr(" + str + "," + new Integer(startIndex).toString() + ","
				+ new Integer(endIndex).toString() + ")";
	}

	public String stringToDatetime(String str) {
		return "to_date( '" + (str.replace(".0", "")) + "','YYYY-MM-DD HH24:MI:SS')";
	}

	public String stringToDate(String str) {
		return "to_date( '" + str + "','YYYY-MM-DD')";
	}

	public String getSecondsBetweenDays(String dateColumnName) {
		return "((sysdate - " + dateColumnName + ") * 24 * 60 * 60)";
	}

	public String getUpperFunction(String columnName) {
		return "UPPER(" + columnName + ")";
	}

	public String getDateValueHour(String col) {
		return "concat(to_char(" + col + ",'hh24'),':00')";
	}

	public String getGroupByHour(String col) {
		return "to_char(" + col + ",'hh24'),to_char(" + col + ",'YYYY/MM/DD')";
	}

	public String getDateValueDay(String col) {

		return "to_char(" + col + ",'YYYY/MM/DD')";
	}

	public String getGroupByDay(String col) {
		return "to_char(" + col + ",'YYYY/MM/DD')";
	}

	public String[] getIdentityKey(String primaryKeyCol, String domain) {
		return new String[] { primaryKeyCol + ",", "SEQ_COMMON_ID.NEXTVAL," };
	}

	/**
	 * update by Zhanweibin 2011-12-28
	 * 加入了yyyy-MM-dd格式处理
	 */
	public String datetimeTostring(String col, String format) {
		
		if (StringUtil.checkStr(format)) {
			if("yyyy-MM-dd".equals(format)){
				format = "YYYY-MM-DD";
			//add by liuchaobiao 2012/9/7
			}else if("yyyy-MM".equals(format)){
				format = "YYYY-MM";
			}else if("HH:mm:ss".equals(format)){
				format = "HH24:MI:SS";
			}else if("MM-dd".equals(format)){
				format = "MM-DD";
			}else if("HH:mm".equals(format)){
				format = "HH24:MI";
			}else if("mm:ss".equals(format)){
				format = "MI:SS";
			}else if("yyyy".equals(format)){
				format = "YYYY";
			}else if("MM".equals(format)){
				format = "MM";
			}else if("dd".equals(format)){
				format = "DD";
			}else if("HH".equals(format)){
				format = "HH24";
			}else if("mm".equals(format)){
				format = "MI";
			}else if("ss".equals(format)){
				format = "SS";
			}else if("yyyy-MM-dd HH:mm:ss.ms".equals(format)){
				format = "YYYY-MM-DD HH24:MI:SS.FF";
			}else{
				format = "YYYY-MM-DD HH24:MI:SS";
			}
		}else {
			format = "YYYY-MM-DD HH24:MI:SS";
		}
		//format = StringUtil.checkStr(format) ? format : "'YYYY-MM-DD HH24:MI:SS'";
		return "to_char(" + col + ",'" + format + "')";
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
		return "to_char(" + number + ")";
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
		return null;
	}

	/**
	 * 数据库的字符串类型转换为整型类型
	 * 
	 * oracle使用to_number()函数，参数为要转换的字符串
	 * @param 需要转换的字段
	 * @autho Zhanweibin 2011-12-27
	 * @return
	 */
	public String convertStringToInt(String column) {
		return "to_number(" + column + ")";
	}
	
	/**
	 * 得到数据库的类型
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return 数据库小写名称
	 */
	public String getDBType() {
		return "oracle";
	}
	
	/**
	 * 获取数据库用户表信息
	 * @author tanjianwen
	 * @return 用户表信息查询语句
	 */
	public String getUserTableInfo(String tableNames, String ... args){
		String account = checkArgs(args, 0)?(args[0].toUpperCase()):"";
		
		StringBuffer sql = new StringBuffer();
		sql.append("select utcol.TABLE_NAME as \"sTableName\", utcom.COMMENTS as \"sTableComment\",  ");
		sql.append("utcol.COLUMN_NAME as \"sFieldName\", uccom.COMMENTS as \"sColumnComment\", ");
		sql.append("utcol.NULLABLE as \"iRequired\", utcol.DATA_TYPE as \"iFieldType\", ");
		sql.append("utcol.DATA_LENGTH as \"iFieldLength\", utcol.DATA_PRECISION as \"iFieldPrec\", utcol.DATA_SCALE as \"iFieldScale\", ");
		sql.append("uc.CONSTRAINT_TYPE as \"sConstraintType\",utcol.DATA_DEFAULT as \"sDefaultValue\" ");
		sql.append("from user_tab_comments utcom inner join user_tab_cols utcol ");
		sql.append("on utcom.TABLE_NAME = utcol.TABLE_NAME  ");
		sql.append("inner join user_col_comments uccom  ");
		sql.append("on utcol.TABLE_NAME = uccom.TABLE_NAME and utcol.COLUMN_NAME = uccom.COLUMN_NAME ");
		sql.append(" ## ");
		sql.append("left join user_cons_columns uccol  ");
		sql.append("inner join user_constraints uc  ");
		sql.append("on uccol.CONSTRAINT_NAME = uc.CONSTRAINT_NAME and uc.CONSTRAINT_TYPE <> 'C' ");
		sql.append("on utcol.TABLE_NAME = uccol.TABLE_NAME and utcol.COLUMN_NAME = uccol.COLUMN_NAME  ");
		//sql.append("where not exists(select 1 from tbFdTable where sTableName = utcol.TABLE_NAME) ");
		sql.append("where 1=1 ");
		if (StringUtil.checkStr(tableNames)) {
			StringBuffer tblNames = new StringBuffer();
			for (String tableName : tableNames.split(",")) {
				if (tblNames.toString().length() > 0) tblNames.append(",");
				tblNames.append("'").append(tableName.toUpperCase()).append("'");
			}
			sql.append("and utcom.TABLE_NAME in (").append(tblNames.toString()).append(")");
		}
		
		String temp = ""; 
		if(account.length()>0){
			temp = "inner join all_tables atbl on atbl.TABLE_NAME = utcol.TABLE_NAME";
			sql.append(" and atbl.OWNER='").append(account).append("'");
		}
		
		sql.append("order by utcol.TABLE_NAME,utcol.column_id");
		return sql.toString().replace("##", temp);
	}
	
	/**
	 * 根据条件查询系统表
	 */
	public String selectSystemTable(Map map) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ut.TABLE_NAME as \"sTableName\", utc.COMMENTS as \"sComment\" ");
		sql.append("from user_tables ut left join user_tab_comments utc on ut.TABLE_NAME = utc.TABLE_NAME ");
		sql.append("where 1=1 ");
		if (StringUtil.checkObj(map.get("sTableName")))
			sql.append("and utc.TABLE_NAME like '%").append(map.get("sTableName")).append("%' ");
		sql.append("and not exists(select 1 from tbFdTable where sTableName = ut.TABLE_NAME) ");
		sql.append("order by ut.TABLE_NAME");
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
		if ("INTEGER".equals(fieldType) || 
				(("NUMBER".equals(fieldType) || "DECIMAL".equals(fieldType)) && ("0".equals(precNum) || "null".equals(precNum)))) {
			return "1";
		} else if ((("NUMBER".equals(fieldType) || "DECIMAL".equals(fieldType)) 
				|| "FLOAT".equals(fieldType) || "REAL".equals(fieldType))) {
			return "2";
		} else if ("CHAR".equals(fieldType) || "VARCHAR2".equals(fieldType) 
				|| "NCHAR".equals(fieldType) || "NVARCHAR2".equals(fieldType) 
				|| "LONG".equals(fieldType) || "CLOB".equals(fieldType) 
				|| "NCLOB".equals(fieldType)) {
			return "3";
		} else if ("DATE".equals(fieldType)) {
			return "4";
		} else {
			return "0";
		}
	}
	
	/**
	 * 创建表
	 * @param map
	 * @return
	 */
	public String createTable(String tableName, List<Map> list) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table ").append(tableName).append(" (");
		for (Map m : list) {
			if ("1".equals(m.get("iIsPrimaryKey"))) {
				sql.append(StringUtil.toString(m.get("sFieldName"))).append(" ").append(getDataType(m)).append(" primary key");
			}
		}
		sql.append(")");
		return sql.toString();
	}
	
	/**
	 * 添加列
	 * @param tableName 表名
	 * @param dataList 列数据
	 * @return
	 */
	public String addField(String tableName, List<Map> dataList) {
		StringBuffer cols = new StringBuffer();
		//sql.append("alter table ").append(tableName).append(" add(");
		for (Map<String,String> m : dataList) {
			if (!"I".equals(m.get("flag")) || "1".equals(m.get("iIsPrimaryKey"))) continue;
			if (cols.toString().length() > 0) cols.append(",");
			String sRequired = StringUtil.checkObj(m.get("iRequired"))?"1".equals(m.get("iRequired"))?"not null":"null":"null";
			cols.append(m.get("sFieldName")).append(" ").append(getDataType(m)).append(" ");
			if (StringUtil.checkObj(m.get("iRequired")) && "1".equals(m.get("iRequired"))) {
				cols.append(" not null");
			} else {cols.append(" null");}
		}
		if (cols.toString().length() == 0) {
			return null;
		}
		return "alter table "+tableName+" add("+cols.toString()+")";
	}
	
	/**
	 * 修改列
	 * @param tableName 表明
	 * @param dataList 列数据
	 * @return
	 */
	public String[] editField(String tableName, List<Map> dataList) {
		List<String> list = new ArrayList<String>();
		StringBuffer sql = null;
		for (Map<String,String> m : dataList) {
			if (!"U".equals(m.get("flag"))) continue;
			sql = new StringBuffer();
			sql.append("alter table ").append(tableName).append(" add(");
			String sRequired = StringUtil.checkObj(m.get("iRequired"))?"1".equals(m.get("iRequired"))?"not null":"null":"null";
			sql.append(m.get("sFieldName")).append(" ").append(getDataType(m)).append(" ");
			if (StringUtil.checkObj(m.get("iRequired")) && "1".equals(m.get("iRequired"))) {
				sql.append(" not null");
			} else {sql.append(" null");}
			sql.append(")");
			list.add(sql.toString());
		}
		
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * 删除要修改的列
	 * @param tableName
	 * @param dataList
	 * @return
	 */
	public String[] deleteEditField(String tableName, List<Map> dataList) {
		StringBuffer cols = new StringBuffer();
		List<String> sql = new ArrayList<String>();
		for (Map<String,String> m : dataList) {
			sql.add("alter table "+tableName+" drop column "+StringUtil.toString(m.get("sFieldName"))+"");
		}
		
		return sql.toArray(new String[sql.size()]);
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
			sql.add("alter table "+tableName+" drop column "+StringUtil.toString(m.get("sFieldName"))+"");
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
				dataType = "NUMBER(" + String.valueOf(m.get("iFieldLength")) + ")";
			} else {
				dataType = "NUMBER(15)";
			}
		} else if("2".equals(m.get("iFieldType"))) {//小数
			if (StringUtil.checkObj(m.get("iFieldLength")) && StringUtil.checkObj(m.get("iFieldPrec"))) {
				dataType = "NUMBER(" + String.valueOf(m.get("iFieldLength")) + "," + String.valueOf(m.get("iFieldPrec")) + ")";
			} else if (StringUtil.checkObj(m.get("iFieldLength"))) {
				dataType = "NUMBER(" + String.valueOf(m.get("iFieldLength")) + ",2)";
			} else if (StringUtil.checkObj(m.get("iFieldPrec"))) {
				dataType = "NUMBER(15," + String.valueOf(m.get("iFieldPrec")) + ")";
			} else {
				dataType = "NUMBER(15,2)";
			}
		} else if("3".equals(m.get("iFieldType"))) {
			if (StringUtil.checkObj(m.get("iFieldLength")) && Integer.parseInt(m.get("iFieldLength")) <= 4000) {
				dataType = "VARCHAR2(" + String.valueOf(m.get("iFieldLength")) + ")";
			} else if(StringUtil.checkObj(m.get("iFieldLength")) && Integer.parseInt(m.get("iFieldLength")) > 4000){
				dataType = "CLOB";
			} else {
				dataType = "VARCHAR2(200)";
			}
		} else {
			dataType = "date";
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
	 * 复制表结构,创建临时,可同时带上数据
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
	public String getCheckTableSql(String tableName, String ...args) {
		String account = checkArgs(args, 0)?(args[0].toUpperCase()):"";
		/*String sql="";
		sql = "select 1 from "+account+"user_tab_comments t where t.table_name = '"
			+ tableName.toUpperCase() + "'";*/
		String sql="select 1 from user_tab_comments a ## WHERE a.TABLE_NAME='"+tableName.toUpperCase()+"'";
		
		String leftJoin ="";
		if(account.length()>0){//指定某账号下的表
			leftJoin =" left join all_tables b on a.TABLE_NAME=b.TABLE_NAME";
			sql+=" and b.OWNER='"+account+"' ";
		}
		sql = sql.replace("##", leftJoin);
		return sql;
	}
	
	/**
	 * 获取检查是否存在相同的表名称的sql语句,后面pin in语句
	 */
	public String getCheckTablesSql() {
		String sql="";
		sql = "select table_name as \"tableName\" from user_tab_comments t where t.table_name ";
		return sql;
	}
	
	public String indexOf(String column, String str) {
		return "inStr(" + column + "," + str + ")";
	}
	
	public String subString(String column,String startExp,String endExp){
		return "subStr(" + column + "," + startExp + "," + endExp + ")";
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
		if(StringUtil.checkStr(ftype)){
			if(ftype.equals("1")){
				fieldType = (flen==0 && decpos==0)? " number " : (" number("+(flen==0?22:flen)+","+decpos+") ");
				//fieldType=" number("+(flen==0?15:flen)+","+decpos+") ";
			}else if(ftype.equals("2")){
				fieldType=" number(22) ";//因为整数Integer系统表记录成number(22),所以这里整数也将返回number
			}else if(ftype.equals("3")){
				if(flen>4000)flen=4000; //varchar2最大只能设置4000
				fieldType=" varchar2("+flen+") ";
			}else if(ftype.equals("4") || ftype.equals("7")){
				fieldType=" date ";
			}else if(ftype.equals("5")){
				fieldType=" clob ";
			}else if(ftype.equals("6")){
				if(flen>2000)flen=2000;
				fieldType=" char("+flen+") ";//char最大长度只能在2000以内
			}
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
	
	/**
	 * 拿到修改列名sql
	 * @param map
	 * @return
	 */
	public String getRenameColumnSql(Map map) {
		String tableName = StringUtil.toString(map.get("tableName"));
		String oldName = StringUtil.toString(map.get("oldFieldName"));
		String newName = StringUtil.toString(map.get("newFieldName"));
		String sql = "ALTER TABLE "+tableName+" RENAME COLUMN "+oldName+" TO "+newName+"";
		return sql;
	}
	
	public String charIndex(String str,String str1){
		return "instr("+str1+","+str+")";
	}
	
	public String dataLength(String str) {
		return "length("+str+")";
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
		return "abs(("+dateColumnName1+" - " + dateColumnName2 + ") * 24 * 60 * 60)";
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
		return "((" + endColumnName + " - " + beginColumnName + ") * 24 * 60 * 60)";
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
	
	/**检查数组的第n个无素是否为空*/
	private boolean checkArgs(String[] args, int n){
		return (args!=null && args.length>n && StringUtil.checkStr(args[n]));
	}
}
