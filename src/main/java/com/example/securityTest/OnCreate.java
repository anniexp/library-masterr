/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import javax.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author Lenovo
 */
interface OnCreate {
    
}
interface OnUpdate {}

@Service
@Validated
class ValidatingServiceWithGroups{

//@Validated(OnCreate.class)
}