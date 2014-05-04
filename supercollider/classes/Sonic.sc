/* Sonic, all methods are class methods, we only want one "instance" of this (or singleton) */
Sonic {
  classvar <c_server;
  classvar c_groups;
  classvar <c_synths;
  classvar <c_fading;

  classvar <c_x_bus;
  classvar <c_y_bus;
  classvar <c_angle_bus;

  classvar <c_nb_outs = 4;
    

  *sonInit {
    arg fad = 0.7;
    c_fading = fad;
    c_server = Server.local;
    c_server.boot;
    c_groups = IdentityDictionary.new; //des IdentityDisctionary parce que plus rapides (comparaison physique)
    c_synths = IdentityDictionary.new; //donc il faut faire attention et passer des symbols (asSymbol)
    c_x_bus = Bus.control(c_server);
    c_y_bus = Bus.control(c_server);
    c_angle_bus = Bus.control(c_server);
  }

  *sonFree {
    c_server.quit;
    c_groups.free;
    c_synths.free;
    c_x_bus.free;
    c_y_bus.free;
    c_angle_bus.free;
  }

  *addEEGroup {
    arg name, spec;
    var g = spec.new(name,c_server);
    c_groups.put(name, g);
    ^g;
  }


  //le boulot c'est de tester si l'objet est déja référencé, si oui on se plaint, sinon
  //on regarde si le groupe existe, est-ce que son type n'est pas cohérent avec 'to_spat', si oui on se plaint
  //sinon on créé un groupe
  //si l'identifiant de l'objet ou du groupe est nil on se plaint aussi
  //on retourne EE_Group (spécialisé Spat ou NoSpat) si succes, false sinon
  *prepareForPlay {
    arg s_name, s_groupname, to_spat = false; //les s_ sont des symboles, c'est moi qui le dit!
    var group;
    if(s_name === \nil) {"Identifiant nil".postln; ^false};
    if(c_synths.includesKey(s_name)) {("Identifiant" + s_name.asString + "déja utilisé").postln; ^false};
    if(s_groupname === \nil) {"Identifiant de groupe nil".postln; ^false};
    if((group = c_groups.at(s_groupname)) != nil) {
      if ( ((group.class === EE_Group_NoSpat) && (to_spat == true)) || ((group.class === EE_Group_Spat) && (to_spat == false))) {
	"Incohérence de groupe (spatialisation && non_spatialisation)".postln; ^false;
      }
    } {
      (group = this.addEEGroup(s_groupname, if(to_spat, EE_Group_Spat, EE_Group_NoSpat))); //si le groupe n'existe pas on l'ajoute.
    }
    ^group; //Si tout s'est bien passé on retourne le groupe.
  }

  //Verifie que le debut de lecture est cohérent, puis appel prepareForPlay, retourne l'EE_Group ainsi que le SoundFile
  *prepareFileForPlay {
    arg name, filename, groupname, start, dur, to_spat;
    var s_name = name.asSymbol, s_groupname = groupname.asSymbol, g, f, len;
    f = SoundFile.new(filename);
    if (f.openRead == false) {("Erreur d'ouverture du fichier" + filename).postln; f.close; ^false};
    len = f.duration;
    if (len < start) {("Offset de lecture" + start.asString +"du fichier" + filename + "de taille" + len.asString + "trop important.").postln; f.close; ^false};
    if(( g = this.prepareForPlay(s_name, s_groupname, to_spat)) == false) {f.close; ^false;};
    ^[g, f];
  }

  //Fait les vérifs ci-dessus et appel play_type sur l'EE_Group
  *playFile {
    arg s_name, filename, s_groupname = \ambiance, start = 0, dur = -1, to_spat=false, play_type;
    var g, f, ret;
    ret = this.prepareFileForPlay(s_name, filename, s_groupname, start, dur, to_spat);
    if (ret == false) {^false};
    g = ret[0]; f = ret[1];
    c_synths.put(s_name, nil); //<-- reservation (operations de buffer asynchrones)
    ret = play_type.value(g,f);
    ^true;
  }

  *playFileThisTime {
    arg name, filename, groupname, time, start = 0, dur = -1, to_spat = false, x, y;
    ^this.playFile(name.asSymbol,filename.asString, groupname.asSymbol, start, dur, to_spat.asBoolean, {|a,f| a.playFileThisTime(f, start, dur, time, name.asSymbol, x, y)});
  }

  *playFileOnce {
    arg name, filename, groupname, start = 0, dur = -1, to_spat = false, x, y;
    ^this.playFile(name.asSymbol,filename.asString, groupname.asSymbol, start, dur, to_spat.asBoolean, {|a,f| a.playFileOnce(f, start, dur, name.asSymbol, x, y)});
  }

  *playFileLoop {
    arg name, filename, groupname, start = 0, dur = -1, to_spat = false, x, y;
    ^this.playFile(name.asSymbol,filename.asString, groupname.asSymbol, start, dur, to_spat.asBoolean, {|a,f| a.playFileLoop(f, start, dur, name.asSymbol, x, y)});
  }

  //joue la synth 'synth' dans le groupe 'groupname'
  *playSynth {
    arg name, synth, groupname, to_spat = false, args = [];
    var s_name = name.asSymbol, s_groupname = groupname.asSymbol, g, s;
    g = prepareForPlay(s_name,s_groupname,to_spat);
    if(g == false) {^false};
    s = g.playSynth(synth,args);
    s.onFree({c_synths.removeAt(s_name)});
    c_synths.put(s_name,s);
  }

  *removeAt {
    arg name;
    var s;
    s = c_synths.at(name.asSymbol);
    s.set(\stop,0);
  }

  *setPos {
    arg x,y;
    c_x_bus.setSynchronous(x);
    c_y_bus.setSynchronous(y);
  }

  *setAngle {
    arg angle;
    c_angle_bus.setSynchronous(angle);
  }
}
