package util;

import pub.dbDialectFactory.DialectFactory;
import pub.servlet.ConfigInit;

/**
 * 常量定义类(每个常量须写明注释)
 * 
 * @author 罗德成
 * 
 */
public class ConstantsUtil {

	// 加密的算法名称
	public static final String ENCRYPE_TYPE = "MD5";

	// 连续6次输入密码错误就锁定人员
	public static final String IS_LOCK_USER = ConfigInit.Config.getProperty("isLockUser","1");

	// 主数据库
	public static final String DEFAULT_DATASRC = DialectFactory.getDefaultDatasrc();//getProperty里，一定别给默认值
	
	// 日志备用库名称
	public static final String BACKUP_DATASRC = ConfigInit.Config.getProperty("BACKUP_DATASRC");//getProperty里，一定别给默认值
}
