package com.barber.api.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingTokenUtils {
  public static String generateToken(String email, Long bookingId) {
    try {
      Map<String, Object> data = new HashMap<>();
      data.put("email", email);
      data.put("bookingId", bookingId);

      long exp = System.currentTimeMillis() + (5 * 60 * 1000);
      data.put("exp", exp);

      String json = JsonUtils.convertJsonToString(data);

      return AESUtils.encrypt(json, true);
    } catch (Exception e) {
      log.error("===> Cannot generate token, {}", e.getMessage());
      return null;
    }
  }

  public static Map<String, Object> parseToken(String token) {
    try {
      String json = AESUtils.decrypt(token, true);
      return JsonUtils.convertJsonStringToMap(json);
    } catch (Exception e) {
      log.error("===> Invalid token, {}", e.getMessage());
      return null;
    }
  }
}
