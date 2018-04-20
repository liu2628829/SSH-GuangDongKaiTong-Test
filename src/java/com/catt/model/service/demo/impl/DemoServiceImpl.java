package com.catt.model.service.demo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pub.dbDialectFactory.Dialect;
import pub.dbDialectFactory.DialectFactory;
import pub.source.Data;
import pub.source.DataType;
import pub.source.DatabaseUtil;
import util.StringUtil;

import com.catt.entity.TbEmployee;
import com.catt.model.service.demo.DemoService;

@Service("demoService")
public class DemoServiceImpl implements DemoService{
	
	/**
	 * 查询人员集合
	 */
	public List getEmployeeList(Map map) {
		//return demoDao.getEmployeeList(map);
		String pageNo = (String)map.get("pageNo");
		String limit = (String)map.get("limit");
		
		List list=new ArrayList();
		
		Dialect dia = DialectFactory.getDialect();
		
		StringBuffer sql = new StringBuffer();
		//如果是oracle库,都加个别名，并用“”号引起来
		sql.append("select e.iEmployeeId as \"iEmployeeId\",e.cEmployeeName \"cEmployeeName\",e.iSex \"iSex\", (case e.iSex when 1 then '男' when 2 then '女' end) as \"sSex\",");
		sql.append("e.iLengthOfService \"iLengthOfService\",");
		sql.append(dia.datetimeTostring("e.dEmployDate", "yyyy-MM-dd")); 
		sql.append(" \"dEmployDate\",e.cTel \"cTel\",e.iDutyId \"iDutyId\",du.cDutyName \"cDutyName\",");
		sql.append("e.iDeptId \"iDeptId\",de.cDeptName \"cDeptName\",e.remark \"remark\" ");
		sql.append(" from tbEmployee e left join tbDept de on e.iDeptId=de.iDeptId left join tbDuty du");
		sql.append(" on e.iDutyId=du.iDutyId where 1=1 ");
		
		if(StringUtil.checkObj(map.get("iEmployeeId"))){
			sql.append(" and e.iEmployeeId = ?");
			list.add(Integer.valueOf((String)map.get("iEmployeeId")));
		}
		if(StringUtil.checkObj(map.get("cEmployeeName"))){
			sql.append(" and e.cEmployeeName like ? ");
			list.add("%"+map.get("cEmployeeName")+"%");
		}
		if(StringUtil.checkObj(map.get("sName"))){
			sql.append(" and e.cEmployeeName like ? ");
			list.add("%"+map.get("sName")+"%");
		}
		
		if(StringUtil.checkObj(map.get("sSex"))){
			sql.append(" and e.iSex = ? ");
			//list.add(StringUtil.toInt((String)map.get("sSex")));
			
			list.add(DataType.INTEGER.getVal(map.get("sSex")));
		}
		if(StringUtil.checkObj(map.get("iDeptId"))){
			String inParams = DatabaseUtil.inParameterLoader(StringUtil.toString(map.get("iDeptId")), list);
			sql.append(" and e.iDeptId ");
			sql.append(inParams);
		}
		if(StringUtil.checkObj(map.get("iDuty"))){
			String inParams = DatabaseUtil.inParameterLoader(StringUtil.toString(map.get("iDuty")), list);
			sql.append(" and e.iDutyId ");
			sql.append(inParams);
		}
		if(StringUtil.checkObj(map.get("sTel"))){
			sql.append(" and e.cTel like ? ");
			list.add("%"+map.get("sTel")+"%");
		}
		if (StringUtil.checkObj(map.get("remark"))) {
			sql.append(" and e.remark like ?");
			list.add("%"+map.get("remark")+"%");
		}
		if(StringUtil.checkObj(map.get("creatTime")) && StringUtil.checkObj(map.get("endTime"))){
			sql.append(" and e.dEmployDate between ?").append(" and ?");
			list.add(StringUtil.parseObj("7", (String)map.get("creatTime") + " 00:00:00"));
			list.add(StringUtil.parseObj("4", (String)map.get("endTime"), null, true));
		}else if (StringUtil.checkObj(map.get("endTime"))) {
			sql.append(" and e.dEmployDate < ?");
			list.add(StringUtil.parseObj("4", (String)map.get("endTime"), null, true));
		}else if (StringUtil.checkObj(map.get("creatTime"))) {
			sql.append(" and e.dEmployDate > ?");
			list.add(StringUtil.parseObj("7", (String)map.get("creatTime") + " 00:00:00"));
		}
		sql.append(" order by e.dEmployDate desc,e.iEmployeeId desc");
		
		
		/** PrepareStatement 传参	*/
		List list1=null;
		if(StringUtil.checkObj(pageNo) && StringUtil.checkObj(limit)){
			list1 = DatabaseUtil.queryForListByPage(sql.toString(), Integer.parseInt(pageNo), Integer.parseInt(limit), list, null);
		}else{
			list1 = DatabaseUtil.queryForList(sql.toString(), list, null);
		}
			
		return list1;
	}

	/**
	 * 新增人员信息
	 */
	public int addEmployee(Map map) {
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
		return editEmployee(map);
	}

	/**
	 * 修改人员信息
	 */
	public int editEmployee(Map map) {
		List<Data> datas = new ArrayList<Data>();
		
		String idField = TbEmployee.iEmployeeId.toString();
		String id = (String)map.get(idField);
		
		//如果从前端请求参数中没取到ID值，就是新增；否则就是修改
		if( ! StringUtil.checkStr(id)){
			id = DatabaseUtil.getKeyId(null);
			idField = null; 
		}
		
		datas.add(Data.get(TbEmployee.iEmployeeId, id));
		datas.add(Data.get(TbEmployee.cEmployeeName, map.get(TbEmployee.cEmployeeName.toString())));
		datas.add(Data.get(TbEmployee.iSex, map.get(TbEmployee.iSex.toString())));
		datas.add(Data.get(TbEmployee.iLengthOfService, map.get(TbEmployee.iLengthOfService.toString())));
		datas.add(Data.get(TbEmployee.dEmployDate, map.get(TbEmployee.dEmployDate.toString())+" 00:00:00"    ));
		datas.add(Data.get(TbEmployee.iDutyId, map.get(TbEmployee.iDutyId.toString())));
		datas.add(Data.get(TbEmployee.iDeptId, map.get(TbEmployee.iDeptId.toString())));
		datas.add(Data.get(TbEmployee.cTel, map.get(TbEmployee.cTel.toString())));
		datas.add(Data.get(TbEmployee.remark, map.get(TbEmployee.remark.toString())));
		
		//第3个参数如果不为空，说明是修改操作；否则是新增操作
		return DatabaseUtil.saveByDataMap(TbEmployee.cEmployeeName.getTableName(), datas, idField, null);
	}

	/**
	 * 删除人员信息
	 */
	public int deleteEmployee(String empId) {
		String sql = "delete from tbEmployee where iEmployeeId in ("+empId+")";
		return DatabaseUtil.updateDateBase(sql,null);
	}
}
