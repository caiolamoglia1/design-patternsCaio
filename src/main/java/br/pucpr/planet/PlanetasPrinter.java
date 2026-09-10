package br.pucpr.planet;

import br.pucpr.user.Theme;
import java.util.ArrayList;

public class PlanetasPrinter {
  public void print(ArrayList<Planet> planets, boolean alignRight, Theme theme) {
    if (planets == null || planets.isEmpty()) {
      System.out.println("ERRO: Lista de planetas vazia ou nula.");
      return;
    }

    final var borderChar = theme.getBorderChar();
    final var header =
        String.format(
            "| %-10s | %10s | %15s | %15s | %-10s |%n",
            "Nome", "Diâmetro", "Dist. sol (km)", "Dist. sol (ua)", "Tipo");

    final var borderWidth = header.stripTrailing().length();
    var sb = new StringBuilder();
    sb.repeat(borderChar, borderWidth).append("\n");
    sb.append(header);
    sb.repeat(borderChar, borderWidth).append("\n");

    for (var planet : planets) {
      if (planet == null) {
        continue;
      }
      sb.append(
          String.format(
              "| %-10s | %,10.1f | %,15d | %,15.2f | %-10s |%n",
              formatName(planet.name()),
              planet.diameterKm(),
              planet.sunDistanceKm(),
              kmToAu(planet.sunDistanceKm()),
              formatType(planet.type())));
    }

    sb.repeat(borderChar, borderWidth).append("\n");

    if (alignRight) {
      var lines = sb.toString().split("\n");
      for (var line : lines) {
        System.out.println("                    " + line);
      }
    } else {
      System.out.print(sb);
    }
  }

  private static String formatName(String name) {
    if (name == null || name.isEmpty()) {
      return "NÃO INFORMADO";
    }
    if (name.length() > 10) {
      return name.substring(0, 7) + "...";
    }
    return name;
  }

  private static String formatType(PlanetType type) {
    if (type == null) {
      return "Desconhecido";
    }

    return switch (type) {
      case ROCK -> "Rochoso";
      case GAS -> "Gososo";
      case ICE -> "Gelado";
      case DWARF -> "Anão";
    };
  }

  private static double kmToAu(long km) {
    return km / 149_600_000.0;
  }
}
