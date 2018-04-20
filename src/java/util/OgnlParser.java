package util;

import java.lang.reflect.Method;
import java.util.Map;

import org.xidea.el.ExpressionFactory;
import org.xidea.el.ExpressionToken;
import org.xidea.el.impl.ExpressionFactoryImpl;

/**
 * @author ���
 * @version 2013-3-6
 */
public class OgnlParser {
	
	ExpressionFactory factory;
	boolean isDefault;
	
	/**
	 * Ĭ��������Ѿ�֧�����¹��ܣ�
	 *  �����֧��
����			JSEL ֧�ִ󲿷֡���׼ECMA262 ��javascript 1.5��������
		����֧��
		����JSEL ֧�ִ󲿷֡���׼ECMA262 ��javascript 1.5����׼����
		������չ
		����JSEL �����û��Զ��庯�����Զ��������
	 * @param isDefault�� �Ƿ�ʹ��Ĭ�ϵĽ����������Ҫ�Զ��巽������ȫ�ֱ���������Ϊfalse��������Ϊtrue��
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
	ȫ�ֺ���:
		isFinite, isNaN, parseFloat, parseInt, decodeURI, decodeURIComponent, encodeURI, encodeURIComponent
	ȫ�ֱ���:
		NaN, Infinity
	Math: ȫ�ֶ��󣨹��ߺ����ۣ�
		 * ��Ա�����У�abs, acos, asin, atan, ceil, asin, cos,
		 *             exp, floor, log, round,sin, sqrt, tan
		 *             random, min, max, pow ,atan2
		 * ��Ա�����У�E,PI,LN10,LN2��LOG2E,LOG10E,SQRT1_2, SQRT2,

	JSON: ��Ա������:
		JSON.stringify(object),
		JSON.parse(json)
		
	���ͣ�String,
	charAt, charCodeAt, concat, indexOf, lastIndexOf, fromCharCode, slice ,split, replace, lastIndexOf, substr, substring, toUpperCase, toLowerCase

	���ͣ�Number,
	toFixed, toPrecision, toExponential, toString
	
	���ͣ�List:
	sort, join, slice, concat, reverse, toString	
	
	 * @param expression
	 * @return
	 */
	public Object excuteOgnlExp(String expression) {
		return factory.create(expression).evaluate();
	}
	
	
	/**
		ȫ�ֺ���:
			isFinite, isNaN, parseFloat, parseInt, decodeURI, decodeURIComponent, encodeURI, encodeURIComponent
		ȫ�ֱ���:
			NaN, Infinity
		Math: ȫ�ֶ��󣨹��ߺ����ۣ�
			 * ��Ա�����У�abs, acos, asin, atan, ceil, asin, cos,
			 *             exp, floor, log, round,sin, sqrt, tan
			 *             random, min, max, pow ,atan2
			 * ��Ա�����У�E,PI,LN10,LN2��LOG2E,LOG10E,SQRT1_2, SQRT2,
	
		JSON: ��Ա������:
			JSON.stringify(object),
			JSON.parse(json)
			
		���ͣ�String,
		charAt, charCodeAt, concat, indexOf, lastIndexOf, fromCharCode, slice ,split, replace, lastIndexOf, substr, substring, toUpperCase, toLowerCase
	
		���ͣ�Number,
		toFixed, toPrecision, toExponential, toString
		
		���ͣ�List:
		sort, join, slice, concat, reverse, fromCharCode, toString	
		
	 * @param expression
	 * @param context
	 * @return
	 */
	public Object excuteOgnlExp(String expression, Map<String, Object> context) {
		return factory.create(expression).evaluate(context);
	}
	
	/**
	 * ���ȫ�ֱ������������Ƕ��󣬼������Ե��ô˶���ķ�����
	 * @param name
	 * @param value
	 */
	public void addVariable(String name, Object value){
		if(!isDefault){
			((ExpressionFactoryImpl)factory).addVar(name, value);
		}
	}
	
	/**
	 * ��Ӿ�̬������
	 * @param name�� ��һ�����Ի���������
	 * @param method�� ��ȷ����ӵķ�������static�����ε�
	 */
	public void addMethod(String name, Method method){
		if(!isDefault){
			((ExpressionFactoryImpl)factory).addVar(name, method);
		}
	}
	
	/**
	 * ����Զ��������,Ĭ�ϻ����������ȼ���Ϊ >= ����
	 * @param name����һ�����Ի���������
	 * @param method�� ȷ����ӵķ�������static�����ε�
	 */
	public void addOperation(String name, Method method){
		if(!isDefault){
			((ExpressionFactoryImpl)factory).addOperator(ExpressionToken.OP_GTEQ, name, method);
		}
	}
}
