/*
Navicat MySQL Data Transfer

Source Server         : root@local
Source Server Version : 50173
Source Host           : localhost:3306
Source Database       : safemgrdev

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2018-04-24 14:50:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbactionrelright
-- ----------------------------
DROP TABLE IF EXISTS `tbactionrelright`;
CREATE TABLE `tbactionrelright` (
  `iActionId` decimal(15,0) NOT NULL,
  `sActionName` varchar(128) NOT NULL,
  `sMethodName` varchar(64) NOT NULL,
  `iRightId` decimal(15,0) DEFAULT NULL,
  `iBtnRightId` decimal(15,0) DEFAULT NULL,
  `sContent` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`iActionId`),
  KEY `tbActionRelRight_sActionName` (`sActionName`),
  KEY `tbActionRelRight_iRightId` (`iRightId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbactionrelright
-- ----------------------------

-- ----------------------------
-- Table structure for tbctenumtbl2
-- ----------------------------
DROP TABLE IF EXISTS `tbctenumtbl2`;
CREATE TABLE `tbctenumtbl2` (
  `sEnumTblName` varchar(64) NOT NULL,
  `sEnumColName` varchar(64) NOT NULL,
  `iEnumValue` varchar(64) NOT NULL,
  `sEnumName` varchar(64) NOT NULL,
  `isEnabled` smallint(6) NOT NULL,
  `sRemark` varchar(300) DEFAULT NULL,
  `iDomainId` decimal(15,0) NOT NULL,
  KEY `tbCtEnumTbl2_sEnumTblName` (`sEnumTblName`),
  KEY `tbCtEnumTbl2_sEnumColName` (`sEnumColName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbctenumtbl2
-- ----------------------------

-- ----------------------------
-- Table structure for tbdept
-- ----------------------------
DROP TABLE IF EXISTS `tbdept`;
CREATE TABLE `tbdept` (
  `iDeptId` decimal(10,0) NOT NULL,
  `cDeptName` varchar(64) NOT NULL,
  PRIMARY KEY (`iDeptId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbdept
-- ----------------------------
INSERT INTO `tbdept` VALUES ('1', '研发部');
INSERT INTO `tbdept` VALUES ('2', '售前部');
INSERT INTO `tbdept` VALUES ('3', '人事部');
INSERT INTO `tbdept` VALUES ('4', '产品部');
INSERT INTO `tbdept` VALUES ('5', '财务部');
INSERT INTO `tbdept` VALUES ('6', '工程部');

-- ----------------------------
-- Table structure for tbduty
-- ----------------------------
DROP TABLE IF EXISTS `tbduty`;
CREATE TABLE `tbduty` (
  `iDutyId` decimal(10,0) NOT NULL,
  `cDutyName` varchar(64) NOT NULL,
  `iDeptId` decimal(10,0) DEFAULT NULL,
  PRIMARY KEY (`iDutyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbduty
-- ----------------------------
INSERT INTO `tbduty` VALUES ('1', '开发经理', '1');
INSERT INTO `tbduty` VALUES ('2', '副开发经理', '1');
INSERT INTO `tbduty` VALUES ('3', '副测试经理', '1');
INSERT INTO `tbduty` VALUES ('4', '项目副开发经理', '1');
INSERT INTO `tbduty` VALUES ('5', '客户经理', '2');
INSERT INTO `tbduty` VALUES ('6', '高级客户经理', '2');
INSERT INTO `tbduty` VALUES ('7', '售前总经理', '2');
INSERT INTO `tbduty` VALUES ('8', '人力资源助理', '3');
INSERT INTO `tbduty` VALUES ('9', '人力经理', '3');
INSERT INTO `tbduty` VALUES ('10', '售前a职', '2');
INSERT INTO `tbduty` VALUES ('11', '售前b职', '2');
INSERT INTO `tbduty` VALUES ('12', '售前c职', '2');
INSERT INTO `tbduty` VALUES ('13', '售前d职', '2');
INSERT INTO `tbduty` VALUES ('14', '售前e职', '2');
INSERT INTO `tbduty` VALUES ('15', '售前f职', '2');
INSERT INTO `tbduty` VALUES ('16', '售前g职', '2');
INSERT INTO `tbduty` VALUES ('17', '售前h职', '2');
INSERT INTO `tbduty` VALUES ('18', '售前i职', '2');

-- ----------------------------
-- Table structure for tbemployee
-- ----------------------------
DROP TABLE IF EXISTS `tbemployee`;
CREATE TABLE `tbemployee` (
  `iEmployeeId` decimal(10,0) NOT NULL,
  `cEmployeeName` varchar(64) NOT NULL,
  `iSex` smallint(6) NOT NULL,
  `iDeptId` decimal(10,0) NOT NULL,
  `iLengthOfService` smallint(6) NOT NULL,
  `dEmployDate` datetime NOT NULL,
  `cTel` varchar(20) DEFAULT NULL,
  `remark` varchar(512) DEFAULT NULL,
  `iDutyId` decimal(15,0) DEFAULT NULL,
  PRIMARY KEY (`iEmployeeId`),
  KEY `tbEmployee_iSex` (`iSex`),
  KEY `tbEmployee_iDutyId` (`iDutyId`),
  KEY `tbEmployee_iDeptId` (`iDeptId`),
  KEY `tbEmployee_dEmployDate` (`dEmployDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbemployee
-- ----------------------------
INSERT INTO `tbemployee` VALUES ('100100', 'liumiao', '1', '1', '1', '2018-04-23 00:00:00', 'aaaa', 'weqweqw', '2');
INSERT INTO `tbemployee` VALUES ('100101', 'liumiao', '1', '1', '1', '2018-04-23 00:00:00', 'aaaa', 'weqweqw', '2');
INSERT INTO `tbemployee` VALUES ('100102', 'gfg', '1', '1', '1', '2018-04-23 00:00:00', 'aaaa', '12321', '1');
INSERT INTO `tbemployee` VALUES ('100103', 'gfg', '1', '1', '1', '2018-04-23 00:00:00', 'aaaa', '12321', '1');
INSERT INTO `tbemployee` VALUES ('111112', 'sdasds', '1', '1', '5', '2018-04-15 19:13:43', '121231231', '123123', '123');
INSERT INTO `tbemployee` VALUES ('111113', 'sdasdss', '1', '1', '5', '2018-04-15 19:13:43', '121231231', '123123', '123');
INSERT INTO `tbemployee` VALUES ('10000431', 'BB', '1', '1', '5', '2011-08-22 02:02:00', '13322333322', 'test', '1');
INSERT INTO `tbemployee` VALUES ('10000432', 'CC', '1', '1', '5', '2011-08-22 02:02:00', '13322333322', 'test', '1');
INSERT INTO `tbemployee` VALUES ('10000433', 'DD', '1', '1', '5', '2011-08-22 02:02:00', '13322333322', 'test', '1');
INSERT INTO `tbemployee` VALUES ('10000434', 'EE', '1', '1', '5', '2011-08-22 02:02:00', '13322333322', 'test', '1');

-- ----------------------------
-- Table structure for tbexceptionlog
-- ----------------------------
DROP TABLE IF EXISTS `tbexceptionlog`;
CREATE TABLE `tbexceptionlog` (
  `iId` int(11) NOT NULL,
  `sMd5` varchar(64) DEFAULT NULL,
  `dThrowTime` datetime NOT NULL,
  `iStaffId` decimal(15,0) DEFAULT NULL,
  `sStaffAccount` varchar(32) DEFAULT NULL,
  `sStaffName` varchar(32) DEFAULT NULL,
  `sSysExceptionName` varchar(512) DEFAULT NULL,
  `sSysExceptionMessage` varchar(1024) DEFAULT NULL,
  `sSelfExceptionMessage` varchar(512) DEFAULT NULL,
  `sPath` varchar(1024) DEFAULT NULL,
  `sParams` varchar(1024) DEFAULT NULL,
  `sDataSource` varchar(32) DEFAULT NULL,
  `sSql` varchar(2048) DEFAULT NULL,
  `sUrl` varchar(256) DEFAULT NULL,
  `iActionId` decimal(15,0) DEFAULT NULL,
  `iCostTime` int(11) DEFAULT NULL,
  `iRightId` decimal(15,0) DEFAULT NULL,
  `iBtnRightId` decimal(15,0) DEFAULT NULL,
  `sContent` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`iId`),
  KEY `tbExceptionLog_dThrowTime` (`dThrowTime`),
  KEY `tbExceptionLog_ExceptionName` (`sSysExceptionName`(255)),
  KEY `tbExceptionLog_sPath` (`sPath`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbexceptionlog
-- ----------------------------

-- ----------------------------
-- Table structure for tbjdbclog
-- ----------------------------
DROP TABLE IF EXISTS `tbjdbclog`;
CREATE TABLE `tbjdbclog` (
  `iId` int(11) NOT NULL,
  `sMd5` varchar(64) DEFAULT NULL,
  `sSql` varchar(2048) DEFAULT NULL,
  `iCostTime` int(11) DEFAULT NULL,
  `iRowCount` int(11) DEFAULT NULL,
  `sDataSource` varchar(32) DEFAULT NULL,
  `dInsertTime` datetime NOT NULL,
  `sPath` varchar(1024) DEFAULT NULL,
  `sRemark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`iId`),
  KEY `tbJdbcLog_iCostTime` (`iCostTime`),
  KEY `tbJdbcLog_dInsertTime` (`dInsertTime`),
  KEY `tbJdbcLog_sPath` (`sPath`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbjdbclog
-- ----------------------------

-- ----------------------------
-- Table structure for tblogfileinfo
-- ----------------------------
DROP TABLE IF EXISTS `tblogfileinfo`;
CREATE TABLE `tblogfileinfo` (
  `iLogId` int(11) NOT NULL,
  `sLogPattern` varchar(50) NOT NULL,
  `sLogFileIp` varchar(32) DEFAULT NULL,
  `sLogFilePath` varchar(128) DEFAULT NULL,
  `iSizeCount` int(11) DEFAULT NULL,
  `iStatus` int(11) DEFAULT NULL,
  `dFileCreateTime` int(11) DEFAULT NULL,
  PRIMARY KEY (`iLogId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tblogfileinfo
-- ----------------------------

-- ----------------------------
-- Table structure for tbloginlog
-- ----------------------------
DROP TABLE IF EXISTS `tbloginlog`;
CREATE TABLE `tbloginlog` (
  `sLoginName` varchar(30) NOT NULL,
  `iState` smallint(6) NOT NULL,
  `dLoginTime` datetime NOT NULL,
  `dLoginOutTime` datetime DEFAULT NULL,
  `iLoginSeconds` decimal(14,0) DEFAULT NULL,
  `sessionID` varchar(50) NOT NULL,
  `sBrowserType` varchar(30) DEFAULT NULL,
  `sLoginIP` varchar(32) DEFAULT NULL,
  `sMd5` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`sessionID`,`dLoginTime`),
  KEY `tbLoginLog_dLoginTime` (`dLoginTime`),
  KEY `tbLoginLog_sLoginName` (`sLoginName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbloginlog
-- ----------------------------

-- ----------------------------
-- Table structure for tbpubfilesupload
-- ----------------------------
DROP TABLE IF EXISTS `tbpubfilesupload`;
CREATE TABLE `tbpubfilesupload` (
  `iFileId` decimal(15,0) NOT NULL,
  `sTableName` varchar(32) NOT NULL,
  `iTablePKId` decimal(15,0) NOT NULL,
  `sNewFileName` varchar(128) NOT NULL,
  `sOldFileName` varchar(128) NOT NULL,
  `dUploadTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cUpStaffName` varchar(64) DEFAULT NULL,
  `iUpStaffId` decimal(15,0) DEFAULT NULL,
  `cDeptName` varchar(64) DEFAULT NULL,
  `iDeptId` decimal(15,0) DEFAULT NULL,
  `iDomainId` decimal(15,0) DEFAULT NULL,
  `cSvrFilePath` varchar(512) DEFAULT NULL,
  `sFileSize` varchar(32) DEFAULT NULL,
  `iDownTimes` decimal(15,0) DEFAULT NULL,
  PRIMARY KEY (`iFileId`),
  KEY `tbPubFilesUpload_sNewFileName` (`sNewFileName`),
  KEY `tbPubFilesUpload_cUpStaffName` (`cUpStaffName`),
  KEY `tbPubFilesUpload_cDeptName` (`cDeptName`),
  KEY `tbPubFilesUpload_dUploadTime` (`dUploadTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbpubfilesupload
-- ----------------------------

-- ----------------------------
-- Table structure for tbpubsequence
-- ----------------------------
DROP TABLE IF EXISTS `tbpubsequence`;
CREATE TABLE `tbpubsequence` (
  `sTblName` varchar(64) NOT NULL,
  `iCurSquence` decimal(15,0) NOT NULL DEFAULT '1',
  PRIMARY KEY (`sTblName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbpubsequence
-- ----------------------------
INSERT INTO `tbpubsequence` VALUES ('Global', '100');
INSERT INTO `tbpubsequence` VALUES ('Global2', '100300');

-- ----------------------------
-- Table structure for tb_files
-- ----------------------------
DROP TABLE IF EXISTS `tb_files`;
CREATE TABLE `tb_files` (
  `IID` decimal(15,0) NOT NULL,
  `S_FILE_NAME` varchar(128) NOT NULL,
  `I_FILE_TYPE` smallint(6) DEFAULT NULL,
  `S_FILE_PATH` varchar(256) DEFAULT NULL,
  `I_FILE_SIZE` decimal(10,0) DEFAULT NULL,
  `S_UPLOAD_USER` varchar(64) DEFAULT NULL,
  `D_UPLOAD_TIME` datetime DEFAULT NULL,
  `S_REMARK` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`IID`),
  KEY `TB_FILES_I_FILE_TYPE` (`I_FILE_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_files
-- ----------------------------
INSERT INTO `tb_files` VALUES ('1', '3213', '1', '123', '123', 'sda', '2018-04-23 08:14:55', 'sada');
INSERT INTO `tb_files` VALUES ('3', 's', '3', 's', '1', '1', '2018-04-23 00:00:00', 'ss');
INSERT INTO `tb_files` VALUES ('11', '3213', null, '123', '123', '1', '2018-04-23 00:00:00', 'sdd');
INSERT INTO `tb_files` VALUES ('21', '2323', null, '323', '3', '1', '2018-04-23 00:00:00', 'ds');
INSERT INTO `tb_files` VALUES ('31', 's', '4', 's', '1', '1', '2018-04-23 00:00:00', 'ss');
INSERT INTO `tb_files` VALUES ('100204', 's', '2', 's', '1', '1', '2018-04-23 00:00:00', 'ss');
INSERT INTO `tb_files` VALUES ('1002041', 's', '1', 's', '1', '1', '2018-04-23 00:00:00', 'ss');

-- ----------------------------
-- Procedure structure for GetDataByPageEoms
-- ----------------------------
DROP PROCEDURE IF EXISTS `GetDataByPageEoms`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetDataByPageEoms`(
__sqlStr   varchar(8000),
__start    int,
__limit    int
)
BEGIN
    SET __SqlStr = LTRIM(__SqlStr);
    IF __limit > 50000 THEN SET __limit = 50000; END IF;
    SET @SqlStr = CONCAT('select count(*) into @TotalCount from (', __SqlStr , ' ) __tempTb ' );
    PREPARE stmt1 FROM @SqlStr;
    EXECUTE stmt1;
    DEALLOCATE PREPARE stmt1;
    SET @rowId = -1;
    SET @SqlStr2 = CONCAT('SELECT @TotalCount as iRecCount , @TotalCount as totalCount ,(@rowId := @rowId + 1) as iRowId ,__TempTable.*  From ( ',__SqlStr , ') __TempTable limit ');
    SET @SqlStr2 = CONCAT(@SqlStr2,CONCAT(__start,',',__limit));
    PREPARE stmt2 FROM @SqlStr2;
    EXECUTE stmt2;
    DEALLOCATE PREPARE stmt2;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_GET_ID_EX
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_GET_ID_EX`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_GET_ID_EX`(__tbl varchar(32))
BEGIN
DECLARE __Id NUMERIC(15);    
    IF RTRIM(__tbl) IS NULL THEN 
       SELECT __tbl = 'Global'; 
    END IF;
    
    START TRANSACTION;
    UPDATE tbPubSequence SET iCurSquence = iCurSquence + 1 WHERE sTblName = __tbl;  
    SELECT iCurSquence AS Id FROM tbPubSequence WHERE sTblName = __tbl;  
    COMMIT;    
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_GET_ID_EX2
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_GET_ID_EX2`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_GET_ID_EX2`(cachePrimaryKeys int)
BEGIN
    START TRANSACTION;
    SELECT iCurSquence AS Id FROM tbPubSequence WHERE sTblName ='Global2';  
    UPDATE tbPubSequence SET iCurSquence = iCurSquence + cachePrimaryKeys WHERE sTblName ='Global2';  
    COMMIT;    
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for SP_GET_INCREMENT
-- ----------------------------
DROP FUNCTION IF EXISTS `SP_GET_INCREMENT`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `SP_GET_INCREMENT`(tbl VARCHAR(64)) RETURNS decimal(15,0)
BEGIN
   DECLARE sqe NUMERIC(15) DEFAULT NULL;
   DECLARE tbl1 VARCHAR(64) DEFAULT 'Global';
   
   IF tbl IS NOT NULL THEN
    SET tbl1 = tbl;
   END IF;
    SELECT iCurSquence INTO sqe FROM tbPubSequence WHERE sTblName = tbl1; 
    IF sqe IS NULL THEN
      INSERT INTO  tbPubSequence (sTblName,iCurSquence) VALUES (tbl1,2);
      SET sqe = 1;
    ELSE
      UPDATE tbPubSequence SET iCurSquence = iCurSquence + 1 WHERE sTblName = tbl1;  
    END IF;
   RETURN sqe;
END
;;
DELIMITER ;
