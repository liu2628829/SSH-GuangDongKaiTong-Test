
CREATE TABLE tbPubSequence (
  sTblName varchar(64) NOT NULL,
  iCurSquence decimal(15,0) NOT NULL DEFAULT '1',
  PRIMARY KEY (sTblName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
go

CREATE TABLE tbCtEnumTbl2 (
  sEnumTblName varchar(64) NOT NULL,
  sEnumColName varchar(64) NOT NULL,
  iEnumValue varchar(64) NOT NULL,
  sEnumName varchar(64) NOT NULL,
  isEnabled smallint(6) NOT NULL,
  sRemark varchar(300) DEFAULT NULL,
  iDomainId decimal(15,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8
go
CREATE  INDEX tbCtEnumTbl2_sEnumTblName  ON tbCtEnumTbl2(sEnumTblName)
go
CREATE  INDEX tbCtEnumTbl2_sEnumColName  ON tbCtEnumTbl2(sEnumColName)
go

CREATE TABLE tbPubFilesUpload (
  iFileId decimal(15,0) NOT NULL,
  sTableName varchar(32) NOT NULL,
  iTablePKId decimal(15,0) NOT NULL,
  sNewFileName varchar(128) NOT NULL,
  sOldFileName varchar(128) NOT NULL,
  dUploadTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cUpStaffName varchar(64) DEFAULT NULL,
  iUpStaffId decimal(15,0) DEFAULT NULL,
  cDeptName varchar(64) DEFAULT NULL,
  iDeptId decimal(15,0) DEFAULT NULL,
  iDomainId decimal(15,0) DEFAULT NULL,
  cSvrFilePath varchar(512) DEFAULT NULL,
  sFileSize varchar(32) DEFAULT NULL,
  iDownTimes decimal(15,0) DEFAULT NULL,
  PRIMARY KEY (iFileId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
go
CREATE  INDEX tbPubFilesUpload_sNewFileName    ON tbPubFilesUpload(sNewFileName)
go
CREATE  INDEX tbPubFilesUpload_cUpStaffName    ON tbPubFilesUpload(cUpStaffName)
go
CREATE  INDEX tbPubFilesUpload_cDeptName    ON tbPubFilesUpload(cDeptName)
go
CREATE  INDEX tbPubFilesUpload_dUploadTime    ON tbPubFilesUpload(dUploadTime)
go


/**»ù´¡Êý¾Ý**********************/
INSERT INTO tbPubSequence ( sTblName, iCurSquence ) VALUES ( 'Global', 100 )
go
INSERT INTO tbPubSequence ( sTblName, iCurSquence ) VALUES ( 'Global2', 100000 )
go