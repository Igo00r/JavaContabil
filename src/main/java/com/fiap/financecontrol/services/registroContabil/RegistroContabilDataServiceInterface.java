package com.fiap.financecontrol.services.registroContabil;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.RegistroContabil;

import java.math.BigDecimal;

public interface RegistroContabilDataServiceInterface {
    RegistroContabil execute(RegistroContabil registro);
}
