create table `tbLogFileInfo`
(
  `iLogId` int(11) NOT NULL,
  `sLogPattern` varchar(50) NOT NULL,
  `sLogFileIp` varchar(32) DEFAULT NULL,
  `sLogFilePath` varchar(128) DEFAULT NULL,
  `iSizeCount` int(11) DEFAULT NULL,
  `iStatus` int(11) DEFAULT NULL,
  `dFileCreateTime` int(11) DEFAULT NULL,
   PRIMARY KEY (`iLogId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
