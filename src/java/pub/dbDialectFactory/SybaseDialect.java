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
 * Sybase ����
 * 
 * @author gaotao 2011-08-09
 */
class SybaseDialect implements Dialect {
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
		
        /*  �������ʱ���, ��Ϊ���ֹ���ʱ������ͻ������
         	��sybase ase12.5 ֻ����ʱ��ǰ12λ��ΪΨһ��ʶ��������ʱ�����Ȼ�����˳�ͻ
        	String tempTableName = "#Paging"
                + DateUtil.parseToString(new Date(), "yyyyMMddHHmmssSSSS")
                + Math.round(Math.random() * 100);*/
		/*����UUID,���ڱ������ܳ�30λ��ȡ*/
		String tempTableName = "#P"+UUID.randomUUID().toString().replace("-", "").substring(0,27);
		
		sql = sql.replaceAll("\\s{2,}", " ");
		int index=sql.toUpperCase().indexOf(" FROM ");
		StringBuffer sb=new StringBuffer(sql).insert(index, (" INTO " + tempTableName));
		
		//�����distinct
		index=sb.toString().toUpperCase().indexOf("SELECT DISTINCT");
		if(index>=0){
			sb.insert(index+15, " iRowId =identity(10), ");
		}else {
			index=sb.toString().toUpperCase().indexOf("SELECT");
			sb.insert(index+7, "iRowId =identity(10), ");
		}
		
		//�����top
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
		logger.info("sql��ѯǰ��"+sdf.format(new Date()));
		logger.info("sql��䣺"+sb.toString());
		logger.info("sql��ѯ��"+sdf.format(new Date()));*/
		ResultSet rs = sta.executeQuery();
		return new Object[] {sta, rs };
	}
	
	/**
	 * ȡ����
	 * ��ͬ���ݿ⣬ȡ����������һ�£�sybase,mysql�����ô洢���̣�oracle,DB2������
	 * ͬ��ҳ����һ����Statment��������ڱط���ʵ������ʵ����
	 * ͬ��ҳ����һ����Ϊ�˱�֤�ܹ���DataBaseUtilͳһ�ر����ݿ�������󣬹�Ҳ����һ����������
	 * ������2��Ԫ�أ���һ��Ԫ����Statement���ڶ���Resultset
	 * cachePrimaryKeys:���������ĸ������˲�����Խ�mysql,sybase��Ч
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
	 * ������yyyy-MM-dd��ʽ����
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
		return "convert(varchar," + number + ")";
	}
	
	public String cancelOrderBy(String sql){
		return sql.replace("order ", "-- order ").replace("ORDER ", "-- order ")+" \n";
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
		return "convert(numeric(18,0), " + column + ")";
	}
	
	/**
	 * �õ����ݿ������
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return ���ݿ�Сд����
	 */
	public String getDBType() {
		return "sybase";
	}
	
	/**
	 * ��ȡ���ݿ��û�����Ϣ
	 * @author Zhanweibin
	 * @return �û�����Ϣ��ѯ���
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
	 * ����������ѯϵͳ��
	 */
	public String selectSystemTable(Map map) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("select obj.name as \"sTableName\", NULL as \"sComment\" ");
		sql.append("from sysobjects obj where 1=1 and obj.type = 'U' ");//UΪ�û���
		if (StringUtil.checkObj(map.get("sTableName")))
			sql.append("and obj.name like '%").append(map.get("sTableName")).append("%' ");
		sql.append("and not exists(select 1 from tbFdTable where sTableName = obj.name) ");
		sql.append("order by obj.name");
		return sql.toString();
	}
	
	/**
	 * �������ݿ��ֶ����ͻ�ȡ�ֶι�������
	 * (����1��С��2���ַ���3������ʱ��4������5��ʱ��6��ʲô������0)
	 * @author tanjianwen
	 * @param fieldType �ֶ����ݿ�����
	 * @param precNum �ֶξ���
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
	 * ������
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
		StringBuffer cols = new StringBuffer();
		List<String> sql = new ArrayList<String>();
		for (Map<String,String> m : dataList) {
			if (!"D".equals(m.get("flag"))) continue;
			sql.add("alter table "+tableName+" drop "+StringUtil.toString(m.get("sFieldName"))+"");
		}
		
		return sql.toArray(new String[sql.size()]);
	}
	
	/**
	 * ��ȡ��������
	 * @param m
	 * @return
	 */
	private String getDataType(Map<String,String> m) {
		String dataType = "";
		if ("1".equals(m.get("iFieldType"))) {//����
			if (StringUtil.checkObj(m.get("iFieldLength"))) {
				dataType = "numeric(" + String.valueOf(m.get("iFieldLength")) + ")";
			} else {
				dataType = "numeric(15)";
			}
		} else if("2".equals(m.get("iFieldType"))) {//С��
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
	 * ���Ʊ�ṹ,�����±�,��ͬʱ��������
	 * @param fromTable Դ�����
	 * @param toTable �±����
	 * @param fields ��Ҫ���Ƶ��ֶΣ�����ֶ��ö��Ÿ��������û�У������������ֶ�
	 * @param where �и��������������������ݣ�û��������������Ʊ�ṹ
	 */
	public String copyTableStructure(String fromTable, String toTable,String fields,String where) {
		//��: select *  into AA_temp from AA where 1=2
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
	 * ���Ʊ�ṹ,�����±�,��ͬʱ��������
	 * @param fromTable Դ�����
	 * @param toTable �±����(SybaseҪ��#Ϊǰ׺)
	 * @param fields ��Ҫ���Ƶ��ֶΣ�����ֶ��ö��Ÿ��������û�У������������ֶ�
	 * @param where �и��������������������ݣ�û��������������Ʊ�ṹ
	 */
	public String copyTempTableStructure(String fromTable, String toTable,String fields,String where) {
		//��: select *  into AA_temp from AA where 1=2
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
	 * ��ȡ����Ƿ������ͬ�ı����Ƶ�sql���
	 */
	public String getCheckTableSql(String tableName, String ... args) {
		String sql="";
		sql = "select 1 from sysobjects t where t.name = '"
			+ tableName + "'";
		return sql;
	}
	
	/**
	 * ��ȡ����Ƿ������ͬ�ı����Ƶ�sql���,����pin in���
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
	 * �����ֶ�����,���ʹ��
	 * @param ftype ��������
	 * @param flen �ֶγ���
	 * @param decpos С��λ����
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
	
	public String charIndex(String str,String str1){
		return "charindex("+str1+","+str+")";
	}
	
	public String dataLength(String str) {
		return "datalength("+str+")";
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
		String sql = "sp_rename  '"+tableName+"."+oldName+"', "+newName;
		return sql;
	}
	
	/**
	 * insert ���ƴ�������ֶΣ�ȡֵSQL���֣��˷�����ͨ�ñ�2.0����
	 * @param tableName ����
	 * @return �������3��Ԫϵ�������ʵ�����ʵ�� 
	 */
	public String[] getIncrementSQL(String tableName) {
		return new String []{
				"declare @a numeric(15) begin execute @a=SP_GET_ID_EX '"+tableName+"' ",
				"@a",
				"end "
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
	 * ���������ֶμ���������
	 * @param beginColumnName ��ʼʱ��
	 * @param endColumnName ����ʱ��
	 * @return �������
	 * @author tangxiaolong
	 * @version 2013-11-1
	 */
	public String getSecondsBetweenDays(String beginColumnName, String endColumnName){
		return "datediff(ss, " + beginColumnName + ", " + endColumnName + ")";
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
