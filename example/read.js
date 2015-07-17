var getmedia = require('get-user-media');
var unpacker = require('uart-unpack-frame');
var through = require('through2');

getmedia({ audio: true }, function (err, stream) {
    if (err) return console.error(err);
    var Context = global.AudioContext || global.webkitAudioContext;
    var context = new Context;
    var win = context.sampleRate / 9600;
    var mstream = context.createMediaStreamSource(stream);
    var unpack = unpacker();
    unpack.pipe(through(function (buf, enc, next) {
        //console.log('buf=', [].slice.call(buf));
        next();
    }));
    
    var sproc = context.createScriptProcessor(2048, 1, 1);
    sproc.addEventListener('audioprocess', onaudio);
    var MIN = 0.05;
    var bytev = 0;
    var bytepos = 0;
    
    mstream.connect(sproc);
    sproc.connect(sproc.context.destination);
    
    function onaudio (ev) {
        var input = ev.inputBuffer.getChannelData(0);
        var prev = input[0] > 0 ? 1 : 0;
        var last = 0;
        var bytes = [];
        if (Math.abs(prev) < MIN) prev = null;
        
        for (var i = 1; i < input.length; i++) {
            if (Math.abs(input[i]) < MIN) {
                prev = null;
                continue;
            }
            var cur = input[i] > 0 ? 1 : 0;
            if (prev === null) {
                prev = cur;
                last = i;
                continue;
            }
            
            if (cur ^ prev) {
                var dist = i - last;
                last = i;
                
                var nbits = Math.round(dist / win);
                for (var j = 0; j < nbits; j++) {
                    bytev += prev << bytepos;
                    if (++bytepos === 8) {
                        console.log('BYTE=', bytev);
                        bytes.push(bytev);
                        bytev = 0;
                    }
                }
            }
        }
        
        if (bytes.length) unpack.write(Buffer(bytes));
    }
});
