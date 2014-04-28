/* Sonic, all methods are class methods, we only want one "instance" of this (or singleton) */
Sonic {
  classvar c_server;
  classvar c_groups;
  classvar c_synths;
  classvar c_fading;

  *sonInit {
    c_server = Server.local;
    c_server.boot;
    c_groups = ObjectTable.new;
    c_synths = ObjectTable.new;
    c_fading = 0.7;
  }

  *addGroup_nospat {
    arg name;
    var g = EE_Group_NoSpat.new(name,c_server);
    c_groups.put(name, g);
    ^g;
  }

  *ifNoGrCreate {
    arg groupname;
    var g = c_groups.at(groupname);
    if (g == nil,
      {g = this.addGroup_nospat(groupname)});
    ^g;
  }

  *playFile {
    arg name, filename, groupname, loop, dur, to_spatialise;
    var g = this.ifNoGrCreate(groupname);
    if (c_synths.at(name) != nil,
      {"Un objet portant ce nom existe déja".postln},
      {c_synths.put(name, g.addSound(filename))});
  }

  *playSynth {
    arg name, synth, groupname, args;
    var g = this.ifNoGrCreate(groupname);
    if (c_synths.at(name) != nil, 
      {"Un objet portant ce nom existe déja".postln},
      {c_synths.put(name, g.addSynth(synth,args))});//go try si synth existe
  }

  *removeObject {
    arg name;
    c_synths.at(name).free;
    c_synths.removeAt(name);
  }
  
  *initClass {
    StartUp.add { SynthDef(\one2four, { arg bus;
	  Out.ar(0, Pan4.ar(In.ar(bus)))}).add 
	};
    //StartUp.add { SynthDef(\spat, {/* On verra ça plus tard */}).add };
    StartUp.add { SynthDef(\playbuf, { arg i_bus, i_buf, i_dur, i_loop=0;
	  Out.ar(i_bus, PlayBuf.ar(1, i_buf, BufRateScale.kr(i_buf)))
	    }).add 
	};
  }
}
