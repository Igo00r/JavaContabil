package com.fiap.financecontrol.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VendaEventoMongoRepository
        extends MongoRepository<VendaEventoDocument, String> {
}