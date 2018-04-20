package pub.source;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import util.StringUtil;

/**
 * 动态数据源
 * @author tanjianwen
 */
public class DynamicDataSource extends AbstractRoutingDataSource 
    implements PlatformTransactionManager 
    {
	/**当前线程*/
	private static final ThreadLocal contextHolder = new ThreadLocal();
	/**默认数据源*/
	//private static DataSource defaultTargetDataSource;
	private static String defaultTargetDataSource ;
	
	/**数据源集合*/
	private static Map<Object, DataSourceTransactionManager> targetTranManger =   
         new HashMap<Object, DataSourceTransactionManager>();   

	protected Object determineCurrentLookupKey() {
		return DynamicDataSource.getCustomerType();
	}
	
	/**
	 * 重置数据源
	 * @param customerType 数据源节点名称
	 */
	public static void setCustomerType(String customerType) {
		if(!StringUtil.checkStr(customerType) && StringUtil.checkStr(defaultTargetDataSource)){
				customerType = defaultTargetDataSource;//.getAlias();
		}
		contextHolder.set(customerType);
	}
	
	public static String getCustomerType() {
	    if(contextHolder.get() == null 
	            && StringUtil.checkStr(defaultTargetDataSource)){
	    	setCustomerType(defaultTargetDataSource); //.getAlias()
	    }
	    String customerType =(String)contextHolder.get();
	    return customerType;
	}
	
	public static void clearCustomerType() {
		contextHolder.remove();
	}

    public void commit(TransactionStatus status) throws TransactionException {
        getTargetTranManger().commit(status);   
    }

    public TransactionStatus getTransaction(TransactionDefinition definition)
            throws TransactionException {
        return getTargetTranManger().getTransaction(definition);   
    }

    public void rollback(TransactionStatus status) throws TransactionException {
        getTargetTranManger().rollback(status);   
    }

    protected DataSourceTransactionManager getTargetTranManger() {
        Object context = getCustomerType();   
        
        return targetTranManger.get(context);   
    }
    
    public static DataSource getDataSource(){
        Object context = getCustomerType(); 
        
        if(targetTranManger.get(context) == null){
            return null;
        }else{
            return targetTranManger.get(context).getDataSource(); 
        }
    }

    public void setTargetTranManger(
            Map<Object, DataSourceTransactionManager> targetTranManger) {
        this.targetTranManger = targetTranManger;
    }
    
    public void setDefaultTargetDataSource(String pds){
        this.defaultTargetDataSource = pds;
    }

	public Logger getParentLogger() {
		return null;
	}
	
}
