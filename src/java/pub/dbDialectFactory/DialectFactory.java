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
 * ����
 * 
 * @author gaotao 2011-08-08
 */
public class DialectFactory {
	
	/**���湤����(��ʵ����ȫ������Ϊdialects�ļ�)*/
	private static Map<String, Dialect> dialects = new HashMap<String, Dialect>();
	/**���Խڵ�*/
	private static NodeList nodes = null;
	/**ȡ���Ե����ã���config.properties��applicationConext.xml*/
	private static String dialectConfig = ConfigInit.Config.getProperty("dialectConfig","applicationContext");
	/**Ĭ������Դ����*/
	private static String defaultDataSource=null;
	
	/**��̫���ʼ��*/
	static{
		try{
			getApplicationContext();
		}catch(Exception e){
			LogOperateUtil.logException(e, "DialectFactory�������ʼ��ʧ��!", null);
		}
	}
	
	/**
	 * ��ͬ���ݿ�Դ��Ӧ��ͬ�ķ���:domain���Ǹ�xml�����õ�����ԴID
	 * */ 
	public static Dialect getDialect(String domain) {
		Dialect dialect = null;
		String dialectType="";
		try {
			if ("config".equals(dialectConfig)) {// ʹ��config.properties���Զ������ݿⷽ��
				domain = StringUtil.checkStr(domain) ? domain : getDefaultDatasrc("1"); //domainû��ָ��������Ĭ��domainΪ׼
				
				dialectType = ConfigInit.Config.getProperty(domain); //�õ���������
				dialect = dialects.get(dialectType);//����Ƿ���ڻ��浱ǰ���͵ķ��Զ���ʵ��
				
				if (dialect == null && StringUtil.checkStr(dialectType)) { //���޻���,���ҷ���������
					dialect = (Dialect) Class.forName(dialectType).newInstance();
					dialects.put(dialectType, dialect);
				}else if(dialect == null && !StringUtil.checkStr(dialectType)){
					throw new BaseRuntimeException("config.properties��û������"+domain+"���ݿⷽ�Դ�����", null);
				}
			} else {// ʹ��applicationContext.xml���Զ������ݿⷽ��
				//getApplicationContext();
				dialectType = getBeanName(domain);
				dialect = dialects.get(dialectType);

				if (dialect == null) {
					dialect = (Dialect) Class.forName(dialectType).newInstance();
					dialects.put(dialectType, dialect);
				}
			}
		} catch (Exception e) {
			throw LogOperateUtil.logSQLError(e,domain,"û�������κ����ݿⷽ�Դ�������߷�����"+dialectType+"�����ڡ�",LogOperateUtil.logCallStack());
		}
		return dialect;
	}

	/**
	 * Ĭ�Ϸ���
	 * */ 
	public static Dialect getDialect() {
		Dialect dialect = getDialect(null);
		return dialect;
	}

	/**
	 * ��ʵ������������ԴȡID��ȡ��������ֵ
	 * */
	public static String getPrimaryKeys(String domain){
		return getDialect(domain).getInitPrimaryKeys(domain);

	}
	
	/**
	 * ��ʵ������������Դ�������»���������ʼֵ
	 * */
	public static void setPrimaryKeys(String domain,String curKey){
		Map<String,Long> temp=DialectCacheObjects.primaryKeys.get(domain);
		if(temp!=null){
			temp.put("cnt", Long.valueOf(1));//�����б�ȡ���ĸ�����1��ʼ
			temp.put("curKey", Long.parseLong(curKey));
			DialectCacheObjects.primaryKeys.put(domain, temp);
		}
	}
	
	
	/**��ȡĬ������Դ*/
	public static String getDefaultDatasrc(){
		return defaultDataSource;
	}
	
	/**
	 * ��applicationContext.xml��ȡĬ������Դ
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
	 * ��config.properties����Ĭ������Դ����
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
			LogOperateUtil.logException(e, "��ȡĬ������Դ����ʧ��!", null);
		}
		defaultDataSource = domain;
		return domain;
	}

	/**
	 * ��application����
	 * */ 
	private static void getApplicationContext() throws ParserConfigurationException, SAXException, IOException {
		if (nodes == null){
		    //class�ļ���Ŀ¼
		    InputStream in = DialectFactory.class.getClassLoader().getResourceAsStream("applicationContext.xml");
		    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = dbf.newDocumentBuilder();
		    Document doc = builder.parse(in);
		    nodes = doc.getElementsByTagName("bean");	
		    
		    getDefaultDatasrc(nodes);
		}
	}
	
	/**
	 * �õ����Դ�������
	 * */
	private static String getBeanName(String domain) {
		if (domain == null || "".equals(domain.trim()))
			domain =  getDefaultDatasrc();//Ĭ�������ⷽ��
        String dialectType  = getDescription(nodes, domain);
		return dialectType;
	}
	
	/**
	 * ����applicationContext.xml��ȡ����
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
	
	/**����*/
	public static void main(String[] args)throws Exception {
		//getApplicationContext();	
	}
}
