package TP02.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import TP02.model.Episodio;
import TP02.model.Serie;

public class RelacionamentoSerieEpisodio {
    private Arquivo<Episodio> arqEpisodios;
    private Arquivo<Serie> arqSerie;
    public ArvoreBMais<ParIDSerieEpisodio> arvoreRelacionamento; // Tornada pública para acesso de diagnóstico


    public void testarInsercaoArvore() {
        try {
            // Cria um par simples para teste
            ParIDSerieEpisodio par = new ParIDSerieEpisodio(1, 1);

            // Tenta inserir na árvore
            boolean resultado = arvoreRelacionamento.create(par);

            System.out.println("Resultado da inserção de teste: " + resultado);

            // Verifica se o par foi inserido
            ArrayList<ParIDSerieEpisodio> busca = arvoreRelacionamento.read(par);
            System.out.println("Elementos encontrados na busca: " + busca.size() + busca.toString());

        } catch (Exception e) {
            System.err.println("Erro no teste de inserção: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public RelacionamentoSerieEpisodio(Arquivo<Serie> arqSerie, Arquivo<Episodio> arqEpisodios) {
        this.arqSerie = arqSerie;
        this.arqEpisodios = arqEpisodios;

        try {
            // IMPORTANTE: Usando caminho absoluto para garantir consistência
            String diretorioAtual = System.getProperty("user.dir");
            String caminhoData = diretorioAtual + "/TP01/TP01/Data";
            
            // Verificar se o diretório de dados existe
            java.io.File dataDir = new java.io.File(caminhoData);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("Diretório de dados criado: " + caminhoData);
            }

            // Usar o caminho completo e absolutamente correto para o arquivo
            String arquivoIndice = caminhoData + "/serieEpisodio.db";
            
            // Verificar se o arquivo de índice existe
            java.io.File idxFile = new java.io.File(arquivoIndice);
            boolean arquivoNovo = !idxFile.exists();

            // Inicializa a árvore B+ para relacionamento
            Constructor<ParIDSerieEpisodio> construtor = ParIDSerieEpisodio.class.getConstructor();
            this.arvoreRelacionamento = new ArvoreBMais<>(
                    construtor,
                    4, // Ordem da árvore
                    arquivoIndice
            );

            // Se o arquivo for novo, inicializa com um registro dummy
            if (arquivoNovo) {
                try {
                    testarInsercaoArvore();
                } catch (Exception e) {
                    System.err.println("Erro ao inicializar árvore B+: " + e.getMessage());
                }
            } else {
                // Verificar estado da árvore
                verificarIntegridadeArvore();
            }

            // Verificar se a árvore funciona corretamente com um teste básico
            try {
                
                // Criar um par de teste
                ParIDSerieEpisodio parTeste = new ParIDSerieEpisodio(999, 999);
                
                // Inserir o par na árvore
                boolean resultadoInsercao = arvoreRelacionamento.create(parTeste);
                
                // Buscar o par inserido
                ArrayList<ParIDSerieEpisodio> resultadoBusca = arvoreRelacionamento.read(parTeste);
                boolean parEncontrado = false;
                for (ParIDSerieEpisodio par : resultadoBusca) {
                    if (par.getIdSerie() == 999 && par.getIdEpisodio() == 999) {
                        parEncontrado = true;
                        break;
                    }
                }
                
                // Remover o par de teste
                boolean resultadoRemocao = arvoreRelacionamento.delete(parTeste);
                
                // Verificar se a árvore está funcional
                if (!(resultadoInsercao && parEncontrado && resultadoRemocao)) {
                    reconstruirIndice();
                } 
            } catch (Exception e) {
                System.err.println("ERRO no teste de sanidade da árvore B+: " + e.getMessage());
                e.printStackTrace();
                
                // Se o teste falhar, tenta reconstruir o índice
                reconstruirIndice();
            }

        } catch (Exception e) {
            System.err.println("Erro ao inicializar árvore B+: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verificarIntegridadeArvore() {
        try {
            boolean vazia = arvoreRelacionamento.empty();
            if (vazia) {
                // Tentar reconstruir o índice automaticamente
                reconstruirIndice();
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar integridade da árvore: " + e.getMessage());
        }
    }

    // Método para reconstruir o índice completo
    private void reconstruirIndice() {
        try {
            int totalRegistros = 0;

            // Percorre todos os episódios e reconstrói a árvore
            int ultimoIdEpisodio = arqEpisodios.ultimoId();
            for (int i = 1; i <= ultimoIdEpisodio; i++) {
                Episodio ep = arqEpisodios.read(i);
                if (ep != null && ep.getSerieId() > 0) {
                    try {
                        ParIDSerieEpisodio par = new ParIDSerieEpisodio(ep.getSerieId(), ep.getId());
                        arvoreRelacionamento.create(par);
                        totalRegistros++;
                    } catch (Exception e) {
                        System.err.println("Erro ao inserir episódio " + i + " no índice: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Falha na reconstrução do índice: " + e.getMessage());
        }
    }
    
    // Adicionar um relacionamento entre série e episódio
    public boolean adicionarRelacionamento(int idSerie, int idEpisodio) throws Exception {
        // Verifica se a série existe
        Serie serie = arqSerie.read(idSerie);
        if (serie == null) {
            System.err.println("ERRO: Série ID " + idSerie + " não encontrada ao adicionar relacionamento");
            return false;
        }

        // Verifica se o episódio existe
        Episodio episodio = arqEpisodios.read(idEpisodio);
        if (episodio == null) {
            System.err.println("ERRO: Episódio ID " + idEpisodio + " não encontrado ao adicionar relacionamento");
            return false;
        }

        // Atualiza o ID da série no episódio, se necessário
        if (episodio.getSerieId() != idSerie) {
            episodio.setSerieId(idSerie);
            arqEpisodios.update(episodio);
        }

        try {
            // Primeiro verifica se o relacionamento já existe na árvore
            ParIDSerieEpisodio parBusca = new ParIDSerieEpisodio(idSerie, idEpisodio);
            ArrayList<ParIDSerieEpisodio> pares = arvoreRelacionamento.read(parBusca);
            boolean jaExiste = false;

            // Verifica se o par exato já existe
            for (ParIDSerieEpisodio par : pares) {
                if (par.getIdSerie() == idSerie && par.getIdEpisodio() == idEpisodio) {
                    jaExiste = true;
                    break;
                }
            }

            // Se já existe, considera como sucesso
            if (jaExiste) {
                return true;
            }

            // Adiciona o relacionamento na árvore B+
            ParIDSerieEpisodio par = new ParIDSerieEpisodio(idSerie, idEpisodio);

            // Tente até 3 vezes para corrigir possíveis problemas temporários
            boolean resultado = false;
            int tentativas = 0;
            Exception ultimoErro = null;

            while (!resultado && tentativas < 3) {
                tentativas++;
                try {
                    // Verifica se a árvore está vazia antes de tentar inserir
                    boolean arvoreVazia = false;
                    try {
                        arvoreVazia = arvoreRelacionamento.empty();
                        if (arvoreVazia && tentativas == 1) {
                            // Se estiver vazia na primeira tentativa, tenta reconstruir o índice
                            reconstruirIndice();
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao verificar se a árvore está vazia: " + e.getMessage());
                    }


                        // Tenta inserir o elemento
                        resultado = arvoreRelacionamento.create(par);
                        System.out.println("Tentativa " + tentativas + ": " + (resultado ? "Sucesso" : "Falha"));

                    
                    // Verificar se a inserção realmente funcionou fazendo uma leitura direta
                    ArrayList<ParIDSerieEpisodio> testeLeitura = arvoreRelacionamento.read(par);
                    boolean encontrado = false;
                    for (ParIDSerieEpisodio p2 : testeLeitura) {
                        if (p2.getIdSerie() == par.getIdSerie() && p2.getIdEpisodio() == par.getIdEpisodio()) {
                            encontrado = true;
                            break;
                        }
                    }
                    System.out.println("Verificação de inserção: " + (encontrado ? "O par foi encontrado após inserção" : "O par NÃO foi encontrado após inserção!"));
                    
                    // Se não encontrou mesmo após o sucesso da inserção, algo está errado com a persistência
                    if (resultado && !encontrado) {
                        System.out.println("ALERTA: Árvore B+ retornou sucesso na inserção, mas elemento não foi encontrado depois.");
                        System.out.println("Isto pode indicar um problema de persistência ou corrupção da árvore.");
                        
                        // Forçar a inserção novamente apenas como teste
                        System.out.println("Tentando inserção forçada para diagnóstico...");
                        arvoreRelacionamento.create(new ParIDSerieEpisodio(idSerie, idEpisodio));
                        
                        // Verificar se agora o elemento foi inserido
                        testeLeitura = arvoreRelacionamento.read(par);
                        encontrado = false;
                        for (ParIDSerieEpisodio p2 : testeLeitura) {
                            if (p2.getIdSerie() == par.getIdSerie() && p2.getIdEpisodio() == par.getIdEpisodio()) {
                                encontrado = true;
                                break;
                            }
                        }
                        System.out.println("Após inserção forçada: " + (encontrado ? "SUCESSO" : "FALHA PERSISTENTE"));
                    }
                } catch (Exception e) {
                    ultimoErro = e;
                    System.err.println("Tentativa " + tentativas + " falhou: " + e.getMessage());

                    // Pequeno atraso entre tentativas
                    try { Thread.sleep(100); } catch (InterruptedException ie) { }
                }
            }

            if (!resultado && ultimoErro != null) {
                throw ultimoErro;
            }

            return resultado;
        } catch (Exception e) {
            System.err.println("ERRO ao adicionar relacionamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Remover um relacionamento entre série e episódio
    public boolean removerRelacionamento(int idSerie, int idEpisodio) throws Exception {
        ParIDSerieEpisodio par = new ParIDSerieEpisodio(idSerie, idEpisodio);
        return arvoreRelacionamento.delete(par);
    }
    
    // Verificar se uma série tem episódios
    public boolean serieTemEpisodios(int idSerie) throws Exception {
        boolean resultado = false;
        
        try {
            // Tentativa 1: Usar a árvore B+
            System.out.println("Verificando se série ID=" + idSerie + " tem episódios via árvore B+");
            ParIDSerieEpisodio parBusca = new ParIDSerieEpisodio(idSerie, -1);
            ArrayList<ParIDSerieEpisodio> pares = arvoreRelacionamento.read(parBusca);
            
            System.out.println("Árvore B+ retornou " + pares.size() + " pares para a série ID=" + idSerie);
            
            // Verifica se algum par com o idSerie foi encontrado
            for (ParIDSerieEpisodio par : pares) {
                if (par.getIdSerie() == idSerie) {
                    System.out.println("Encontrado episódio ID=" + par.getIdEpisodio() + " para série ID=" + idSerie);
                    resultado = true;
                    break;
                }
            }
            
            // Se não encontrar nada pela árvore, tenta método alternativo
            if (!resultado) {
                System.out.println("Árvore B+ não encontrou episódios. Usando busca alternativa...");
                resultado = verificarEpisodiosPorBuscaDireta(idSerie);
            }
            
            return resultado;
        } catch (Exception e) {
            System.err.println("Erro ao verificar episódios pela árvore B+: " + e.getMessage());
            
            // Em caso de erro, usa o método alternativo
            return verificarEpisodiosPorBuscaDireta(idSerie);
        }
    }
    
    // Método alternativo para verificar episódios por busca direta
    private boolean verificarEpisodiosPorBuscaDireta(int idSerie) throws Exception {
        System.out.println("Verificando episódios por busca direta para série ID=" + idSerie);
        
        int ultimoId = arqEpisodios.ultimoId();
        for (int i = 1; i <= ultimoId; i++) {
            Episodio ep = arqEpisodios.read(i);
            if (ep != null && ep.getSerieId() == idSerie) {
                System.out.println("Encontrado episódio ID=" + ep.getId() + " (busca direta)");
                
                // Tenta corrigir a árvore B+
                System.out.println("Corrigindo relação na árvore B+ para série ID=" + idSerie + 
                                   ", episódio ID=" + ep.getId());
                adicionarRelacionamento(idSerie, ep.getId());
                
                return true;
            }
        }
        
        System.out.println("Nenhum episódio encontrado para série ID=" + idSerie + " (busca direta)");
        return false;
    }
    
    // Obter todos os episódios de uma série
    public ArrayList<Episodio> getEpisodiosDaSerie(int idSerie) throws Exception {
        ArrayList<Episodio> episodios = new ArrayList<>();
        
        try {
            // Abordagem principal: Usar a árvore B+ para buscar episódios
            ParIDSerieEpisodio parBusca = new ParIDSerieEpisodio(idSerie, -1);
            ArrayList<ParIDSerieEpisodio> pares = arvoreRelacionamento.read(parBusca);
                        
            // Para cada par encontrado, recupera o episódio
            for (ParIDSerieEpisodio par : pares) {
                if (par.getIdSerie() == idSerie) { // Verifica se o ID da série corresponde
                    Episodio ep = arqEpisodios.read(par.getIdEpisodio());
                    if (ep != null) {
                        episodios.add(ep);
                    }
                } else {
                    // Como a árvore B+ retorna todos os elementos maiores ou iguais,
                    // podemos parar quando o idSerie for diferente
                    break;
                }
            }
            
            // Se não encontrou episódios pela árvore, tenta o método alternativo
            if (episodios.isEmpty()) {
                
                // Busca linear: verificar todos os episódios diretamente no arquivo
                int ultimoId = arqEpisodios.ultimoId();
                for (int i = 1; i <= ultimoId; i++) {
                    Episodio ep = arqEpisodios.read(i);
                    if (ep != null && ep.getSerieId() == idSerie) {
                        episodios.add(ep);
                        
                        // Tenta corrigir a árvore B+ adicionando o relacionamento que faltava
                        adicionarRelacionamento(idSerie, ep.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERRO na busca por episódios: " + e.getMessage());
            
            // Em caso de erro, tenta o método alternativo
            int ultimoId = arqEpisodios.ultimoId();
            for (int i = 1; i <= ultimoId; i++) {
                Episodio ep = arqEpisodios.read(i);
                if (ep != null && ep.getSerieId() == idSerie) {
                    episodios.add(ep);
                }
            }
        }
        
        return episodios;
    }
    
    // Obter episódios de uma temporada específica
    public ArrayList<Episodio> getEpisodiosPorTemporada(int idSerie, int temporada) throws Exception {
        ArrayList<Episodio> todosEpisodios = getEpisodiosDaSerie(idSerie);
        ArrayList<Episodio> episodiosTemporada = new ArrayList<>();
        
        for (Episodio ep : todosEpisodios) {
            if (ep.getNumTemporada() == temporada) {
                episodiosTemporada.add(ep);
            }
        }
        
        return episodiosTemporada;
    }
    
    // Contar o número de episódios por temporada de uma série
    public HashMap<Integer, Integer> contarEpisodiosPorTemporada(int idSerie) throws Exception {
        HashMap<Integer, Integer> contagem = new HashMap<>();
        ArrayList<Episodio> episodios = getEpisodiosDaSerie(idSerie);
        
        for (Episodio ep : episodios) {
            int temporada = ep.getNumTemporada();
            contagem.put(temporada, contagem.getOrDefault(temporada, 0) + 1);
        }
        
        return contagem;
    }
    
    // Organizar episódios por temporada
    public HashMap<Integer, ArrayList<Episodio>> organizarEpisodiosPorTemporada(int idSerie) throws Exception {
        HashMap<Integer, ArrayList<Episodio>> resultado = new HashMap<>();
        ArrayList<Episodio> episodios = getEpisodiosDaSerie(idSerie);
        
        for (Episodio ep : episodios) {
            int temporada = ep.getNumTemporada();
            if (!resultado.containsKey(temporada)) {
                resultado.put(temporada, new ArrayList<>());
            }
            resultado.get(temporada).add(ep);
        }
        
        return resultado;
    }
    
    // Atualizar os índices após criação, atualização ou remoção de episódios
    public void atualizarIndicesAposOperacao(Episodio episodio, String operacao) throws Exception {
        int idSerie = episodio.getSerieId();
        int idEpisodio = episodio.getId();
        
        switch (operacao.toLowerCase()) {
            case "create":
            case "update":
                boolean resultado = adicionarRelacionamento(idSerie, idEpisodio);
                if (!resultado) {
                    System.err.println("AVISO: Falha ao adicionar relacionamento entre série " + idSerie + 
                                     " e episódio " + idEpisodio + " à árvore B+");
                }
                break;
            case "delete":
                removerRelacionamento(idSerie, idEpisodio);
                break;
        }
    }
    
    // Obter o número total de episódios de uma série
    public int getTotalEpisodios(int idSerie) throws Exception {
        return getEpisodiosDaSerie(idSerie).size();
    }
    
    // Obter o número total de temporadas de uma série
    public int getTotalTemporadas(int idSerie) throws Exception {
        HashMap<Integer, Integer> episodiosPorTemporada = contarEpisodiosPorTemporada(idSerie);
        return episodiosPorTemporada.size();
    }

    
    // Buscar série por nome (parcial ou completo)
    public ArrayList<Serie> buscarSeriePorNome(String nomeParcial) throws Exception {
        ArrayList<Serie> seriesEncontradas = new ArrayList<>();
        int ultimoId = arqSerie.ultimoId();
        
        // Converter para minúsculo para busca case-insensitive
        String termoBusca = nomeParcial.toLowerCase();
        
        for (int i = 1; i <= ultimoId; i++) {
            Serie serie = arqSerie.read(i);
            if (serie != null && serie.getTitulo().toLowerCase().contains(termoBusca)) {
                seriesEncontradas.add(serie);
            }
        }
        
        return seriesEncontradas;
    }
    
    // Buscar episódio por nome (parcial ou completo)
    public ArrayList<Episodio> buscarEpisodioPorNome(String nomeParcial) throws Exception {
        ArrayList<Episodio> episodiosEncontrados = new ArrayList<>();
        int ultimoId = arqEpisodios.ultimoId();
        
        // Converter para minúsculo para busca case-insensitive
        String termoBusca = nomeParcial.toLowerCase();
        
        for (int i = 1; i <= ultimoId; i++) {
            Episodio episodio = arqEpisodios.read(i);
            if (episodio != null && episodio.getTitulo().toLowerCase().contains(termoBusca)) {
                episodiosEncontrados.add(episodio);
            }
        }
        
        return episodiosEncontrados;
    }
    
    // Buscar episódios por nome em uma série específica
    public ArrayList<Episodio> buscarEpisodioPorNomeEmSerie(String nomeParcial, int idSerie) throws Exception {
        ArrayList<Episodio> episodiosEncontrados = new ArrayList<>();
        int ultimoId = arqEpisodios.ultimoId();
        
        // Converter para minúsculo para busca case-insensitive
        String termoBusca = nomeParcial.toLowerCase();
        
        for (int i = 1; i <= ultimoId; i++) {
            Episodio episodio = arqEpisodios.read(i);
            if (episodio != null && 
                episodio.getSerieId() == idSerie && 
                episodio.getTitulo().toLowerCase().contains(termoBusca)) {
                episodiosEncontrados.add(episodio);
            }
        }
        
        return episodiosEncontrados;
    }
}