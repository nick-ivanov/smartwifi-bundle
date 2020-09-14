package com.thinklab.smartwifi;

import java.util.ArrayList;

public class ClientSecret {
    private int size;
    private String seed;
    private ArrayList<String> hashchain;
    private String top;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public ArrayList<String> getHashchain() {
        return hashchain;
    }

    public void setHashchain(ArrayList<String> hashchain) {
        this.hashchain = hashchain;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    @Override
    public String toString() {
        return "ClientSecret{" +
                "size=" + size +
                ", seed='" + seed + '\'' +
                ", hashchain=" + hashchain +
                ", top='" + top + '\'' +
                '}';
    }
}
