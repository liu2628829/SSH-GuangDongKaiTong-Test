<%@ page import="vo.deptMgr.StaffRegister"%>
<%@ page import="util.SessionUtil"%>

<!-- 获权当前登录人的权限数据 -->
<script type="text/javascript">
	var sysRightList = <%=((StaffRegister)SessionUtil.getAttribute("staff", session)).getSysRightListJson() %>;
</script>