/* Sonic, all methods are class methods, we only want one "instance" of this (or singleton) */
Sonic {
  classvar <c_server;
  classvar c_groups;
  classvar <c_synths;
  classvar c_fading;

  *sonInit {
    c_server = Server.local;
    c_server.boot;
    c_groups = ObjectTable.new;
    c_synths = ObjectTable.new;
    c_fading = 0.7;
  }

  //ajout d'un groupe non spatialisé
  *addGroup_nospat {
    arg name;
    var g = EE_Group_NoSpat.new(name,c_server);
    c_groups.put(name, g);
    ^g;
  }

  //creation du groupe 'groupname' s'il n'existe pas (non spatialisé)
  *ifNoGrCreate {
    arg groupname;
    var g = c_groups.at(groupname);
    if (g == nil,
      {g = this.addGroup_nospat(groupname)});
    ^g;
  }

  *nameExists {
    arg name;
    ^if(c_synths.at(name) != nil) {true} {false}   
  }

  *tellIfNameExists {
    arg name;
    if(this.nameExists(name)) {"Un objet portant ce nom existe déja".postln; true} {false}
  }

  //joue le fichier 'filename' dans le groupe 'groupname' et l'enregistre comme 'name' dans la table
  *playFileOnce {
    arg name, filename, groupname, start, dur;
    if(! this.tellIfNameExists(name)) {
      var g = this.ifNoGrCreate(groupname);
      var s = g.playFileOnce(filename,start,dur);
      c_synths.put(name, s);
    }
    
  }

  //joue la synth 'synth' dans le groupe 'groupname' et l'enregistre comme 'name' dans la table
  *playSynth {
    arg name, synth, groupname, args;
    var g = this.ifNoGrCreate(groupname);
    if (c_synths.at(name) != nil, 
      {"Un objet portant ce nom existe déja".postln},
      {c_synths.put(name, g.playSynth(synth,args))});//go try si synth existe
  }

  *removeObject {
    arg name;
    c_synths.at(name).free;
    c_synths.removeAt(name);
  }
  
  *initClass {
    StartUp.add { 
      SynthDef(\one2four, { arg bus;
	  Out.ar(0, Pan4.ar(In.ar(bus)))}).add 
	};
    
    //StartUp.add { SynthDef(\spat, {/* On verra ça plus tard */}).add };
    
    //On peut envoyer stop =1 aux trois synthdefs qui suivent pour arreter la lecture.
    //joue le fichier une fois.
    StartUp.add { 
      SynthDef(\playFileOnce,{
	  |i_bufnum, i_out, stop=1,i_att,i_dec,i_bufdur|
	    var pb = PlayBuf.ar(1, i_bufnum,
				BufRateScale.kr(i_bufnum),
				doneAction: 2),
	    lin = Linen.kr(stop,0,1,i_dec,2);
	  Out.ar(i_out,
		 pb
		 * lin
		 * EnvGen.kr(Env.linen(i_att,i_bufdur - i_att - i_dec,i_dec)));
	  SendTrig.kr(Done.kr(lin) + Done.kr(pb));//utile pour dire au client de clean son ObjectTable
	}).add 
	};
    
  StartUp.add {
    SynthDef(\playFileThisTime, {
	|i_bufnum,i_out,stop=1,i_att,i_dec,i_time|
	  var eg = EnvGen.kr(Env.linen(i_att,
				       i_time-i_att-i_dec,i_dec),
			     doneAction:2);
	  Out.ar(i_out,PlayBuf.ar(1,i_bufnum,
				  BufRateScale.kr(i_bufnum),loop: 1)
		 * eg);
	  SendTrig.kr(Done.kr(eg));
      }).add
      };

  StartUp.add {
    SynthDef(\playFileLoop,{
	|i_bufnum,i_out,stop=1,i_att,i_dec,t_stop|
	  var lin = Linen.kr(stop,i_att,1,i_dec,2);
	  Out.ar(i_out,
		 PlayBuf.ar(1,i_bufnum,
			    BufRateScale.kr(i_bufnum),
			    loop: 1) * lin
		 );
	  SendTrig.kr(Done.kr(lin));
      }).add
      };
  }
}
