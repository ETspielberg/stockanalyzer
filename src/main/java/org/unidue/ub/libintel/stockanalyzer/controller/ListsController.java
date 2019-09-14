package org.unidue.ub.libintel.stockanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.stockanalyzer.datarepositories.EventanalysisRepository;
import org.unidue.ub.libintel.stockanalyzer.model.lists.Memorylist;
import org.unidue.ub.libintel.stockanalyzer.model.lists.MemorylistItem;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.MemorylistRepository;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.StockcontrolRepository;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.*;

import java.util.Date;


@Controller
public class ListsController {

    private final MemorylistRepository memorylistRepository;

    private final EventanalysisRepository eventanalysisRepository;

    private final StockcontrolRepository stockcontrolRepository;

    @Autowired
    public ListsController(MemorylistRepository memorylistRepository,
                           EventanalysisRepository eventanalysisRepository,
                           StockcontrolRepository stockcontrolRepository) {
        this.memorylistRepository = memorylistRepository;
        this.eventanalysisRepository = eventanalysisRepository;
        this.stockcontrolRepository = stockcontrolRepository;
    }

    @GetMapping("/memorylist/forUser/{username}")
    public ResponseEntity<?> getUserlistsForUsername(@PathVariable String username) {
        return ResponseEntity.ok(memorylistRepository.findAllByUsernameOrderByName(username));
    }

    @GetMapping("/memorylist/fromStockcontrol/{identifier}")
    public ResponseEntity<?> getUserlistsForStockcontrol(@PathVariable String identifier) {
        Memorylist memorylist = new Memorylist(stockcontrolRepository.getByIdentifier(identifier));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        memorylist.setUsername(principalName);
        this.eventanalysisRepository.findByStockcontrolId(identifier).forEach(
                entry -> {
                    MemorylistItem memorylistItem = new MemorylistItem(entry);
                    memorylist.addMemorylistItem(memorylistItem);
                    memorylistItem.setMemorylist(memorylist);
                }
        );
        this.memorylistRepository.save(memorylist);
        return ResponseEntity.ok(memorylist);
    }

    @PostMapping("/newMemorylist")
    public ResponseEntity<?> saveItemlist(@RequestBody Memorylist memorylist) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        memorylist.setUsername(principalName);
        memorylist.setLastChange(new Date());
        memorylistRepository.save(memorylist);
        return ResponseEntity.ok(memorylist);
    }

/*
    @PostMapping("/memorylist/createItemlist")
    public ResponseEntity<?> createNewList(@RequestBody Memorylist memorylist) {
        return ResponseEntity.ok(this.memorylistRepository.save(memorylist));
    }



    @GetMapping("/getMemorylist/{uuid}")
    public ResponseEntity<?> retrieveItemlist(@PathVariable UUID uuid) {
        return ResponseEntity.ok(memorylistRepository.getById(uuid));
    }

    @PostMapping("/memorylist/addMemorylistItem")
    public ResponseEntity<?> saveItemToMemoryList(@RequestParam UUID uuid, @RequestBody MemorylistItem memorylistItem) {
        Memorylist memorylist = memorylistRepository.getById(uuid);
        memorylist.addMemorylistItem(memorylistItem);
        memorylistItem.setMemorylist(memorylist);
        memorylistItemRepository.save(memorylistItem);
        return ResponseEntity.ok(memorylistItem);
    }

    @PostMapping("/memorylistItem/addTitleAction")
    public ResponseEntity<?> saveTitleAction(@RequestParam UUID uuid, @RequestBody TitleAction titleAction) {
        MemorylistItem memorylistItem = memorylistItemRepository.getById(uuid);
        memorylistItem.addTitleAction(titleAction);
        titleAction.setMemorylistItem(memorylistItem);
        titleActionRepository.save(titleAction);
        return ResponseEntity.ok(titleAction);
    }

    @PostMapping("/memorylistItem/addItemAction")
    public ResponseEntity<?> saveItemAction(@RequestParam UUID uuid, @RequestBody ItemAction itemAction) {
        MemorylistItem memorylistItem = memorylistItemRepository.getById(uuid);
        memorylistItem.addItemAction(itemAction);
        itemAction.setMemorylistItem(memorylistItem);
        itemActionRepository.save(itemAction);
        return ResponseEntity.ok(itemAction);
    }

    @DeleteMapping("/deleteMemorylistItem/{uuid}")
    public ResponseEntity deleteMemorylistItem(@PathVariable UUID uuid) {
        memorylistItemRepository.deleteById(uuid);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/memorylist/latestForUser/{username}")
    public ResponseEntity<?> getLatestUserlistsForUsername(@PathVariable String username) {
        return ResponseEntity.ok(memorylistRepository.findAllByUsernameOrderByLastChangeDesc(username));
    }

    @GetMapping("/memorylistItem/forMemorylist/{uuid}")
    public ResponseEntity<?> getSavedItemsForItemlist(@PathVariable UUID uuid) {
        Memorylist memorylist = memorylistRepository.getById(uuid);
        List<MemorylistItem> memorylistItems = memorylistItemRepository.findAllByMemorylist(memorylist);
        return ResponseEntity.ok(memorylistItems);
    }


    */
}
