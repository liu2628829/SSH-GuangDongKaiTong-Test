/*****************************以下脚本请一块一块执行 *****************************/
CREATE PROCEDURE GetDataByPageEoms(
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
go

/**************  Procedure structure for procedure `SP_GET_ID_EX` ****************************/

CREATE PROCEDURE `SP_GET_ID_EX`(__tbl varchar(32))
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
go

/************** Procedure structure for procedure `SP_GET_ID_EX2` ****************************/

CREATE PROCEDURE SP_GET_ID_EX2(cachePrimaryKeys int)
BEGIN
    START TRANSACTION;
    SELECT iCurSquence AS Id FROM tbPubSequence WHERE sTblName ='Global2';  
    UPDATE tbPubSequence SET iCurSquence = iCurSquence + cachePrimaryKeys WHERE sTblName ='Global2';  
    COMMIT;    
END 
go

/**表有自增长列时，会用到此函数，方言类里 getIncrementSQL(tableName) 有写,通用表单有用到*******/
CREATE FUNCTION SP_GET_INCREMENT (tbl VARCHAR(64))
RETURNS NUMERIC(15)
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
go