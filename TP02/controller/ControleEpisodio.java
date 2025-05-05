package TP02.controller;

import java.util.ArrayList;
import java.util.Scanner;

import TP02.model.Episodio;
import TP02.model.Serie;
import TP02.service.Arquivo;
import TP02.service.ParIDSerieEpisodio;
import TP02.service.RelacionamentoSerieEpisodio;
import TP02.view.VisaoEpisodio;
import TP02.view.VisaoSerie;

public class ControleEpisodio {
    private Scanner sc;
    private Arquivo<Episodio> arqEpisodios;
    private Arquivo<Serie> arqSerie;
    private VisaoEpisodio visaoEpisodio;
    private VisaoSerie visaoSerie;
    private RelacionamentoSerieEpisodio relacionamento;

    public ControleEpisodio(Scanner sc, Arquivo<Episodio> arqEpisodios, Arquivo<Serie> arqSerie) {
        this.sc = sc;
        this.arqEpisodios = arqEpisodios;
        this.arqSerie = arqSerie;
        this.visaoEpisodio = new VisaoEpisodio(sc);
        this.visaoSerie = new VisaoSerie(sc);
        this.relacionamento = new RelacionamentoSerieEpisodio(arqSerie, arqEpisodios);
    }

    public void menuEpisodio() throws Exception {
        int op;
        do {
            System.out.println("\nPUCFlix 1.0\n-----------\n> Início > Episódios");
            System.out.println("1) Listar todas as séries");
            System.out.println("2) Gerenciar episódios de uma série específica");
            System.out.println("3) Buscar episódio por nome");
            System.out.println("0) Retornar ao menu anterior");
            System.out.print("Escolha uma opção: ");

            op = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (op) {
                case 1 -> listarSeries();
                case 2 -> gerenciarEpisodiosPorNome();
                case 3 -> buscarEpisodioPorNome();
                default -> {
                    if (op != 0) {
                        System.out.println("Opção inválida!");
                    }
                }
            }
        } while (op != 0);
    }

    private void listarSeries() throws Exception {
        System.out.println("\nSéries disponíveis:");
        int ultimoId = arqSerie.ultimoId();
        boolean temSeries = false;

        for (int i = 1; i <= ultimoId; i++) {
            Serie serie = arqSerie.read(i);
            if (serie != null) {
                System.out.println("ID: " + serie.getId() + " | Nome: " + serie.getTitulo());
                temSeries = true;
            }
        }

        if (!temSeries) {
            System.out.println("Não há séries cadastradas. Cadastre uma série primeiro.");
        }
    }

    private void gerenciarEpisodiosPorNome() throws Exception {
        String termoBusca = visaoSerie.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Serie> resultados = relacionamento.buscarSeriePorNome(termoBusca);
        
        // Exibir resultados
        visaoSerie.mostraResultadoBuscaSeries(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar série para gerenciar episódios
        System.out.print("\nDigite o ID da série para gerenciar seus episódios (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        Serie serieSelecionada = null;
        for (Serie s : resultados) {
            if (s.getId() == idSelecionado) {
                encontrado = true;
                serieSelecionada = s;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        gerenciarEpisodiosDeSerie(idSelecionado);
    }
    
    // Mantido para uso interno
    private void gerenciarEpisodiosDeSerie(int idSerie) throws Exception {
        Serie serie = arqSerie.read(idSerie);
        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }

        System.out.println("Gerenciando episódios da série: " + serie.getTitulo());

        int op;
        do {
            System.out.println("\nPUCFlix 1.0\n-----------\n> Início > Episódios > " + serie.getTitulo());
            System.out.println("1) Incluir novo episódio");
            System.out.println("2) Buscar episódio por nome");
            System.out.println("3) Alterar episódio");
            System.out.println("4) Excluir episódio");
            System.out.println("5) Listar todos os episódios desta série");
            System.out.println("0) Retornar ao menu anterior");
            System.out.print("Escolha uma opção: ");

            op = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (op) {
                case 1 -> incluirEpisodio(idSerie);
                case 2 -> buscarEpisodioNaSeriePorNome(idSerie);
                case 3 -> alterarEpisodioPorNome(idSerie);
                case 4 -> excluirEpisodioPorNome(idSerie);
                case 5 -> listarEpisodiosDaSerie(idSerie);
                default -> {
                    if (op != 0) {
                        System.out.println("Opção inválida!");
                    }
                }
            }
        } while (op != 0);
    }

    private void incluirEpisodio(int idSerie) throws Exception {
        // Verificar se a série existe
        Serie serie = arqSerie.read(idSerie);
        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }
        
        Episodio episodio = visaoEpisodio.leEpisodio(idSerie, false);
        if (episodio == null) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        int id = arqEpisodios.create(episodio);
        
        // Importante: atualiza o ID no objeto episódio após a criação
        episodio.setId(id);
        

        // Verifica se o relacionamento foi adicionado corretamente
        ParIDSerieEpisodio parTeste = new ParIDSerieEpisodio(idSerie, id);
        ArrayList<ParIDSerieEpisodio> paresEncontrados = relacionamento.arvoreRelacionamento.read(parTeste);
        
    }

    private void buscarEpisodioPorId() throws Exception {
        int id = visaoEpisodio.leIdEpisodio();
        Episodio episodio = arqEpisodios.read(id);
        if (episodio != null) {
            Serie serie = arqSerie.read(episodio.getSerieId());
            String nomeSerie = (serie != null) ? serie.getTitulo() : "Série não encontrada";
            System.out.println("Série: " + nomeSerie);
        }
        visaoEpisodio.mostraEpisodio(episodio);
    }
    
    private void buscarEpisodioPorNome() throws Exception {
        String termoBusca = visaoEpisodio.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca em todos os episódios
        ArrayList<Episodio> resultados = relacionamento.buscarEpisodioPorNome(termoBusca);
        
        // Exibir resultados
        visaoEpisodio.mostraResultadoBuscaEpisodios(resultados);
        
        // Se houver resultados, perguntar se quer selecionar um episódio
        if (!resultados.isEmpty()) {
            System.out.print("\nDeseja selecionar um episódio para ver mais detalhes? (S/N): ");
            String resposta = sc.nextLine().toUpperCase();
            
            if (resposta.equals("S")) {
                int idSelecionado = visaoEpisodio.selecionaEpisodioDoResultado(resultados);
                
                if (idSelecionado > 0) {
                    Episodio episodioSelecionado = arqEpisodios.read(idSelecionado);
                    
                    // Mostrar também o nome da série
                    if (episodioSelecionado != null) {
                        Serie serie = arqSerie.read(episodioSelecionado.getSerieId());
                        String nomeSerie = (serie != null) ? serie.getTitulo() : "Série não encontrada";
                        System.out.println("Série: " + nomeSerie);
                    }
                    
                    visaoEpisodio.mostraEpisodio(episodioSelecionado);
                }
            }
        }
    }

    private void buscarEpisodioNaSeriePorNome(int idSerie) throws Exception {
        String termoBusca = visaoEpisodio.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca em episódios da série específica
        ArrayList<Episodio> resultados = relacionamento.buscarEpisodioPorNomeEmSerie(termoBusca, idSerie);
        
        // Exibir resultados
        visaoEpisodio.mostraResultadoBuscaEpisodios(resultados);
        
        // Se houver resultados, perguntar se quer selecionar um episódio
        if (!resultados.isEmpty()) {
            System.out.print("\nDeseja selecionar um episódio para ver mais detalhes? (S/N): ");
            String resposta = sc.nextLine().toUpperCase();
            
            if (resposta.equals("S")) {
                int idSelecionado = visaoEpisodio.selecionaEpisodioDoResultado(resultados);
                
                if (idSelecionado > 0) {
                    Episodio episodioSelecionado = arqEpisodios.read(idSelecionado);
                    visaoEpisodio.mostraEpisodio(episodioSelecionado);
                }
            }
        }
    }
    
    // Mantido para uso interno
    private void buscarEpisodioNaSerie(int idSerie, int id) throws Exception {
        Episodio episodio = arqEpisodios.read(id);
        
        if (episodio != null && episodio.getSerieId() == idSerie) {
            visaoEpisodio.mostraEpisodio(episodio);
        } else if (episodio != null) {
            System.out.println("O episódio ID " + id + " não pertence a esta série.");
        } else {
            System.out.println("Episódio não encontrado.");
        }
    }

    private void alterarEpisodioPorNome(int idSerie) throws Exception {
        String termoBusca = visaoEpisodio.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca em episódios da série específica
        ArrayList<Episodio> resultados = relacionamento.buscarEpisodioPorNomeEmSerie(termoBusca, idSerie);
        
        // Exibir resultados
        visaoEpisodio.mostraResultadoBuscaEpisodios(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar episódio para alterar
        System.out.print("\nDigite o ID do episódio que deseja alterar (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        for (Episodio ep : resultados) {
            if (ep.getId() == idSelecionado) {
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        // Continuar com a alteração do episódio
        alterarEpisodio(idSerie, idSelecionado);
    }
    
    // Mantido para uso interno
    private void alterarEpisodio(int idSerie, int id) throws Exception {
        // Verificar se o episódio existe e pertence à série atual
        Episodio episodioAtual = arqEpisodios.read(id);
        if (episodioAtual == null) {
            System.out.println("Episódio não encontrado.");
            return;
        }
        
        if (episodioAtual.getSerieId() != idSerie) {
            System.out.println("Este episódio pertence a outra série. Não é possível editar.");
            return;
        }
        
        Episodio episodioNovo = visaoEpisodio.leEpisodio(idSerie, true);
        if (episodioNovo == null) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Remover o relacionamento antigo
        relacionamento.removerRelacionamento(episodioAtual.getSerieId(), episodioAtual.getId());
        
        episodioNovo.setId(id);
        boolean resultado = arqEpisodios.update(episodioNovo);
        
        if (resultado) {
            // Adicionar o novo relacionamento
            relacionamento.atualizarIndicesAposOperacao(episodioNovo, "update");
            System.out.println("Episódio atualizado com sucesso!");
        } else {
            System.out.println("Erro ao atualizar episódio.");
        }
    }

    private void excluirEpisodio() throws Exception {
        int id = visaoEpisodio.leIdEpisodio();
        // Verificar se o episódio existe antes de excluir
        Episodio episodio = arqEpisodios.read(id);
        if (episodio == null) {
            System.out.println("Episódio não encontrado.");
            return;
        }
        
        // Confirmar exclusão
        System.out.print("\nConfirmar exclusão do episódio \"" + episodio.getTitulo() + "\"? (S/N): ");
        String confirmacao = sc.nextLine().toUpperCase();
        if (!confirmacao.equals("S")) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Remover da árvore B+ antes de excluir do arquivo
        relacionamento.atualizarIndicesAposOperacao(episodio, "delete");
        
        boolean resultado = arqEpisodios.delete(id);
        System.out.println(resultado ? "Episódio excluído com sucesso!" : "Erro ao excluir episódio.");
    }
    
    private void excluirEpisodioPorNome(int idSerie) throws Exception {
        String termoBusca = visaoEpisodio.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca em episódios da série específica
        ArrayList<Episodio> resultados = relacionamento.buscarEpisodioPorNomeEmSerie(termoBusca, idSerie);
        
        // Exibir resultados
        visaoEpisodio.mostraResultadoBuscaEpisodios(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar episódio para excluir
        System.out.print("\nDigite o ID do episódio que deseja excluir (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        Episodio episodioParaExcluir = null;
        
        for (Episodio ep : resultados) {
            if (ep.getId() == idSelecionado) {
                encontrado = true;
                episodioParaExcluir = ep;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        // Confirmar exclusão
        System.out.print("\nConfirmar exclusão do episódio \"" + episodioParaExcluir.getTitulo() + "\"? (S/N): ");
        String confirmacao = sc.nextLine().toUpperCase();
        
        if (!confirmacao.equals("S")) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Remover da árvore B+ antes de excluir do arquivo
        relacionamento.atualizarIndicesAposOperacao(episodioParaExcluir, "delete");
        
        boolean resultado = arqEpisodios.delete(idSelecionado);
        System.out.println(resultado ? "Episódio excluído com sucesso!" : "Erro ao excluir episódio.");
    }

    private void listarEpisodiosDaSerie(int idSerie) throws Exception {
        System.out.println("\n======= LISTANDO EPISÓDIOS DA SÉRIE ID: " + idSerie + " =======");
        
        // Primeiro verificar se a série existe
        Serie serie = arqSerie.read(idSerie);
        if (serie == null) {
            System.out.println("Série não encontrada com ID: " + idSerie);
            return;
        }
        
        System.out.println("Série: " + serie.getTitulo());
        
        // Obter episódios usando o método padrão do relacionamento
        ArrayList<Episodio> episodios = relacionamento.getEpisodiosDaSerie(idSerie);
        
        if (episodios.isEmpty()) {
            System.out.println("\nNão há episódios cadastrados para esta série.");
            return;
        }
        
        // Mostrar quantidade encontrada
        System.out.println("\nForam encontrados " + episodios.size() + " episódio(s):");
        
        // Exibir os episódios
        visaoEpisodio.mostraListaEpisodios(episodios);
    }


}
