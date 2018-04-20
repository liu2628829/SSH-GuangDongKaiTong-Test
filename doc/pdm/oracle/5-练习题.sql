--�ļ���Ϣ����
create table TB_FILES
(
  IID          NUMBER(15) not null,     --id����
  S_FILE_NAME  VARCHAR2(128) not null,  --�ļ���
  I_FILE_TYPE  NUMBER(1),               --�ļ����� (ö�٣�1.doc, 2.xls, 3.ppt,4.docx, 5.xlsx, 6.pptx)
  S_FILE_PATH  VARCHAR2(256),           --�ļ�·��
  I_FILE_SIZE  NUMBER(10),              --�ļ���С��KB��
  S_UPLOAD_USER    VARCHAR2(64),        --�ļ��ϴ���
  D_UPLOAD_TIME    DATE,                --�ļ��ϴ�ʱ��
  S_REMARK     VARCHAR2(256)            --�ļ����
)
;
alter table TB_FILES add constraint PK_TB_FILES primary key (IID);
create index TB_FILES_I_FILE_TYPE on TB_FILES (I_FILE_TYPE);

/*******************************
����Ҫ��

���ϱ�ʵ����ɾ�Ĳ鹦�ܡ�

��IID�����⣬�����ֶζ�Ҫ���ڽ���ɱ༭��
��S_REMARK�⣬�����ֶζ�Ҫ����
�ļ�������������ʵ�֡�
�����ļ������ı���ʧȥ������Զ�ʶ���ļ���׺���ļ������������Զ�ѡ�ж�Ӧѡ�
����ļ����Ͳ����ڣ�Ҫ��ʾ��
�ļ���СҪ����ֵ��ֻ������������
�ļ��ϴ�ʱ��Ҫ��ʱ��ѡ��ؼ���


**/