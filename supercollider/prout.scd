Sonic.sonInit;

SynthDef.new(\caca, {
	arg bus;
	Out.ar(bus, {SinOsc.ar})
}).add;

s.boot;


~ev = Event.new;
f = Sonic.playFile(\truc,"/home/yarl/Téléchargements/birds.wav",\ambiance);
f.openRead;
f.inspect;
f.cue(~ev,false);
f.duration;
~ev.resume;
f.close;
f.free;
)



s.queryAllNodes;

s.scope;