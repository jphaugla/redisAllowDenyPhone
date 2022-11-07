package com.spring.redis.streams.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class FromTo implements Serializable {
    // keys
    private String from;
    private String to;
    private String product;
    private static String PREFIX="rle:";
    private static int FROM_KEY=1;
    private static int TO_KEY=2;
    private static int PRODUCT_KEY=3;
    public static String generateKey(String from, String to, String product) {
        return (PREFIX + from + ':' + to + ':' + product);
    }
    public static String getPREFIX() {
        return PREFIX;
    }
    public String getKey() {
        return (PREFIX + from + ':' + to + ':' + product);
    }
    public static String [] getKeys(String key) {

        return key.split(":");
    }
    public static String getFromKey(String key) {
        return getKeys(key)[FROM_KEY];
    }
    public static String getToKey(String key) {
        return getKeys(key)[TO_KEY];
    }
    public static String getProductKey(String key) {
        return getKeys(key)[PRODUCT_KEY];
    }
    // values
    private String decision;
    private String ruleId;
    private String reason;
    private static int DECISION_KEY=0;
    private static int RULE_ID_KEY=1;
    private static int REASON_KEY=2;
    public static String generateValue(String decision, String rule_id, String reason) {
        return (decision + ':' + rule_id + ':' + reason);
    }
    public String getValue() {
        return (decision + ':' + ruleId + ':' + reason);
    }
    public String [] getValues(String value) {
        return value.split(":");
    }
    public String getDecision(String value) {

        return getValues(value)[DECISION_KEY];
    }
    public String getRuleId(String value) {
        return getValues(value)[RULE_ID_KEY];
    }
    public String getReason(String value) {
        return getValues(value)[REASON_KEY];
    }
    public String getFromPrefix(String value) {

        return getFromKey(value).substring(0,4);
    }
    public FromTo(String key, String value) {
        String [] keys = getKeys(key);
        String [] values = getValues(value);
        this.from = keys[FROM_KEY];
        this.to = keys[TO_KEY];
        this.product = keys[PRODUCT_KEY];
        this.decision = values[DECISION_KEY];
        this.ruleId = values[RULE_ID_KEY];
        this.reason = values[REASON_KEY];
    }
}
