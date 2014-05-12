#include <iostream>
#include <string>
#include <cstdlib>

#include "zou.hpp"

#include "globals.hpp"

extern std::string const dboptions;
extern int const port;

int cb_lo_position(char const* path, char const* types, lo_arg ** argv, int argc, void* msg, void* zou) {
  std::cout << "position callback : " << argv[0]->i << ", " << argv[1]->i << std::endl;
  ((Zou*)zou)->setPositionGamer(argv[0]->i, argv[1]->i);
    return 0;
}

int cb_lo_compas(char const* path, char const* types, lo_arg ** argv, int argc, void* msg, void* zou) {
  std::cout << "compas callback : " << argv[0]->i << std::endl;  
  ((Zou*)zou)->setCompasGamer(argv[0]->i);
  return 0;
}

/*
int cb_lo_all(char const* path, char const* types, lo_arg ** argv, int argc, void* msg, void* zou) {
  std::cout << "prout" << std::endl;
  return 0;
}
*/

int cb_lo_stoploop(char const* path, char const* types, lo_arg ** argv, int argc, void* msg, void* zou) {
  ((Zou*)zou)->set_loop(false);
  return 0;
}

Zou::Zou(std::string const& s) : m_pgcon(s), m_lo_st(port) {
  std::cout << "Base de données ... ";
  if (m_pgcon.is_open())
    std::cout << "ouverte.\n";
  else {
    std::cout << "echec d'ouverture.\n";
    throw "OUCH";
  }

  if (!m_lo_st.is_valid()) {
    std::cout << "Echec d'ouverture du port : " << port << std::endl;
    throw "OUCH";
  }
  std::cout << "URL : " << m_lo_st.url() << std::endl;
  
  m_lo_st.add_method("/position/position", "ff", cb_lo_position, (void*)this);
  m_lo_st.add_method("/position/compas", "ff", cb_lo_compas, (void*)this);

  m_lo_st.add_method("/zou/stoploop","ff",cb_lo_stoploop,NULL);

  m_lo_st.start();
}

Zou::~Zou() {m_pgcon.disconnect();}

void Zou::loop() {
  TransOut t_out;
  m_loop = true;
  while(m_loop) {
  m_pgcon.perform(Trans("SELECT pioche('(" + std::to_string(m_xGamer) + "," + std::to_string(m_yGamer) + ")')", t_out));
  
  processAll(t_out.m_result);
  }
}

void Zou::set_loop(boolean b) {
  m_loop = b;
}

void Zou::processAll(pqxx::result const& result) {
  for(pqxx::result::size_type i=0 ; i!=result.size() ; ++i) {
    processProgram(result[i][0].c_str());
  }
}

void Zou::processProgram(char const* program) {
  std::cout << program << std::endl;
}

void Zou::setPositionGamer(float x, float y) {
  m_xGamer = x;
  m_yGamer = y;
}

void Zou::setCompasGamer(float c) {
  m_compasGamer = c;
}

using namespace std; 
int main() {
  Zou *z;
  try {
    z = new Zou(dboptions);
  }catch (const std::exception &e){
    cerr << e.what() << std::endl;
    return(EXIT_FAILURE);
  }
  
  z->loop();

  return(EXIT_SUCCESS);
}
