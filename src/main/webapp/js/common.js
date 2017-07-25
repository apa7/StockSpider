(function($) {
	// 备份jquery的ajax方法
	var _ajax = $.ajax;
	// 重写jquery的ajax方法
	$.ajax = function(opt) {
		// 备份opt中error和success方法
		var fn = {
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			},
			success : function(data, textStatus) {
			},
			beforeSend : function(){
				
			},
			complete : function(){
				
			}
		}
		if (opt.error) {
			fn.error = opt.error;
		}
		if (opt.success) {
			fn.success = opt.success;
		}
		
		// 扩展增强处理
		var _opt = $
				.extend(
						opt,
						{
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
									if(XMLHttpRequest.responseText.indexOf("权限") > -1){
										window.location.href = '/system/error.htm?error=你没有此功能权限！';
									}else{
										window.location.href = '/system/error.htm?error=后台出现错误，请联系管理员！';
									}
							},
							success : function(data, textStatus) {                             
								if (typeof (data) == "object"&&data!=null) {
									if ('systemErrorCode' in data) { // 系统错误处理
										if(data.systemErrorCode=="40008") {//session 过期
											//alert('登录超时，请重新登录！');
											if(parent.window!=undefined){
											    parent.window.location.href = '/system/login.htm';
											}else{
												window.location.href='/system/login.htm';
											}
										} else{ //其他系统错误	
											window.location.href = '/system/error.htm?error='
													+ data.systemErrorMsg+"&errorCode="+data.systemErrorCode;
										}
									} else {
										fn.success(data, textStatus);
									}
								} else {
									fn.success(data, textStatus);
								}
							},
							beforeSend: function(){
								try{
									//异步查数据不加载进度条
									var name = opt.url.split("/");
									var value = name[name.length-1];
									var showFlag = true;
									if(value.length >= 3 && value.substring(0,3) == 'get'){
										showFlag = false;
									}
									if(value.length >= 5 && value.substring(0,5) == 'query'){
										showFlag = false;
									}
									if(showFlag){
										// showProgress();
									}
								}catch(e){}
								fn.beforeSend();
							},
							complete: function () {
								try{
									//查数据字典不加载进度条
									if(opt.url.indexOf('getDictionarys') == -1){
										hideProgress();
									}
								}catch(e){}
								fn.complete();
							}
						});
		_ajax(_opt);
	};
})(jQuery);

/*************************** 页面进度条效果 BEGIN **********************************************/
//显示进度条,scale表示每次循环加载进度条的百分比例,值越大，进度条停留越久
function showProgress(scale){
	//如果进度条已经在运行中，返回
	if(!$("#progressedDiv_shade").hasClass("hide")){
		return;
	}
	//计算top和left值，使进度条居中
	var top = $(window).height()/2+$(document).scrollTop();
	var left = $(window).width()/2+$(document).scrollLeft();
	

	var obj = $("#progressedDiv_success");
	var childObj = $(obj).find(".progress-bar");
	//初始化进度为0
	$(childObj).css("width","0px").attr("aria-valuenow",0);
	$(childObj).find("span").find("span").empty().append("0");
	
	$("#progressedDiv_shade").removeClass('hide');
	$(obj).css({"top":top,"left":left}).removeClass('hide');
	
	//计算每次设置进度条的比例值
	if(scale == undefined || scale == ''){
		scale = 5;
	}
	var divWidth = $(obj).width();
	var stepUp = divWidth/scale;//递增宽度
	
	setTimeout(function(){setProgress(childObj,divWidth,stepUp)},100);
}
//动态设置进度条
function setProgress(childObj,divWidth,stepUp){
	var childWidth = $(childObj).width();
	
	//如果进度为100，则不再执行定时器
	if($(childObj).find("span").find("span").text() == '100'){
		return;
	}
	
	if((childWidth+stepUp) >= divWidth){
		$(childObj).css("width","99%");
		$(childObj).find("span").find("span").empty().append("99");
	}else{
		var percent = parseInt((childWidth+stepUp)*100/divWidth);//百分比
		$(childObj).width(childWidth+stepUp);
		$(childObj).find("span").find("span").empty().append(percent);
		setTimeout(function(){setProgress(childObj,divWidth,stepUp)},100);
	}
}
//隐藏进度条
function hideProgress(){
	//设置成100%
	var obj = $("#progressedDiv_success");
	var childObj = $(obj).find(".progress-bar");
	$(childObj).css("width","100%");
	$(childObj).find("span").find("span").empty().append("100");
	
	//延迟500毫秒隐藏
	setTimeout(function(){
		$("#progressedDiv_shade").addClass("hide");
		$("#progressedDiv_success").addClass("hide");
	},1);
}
/*************************** 页面进度条效果 END **********************************************/

$(function(){
	// //加载日期时间控件
    // $(".date-timepicker").datetimepicker({
	// 	format:'YYYY-MM-DD HH:mm:ss',
	// 	//无效 language: 'zh-cn',
	// 	autoclose: true,
	// 	todayHighlight: true
	// });
    //
    // //加载日期控件
	// $('.date-picker').datepicker({
	// 	language: 'zh-CN',
	// 	autoclose: true,
	// 	todayBtn: 'linked',
	// 	//clearBtn: true,和todayBtn不在一行，比较难看
	// 	keyboardNavigation: true,//允许方向键改变日期
	// 	todayHighlight: true
	// });
	//
	//在指定对象上下左右弹出一个层
	$("[data-toggle='popover']").popover({
		container: 'body',
		//content:getPopoverContent(),
		html:true,
		placement: 'bottom' //'top/left/bottom/right'
	});
	
	//监听列表页面的th复选框点击事件，对tr中的复选框进行全选或取消全选
	$("table thead th input:checkbox").click(function(e){
		var that = this;
		$('table tbody tr input:checkbox').each(function(){
			$(this).attr("checked", that.checked);
			$(this).closest('tr').toggleClass('selected');
		});
	});
	//监听列表页面的tr点击事件，如果有复选框则自动选中或取消选中
	$("table tbody tr").click(function(e){
		var obj = $(this).find('input[type=checkbox]');
		if(obj.length > 0){
			if(obj.is(':checked')){
				obj.attr('checked',false);
			}else{
				obj.attr('checked',true);
			}
		}
	});
});

//格式化日期
function formatDate(date,separator){
	if(separator == undefined || separator == ''){
		separator = '-';
	}
	var year = date.getFullYear();
	var month = date.getMonth()+1;
	var day = date.getDate();
	if(month < 10){
		month = '0' + month;
	}
	if(day < 10){
		day = '0' + day;
	}
	return year + separator + month + separator + day;
}
//格式化时间
function formatDatetime(date,separator){
	if(separator == undefined || separator == ''){
		separator = '-';
	}
	var year = date.getFullYear();
	var month = date.getMonth()+1;
	var day = date.getDate();
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	if(month < 10){
		month = '0' + month;
	}
	if(day < 10){
		day = '0' + day;
	}
	if(hours < 10){
		hours = '0' + hours;
	}
	if(minutes < 10){
		minutes = '0' + minutes;
	}
	if(seconds < 10){
		seconds = '0' + seconds;
	}
	return year + separator + month + separator + day + ' ' + hours + ":" + minutes + ":" +seconds;
}
