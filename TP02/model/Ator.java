package TP02.model;

import TP02.interfaces.Registro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Ator implements Registro {

    protected int id;
    protected String nome;
    protected int idade;

    // Construtores
    public Ator(int codigo, String nome, int idade) {
        this.id = codigo;
        this.nome = nome;
        this.idade = idade;
    }

    public Ator(String nome, int tam) {
        this(0, nome, tam); // O tamanho será calculado no construtor principal
    }

    public Ator() {
        this(-1, "", 0);
    }

    //getters e setters
    public int getId() {
        return id;
    }

    public void setId(int codigo) {
        this.id = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    // Serialização em vetor de bytes
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(buffer);

        dataOut.writeInt(id);
        dataOut.writeUTF(nome);
        dataOut.writeInt(idade);

        return buffer.toByteArray();
    }

    // Desserialização do vetor de bytes
    public void fromByteArray(byte[] dados) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(dados);
        DataInputStream dataIn = new DataInputStream(input);

        id = dataIn.readInt();
        nome = dataIn.readUTF();
        idade = dataIn.readInt();
       
    }

    @Override
    public String toString() {
        return "Ator #" + id + ", nome=" + nome + ", idade=" + idade;
    }

}
