EE_Group {
  var m_server;
  var m_name;
  var m_g;
  var m_synths;
  var m_effects;

  *new {
    arg name, server;
    ^super.new.eegInit(name, server);
  }

  eegInit {
    arg name, server;
    m_server = server;
    m_name = name;
    m_g = Group.new(server);
    m_synths = ParGroup.new(m_g);
    m_effects = Group.after(m_synths);
  }
  
  free {
    m_name.free;
    m_g.free;
    m_synths.free;
    m_effects.free;
    super.free;
  }
}


EE_Group_NoSpat : EE_Group {
  var m_bus;
  var m_one2four;
  var m_bufs;


  *new {
    arg name, server;
    ^super.new(name,server).eegnsInit(server);
  }

  eegnsInit {
    arg server;
    "Group_NoSpat : reservation du bus : ".post;
    m_bus = Bus.audio(server, 1);
    m_bus.postln;
    m_one2four = Synth.after(m_effects, \one2four,[\bus, m_bus]);
    m_bufs = [];
  }

  free {
    m_one2four.free;
    m_bus.free;
    super.free;
  }

  addSynth {
    arg synth, args;
    var allargs = [\bus, m_bus];
    if(args != nil,
       allargs = allargs ++ args);
    allargs.postln;
    ^Synth.new(synth, allargs, m_synths);
  }

  addSound {
    arg filename;
    var buffer = Buffer.read(m_server, filename);
    var args = [\i_bus, m_bus, \i_buf, buffer];
    args.postln;
    ^Synth.new(\playbuf, args, m_synths, doneAction: 2);
  }
}


EE_Group_Spat : EE_Group {
  *new {
    arg name, server;
    ^super.new(name,server).eegsInit(server);
  }

  *eegsInit {
    arg server;
    // m_bus = Bus.audio(server, 4);
  }

  free {
    super.free;
  }
}
