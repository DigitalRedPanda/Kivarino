package com.digiunion.kick.util;

import java.util.regex.Pattern;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;

public interface Slugify {
  static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
  static final Pattern WHITESPACE = Pattern.compile("[\\s_]");

  public static String slugify(String input) {
    String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
    String normalized = Normalizer.normalize(noWhitespace, Form.NFD);
    String latinOnly = NONLATIN.matcher(normalized).replaceAll("");
    return latinOnly.toLowerCase(Locale.ENGLISH);
  } 
}
