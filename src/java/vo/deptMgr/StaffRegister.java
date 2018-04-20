package vo.deptMgr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pub.servlet.ConfigInit;
import pub.source.DatabaseUtil;
import util.CacheUtil;
import util.StringUtil;

@SuppressWarnings("unchecked")
public class StaffRegister extends Staff implements Serializable{
    /*
    //岗位缓存
    private static Map<String, Map> postMap = new HashMap();
    //角色缓存
    private static Map<String, Map> roleMap = new HashMap();
    //权限缓存
    private static Map<String, Map> rightMap = new HashMap();
    //区域缓存
    private static Map<String, Map> domainMap = new HashMap();
    //部门缓存
    private static Map<String, Map> deptMap = new HashMap();
    */
    /**
     * 指定序列号
     */
    private static final long serialVersionUID = 1933451379004624401L;
    
    /**所有控制的URL菜单权限 */
    public static List allMenuList = new ArrayList();
    
    
    private String sDispName;
    private String sDeptName;
    private String sDomainName;
    private String sTitleName;
    private String sPostIds;
    private String sRoleIds;
    private List groupList;
    private List dutyList;
    private String sRightIds;
    private String sDeptIds;
    private String sDomainIds;
    /**菜单权限*/
    private String sMenuRightIds;
    /**自定义菜单*/
    private List sUIMenuList;
    private String sIpAddress;
    private String sessionId;
    
    private String sysRightListJson;
    private String tabRightListJson;
    private String sBtnRightIds;
    private String sTabRightIds;
    private String deptType;
    private String agCompanyId;
    private String deptParentId;
    private String iDomainType;
    private String iEiacDeptId;
    private String teamIds;
    private String dCreateDate;
    private String dUpdatePassTime;
    private String sysDate;
    /**区域码*/
    private String sDomainCode;
    /** 安全管理模块数据源    */
    private static String safeMgrDataSource = ConfigInit.Config.getProperty(
            "safeManagerDataSource", "");  
    /** 网元对象权限   */
    private String neObjRight;
    /** 光缆对象权限   */
    private String cableObjRight;
    /** 系统对象权限   */
    private String sysObjRight;
    /** 电路对象权限   */
    private String cirObjRight;
    /** 光路对象权限   */
    private String beamObjRight;
    /**新增所属地市编号，登录时从所属部门中获取 */
    private String sCityCode;
    /** 已通过验证的敏感菜单   */
    private Map sensitiveMap = new HashMap();
    
    public String getDCreateDate() {
        return dCreateDate;
    }
    public void setDCreateDate(String createDate) {
        dCreateDate = createDate;
    }
    public String getDUpdatePassTime() {
        return dUpdatePassTime;
    }
    public void setDUpdatePassTime(String updatePassTime) {
        dUpdatePassTime = updatePassTime;
    }
    public String getSysDate() {
        return sysDate;
    }
    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }
    public List getDutyList() {
        return dutyList;
    }
    public void setDutyList(List dutyList) {
        this.dutyList = dutyList;
    }
    public List getGroupList() {
        return groupList;
    }
    public void setGroupList(List groupList) {
        this.groupList = groupList;
    }
    
    public String getPostIds() {
        return this.sPostIds;
    }
    public List getPostList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.iPostId as \"iPostId\", a.sPostName as \"sPostName\" ")
           .append(" from tbOsPost a ");
        return getObjectList(sPostIds, "post", "iPostId", sql);
    }
    public void setPostList(String sPostIds) {
        this.sPostIds = sPostIds;
    }
    
    public String getRoleIds() {
        return this.sRoleIds;
    }
    public List getRoleList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.iRoleId as \"iRoleId\", a.sRoleName as \"sRoleName\" ")
           .append(" from tbOsRole a ");
        return getObjectList(sRoleIds, "role", "iRoleId", sql);
    }
    public void setRoleList(String sRoleIds) {
        this.sRoleIds = sRoleIds;
    }
    public String getSDeptName() {
        return sDeptName;
    }
    public void setSDeptName(String deptName) {
        sDeptName = deptName;
    }
    public String getSDomainName() {
        return sDomainName;
    }
    public void setSDomainName(String domainName) {
        sDomainName = domainName;
    }
    public String getSTitleName() {
        return sTitleName;
    }
    public void setSTitleName(String titleName) {
        sTitleName = titleName;
    }
    public List getRightList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.iRight as \"iRight\", a.sPathId as \"sPathId\", ")
            .append("a.sRightNo as \"sRightNo\", ")
            .append("a.sRightName as \"sRightName\", ")
            .append("a.iSeq as \"iSeq\", a.iParentId as \"iParentId\",")
            .append("a.iType as \"iType\", a.sURL as \"sURL\", ")
            .append("a.sIcon as \"sIcon\", a.sRemark as \"sRemark\", ")
            .append("a.sExInfo as \"sExInfo\",  ")
            .append("a.iIsSensitive as \"iIsSensitive\",iAppFor as \"iAppFor\"  ")
            .append(" from tbOsRight a ");
        return getObjectList(sRightIds, "right", "iRight", sql);
    }
    
    public void setRightList(String sRightIds) {
        this.sRightIds = sRightIds;
        setButtonRightList();
    }
    public String getRightIds(){
        return sRightIds;
    }
    
    /**
     * 设置按钮/Tab页权限
     */
    public void setButtonRightList() {
        List rightList = getRightList();
        List<Map> list = new ArrayList<Map>();
        StringBuffer btnRightIds = new StringBuffer();
        StringBuffer tabRightIds = new StringBuffer();
        StringBuffer btnSb = new StringBuffer();
        StringBuffer tabSb = new StringBuffer();
        btnSb.append("{");
        tabSb.append("{");
        if(rightList != null && rightList.size() > 0){
            int btn = 0;
            int tab = 0;
            for (int i = 0; i < rightList.size(); i++) {
                Map rightMap = (Map) rightList.get(i);
                //功能按钮
                if("2".equals((String)rightMap.get("iType"))){
                    if (btnRightIds.length() > 0) btnRightIds.append(",");
                    btnRightIds.append(rightMap.get("iRight"));
                    if(btn>0)btnSb.append(",");
                    btnSb.append("\"").append(rightMap.get("sRightNo"))
                        .append("\"").append(":1");
                    btn++;
                }
                //Tab页
                if("5".equals((String)rightMap.get("iType"))){
                    if (tabRightIds.length() > 0) tabRightIds.append(",");
                    tabRightIds.append(rightMap.get("iRight"));
                    if(tab>0)tabSb.append(",");
                    tabSb.append("\"").append(rightMap.get("sRightNo"))
                        .append("\"").append(":1");
                    tab++;
                }
            }
        }
        btnSb.append("}");
        tabSb.append("}");
        this.setSysRightListJson(btnSb.toString());
        this.setTabRightListJson(tabSb.toString());
        this.setSysRightList(btnRightIds.toString());
        this.setTabRightList(tabRightIds.toString());
    }
    
    
    public boolean hasRight(String rightId){
        List list = this.getRightList();
        Iterator it = list.iterator();
        boolean result = false;
        while(it.hasNext()){
            if(((String)it.next()).equals(rightId)){
                result = true;
                break;
            }
                
        }   
        return result;
    }

    /**
     * 判断人员是否拥有某rightNo的权限
     */
    public List<Boolean> hasRight(List<String> rightNos){
        List list = this.getRightList();
        Iterator it = list.iterator();
        Map rights = new HashMap();
        while(it.hasNext()){
            rights.put(((Map)it.next()).get("sRightNo"),"1");
        }
        List<Boolean> result = new ArrayList();
        for(String rightNo:rightNos){
            result.add("1".equals(rights.get(rightNo)));
        }
        return result;
    }
    
    public String getAgCompanyId() {
        return agCompanyId;
    }
    public void setAgCompanyId(String agCompanyId) {
        this.agCompanyId = agCompanyId;
    }
    public String getDeptType() {
        return deptType;
    }
    public void setDeptType(String deptType) {
        this.deptType = deptType;
    }
    public String getDeptParentId() {
        return deptParentId;
    }
    public void setDeptParentId(String deptParentId) {
        this.deptParentId = deptParentId;
    }
    public String getTeamIds(){
        return teamIds;
    }
    public List getTeamList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.iDeptId as \"iDeptId\", a.sDeptName as \"sDeptName\", a.sDispName as \"sDispName\", ")
            .append("a.sShortName as \"sShortName\", a.iDeptType as \"iDeptType\", a.sDeptCode as \"sDeptCode\", ")
           .append("a.iDomainId as \"iDomainId\",  a.iParentId as \"iParentId\", a.sPathId as \"sPathId\" ")
           .append(" from tbOsDepartment a ");
        Map teamMap = (Map<String, Map>)CacheUtil.getInstance().get("team");
        if(teamMap == null){
            teamMap = new HashMap<String, Map>();
            CacheUtil.getInstance().put("team", teamMap);
        }
        return getObjectList(teamIds, "team", "iDeptId", sql);
    }
    public void setTeamList(String teamIds) {
        this.teamIds = teamIds;
    }
    public String getIDomainType() {
        return iDomainType;
    }
    public void setIDomainType(String domainType) {
        iDomainType = domainType;
    }
    public String getIEiacDeptId() {
        return iEiacDeptId;
    }
    public void setIEiacDeptId(String eiacDeptId) {
        iEiacDeptId = eiacDeptId;
    }
    public String getSysRightListJson() {
        return sysRightListJson;
    }
    
    public String getTabRightListJson() {
        return tabRightListJson;
    }
    
    public void setSysRightListJson(String sysRightListJson) {
        this.sysRightListJson = sysRightListJson;
    }
    public void setTabRightListJson(String tabRightListJson) {
        this.tabRightListJson = tabRightListJson;
    }
    public String getDeptIds(){
        return this.sDeptIds;
    }
    public List getReDeptList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.iDeptId as \"iDeptId\", a.sDeptName as \"sDeptName\", a.sDispName as \"sDispName\", ")
           .append("a.sShortName as \"sShortName\", a.iDeptType as \"iDeptType\", a.sDeptCode as \"sDeptCode\", ")
           .append("a.iDomainId as \"iDomainId\",  a.iParentId as \"iParentId\", a.sPathId as \"sPathId\" ")
           .append(" from tbOsDepartment a ");
        return getObjectList(sDeptIds, "dept", "iDeptId", sql);
    }
    public void setReDeptList(String sDeptIds) {
        this.sDeptIds = sDeptIds;
    }
    
    public String getDomainIds() {
        return this.sDomainIds;
    }
    public List getDomainList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.iDomainId as \"iDomainId\", a.sDomainName as \"sDomainName\", a.sDomainCode as \"sDomainCode\", ")
           .append("a.iDomainType as \"iDomainType\", a.iParentId as \"iParentId\", a.iSequence as \"iSequence\" ")
           .append(" from tbOsDomain a ");
        return getObjectList(sDomainIds, "domain", "iDomainId", sql);
    }
    public void setDomainList(String sDomainIds) {
        this.sDomainIds = sDomainIds;
    }
    public String getSDispName()
    {
        return sDispName;
    }
    public void setSDispName(String dispName)
    {
        sDispName = dispName;
    }
    
    public String getMenuRightIds() {
        return this.sMenuRightIds;
    }
    
    //获取菜单权限信息
    public List getMenuList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.iRight as \"iRight\", a.sPathId as \"sPathId\",")
           .append("a.sRightNo as \"sRightNo\", a.sRightName as \"sRightName\", ")
           .append("a.iSeq as \"iSeq\", a.iParentId as \"iParentId\", ")
           .append("a.iType as \"iType\", a.sURL as \"sURL\", ")
           .append("a.sIcon as \"sIcon\", a.sRemark as \"sRemark\", ")
           .append("a.sExInfo as \"sExInfo\",  ")
           .append("a.iIsSensitive as \"iIsSensitive\",iAppFor as \"iAppFor\" ");
        sql.append(" from tbOsRight a ");
        return getObjectList(sMenuRightIds, "right", "iRight", sql);
    }
    public void setMenuList(String sMenuRightIds) {
        this.sMenuRightIds = sMenuRightIds;
    }
    
    //获取自定义菜单信息
    public void setUIMenuList(List sUIMenuList) {
        this.sUIMenuList = sUIMenuList;
    }
    public List getUIMenuList(){
        return this.sUIMenuList;
    }
    public String getSIpAddress() {
        return sIpAddress;
    }
    public void setSIpAddress(String ipAddress) {
        sIpAddress = ipAddress;
    }
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public static List getAllMenuList() {
        return allMenuList;
    }
    public static void setAllMenuList(List list) {
        allMenuList.clear();
        allMenuList.addAll(list);
    }
    
    public String getBtnRightIds() {
        return this.sBtnRightIds;
    }
    
    public String getTabRightIds() {
        return this.sTabRightIds;
    }
    
    //获取按钮权限数据
    public List getSysRightList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.iRight as \"iRight\", a.sPathId as \"sPathId\",")
           .append("a.sRightNo as \"sRightNo\", a.sRightName as \"sRightName\", ")
           .append("a.iSeq as \"iSeq\", a.iParentId as \"iParentId\", ")
           .append("a.iType as \"iType\", a.sURL as \"sURL\", ")
           .append("a.sIcon as \"sIcon\", a.sRemark as \"sRemark\", ")
           .append("a.sExInfo as \"sExInfo\",")
           .append("a.iIsSensitive as \"iIsSensitive\",iAppFor as \"iAppFor\"  ")
           .append(" from tbOsRight a ");
        if("all".equals(sBtnRightIds)){
            sql.append(" where a.iStatus = 1 ");
        }
        return getObjectList(sBtnRightIds, "right", "iRight", sql);
    }
    
    /**
     * 查询列表
     * @param ids id范围
     * @param type 缓存key
     * @param key 主键
     * @param sql sql查询
     * @return 数据列表
     */
    private List getObjectList(String ids,  String type, String key, StringBuffer sql) {
        Map<String, Map> cacheMap = (Map<String, Map>)CacheUtil.getInstance().get(type);
        if (!StringUtil.checkStr(ids) || cacheMap==null) return new ArrayList();
        //超级管理员获取所有数据
        if ("all".equals(ids)){
            List<Map> newList = DatabaseUtil.queryForList(sql.toString(), safeMgrDataSource);
            //添加到缓存里
            for (Map m : newList) {
                cacheMap.put(String.valueOf(m.get(key)), m);
            }
            return newList;
        }
        List<Map> list = new ArrayList();
        StringBuffer noCacheIds = new StringBuffer();//没有缓存的岗位
        for (String sRightId : ids.split(",")) {
            Map m = cacheMap.get(sRightId);
            //如果缓存有数据,则从缓存拿,如果没有,从数据库找
            if (StringUtil.checkObj(m)) {
                list.add(m);
            } else {
                if (noCacheIds.length() > 0)noCacheIds.append(",");
                noCacheIds.append(sRightId);
            }
        }
        if (noCacheIds.length() > 0) {
            if (noCacheIds.toString().split(",").length < 1000) {
                sql.append(" where a.").append(key).append(" in (").append(noCacheIds.toString()).append(")");
            } else {
                sql.append(" where a.").append(key).append(" in (-9999");
                String[] strs = noCacheIds.toString().split(",");
                for (int i = 0; i < strs.length; i++ ) {
                    sql.append(",").append(strs[i]);
                    if (i%500==0 && i != strs.length - 1) {
                        sql.append(") or a.").append(key).append(" in (-9999");
                    }
                }
                sql.append(")");
            }
            List<Map> newList = DatabaseUtil.queryForList(sql.toString(), safeMgrDataSource);
            //添加到缓存里
            for (Map m : newList) {
                cacheMap.put(String.valueOf(m.get(key)), m);
            }
            list.addAll(newList);
        }
        CacheUtil.getInstance().put(type, cacheMap);
        return list;
    }
    
    
    public void setSysRightList(String sBtnRightIds) {
        this.sBtnRightIds = sBtnRightIds;
    }
    public void setTabRightList(String sTabRightIds) {
        this.sTabRightIds = sTabRightIds;
    }
    public String getSCityCode() {
        return sCityCode;
    }
    public void setSCityCode(String cityCode) {
        sCityCode = cityCode;
    }
    public Map getSensitiveMap() {
        return sensitiveMap;
    }
    public void setSensitiveMap(Map sensitiveMap) {
        this.sensitiveMap = sensitiveMap;
    }
    public String getSDomainCode() {
        return sDomainCode;
    }
    public void setSDomainCode(String domainCode) {
        sDomainCode = domainCode;
    }
    
    public String getNeObjRight(){
        return this.neObjRight;
    }
    public String getSysObjRight(){
        return this.sysObjRight;
    }
    public String getCableObjRight(){
        return this.cableObjRight;
    }
    public String getCirObjRight(){
        return this.cirObjRight;
    }
    public String getBeamObjRight(){
        return this.beamObjRight;
    }
    
    /**
     * 获取数据对象
     * @param type 1:网元 2:光缆 3:系统 4:电路 5:光路
     */
    public String getObjRight(int type){
        return getCache(type + "");
    }
    
    public void setNeObjRight(String neObjRight){
        setCache("1", neObjRight);
//        this.neObjRight = neObjRight;
    }
    public void setCableObjRight(String cableObjRight){
        setCache("2", cableObjRight);
//        this.cableObjRight = cableObjRight;
    }
    public void setSysObjRight(String sysObjRight){
        setCache("3", sysObjRight);
//        this.sysObjRight = sysObjRight;
    }
    public void setCirObjRight(String cirObjRight){
        setCache("4", cirObjRight);
//        this.cirObjRight = cirObjRight;
    }
    public void setBeamObjRight(String beamObjRight){
        setCache("5", beamObjRight);
//        this.beamObjRight = beamObjRight;
    }
    
    /**
     * 设置区域权限缓存
     */
    private void setCache(String type, String right){
        CacheUtil cache = CacheUtil.getInstance();
        Map map = (Map)cache.get("STAFF_DOMAIN_RIGHT");
        if(!StringUtil.checkObj(map)){
            map = new HashMap();
            map.put(this.getSStaffAccount(), new HashMap());
        }
        Map objRight = (Map)map.get(this.getSStaffAccount());
        if(!StringUtil.checkObj(objRight)){
            objRight = new HashMap();
            map.put(this.getSStaffAccount(), objRight);
        }
        objRight.put(type, right);
        cache.put("STAFF_DOMAIN_RIGHT", map);
    }
    
    /**
     * 获取区域权限缓存
     */
    private String getCache(String type){
        String objDomainRight = ConfigInit.Config.getProperty("objDomainRight","0");
        if("0".equals(objDomainRight)){
            return null;
        }
        CacheUtil cache = CacheUtil.getInstance();
        Map map = (Map)cache.get("STAFF_DOMAIN_RIGHT");
        if(!StringUtil.checkObj(map)){
            map = new HashMap();
            map.put(this.getSStaffAccount(), new HashMap());
        }
        Map objRight = (Map)map.get(this.getSStaffAccount());
        if(!StringUtil.checkObj(objRight)){
            objRight = new HashMap();
        }
        cache.put("STAFF_DOMAIN_RIGHT", map);
        return (String)objRight.get(type);
    }
}
