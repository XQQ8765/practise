//Show pageAction when valud url
//function checkForValidUrl(tabId, changeInfo, tab) {
//    chrome.pageAction.show(tabId);
//  }
//};
// Listen for any changes to the URL of any tab.
//chrome.tabs.onUpdated.addListener(checkForValidUrl);

chrome.extension.onMessage.addListener(
	function(request, sender, sendResponse) {
		sendResponse({farewell:"Background Received:"+request.searchUrl});
		chrome.tabs.create({
			url: request.searchUrl
		}, function(){});
	}
);
