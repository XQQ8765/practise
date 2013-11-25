var notification = {
	init_: function(){
		chrome.extension.onRequest.addListener(function(request, sender, sendResponse){
			if (request.fun == "submitToServer_"){
				dataExchange.submitToServer_(request.data, sendResponse);
			}else{
				sendResponse({status: 'cannot process'});
			}
		});
	},
	
	sendNotification_: function(data){
		var notification = window.webkitNotifications.createNotification(chrome.extension.getURL('icon.png'), data.title, data.message);
		notification.show();
	}
}
$(function (){
	notification.init_();
});