~playfile = {|name, dur, loop| "playfile " + name + ", " + dur + ", " + loop};

OSCFunc.new(\playfile, {|name, dur,loop| ~playfile.value(name,dur,loop)}, "/play/file");


OSCFunc.new(\playsynth, {|name, dur, 