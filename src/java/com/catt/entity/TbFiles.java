package com.catt.entity;

import pub.source.DataType;
import pub.source.Field;

/**
 * <PRE>
 * 人员对象字段定义枚举
 * </PRE>
 *
 * 项目名称：SSH</BR>
 * 技术支持：广东凯通科技股份有限公司 (c) 2017</BR>
 *
 * @version 1.0 2017年9月27日
 * @author gaotao
 * @since JDK1.8
 */
public enum TbFiles implements Field{

	/**枚举字段定义*/
	//命名转成小写，且去掉下划线
//	iId(DataType.LONG),
//	sFileName(DataType.STRING),
//	iFileType(DataType.INTEGER),
//	sFilePath(DataType.STRING),
//	iFileSize(DataType.INTEGER),
//	sUploadUser(DataType.STRING),
//	dUploadTime(DataType.TIME_STAMP),
//	sRemark(DataType.STRING)
	iId(DataType.LONG),
	sFileName(DataType.STRING),
	iFileType(DataType.INTEGER),
	sFilePath(DataType.STRING),
	iFileSize(DataType.INTEGER),
	sUploadUser(DataType.STRING),
	dUploadTime(DataType.TIME_STAMP),
	sRemark(DataType.STRING);

//	iLengthOfService(DataType.INTEGER),
//	dEmployDate(DataType.TIME_STAMP),
//	cTel(DataType.STRING),
//	iDutyId(DataType.LONG),
//	iDeptId(DataType.LONG),
//	remark(DataType.STRING)


	/**构造*/
	private TbFiles(DataType dataType){
		this.dataType = dataType;
	}

	/**属性：字段类型*/
	private DataType dataType;

	/**get方法, 不开放set方法*/
	public DataType getDataType(){
		return this.dataType;
	}

	/**可考虑加一个此方法，返回表名，这样就不用到处硬编码写表名了，A.A.getTableName()就能取到值*/
	public String getTableName(){
		return "tb_files";
	}

}
