package com.example.digioapp;

public class HistoryModel {
    int id, user, table_num, position, isWon;

    public HistoryModel(int id, int user, int table_num, int position, int isWon) {
        this.id = id;
        this.user = user;
        this.table_num = table_num;
        this.position = position;
        this.isWon = isWon;
    }

    public HistoryModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getTable_num() {
        return table_num;
    }

    public void setTable_num(int table_num) {
        this.table_num = table_num;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIsWon() {
        return isWon;
    }

    public void setIsWon(int isWon) {
        this.isWon = isWon;
    }
}
