<%@ page import="vo.deptMgr.StaffRegister"%>
<%@ page import="util.SessionUtil"%>

<!-- ��Ȩ��ǰ��¼�˵�Ȩ������ -->
<script type="text/javascript">
	var sysRightList = <%=((StaffRegister)SessionUtil.getAttribute("staff", session)).getSysRightListJson() %>;
</script>