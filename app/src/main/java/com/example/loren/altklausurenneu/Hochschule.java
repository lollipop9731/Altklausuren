package com.example.loren.altklausurenneu;

import java.util.ArrayList;

class Hochschule {

    String name;
    ArrayList<String> module;

    public Hochschule(String name, ArrayList<String> module) {
        this.name = name;
        this.module = module;
    }

    public Hochschule(String name) {

        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getModule() {
        return module;
    }

    public void setModule(ArrayList<String> module) {
        this.module = module;
    }
}
