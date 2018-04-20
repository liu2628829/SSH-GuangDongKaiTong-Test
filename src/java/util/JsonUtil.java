package util;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.type.TypeFactory;
import util.StringUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * <pre>
 * ClassName: util.app.JsonUtil
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON
 * author: Zhang zhongtao
 * version:
 * since: Ver 1.1
 * date: 2011-10-27 21:24
 * </pre>
 */
public class JsonUtil {
    private static Logger log = Logger.getLogger(JsonUtil.class);
    private static ObjectMapper mapper = new ObjectMapper();

    /***
     * Json�ַ���ת��Ϊ��������
     * 1�����json�ַ�������ת��Ŀ¼����û�е����ԣ�
     *    ����Ҫ��Ŀ��������@JsonIgnoreProperties(ignoreUnknown = true)ע��
     *    ��������jackjson����û�ж�Ӧ������
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T renderJson2Object(String json, Class clazz){
        if(!util.StringUtil.checkObj(json)){
            return null;
        }

        try {
            mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
            DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //�����л���ʽ
            mapper.getDeserializationConfig().setDateFormat(myDateFormat);
            return (T)mapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    /***
     * Object����ת��ΪJson�ַ���
     * @param obj
     * @return
     */
    public static String object2Json(Object obj){
        StringWriter stringWriter = null;
        try{
             mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
            DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.getSerializationConfig().setDateFormat(myDateFormat);
            stringWriter = new StringWriter();
            mapper.writeValue(stringWriter,obj);
            return  stringWriter.toString();
        }catch (Exception ex){
            log.error(ex);
            throw new RuntimeException(ex);
        }finally {
            try{
                stringWriter.close();
            }catch (Exception ex){
                
            }
        }
    }

     /***
     * Json�ַ���ת��Ϊ���϶���
     * 1�����json�ַ�������ת��Ŀ�����û�е����ԣ�
     *    ����Ҫ��Ŀ��������@JsonIgnoreProperties(ignoreUnknown = true)ע��
     *    ��������jackjson����û�ж�Ӧ������
     * @param json json�ַ���
     * @param clazz list�ж�������
     * @return
     */
    public static List renderJson2List(String json,Class clazz){
        if(!StringUtil.checkObj(json)){
            return null;
        }

        try {

            mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
            DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.getSerializationConfig().setDateFormat(myDateFormat);
            return mapper.readValue(json, TypeFactory.collectionType(List.class, clazz));
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }
}
