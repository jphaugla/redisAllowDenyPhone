package com.redis.allowDeny.controller;

import com.redis.allowDeny.domain.FromTo;
import com.redis.allowDeny.strategy.ScanStrategy;
import com.redis.allowDeny.strategy.impl.Scan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.redis.allowDeny.service.AllowDenyService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/")
@RestController
public class AllowDenyRestController {

    @Inject
    AllowDenyService allowDenyService;


    @GetMapping("/status")
    public String status() {
        return "OK";
    }
    @GetMapping("/key")
    public FromTo getKey(
            @RequestParam(name="from")String from,
            @RequestParam(name="to")String to,
            @RequestParam(name="product")String product){
        return allowDenyService.returnFromTo(from, to, product);
    }
    @PostMapping("/postFromTo")
    public String postTicker(@RequestBody FromTo fromTo) throws IOException {
        return allowDenyService.createFromTo(fromTo);
    }
    @GetMapping("/reload")
    public String reloadData() {
        return allowDenyService.reloadData(100,FromTo.getPREFIX() + '*');
    }


}
