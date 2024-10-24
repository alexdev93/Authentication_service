package com.app.myapp;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {
}
