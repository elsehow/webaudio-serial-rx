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
        console.log('buf=', [].slice.call(buf));
        next();
    }));
    
    var sproc = context.createScriptProcessor(2048, 1, 1);
    sproc.addEventListener('audioprocess', onaudio);
    
    mstream.connect(sproc);
    sproc.connect(sproc.context.destination);
    
    function onaudio (ev) {
        var input = ev.inputBuffer.getChannelData(0);
        for (var i = 0; i < input.length; i++) {
            if (Math.abs(input[i] >= 0.01)) {
                for (var j = i; j < input.length; j++) {
                    if (Math.abs(input[j] < 0.01)) break;
                }
                onsample(input.subarray(i,j));
            }
        }
    }
    
    function onsample (input) {
        var sample = new Buffer(Math.floor(input.length / win / 8));
        for (var i = 0; i < sample.length; i++) {
            sample[i] = 0;
            for (var j = 0; j < 8; j++) {
                var x = input[(i*8+j)*win] > 0 ? 1 : 0;
                sample[i] += x << j;
            }
        }
        unpack.write(sample);
    }
});
