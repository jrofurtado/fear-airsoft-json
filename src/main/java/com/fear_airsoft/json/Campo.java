package com.fear_airsoft.json;

import java.util.List;

public class Campo {
    String id;
    String nome;
    String morada;
    double estacionamentoLng;
    double lng;
    double lat;
    double estacionamentoLat;
    List<String> foto;

    public double getEstacionamentoLat() {
        return estacionamentoLat;
    }

    public void setEstacionamentoLat(double estacionamentoLat) {
        this.estacionamentoLat = estacionamentoLat;
    }

    public double getEstacionamentoLng() {
        return estacionamentoLng;
    }

    public void setEstacionamentoLng(double estacionamentoLng) {
        this.estacionamentoLng = estacionamentoLng;
    }

    public List<String> getFoto() {
        return foto;
    }

    public void setFoto(List<String> foto) {
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}