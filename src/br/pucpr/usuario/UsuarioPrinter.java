package br.pucpr.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.GroupLayout.Alignment;

public class UsuarioPrinter {
    public record Usuario(Long id, String nome, String email, String cpf) {
    }

    public void print(ArrayList<Usuario> lista, boolean maskCpf, boolean alignRight, String theme) {
        if (lista != null && !lista.isEmpty()) {
            var borderChar = "=";
            if (Objects.equals(theme, "DARK")) {
                borderChar = "#";
            } else if (Objects.equals(theme, "LIGHT")) {
                borderChar = "-";
            }

            // Borda superior e cabeçalho
            var sb = new StringBuilder();
            sb.repeat(borderChar, 74).append("\n");
            sb.append(String.format("| %-5s | %-20s | %-22s | %-14s |\n", "ID", "NOME", "EMAIL", "CPF"));
            sb.repeat(borderChar, 74).append("\n");
            for (var u : lista) {
                if (u != null) {
                    //Formatação do nome
                    var n = u.nome();
                    if (n == null || n.isEmpty()) {
                        n = "NÃO INFORMADO";
                    } else if (n.length() > 20) {
                        n = n.substring(0, 17) + "...";
                    }

                    // Formatação do email
                    var e = u.email();
                    if (e == null || !e.contains("@")) {
                        e = "INVALIDO";
                    }

                    // Formatação do CPF
                    var c = u.cpf();
                    if (c != null && c.length() == 11) {
                        if (maskCpf) {
                            c = "***." + c.substring(3, 6) + "." + c.substring(6, 9) + "-**";
                        } else {
                            c = c.substring(0, 3) + "." + c.substring(3, 6) + "." + c.substring(6, 9) + "-" + c.substring(9, 11);
                        }
                    } else {
                        c = "CPF INVALIDO";
                    }

                    var idStr = u.id() != null ? u.id().toString() : "0";
                    sb.append(String.format("| %-5s | %-20s | %-22s | %-14s |\n", idStr, n, e, c));
                }

                //Borda inferior
                sb.repeat(borderChar, 74).append("\n");

                //Espaçamento
                if (alignRight) {
                    var lines = sb.toString().split("\n");
                    for (var line : lines) {
                        System.out.println("                    " + line);
                    }
                } else {
                    System.out.print(sb);
                }
            }
        } else {
            System.out.println("ERRO: Lista de usuários vazia ou nula.");
        }
    }

    public static void main(String[] args) {
        var usuarios = new ArrayList<Usuario>();
        usuarios.add(new Usuario(101L, "Carlos Eduardo de Souza", "carlos.souza@email.com", "12345678901"));
        usuarios.add(new Usuario(102L, "Ana Maria Silva", "ana.silva@email.com", "98765432100"));
        usuarios.add(new Usuario(103L, "João Pedro de Alcântara Bragança", "joao.pedro@email.com", "45678912345"));
        usuarios.add(new Usuario(104L, "Mariana Costa", "marianacosta.email.com", "11122233344"));
        usuarios.add(new Usuario(105L, "Lucas Mendes", "lucas@email.com", "12345"));
        usuarios.add(new Usuario(106L, "", "beatriz@email.com", "55566677788"));

        var printer = new UsuarioPrinter();package br.pucpr.usuario;

import java.util.List;
import java.util.Objects;

public class UsuarioPrinter {

    // --- DOMÍNIO & OBJETOS DE VALOR ---
    public record Usuario(Long id, String nome, String email, String cpf) {}

    public enum Theme {
        DARK("#"),
        LIGHT("-"),
        DEFAULT("=");

        private final String borderChar;

        Theme(String borderChar) {
            this.borderChar = borderChar;
        }

        public String getBorderChar() {
            return borderChar;
        }
    }

    public enum Alignment {
        LEFT(""),
        RIGHT("                    "); // 20 espaços

        private final String indentation;

        Alignment(String indentation) {
            this.indentation = indentation;
        }

        public String getIndentation() {
            return indentation;
        }
    }

    // --- CONFIGURAÇÃO DE IMPRESSÃO (Substitui Flag Arguments) ---
    public record PrintConfig(Theme theme, Alignment alignment, boolean maskCpf) {
        public static PrintConfig ofDefault() {
            return new PrintConfig(Theme.DEFAULT, Alignment.LEFT, false);
        }
    }

    // --- FORMATADORES DE DADOS ---
    public static class UserFieldFormatter {
        public static String formatId(Long id) {
            return id != null ? id.toString() : "0";
        }

        public static String formatNome(String nome) {
            if (nome == null || nome.isBlank()) {
                return "NÃO INFORMADO";
            }
            if (nome.length() > 20) {
                return nome.substring(0, 17) + "...";
            }
            return nome;
        }

        public static String formatEmail(String email) {
            if (email == null || !email.contains("@")) {
                return "INVALIDO";
            }
            return email;
        }

        public static String formatCpf(String cpf, boolean maskCpf) {
            if (cpf == null || cpf.length() != 11) {
                return "CPF INVALIDO";
            }
            if (maskCpf) {
                return "***." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-**";
            }
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
        }
    }

    // --- RENDERIZADOR DE TABELA ---
    public static class TableRenderer {
        private static final int TABLE_WIDTH = 74;
        private static final String ROW_FORMAT = "| %-5s | %-20s | %-22s | %-14s |\n";

        public String render(List<Usuario> usuarios, PrintConfig config) {
            if (usuarios == null || usuarios.isEmpty()) {
                return "ERRO: Lista de usuários vazia ou nula.\n";
            }

            var border = config.theme().getBorderChar().repeat(TABLE_WIDTH) + "\n";
            var rawTable = new StringBuilder();

            rawTable.append(border);
            rawTable.append(String.format(ROW_FORMAT, "ID", "NOME", "EMAIL", "CPF"));
            rawTable.append(border);

            for (var u : usuarios) {
                if (u != null) {
                    var idStr = UserFieldFormatter.formatId(u.id());
                    var nomeStr = UserFieldFormatter.formatNome(u.nome());
                    var emailStr = UserFieldFormatter.formatEmail(u.email());
                    var cpfStr = UserFieldFormatter.formatCpf(u.cpf(), config.maskCpf());

                    rawTable.append(String.format(ROW_FORMAT, idStr, nomeStr, emailStr, cpfStr));
                }
            }
            rawTable.append(border);

            return applyIndentation(rawTable.toString(), config.alignment());
        }

        private String applyIndentation(String text, Alignment alignment) {
            if (alignment == Alignment.LEFT) {
                return text;
            }
            var indented = new StringBuilder();
            for (var line : text.split("\n")) {
                indented.append(alignment.getIndentation()).append(line).append("\n");
            }
            return indented.toString();
        }
    }

    // --- PONTO DE ENTRADA PRINCIPAL ---
    private final TableRenderer renderer = new TableRenderer();

    public void print(List<Usuario> lista, PrintConfig config) {
        var output = renderer.render(lista, config);
        System.out.print(output);
    }

    public static void main(String[] args) {
        var usuarios = List.of(
            new Usuario(101L, "Carlos Eduardo de Souza", "carlos.souza@email.com", "12345678901"),
            new Usuario(102L, "Ana Maria Silva", "ana.silva@email.com", "98765432100"),
            new Usuario(103L, "João Pedro de Alcântara Bragança", "joao.pedro@email.com", "45678912345"),
            new Usuario(104L, "Mariana Costa", "marianacosta.email.com", "11122233344"),
            new Usuario(105L, "Lucas Mendes", "lucas@email.com", "12345"),
            new Usuario(106L, "", "beatriz@email.com", "55566677788")
        );

        var printer = new UsuarioPrinter();
        var config = new PrintConfig(Theme.LIGHT, Alignment.RIGHT, true);
        
        printer.print(usuarios, config);
    }
}
        printer.print(usuarios, true, true, "LIGHT");
    }
}