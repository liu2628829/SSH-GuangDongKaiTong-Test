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
 * ���ݿ�������
 * @author pengjiewen
 * @version Aug 20, 2013
 */
public class DatabaseConnection {
	
   /** application.xml�ļ�Դ */
   private static Resource resource;
   /** bean�������� */
   private static BeanFactory factory;
   /** spring���õ�Ĭ������Դbean */
   private static SpringDataSource defaultSource;
   /** Ĭ������Դ */
   private static DataSource datasource;
//   /** ��־�� */
//   private final static Logger log = Logger.getLogger(
//           DatabaseConnection.class);
   /** �Ƿ�ʹ��jndi����Դ����    */
   private static String useJndi;
   /** ʹ��jndi�Ƿ��������    */
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
    * ��ȡĬ�ϵ�����Դ
    * @return ����Դ����
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
    * ���ݴ���ı���ȡ�����õ�����Դ
    * @param domain ����Դ����
    * @return ����Դ����
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
    * ��ȡApplicationContext.xml��Ĭ�ϵ�����Դ
    * @return ����Դ����
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
    * ���ݴ���ı���ȡ��ApplicationContext.xml�����õ�����Դ
    * @param domain ����Դ����
    * @return ����Դ����
    */
   public static Connection getSpringConnection(String domain) { 
       String CallStack=LogOperateUtil.logCallStack();
       try{
            if(resource == null) {
                resource = new ClassPathResource("applicationContext.xml"); 
                factory = new XmlBeanFactory(resource); 
            }
            //���Ӷ�̬����Դ���ӻ�ȡ��add by linxiugang
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
    * �Զ�������Դ��ȡ��
    * @param domain ����Դ����
    * @param e ԭ���׳����쳣
    */
   private static Connection setCustomConnect(String domain, Exception e)
       throws Exception{
       //�����ļ����Ҳ�����Ӧ������Դ���ã�����Զ���ӿڻ�ȡʵ��
       //�Ƿ�ʹ���Զ����ȡ����Դ����
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
    * ���ݴ���ı���jndi����Դ����
    * @param domain ����Դ����
    * @return ����Դ����
    */
   public static Connection getJndiConnection(String domain) { 
       String callStack=LogOperateUtil.logCallStack();
       
       if(!StringUtil.checkStr(domain)){
  	     	domain = ConfigInit.Config.getProperty("defaultJndiDs", "Develop");
  	   }
       
       try{
           //��ʽ��������Դ��ȡ
           Context ctx = new InitialContext();
           DataSource ds = null;
           try{
               ds = (DataSource)ctx.lookup("jdbc/" + domain);//was����
           }catch(NameNotFoundException e){
               ds = (DataSource)ctx.lookup(
                       "java:comp/env/jdbc/" + domain);//tomcat����
           }
           return initJndiConnection(ds, domain, false);
       } catch (Exception e) {
           LogOperateUtil.logSQLError(e,domain,"",callStack);
       }
       return null;
   }
   
   /**
    * ���ÿ����?
    * @param conn ����Դ����
    * @param domain ����Դ����
    * @param isFirst �Ƿ��һ�γ�������
    * @return �����Ƿ�ɹ�
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
                   //log.error("��ȡ"+domain+"����ʱ�ر�Statementʱ�����쳣", eq);
               }
           }
           if(!isFirst){
               throw ex;
           }
           if (isFirst && conn != null) {//��һ���쳣
               try {
                   conn.close();
               } catch (SQLException eq) {
                   //log.error("��ȡ"+domain+"����ʱ�ر�Connectionʱ�����쳣", eq);
               }
           }
       }
       return success;
   }    
   
   /**
    * ��ʼ��������jndi����Դ����
    * @param ds ����Դ
    * @param domain ����Դ���� 
    * @param isDefault �Ƿ�Ĭ������Դ
    * @return ��ʼ���������Դ����
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
               //log.info("**************������һ��**********");
               if(useCustomCon){
                   conn = setCustomConnect(domain, tempEx);
               }else{
                   conn = ds.getConnection();
               }
               try{
                   setStmtTransction(conn, domain, false);
               }catch(Exception ee){
                   //log.error("��ȡ" + domain + "����ʱ���ÿ����ʱ�����쳣:", ee);
                   LogOperateUtil.logSQLError(ee, domain, "", callStack);
               }
           }
       }
       return conn;
   }

}