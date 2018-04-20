 package pub.source;

import org.logicalcobwebs.proxool.ProxoolDataSource;

import util.DESTool;

/** 
 * @author 李军
 * @version 1.0
 * @datetime 2011-9-2 下午04:19:33 
 * 类说明：用于拦截数据库spring datasource，将配置文件中密文形式的账号密码进行解密
 */
public class SelfDataSource extends ProxoolDataSource {

	@Override
	public void setPassword(String password) {
//		password = MD5Tool.HexDecode(password);
		password = DESTool.decrypt(password);
		super.setPassword(password);
	}
	
	@Override
	public void setUser(String user) {
//		user = MD5Tool.HexDecode(user);
		super.setUser(user);
	}

	/**
	 * mysql:  jdbc:mysql://localhost:3306/safemgrDev?user=root&amp;password=root&amp;autoReconnect=true
	 * sybase: jdbc:sybase:Tds:192.168.168.62:5000/cattjb?user=cattjbln&amp;password=cattjbln&amp;CHARSET=cp936&amp;jconnect_version=6
	 * oracle: jdbc:oracle:thin:safeMgrDev/safeMgrDev@192.168.168.162:1521:orcl
	 */
	@Override
	public void setDriverUrl(String driverUrl) {
		
		if(driverUrl.indexOf("jdbc:oracle:thin:")==0){//oracle
			String temp=driverUrl.replace("jdbc:oracle:thin:", "");
			temp=temp.substring(0,temp.indexOf("@"));
			String ms[]=temp.split("/");
//			ms[0]=MD5Tool.HexDecode(ms[0]);
//			ms[1]=MD5Tool.HexDecode(ms[1]);
			ms[1]=DESTool.decrypt(ms[1]);
			driverUrl=driverUrl.replace(temp, ms[0]+"/"+ms[1]);
		}else{//其它数据库
//			String u1 = driverUrl.substring(driverUrl.indexOf("user="));
//			String u2 = u1.substring(u1.indexOf("=")+1);
//			String u3 = u2.substring(0, u2.indexOf("&"));
//			driverUrl = driverUrl.replace("user="+u3, "user=" + MD5Tool.HexDecode(u3));
			String p1 = driverUrl.substring(driverUrl.indexOf("password="));
			String p2 = p1.substring(p1.indexOf("=")+1);
			String p3 = p2.substring(0, p2.indexOf("&"));
//			driverUrl = driverUrl.replace("password="+p3, "password=" + MD5Tool.HexDecode(p3));
			driverUrl = driverUrl.replace("password="+p3, "password=" 
			        + DESTool.decrypt(p3));
		}
		//System.out.println(driverUrl);
		super.setDriverUrl(driverUrl);
	}
}