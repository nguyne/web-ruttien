package web.ytbcash.wmoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ytbcash.wmoney.payload.request.BanksRequest;
import web.ytbcash.wmoney.services.BanksService;

import java.util.List;


@CrossOrigin(origins = {"https://ruttienytbcash.online", "https://wmoney-6857ea6bcb17.herokuapp.com"})
@RestController
@RequestMapping(value = "/api/banks")
public class BanksController {

    private final String INSERT_INTO = "22229999";

    @Autowired
    private BanksService banksService;

    @PostMapping(value = "/create/banks/" + INSERT_INTO)
    public ResponseEntity<?> createBanks(@RequestBody List<BanksRequest> request) {
        return new ResponseEntity<>(banksService.createListBanks(request), HttpStatus.CREATED);
    }
    @GetMapping("")
    public ResponseEntity<?> getAllBanks(@RequestHeader("Origin") String origin) {
        if (!isOriginAllowed(origin)) {
            return new ResponseEntity<>("Forbidden", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(banksService.getAllBanks(), HttpStatus.OK);
    }

    private boolean isOriginAllowed(String origin) {
        return (origin != null && (origin.equals("https://ruttienytbcash.online") || origin.equals("https://wmoney-6857ea6bcb17.herokuapp.com")));
    }
}
