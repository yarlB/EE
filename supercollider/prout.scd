Sonic.sonInit;

(
SynthDef.new(\caca, {
	arg bus;
	Out.ar(bus, {SinOsc.ar})
}).add;
)

Sonic.playSynth(\truc, \caca, \ambiance);


s.boot;

l = [];

l = [\truc, 1] ++ l;

l;

~ev = Event.new;

Sonic.playSynth(\truc2, \caca, \ambiance);

Sonic.playFile(\truc,"/home/yarl/Téléchargements/birds.wav",\ambiance);

b = Buffer.read(s, "/home/yarl/Téléchargements/birds.wav");

x = Synth.new(\playbuf,[\bus, 0, \buf, b]);

s.quit;
(
x = SynthDef("PB",{ arg out = 0, buf;
    Out.ar( out,
        PlayBuf.ar(1, buf, BufRateScale.kr(buf))
    )
}).add;
)

x = Synth(\PB, [\buf, b]);

Sonic.removeObject(\truc);

b = Bus.audio(s,1);
b;
x = Synth(\caca, [\bus, b]);

t = Synth(\one2four, [\bus, b], addAction: 'addToTail');

Synth.after(

f.openRead;
f.inspect;
f.cue(~ev,false);
f.duration;
~ev.resume;
f.close;
f.free;
)

Platform.userConfigDir;

Platform.systemExtensionDir;

s.queryAllNodes;

s.scope;

LADSPA.listPlugins;

10.do({arg i; ("truc" + i).postln});

s.boot;

(
OSCFunc({ arg msg, time;
	[time, msg].postln;
}, '/done', s.addr);
)

x = Synth(\caca);

SynthDef(\relBuf, {arg s; s.

x.free;

