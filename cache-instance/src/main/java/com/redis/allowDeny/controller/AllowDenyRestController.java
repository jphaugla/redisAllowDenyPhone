package com.redis.allowDeny.controller;

import com.redis.allowDeny.domain.FromTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.redis.allowDeny.service.AllowDenyService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/")
@RestController
public class AllowDenyRestController {

    @Inject
    AllowDenyService allowDenyService;


    @GetMapping("/status")
    public String status() {
        log.info("this is the status check");
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
        return allowDenyService.reloadData(100,FromTo.getPREFIX() + '*', Boolean.TRUE);
    }

    @GetMapping("/dumpCache")
    public String dumpCache() {
        return allowDenyService.getAllHash();
    }

    @GetMapping("/checkCache")
    public HashMap<String,String> checkCache() {
        return allowDenyService.checkCache();
    }

    @GetMapping("/getCache")
    public HashMap<String, String> getCache(String key) {
        return allowDenyService.getCache(key);
    }

}
