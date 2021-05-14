/**
 * form和json互相转换
 */
(function($) {
	//序列化表单到对象
	$.fn.serializeObject = function()
	{
	   var o = {};
	   var a = $(this).serializeArray();
	   $.each(a, function() {
	       if (o[this.name]) {
	           if (!o[this.name].push) {
	               o[this.name] = [o[this.name]];
	           }
	           o[this.name].push(this.value || '');
	       } else {
	           o[this.name] = this.value || '';
	       }
	   });
	   return o;
	};
	
	$.fn.serializeParams = function()
	{
	   var o = this.serializeObject();
	   for (prp in o) {
		   if (o[prp].push) {
			   for (var i=0; i<o[prp].length; i++) {
				   o[prp + "[" + i + "]"] = o[prp][i];
			   }
			   o[prp] = undefined;
		   }
	   }
	   return o;
	};
	
	//将对象转换为表单
	$.fn.unSerializeObject = function(values) {
		var form = $(this);
		$.each(values, function(key,value) {
	        var object = form.find("*[name='" + key + "']");
	        if (object.length == 1){
	            if (object.attr("type").toLowerCase() == 'radio' || object.attr("type").toLowerCase() == 'checkbox'){
	                if (object.val() == value){
	                    object.attr("checked",true);
	                }
	                return true;
	            } else {
	                object.val(value);
	            }
	        } else if (object.length > 1){
	            object.each(function(i) {
	                if (object.attr("type").toLowerCase() == 'radio' || object.attr("type").toLowerCase() == 'checkbox'){
	                    if ($.inArray($(this).val(),value) != -1){
	                        $(this).attr("checked",true);
	                    } else {
	                        $(this).attr("checked",false);
	                    }
	                } else {
	                    $(this).val(value[i]);
	                }
	            });
	        }
	    });
	};
})(jQuery);