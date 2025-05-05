package TP02.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import TP02.model.*;
import TP02.service.*;
import TP02.view.*;

public class ControleAtor {
    private Scanner sc;
    private Arquivo<Ator> arqAtor;
    private VisaoAtor visaoAtor;
    private RelacionamentoSerieAtor relacionamento;

    public ControleAtor(Scanner sc, Arquivo<Ator> arqAtor, Arquivo<Serie> arqSerie) {
        this.sc = sc;
        this.arqAtor = arqAtor;
        this.visaoAtor = new VisaoAtor(sc);
        this.relacionamento = new RelacionamentoSerieAtor(arqSerie, arqAtor);
    }

    public void menuSerie() throws Exception {
        int op;
        do {
            System.out.println("\nPUCFlix 1.0\n-----------\n> Início > Ator");
            System.out.println("1) Incluir novo ator");
            System.out.println("2) Buscar ator");
            System.out.println("3) Alterar ator");
            System.out.println("4) Excluir ator");
            System.out.println("5) Visualizar séries de ator");
            System.out.println("0) Retornar ao menu anterior");
            System.out.print("Escolha uma opção: ");

            op = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (op) {
                case 1 -> incluirAtor();
                case 2 -> buscarAtorPorNome();
                case 3 -> alterarAtorPorNome();
                case 4 -> excluirAtorPorNome();
                case 5 -> visualizarAtorComSeries();
                default -> {
                    if (op != 0) {
                        System.out.println("Opção inválida!");
                    }
                }
            }
        } while (op != 0);
    }

    private void incluirAtor() throws Exception {
        Ator ator = visaoAtor.leSerie(false);
        if (ator == null) {
            System.out.println("Operação cancelada.");
            return;
        }
        int id = arqAtor.create(ator);
        System.out.println("Ator adicionado com sucesso! ID: " + id);
    }

    private void buscarAtorPorId() throws Exception {
        int id = visaoAtor.leIdSerie();
        Ator ator = arqAtor.read(id);
        visaoAtor.mostraAtor(ator);
    }
    
    private void buscarAtorPorNome() throws Exception {
        String termoBusca = visaoAtor.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Ator> resultados = relacionamento.buscarAtorPorNome(termoBusca);
        
        // Exibir resultados
        visaoAtor.mostraResultadoBuscaAtor(resultados);
        
        // Se houver resultados, perguntar se quer selecionar uma série
        if (!resultados.isEmpty()) {
            System.out.print("\nDeseja selecionar um ator para ver mais detalhes? (S/N): ");
            String resposta = sc.nextLine().toUpperCase();
            
            if (resposta.equals("S")) {
                int idSelecionado = visaoAtor.selecionaAtorDoResultado(resultados);
                
                if (idSelecionado > 0) {
                    Ator atorSelecionado = arqAtor.read(idSelecionado);
                    visaoAtor.mostraAtor(atorSelecionado);
                }
            }
        }
    }

    private void alterarAtorPorNome() throws Exception {
        String termoBusca = visaoAtor.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Ator> resultados = relacionamento.buscarAtorPorNome(termoBusca);
        
        // Exibir resultados
        visaoAtor.mostraResultadoBuscaAtor(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar série para alterar
        System.out.print("\nDigite o ID do ator que deseja alterar (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        for (Ator a : resultados) {
            if (a.getId() == idSelecionado) {
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        // Obter série atual
        Ator atorAtual = arqAtor.read(idSelecionado);
        System.out.println("\nDados atuais do ator:");
        visaoAtor.mostraAtor(atorAtual);
        
        // Obter novos dados
        Ator atorNovo = visaoAtor.leAtor(true);
        if (atorNovo == null) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        atorNovo.setId(idSelecionado);
        boolean resultado = arqAtor.update(atorNovo);
        System.out.println(resultado ? "Ator atualizado com sucesso!" : "Erro ao atualizar ator.");
    }
    
    // Mantido para uso interno
    private void alterarAtor(int id) throws Exception {
        // Verificar se a série existe
        Ator atorAtual = arqAtor.read(id);
        if (atorAtual == null) {
            System.out.println("Ator não encontrada.");
            return;
        }
        
        System.out.println("\nDados atuais do ator:");
        visaoAtor.mostraAutor(atorAtual);
        
        Ator atorNovo = visaoAtor.leAtor(true);
        if (atorNovo == null) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        atorNovo.setId(id);
        boolean resultado = arqAtor.update(atorNovo);
        System.out.println(resultado ? "Ator atualizado com sucesso!" : "Erro ao atualizar ator.");
    }

    private void excluirAtorPorNome() throws Exception {
        String termoBusca = visaoAtor.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Serie> resultados = relacionamento.buscarAtorPorNome(termoBusca);
        
        // Exibir resultados
        visaoAtor.mostraResultadoBuscaAtor(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar série para excluir
        System.out.print("\nDigite o ID do autor que deseja excluir (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        Ator atorSelecionado = null;
        for (Ator a : resultados) {
            if (a.getId() == idSelecionado) {
                encontrado = true;
                atorSelecionado = a;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        // Continuar com a exclusão usando o ID selecionado
        excluirAtor(idSelecionado);
    }
    
    // Mantido para uso interno
    private void excluirAtor(int id) throws Exception {
        // Verificar se a série existe
        Ator ator = arqAtor.read(id);
        if (ator == null) {
            System.out.println("Ator não encontrado.");
            return;
        }

        // Verificar se existem episódios vinculados à série usando a árvore B+
        if (relacionamento.atorTemSeries(id)) {
            System.out.println("Não é possível excluir! Existem series vinculados a este ator.");
            System.out.println("Por favor, exclua todas séries desse ator antes de excluí-lo.");
            
            // Mostrar a quantidade de episódios vinculados
            int totalSeries = relacionamento.getTotalSeries(id);
            System.out.println("Total de séries vinculadas: " + totalSeries);
            return;
        }
        
        // Confirmar exclusão
        System.out.print("\nConfirmar exclusão do ator \"" + ator.getNome() + "\"? (S/N): ");
        String confirmacao = sc.nextLine().toUpperCase();
        if (!confirmacao.equals("S")) {
            System.out.println("Operação cancelada.");
            return;
        }

        boolean resultado = arqAtor.delete(id);
        System.out.println(resultado ? "Ator excluída com sucesso!" : "Erro ao excluir ator.");
    }
    
    private void visualizarAtorComSeries() throws Exception {
        String termoBusca = visaoAtor.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Ator> resultados = relacionamento.buscarAtorPorNome(termoBusca);
        
        // Exibir resultados
        visaoAtor.mostraResultadoBuscaAtor(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar série para visualizar
        System.out.print("\nDigite o ID do ator que deseja visualizar (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        for (Ator a : resultados) {
            if (a.getId() == idSelecionado) {
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        // Obter série atual
        Ator ator = arqAtor.read(idSelecionado);
        
        // Verificar se a série possui episódios
        if (!relacionamento.atorTemSerie(idSelecionado)) {
            System.out.println("\nO ator \"" + ator.getNome() + "\" não possui séries cadastradas.");
            return;
        }
        
        visaoAtor.mostraTodasSeries(ator, serie);
    }


}