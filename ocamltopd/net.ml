#load "unix.cma";;
open Unix

let tell_pd =
  let fd = socket PF_INET SOCK_DGRAM 0 and 
      addr = ADDR_INET (inet_addr_any, 3005)
  in 
  connect fd addr;
  (fun msg -> write fd (msg ^ ";\n") 0 (String.length msg + 2))

let _ = tell_pd "salut"
