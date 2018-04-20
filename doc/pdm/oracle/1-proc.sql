--主键序列
create sequence SEQ_COMMON_ID
minvalue 1
maxvalue 999999999999999999999999999
start with 50000
increment by 1
cache 20;

--分页存储过程
CREATE OR REPLACE PACKAGE "BYPAGEPACKAGE" AS
    TYPE BYPAGE_CURSOR IS REF CURSOR ;
end bypagepackage;

CREATE OR REPLACE PROCEDURE "GETDATABYPAGEEOMS" (p_cursor out bypagepackage.BYPAGE_CURSOR, sqlstr in varchar,limit in int,page in int ) as
reccount int;
tempstr varchar(5000);
begin
      tempstr:='select count(*) from ('||sqlstr||')';
      execute immediate tempstr into reccount ;
      tempstr:='select '||reccount||'as "iRecCount", '||reccount||' as "totalCount",tm2.* from (select rownum as rn,tm1.* from ( '||sqlstr||') tm1) tm2 where rn > '||((page-1)*limit)||' and rn <='||(page*limit);
      OPEN p_CURSOR for tempstr;
end GetDataByPageEoms;

--取主键存储过程(已过期未用)
CREATE OR REPLACE PROCEDURE "SP_GET_ID_EX" (tbl in varchar,cur out bypagepackage.BYPAGE_CURSOR)
AS
tbl1 varchar(100);
s varchar2(1000);
BEGIN
    if tbl is null
    then
        tbl1 := 'Global' ;
    else
        tbl1:=tbl;
    end if;

     s:='update tbPubSequence set iCurSquence = iCurSquence + 1 where sTblName = :tbl1';
     execute immediate s using tbl1;
     execute immediate 'commit';
     s:= 'select iCurSquence from tbPubSequence where sTblName = :tbl1';
     --execute immediate s into id using tbl1;
     open cur for s using tbl1;
END SP_GET_ID_EX;

/**表有自增长列时，会用到此函数，方言类里 getIncrementSQL(tableName) 有写,通用表单有用到 */
create or replace function SP_GET_INCREMENT (tbl in varchar2) return number
is 
type c_t is ref cursor;
c c_t;
r tbPubSequence%rowtype;
tbl1 varchar2(64);
s varchar2(1000);
seq number(15);
BEGIN
     if tbl is null
     then
        tbl1 := 'Global' ;
     else
        tbl1:=tbl;
     end if;    
     
     s:='update tbPubSequence set iCurSquence = iCurSquence + 1 where sTblName = :tbl1';
     execute immediate s using tbl1;
     s:= 'select * from tbPubSequence where sTblName = :en';
     open c for s using tbl1;
     loop fetch c into r;
      exit when c%notfound;
      seq := r.iCurSquence;
     end loop;
     close c; 
     dbms_output.put_line(seq);
     
     if seq is null
     then 
       s:='insert into tbPubSequence (sTblName,iCurSquence) values(:tbl1,1)';
       execute immediate s using tbl1;
       seq := 1;
     end if;
     return seq;    
END SP_GET_INCREMENT;