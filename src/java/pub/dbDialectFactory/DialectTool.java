package pub.dbDialectFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pub.source.LogOperateUtil;

/**
 * 方言工具类，仅允许被当前包内的相关方言类使用
 * 
 * @author gaotao
 * @date 2013/9/7 
 */
public class DialectTool {
	
	/**
	 * 关闭连接资源(在各方言实现类中有被用到)
	 * @param conn 连接对象
	 * @param st 执行对象
	 * @param rs 结果集对象
	 */
	protected static synchronized void free(Connection conn, Statement st, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				LogOperateUtil.logException(e, "关闭ResultSet对像失败!");
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				LogOperateUtil.logException(e, "关闭Statement对像失败!");
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				LogOperateUtil.logException(e, "关闭Connection对像失败!");
			}
		}
	}
	
	/**
	 * 包装获取总记录数的SQL
	 * @param sql 原始SQL
	 */
	protected static String getCountSql(String sql){
		//如果不replace，当以下关键字前正好是换行时，就会因匹配不上而出错，特别是当SQL是配置在数据库或文件中时很容易出错
		String tempSQL = sql.toLowerCase().replace("\n", " ").replace("\r", " "); 
		if(tempSQL.contains(" union ") || tempSQL.contains(" distinct")
				||  tempSQL.contains(" group ") || tempSQL.contains(" order by ")){
			sql = "select count(*)  from ( " + sql + ") temp";
		}else{
			int index = tempSQL.indexOf(" from ");
			sql = "select count(*) " + sql.substring(index);
		}
		
		//sql = "select count(*)  from ( " + sql + ") temp"; //最初始现
		return sql;
	}
	
	/**测试方法*/
	public static void main(String[] args) {
		String noUnion = "select a,b,c from (select a,b,c from tableA where 1=2) temp";
		System.out.println("=====无union=====");
		System.out.println(getCountSql(noUnion));
		
		String union = "select a,b,c from table union select a,b,c from temp";
		System.out.println("=====有union=====");
		System.out.println(getCountSql(union));
	}
}
