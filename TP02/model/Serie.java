package TP02.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import TP02.interfaces.Registro;

public class Serie implements Registro {

    protected int codigo;
    protected String titulo;
    protected long ano;
    protected int tamanhoSinopse;
    protected String sinopse;
    protected String plataforma;

    // Construtores
    public Serie(int codigo, String titulo, long ano, int tam, String desc, String plat) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.ano = ano;
        this.sinopse = desc;
        this.tamanhoSinopse = (desc != null) ? desc.length() : 0; // Calcula automaticamente
        this.plataforma = plat;
    }

    public Serie(String titulo, long ano, int tam, String sinopse, String plat) {
        this(0, titulo, ano, 0, sinopse, plat); // O tamanho será calculado no construtor principal
    }

    public Serie() {
        this(-1, "", 0, 0, "", "");
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

    public long getAno() {
        return ano;
    }

    public void setAno(long anoLanc) {
        this.ano = anoLanc;
    }

    public int getTamanhoSinopse() {
        return tamanhoSinopse;
    }

    public void setTamanhoSinopse(int tamanho) {
        this.tamanhoSinopse = tamanho;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setsinopse(String sinopse) {
        this.sinopse = sinopse;
        this.tamanhoSinopse = (sinopse != null) ? sinopse.length() : 0;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String stream) {
        this.plataforma = stream;
    }

    // Serialização em vetor de bytes
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(buffer);

        dataOut.writeInt(codigo);
        dataOut.writeUTF(titulo);
        dataOut.writeLong(ano);
        dataOut.writeUTF(sinopse);
        dataOut.writeUTF(plataforma);

        return buffer.toByteArray();
    }

    // Desserialização do vetor de bytes
    public void fromByteArray(byte[] dados) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(dados);
        DataInputStream dataIn = new DataInputStream(input);

        codigo = dataIn.readInt();
        titulo = dataIn.readUTF();
        ano = dataIn.readLong();
        sinopse = dataIn.readUTF();
        plataforma = dataIn.readUTF();
        
        // Recalcular o tamanho da sinopse
        tamanhoSinopse = (sinopse != null) ? sinopse.length() : 0;
    }

    @Override
    public String toString() {
        return "📺 Série #" + codigo + "\n" +
                "Título: " + titulo + "\n" +
                "Ano de Lançamento: " + ano + "\n" +
                "Plataforma: " + plataforma + "\n" +
                "Sinopse (" + tamanhoSinopse + " caracteres): " + sinopse + "\n";
    }

}
