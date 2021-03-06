#include <iostream>
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
    //pause (nanosleep?)
  }
}

void Zou::set_loop(boolean b) {
  m_loop = b;
}

//ligne résultat : (nom_programme, programme, nom_zone)
void Zou::processAll(pqxx::result const& result) {
  std::unordered_set<char const*> zones_names_tmp;

  for(pqxx::result::size_type i=0 ; i!=result.size() ; ++i) {
    field_raw_t progname_raw = result[i][0].c_str(), program_raw = result[i][1].c_str(),
      zonename_raw = result[i][2].c_str();
    zonename_t zonename(zonename_raw);
    zones_names_tmp.insert(zonename_raw);
    progset_t hmm;
    if(m_zones.count(zonename) == 0)
      hmm = m_zones.insert(zonename, progset_t());
    else
      hmm = m_zones.find(zonename);
    processProgram(hmm, program_raw);
  }

  //envoi ordre de gelement des zones de m_zones pas dans zones_names_tmp;
  //les zones gelées disparaissent (et tous leurs objets) au bout d'un certain temps
}

void Zou::processProgram(progset_t & zonename, field_raw_t program) {
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
