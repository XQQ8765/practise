var dataExchange = {
	websocket_: null,
	wsURL_: "ws://zhu10514:8081/loginNotification/ws",
	
	init_: function(){
		//connect to server
		this.websocket_ = new WebSocket(this.wsURL_);
		this.websocket_.onopen = function(evt) {
			console.log(dataExchange.wsURL_+" connected");
			//need consider to keep heartbeat job, need clear it after websocket is closed
			setInterval(function() {
				console.log("send heartbeat "+new Date());
				dataExchange.websocket_.send('heartbeat');
			}, 15*1000);
		}; 
		this.websocket_.onmessage = function(evt) { 
		    notification.sendNotification_($.parseJSON(evt.data));
		}; 
		this.websocket_.onerror = function(evt) { 
		    console.log(dataExchange.wsURL_+" error occurred: "+evt.data);
		};
	},
	
	submitToServer_: function(username, sendResponse){
		$.ajax({
			url: 'http://zhu10514:8081/loginNotification/loginDetected.jsp?u='+encodeURIComponent(username),
			async:true,
			timeout:3000,
			error: function(e){
				sendResponse({status: 'failed'});
			},
			success: function(){
				sendResponse({status: 'success'});
			} 
		  });
	}

}
$(function (){
	dataExchange.init_();
});