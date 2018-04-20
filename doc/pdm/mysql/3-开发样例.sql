CREATE TABLE `tbdept` (
  `iDeptId` decimal(10,0) NOT NULL,
  `cDeptName` varchar(64) NOT NULL,
  PRIMARY KEY (`iDeptId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tbduty` (
  `iDutyId` decimal(10,0) NOT NULL,
  `cDutyName` varchar(64) NOT NULL,
  `iDeptId` decimal(10,0) DEFAULT NULL,
  PRIMARY KEY (`iDutyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

/**基础数据**********************/
insert  into tbDept(iDeptId,cDeptName) values (1,'研发部');
insert  into tbDept(iDeptId,cDeptName) values(2,'售前部');
insert  into tbDept(iDeptId,cDeptName) values(3,'人事部');
insert  into tbDept(iDeptId,cDeptName) values(4,'产品部');
insert  into tbDept(iDeptId,cDeptName) values(5,'财务部');
insert  into tbDept(iDeptId,cDeptName) values(6,'工程部');

INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 1, '开发经理', 1 );
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 2, '副开发经理', 1 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 3, '副测试经理', 1 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 4, '项目副开发经理', 1 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 5, '客户经理', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 6, '高级客户经理', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 7, '售前总经理', 2 );
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 8, '人力资源助理', 3 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 9, '人力经理', 3 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 10, '售前a职', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 11, '售前b职', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 12, '售前c职', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 13, '售前d职', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 14, '售前e职', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 15, '售前f职', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 16, '售前g职', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 17, '售前h职', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 18, '售前i职', 2 ) ;

insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000430,'AA',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000431,'BB',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000432,'CC',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000433,'DD',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000434,'EE',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
