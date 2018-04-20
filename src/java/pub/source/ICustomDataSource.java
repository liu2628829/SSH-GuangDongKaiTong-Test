package pub.source;

import java.sql.Connection;

/**
 * 
 * 自定义获取数据连接 接口
 * @author liujunjun
 *
 */
public interface ICustomDataSource {
    
    /**
     * 根据别名获取数据源（数据源连接获取逻辑自行实现）
     * @param ds 数据源名称
     * @return 数据源连接
     */
    public abstract Connection getConnection(String ds);
}
