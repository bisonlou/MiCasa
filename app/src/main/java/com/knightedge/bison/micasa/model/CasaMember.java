package com.knightedge.bison.micasa.model;

/**
 * Created by Bison on 30/10/2017.
 */

public class CasaMember {
    String name;
    String email;
    String status;
    String rights;

    public CasaMember(){

    }

    public CasaMember(String name, String email, String status, String rights) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.rights = rights;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getRights() {
        return rights;
    }
}
