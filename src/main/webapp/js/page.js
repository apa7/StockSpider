/**
 * Created by beidd on 2017/1/4.
 */
$(function () {
    var _totalPage = $("#totalPage").val();
    var _currentPage = $("#currentPage").val();
    $("#pagination").twbsPagination({
        currentPage: _currentPage,
        totalPages: _totalPage,
        first: "首页",
        last: "未页",
        prev: '上一页',
        next: '下一页',
        startPage: 1,
        // initiateStartPageClick: false,
        visiblePages: _totalPage > 10 ? 10 : _totalPage, // 解决当totalPages小于visiblePages页码变负值的bug
        onPageClick: function (event, page) {
            $("#currentPage").val(page);

            //  点击页码发起ajax请求
            //点击首页ajax异步请求
            console.log($("#currentPage").val());
            // $("#queryForm").submit();
            alert(1);
        }
    });
});
