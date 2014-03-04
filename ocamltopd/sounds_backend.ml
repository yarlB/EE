#use "net.ml";;
type pdsound = PDSound of string * int
let str_of_pdsound (PDSound (str, i)) = str ^ " " ^ (string_of_int i)
module IISet = struct
  type t = int
  let compare = compare
end
module ISet = Set.Make(IISet)
open ISet

let playing_sounds = ref empty

let play_sound (pds: pdsound) =
  let (PDSound (_,i)) = pds in
  if mem i !playing_sounds then ()
  else playing_sounds := add i !playing_sounds;
  tell_pd ("open " ^ (str_of_pdsound pds));
  ()

let unplay_sound (i: int) =
  playing_sounds := remove i !playing_sounds;
  tell_pd ("close " ^ (string_of_int i));
  ()


let _ = play_sound (PDSound("simpleosc",1))
(*let _ = unplay_sound 1*)
