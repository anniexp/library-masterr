/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

/**
 *
 * @author Lenovo
 */
public enum Rolee { ROLE_USER, ROLE_ADMIN, ROLE_EMPLOYEE;  

    public static Rolee getROLE_USER() {
        return ROLE_USER;
    }

    public static Rolee getROLE_ADMIN() {
        return ROLE_ADMIN;
    }

    public static Rolee getROLE_EMPLOYEE() {
        return ROLE_EMPLOYEE;
    }
}  

