var Serial = require('serialport').SerialPort;
var serial = new Serial('/dev/ttyUSB0', { baudrate: 9600 });

serial.on('open', function () {
    setInterval(function () {
        serial.write('ABC');
    }, 100);
});
