package com.catt.model.service.demo;

import java.util.List;
import java.util.Map;

public interface DemoService {

	public List getEmployeeList(Map map);
	
	public int addEmployee(Map map);
	
	public int editEmployee(Map map);
	
	public int deleteEmployee(String empId);
}
