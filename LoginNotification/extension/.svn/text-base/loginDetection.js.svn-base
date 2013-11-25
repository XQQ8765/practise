var loginDetection = {
	init_: function(){
		var div = $("<div>",{id: 'injectDiv'}).appendTo("body");
		$(div).append("<img src="+chrome.extension.getURL('icon.png')+"> This page is monitored!");
		//add form listener
		document.forms[0].addEventListener('submit', function(){
			loginDetection.formSubmitted_();
			return true; 
		});
	},
	
	formSubmitted_: function(){
		var username = $(document.forms[0]).find(':input[type=text]')[0].value;
		chrome.extension.sendRequest({'fun': 'submitToServer_', 'data': username}, function(responseData){
			console.log('submit data to server, status:'+responseData.status)
		});
		console.log('Form Submitted!');
	}
}
$(function (){
	loginDetection.init_();
});