create table TBDEPT
(
  IDEPTID   NUMBER(10) not null,
  CDEPTNAME VARCHAR2(64) not null
)
go
alter table TBDEPT
  add primary key (IDEPTID)
go
create table TBDUTY
(
  IDUTYID   NUMBER(10) not null,
  CDUTYNAME VARCHAR2(64) not null,
  IDEPTID   NUMBER(10)
)
go
alter table TBDUTY
  add primary key (IDUTYID)
go


create table TBEMPLOYEE
(
  IEMPLOYEEID      NUMBER(10) not null,
  CEMPLOYEENAME    VARCHAR2(64) not null,
  ISEX             INTEGER not null,
  IDUTYID          NUMBER(10) not null,
  IDEPTID          NUMBER(10) not null,
  ILENGTHOFSERVICE INTEGER not null,
  DEMPLOYDATE      DATE not null,
  CTEL             VARCHAR2(20),
  REMARK           VARCHAR2(1000)
)
go
alter table TBEMPLOYEE
  add primary key (IEMPLOYEEID)
go

/**基础数据**********************/
insert  into tbDept(iDeptId,cDeptName) values (1,'研发部')
go
insert  into tbDept(iDeptId,cDeptName) values(2,'售前部')
go
insert  into tbDept(iDeptId,cDeptName) values(3,'人事部')
go
insert  into tbDept(iDeptId,cDeptName) values(4,'产品部')
go
insert  into tbDept(iDeptId,cDeptName) values(5,'财务部')
go
insert  into tbDept(iDeptId,cDeptName) values(6,'工程部')
go

INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 1, '开发经理', 1 )
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 2, '副开发经理', 1 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 3, '副测试经理', 1 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 4, '项目副开发经理', 1 )
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 5, '客户经理', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 6, '高级客户经理', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 7, '售前总经理', 2 )
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 8, '人力资源助理', 3 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 9, '人力经理', 3 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 10, '售前a职', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 11, '售前b职', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 12, '售前c职', 2 )
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 13, '售前d职', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 14, '售前e职', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 15, '售前f职', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 16, '售前g职', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 17, '售前h职', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 18, '售前i职', 2 ) 
go

insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000430,'AA',1,1,5,TO_DATE('2011-08-22 02:02:00', 'YYYY-MM-DD HH24:MI:SS'),'13322333322','test',1) 
go
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000431,'BB',1,1,5,TO_DATE('2011-08-22 02:02:00', 'YYYY-MM-DD HH24:MI:SS'),'13322333322','test',1)
 go
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000432,'CC',1,1,5,TO_DATE('2011-08-22 02:02:00', 'YYYY-MM-DD HH24:MI:SS'),'13322333322','test',1) 
go
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000433,'DD',1,1,5,TO_DATE('2011-08-22 02:02:00', 'YYYY-MM-DD HH24:MI:SS'),'13322333322','test',1) 
go
insert  into tbEmployee(iEmployeeId,cEmployeeName,iSex,iDeptId,iLengthOfService,dEmployDate,cTel,remark,iDutyId) values (10000434,'EE',1,1,5,TO_DATE('2011-08-22 02:02:00', 'YYYY-MM-DD HH24:MI:SS'),'13322333322','test',1) 
go