<%--<%@ page contentType="text/html; charset=GBK"%>--%>
<%--<%@ page isELIgnored="false" %>--%>
<%--<%@ page import="util.StringUtil" %>--%>
<%--<%--%>
    <%--String path = request.getContextPath();--%>
<%--%>--%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <!-- ����IEʹ�ü���ģʽ -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
    <meta name="renderer" content="webkit">
    <!-- TopJUI�����ʽ -->
    <link type="text/css" href="../../topjui/css/topjui.core.min.css" rel="stylesheet">
    <link type="text/css" href="../../topjui/themes/default/topjui.blue.css" rel="stylesheet" id="dynamicTheme"/>
    <!-- FontAwesome����ͼ�� -->
    <link type="text/css" href="../../static/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet"/>
    <!-- layui�����ʽ -->
    <link type="text/css" href="../../static/plugins/layui/css/layui.css" rel="stylesheet"/>
    <!-- jQuery������� -->
    <script type="text/javascript" src="../../static/plugins/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../../static/plugins/jquery/jquery.cookie.js"></script>
    <!-- TopJUI������� -->
    <script type="text/javascript" src="../../static/public/js/topjui.config.js"></script>
    <!-- TopJUI��ܺ���-->
    <script type="text/javascript" src="../../topjui/js/topjui.core.min.js"></script>
    <!-- TopJUI����֧�� -->
    <script type="text/javascript" src="../../topjui/js/locale/topjui.lang.zh_CN.js"></script>
    <!-- layui���js -->
    <script type="text/javascript" src="../../static/plugins/layui/layui.js" charset="utf-8"></script>

    <%--<%@include file="/admin/common/skinCss.jsp" %>--%>
    <%--<script type="text/javascript" >--%>
        <%--var path = "<%=path%>";--%>
    <%--</script>--%>
</head>

<body>
<!-- layout���� ��ʼ -->

<div data-toggle="topjui-layout" data-options="fit:true">
    <div data-options="region:'center',title:'',fit:true,split:true,border:false">
        <!-- datagrid��� -->
        <table data-toggle="topjui-datagrid"
               data-options="id: 'productDg',
               fitColumns:true,

                <!--url: '../../json/product/list.json',-->
                url: 'http://localhost:8080/demo/demo!getEmployeeList.action',
                filter: [{
                    field: 'cEmployeeName',
                    type: 'textbox',
                    op: ['contains', 'equal', 'notequal', 'less', 'greater']
                }, {
                    field: 'sSex',
                    type: 'combobox',
                    options: {
                        valueField: 'label',
                        textField: 'value',
                        data: [{
                            label: '2103',
                            value: '2103'
                        }, {
                            label: '5103',
                            value: '5103'
                        }, {
                            label: '1204',
                            value: '1204'
                        }]
                    },
                    op: ['contains', 'equal', 'notequal', 'less', 'greater']
                }, {
                    field: 'spec',
                    type: 'combobox',
                    options: {
                        valueField: 'label',
                        textField: 'value',
                        multiple: true,
                        data: [{
                            label: 'KC-W200SW',
                            value: 'KC-W200SW'
                        }, {
                            label: '��ɫLR-1688BY-2',
                            value: '��ɫLR-1688BY-2'
                        }, {
                            label: '����ɫBCD-339WBA 339',
                            value: '����ɫBCD-339WBA 339'
                        }]
                    },
                    op: ['contains', 'equal', 'notequal', 'less', 'greater']
                }]">
            <thead>
            <tr>
                <th data-options="field:'uuid',title:'UUID',checkbox:true"></th>
                <th data-options="field:'cEmployeeName',title:'��Ա����',sortable:true,width:100"></th>
                <th data-options="field:'sSex',title:'��Ա�Ա�',sortable:true,width:50"></th>
                <th data-options="field:'spec',title:'����ͺ�',sortable:true,width:100"></th>
                <th data-options="field:'sale_price',title:'���۵���',sortable:true,width:50"></th>
                <th data-options="field:'rate',title:'�����',sortable:true,formatter:progressFormatter"></th>
                <th data-options="field:'operate',title:'����',sortable:true,formatter:operateFormatter,width:80"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>
<!-- layout���� ���� -->

<!-- ��񹤾�����ʼ -->
<div id="productDg-toolbar" class="topjui-toolbar"
     data-options="grid:{
           type:'datagrid',
           id:'productDg'
       }">
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method:'openDialog',
       extend: '#productDg-toolbar',
       iconCls: 'fa fa-plus',
       dialog:{
           id:'userAddDialog',
           title:'��ѡ����ֵı�',
           href:_ctx + '/html/complex/dialog_add.html',
           buttonsGroup:[
               {text:'����',url:_ctx + '/json/response/success.json',iconCls:'fa fa-plus',handler:'ajaxForm',btnCls:'topjui-btn-green'}
           ]
       }">����</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method: 'openDialog',
            extend: '#productDg-toolbar',
            iconCls: 'fa fa-pencil',
            btnCls: 'topjui-btn-green',
            grid: {
                type: 'datagrid',
                id: 'productDg'
            },
            dialog: {
                title:'��ͨ���ֵı�',
                href: _ctx + '/html/complex/dialog_edit.html?uuid={uuid}',
                url: _ctx + '/json/product/detail.json?uuid={uuid}',
                buttonsGroup: [
                    {
                        text: '�Զ��巽��',
                        url: _ctx + '/json/response/success.json',
                        iconCls: 'fa fa-cog',
                        handler: myQuery,
                        btnCls: 'topjui-btn-brown'
                    },
                    {
                        text: '����',
                        url: _ctx + '/json/response/save2.json',
                        iconCls: 'fa fa-save',
                        handler: 'ajaxForm',
                        btnCls: 'topjui-btn-green'
                    }
                ]
            }">�༭</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method: 'openDialog',
       extend: '#productDg-toolbar',
       iconCls: 'fa fa-cog',
       btnCls: 'topjui-btn-red',
       grid: {
           type: 'datagrid',
           id: 'productDg',
           param:'uuid2:uuid,code',
           uncheckedMsg:'����ѡ����Ҫ��������������'
       },
       dialog: {
           title: '������дҪ�������µ�����',
           href: _ctx + '/html/complex/dialog_add.html',
           buttonsGroup: [
               {
                   text: '�ύ',
                   url: _ctx + '/json/response/success.json',
                   iconCls: 'fa fa-cog',
                   handler: 'multiAjaxForm',
                   btnCls: 'topjui-btn-green'
               }
           ]
       }">��������</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method:'doAjax',
       extend: '#productDg-toolbar',
       btnCls:'topjui-btn-brown',
       iconCls:'fa fa-trash',
       confirmMsg:'����ǹ�ѡ��ѡ��ʵ�ֶ������ݵ�Ajaxɾ���ύ�������ύgrid.param��ָ���Ĳ���ֵ',
       grid: {uncheckedMsg:'���ȹ�ѡҪɾ��������',param:'uuid:uuid,code:code',updateRow:true},
       url:_ctx + '/json/response/success.json'">ɾ��</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method:'filter',
       extend: '#productDg-toolbar',
       btnCls:'topjui-btn-black'">����</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method:'search',
       extend: '#productDg-toolbar',
       btnCls:'topjui-btn-blue'">��ѯ</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method:'import',
       extend: '#productDg-toolbar',
       grid:{
           type:'datagrid',
           id:'productDg'
       },
       iconCls:'fa fa-cloud-upload',
       btnCls:'topjui-btn-orange',
       uploadUrl:_ctx + '/json/response/upload.json',
       url:_ctx+'/json/response/success.json'">����</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method:'export',
       extend: '#productDg-toolbar',
       iconCls:'fa fa-cloud-download',
       btnCls:'topjui-btn-red',
       url:_ctx + '/json/response/export.html'">����</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method:'openTab',
       extend: '#productDg-toolbar',
       btnCls:'topjui-btn-purple',
       tab:{
           title:'Panel+դ�񲼾ֵı�',
           href:_ctx + '/html/complex/panel_add.html?uuid={uuid}'
       }">��ǩҳ</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method:'openWindow',
       extend: '#productDg-toolbar',
       btnCls:'topjui-btn-green',
       href:'https://www.topjui.com?uuid={uuid}'">�´���</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="method:'request',
       btnCls:'topjui-btn-brown',
       url:_ctx + '/json/response/success.json?uuid={uuid}&code={code}'">��ͨ����</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="btnCls:'topjui-btn-black',
       onClick:myQuery">�Զ��巽��</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="menu:'#exportSubMenu',
       btnCls:'topjui-btn-blue',
       hasDownArrow:true,
       iconCls:'fa fa-list'">����</a>
    <div id="exportSubMenu" class="topjui-toolbar" style="width:150px;">
        <div data-toggle="topjui-menubutton"
             data-options="method:'request',
             iconCls:'fa fa-file-pdf-o',
             url:_ctx + '/json/response/success.json?uuid={uuid}'">���� PDF ����
        </div>
        <div data-toggle="topjui-menubutton"
             data-options="method:'export',
             extend: '#productDg-toolbar',
             iconCls:'fa fa-file-excel-o',
             url: _ctx + '/json/response/export.html'">����EXCEL�б�
        </div>
        <div data-toggle="topjui-menubutton"
             data-options="method:'request',
             iconCls:'fa fa-file-excel-o',
             url:_ctx + '/json/response/success.json?uuid={uuid}'">����EXCEL����
        </div>
        <div data-toggle="topjui-menubutton"
             data-options="method:'request',
             iconCls:'fa fa-file-word-o',
             url:_ctx + '/json/response/success.json?uuid={uuid}'">����WORD����
        </div>
    </div>
    <form id="queryForm" class="search-box">
        <input type="text" name="name" data-toggle="topjui-textbox"
               data-options="id:'name',prompt:'��Ʒ����',width:100">
        <input type="text" name="code" data-toggle="topjui-textbox"
               data-options="id:'code',prompt:'��Ʒ�ͺ�',width:100">
        <a href="javascript:void(0)"
           data-toggle="topjui-menubutton"
           data-options="method:'query',
           iconCls:'fa fa-search',
           btnCls:'topjui-btn-blue',
           form:{id:'queryForm'},
           grid:{type:'datagrid','id':'productDg'}">��ѯ</a>
    </form>
</div>
<!-- ��񹤾������� -->

<script>
    function saveSuccess(data) {
        console.log(data);
    }

    function deleteSuccess(data) {
        console.log(data);
    }

    function progressFormatter(value, row, index) {
        var htmlstr = '<div id="p" class="topjui-progressbar progressbar" data-options="value:' + value + '" style="width: 398px; height: 26px;">';
        htmlstr += '<div class="progressbar-text" style="width: 398px; height: 26px; line-height: 26px;">' + value + '%</div>';
        htmlstr += '<div class="progressbar-value" style="width: ' + value + '%; height: 26px; line-height: 26px;">';
        htmlstr += '<div class="progressbar-text" style="width: 398px; height: 26px; line-height: 26px;">' + value + '%</div>';
        htmlstr += '</div>';
        htmlstr += '</div>';
        return htmlstr;
    }

    function operateFormatter(value, row, index) {
        var htmlstr = '<button class="layui-btn layui-btn-xs" onclick="openEditDiag(\'' + row.uuid + '\')">�༭</button>';
        htmlstr += '<button class="layui-btn layui-btn-xs layui-btn-danger" onclick="deleteRow(\'' + row.uuid + '\')">ɾ��</button>';
        return htmlstr;
    }

    function openEditDiag(uuid) {
        var $editDialog = $('<form></form>');
        $editDialog.iDialog({
            title: '�༭����',
            width: 950,
            height: 500,
            closed: false,
            cache: false,
            href: _ctx + '/html/complex/dialog_edit.html?uuid=' + uuid,
            modal: true,
            buttons: [{
                text: '����',
                iconCls: 'fa fa-save',
                btnCls: 'topjui-btn-blue',
                handler: function () {
                    // ��ʾ��Ϣ
                    $.iMessager.alert('������ʾ', '����ݾ��������д���룬��ajax������������ʧ����ʾ������ɹ���ʾ������ɹ���رմ��ڲ�ˢ�±��ȣ�', 'messager-info');
                }
            }, {
                text: '�ر�',
                iconCls: 'fa fa-close',
                btnCls: 'topjui-btn-red',
                handler: function () {
                    $editDialog.iDialog('close');
                }
            }],
            onLoad: function () {
                //���ر�����
                $.getJSON(_ctx + '/json/product/detail.json?uuid=' + uuid, function (data) {
                    $editDialog.form('load', data);
                });
            }
        });
    }

    function deleteRow(uuid) {
        $.iMessager.alert('������ʾ', '����ݾ��������д���룬��ajaxɾ����������ʧ����ʾ������ɹ���ʾ������ɹ���ˢ�±��ȣ�', 'messager-info');
    }

    // �Զ��巽��
    function myQuery() {
        // ��ʾ��Ϣ
        $.iMessager.alert('�Զ��巽��', '�Զ��巽����ִ���ˣ�', 'messager-info');

        var checkedRows = $('#productDg').iDatagrid('getChecked');
        console.log(checkedRows);

        var selectedRow = $('#productDg').iDatagrid('getSelected');
        console.log(selectedRow);

        // �ύ������ѯ�������
        $('#productDg').iDatagrid('reload', {
            name: $('#name').iTextbox('getValue'),
            code: $('#code').iTextbox('getValue')
        });
    }
</script>
</body>

<!-- �����ڿؼ�js��������룬�������head�ﵼ�룬��ʱ�����򲻿� -->
<%--<%@include file="/admin/common/commonJs.jsp" %>--%>
<%--<script type="text/javascript" src="<%=path%>/js/commonEnum.js"></script>--%>
<%--<script type="text/javascript" src="<%=path%>/demo/dbam/jquery/js/index.js"></script>--%>
<%--<script type="text/javascript" src="<%=path%>/calendar/WdatePicker.js"></script>--%>

</html>