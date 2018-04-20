--文件信息管理
create table TB_FILES
(
  IID          NUMBER(15) not null,     --id主键
  S_FILE_NAME  VARCHAR2(128) not null,  --文件名
  I_FILE_TYPE  NUMBER(1),               --文件类型 (枚举：1.doc, 2.xls, 3.ppt,4.docx, 5.xlsx, 6.pptx)
  S_FILE_PATH  VARCHAR2(256),           --文件路径
  I_FILE_SIZE  NUMBER(10),              --文件大小（KB）
  S_UPLOAD_USER    VARCHAR2(64),        --文件上传人
  D_UPLOAD_TIME    DATE,                --文件上传时间
  S_REMARK     VARCHAR2(256)            --文件简介
)
;
alter table TB_FILES add constraint PK_TB_FILES primary key (IID);
create index TB_FILES_I_FILE_TYPE on TB_FILES (I_FILE_TYPE);

/*******************************
开发要求：

对上表实现增删改查功能。

除IID主键外，其它字段都要求在界面可编辑。
除S_REMARK外，其它字段都要求必填。
文件类型用下拉框实现。
输入文件名，文本框失去焦点后，自动识别文件后缀，文件类型下拉框自动选中对应选项。
如果文件类型不存在，要提示。
文件大小要用数值框，只允许填整数。
文件上传时间要用时间选择控件。


**/