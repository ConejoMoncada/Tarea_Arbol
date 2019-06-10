/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import java.util.ArrayList;

/**
 *
 * @author Dell
 */
public class BNode {

    private ArrayList<Integer> keys;
    boolean leaf;



    public BNode(boolean leaf) {
        this.leaf = leaf;
        keys = new ArrayList();
    }

    public ArrayList<Integer> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<Integer> keys) {
        this.keys = keys;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    @Override
    public String toString() {
        String k = "";
        for (int key : keys) {
            k += key + " ";
        }
        return k;
    }
}
