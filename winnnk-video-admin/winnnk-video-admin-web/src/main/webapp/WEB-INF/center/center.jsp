<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>winnnk短视频后台管理系统</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1" name="viewport"/>

    <jsp:include page="common/commonHeaderCSS.jsp"></jsp:include>

    <style>
        .ui-jqgrid tr.jqgrow td {
            vertical-align: middle;
            white-space: normal !important;
            height: auto;
            word-break: break-all;
        }
    </style>
</head>

<body class="page-header-fixed page-sidebar-closed-hide-logo page-content-white">
<div class="page-wrapper">
    <jsp:include page="common/header.jsp"></jsp:include>
    <div class="clearfix"></div>
    <div class="page-container">
        <jsp:include page="common/menu.jsp"></jsp:include>
        <div class="page-content-wrapper">
            <div class="page-content">
                <div class="page-content-body">
                    <jsp:include page="imooc.jsp"></jsp:include>

                </div>
            </div>
        </div>

    </div>
</div>

<jsp:include page="common/footer.jsp"></jsp:include>

<jsp:include page="common/commonFooterJS.jsp"></jsp:include>

</body>
</html>
