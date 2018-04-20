package vo.deptMgr;

import java.io.Serializable;

public class Staff implements Serializable{
    /**
     * ÷∏∂®–Ú¡–∫≈
     */
    private static final long serialVersionUID = 1933451379004624400L;
	private int iCount;
	private String iStaffId;
	private String sStaffName;
	private String sStaffNo;
	private String sStaffAccount;
	private String sPassword;
	private String sTelphone;
	private String sMobile;
	private String cCDMA;
	private String sPHS;
	private String sOutEmail;
	private String sInEmail;
	private String sFaxCode;
	private String sPwdQuestion;
	private String sCustomQuestion;
	private String sPwdAnswer;
	private String dAvailBeginDate;
	private String dAvailEndDate;
	private String iDeptId;
	private String iDomainId;
	private String iTitletId;
	private String iPostId;
	private String iErrorTime;
	private String dErrorDate;
	private int iDelFlag;
	private String dCreateDate;
	private String iCreateStaffId;
	private String dLastModify;
	private String iLastModifyStaffId;
	private String sGuid;
	private String sDeptName;
	private String sDomainName;
	private String sTitleName;
	private String sPostName;
	private String sClassGroupName;
	private String iTitleId;
	private String iIsSynch;
	private int iValid;
	
	public String getITitleId() {
		return iTitleId;
	}
	public void setITitleId(String titleId) {
		iTitleId = titleId;
	}
	public String getSClassGroupName() {
		return sClassGroupName;
	}
	public void setSClassGroupName(String classGroupName) {
		sClassGroupName = classGroupName;
	}
	public String getSPostName() {
		return sPostName;
	}
	public void setSPostName(String postName) {
		sPostName = postName;
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
	public String getCCDMA() {
		return cCDMA;
	}
	public void setCCDMA(String ccdma) {
		cCDMA = ccdma;
	}
	public String getDAvailBeginDate() {
		return dAvailBeginDate;
	}
	public void setDAvailBeginDate(String availBeginDate) {
		dAvailBeginDate = availBeginDate;
	}
	public String getDAvailEndDate() {
		return dAvailEndDate;
	}
	public void setDAvailEndDate(String availEndDate) {
		dAvailEndDate = availEndDate;
	}
	public String getDCreateDate() {
		return dCreateDate;
	}
	public void setDCreateDate(String createDate) {
		dCreateDate = createDate;
	}
	public String getDLastModify() {
		return dLastModify;
	}
	public void setDLastModify(String lastModify) {
		dLastModify = lastModify;
	}
	public String getICreateStaffId() {
		return iCreateStaffId;
	}
	public void setICreateStaffId(String createStaffId) {
		iCreateStaffId = createStaffId;
	}
	public String getIErrorTime() {
		return iErrorTime;
	}
	public void setIErrorTime(String errorTime) {
		iErrorTime = errorTime;
	}
	public String getDErrorDate() {
		return dErrorDate;
	}
	public void setDErrorDate(String errorDate) {
		dErrorDate = errorDate;
	}
	public int getIDelFlag() {
		return iDelFlag;
	}
	public void setIDelFlag(int delFlag) {
		iDelFlag = delFlag;
	}
	public String getIDeptId() {
		return iDeptId;
	}
	public void setIDeptId(String deptId) {
		iDeptId = deptId;
	}
	public String getIDomainId() {
		return iDomainId;
	}
	public void setIDomainId(String domainId) {
		iDomainId = domainId;
	}
	public String getILastModifyStaffId() {
		return iLastModifyStaffId;
	}
	public void setILastModifyStaffId(String lastModifyStaffId) {
		iLastModifyStaffId = lastModifyStaffId;
	}
	public String getIStaffId() {
		return iStaffId;
	}
	public void setIStaffId(String staffId) {
		iStaffId = staffId;
	}
	public String getITitletId() {
		return iTitletId;
	}
	public void setITitletId(String titletId) {
		iTitletId = titletId;
	}
	public String getSFaxCode() {
		return sFaxCode;
	}
	public void setSFaxCode(String faxCode) {
		sFaxCode = faxCode;
	}
	public String getSGuid() {
		return sGuid;
	}
	public void setSGuid(String guid) {
		sGuid = guid;
	}
	public String getSInEmail() {
		return sInEmail;
	}
	public void setSInEmail(String inEmail) {
		sInEmail = inEmail;
	}
	public String getSMobile() {
		return sMobile;
	}
	public void setSMobile(String mobile) {
		sMobile = mobile;
	}
	public String getSOutEmail() {
		return sOutEmail;
	}
	public void setSOutEmail(String outEmail) {
		sOutEmail = outEmail;
	}
	public String getSPassword() {
		return sPassword;
	}
	public void setSPassword(String password) {
		sPassword = password;
	}
	public String getSPHS() {
		return sPHS;
	}
	public void setSPHS(String sphs) {
		sPHS = sphs;
	}
	public String getSStaffAccount() {
		return sStaffAccount;
	}
	public void setSStaffAccount(String staffAccount) {
		sStaffAccount = staffAccount;
	}
	public String getSStaffName() {
		return sStaffName;
	}
	public void setSStaffName(String staffName) {
		sStaffName = staffName;
	}
	public String getSStaffNo() {
		return sStaffNo;
	}
	public void setSStaffNo(String staffNo) {
		sStaffNo = staffNo;
	}
	public String getSTelphone() {
		return sTelphone;
	}
	public void setSTelphone(String telphone) {
		sTelphone = telphone;
	}
	public String getIPostId() {
		return iPostId;
	}
	public void setIPostId(String postId) {
		iPostId = postId;
	}
	public int getICount() {
		return iCount;
	}
	public void setICount(int count) {
		iCount = count;
	}
	public String getIIsSynch() {
		return iIsSynch;
	}
	public void setIIsSynch(String isSynch) {
		iIsSynch = isSynch;
	}
    public String getSPwdQuestion() {
        return sPwdQuestion;
    }
    public void setSPwdQuestion(String pwdQuestion) {
        sPwdQuestion = pwdQuestion;
    }
    public String getSCustomQuestion() {
        return sCustomQuestion;
    }
    public void setSCustomQuestion(String customQuestion) {
        sCustomQuestion = customQuestion;
    }
    public String getSPwdAnswer() {
        return sPwdAnswer;
    }
    public void setSPwdAnswer(String pwdAnswer) {
        sPwdAnswer = pwdAnswer;
    }
    public int getIValid() {
        return iValid;
    }
    public void setIValid(String valid) {
        iValid = Integer.parseInt(valid);
    }
}
