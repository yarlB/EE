#ifndef ZOU_INCL
#define ZOU_INCL

#include <pqxx/pqxx>
#include "trans.hpp"

#include <lo/lo.h>
#include <lo/lo_cpp.h>


class Zou {
public:
  Zou(std::string const&); //chaine de param pqxx
  ~Zou();
  
  void loop();
  void set_loop(boolean);

  void processAll(pqxx::result const&);
  void processProgram(char const*);

  void setPositionGamer(float, float);
  void setCompasGamer(float);
  
private:
  pqxx::connection m_pgcon;
  lo::ServerThread m_lo_st;

  float m_xGamer;
  float m_yGamer;
  float m_compasGamer;

  boolean m_loop;
};

#endif //ZOU_INCL

