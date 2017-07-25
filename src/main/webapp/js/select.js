(function($) {
	
 $.select = {};
 
 /** 构造下拉框
 {
 	url				: "url", 返回数据为json格式的url
 	valueField		: "value", json数据中的value字段
 	textField		: "text",  json数据中的文本字段
 	selectId		: "id" ,  select下拉框的ID
 	defaultValue	: 默认值
 }
 callbackEvent	: 调用一个回调函数 
 **/
 $.select.init = function(data,callbackEvent){
	var url 			= data.url;
	var textField 		= data.textField;
	var valueField 		= data.valueField;
	var selectId		= data.selectId;
	var defaultValue	= data.defaultValue;
	
	var select = $("#"+selectId);
	select.children("option").each(function(){
		if($(this).attr("value") != "")
			$(this).remove();
	});
	
	if(url != null){
		$.post(
				url,
				function(data)
				{
					for(var i=0;i<data.length;i++)
					{
						var row = data[i];
						if((defaultValue || defaultValue == 0) && row[valueField] == defaultValue)
							$("<option value="+row[valueField]+" selected>"+row[textField]+"</option>").appendTo("#"+selectId);
						else	
							$("<option value="+row[valueField]+">"+row[textField]+"</option>").appendTo("#"+selectId); 
					} 
		       		
					if(callbackEvent != null)
						callbackEvent();
		         },
			    "json"
		);
	}
 };

})(jQuery);
