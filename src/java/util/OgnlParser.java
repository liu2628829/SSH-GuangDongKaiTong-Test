package util;

import java.lang.reflect.Method;
import java.util.Map;

import org.xidea.el.ExpressionFactory;
import org.xidea.el.ExpressionToken;
import org.xidea.el.impl.ExpressionFactoryImpl;

/**
 * @author 李海谅
 * @version 2013-3-6
 */
public class OgnlParser {
	
	ExpressionFactory factory;
	boolean isDefault;
	
	/**
	 * 默认情况下已经支持以下功能：
	 *  运算符支持
　　			JSEL 支持大部分　标准ECMA262 （javascript 1.5）操作符
		函数支持
		　　JSEL 支持大部分　标准ECMA262 （javascript 1.5）标准函数
		功能扩展
		　　JSEL 允许用户自定义函数，自定于运算符
	 * @param isDefault： 是否使用默认的解释器？如果要自定义方法，设全局变量，请设为false，否则设为true；
	 */
	public OgnlParser(boolean isDefault){
		if(isDefault){
			factory = ExpressionFactory.getInstance();
		}else{
			this.isDefault = isDefault;
			factory = new ExpressionFactoryImpl();
		}
	}
	/**
	全局函数:
		isFinite, isNaN, parseFloat, parseInt, decodeURI, decodeURIComponent, encodeURI, encodeURIComponent
	全局变量:
		NaN, Infinity
	Math: 全局对象（工具函数聚）
		 * 成员函数有：abs, acos, asin, atan, ceil, asin, cos,
		 *             exp, floor, log, round,sin, sqrt, tan
		 *             random, min, max, pow ,atan2
		 * 成员属性有：E,PI,LN10,LN2，LOG2E,LOG10E,SQRT1_2, SQRT2,

	JSON: 成员函数有:
		JSON.stringify(object),
		JSON.parse(json)
		
	类型：String,
	charAt, charCodeAt, concat, indexOf, lastIndexOf, fromCharCode, slice ,split, replace, lastIndexOf, substr, substring, toUpperCase, toLowerCase

	类型：Number,
	toFixed, toPrecision, toExponential, toString
	
	类型：List:
	sort, join, slice, concat, reverse, toString	
	
	 * @param expression
	 * @return
	 */
	public Object excuteOgnlExp(String expression) {
		return factory.create(expression).evaluate();
	}
	
	
	/**
		全局函数:
			isFinite, isNaN, parseFloat, parseInt, decodeURI, decodeURIComponent, encodeURI, encodeURIComponent
		全局变量:
			NaN, Infinity
		Math: 全局对象（工具函数聚）
			 * 成员函数有：abs, acos, asin, atan, ceil, asin, cos,
			 *             exp, floor, log, round,sin, sqrt, tan
			 *             random, min, max, pow ,atan2
			 * 成员属性有：E,PI,LN10,LN2，LOG2E,LOG10E,SQRT1_2, SQRT2,
	
		JSON: 成员函数有:
			JSON.stringify(object),
			JSON.parse(json)
			
		类型：String,
		charAt, charCodeAt, concat, indexOf, lastIndexOf, fromCharCode, slice ,split, replace, lastIndexOf, substr, substring, toUpperCase, toLowerCase
	
		类型：Number,
		toFixed, toPrecision, toExponential, toString
		
		类型：List:
		sort, join, slice, concat, reverse, fromCharCode, toString	
		
	 * @param expression
	 * @param context
	 * @return
	 */
	public Object excuteOgnlExp(String expression, Map<String, Object> context) {
		return factory.create(expression).evaluate(context);
	}
	
	/**
	 * 添加全局变量或对象。如果是对象，加完后可以调用此对象的方法。
	 * @param name
	 * @param value
	 */
	public void addVariable(String name, Object value){
		if(!isDefault){
			((ExpressionFactoryImpl)factory).addVar(name, value);
		}
	}
	
	/**
	 * 添加静态方法。
	 * @param name： 想一个难以混淆的名字
	 * @param method： 请确认添加的方法是用static来修饰的
	 */
	public void addMethod(String name, Method method){
		if(!isDefault){
			((ExpressionFactoryImpl)factory).addVar(name, method);
		}
	}
	
	/**
	 * 添加自定义运算符,默认基操作符优先级别为 >= 级别。
	 * @param name：想一个难以混淆的名字
	 * @param method： 确认添加的方法是用static来修饰的
	 */
	public void addOperation(String name, Method method){
		if(!isDefault){
			((ExpressionFactoryImpl)factory).addOperator(ExpressionToken.OP_GTEQ, name, method);
		}
	}
}
