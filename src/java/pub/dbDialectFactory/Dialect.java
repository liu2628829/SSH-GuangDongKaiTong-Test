package pub.dbDialectFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * sql���ػ��ӿ�
 * 
 * @author fans
 * ���¼�¼:
 * 2012/3/29 gaotao �ӷ���getDataByPageEoms ,����˵����ע��
 */
public interface Dialect {
	/**
	 * ��ҳ����
	 * ���oracle���ô洢���̣�ȡ�����Ҫ���α�
	 * ���oracle����sql��ʽ����Ҫ����������Ҫȡ��ҳ�����ܳɼ�¼����Ȼ����ƴ��ҳ���
	 * ����������洢����SQL����ͨ��ҳSQL��Ӧ��д�ڹ���ʵ����
	 * ���Ӷ���conn�����Դ���ߴ��룬��statement���󣬱����ڷ���ʵ������ʵ��������������һ���Դ�sql,��ͨ��conn���ɶ���
	 * ����ʵ������ʵ���Ķ����ֲ����ȹر����ӣ������ٵ�����(DataBaseUtil)���޷����������
	 * Ϊ�˱�֤�ܹ���DataBaseUtilͳһ�ر����ݿ��������,���Է���һ����������
	 * ������2��Ԫ�أ���һ��Ԫ����Statement���ڶ���Resultset
	 */
	public Object[] getDataByCallableStatement(Connection conn,String sql,
			int page, int limit) throws SQLException;

	/**
	 * ����prepareStatement���з�ҳ��ѯ
	 * @throws SQLException
	 */
	public Object[] getDataByPageEoms(Connection conn,String sql,
			int page, int limit,List list)throws SQLException;
	
	/**
	 * ȡ����
	 * ��ͬ���ݿ⣬ȡ����������һ�£�sybase,mysql�����ô洢���̣�oracle,DB2������
	 * ͬ��ҳ����һ����Statment��������ڱط���ʵ������ʵ����
	 * ͬ��ҳ����һ����Ϊ�˱�֤�ܹ���DataBaseUtilͳһ�ر����ݿ�������󣬹�Ҳ����һ����������
	 * ������2��Ԫ�أ���һ��Ԫ����Statement���ڶ���Resultset
	 * cachePrimaryKeys:���������ĸ������˲�����Խ�mysql,sybase��Ч
	 */
	public Object[] getKeyId(Connection conn,int cachePrimaryKeys)throws SQLException;
	
	/**
	 * �޸�Clob��text���ı��ֶ�
	 */
	public Object[] writeClobOrText(Connection conn,String tableName,String where,String cols[],String val[])throws Exception;
	
	/**
	 * ���ݿ⵱ǰϵͳʱ��
	 */
	public String getDate();

	/**
	 * �ж��Ƿ�Ϊ��
	 */
	public String isNull(String property, String o);

	/**
	 * ��ȡ�ַ���
	 */
	public String subString(String str, int startIndex, int endIndex);

	/**
	 * �ַ���ת������+ʱ�䣬Sybase mysql ֱ���õ�������ס��oracleҪת�����ڸ�ʽ ��ʽYYYY-MM-DD hh:mm:ss
	 */
	public String stringToDatetime(String str);

	/**
	 * �ַ���ת�����ڣ�Sybase mysql ֱ���õ�������ס��oracleҪת�����ڸ�ʽ ��ʽYYYY-MM-DD
	 */
	public String stringToDate(String str);

	/**
	 * ��ǰʱ���ȥĳʱ��(ʱ�����͵��ֶ�����)��������
	 */
	public String getSecondsBetweenDays(String dateColumnName);

	/**
	 * ת��д
	 */
	public String getUpperFunction(String columnName);

	/**
	 * ��ʵ�������ʵ��
	 */
	public String getDateValueHour(String col);

	/**
	 * ��ʵ�������ʵ��
	 */
	public String getGroupByHour(String col);

	/**
	 * ��ʵ�������ʵ��
	 */
	public String getDateValueDay(String col);

	/**
	 * ��ʵ�������ʵ��
	 */
	public String getGroupByDay(String col);
	
	/**
	 * ���ڰ���ʽת���ַ��� 
	 * �����ʽ�ַ���Ϊ��Ĭ�ϸ�ʽΪYYYY-MM-DD hh:mm:ss
	 * @param col �ֶ���
	 * @param format ������ö�٣�
	 * 				 ����ʱ�䣺""��YYYY-MM-DD hh:mm:ss 
	 *               ���ڣ�yyyy-MM-dd
	 *               ʱ�䣺HH:mm:ss
	 *               ���£�yyyy-MM
	 *               ���գ�MM-dd
	 *               ʱ�֣�HH:mm
	 *               ���룺mm:ss
	 */
	public String datetimeTostring(String col, String format);

	/**
	 * ����������,����һ������Ϊ2�����飬��0��ֵ�������У���1��ֵ������ȡֵ��ʽ
	 * ��oracle���⣬������Ӧ�ö��Ƿ��������մ�
	 * �˷���Ŀǰ���ڱ��������־ʱ���õ�
	 * @param primaryKeyCol
	 * @return
	
	public String[] getIdentityKey(String primaryKeyCol,String domain); */
	
	/**
	 * �������ݿ���ַ�����ƴ�ӽ��
	 * sybase �� oracle 'a'||'b' = 'ab'
	 * mysql concat������concat('a','b') = 'ab'
	 * @author ̷����
	 * @return
	 */
	public String getConcatResult(String... strs);
	
	/**
	 * �������ݿ���ַ�����ƴ�ӽ��
	 * @param splitChar ƴ��ʱ���м���Զ���ļ����
	 * @param strs ����ֶ������൱��һ������
	 * @return ƴ�Ӵ��Ľ��
	 */
	public String getConcatResult2(String splitChar, String... strs);
	
	/**
	 * ���ݿ����������ת�ַ�������
	 * @param ��Ҫת��������
	 * @author ̷����
	 * @return 
	 */
	public String convertNumberToString(String number);
	
	/**
	 * ȡ��order by,����֪��sql,��Ϊ�Ӳ�ѯ��sybase�ⲻ�����Ӳ�ѯ����order by 
	 */
	public String cancelOrderBy(String sql);
	
	/**
	 * ���ȴ�Ӧ�û���ȡ������oracle,DB2������Ӧ���ڻ�������������Ҫ��������,�ʼӴ˷���
	 * @param domain ����ԴID
	 * @return
	 */
	public String getInitPrimaryKeys(String domain);
	
	/**
	 * ���ݿ���ַ�������ת��Ϊ��������
	 * 
	 * @param ��Ҫת�����ֶ�
	 * @autho Zhanweibin 2011-12-27
	 * @return
	 */
	public String convertStringToInt(String column);
	
	/**
	 * �õ����ݿ������
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return ���ݿ�Сд����
	 */
	public String getDBType();
	
	/**
	 * ��ȡ���ݿ��û�����Ϣ
	 * @author tanjianwen
	 * @return �û�����Ϣ��ѯ���
	 */
	public String getUserTableInfo(String tableNames, String ... args);
	
	/**
	 * ����������ѯϵͳ��
	 * @param map
	 * @return
	 */
	public String selectSystemTable(Map map);
	
	/**
	 * �������ݿ��ֶ����ͻ�ȡ�ֶι�������
	 * (����1��С��2���ַ���3������ʱ��4������5��ʱ��6)
	 * @author tanjianwen
	 * @param fieldType �ֶ����ݿ�����
	 * @param precNum �ֶξ���
	 * @return 
	 */
	public String getDBTblFieldType(String fieldType, String precNum);
	
	/**
	 * ������
	 * @param map
	 * @return
	 */
	public String createTable(String tableName, List<Map> list);
	
	/**
	 * �����
	 * @param tableName ����
	 * @param dataList ������
	 * @return
	 */
	public String addField(String tableName, List<Map> dataList);
	
	/**
	 * �޸���
	 * @param tableName ����
	 * @param dataList ������
	 * @return
	 */
	public String[] editField(String tableName, List<Map> dataList);
	
	/**
	 * ɾ��Ҫ�޸ĵ���
	 * @param tableName
	 * @param dataList
	 * @return
	 */
	public String[] deleteEditField(String tableName, List<Map> dataList);
	
	/**
	 * ɾ����
	 * @param tableName
	 * @param dataList
	 * @return
	 */
	public String[] deleteField(String tableName, List<Map> dataList);
	
	/**
	 * ���Ʊ�ṹ,�����±�,��ͬʱ��������
	 * @param fromTable Դ�����
	 * @param toTable �±����
	 * @param fields ��Ҫ���Ƶ��ֶΣ�����ֶ��ö��Ÿ��������û�У������������ֶ�
	 * @param where �и��������������������ݣ�û��������������Ʊ�ṹ
	 */
	public String copyTableStructure(String fromTable,String toTable,String fields,String where);
	
	/**
	 * ���Ʊ�ṹ,������ʱ��,��ͬʱ��������
	 * @param fromTable Դ�����
	 * @param toTable �±����(SybaseҪ��#Ϊǰ׺)
	 * @param fields ��Ҫ���Ƶ��ֶΣ�����ֶ��ö��Ÿ��������û�У������������ֶ�
	 * @param where �и��������������������ݣ�û��������������Ʊ�ṹ
	 */
	public String copyTempTableStructure(String fromTable,String toTable,String fields,String where);

	 /**
	 * ����str������column�е�λ��
	 * @param column
	 * @param str
	 * @return
	 */
	public String indexOf(String column,String str);
	
	/**
	 * ��ȡ�ַ���
	 * @param column ����(�ַ���)
	 * @param startExp ����ʼ�������ʽ
	 * @param endExp �ý����������ʽ
	 * @return
	 */
	public String subString(String column,String startExp,String endExp);
	
	/**
	 * ��ȡ����Ƿ������ͬ�ı����Ƶ�sql���
	 */
	public String getCheckTableSql(String tableName, String ... args);
	
	/**
	 * ��ȡ����Ƿ������ͬ�ı����Ƶ�sql���,����pin in���
	 */
	public String getCheckTablesSql();
	
	/**
	 * �����ֶ�����,���ʹ��
	 * @param ftype ��������
	 * @param flen �ֶγ���
	 * @param decpos С��λ����
	 * @return
	 */
	public String parseType(String ftype,int flen, int decpos);
	
	/**
	 * �޸���
	 * @param tableName ����
	 * @param dataList ������
	 * @return
	 */
	public String[] modifyField(String tableName, List<Map> dataList);
	
	/**
	 * ������
	 * @param tableName ����
	 * @param dataList ������
	 * @return
	 */
	public String[] insertField(String tableName, List<Map> dataList);
	
	/**
	 * �ַ������ڵ�λ��  str��str1�е�λ��
	 * @param str �Ӵ�
	 * @param str1 Ŀ�괮
	 * @return
	 */
	public String charIndex(String str,String str1);
	
	/**
	 * �ַ�������
	 * @param str Ŀ�괮
	 * @return
	 */
	public String dataLength(String str);
	
	/**
	 * �õ��޸�����sql
	 * @param map
	 * @return
	 */
	public String getRenameColumnSql(Map map);
	
	/**
	 * insert ���ƴ�������ֶΣ�ȡֵSQL���֣��˷�����ͨ�ñ�2.0����
	 * ��oracleΪ��������Ҫʹinsert�������������
	 * declare a number(15);
	 * begin 
	 * a:= SP_GET_INCREMENT('Global3');
	 * insert into A1(f1,f2)values(a,'f2');
	 * end;
	 * @return �������3��Ԫϵ�������ʵ�����ʵ�� 
	 */
	public String[] getIncrementSQL(String tableName);
	
	/**
	 * ��ȡ����ֶζ���Ĭ�ϳ���
	 * @param ftype ��������
	 * @param flen ����
	 * @param decpos ����
	 * @return
	 */
	public int getDgtDefaultFieldsLen(String ftype,int flen,int decpos);
	
	/**
	 * ����ʱ���������� (����ʱ�����˳�����⣬��ȡ���ʱ��ľ���ֵ)
	 * @param dateColumnName1 ʱ���ֶ�1
	 * @param dateColumnName2 ʱ���ֶ�2
	 * @return
	 */
	public String getSecondsBetween(String dateColumnName1,String dateColumnName2);
	
	/**
	 * ��ѯʱ�滻�������ı�:  _,%,[������[ ֻ��sybase������
	 * ���� exp = %?%, value = % ����sybase�±��滻��: %[%]% ���������ܲ������%�ֺŵ������ˣ������ǰ�%�ŵ���ͨ��� 
	 * */
	public String[] replaceSpecialCharForLike(String exp, String value);
	
	
	/**
	 * ���������ֶμ���������
	 * @param beginColumnName ��ʼʱ��
	 * @param endColumnName ����ʱ��
	 * @return �������
	 * @author tangxiaolong
	 * @version 2013-11-1
	 */
	public String getSecondsBetweenDays(String beginColumnName, String endColumnName);
	
	/**
	 * ��format��Ӧ��ʱ�䵥λ��������Ӧ��ֵ���õ�һ���µ�ʱ�䡣
	 * ��oracle��3�������ֱ���� "sysdate"��"10","year", �����ڵ�ǰʱ��Ļ����ϼ�10��
	 * @param col �����ֶ�����
	 * @param num ������ֵ
	 * @param format ʱ�䵥λ (��������ֵ�� year, month, day, hour, minute, second)
	 * @return ������
	 */
	public String addDateTime(String col, String num, String format);
}
