package com.catt.model.service.demo.impl;

import com.catt.entity.TbFiles;
import com.catt.model.service.demo.FilesService;
import org.springframework.stereotype.Service;
import pub.dbDialectFactory.Dialect;
import pub.dbDialectFactory.DialectFactory;
import pub.source.Data;
import pub.source.DatabaseUtil;
import util.StringUtil;
import pub.source.DataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("FilesService")
public class FilesServiceImpl implements FilesService{

	/**
	 * 查询文件集合
	 */
	@SuppressWarnings("unchecked")
	public List getFilesList(Map map) {
		//return demoDao.getEmployeeList(map);
		String pageNo = (String)map.get("pageNo");
		String limit = (String)map.get("limit");

		List list=new ArrayList();

		Dialect dia = DialectFactory.getDialect();

		StringBuilder sql = new StringBuilder();
		//如果是oracle库,都加个别名，并用“”号引起来
		sql.append("SELECT\n" +
				"\te.IID AS \"iId\",\n" +
				"\te.S_FILE_NAME \"sFileName\",\n" +
				"\te.I_FILE_TYPE \"iFileType\",\n" +
				"\t(\n" +
				"\t\tCASE e.I_FILE_TYPE\n" +
				"\t\tWHEN 1 THEN\n" +
				"\t\t\t'.doc'\n" +
				"\t\tWHEN 2 THEN\n" +
				"\t\t\t'.xls'\n" +
				"\t\tWHEN 3 THEN\n" +
				"\t\t\t'.ppt'\n" +
				"\t\tWHEN 4 THEN\n" +
				"\t\t\t'.docx'\n" +
				"\t\tWHEN 5 THEN\n" +
				"\t\t\t'.xlsx'\n" +
				"\t\tWHEN 6 THEN\n" +
				"\t\t\t'.pptx'\n" +
				"\t\tEND\n" +
				"\t) AS \"sFileType\",\n" +
				"\te.S_FILE_PATH \"sFilePath\",\n" +
				"\te.I_FILE_SIZE AS \"iFileSize\",\n" +
				"\te.S_UPLOAD_USER \"sUploadUser\",\n");
		sql.append(dia.datetimeTostring("e.D_UPLOAD_TIME", "yyyy-MM-dd"));
		sql.append(" \"dUploadTime\",e.S_REMARK \"sRemark\"\n" +
				"from tb_files e\n" +
				"WHERE\n" +
				"\t1 = 1");
//		sql.append("select e.IID as \"iId\",e.S_FILE_NAME \"sFileName\",e.I_FILE_TYPE \"iFileType\",");
//		sql.append("e.iLengthOfService \"iLengthOfService\",");
//		sql.append(dia.datetimeTostring("e.dEmployDate", "yyyy-MM-dd"));
//		sql.append(" \"dEmployDate\",e.cTel \"cTel\",e.iDutyId \"iDutyId\",du.cDutyName \"cDutyName\",");
//		sql.append("e.iDeptId \"iDeptId\",de.cDeptName \"cDeptName\",e.remark \"remark\" ");
//		sql.append(" from tbEmployee e left join tbDept de on e.iDeptId=de.iDeptId left join tbDuty du");
//		sql.append(" on e.iDutyId=du.iDutyId where 1=1 ");

		//拼接条件查询
		if(StringUtil.checkObj(map.get("iId"))){
			sql.append(" and e.IID = ?");
			//noinspection unchecked
			list.add(Integer.valueOf((String)map.get("iId")));
		}
		if(StringUtil.checkObj(map.get("sFileName"))){
			sql.append(" and e.S_FILE_NAME like ? ");
			list.add("%"+map.get("sFileName")+"%");
		}

//		if(StringUtil.checkObj(map.get("sName"))){
//			sql.append(" and e.cEmployeeName like ? ");
//			list.add("%"+map.get("sName")+"%");
//		}

		if(StringUtil.checkObj(map.get("sFileType"))){
			sql.append(" and e.I_FILE_TYPE = ? ");
			//list.add(StringUtil.toInt((String)map.get("sSex")));

			list.add(DataType.SHORT.getVal(map.get("sFileType")));
		}
		if(StringUtil.checkObj(map.get("sFilePath"))){
			sql.append(" and e.S_FILE_PATH = ? ");
			//list.add(StringUtil.toInt((String)map.get("sSex")));

			list.add(DataType.SHORT.getVal(map.get("sFilePath")));
		}
		if(StringUtil.checkObj(map.get("iFileSize"))){
			sql.append(" and e.I_FILE_SIZE = ?");
			list.add(Integer.valueOf((String)map.get("iFileSize")));
		}
		if(StringUtil.checkObj(map.get("sUploadUser"))){
			sql.append(" and e.S_UPLOAD_USER like ? ");
			list.add("%"+map.get("sUploadUser")+"%");
		}
		//文件创建时间的筛选
		if(StringUtil.checkObj(map.get("creatTime")) && StringUtil.checkObj(map.get("endTime"))){
			sql.append(" and e.D_UPLOAD_TIME between ?").append(" and ?");
			list.add(StringUtil.parseObj("7", map.get("creatTime") + " 00:00:00"));
			list.add(StringUtil.parseObj("4", (String)map.get("endTime"), null, true));
		}else if (StringUtil.checkObj(map.get("endTime"))) {
			sql.append(" and e.D_UPLOAD_TIME < ?");
			list.add(StringUtil.parseObj("4", (String)map.get("endTime"), null, true));
		}else if (StringUtil.checkObj(map.get("creatTime"))) {
			sql.append(" and e.D_UPLOAD_TIME > ?");
			list.add(StringUtil.parseObj("7", map.get("creatTime") + " 00:00:00"));
		}
		if(StringUtil.checkObj(map.get("sRemark"))){
			sql.append(" and e.S_REMARK like ? ");
			list.add("%"+map.get("sRemark")+"%");
		}

//		if(StringUtil.checkObj(map.get("iDuty"))){
//			String inParams = DatabaseUtil.inParameterLoader(StringUtil.toString(map.get("iDuty")), list);
//			sql.append(" and e.iDutyId ");
//			sql.append(inParams);
//		}
//		if(StringUtil.checkObj(map.get("sTel"))){
//			sql.append(" and e.cTel like ? ");
//			list.add("%"+map.get("sTel")+"%");
//		}
//		if (StringUtil.checkObj(map.get("remark"))) {
//			sql.append(" and e.remark like ?");
//			list.add("%"+map.get("remark")+"%");
//		}
//		if(StringUtil.checkObj(map.get("creatTime")) && StringUtil.checkObj(map.get("endTime"))){
//			sql.append(" and e.dEmployDate between ?").append(" and ?");
//			list.add(StringUtil.parseObj("7", (String)map.get("creatTime") + " 00:00:00"));
//			list.add(StringUtil.parseObj("4", (String)map.get("endTime"), null, true));
//		}else if (StringUtil.checkObj(map.get("endTime"))) {
//			sql.append(" and e.dEmployDate < ?");
//			list.add(StringUtil.parseObj("4", (String)map.get("endTime"), null, true));
//		}else if (StringUtil.checkObj(map.get("creatTime"))) {
//			sql.append(" and e.dEmployDate > ?");
//			list.add(StringUtil.parseObj("7", (String)map.get("creatTime") + " 00:00:00"));
//		}
		sql.append(" ORDER BY\n" +
				"\te.D_UPLOAD_TIME DESC,\n" +
				"\te.IID DESC");


		/** PrepareStatement 传参	*/
		List list1;
		if(StringUtil.checkObj(pageNo) && StringUtil.checkObj(limit)){
			list1 = DatabaseUtil.queryForListByPage(sql.toString(), Integer.parseInt(pageNo), Integer.parseInt(limit), list, null);
		}else{
			list1 = DatabaseUtil.queryForList(sql.toString(), list, null);
		}

		return list1;
	}


	/**
	 * 新增文件信息
	 */
	public int addFiles(Map map) {
		/*实现方案1：
		List<Data> datas = new ArrayList<Data>();
		datas.add(Data.get("iEmployeeId", DataType.LONG, DatabaseUtil.getKeyId(null)));
		datas.add(Data.get("cEmployeeName", DataType.STRING, map.get("cEmployeeName")));
		datas.add(Data.get("iSex", DataType.INTEGER, map.get("iSex")));
		datas.add(Data.get("iLengthOfService", DataType.INTEGER, map.get("iLengthOfService")));
		datas.add(Data.get("dEmployDate", DataType.TIME_STAMP, map.get("dEmployDate")));
		datas.add(Data.get("iDutyId", DataType.LONG, map.get("iDutyId")));
		datas.add(Data.get("iDeptId", DataType.LONG, map.get("iDeptId")));
		datas.add(Data.get("cTel", DataType.STRING, map.get("cTel")));
		datas.add(Data.get("remark", DataType.STRING, map.get("remark")));
		return DatabaseUtil.saveByDataMap(TbEmployee.cEmployeeName.getTableName(), datas, null, null);
		*/

		//实现方案2：
		return editFiles(map);
	}

	/**
	 * 修改文件信息
	 */
	public int editFiles(Map map) {
		List<Data> datas = new ArrayList<>();

		String idField = TbFiles.iId.toString();
		String id = (String)map.get(idField);

		//如果从前端请求参数中没取到ID值，就是新增；否则就是修改
		if( ! StringUtil.checkStr(id)){
			id = DatabaseUtil.getKeyId(null);
			idField = null;
		}
		//将驼峰的类属性名转换成数据库字段名
		Data data = Data.get(TbFiles.iId, id);
		data.setFiledName("IID");
		datas.add(data);
		data = Data.get(TbFiles.sFileName, map.get(TbFiles.sFileName.toString()));
		data.setFiledName("S_FILE_NAME");
		datas.add(data);
		data = Data.get(TbFiles.iFileType, map.get(TbFiles.iFileType.toString()));
		data.setFiledName("I_FILE_TYPE");
		datas.add(data);
		data = Data.get(TbFiles.sFilePath, map.get(TbFiles.sFilePath.toString()));
		data.setFiledName("S_FILE_PATH");
		datas.add(data);
		data = Data.get(TbFiles.iFileSize, map.get(TbFiles.iFileSize.toString()));
		data.setFiledName("I_FILE_SIZE");
		datas.add(data);
		data = Data.get(TbFiles.sUploadUser, map.get(TbFiles.sUploadUser.toString()));
		data.setFiledName("S_UPLOAD_USER");
		datas.add(data);
		data = (Data.get(TbFiles.dUploadTime, map.get(TbFiles.dUploadTime.toString())+" 00:00:00"    ));
		data.setFiledName("D_UPLOAD_TIME");
		datas.add(data);
		data = (Data.get(TbFiles.sRemark, map.get(TbFiles.sRemark.toString())));
		data.setFiledName("S_REMARK");
		datas.add(data);

//		datas.add(Data.get(TbFiles.iId, id));
//		datas.add(Data.get(TbFiles.sFileName, map.get(TbFiles.sFileName.toString())));
//		datas.add(Data.get(TbFiles.iFileType, map.get(TbFiles.iFileType.toString())));
//		datas.add(Data.get(TbFiles.sFilePath, map.get(TbFiles.sFilePath.toString())));
//		datas.add(Data.get(TbFiles.iFileSize, map.get(TbFiles.iFileSize.toString())));
//		datas.add(Data.get(TbFiles.sUploadUser, map.get(TbFiles.sUploadUser.toString())));
//		datas.add(Data.get(TbFiles.dUploadTime, map.get(TbFiles.dUploadTime.toString())+" 00:00:00"    ));
//		datas.add(Data.get(TbFiles.sRemark, map.get(TbFiles.sRemark.toString())));

//		datas.add(Data.get(TbFiles.dEmployDate, map.get(TbFiles.dEmployDate.toString())+" 00:00:00"    ));
//		datas.add(Data.get(TbFiles.iDutyId, map.get(TbFiles.iDutyId.toString())));
//		datas.add(Data.get(TbFiles.iDeptId, map.get(TbFiles.iDeptId.toString())));
//		datas.add(Data.get(TbFiles.cTel, map.get(TbFiles.cTel.toString())));
//		datas.add(Data.get(TbFiles.remark, map.get(TbFiles.remark.toString())));

		//第3个参数如果不为空，说明是修改操作；否则是新增操作
		return DatabaseUtil.saveByDataMap(TbFiles.sFileName.getTableName(), datas, "IID", (String) null);
	}

	/**
	 * 删除文件信息
	 */
	public int deleteFiles(String filesId) {
		String sql = "delete from tb_Files where IID in ("+filesId+")";
		return DatabaseUtil.updateDateBase(sql,null);
	}
}
