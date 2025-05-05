package TP02.view;

import TP02.model.Ator;
import TP02.model.Serie;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class VisaoAtor {
    private Scanner sc;

    public VisaoAtor(Scanner sc) {
        this.sc = sc;
    }

    public Ator leAtor(boolean alterar) {
        if (alterar) {
            System.out.println("\n=== Alterar Ator ===");
         } else {
            System.out.println("\n=== Criar Novo Ator ===");
        }
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        
        System.out.print("Idade: ");
        int idade = sc.nextInt();
        
        // Confirmar criação
        System.out.println("\nConfirme os dados do ator:");
        System.out.println("Nome: " + nome);
        System.out.println("Idade: " + idade);
        
        System.out.print("\nConfirmar criação? (S/N): ");
        String confirmacao = sc.nextLine().toUpperCase();
        
        if (confirmacao.equals("S")) {
            return new Ator(nome, idade);
        } else {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
    }

    public void mostraAtor(Ator ator) {
        if (ator == null) {
            System.out.println("Ator não encontrado.");
            return;
        }

        System.out.println("\n" + ator.toString());
    }
    
    
    
    public void mostraTodasSeries(Ator ator, HashMap<Integer, ArrayList<Serie>> seriePorAtor) {
        if (ator == null) {
            System.out.println("Série não encontrada.");
            return;
        }
        
        System.out.println("\n====================================");
        System.out.println("   " + ator.getNome() + " (" + ator.getIdade() + ")");
        System.out.println("====================================");
        
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
