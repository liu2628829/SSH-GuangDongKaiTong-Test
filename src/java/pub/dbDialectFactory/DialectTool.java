package pub.dbDialectFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pub.source.LogOperateUtil;

/**
 * ���Թ����࣬��������ǰ���ڵ���ط�����ʹ��
 * 
 * @author gaotao
 * @date 2013/9/7 
 */
public class DialectTool {
	
	/**
	 * �ر�������Դ(�ڸ�����ʵ�������б��õ�)
	 * @param conn ���Ӷ���
	 * @param st ִ�ж���
	 * @param rs ���������
	 */
	protected static synchronized void free(Connection conn, Statement st, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				LogOperateUtil.logException(e, "�ر�ResultSet����ʧ��!");
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				LogOperateUtil.logException(e, "�ر�Statement����ʧ��!");
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				LogOperateUtil.logException(e, "�ر�Connection����ʧ��!");
			}
		}
	}
	
	/**
	 * ��װ��ȡ�ܼ�¼����SQL
	 * @param sql ԭʼSQL
	 */
	protected static String getCountSql(String sql){
		//�����replace�������¹ؼ���ǰ�����ǻ���ʱ���ͻ���ƥ�䲻�϶������ر��ǵ�SQL�����������ݿ���ļ���ʱ�����׳���
		String tempSQL = sql.toLowerCase().replace("\n", " ").replace("\r", " "); 
		if(tempSQL.contains(" union ") || tempSQL.contains(" distinct")
				||  tempSQL.contains(" group ") || tempSQL.contains(" order by ")){
			sql = "select count(*)  from ( " + sql + ") temp";
		}else{
			int index = tempSQL.indexOf(" from ");
			sql = "select count(*) " + sql.substring(index);
		}
		
		//sql = "select count(*)  from ( " + sql + ") temp"; //���ʼ��
		return sql;
	}
	
	/**���Է���*/
	public static void main(String[] args) {
		String noUnion = "select a,b,c from (select a,b,c from tableA where 1=2) temp";
		System.out.println("=====��union=====");
		System.out.println(getCountSql(noUnion));
		
		String union = "select a,b,c from table union select a,b,c from temp";
		System.out.println("=====��union=====");
		System.out.println(getCountSql(union));
	}
}
