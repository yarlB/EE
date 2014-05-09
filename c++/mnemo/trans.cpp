#include "trans.hpp"

void TransOut::updateResult(pqxx::result const & r) {
  m_result = r;
}


Trans::Trans(std::string const& procname, TransOut &out) :
  transactor<argument_type>("transaction"), m_command(command), m_out(out) {}

void Trans::operator()(argument_type &T) {
  m_result = T.exec(m_command);
}

void Trans::on_commit(void) {
  m_out.updateResult(m_result);
}
