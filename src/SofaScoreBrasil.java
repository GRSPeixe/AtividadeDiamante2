import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SofaScoreBrasil {

    /*
     * Classe times para criar variáveis que vão armazenar as informações dos times
     */
    static class Time {
        String nome;
        int pontos = 0;
        int vitorias = 0;
        int empates = 0;
        int derrotas = 0;
        int golsPro = 0;
        int golsContra = 0;

        public Time(String nome) {
            this.nome = nome;
        }

        int saldoGols() {
            return golsPro - golsContra;
        }

        /* Tarefa 3: Método getCategoria */
        String getCategoria() {
            int p = pontos;
            switch (p) {
                case 20, 21, 22, 23, 24, 25 -> {
                    return "LÍDER";
                }
                default -> {
                    if (p >= 14)
                        return "G4";
                    else if (p >= 8)
                        return "MEIO DE TABELA";
                    else if (p >= 4)
                        return "ALERTA";
                    else
                        return "REBAIXAMENTO";
                }
            }
        }
    }

    /* Tarefa 1: Métodos parsearPartida */
    private static String[] parsearPartida(String linha) {
        String[] campos = linha.split(":");
        if (campos.length != 4) {
            System.out.println("[ERRO] Linha inválida: " + linha);
            return null;
        }

        String timeA = campos[0].trim().toUpperCase();
        String golsA = campos[1].trim();
        String golsB = campos[2].trim();
        String timeB = campos[3].trim().toUpperCase();

        // Validar os dados
        if (timeA.isEmpty() || timeB.isEmpty() || !golsA.matches("\\d+") || !golsB.matches("\\d+")) {
            System.out.println("[ERRO] Linha inválida: " + linha);
            return null;
        }

        return new String[] { timeA, golsA, golsB, timeB };
    }

    /* Método Main para rodar o projeto */
    public static void main(String[] args) {

        String[] partidas = {
                "Flamengo:3:1:Palmeiras",
                "Corinthians:0:0:São Paulo",
                "Atletico-MG:2:2:Fluminense",
                "Palmeiras:1:0:Corinthians",
                "São Paulo:3:2:Flamengo",
                "Fluminense:0:1:Atletico-MG",
                "Flamengo:2:0:Corinthians",
                "Palmeiras:4:1:Fluminense",
                "São Paulo:0:0:Atletico-MG",
                "Corinthians:1:3:Fluminense",
                "Atletico-MG:0:2:Flamengo",
                "Fluminense:1:1:São Paulo"
        };

        Map<String, Time> tabela = new HashMap<>();

        for (String partida : partidas) {
            String[] dados = parsearPartida(partida);
            if (dados != null) {
            }
        }

        /* Tarefa 2: Processar partidas */
        int resultado = 0;

        while (resultado < partidas.length) {

            String linha = partidas[resultado];

            String[] dados = parsearPartida(linha);
            if (dados == null) {
                resultado++;
                continue;
            }

            String casa = dados[0];
            int golsCasa = Integer.parseInt(dados[1]);
            int golsFora = Integer.parseInt(dados[2]);
            String fora = dados[3];

            tabela.putIfAbsent(casa, new Time(casa));
            tabela.putIfAbsent(fora, new Time(fora));

            Time timeCasa = tabela.get(casa);
            Time timeFora = tabela.get(fora);

            timeCasa.golsPro += golsCasa;
            timeCasa.golsContra += golsFora;

            timeFora.golsPro += golsFora;
            timeFora.golsContra += golsCasa;

            if (golsCasa > golsFora) {
                timeCasa.pontos += 3;
                timeCasa.vitorias++;
                timeFora.derrotas++;
            } else if (golsCasa < golsFora) {
                timeFora.pontos += 3;
                timeFora.vitorias++;
                timeCasa.derrotas++;
            } else {
                timeCasa.pontos++;
                timeFora.pontos++;
                timeCasa.empates++;
                timeFora.empates++;
            }

            resultado++;
        }

        // TAREFA 4 - Análise com o Streams

        System.out.println("\n--- ANÁLISES ---");

        tabela.values().stream()
                .max(Comparator.comparingInt(t -> t.golsPro))
                .ifPresent(t -> System.out.println("Mais gols: " + t.nome + " (" + t.golsPro + ")"));

        double media = Arrays.stream(partidas)
                .map(SofaScoreBrasil::parsearPartida)
                .filter(Objects::nonNull)
                .mapToInt(p -> Integer.parseInt(p[1]) + Integer.parseInt(p[2]))
                .average()
                .orElse(0);

        System.out.println("Média de gols por partida: " + media);

        List<Time> rebaixados = tabela.values().stream()
                .filter(t -> t.pontos < 4)
                .collect(Collectors.toList());

        System.out.println("Rebaixamento:");
        rebaixados.forEach(t -> System.out.println("- " + t.nome));

        List<Time> classificacao = tabela.values().stream()
                .sorted(Comparator
                        .comparingInt((Time t) -> t.pontos).reversed()
                        .thenComparingInt(Time::saldoGols).reversed())
                .collect(Collectors.toList());

        /*TAREFA 5 - Relatório Final */
        System.out.println("\n=== CAMPEONATO BRASILEIRO 2026 ===");
        System.out.println("POS | TIME           | PTS | V | E | D | SG | CATEGORIA");
        System.out.println("-------------------------------------------------------");

        int pos = 1;

        // usando while (requisito)
        int i = 0;
        while (i < classificacao.size()) {
            Time t = classificacao.get(i);

            System.out.println(String.format(
                    "%3d | %-14s | %3d | %1d | %1d | %1d | %+3d | %s",
                    pos++,
                    t.nome,
                    t.pontos,
                    t.vitorias,
                    t.empates,
                    t.derrotas,
                    t.saldoGols(),
                    t.getCategoria()));

            i++;
        }
    }

}
