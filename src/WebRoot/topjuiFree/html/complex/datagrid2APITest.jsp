<%@ page contentType="text/html; charset=GBK"%>
<%@ page isELIgnored="false" %>
<%@ page import="util.StringUtil" %>
<%
    String path = request.getContextPath();
%>

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

    <%@include file="/admin/common/skinCss.jsp" %>
    <script type="text/javascript" >
        var path = "<%=path%>";
    </script>
</head>

<body>
<table id="productDg"></table>

<!-- ��񹤾�����ʼ -->
<div id="productDg-toolbar" class="topjui-toolbar"
     data-options="grid:{
           type:'datagrid',
           id:'productDg'
       }">
    <a id="add" href="javascript:void(0)">����</a>
    <a id="edit" href="javascript:void(0)">�༭</a>
    <a id="batchUpdate" href="javascript:void(0)">��������</a>
    <a id="delete" href="javascript:void(0)">ɾ��</a>
    <a id="filter" href="javascript:void(0)">����</a>
    <a id="search" href="javascript:void(0)">��ѯ</a>
    <a id="import" href="javascript:void(0)">����</a>
    <a id="export" href="javascript:void(0)">����</a>
    <a id="openTab" href="javascript:void(0)">��ǩҳ</a>
    <a id="openWindow" href="javascript:void(0)">�´���</a>
    <a id="request" href="javascript:void(0)">��ͨ����</a>
    <a id="myFun" href="javascript:void(0)">�Զ��巽��</a>
    <a href="javascript:void(0)"
       data-toggle="topjui-menubutton"
       data-options="menu:'#exportSubMenu',
       btnCls:'topjui-btn-blue',
       hasDownArrow:true,
       iconCls:'fa fa-list'">����</a>
    <div id="exportSubMenu" class="topjui-toolbar"
         data-options="grid:{
           type:'datagrid',
           id:'productDg'
       }" style="width:150px;">
        <div data-toggle="topjui-menubutton"
             data-options="method:'request',
             iconCls:'fa fa-file-pdf-o',
             url:_ctx + '/json/response/success.json?uuid={uuid}'">���� PDF ����
        </div>
        <div data-toggle="topjui-menubutton"
             data-options="method:'export',
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
        <a id="queryBtn" href="javascript:void(0)">��ѯ</a>
    </form>
</div>
<!-- ��񹤾������� -->

<!-- ����б༭���� -->
<form id="editDialog"></form>

<script>
    function progressFormatter(value, rowData, rowIndex) {
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
        var $editDialog = $('#editDialog');
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
//                $.getJSON(_ctx + '/json/product/detail.json?uuid=' + uuid, function (data) {
//                    $editDialog.form('load', data);
//                });

                $.getJSON(path + '/demo/demo!getEmployeeList.action' , function (data) {
                    $editDialog.form('load', data);
                });
//                path + "/demo/demo!getEmployeeList.action"
            }
        });
    }

    function deleteRow(uuid) {
        $.iMessager.alert('������ʾ', '����ݾ��������д���룬��ajaxɾ����������ʧ����ʾ������ɹ���ʾ������ɹ���ˢ�±��ȣ�', 'messager-info');
    }

    $(function () {
        var productDg = {
            type: 'datagrid',
            id: 'productDg'
        };

        $("#productDg").iDatagrid({
            url: path + '/demo/demo!getEmployeeList.action',
            columns: [[
                {field: 'uuid', title: 'UUID', checkbox: true},
                {field: 'cEmployeeName', title: '��Ʒ����', sortable: true},
                {field: 'sSex', title: '��Ʒ���', sortable: true},
                {field: 'spec', title: '����ͺ�', sortable: true},
                {field: 'sale_price', title: '���۵���', sortable: true},
                {field: 'rate', title: '�����', sortable: true, formatter: progressFormatter},
                {field: 'operate', title: '����', sortable: true, formatter: operateFormatter, width:100}
            ]],
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
            }]
        });

        $("#add").iMenubutton({
            method: 'openDialog',
            extend: '#productDg-toolbar',
            iconCls: 'fa fa-plus',
            dialog: {
                id: 'userAddDialog',
                title: '��ѡ����ֵı�',
                href: _ctx + '/html/complex/dialog_add.html',
                buttonsGroup: [
                    {
                        text: '����',
                        url: _ctx + '/json/response/success.json',
                        iconCls: 'fa fa-plus',
                        handler: 'ajaxForm',
                        btnCls: 'topjui-btn-brown'
                    }
                ]
            }
        });

        $("#edit").iMenubutton({
            method: 'openDialog',
            extend: '#productDg-toolbar',
            iconCls: 'fa fa-pencil',
            btnCls: 'topjui-btn-green',
            grid: productDg,
            dialog: {
                title: '��ͨ���ֵı�',
                href: _ctx + '/html/complex/dialog_edit.html?uuid={uuid}',
                url: _ctx + '/json/product/detail.json?uuid={uuid}',
                buttonsGroup: [
                    {
                        text: '����',
                        url: _ctx + '/json/response/success.json',
                        iconCls: 'fa fa-save',
                        handler: 'ajaxForm',
                        btnCls: 'topjui-btn-green'
                    }
                ]
            }
        });

        $("#batchUpdate").iMenubutton({
            method: 'openDialog',
            extend: '#productDg-toolbar',
            iconCls: 'fa fa-cog',
            btnCls: 'topjui-btn-red',
            grid: {
                type: 'datagrid',
                id: 'productDg',
                param: 'uuid:uuid,code',
                uncheckedMsg: '����ѡ����Ҫ��������������'
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
            }
        });

        $("#delete").iMenubutton({
            method: 'doAjax',
            extend: '#productDg-toolbar',
            iconCls: 'fa fa-trash',
            btnCls: 'topjui-btn-brown',
            confirmMsg: '����ǹ�ѡ��ѡ��ʵ�ֶ������ݵ�Ajaxɾ���ύ�������ύgrid.param��ָ���Ĳ���ֵ',
            grid: {
                type: 'datagrid',
                id: 'productDg',
                uncheckedMsg: '���ȹ�ѡҪɾ��������',
                param: 'uuid:uuid,code:code'
            },
            url: _ctx + '/json/response/success.json'
        });

        $("#filter").iMenubutton({
            method: 'filter',
            extend: '#productDg-toolbar',
            btnCls: 'topjui-btn-black',
            grid: productDg
        });

        $("#search").iMenubutton({
            method: 'search',
            extend: '#productDg-toolbar',
            btnCls: 'topjui-btn-blue',
            grid: productDg
        });

        $("#import").iMenubutton({
            method: 'import',
            extend: '#productDg-toolbar',
            btnCls: 'topjui-btn-orange',
            uploadUrl: _ctx + '/json/response/upload.json',
            url: _ctx + '/json/response/success.json'
        });

        $("#export").iMenubutton({
            method: 'export',
            extend: '#productDg-toolbar',
            btnCls: 'topjui-btn-red',
            url: _ctx + '/json/response/export.html'
        });

        $("#openTab").iMenubutton({
            method: 'openTab',
            btnCls: 'topjui-btn-purple',
            tab: {
                title: '�����µ�Tab����',
                href: _ctx + '/html/complex/panel_add.html'
            },
            grid: productDg
        });

        $("#openWindow").iMenubutton({
            method: 'openWindow',
            extend: '#productDg-toolbar',
            btnCls: 'topjui-btn-green',
            href: 'https://www.topjui.com?uuid={uuid}',
            grid: productDg
        });

        $('#request').iMenubutton({
            method: 'request',
            btnCls: 'topjui-btn-brown',
            url: _ctx + '/json/response/success.json'
        });

        $('#myFun').iMenubutton({
            btnCls: 'topjui-btn-black',
            onClick: myQuery
        });

        $('#queryBtn').iMenubutton({
            method: 'query',
            iconCls: 'fa fa-search',
            btnCls: 'topjui-btn-blue',
            form: {id: 'queryForm'},
            grid: {type: 'datagrid', 'id': 'productDg'}
        });
    });

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
<%@include file="/admin/common/commonJs.jsp" %>
<script type="text/javascript" src="<%=path%>/js/commonEnum.js"></script>
<script type="text/javascript" src="<%=path%>/demo/dbam/jquery/js/index.js"></script>
<script type="text/javascript" src="<%=path%>/calendar/WdatePicker.js"></script>

</html>