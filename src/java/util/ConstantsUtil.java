package util;

import pub.dbDialectFactory.DialectFactory;
import pub.servlet.ConfigInit;

/**
 * ����������(ÿ��������д��ע��)
 * 
 * @author �޵³�
 * 
 */
public class ConstantsUtil {

	// ���ܵ��㷨����
	public static final String ENCRYPE_TYPE = "MD5";

	// ����6��������������������Ա
	public static final String IS_LOCK_USER = ConfigInit.Config.getProperty("isLockUser","1");

	// �����ݿ�
	public static final String DEFAULT_DATASRC = DialectFactory.getDefaultDatasrc();//getProperty�һ�����Ĭ��ֵ
	
	// ��־���ÿ�����
	public static final String BACKUP_DATASRC = ConfigInit.Config.getProperty("BACKUP_DATASRC");//getProperty�һ�����Ĭ��ֵ
}
