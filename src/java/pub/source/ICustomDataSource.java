package pub.source;

import java.sql.Connection;

/**
 * 
 * �Զ����ȡ�������� �ӿ�
 * @author liujunjun
 *
 */
public interface ICustomDataSource {
    
    /**
     * ���ݱ�����ȡ����Դ������Դ���ӻ�ȡ�߼�����ʵ�֣�
     * @param ds ����Դ����
     * @return ����Դ����
     */
    public abstract Connection getConnection(String ds);
}
