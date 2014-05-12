#ifndef ZONE_INCL
#define ZONE_INCL

#include <unordered_set>
#include <string>

typedef progname_t std::string;
typedef zonename_t std::string;

class Zone {
public:
  Zone(zonename_t);
  ~Zone();

  void add_prog(progname_t const&);
  void rm_prog(progname_t const&);

private:
  zonename_t m_name;
  std::unordered_set<progname_t> m_progs;
  
  Zone();
};

#endif //ZONE_INCL
