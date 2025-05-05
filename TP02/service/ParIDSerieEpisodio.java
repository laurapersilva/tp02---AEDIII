package TP02.service;

import TP02.interfaces.RegistroArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParIDSerieEpisodio implements RegistroArvoreBMais<ParIDSerieEpisodio> {
    
    private int idSerie;
    private int idEpisodio;
    
    public ParIDSerieEpisodio() {
        this.idSerie = -1;
        this.idEpisodio = -1;
    }
    
    public ParIDSerieEpisodio(int idSerie, int idEpisodio) {
        this.idSerie = idSerie;
        this.idEpisodio = idEpisodio;
    }
    
    public int getIdSerie() {
        return this.idSerie;
    }
    
    public void setIdSerie(int idSerie) {
        this.idSerie = idSerie;
    }
    
    public int getIdEpisodio() {
        return this.idEpisodio;
    }
    
    public void setIdEpisodio(int idEpisodio) {
        this.idEpisodio = idEpisodio;
    }
    
    @Override
    public short size() {
        return 8; // 4 bytes para cada int (idSerie e idEpisodio)
    }
    
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(idSerie);
        dos.writeInt(idEpisodio);
        
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        this.idSerie = dis.readInt();
        this.idEpisodio = dis.readInt();
    }

    @Override
    public int compareTo(ParIDSerieEpisodio outro) {
        // Primeiro compara pelo idSerie
        if (this.idSerie != outro.idSerie) {
            return this.idSerie - outro.idSerie;
        }
        
        // Caso especial: se estamos buscando todos os episódios de uma série
        // usando idEpisodio = -1, consideramos que é menor que qualquer outro par
        if (this.idEpisodio == -1) {
            return -1; // Sempre menor que qualquer episódio real
        } else if (outro.getIdEpisodio() == -1) {
            return 1; // Sempre maior que o elemento de busca
        }
        
        // Caso normal: se idSerie for igual, compara pelo idEpisodio
        return this.idEpisodio - outro.idEpisodio;
    }


    @Override
    public ParIDSerieEpisodio clone() {
        return new ParIDSerieEpisodio(this.idSerie, this.idEpisodio);
    }
    
    @Override
    public String toString() {
        return "Serie: " + idSerie + ", Episodio: " + idEpisodio;
    }
}