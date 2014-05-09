#ifndef TRANS_INCL
#define TRANS_INCL

#include <pqxx/pqxx>

class TransOut {
public:
  void updateResult(pqxx::result const &);
  
  pqxx::result m_result;
};

class Trans : public pqxx::transactor<> {
public:
  Trans(std::string const&, TransOut &);
  virtual void operator()(argument_type &);
  virtual void on_commit(void);

private:
  std::string m_command;
  TransOut &m_out;

  pqxx::result m_result;
};

#endif //TRANS_INCL
