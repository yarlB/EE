(

/*
  Les SynthDef suivantes ne sont pas à utiliser directement, faut passer
par Sonic.
*/
~makePFOnce = {|name, nbchans|
	SynthDef(name,{
		|i_bufnum, i_out,i_fad,i_bufdur, stop=1|
		var pb = PlayBuf.ar(1, i_bufnum,BufRateScale.kr(i_bufnum),1,0,0,2) ! nbchans,
		lin = Linen.kr(stop,0,1,i_fad,2) ! nbchans,
		eg = EnvGen.kr(Env.linen(i_fad,i_bufdur - (i_fad*2),i_fad)) ! nbchans;
		Out.ar(i_out,pb * lin * eg)
	},prependArgs: nbchans).add;
};
~makePFOnce.value(\playFileOnceMono, 1);
~makePFOnce.value(\playFileOnceQuad, 4);

~makePFTT = {|name, nbchans|
	SynthDef(name, {
		|i_bufnum,i_out,i_fad,i_time,stop=1|
		var eg = EnvGen.kr(Env.linen(i_fad,i_time-(i_fad*2),i_fad),1,1,0,1,2) ! nbchans,
		pb = PlayBuf.ar(1,i_bufnum,BufRateScale.kr(i_bufnum),1,0,1) ! nbchans,
		lin = Linen.kr(stop,i_fad,1,i_fad,2) ! nbchans;
		Out.ar(i_out, pb * eg * lin)
	},prependArgs: nbchans).add;
};
~makePFTT.value(\playFileThisTimeMono, 1);
~makePFTT.value(\playFileThisTimeQuad, 4);

~makePFL = {|name, nbchans|
	SynthDef(name,{
		|i_bufnum,i_out,i_fad,stop=1|
		var lin = Linen.kr(stop,i_fad,1,i_fad,2) ! nbchans,
		pb = PlayBuf.ar(1,i_bufnum,BufRateScale.kr(i_bufnum),1,0,1) ! nbchans;
		Out.ar(i_out,pb * lin);
	}).add;
};
~makePFL.value(\playFileLoopMono,1);
~makePFL.value(\playFileLoopQuad,4);

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

Sonic.sonInit;
Sonic.playFileLoop("prout", "/home/yarl/Téléchargements/birds.wav","perso",0,2,true);

Sonic.removeObject("prout");
x = Sonic.c_synths.at(\prout);
x.set(\xo, 50);

s.plotTree;
s.scope;



(
SynthDef(\truc, {|lol,nbchans|Out.ar(0,SinOsc.ar ! nbchans)},prependArgs: [\nbchans, 1]).add;
)
s.boot;
Synth(\truc);
		