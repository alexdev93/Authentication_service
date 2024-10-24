package com.app.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    // Create a new item
    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item savedItem = itemRepository.save(item);
        return ResponseEntity.status(201).body(savedItem);
    }

    // Get all items
    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
