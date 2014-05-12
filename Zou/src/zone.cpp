#include "zone.cpp"

Zone::Zone(zonename_t n) : m_name(n) {}

Zone::~Zone() {}

void Zone::add_prog(progname_t const& pn) {
  m_progs.insert(pn);
}

void Zone::rm_prog(progname_t const& pn) {
  m_progs.erase(pn);
}


