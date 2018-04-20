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
 * Oracle ����
 * 
 * @author gaotao 2011-08-09
 */
class OracleDialect implements Dialect {
	/**
	 * �洢���̷�ҳ����
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
		v_sql.append(" as \"totalCount\", tm2.* from (select * from ("); //�ټ�һ�㣬����ʱ���ݲŲ������
		v_sql.append(" select rownum as rn, m1.* from (");
		v_sql.append(sql);
		v_sql.append(" ) m1 ) tm1 where tm1.rn <="); //���кŹ��˷����ڲ��Ӳ�ѯ���ܽ�һ�������Ӳ�ѯ������������
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
	public synchronized Object[] getKeyId(Connection conn,int cachePrimaryKeys) throws SQLException {
		String sql = "select SEQ_COMMON_ID.NEXTVAL from dual";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		return new Object[] { st, rs };
	}

	/**
	 * �޸�Clob��text���ı��ֶ�
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
				String[] arrx = this.pagedClobStream(va);//Ҫ����ڴ�����쳣������Ѿ��Դ��byte[]���з�ҳ
	                if (arrx != null){
	                    for (int j = 0; j < arrx.length; j++) {
	                    	writer.write(arrx[j]);
	                    	writer.flush();//Ҫ����ڴ�����쳣,����һҳһҳ��flush()�����ݿ�
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
	
	/**�����ִַ��ֳ�n������*/
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
	 * ������yyyy-MM-dd��ʽ����
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
		return "to_char(" + number + ")";
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
		return null;
	}

	/**
	 * ���ݿ���ַ�������ת��Ϊ��������
	 * 
	 * oracleʹ��to_number()����������ΪҪת�����ַ���
	 * @param ��Ҫת�����ֶ�
	 * @autho Zhanweibin 2011-12-27
	 * @return
	 */
	public String convertStringToInt(String column) {
		return "to_number(" + column + ")";
	}
	
	/**
	 * �õ����ݿ������
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return ���ݿ�Сд����
	 */
	public String getDBType() {
		return "oracle";
	}
	
	/**
	 * ��ȡ���ݿ��û�����Ϣ
	 * @author tanjianwen
	 * @return �û�����Ϣ��ѯ���
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
	 * ����������ѯϵͳ��
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
	 * �������ݿ��ֶ����ͻ�ȡ�ֶι�������
	 * (����1��С��2���ַ���3������ʱ��4������5��ʱ��6��ʲô������0)
	 * @author tanjianwen
	 * @param fieldType �ֶ����ݿ�����
	 * @param precNum �ֶξ���
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
	 * ������
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
	 * �����
	 * @param tableName ����
	 * @param dataList ������
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
	 * �޸���
	 * @param tableName ����
	 * @param dataList ������
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
	 * ɾ��Ҫ�޸ĵ���
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
			sql.add("alter table "+tableName+" drop column "+StringUtil.toString(m.get("sFieldName"))+"");
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
				dataType = "NUMBER(" + String.valueOf(m.get("iFieldLength")) + ")";
			} else {
				dataType = "NUMBER(15)";
			}
		} else if("2".equals(m.get("iFieldType"))) {//С��
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
	 * ���Ʊ�ṹ,������ʱ,��ͬʱ��������
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
	public String getCheckTableSql(String tableName, String ...args) {
		String account = checkArgs(args, 0)?(args[0].toUpperCase()):"";
		/*String sql="";
		sql = "select 1 from "+account+"user_tab_comments t where t.table_name = '"
			+ tableName.toUpperCase() + "'";*/
		String sql="select 1 from user_tab_comments a ## WHERE a.TABLE_NAME='"+tableName.toUpperCase()+"'";
		
		String leftJoin ="";
		if(account.length()>0){//ָ��ĳ�˺��µı�
			leftJoin =" left join all_tables b on a.TABLE_NAME=b.TABLE_NAME";
			sql+=" and b.OWNER='"+account+"' ";
		}
		sql = sql.replace("##", leftJoin);
		return sql;
	}
	
	/**
	 * ��ȡ����Ƿ������ͬ�ı����Ƶ�sql���,����pin in���
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
	 * �����ֶ�����,���ʹ��
	 * @param ftype ��������
	 * @param flen �ֶγ���
	 * @param decpos С��λ����
	 * @return
	 */
	public String parseType(String ftype,int flen,int decpos){
		String fieldType = "";
		if(StringUtil.checkStr(ftype)){
			if(ftype.equals("1")){
				fieldType = (flen==0 && decpos==0)? " number " : (" number("+(flen==0?22:flen)+","+decpos+") ");
				//fieldType=" number("+(flen==0?15:flen)+","+decpos+") ";
			}else if(ftype.equals("2")){
				fieldType=" number(22) ";//��Ϊ����Integerϵͳ���¼��number(22),������������Ҳ������number
			}else if(ftype.equals("3")){
				if(flen>4000)flen=4000; //varchar2���ֻ������4000
				fieldType=" varchar2("+flen+") ";
			}else if(ftype.equals("4") || ftype.equals("7")){
				fieldType=" date ";
			}else if(ftype.equals("5")){
				fieldType=" clob ";
			}else if(ftype.equals("6")){
				if(flen>2000)flen=2000;
				fieldType=" char("+flen+") ";//char��󳤶�ֻ����2000����
			}
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
	
	/**
	 * �õ��޸�����sql
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
		return "abs(("+dateColumnName1+" - " + dateColumnName2 + ") * 24 * 60 * 60)";
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
		return "((" + endColumnName + " - " + beginColumnName + ") * 24 * 60 * 60)";
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
	
	/**�������ĵ�n�������Ƿ�Ϊ��*/
	private boolean checkArgs(String[] args, int n){
		return (args!=null && args.length>n && StringUtil.checkStr(args[n]));
	}
}
