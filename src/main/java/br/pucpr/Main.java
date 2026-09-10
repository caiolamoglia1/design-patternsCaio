package br.pucpr;

import static br.pucpr.planet.PlanetType.*;
import static br.pucpr.planet.PlanetType.DWARF;
import static br.pucpr.planet.PlanetType.ICE;
import static br.pucpr.table.Theme.LIGHT;

import br.pucpr.planet.Planet;
import br.pucpr.planet.PlanetaColumns;
import br.pucpr.table.Table;
import br.pucpr.table.TableBuilder;
import br.pucpr.table.model.ColumnTableData;
import br.pucpr.table.model.PaginatedTableData;
import br.pucpr.user.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Main {
  public static void main(String[] args)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final var usuarios = new ArrayList<User>();
    usuarios.add(
        new User(101L, "Carlos Eduardo de Souza", "carlos.souza@email.com", "12345678901"));
    usuarios.add(new User(102L, "Ana Maria Silva", "ana.silva@email.com", "98765432100"));
    usuarios.add(
        new User(103L, "João Pedro de Alcântara Bragança", "joao.pedro@email.com", "45678912345"));
    usuarios.add(new User(104L, "Mariana Costa", "marianacosta.email.com", "11122233344"));
    usuarios.add(new User(105L, "Lucas Mendes", "lucas@email.com", "12345"));
    usuarios.add(new User(106L, "", "beatriz@email.com", "55566677788"));

    final var usuariosData =
        new ColumnTableData<>(
            usuarios, new IdColumn(), new CpfColumn(), new EmailColumn(), new NameColumn());
    final var usuariosPaginados = new PaginatedTableData(usuariosData, 3);

    System.out.println("IMPRIMINDO USUARIOS");
    System.out.println("-------------------");
    new Table(usuariosPaginados, LIGHT, true)
        .print();

    usuariosData.addRow(new User(107L, "Paula Nunes", "paula.nunes@email.com", "33344455566"));
    usuariosPaginados.nextPage();

    final var planetas = new ArrayList<Planet>();
    planetas.add(new Planet("Mercúrio", 4879, 57_910_000L, ROCK));
    planetas.add(new Planet("Vênus", 12104, 108_200_000L, ROCK));
    planetas.add(new Planet("Terra", 12756, 149_600_000L, ROCK));
    planetas.add(new Planet("Marte", 6792, 227_940_000L, ROCK));
    planetas.add(new Planet("Júpiter", 142984, 778_330_000L, GAS));
    planetas.add(new Planet("Saturno", 120536, 1_429_400_000L, GAS));
    planetas.add(new Planet("Urano", 51118, 2_870_990_000L, ICE));
    planetas.add(new Planet("Netuno", 49528, 4_504_300_000L, ICE));
    planetas.add(new Planet("Plutão", 2376, 5_906_380_000L, DWARF));

    System.out.println();
    System.out.println("IMPRIMINDO PLANETAS");
    System.out.println("-------------------");
    var planetasPaginados = new PaginatedTableData(new ColumnTableData<>(planetas, PlanetaColumns.values()), 4);
    new Table(planetasPaginados).print();
    planetasPaginados.nextPage();

    System.out.println("PLANETAS REFLETIDOS");
    System.out.println("-------------------");

    new TableBuilder()
        .light()
        .rightAligned()
        .withData(
            planetas,
            c ->
                c.add(PlanetaColumns.values())
                    .add(
                        "Distance (au)",
                        p -> "%,11.2f".formatted(Planet.kmToAu(p.sunDistanceKm()))),
            3)
        .print();
  }
}
