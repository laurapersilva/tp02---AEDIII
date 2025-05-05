package TP02.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import TP02.model.*;

public class VisaoEpisodio {
    private Scanner sc;

    public VisaoEpisodio(Scanner sc) {
        this.sc = sc;
    }

    public Episodio leEpisodio(int idSerie, boolean alterar) {
        if (alterar) {
            System.out.println("\n=== Alterar Episódio ===");
        } else {
            System.out.println("\n=== Criar Novo Episódio ===");
        }
        System.out.print("Título: ");
        String titulo = sc.nextLine();
        
        System.out.print("Número da Temporada: ");
        int temporada = sc.nextInt();
        sc.nextLine(); // Limpar buffer

        LocalDate dataLancamento = null;
        boolean dataValida = false;

        while (!dataValida) {
            try {
                System.out.print("Data de lançamento (DD/MM/AAAA): ");
                String data = sc.nextLine();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dataLancamento = LocalDate.parse(data, dtf);
                dataValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Tente novamente.");
            }
        }

        System.out.print("Duração (em minutos): ");
        long duracao = sc.nextLong();
        sc.nextLine(); // Limpar buffer

        // Confirmar a criação
        System.out.println("\nConfirme os dados do episódio:");
        System.out.println("Título: " + titulo);
        System.out.println("Temporada: " + temporada);
        System.out.println("Data de lançamento: " + dataLancamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("Duração: " + duracao + " minutos");
        System.out.println("ID da Série: " + idSerie);
        
        System.out.print("\nConfirmar criação? (S/N): ");
        String confirmacao = sc.nextLine().toUpperCase();
        
        if (confirmacao.equals("S")) {
            return new Episodio(titulo, temporada, dataLancamento, duracao, idSerie);
        } else {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
    }

    public void mostraEpisodio(Episodio ep) {
        if (ep == null) {
            System.out.println("Episódio não encontrado.");
            return;
        }

        System.out.println("\n" + ep.toString());
    }

    public void mostraListaEpisodios(ArrayList<Episodio> episodios) {
        if (episodios.isEmpty()) {
            System.out.println("Nenhum episódio encontrado.");
            return;
        }

        System.out.println("\n=== Lista de Episódios ===\n");
        System.out.println("Total de episódios: " + episodios.size());
        
        for (Episodio ep : episodios) {
            System.out.println("\n" + ep.toString());
        }
    }

    public int leIdEpisodio() {
        System.out.print("Digite o ID do episódio: ");
        int id = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        return id;
    }
    
    public String leTermoBusca() {
        System.out.print("Digite o termo de busca para o episódio: ");
        return sc.nextLine();
    }
    
    public void mostraResultadoBuscaEpisodios(ArrayList<Episodio> episodios) {
        if (episodios.isEmpty()) {
            System.out.println("\nNenhum episódio encontrado com o termo informado.");
            return;
        }
        
        System.out.println("\n=== Episódios Encontrados ===");
        System.out.println("Total: " + episodios.size() + " episódio(s)");
        
        for (Episodio ep : episodios) {
            System.out.println("\nID: " + ep.getId() + " | " + ep.getTitulo());
            System.out.println("Temporada: " + ep.getNumTemporada() + " | ID da Série: " + ep.getSerieId());
            System.out.println("Lançamento: " + ep.getLancamento() + " | Duração: " + ep.getDuracaoMin() + " min");
        }
    }
    
    public int selecionaEpisodioDoResultado(ArrayList<Episodio> episodios) {
        if (episodios.isEmpty()) {
            return -1;
        }
        
        if (episodios.size() == 1) {
            System.out.println("\nEpisódio selecionado automaticamente: " + episodios.get(0).getTitulo());
            return episodios.get(0).getId();
        }
        
        System.out.print("\nDigite o ID do episódio que deseja selecionar (0 para cancelar): ");
        int id = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        // Verificar se o ID está na lista
        if (id != 0) {
            boolean idExiste = false;
            for (Episodio ep : episodios) {
                if (ep.getId() == id) {
                    idExiste = true;
                    break;
                }
            }
            
            if (!idExiste) {
                System.out.println("ID inválido! Por favor, selecione um ID da lista apresentada.");
                return selecionaEpisodioDoResultado(episodios); // Recursão para nova tentativa
            }
        }
        
        return id;
    }
}

