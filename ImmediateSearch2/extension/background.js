chrome.extension.onMessage.addListener(
	function(request, sender, sendResponse) {
		sendResponse({farewell:"Background Received:"+request.greeting});
		//chrome.tabs.create({
		//	"http://www.qq.com"
		//}, function(){console.log('--------------on message function');});
	}
);
