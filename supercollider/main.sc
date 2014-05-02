(

/*
  Les SynthDef suivantes ne sont pas à utiliser directement, faut passer
par Sonic.
*/

SynthDef(\playFileOnce,{
	|i_bufnum, i_out,i_fad,i_bufdur, stop=1|
	var pb = PlayBuf.ar(1, i_bufnum,BufRateScale.kr(i_bufnum),1,0,0,2),
	lin = Linen.kr(stop,0,1,i_fad,2),
	eg = EnvGen.kr(Env.linen(i_fad,i_bufdur - (i_fad*2),i_fad));
	Out.ar(i_out,pb * lin * eg)
}).add;


SynthDef(\playFileThisTime, {
	|i_bufnum,i_out,i_fad,i_time,stop=1|
	var eg = EnvGen.kr(Env.linen(i_fad,i_time-(i_fad*2),i_fad),1,1,0,1,2),
	pb = PlayBuf.ar(1,i_bufnum,BufRateScale.kr(i_bufnum),1,0,1),
	lin = Linen.kr(stop,i_fad,1,i_fad,2);
	Out.ar(i_out, pb * eg * lin)
}).add;


SynthDef(\playFileLoop,{
	|i_bufnum,i_out,i_fad,stop=1|
	var lin = Linen.kr(stop,i_fad,1,i_fad,2),
	pb = PlayBuf.ar(1,i_bufnum,BufRateScale.kr(i_bufnum),1,0,1);
	Out.ar(i_out,pb * lin);
}).add;


SynthDef(\one2four, { arg i_in; Out.ar(0, Pan4.ar(In.ar(i_in)))}).add;
SynthDef(\four2four, {arg i_in; Out.ar(0, In.ar(i_in, 4))}).add;

SynthDef(\spat, {|i_out, i_in, x=0, y=0, xo=0, yo=0, angle_joueur=0|
	var
	attenuation = 1 - ((((xo - x) ** 2) + ((yo - y) ** 2)) / 250),//on entends plus à 50 metres
	hyp = (((xo - x) ** 2) + ((yo - y) ** 2)).sqrt,
	angle = ((yo - y)/hyp).acos * (180 / pi),
	xpan = angle.fold(0,90) / 90,
	ypan = (90 - angle.abs) / 90;
	Out.ar(i_out, Pan4.ar(In.ar(i_in,4),xpan,ypan,attenuation));
}).add;
)
0.wrap(1,60);
i = IdentityDictionary.new;

i.put(\lol, Synth(\one2four));
s.quit;
170.fold(0,90);
~dist = {|x,y,xo,yo| (((xo - x) ** 2) + ((yo - y) ** 2)).sqrt};
x = 0;
~xo = 2;
y = 0;
~yo = 1;
~adjacent = 0;
~hyp = ~dist.value(0,0,2,1);
(~adjacent/1).acos;
{(((~xo - x) ** 2) + ((~yo - y) ** 2)).sqrt}.value;
Sonic.sonInit;
Sonic.playFileLoop("prout", "/STOCKAGE/SON/jack_capture_02.wav","perso",8,2,true);
x = Sonic.c_synths.at(\prout);
x.set(\stop,0);
Sonic.removeObject("prout");
Sonic.c_synths.at(\prout);
EEGroup_Spat
s.volume = 0.5;
s.plotTree;
s.scope;
		