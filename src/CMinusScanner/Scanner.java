/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CMinusScanner;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public interface Scanner {
    public Token getNextToken();
    public Token viewNextToken();
}
