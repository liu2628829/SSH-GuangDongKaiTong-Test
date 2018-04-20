package com.catt.entity;

import pub.source.DataType;
import pub.source.Field;

/**
 * <PRE>
 * ��Ա�����ֶζ���ö��
 * </PRE>
 *
 * ��Ŀ���ƣ�SSH</BR>
 * ����֧�֣��㶫��ͨ�Ƽ��ɷ����޹�˾ (c) 2017</BR>
 * 
 * @version 1.0 2017��9��27��
 * @author gaotao
 * @since JDK1.8
 */
public enum TbEmployee implements Field{

	/**ö���ֶζ���*/
	iEmployeeId(DataType.LONG),
	cEmployeeName(DataType.STRING),
	iSex(DataType.INTEGER),
	iLengthOfService(DataType.INTEGER),
	dEmployDate(DataType.TIME_STAMP),
	cTel(DataType.STRING),
	iDutyId(DataType.LONG),
	iDeptId(DataType.LONG),
	remark(DataType.STRING)
	;
	
	/**����*/
	private TbEmployee(DataType dataType){
		this.dataType = dataType;
	}
	
	/**���ԣ��ֶ�����*/
	private DataType dataType;
	
	/**get����, ������set����*/
	public DataType getDataType(){
		return this.dataType;
	}
	
	/**�ɿ��Ǽ�һ���˷��������ر����������Ͳ��õ���Ӳ����д�����ˣ�A.A.getTableName()����ȡ��ֵ*/
	public String getTableName(){
		return "TbEmployee";
	}

}
