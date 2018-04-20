create table tbLogFileInfo
(
  ILOGID          INTEGER not null primary key,
  SLOGPATTERN     VARCHAR2(50) not null,
  SLOGFILEIP      VARCHAR2(32),
  SLOGFILEPATH    VARCHAR2(128),
  ISIZECOUNT      INTEGER,
  ISTATUS         INTEGER,
  DFILECREATETIME DATE,
  SSYSTEMNAME VARCHAR2(32)
);

---------------------------------------------------------------------------

--����JDBCLog��
create table TBJDBCLOG
(
  IID         NUMBER(15) not null primary key,
  SMD5        VARCHAR2(64),
  SSQL        VARCHAR2(3000),
  ICOSTTIME   INTEGER,
  IROWCOUNT   INTEGER,
  SDATASOURCE VARCHAR2(32),
  DINSERTTIME DATE,
  SPATH       VARCHAR2(2560),
  SREMARK     VARCHAR2(500),
  SSYSTEMNAME VARCHAR2(32)
);

---------------------------------------------------------------------
--����Exceptiong��
create table TBEXCEPTIONLOG
(
  IID                   NUMBER(15) not null primary key,
  SMD5                  VARCHAR2(64),
  DTHROWTIME            DATE not null,
  ISTAFFID              NUMBER(15),
  SSTAFFACCOUNT         VARCHAR2(32),
  SSTAFFNAME            VARCHAR2(32),
  SSYSEXCEPTIONNAME     VARCHAR2(500),
  SSYSEXCEPTIONMESSAGE  VARCHAR2(1024),
  SSELFEXCEPTIONMESSAGE VARCHAR2(500),
  SPATH                 VARCHAR2(1024),
  SPARAMS               CLOB,
  SDATASOURCE           VARCHAR2(32),
  SSQL                  VARCHAR2(2000),
  SURL                  VARCHAR2(255),
  IACTIONID             NUMBER(15),
  ICOSTTIME             INTEGER,
  IRIGHTID      NUMBER(15),
  IBTNRIGHTID       NUMBER(15),
  sCONTENT      VARCHAR2(512),
  SSYSTEMNAME VARCHAR2(32),
  SREFERER VARCHAR2(256)   
);

---------------------------------------------------------------------
--����action��right�Ĺ�����
create table TBACTIONRELRIGHT
(
  IACTIONID   NUMBER(15) PRIMARY KEY NOT NULL,
  SACTIONNAME VARCHAR2(128) not null,
  SMETHODNAME VARCHAR2(64) not null,
  IRIGHTID    NUMBER(15),
  IBTNRIGHTID NUMBER(15),
  SCONTENT    VARCHAR2(512)
);


---------------------------------------------------------------------
--������¼��־��
create table TBLOGINLOG
(
  SLOGINNAME    VARCHAR2(30) not null,
  ISTATE        INTEGER not null,
  DLOGINTIME    DATE not null,
  DLOGINOUTTIME DATE,
  ILOGINSECONDS NUMBER(14),
  SESSIONID     VARCHAR2(50) not null,
  SBROWSERTYPE  VARCHAR2(30),
  SLOGINIP      VARCHAR2(32),
  SMD5          VARCHAR2(64),
  SSYSTEMNAME VARCHAR2(32),
  constraint PK_TBLOGINLOG primary key (SESSIONID, DLOGINTIME)
);

---------------------------------------------------------------------
--����action�����Ͱ�ť�Ĺ�����
CREATE TABLE TBACTION2RIGHT ( 
    IID     NUMBER(15,0) PRIMARY KEY NOT NULL,
    IACTIONID    NUMBER(15,0) NULL, -- ������ť��Ӧ��action
    IRIGHTID    NUMBER(15,0) NULL, -- ������ť����ģ��ID,���޹���״̬�����ֶ�Ϊ��
    IBTNRIGHTID NUMBER(15,0) NULL, -- ������ťID,���޹���״̬�����ֶ�Ϊ��
    SCONTENT    VARCHAR2(512) NULL, --����״̬���������������������ǹ���״̬��������ע
    IRELTYPE    NUMBER(2,0) NULL, -- -1:�޹��� 0����ǵ�δ������ 1��һ��һ���� 2��һ�Զ���� 
    SREQPARAMNAME VARCHAR2(64) NULL,  --һ�Զ������ʱ�������������
    SBTNPARAMVALUE VARCHAR2(128) NULL, --һ�Զ������ʱ�������������ֵ
    
    SUPDATEUSERNAME VARCHAR2(64) NULL, -- ����������Ա����
    DUPDATEDATE DATE NULL,  -- ��������������ʱ��
    SREMARK VARCHAR2(256) NULL -- ��ע
);