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

--创建JDBCLog表
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
--创建Exceptiong表
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
--创建action和right的关联表
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
--创建登录日志表
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
--创建action方法和按钮的关联表
CREATE TABLE TBACTION2RIGHT ( 
    IID     NUMBER(15,0) PRIMARY KEY NOT NULL,
    IACTIONID    NUMBER(15,0) NULL, -- 关联按钮对应的action
    IRIGHTID    NUMBER(15,0) NULL, -- 关联按钮所在模块ID,当无关联状态，该字段为空
    IBTNRIGHTID NUMBER(15,0) NULL, -- 关联按钮ID,当无关联状态，该字段为空
    SCONTENT    VARCHAR2(512) NULL, --关联状态下用来将方法重命名；非关联状态下用来备注
    IRELTYPE    NUMBER(2,0) NULL, -- -1:无关联 0：标记但未做关联 1：一对一关联 2：一对多关联 
    SREQPARAMNAME VARCHAR2(64) NULL,  --一对多关联的时候，用来存参数名
    SBTNPARAMVALUE VARCHAR2(128) NULL, --一对多关联的时候，用来存参数和值
    
    SUPDATEUSERNAME VARCHAR2(64) NULL, -- 关联配置人员名称
    DUPDATEDATE DATE NULL,  -- 关联配置最后更新时间
    SREMARK VARCHAR2(256) NULL -- 备注
);