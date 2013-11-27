function getSelectedText() {
	if (window.getSelection()) {//Chrome, Fixfox, Standard
		return window.getSelection().toString();
	} else {//IE
		return document.selection.createRange();
	}
}
function doSearch(searchContent) {
	var searchUrl='http://www.google.com.hk/#newwindow=1&q='+searchContent+'&safe=strict';
	alert('--searchUrl='+searchUrl);
	//alert('chrome create tab: chrome.tabs:'+chrome.tabs);
	console.log('--------------send message searchUrl:'+searchUrl);
	chrome.extension.sendMessage({greeting: "http://www.apple.com/"}, function(response){
		console.log(response.farewell);
	});
}
function performWhenKeyDown() {
	//alert('alt+s');
	//var selectedText = document.selection.createRange();
	var selectedText = getSelectedText();
	//alert("selectedText:"+selectedText);
	doSearch(selectedText);
	return false;
}
function domo(){
	$(document).bind('keydown', 'Alt+s',function (evt){ return performWhenKeyDown();});
}
$(function (){
	$(document).ready(domo);
});
