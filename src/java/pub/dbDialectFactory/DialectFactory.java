package pub.dbDialectFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pub.servlet.ConfigInit;
import pub.source.LogOperateUtil;
import util.BaseRuntimeException;
import util.StringUtil;

/**
 * 工厂
 * 
 * @author gaotao 2011-08-08
 */
public class DialectFactory {
	
	/**缓存工厂类(将实现类全名，作为dialects的键)*/
	private static Map<String, Dialect> dialects = new HashMap<String, Dialect>();
	/**方言节点*/
	private static NodeList nodes = null;
	/**取方言的配置，从config.properties或applicationConext.xml*/
	private static String dialectConfig = ConfigInit.Config.getProperty("dialectConfig","applicationContext");
	/**默认数据源名称*/
	private static String defaultDataSource=null;
	
	/**静太块初始化*/
	static{
		try{
			getApplicationContext();
		}catch(Exception e){
			LogOperateUtil.logException(e, "DialectFactory方言类初始化失败!", null);
		}
	}
	
	/**
	 * 不同数据库源对应不同的方言:domain就是各xml中配置的数据源ID
	 * */ 
	public static Dialect getDialect(String domain) {
		Dialect dialect = null;
		String dialectType="";
		try {
			if ("config".equals(dialectConfig)) {// 使用config.properties配自定义数据库方言
				domain = StringUtil.checkStr(domain) ? domain : getDefaultDatasrc("1"); //domain没有指定，则以默认domain为准
				
				dialectType = ConfigInit.Config.getProperty(domain); //得到方言类名
				dialect = dialects.get(dialectType);//检查是否存在缓存当前类型的方言对象实例
				
				if (dialect == null && StringUtil.checkStr(dialectType)) { //暂无缓存,并且方类名存在
					dialect = (Dialect) Class.forName(dialectType).newInstance();
					dialects.put(dialectType, dialect);
				}else if(dialect == null && !StringUtil.checkStr(dialectType)){
					throw new BaseRuntimeException("config.properties里没有配置"+domain+"数据库方言处理类", null);
				}
			} else {// 使用applicationContext.xml配自定义数据库方言
				//getApplicationContext();
				dialectType = getBeanName(domain);
				dialect = dialects.get(dialectType);

				if (dialect == null) {
					dialect = (Dialect) Class.forName(dialectType).newInstance();
					dialects.put(dialectType, dialect);
				}
			}
		} catch (Exception e) {
			throw LogOperateUtil.logSQLError(e,domain,"没有配置任何数据库方言处理类或者方言类"+dialectType+"不存在。",LogOperateUtil.logCallStack());
		}
		return dialect;
	}

	/**
	 * 默认方言
	 * */ 
	public static Dialect getDialect() {
		Dialect dialect = getDialect(null);
		return dialect;
	}

	/**
	 * 各实体对象根据数据源取ID获取缓存主键值
	 * */
	public static String getPrimaryKeys(String domain){
		return getDialect(domain).getInitPrimaryKeys(domain);

	}
	
	/**
	 * 各实体对象根据数据源设置最新缓存主键起始值
	 * */
	public static void setPrimaryKeys(String domain,String curKey){
		Map<String,Long> temp=DialectCacheObjects.primaryKeys.get(domain);
		if(temp!=null){
			temp.put("cnt", Long.valueOf(1));//缓存中被取掉的个数从1开始
			temp.put("curKey", Long.parseLong(curKey));
			DialectCacheObjects.primaryKeys.put(domain, temp);
		}
	}
	
	
	/**获取默认数据源*/
	public static String getDefaultDatasrc(){
		return defaultDataSource;
	}
	
	/**
	 * 从applicationContext.xml获取默认数据源
	 */
	private static void getDefaultDatasrc(NodeList nodes){
        for(int i=0;i<nodes.getLength();i++){
            Element node = (Element)nodes.item(i);
            if("defaultSource".equals(node.getAttribute("id"))){
                NodeList nl = node.getElementsByTagName("property");
                Element n = (Element)nl.item(0);
                nl = n.getElementsByTagName("value");
                n =(Element)nl.item(0);
                defaultDataSource = n.getTextContent();
                break;
            }
        }
	}
	
	/**
	 * 从config.properties里获得默认数据源名称
	 * @return
	 */
	private static String getDefaultDatasrc(String flg){
		String domain = "Oracle";
		String domain1 = null;
		try {
			domain1 =  ConfigInit.Config.getProperty("DEFAULT_DATASRC");
			if(StringUtil.checkStr(domain1)){
				domain = domain1;
			}else{
				InputStream istream = DialectFactory.class.getClassLoader().getResourceAsStream("config.properties");
				
				ConfigInit.Config.load(istream);
				istream.close();
				domain1 =  ConfigInit.Config.getProperty("DEFAULT_DATASRC");
				if(StringUtil.checkStr(domain1)){
					domain = domain1;
				}
			}
		} catch (Exception e) {
			LogOperateUtil.logException(e, "获取默认数据源名称失败!", null);
		}
		defaultDataSource = domain;
		return domain;
	}

	/**
	 * 得application配置
	 * */ 
	private static void getApplicationContext() throws ParserConfigurationException, SAXException, IOException {
		if (nodes == null){
		    //class文件根目录
		    InputStream in = DialectFactory.class.getClassLoader().getResourceAsStream("applicationContext.xml");
		    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = dbf.newDocumentBuilder();
		    Document doc = builder.parse(in);
		    nodes = doc.getElementsByTagName("bean");	
		    
		    getDefaultDatasrc(nodes);
		}
	}
	
	/**
	 * 得到方言处理类名
	 * */
	private static String getBeanName(String domain) {
		if (domain == null || "".equals(domain.trim()))
			domain =  getDefaultDatasrc();//默认言主库方言
        String dialectType  = getDescription(nodes, domain);
		return dialectType;
	}
	
	/**
	 * 解析applicationContext.xml获取方言
	 */
	private static String getDescription(NodeList nodes,String domain){
		String description = "";
        for(int i=0;i<nodes.getLength();i++){
            Element node = (Element)nodes.item(i);
            if(domain.equals(node.getAttribute("id"))){
                NodeList nl = node.getChildNodes();
                for(int j=0;j<nl.getLength();j++){
                    Node n = nl.item(j);
                    if("description".equals(n.getNodeName())){
                    	description = n.getFirstChild().getNodeValue();
                    	break;
                    }
                }
            }
        }
        return description;
	}
	
	/**测试*/
	public static void main(String[] args)throws Exception {
		//getApplicationContext();	
	}
}
