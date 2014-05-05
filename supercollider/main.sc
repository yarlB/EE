(
/*
  Les SynthDef suivantes ne sont pas à utiliser directement, faut passer
par Sonic.
*/
~makePFOnce = {|name, nbchans|
	SynthDef(name,{
		|i_bufnum, i_out,i_fad,i_bufdur, stop=1|
		var pb = PlayBuf.ar(1, i_bufnum,BufRateScale.kr(i_bufnum),1,0,0,2) ! nbchans,
		lin = Linen.kr(stop,0,1,i_fad,2),
		eg = EnvGen.kr(Env.linen(i_fad,i_bufdur - (i_fad*2),i_fad));
		Out.ar(i_out,pb * lin * eg)
	}).add;
};
~makePFOnce.value(\playFileOnceMono, 1);
~makePFOnce.value(\playFileOnceQuad, 4);

~makePFTT = {|name, nbchans|
	SynthDef(name, {
		|i_bufnum,i_out,i_fad,i_time,stop=1|
		var eg = EnvGen.kr(Env.linen(i_fad,i_time-(i_fad*2),i_fad),1,1,0,1,2),
		pb = PlayBuf.ar(1,i_bufnum,BufRateScale.kr(i_bufnum),1,0,1) ! nbchans,
		lin = Linen.kr(stop,i_fad,1,i_fad,2);
		Out.ar(i_out, pb * eg * lin)
	}).add;
};
~makePFTT.value(\playFileThisTimeMono, 1);
~makePFTT.value(\playFileThisTimeQuad, 4);

~makePFL = {|name, nbchans|
	SynthDef(name,{
		|i_bufnum,i_out,i_fad,stop=1|
		var lin = Linen.kr(stop,i_fad,1,i_fad,2),
		pb = PlayBuf.ar(1,i_bufnum,BufRateScale.kr(i_bufnum),1,0,1) ! nbchans;
		Out.ar(i_out,pb * lin);
	}).add;
};
~makePFL.value(\playFileLoopMono,1);
~makePFL.value(\playFileLoopQuad,4);

SynthDef(\one2four, { arg i_in; Out.ar(0, Pan4.ar(In.ar(i_in)))}).add;
SynthDef(\four2four, {arg i_in; Out.ar(0, In.ar(i_in, 4))}).add;

SynthDef(\spat, {|i_out, i_in, xb, yb, xo=0.0, yo=0.0, angleb|
	var x = In.kr(xb),y = In.kr(yb),angle = In.kr(angleb),
	attenuation = (1 - ((((xo - x) ** 2.0) + ((yo - y) ** 2.0)) / 250.0)).max(0.0),//on entends plus à 50 metres
	hyp = (((xo - x) ** 2) + ((yo - y) ** 2.0)).sqrt.max(0.01),
	adj = yo - y,
	xpan = LinSelectX.kr(Clip.kr(hyp,0,1),[0,(((adj/hyp).acos * (180/pi))-angle).fold(0,90) / 90]),
	ypan = LinSelectX.kr(Clip.kr(hyp,0,1),[0,(90 - (((adj/hyp).acos * (180/pi))-angle).abs) / 90]);
	Out.ar(i_out, Pan4.ar(In.ar(i_in,1),xpan,ypan,attenuation));
  }).add;

~sonicPath = "/Sonic";
//
~makeOSCF = {|method, path|
    OSCFunc.new({|m| method.value(Sonic,m[1..m.size])},(~sonicPath+/+path).asSymbol);
};

~makeOSCF.value({|a,b| a.playFileOnce(*b)},'playFileOnce');
~makeOSCF.value({|a,b| a.playFileLoop(*b)},'playFileLoop');
~makeOSCF.value({|a,b| a.playFileThisTime(*b)},'playFileThisTime');
~makeOSCF.value({|a,b| a.removeAt(*b)},'removeAt');
~makeOSCF.value({|a,b| a.playSynth(*b)},'playSynth');

OSCFunc.new({|m| Sonic.setPos(*m[1..m.size])}, '/position/position');
OSCFunc.new({|m| /*Sonic.setAngle(m[1])*/m.postln}, '/position/compas');

Sonic.sonInit;
)

Sonic.sonFree;

Sonic.playFileLoop(\bird,"/home/yarl/Téléchargements/birds.wav","groupspat",0,-1,true);

Sonic.c_angle_bus.set(45);

b = Bus.audio(s);
n = Buffer.read(s,"/home/yarl/Téléchargements/birds.wav");
x = Synth(\playFileLoopMono,[\i_bufnum,n,\i_out,b,\i_fad,0.7]);

t = Synth.after(x,\spat,[\i_out,0,\i_in,b]);
t.free;

s.scope;

x = Sonic.c_synths.at(\bird);
x.set(\xo,2);
s.plotTree;
Sonic.sonFree;
x = Sonic.c_synths.at(\bird);
Sonic.removeAt(\bird);

x.get(\xo);
b = Bus.control(s);

x = {SinOsc.ar(In.kr(b))}.play;
b.set(400);
x.free;
