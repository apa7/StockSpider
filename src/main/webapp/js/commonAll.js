//禁用jQuery对数组参数做的序列化操作,对数组参数名带上[]
jQuery.ajaxSettings.traditional = true;
/** table鼠标悬停换色* */
$(function () {
    // 如果鼠标移到行上时，执行函数
    $(".table tr").mouseover(function () {
        $(this).css({
            background: "#CDDAEB"
        });
        $(this).children('td').each(function (index, ele) {
            $(ele).css({
                color: "#1D1E21"
            });
        });
    }).mouseout(function () {
        $(this).css({
            background: "#FFF"
        });
        $(this).children('td').each(function (index, ele) {
            $(ele).css({
                color: "#909090"
            });
        });
    });
});

/** 翻页操作 */
$(function () {
    // 翻页操作
    $(".btn_page").click(
        function () {
            // 获取data-page属性的值
            var pageNo = $(this).data("page")
                || $(":input[name='qo.currentPage']").val();
            $(":input[name='qo.currentPage']").val(pageNo);
            $("#searchForm").submit();
        });
    // 设置每页显示多少条数据:改变pageSize
    $(":input[name='qo.pageSize']").change(function () {
        $(":input[name='qo.currentPage']").val(1);
        $("#searchForm").submit();
    });

    $(".ui_select02").change(function () {
        $("#searchForm").submit();
    });
    $(".ui_select01").change(function () {
        $("#searchForm").submit();
    });
});

/** 点击,跳转到指定URL */
$(function () {
    $(".btn_redirect").click(function () {
        window.location.href = $(this).data("url");
    });
});

/** 批量删除操作 */
$(function () {
    // 全选/全不选:复选框
    $("#all").click(function () {
        $(":input[name=IDCheck]").prop("checked", $(this).prop("checked"));
    });
    // 批量删除
    $(".btn_batchDelete").click(function () {
        // 存储需要被删除的id
        // var ids=[];
        // 获取被选中的复选框的eid值
        /*$.each($(":input[name=IDCheck]:checked"), function (index, item) {
         ids[index] = $(item).data("eid");
         });*/
        var ids = $.map($(":input[name='IDCheck']:checked"), function (item) {
            return $(item).data("eid");
        });

        if (ids.length == 0) {
            $.dialog({
                title: "温馨提示",
                content: "亲,请选择被删除的数据!",
                icon: "face-smile",
                ok: true
            });
            return;
        }

        var url = $(this).data("url");
        $.dialog({
            title: "温馨提示",
            content: "你确定要批量删除吗?",
            icon: "face-smile",
            cancel: true,
            ok: function () {
                // 发送Ajax请求
                $.post(url, {
                    "ids": ids
                }, function () {
                    window.location.reload();// 加载当前文档URL
                    // 批量删除成功的提示
                });
            }
        });
    });
});

$(function () {
    // 确定删除的对话框
    $(".btn_delete").click(function () {
        var deleteUrl = $(this).data("url");
        $.dialog({
            title: "温馨提示",
            content: "你确定要删除吗?",
            icon: "face-smile",
            cancel: true,
            ok: function () {
                var dialog = $.dialog({icon: "face-smile"});//初始化对话框
                $.get(deleteUrl, {}, function (data) {
                    dialog.title("温馨提示").content(data).button({
                        name: "确定",
                        callback: function () {
                            window.location.reload();
                        }
                    });
                });
            }
        });
    });

    // 确定审核的对话框
    $(".btn_audit").click(function () {
        var auditUrl = $(this).data("url");
        $.dialog({
            title: "温馨提示",
            content: "你确定审核吗?",
            icon: "face-smile",
            cancel: true,
            ok: function () {
                var dialog = $.dialog({icon: "face-smile"});//初始化对话框
                $.get(auditUrl, {}, function (data) {
                    dialog.title("温馨提示").content(data).button({
                        name: "确定",
                        callback: function () {
                            window.location.reload();
                        }
                    });
                });
            }
        });
    });
});


// 对话框
function showDialog(content, icon, cancel, ok) {
    $.dialog({
        title: "温馨提示",
        content: content || "",
        icon: icon || "face-smile",
        cancel: cancel || true,
        ok: ok || true
    });
}

/*
 $(function () {
 $(".Wdate").click(function () {
 WdatePicker(${
 skin:"ext",
 });
 });
 });*/
