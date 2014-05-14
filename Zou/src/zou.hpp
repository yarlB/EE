#ifndef ZOU_INCL
#define ZOU_INCL

#include <string>
#include <unordered_map>
#include <unordered_set>

#include <pqxx/pqxx>
#include "trans.hpp"

#include <lo/lo.h>
#include <lo/lo_cpp.h>

typedef char const* fieldraw_t

typedef std::string zonename_t;
typedef std::string progname_t;
typedef std::unordered_set<progname_t> progset_t;
typedef std::unordered_map<zonename_t,progset_t> zonemap_t; 

class Zou {
public:
  Zou(std::string const&); //chaine de param pqxx
  ~Zou();
  
  void loop();
  void set_loop(boolean);

  void processAll(pqxx::result const&);
  void processProgram(zonename_t &, field_raw_t);

  void setPositionGamer(float, float);
  void setCompasGamer(float);
  
private:
  pqxx::connection m_pgcon;
  lo::ServerThread m_lo_st;

  zonemap_t m_zones; //zones dans lesquelles le joueur se trouve.

  float m_xGamer;
  float m_yGamer;
  float m_compasGamer;

  boolean m_loop;
};

#endif //ZOU_INCL

