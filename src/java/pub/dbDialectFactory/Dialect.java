package pub.dbDialectFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * sql本地化接口
 * 
 * @author fans
 * 更新记录:
 * 2012/3/29 gaotao 加方法getDataByPageEoms ,方法说明见注释
 */
public interface Dialect {
	/**
	 * 翻页方法
	 * 如果oracle采用存储过程，取结果集要用游标
	 * 如果oracle采用sql方式，又要走两步，先要取总页数与总成记录数，然后再拼分页语句
	 * 以上情况，存储过程SQL或普通分页SQL都应该写在工厂实现类
	 * 连接对象conn，可以从外边传入，但statement对象，必须在方言实现类内实例化，这样才能一次性传sql,并通过conn生成对象
	 * 但在实现类内实例的对象，又不能先关闭连接，否则再调用类(DataBaseUtil)中无法操作结果集
	 * 为了保证能够再DataBaseUtil统一关闭数据库操作对象,所以返回一个对象数组
	 * 数组有2个元素，第一个元素是Statement，第二个Resultset
	 */
	public Object[] getDataByCallableStatement(Connection conn,String sql,
			int page, int limit) throws SQLException;

	/**
	 * 采用prepareStatement进行分页查询
	 * @throws SQLException
	 */
	public Object[] getDataByPageEoms(Connection conn,String sql,
			int page, int limit,List list)throws SQLException;
	
	/**
	 * 取主键
	 * 不同数据库，取主键方法不一致，sybase,mysql的需用存储过程，oracle,DB2用序列
	 * 同分页方法一样，Statment对象必须在必方言实现类内实例化
	 * 同分页方法一样，为了保证能够再DataBaseUtil统一关闭数据库操作对象，故也返回一个对象数组
	 * 数组有2个元素，第一个元素是Statement，第二个Resultset
	 * cachePrimaryKeys:缓存主键的个数，此参数针对仅mysql,sybase有效
	 */
	public Object[] getKeyId(Connection conn,int cachePrimaryKeys)throws SQLException;
	
	/**
	 * 修改Clob或text大文本字段
	 */
	public Object[] writeClobOrText(Connection conn,String tableName,String where,String cols[],String val[])throws Exception;
	
	/**
	 * 数据库当前系统时间
	 */
	public String getDate();

	/**
	 * 判断是否为空
	 */
	public String isNull(String property, String o);

	/**
	 * 截取字符串
	 */
	public String subString(String str, int startIndex, int endIndex);

	/**
	 * 字符串转成日期+时间，Sybase mysql 直接用单引号引住，oracle要转成日期格式 格式YYYY-MM-DD hh:mm:ss
	 */
	public String stringToDatetime(String str);

	/**
	 * 字符串转成日期，Sybase mysql 直接用单引号引住，oracle要转成日期格式 格式YYYY-MM-DD
	 */
	public String stringToDate(String str);

	/**
	 * 当前时间减去某时间(时间类型的字段名称)相差的秒数
	 */
	public String getSecondsBetweenDays(String dateColumnName);

	/**
	 * 转大写
	 */
	public String getUpperFunction(String columnName);

	/**
	 * 见实现类具体实现
	 */
	public String getDateValueHour(String col);

	/**
	 * 见实现类具体实现
	 */
	public String getGroupByHour(String col);

	/**
	 * 见实现类具体实现
	 */
	public String getDateValueDay(String col);

	/**
	 * 见实现类具体实现
	 */
	public String getGroupByDay(String col);
	
	/**
	 * 日期按格式转成字符串 
	 * 如果格式字符串为空默认格式为YYYY-MM-DD hh:mm:ss
	 * @param col 字段名
	 * @param format 有以下枚举：
	 * 				 日期时间：""或YYYY-MM-DD hh:mm:ss 
	 *               日期：yyyy-MM-dd
	 *               时间：HH:mm:ss
	 *               年月：yyyy-MM
	 *               月日：MM-dd
	 *               时分：HH:mm
	 *               分秒：mm:ss
	 */
	public String datetimeTostring(String col, String format);

	/**
	 * 主键列自增,返回一个长度为2的数组，第0个值是主键列，第1个值是主键取值方式
	 * 除oracle库外，其它库应该都是返回两个空串
	 * 此方法目前仅在保存各种日志时有用到
	 * @param primaryKeyCol
	 * @return
	
	public String[] getIdentityKey(String primaryKeyCol,String domain); */
	
	/**
	 * 返回数据库的字符串的拼接结果
	 * sybase 或 oracle 'a'||'b' = 'ab'
	 * mysql concat函数：concat('a','b') = 'ab'
	 * @author 谭健文
	 * @return
	 */
	public String getConcatResult(String... strs);
	
	/**
	 * 返回数据库的字符串的拼接结果
	 * @param splitChar 拼接时，中间加自定义的间隔符
	 * @param strs 多个字段名，相当于一个数组
	 * @return 拼接串的结果
	 */
	public String getConcatResult2(String splitChar, String... strs);
	
	/**
	 * 数据库的数字类型转字符串类型
	 * @param 需要转换的数字
	 * @author 谭健文
	 * @return 
	 */
	public String convertNumberToString(String number);
	
	/**
	 * 取消order by,将已知的sql,做为子查询，sybase库不允许子查询里有order by 
	 */
	public String cancelOrderBy(String sql);
	
	/**
	 * 优先从应用缓存取主键，oracle,DB2不会在应用内缓存主键，所以要有所区分,故加此方法
	 * @param domain 数据源ID
	 * @return
	 */
	public String getInitPrimaryKeys(String domain);
	
	/**
	 * 数据库的字符串类型转换为整型类型
	 * 
	 * @param 需要转换的字段
	 * @autho Zhanweibin 2011-12-27
	 * @return
	 */
	public String convertStringToInt(String column);
	
	/**
	 * 得到数据库的类型
	 * 
	 * @author Zhanweibin 2012-03-02
	 * @return 数据库小写名称
	 */
	public String getDBType();
	
	/**
	 * 获取数据库用户表信息
	 * @author tanjianwen
	 * @return 用户表信息查询语句
	 */
	public String getUserTableInfo(String tableNames, String ... args);
	
	/**
	 * 根据条件查询系统表
	 * @param map
	 * @return
	 */
	public String selectSystemTable(Map map);
	
	/**
	 * 传入数据库字段类型获取字段公共类型
	 * (整数1、小数2、字符串3、日期时间4、日期5、时间6)
	 * @author tanjianwen
	 * @param fieldType 字段数据库类型
	 * @param precNum 字段精度
	 * @return 
	 */
	public String getDBTblFieldType(String fieldType, String precNum);
	
	/**
	 * 创建表
	 * @param map
	 * @return
	 */
	public String createTable(String tableName, List<Map> list);
	
	/**
	 * 添加列
	 * @param tableName 表名
	 * @param dataList 列数据
	 * @return
	 */
	public String addField(String tableName, List<Map> dataList);
	
	/**
	 * 修改列
	 * @param tableName 表明
	 * @param dataList 列数据
	 * @return
	 */
	public String[] editField(String tableName, List<Map> dataList);
	
	/**
	 * 删除要修改的列
	 * @param tableName
	 * @param dataList
	 * @return
	 */
	public String[] deleteEditField(String tableName, List<Map> dataList);
	
	/**
	 * 删除列
	 * @param tableName
	 * @param dataList
	 * @return
	 */
	public String[] deleteField(String tableName, List<Map> dataList);
	
	/**
	 * 复制表结构,创建新表,可同时带上数据
	 * @param fromTable 源表表名
	 * @param toTable 新表表名
	 * @param fields 需要复制的字段，多个字段用逗号隔开，如果没有，所复制所有字段
	 * @param where 有给条件，则按条件复制数据；没给条件，则仅复制表结构
	 */
	public String copyTableStructure(String fromTable,String toTable,String fields,String where);
	
	/**
	 * 复制表结构,创建临时表,可同时带上数据
	 * @param fromTable 源表表名
	 * @param toTable 新表表名(Sybase要以#为前缀)
	 * @param fields 需要复制的字段，多个字段用逗号隔开，如果没有，所复制所有字段
	 * @param where 有给条件，则按条件复制数据；没给条件，则仅复制表结构
	 */
	public String copyTempTableStructure(String fromTable,String toTable,String fields,String where);

	 /**
	 * 检索str出现在column中的位置
	 * @param column
	 * @param str
	 * @return
	 */
	public String indexOf(String column,String str);
	
	/**
	 * 截取字符串
	 * @param column 列名(字符串)
	 * @param startExp 得起始索引表达式
	 * @param endExp 得结束索引表达式
	 * @return
	 */
	public String subString(String column,String startExp,String endExp);
	
	/**
	 * 获取检查是否存在相同的表名称的sql语句
	 */
	public String getCheckTableSql(String tableName, String ... args);
	
	/**
	 * 获取检查是否存在相同的表名称的sql语句,后面pin in语句
	 */
	public String getCheckTablesSql();
	
	/**
	 * 翻译字段类型,库表使用
	 * @param ftype 数据类型
	 * @param flen 字段长度
	 * @param decpos 小数位长度
	 * @return
	 */
	public String parseType(String ftype,int flen, int decpos);
	
	/**
	 * 修改列
	 * @param tableName 表名
	 * @param dataList 列数据
	 * @return
	 */
	public String[] modifyField(String tableName, List<Map> dataList);
	
	/**
	 * 新增列
	 * @param tableName 表名
	 * @param dataList 列数据
	 * @return
	 */
	public String[] insertField(String tableName, List<Map> dataList);
	
	/**
	 * 字符串所在的位置  str在str1中的位置
	 * @param str 子串
	 * @param str1 目标串
	 * @return
	 */
	public String charIndex(String str,String str1);
	
	/**
	 * 字符串长度
	 * @param str 目标串
	 * @return
	 */
	public String dataLength(String str);
	
	/**
	 * 拿到修改列名sql
	 * @param map
	 * @return
	 */
	public String getRenameColumnSql(Map map);
	
	/**
	 * insert 语句拼自增长字段，取值SQL部分，此方法被通用表单2.0所用
	 * 以oracle为例，最终要使insert语句变成如下样子
	 * declare a number(15);
	 * begin 
	 * a:= SP_GET_INCREMENT('Global3');
	 * insert into A1(f1,f2)values(a,'f2');
	 * end;
	 * @return 数组包含3个元系，具体见实现类的实现 
	 */
	public String[] getIncrementSQL(String tableName);
	
	/**
	 * 获取库表字段定义默认长度
	 * @param ftype 数据类型
	 * @param flen 长度
	 * @param decpos 精度
	 * @return
	 */
	public int getDgtDefaultFieldsLen(String ftype,int flen,int decpos);
	
	/**
	 * 两个时间相差的秒数 (两个时间参数顺序随意，将取相差时间的绝对值)
	 * @param dateColumnName1 时间字段1
	 * @param dateColumnName2 时间字段2
	 * @return
	 */
	public String getSecondsBetween(String dateColumnName1,String dateColumnName2);
	
	/**
	 * 查询时替换掉特殊文本:  _,%,[，其中[ 只在sybase下特殊
	 * 举例 exp = %?%, value = % ，在sybase下被替换成: %[%]% ，这样就能查出包含%分号的数据了，而不是把%号当成通配符 
	 * */
	public String[] replaceSpecialCharForLike(String exp, String value);
	
	
	/**
	 * 两个日期字段间相差的秒数
	 * @param beginColumnName 开始时间
	 * @param endColumnName 结束时间
	 * @return 相差秒数
	 * @author tangxiaolong
	 * @version 2013-11-1
	 */
	public String getSecondsBetweenDays(String beginColumnName, String endColumnName);
	
	/**
	 * 在format对应的时间单位，增加相应的值，得到一个新的时间。
	 * 如oracle下3个参数分别给： "sysdate"，"10","year", 代表在当前时间的基础上加10年
	 * @param col 日期字段名称
	 * @param num 增减数值
	 * @param format 时间单位 (可有如下值： year, month, day, hour, minute, second)
	 * @return 函数串
	 */
	public String addDateTime(String col, String num, String format);
}
