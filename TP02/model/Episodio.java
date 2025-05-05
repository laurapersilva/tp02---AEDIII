package TP02.model;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.time.LocalDate;
import TP02.interfaces.Registro;

public class Episodio implements Registro {

    protected int codigo;
    protected String titulo;
    protected int numTemporada;
    protected LocalDate lancamento;
    protected long duracaoMin;
    protected int serieId;

    // Construtores
    public Episodio(int codigo, String titulo, int temporada, LocalDate data, long duracao, int serie) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.numTemporada = temporada;
        this.lancamento = data;
        this.duracaoMin = duracao;
        this.serieId = serie;
    }

    public Episodio(String titulo, int temporada, LocalDate data, long duracao, int serie) {
        this(0, titulo, temporada, data, duracao, serie);
    }

    public Episodio() {
        this(-1, "", 0, LocalDate.now(), 0, 0);
    }

    // Getters e Setters
    public int getId() {
        return codigo;
    }

    public void setId(int id) {
        this.codigo = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String nome) {
        this.titulo = nome;
    }

    public int getNumTemporada() {
        return numTemporada;
    }

    public void setNumTemporada(int temporada) {
        this.numTemporada = temporada;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public void setLancamento(LocalDate data) {
        this.lancamento = data;
    }

    public long getDuracaoMin() {
        return duracaoMin;
    }

    public void setDuracaoMin(long duracao) {
        this.duracaoMin = duracao;
    }

    public int getSerieId() {
        return serieId;
    }

    public void setSerieId(int id) {
        this.serieId = id;
    }

    // Serializa o objeto para um vetor de bytes
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(buffer);

        dataOut.writeInt(codigo);
        dataOut.writeUTF(titulo);
        dataOut.writeInt(numTemporada);
        dataOut.writeInt((int) lancamento.toEpochDay());
        dataOut.writeLong(duracaoMin);
        dataOut.writeInt(serieId);

        return buffer.toByteArray();
    }

    // Reconstrói o objeto a partir de um vetor de bytes
    public void fromByteArray(byte[] dados) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(dados);
        DataInputStream dataIn = new DataInputStream(input);

        this.codigo = dataIn.readInt();
        this.titulo = dataIn.readUTF();
        this.numTemporada = dataIn.readInt();
        this.lancamento = LocalDate.ofEpochDay(dataIn.readInt());
        this.duracaoMin = dataIn.readLong();
        this.serieId = dataIn.readInt();
    }

    // Representação textual
    @Override
    public String toString() {
        return "🎬 Episódio #" + codigo + "\n" +
               "Título: " + titulo + "\n" +
               "Temporada: " + numTemporada + "\n" +
               "Lançamento: " + lancamento + "\n" +
               "Duração: " + duracaoMin + " min\n" +
               "ID da Série: " + serieId + "\n";
    }
}
