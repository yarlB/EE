#ifndef ZOU_INCL
#define ZOU_INCL

#include <pqxx/pqxx>
#include "../trans/trans.hpp"

#include <lo/lo.h>
#include <lo/lo_cpp.h>


class Zou {
public:
  Zou(std::string const&); //chaine de param pqxx
  ~Zou();
  
  void loop();

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
};

#endif //ZOU_INCL

