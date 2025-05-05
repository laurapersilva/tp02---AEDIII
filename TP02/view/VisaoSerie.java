package TP02.view;

import TP02.model.Episodio;
import TP02.model.Serie;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class VisaoSerie {
    private Scanner sc;

    public VisaoSerie(Scanner sc) {
        this.sc = sc;
    }

    public Serie leSerie(boolean alterar) {
        if (alterar) {
            System.out.println("\n=== Alterar Série ===");
         } else {
            System.out.println("\n=== Criar Nova Série ===");
        }
        System.out.print("Título: ");
        String titulo = sc.nextLine();
        
        System.out.print("Ano de Lançamento: ");
        long anoLancamento = sc.nextLong();
        sc.nextLine(); // Limpar buffer
        
        System.out.print("Sinopse: ");
        String sinopse = sc.nextLine();
        
        // Calcular automaticamente o tamanho da sinopse
        int sinopseSize = sinopse.length();

        System.out.println("\nEscolha a plataforma de streaming:");
        System.out.println("1) Netflix");
        System.out.println("2) Amazon Prime Video");
        System.out.println("3) Max");
        System.out.println("4) Disney Plus");
        System.out.println("5) Globo Play");
        System.out.println("6) Star Plus");
        System.out.print("Escolha: ");
        int streamingOpcao = sc.nextInt();
        sc.nextLine(); // Limpar buffer

        String streaming = switch (streamingOpcao) {
            case 1 -> "Netflix";
            case 2 -> "Amazon Prime Video";
            case 3 -> "Max";
            case 4 -> "Disney Plus";
            case 5 -> "Globo Play";
            case 6 -> "Star Plus";
            default -> "Desconhecido";
        };
        
        // Confirmar criação
        System.out.println("\nConfirme os dados da série:");
        System.out.println("Título: " + titulo);
        System.out.println("Ano de Lançamento: " + anoLancamento);
        System.out.println("Sinopse: " + sinopse + " (" + sinopseSize + " caracteres)");
        System.out.println("Plataforma: " + streaming);
        
        System.out.print("\nConfirmar criação? (S/N): ");
        String confirmacao = sc.nextLine().toUpperCase();
        
        if (confirmacao.equals("S")) {
            return new Serie(titulo, anoLancamento, sinopseSize, sinopse, streaming);
        } else {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
    }

    public void mostraSerie(Serie serie) {
        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }

        System.out.println("\n" + serie.toString());
    }
    
    public void mostraResumoTemporadas(Serie serie, HashMap<Integer, Integer> episodiosPorTemporada, int totalEpisodios, int totalTemporadas) {
        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }
        
        System.out.println("\n=== Resumo da Série: " + serie.getTitulo() + " ===");
        System.out.println("Total de episódios: " + totalEpisodios);
        System.out.println("Total de temporadas: " + totalTemporadas);
        
        System.out.println("\nDistribuição de episódios por temporada:");
        if (episodiosPorTemporada.isEmpty()) {
            System.out.println("Não há episódios cadastrados para esta série.");
            return;
        }
        
        // Ordenar as temporadas para exibição
        ArrayList<Integer> temporadas = new ArrayList<>(episodiosPorTemporada.keySet());
        Collections.sort(temporadas);
        
        for (Integer temporada : temporadas) {
            int numEpisodios = episodiosPorTemporada.get(temporada);
            System.out.println("Temporada " + temporada + ": " + numEpisodios + " episódio(s)");
        }
    }

    public void mostraEpisodiosPorTemporada(ArrayList<Episodio> episodios, int temporada) {
        System.out.println("\n==== Episódios da Temporada " + temporada + " ====");
        if (episodios.isEmpty()) {
            System.out.println("Nenhum episódio encontrado para esta temporada.");
            return;
        }

        for (Episodio ep : episodios) {
            System.out.println(ep.toString());
        }
    }
    
    public void mostraTodosEpisodiosPorTemporada(Serie serie, HashMap<Integer, ArrayList<Episodio>> episodiosPorTemporada) {
        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }
        
        System.out.println("\n====================================");
        System.out.println("   " + serie.getTitulo() + " (" + serie.getAno() + ")");
        System.out.println("====================================");
        System.out.println("Plataforma: " + serie.getPlataforma());
        System.out.println("Sinopse: " + serie.getSinopse());
        
        if (episodiosPorTemporada.isEmpty()) {
            System.out.println("\nNenhum episódio cadastrado para esta série.");
            return;
        }
        
        // Ordenar as temporadas para exibição
        ArrayList<Integer> temporadas = new ArrayList<>(episodiosPorTemporada.keySet());
        Collections.sort(temporadas);
        
        for (Integer temporada : temporadas) {
            ArrayList<Episodio> episodios = episodiosPorTemporada.get(temporada);
            
            System.out.println("\n=== TEMPORADA " + temporada + " ===");
            System.out.println("Total: " + episodios.size() + " episódio(s)");
            
            // Ordenar os episódios por ID
            episodios.sort((ep1, ep2) -> ep1.getId() - ep2.getId());
            
            for (Episodio ep : episodios) {
                System.out.println("\nEpisódio " + ep.getId() + ": " + ep.getTitulo());
                System.out.println("Data: " + ep.getLancamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                System.out.println("Duração: " + ep.getDuracaoMin() + " minutos");
            }
        }
    }

    public int leIdSerie() {
        System.out.print("Digite o ID da série: ");
        int id = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        return id;
    }

    public int leTemporada() {
        System.out.print("Digite a temporada desejada: ");
        int temporada = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        return temporada;
    }
    
    public String leTermoBusca() {
        System.out.print("Digite o termo de busca: ");
        return sc.nextLine();
    }
    
    public void mostraResultadoBuscaSeries(ArrayList<Serie> series) {
        if (series.isEmpty()) {
            System.out.println("\nNenhuma série encontrada com o termo informado.");
            return;
        }
        
        System.out.println("\n=== Séries Encontradas ===");
        System.out.println("Total: " + series.size() + " série(s)");
        
        for (Serie serie : series) {
            System.out.println("\nID: " + serie.getId() + " | " + serie.getTitulo() + " (" + serie.getAno() + ")");
            System.out.println("Plataforma: " + serie.getPlataforma());
        }
    }
    
    public int selecionaSerieDoResultado(ArrayList<Serie> series) {
        if (series.isEmpty()) {
            return -1;
        }
        
        if (series.size() == 1) {
            System.out.println("\nSérie selecionada automaticamente: " + series.get(0).getTitulo());
            return series.get(0).getId();
        }
        
        System.out.print("\nDigite o ID da série que deseja selecionar (0 para cancelar): ");
        int id = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        // Verificar se o ID está na lista
        if (id != 0) {
            boolean idExiste = false;
            for (Serie serie : series) {
                if (serie.getId() == id) {
                    idExiste = true;
                    break;
                }
            }
            
            if (!idExiste) {
                System.out.println("ID inválido! Por favor, selecione um ID da lista apresentada.");
                return selecionaSerieDoResultado(series); // Recursão para nova tentativa
            }
        }
        
        return id;
    }
}
