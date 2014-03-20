Array.prototype.remove = function(value) {
  var idx = this.indexOf(value);
  if (idx != -1) {
      return this.splice(idx, 1); // The second parameter is the number of elements to remove.
  }
  return false;
}

var publisher = {
    clients: [],
    subscribe: function (client) {
        this.clients.push(client);
    },
    unsubscribe: function (client) {
        this.clients.remove(client);
    },
    publish: function (fn, args) {
        this.visitClients(fn, args);
    },
	visitClients: function (fn, args) {
		var i;
    	for(i=0; i<this.clients.length; i++) {
			fn(this.clients[i], args);
		}
	} 
}
publisher.subscribe("Ben");
publisher.subscribe("Kyle");
var xiaomi = function (client, price) {
	console.log('Hi ' + client + ', Xiaomi3 is very cheap, only requires RMB:' + price);
}
publisher.publish(xiaomi, 1999);
var hongmi = function (client, price) {
	console.log('Hi ' + client + ', Hongmi is very cheap, only requires RMB:' + price);
}
publisher.unsubscribe("Kyle");
publisher.publish(hongmi, 799);

