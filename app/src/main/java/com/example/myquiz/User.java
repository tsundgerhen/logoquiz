package com.example.myquiz;

public class User {
    private String name;
    private String email;
    private String password;
    private int coins;
    private int stability;  // Added field

    // Default constructor (required for Firebase)
    public User() {}

    // Constructor
    public User(String name, String email, String password, int coins, int stability) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.coins = coins;
        this.stability = stability;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getStability() {
        return stability;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }
}
