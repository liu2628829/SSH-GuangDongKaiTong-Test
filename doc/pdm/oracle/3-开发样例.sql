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

/**��������**********************/
insert  into tbDept(iDeptId,cDeptName) values (1,'�з���')
go
insert  into tbDept(iDeptId,cDeptName) values(2,'��ǰ��')
go
insert  into tbDept(iDeptId,cDeptName) values(3,'���²�')
go
insert  into tbDept(iDeptId,cDeptName) values(4,'��Ʒ��')
go
insert  into tbDept(iDeptId,cDeptName) values(5,'����')
go
insert  into tbDept(iDeptId,cDeptName) values(6,'���̲�')
go

INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 1, '��������', 1 )
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 2, '����������', 1 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 3, '�����Ծ���', 1 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 4, '��Ŀ����������', 1 )
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 5, '�ͻ�����', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 6, '�߼��ͻ�����', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 7, '��ǰ�ܾ���', 2 )
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 8, '������Դ����', 3 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 9, '��������', 3 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 10, '��ǰaְ', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 11, '��ǰbְ', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 12, '��ǰcְ', 2 )
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 13, '��ǰdְ', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 14, '��ǰeְ', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 15, '��ǰfְ', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 16, '��ǰgְ', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 17, '��ǰhְ', 2 ) 
go
INSERT INTO tbDuty ( iDutyId, cDutyName, iDeptId )  VALUES ( 18, '��ǰiְ', 2 ) 
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