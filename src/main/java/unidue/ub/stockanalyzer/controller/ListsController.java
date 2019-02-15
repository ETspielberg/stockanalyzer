package unidue.ub.stockanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import unidue.ub.stockanalyzer.settingsrepositories.ItemlistRepository;
import unidue.ub.stockanalyzer.settingsrepositories.SavedItemRepository;

import java.util.UUID;

@Controller
public class ListsController {

    private final ItemlistRepository itemlistRepository;

    private final SavedItemRepository savedItemRepository;

    @Autowired
    public ListsController(ItemlistRepository itemlistRepository,
            SavedItemRepository savedItemRepository) {
        this.itemlistRepository = itemlistRepository;
        this.savedItemRepository = savedItemRepository;
    }

    @GetMapping("/itemlist/forUser/{username}")
    public ResponseEntity<?> getUserlistsForUsername(@PathVariable String username) {
        return ResponseEntity.ok(itemlistRepository.findAllByUsernameOrderByName(username));
    }

    @GetMapping("itemlist/latestForUser/{username}")
    public ResponseEntity<?> getLatestUserlistsForUsername(@PathVariable String username) {
        return ResponseEntity.ok(itemlistRepository.findAllByUsernameOrderByLastChangeDesc(username));
    }

    @GetMapping("/savedItem/forItemlist/{itemlist}")
    public ResponseEntity<?> getSavedItemsForItemlist(@PathVariable UUID itemlist) {
        return ResponseEntity.ok(savedItemRepository.findAllByItemlist(itemlist));
    }
}
