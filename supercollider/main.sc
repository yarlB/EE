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

SynthDef(\spat, {|i_out, i_in, x=0.0, y=0.0, xo=0.0, yo=0.0, angle=0|
	var
	attenuation = (1 - ((((xo - x) ** 2.0) + ((yo - y) ** 2.0)) / 250.0)).max(0.0),//on entends plus à 50 metres
	hyp = (((xo - x) ** 2) + ((yo - y) ** 2.0)).sqrt.max(0.01),
	adj = yo - y,
	xpan = LinSelectX.kr(Clip.kr(hyp,0,1),[0,(((adj/hyp).acos * (180/pi))-angle).fold(0,90) / 90]),
	ypan = LinSelectX.kr(Clip.kr(hyp,0,1),[0,(90 - (((adj/hyp).acos * (180/pi))-angle).abs) / 90]);
	Out.ar(i_out, Pan4.ar(In.ar(i_in,1),xpan,ypan,attenuation));
}).add;

n = NetAddr("127.0.0.1", NetAddr.langPort);
~sonicPath = "/Sonic";
//
~makeOSCF = {|method, path|
	OSCFunc.new({|m| method.value(Sonic,m[1..m.size])},(~sonicPath+/+path).asSymbol,n);
};

~makeOSCF.value({|a,b| a.playFileOnce(*b)},'playFileOnce');
~makeOSCF.value({|a,b| a.playFileLoop(*b)},'playFileLoop');
~makeOSCF.value({|a,b| a.playFileThisTime(*b)},'playFileThisTime');
~makeOSCF.value({|a,b| a.removeAt(*b)},'removeAt');
~makeOSCF.value({|a,b| a.playSynth(*b)},'playSynth');

~makeOSCF.value({|a,b| a.setPos(*b)}, 'setPos');

)



n.sendMsg('/playFileOnce',\prout,"/STOCKAGE/EE/bird.wav","amb");
Sonic.sonInit;
Sonic.playFileLoop("prout", "/STOCKAGE/EE/cwarb.wav","spat",0,2,true);
Sonic.c_synths.at(\prout).set(\xo,0.5);
Sonic.sonFree;

s.plotTree;