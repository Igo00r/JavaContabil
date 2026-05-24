package com.fiap.financecontrol.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistroContabilEventoMongoRepository
    extends MongoRepository<RegistroContabilEventoDocument, String>{

}