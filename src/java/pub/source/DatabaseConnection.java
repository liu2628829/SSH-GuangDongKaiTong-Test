package pub.source;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import pub.dbDialectFactory.Dialect;
import pub.dbDialectFactory.DialectFactory;
import pub.servlet.ConfigInit;
import util.StringUtil;

/**
 * 数据库连接类
 * @author pengjiewen
 * @version Aug 20, 2013
 */
public class DatabaseConnection {
	
   /** application.xml文件源 */
   private static Resource resource;
   /** bean解析工厂 */
   private static BeanFactory factory;
   /** spring配置的默认数据源bean */
   private static SpringDataSource defaultSource;
   /** 默认数据源 */
   private static DataSource datasource;
//   /** 日志类 */
//   private final static Logger log = Logger.getLogger(
//           DatabaseConnection.class);
   /** 是否使用jndi数据源配置    */
   private static String useJndi;
   /** 使用jndi是否设置脏读    */
   private static String jndiUnCommit;
  
   
   private static void setUseJndi(){
       if(useJndi == null){
           useJndi = ConfigInit.Config.getProperty("jndiDataSource", "0");
       }
   }
   
   private static void setJndiUnCommit(){
       if(jndiUnCommit == null){
           jndiUnCommit = ConfigInit.Config.getProperty("jndiUnCommit", "0");
       }
   }
   /**
    * 获取默认的数据源
    * @return 数据源连接
    */
   public static synchronized Connection getConnection() {
       setUseJndi();
       if("1".equals(useJndi)){
           return getJndiConnection(null);
       }else{
           return getSpringConnection();
       }
   }

   /**
    * 根据传入的别名取得配置的数据源
    * @param domain 数据源别名
    * @return 数据源连接
    */
   public static synchronized Connection getConnection(String domain) { 
       setUseJndi();
       if("1".equals(useJndi)){
           return getJndiConnection(domain);
       }else{
           return getSpringConnection(domain);
       }
   }
   
   public static void main(String[] args) {
	   System.out.println(getConnection());
   }
   
   /**
    * 获取ApplicationContext.xml中默认的数据源
    * @return 数据源连接
    */
   public static Connection getSpringConnection() {
       String CallStack=LogOperateUtil.logCallStack();
       try{
            if(datasource == null) {
                resource = new ClassPathResource("applicationContext.xml"); 
                factory = new XmlBeanFactory(resource); 
                defaultSource = (SpringDataSource)factory.getBean("defaultSource");
                datasource = (DataSource)factory.getBean(defaultSource.getSourceName()); 
            }
            return datasource.getConnection();
        }catch (Exception e) {
            LogOperateUtil.logSQLError(e,defaultSource==null?"default":defaultSource.getSourceName(),"",CallStack);
        }
        return null;
   }
   
   /**
    * 根据传入的别名取得ApplicationContext.xml中配置的数据源
    * @param domain 数据源别名
    * @return 数据源连接
    */
   public static Connection getSpringConnection(String domain) { 
       String CallStack=LogOperateUtil.logCallStack();
       try{
            if(resource == null) {
                resource = new ClassPathResource("applicationContext.xml"); 
                factory = new XmlBeanFactory(resource); 
            }
            //增加动态数据源连接获取，add by linxiugang
            Connection connection = null;
            try {
                DataSource dataSource = (DataSource) factory.getBean(domain);
                connection = dataSource.getConnection();
            } catch (NoSuchBeanDefinitionException e) {
                connection = setCustomConnect(domain, e);
            }
            return connection;
       }catch (Exception e) {
           LogOperateUtil.logSQLError(e,domain,"",CallStack);
       }
       return null;
   }
   
   /**
    * 自定义数据源获取类
    * @param domain 数据源别名
    * @param e 原先抛出的异常
    */
   private static Connection setCustomConnect(String domain, Exception e)
       throws Exception{
       //配置文件中找不到对应的数据源配置，则从自定义接口获取实现
       //是否使用自定义获取数据源开关
       String enableCustomDataSource = 
           ConfigInit.Config.getProperty("enableCustomDataSource", "0");
       if ("1".equals(enableCustomDataSource)) {
           String customDataSourceClass = 
               ConfigInit.Config.getProperty("customDataSourceClass", 
                       "pub.source.DynamicDatabaseConnection");
           ICustomDataSource customData = 
               (ICustomDataSource)Class.forName(
                       customDataSourceClass).newInstance();
           Connection connection = customData.getConnection(domain);
           return connection;
       } else if(e != null){
           throw e;
       }
       return null;
   }
   
   /**
    * 根据传入的别名jndi数据源连接
    * @param domain 数据源别名
    * @return 数据源连接
    */
   public static Connection getJndiConnection(String domain) { 
       String callStack=LogOperateUtil.logCallStack();
       
       if(!StringUtil.checkStr(domain)){
  	     	domain = ConfigInit.Config.getProperty("defaultJndiDs", "Develop");
  	   }
       
       try{
           //正式环境数据源获取
           Context ctx = new InitialContext();
           DataSource ds = null;
           try{
               ds = (DataSource)ctx.lookup("jdbc/" + domain);//was环境
           }catch(NameNotFoundException e){
               ds = (DataSource)ctx.lookup(
                       "java:comp/env/jdbc/" + domain);//tomcat环境
           }
           return initJndiConnection(ds, domain, false);
       } catch (Exception e) {
           LogOperateUtil.logSQLError(e,domain,"",callStack);
       }
       return null;
   }
   
   /**
    * 设置可脏读?
    * @param conn 数据源连接
    * @param domain 数据源别名
    * @param isFirst 是否第一次尝试设置
    * @return 设置是否成功
    * @throws Exception 
    */
   private static boolean setStmtTransction(Connection conn, String domain,
           boolean isFirst) throws Exception {
       Statement stmt = null;
       boolean success = true;
       try{
           stmt = conn.createStatement();
           stmt.execute("set transaction isolation level 0 ");
           stmt.close();
           stmt = null;
       } catch (Exception ex) {
           success = false;
           if (stmt != null) {
               try {
                   stmt.close();
               } catch (SQLException eq) {
                   //log.error("获取"+domain+"连接时关闭Statement时出现异常", eq);
               }
           }
           if(!isFirst){
               throw ex;
           }
           if (isFirst && conn != null) {//第一次异常
               try {
                   conn.close();
               } catch (SQLException eq) {
                   //log.error("获取"+domain+"连接时关闭Connection时出现异常", eq);
               }
           }
       }
       return success;
   }    
   
   /**
    * 初始化并返回jndi数据源连接
    * @param ds 数据源
    * @param domain 数据源别名 
    * @param isDefault 是否默认数据源
    * @return 初始化后的数据源连接
    */
   private static Connection initJndiConnection(DataSource ds, 
           String domain, boolean isDefault) throws Exception{
       String callStack = LogOperateUtil.logCallStack();
       Connection conn = null;
       boolean useCustomCon = false;
       Exception tempEx = null;
       try{
           conn = ds.getConnection();
       } catch (Exception ex) {
           if(isDefault){
               throw ex;
           }
           conn = setCustomConnect(domain, ex);
           useCustomCon = true;
           tempEx = ex;
       }
       setJndiUnCommit();
       Dialect dialect = DialectFactory.getDialect(domain);
       String dialectName = dialect.getClass().getName();
       if(dialectName.indexOf("Sybase") != -1 
               && "1".equals(jndiUnCommit)){
           if(!setStmtTransction(conn, domain, true)){
               //log.info("**************再连接一次**********");
               if(useCustomCon){
                   conn = setCustomConnect(domain, tempEx);
               }else{
                   conn = ds.getConnection();
               }
               try{
                   setStmtTransction(conn, domain, false);
               }catch(Exception ee){
                   //log.error("获取" + domain + "连接时设置可脏读时出现异常:", ee);
                   LogOperateUtil.logSQLError(ee, domain, "", callStack);
               }
           }
       }
       return conn;
   }

}