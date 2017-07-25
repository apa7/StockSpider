
//校验手机号
function checkMobile(value){
	return /^0{0,1}(1[3|4|5|7|8][0-9]|15[7-9]|15[0-2]|18[6-8])[0-9]{8}$/.test(value); 
}

//校验身份证号码
function checkIdCard(value){
	return /^((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65|71|81|82|91)\d{4})((((19|20)(([02468][048])|([13579][26]))0229))|((20[0-9][0-9])|(19[0-9][0-9]))((((0[1-9])|(1[0-2]))((0[1-9])|(1\d)|(2[0-8])))|((((0[1,3-9])|(1[0-2]))(29|30))|(((0[13578])|(1[02]))31))))((\d{3}(x|X))|(\d{4}))$/.test(value);
}

//检查是否为英文和数字组合
function checkIsCharAndNum(value){
	return /^[a-zA-Z0-9]{1}([a-zA-Z0-9]){0,20}$/.test(value);
}

//检查密码格式，必须为6到16个字符的英文和数字
function checkPassword(value){
	return !(/^(([A-Z]*？[a-z]*？\d*？[-_\~!@#\$%\^&\*\.\(\)\[\]\{\}<>\?\\\/\'\"]*)|.{0,5})$|\s/.test(value));
}

//检查是否为数字
function checkIsNumber(value){
	return !isNaN(value);
}

//检查是否为整数
function checkIsInt(value){
	return /^[\-|0-9]*[0-9]*$/.test(value);
}

//检查是否为日期
function checkIsDate(value){
	return /^(?!0000)[0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)$/.test(value);
}

//检查是否为日期时间
function checkIsDatetime(value){
	return /^(?!0000)[0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)([ ])([0-1][0-9]|2[0-3]):([012345][0-9]):([012345][0-9])$/.test(value);
}

function validateForm(formId){
	var flag = true;
	$("div .help-block").empty();
	$("div").removeClass("has-error");

	$("#"+formId).find(".required").each(function(){
		var tagName = $(this)[0].tagName;
		var validType = $(this).attr("validType");
		if(validType != undefined && validType != ''){
			validType = validType.toLowerCase();
		}
		
		if($(this).val().trim() == ""){
			$(this).parent().parent().addClass("has-error");
			if(tagName == "SELECT"){
				$(this).parent().find("div.help-block").empty().append("请选择"+$(this).attr("text")+"！");
			}else{
				$(this).parent().find("div.help-block").empty().append($(this).attr("text")+"不可为空！");				
			}
			flag = false;
			if(validType != "date" && validType != "datetime"){
				$(this).focus();
			}
		}else if(tagName == "INPUT"){
			var inputFlag = true;
			var text = "格式不正确！";
			if(validType == "mobile"){
				inputFlag = checkMobile($(this).val());
			}else if(validType == "en"){
				inputFlag = checkIsCharAndNum($(this).val());
				text = "必须为英文字母和数字";
			}else if(validType == "idcard"){
				inputFlag = checkIdCard($(this).val());
			}else if(validType == "number"){
				inputFlag = checkIsNumber($(this).val());
				text = "必须为数字";
			}else if(validType == "password"){
				inputFlag = checkPassword($(this).val());
				text += "密码由字母、数字和特殊字符组成，长度在6～16字符之间";
			}else if(validType == "int"){
				inputFlag = checkIsInt($(this).val());
				text = "必须为整数";
			}else if(validType == "date"){
				inputFlag = checkIsDate($(this).val());
			}else if(validType == 'datetime'){
				inputFlag = checkIsDatetime($(this).val());
			}
			if(inputFlag == false){
				$(this).parent().parent().addClass("has-error");
	 			$(this).parent().find("div.help-block").empty().append($(this).attr("text")+text);
				flag = false;
				if(validType != "date" && validType != "datetime"){
					$(this).focus();
				}
			}
		}
	});
	return flag;
}

//动态校验
$(function(){
	var ie = !!window.ActiveXObject || "ActiveXObject" in window;
	//alert(ie);
	//获取页面所有的form
	$("form").each(function(){
		//获取该form下需要校验的字段
		$(this).find(".required").each(function(){
			if(ie){
				$(this).bind("change propertychange",function(e){
					validateObj($(this));
				});
			}else{
				$(this).bind("input",function(e){
					validateObj($(this));
				});
			}			
		});
	});
});

function validateObj(obj){
	$(obj).parent().parent().removeClass("has-error");
	$(obj).parent().find("div.help-block").empty();
	var tagName = $(obj)[0].tagName;				
	if($(obj).val().trim() == ""){
		$(obj).parent().parent().addClass("has-error");
		if(tagName == "SELECT"){
			$(obj).parent().find("div.help-block").empty().append("请选择"+$(obj).attr("text")+"！");
		}else{
			$(obj).parent().find("div.help-block").empty().append($(obj).attr("text")+"不可为空！");				
		}
		$(obj).focus();
	}else if(tagName == "INPUT"){
		var validType = $(obj).attr("validType");
		if(validType != undefined && validType != ''){
			validType = validType.toLowerCase();
		}
		var inputFlag = true;
		var text = "格式不正确！";
		if(validType == "mobile"){
			inputFlag = checkMobile($(obj).val());
		}else if(validType == "en"){
			inputFlag = checkIsCharAndNum($(obj).val());
			text = "必须为英文字母和数字";
		}else if(validType == "idcard"){
			inputFlag = checkIdCard($(obj).val());
		}else if(validType == "number"){
			inputFlag = checkIsNumber($(obj).val());
			text = "必须为数字";
		}else if(validType == "password"){
			inputFlag = checkPassword($(obj).val());
			text += "密码由字母、数字和特殊字符组成，长度在6～16字符之间";
		}else if(validType == "int"){
			inputFlag = checkIsInt($(obj).val());
			text = "必须为整数";
		}else if(validType == "date"){
			inputFlag = checkIsDate($(this).val());
		}else if(validType == 'datetime'){
			inputFlag = checkIsDatetime($(this).val());
		}
		if(inputFlag == false){
			$(obj).parent().parent().addClass("has-error");
			$(obj).parent().find("div.help-block").empty().append($(obj).attr("text")+text);
			$(obj).focus();
		}
	}
}
