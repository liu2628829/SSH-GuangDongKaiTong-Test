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

/**��������**********************/
insert  into tbDept(iDeptId,cDeptName) values (1,'�з���');
insert  into tbDept(iDeptId,cDeptName) values(2,'��ǰ��');
insert  into tbDept(iDeptId,cDeptName) values(3,'���²�');
insert  into tbDept(iDeptId,cDeptName) values(4,'��Ʒ��');
insert  into tbDept(iDeptId,cDeptName) values(5,'����');
insert  into tbDept(iDeptId,cDeptName) values(6,'���̲�');

INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 1, '��������', 1 );
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 2, '����������', 1 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 3, '�����Ծ���', 1 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 4, '��Ŀ����������', 1 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 5, '�ͻ�����', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 6, '�߼��ͻ�����', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 7, '��ǰ�ܾ���', 2 );
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 8, '������Դ����', 3 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 9, '��������', 3 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 10, '��ǰaְ', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 11, '��ǰbְ', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 12, '��ǰcְ', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 13, '��ǰdְ', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 14, '��ǰeְ', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 15, '��ǰfְ', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 16, '��ǰgְ', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 17, '��ǰhְ', 2 ) ;
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 18, '��ǰiְ', 2 ) ;

insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000430,'AA',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000431,'BB',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000432,'CC',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000433,'DD',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000434,'EE',1,1,5,'2011-08-22 02:02:00','13322333322','test',1);
