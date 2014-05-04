EE_Group {
  var m_name;
  var m_g;
  var m_synths;
  var m_effects;
  var m_bus;

  *new {
    arg name, bus_nb;
    ^super.new.eegInit(name, bus_nb);
  }
  
  eegInit {
    arg name, bus_nb;
    m_name = name;
    m_g = Group.new(Sonic.c_server);
    m_synths = ParGroup.new(m_g);
    m_effects = Group.after(m_synths);
    m_bus = Bus.audio(Sonic.c_server, bus_nb);
    "Réservation de bus pour le groupe : ".post;
    m_bus.postln;
  }

  free {
    m_name.free;
    m_g.free;
    m_synths.free;
    m_effects.free;
    m_bus.free;
    super.free;
  }  
}


EE_Group_NoSpat : EE_Group {
  var m_one2four;

  *new {
    arg name;
    ^super.new(name,1).eegnsInit; //<-- 1 bus
  }

  eegnsInit {
    m_one2four = Synth.after(m_effects, \one2four,[\i_in,m_bus]);
  }

  free {
    m_one2four.free;
    super.free;
  }

  playSynth {
    arg synth, args;
    var allargs = [\bus, m_bus];
    if(args != nil,
       allargs = allargs ++ args);
    allargs.postln;
    ^Synth.new(synth, allargs, m_synths);
  }
  
  playFile {
    arg file, start, dur, s_name, s_synth, args = [];
    var syn, buf;
    buf = Buffer.read(Sonic.c_server, file.path, file.sampleRate * start, if(dur > 0,file.sampleRate * dur,-1),
		      {syn = Synth(s_synth, [\i_bufnum, buf, \i_out, m_bus, \i_fad, Sonic.c_fading] ++ args, m_synths);
			syn.onFree({buf.free; Sonic.c_synths.removeAt(s_name)});
			Sonic.c_synths.put(s_name, syn);});		
    file.close;
    ^true;
  }

  playFileOnce {
    arg file, start, dur, s_name;
    ^this.playFile(file, start, dur, s_name, \playFileOnceMono, if(dur > 0, [\i_bufdur, dur], [\i_bufdur, file.duration - start]));
  }

  playFileThisTime {
    arg file, start, dur, time, s_name;
    ^this.playFile(file, start, dur, s_name, \playFileThisTimeMono, [\i_time, time]);
  }

  playFileLoop {
    arg file, start, dur, s_name;
    ^this.playFile(file, start, dur, s_name, \playFileLoopMono);
  }
}


EE_Group_Spat : EE_Group {
  var m_four2four;
  
  *new {
    arg name;
    ^super.new(name,Sonic.c_nb_outs).eegsInit; //<-- 4 bus
  }
  
  eegsInit {
    m_four2four = Synth.after(m_effects, \four2four,[\i_in,m_bus]);
  }
  
  free {
    m_four2four.free;
    super.free;
  }
  
  playFile {
    arg file, start, dur, s_name, s_synth, x, y;
    var gr, syn, buf, bus, spat;
    buf = Buffer.read(Sonic.c_server, file.path, file.sampleRate * start, if(dur > 0,file.sampleRate * dur, -1),
		      {gr = Group.new(m_synths);
			bus = Bus.audio(Sonic.c_server, 1); //<-- du playfile à la spat il n'y a qu'un bus
			spat = Synth.new(\spat, [\i_in, bus, \i_out, m_bus, \x,Sonic.c_x_bus, \y,Sonic.c_y_bus, \angle,Sonic.c_angle_bus, \xo,x, \yo,y], gr);
			syn = Synth.before(spat, s_synth, [\i_bufnum, buf, \i_out, bus, \i_fad, Sonic.c_fading] ++ args);
			syn.onFree({buf.free; Sonic.c_synths.removeAt(s_name); gr.free; bus.free});
			Sonic.c_synths.put(s_name, gr);});
    file.close;
    ^true;
  }

  playFileOnce {
    arg file, start, dur, s_name;
    ^this.playFile(file, start, dur, s_name, \playFileOnceMono, if(dur > 0, [\i_bufdur, dur], [\i_bufdur, file.duration - start]));
  }

  playFileThisTime {
    arg file, start, dur, time, s_name;
    ^this.playFile(file, start, dur, s_name, \playFileThisTimeMono, [\i_time, time]);
  }

  playFileLoop {
    arg file, start, dur, s_name;
    ^this.playFile(file, start, dur, s_name, \playFileLoopMono);
  }
}




















