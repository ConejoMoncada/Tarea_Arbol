/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import java.util.Scanner;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Dell
 */
public class Btree {
    static JTree bTree = new JTree();
    static Scanner sc;
    static DefaultMutableTreeNode nodo;
    static boolean empty = true;
    static final int max = 5;
    static final int min = 2;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int resp,k;
        sc = new Scanner(System.in);
        sc.useDelimiter("\n");
        do{
            System.out.println("1. Insertar");
            System.out.println("2. Mostrar");
            System.out.println("0. Salir");
            System.out.print("Ingrese una opción: ");
            resp = sc.nextInt();
            switch(resp){
                case 1:
                    System.out.print("Ingrese un número entero: ");
                    k = sc.nextInt();
                    if(!search((DefaultMutableTreeNode)((DefaultTreeModel)bTree.getModel()).getRoot(),k)){
                        ((DefaultTreeModel)bTree.getModel()).setRoot(insert((DefaultMutableTreeNode)((DefaultTreeModel)bTree.getModel()).getRoot(),k));
                        if(((BNode)((DefaultMutableTreeNode)((DefaultTreeModel)bTree.getModel()).getRoot()).getUserObject()).getKeys().size()==6){
                            ((DefaultTreeModel)bTree.getModel()).setRoot(splitRoot((DefaultMutableTreeNode)((DefaultTreeModel)bTree.getModel()).getRoot()));
                        }
                    }else
                        System.out.println("Esta llave ya se encuentra en el árbol");
            }
        }while(resp != 0);
    }

    public static DefaultMutableTreeNode insert(DefaultMutableTreeNode tn, int k){
        //este método busca el nodo e inserta en el lugar adecuado
        if(tn == null){
            //árbol vacío
            BNode b = new BNode(true);
            b.getKeys().add(k);
            tn = new DefaultMutableTreeNode(b);
            return tn;
        }else{
            int index = 0;
            BNode b = (BNode)tn.getUserObject();
            if(b.isLeaf()){//busca posición para insertar
                while(index < b.getKeys().size() && k > b.getKeys().get(index)){
                    index ++;
                }
                if(index == b.getKeys().size())
                    b.getKeys().add(k);
                else
                    b.getKeys().add(index, k);
                tn.setUserObject(b);
                return tn; 
            }else{//busca posición para bajar, revisa si su hijo necesita split
                while(index < b.getKeys().size() && k > b.getKeys().get(index)){
                    index ++;
                }
                DefaultMutableTreeNode nn = insert((DefaultMutableTreeNode)tn.getChildAt(index),k);
                try{
                    tn.insert(nn, index);
                    tn.remove(index+1);
                }catch(Exception e){
                }
                if(((BNode)((DefaultMutableTreeNode)tn.getChildAt(index)).getUserObject()).getKeys().size() == 6){
                    tn = split(tn,index);
                }
                return tn;
            }
        }
    }

    public static DefaultMutableTreeNode split(DefaultMutableTreeNode parent, int index){
        //manda la llave central del hijo sobre-llenado al padre y agrega un hijo nuevo dividiendo las llaves
        DefaultMutableTreeNode child = (DefaultMutableTreeNode)parent.getChildAt(index);
        BNode b = (BNode)child.getUserObject();
        BNode bLeft = new BNode(b.isLeaf());
        BNode bRight = new BNode(b.isLeaf());
        //Divide las llaves del nodo sobre-llenado entre los nodos derecho e izquierdo
        for (int i = 0; i < 2; i++) {
            bLeft.getKeys().add(b.getKeys().get(i));
        }
        for (int i = 3; i < 6; i++) {
            bRight.getKeys().add(b.getKeys().get(i));
        }
        DefaultMutableTreeNode left = new DefaultMutableTreeNode(bLeft);
        DefaultMutableTreeNode right = new DefaultMutableTreeNode(bRight);
        if(!b.isLeaf()){
            //manda los hijos del nodo sobre-llenado a los lados respectivos
            for (int i = 0; i < 3; i++) {
                left.add((DefaultMutableTreeNode)child.getChildAt(i));
            }
            for (int i = 3; i < 7; i++) {
                right.add((DefaultMutableTreeNode)child.getChildAt(i));
            }
        }
        //le entrega la llave restante al padre
        BNode bp = (BNode)parent.getUserObject();
        bp.getKeys().add(index, b.getKeys().get(2));
        parent.setUserObject(bp);
        //inserta los nodos en el padre
        parent.remove(index);
        parent.insert(right, index);
        parent.insert(left, index);
        return parent;
    }

    public static DefaultMutableTreeNode splitRoot(DefaultMutableTreeNode root){
        //lo mismo que split pero para la raíz
        DefaultMutableTreeNode parent = new DefaultMutableTreeNode(new BNode(false));
        BNode b = (BNode)root.getUserObject();
        BNode bLeft = new BNode(b.isLeaf());
        BNode bRight = new BNode(b.isLeaf());
        //Divide las llaves del nodo sobre-llenado entre los nodos derecho e izquierdo
        for (int i = 0; i < 2; i++) {
            bLeft.getKeys().add(b.getKeys().get(i));
        }
        for (int i = 3; i < 6; i++) {
            bRight.getKeys().add(b.getKeys().get(i));
        }
        DefaultMutableTreeNode left = new DefaultMutableTreeNode(bLeft);
        DefaultMutableTreeNode right = new DefaultMutableTreeNode(bRight);
        if(!b.isLeaf()){
            //manda los hijos del nodo sobre-llenado a los lados respectivos
            for (int i = 0; i < 3; i++) {
                left.add((DefaultMutableTreeNode)root.getChildAt(i));
            }
            for (int i = 3; i < 7; i++) {
                right.add((DefaultMutableTreeNode)root.getChildAt(i));
            }
        }
        //le entrega la llave restante al padre
        BNode bp = (BNode)parent.getUserObject();
        bp.getKeys().add(b.getKeys().get(2));
        parent.setUserObject(bp);
        //inserta los nodos en el padre
        parent.add(left);
        parent.add(right);
        return parent;
    }

    public static boolean search(DefaultMutableTreeNode tn, int k){
        if(tn == null)
            return false;
        else{
            BNode b = (BNode)tn.getUserObject();
            int i = 0;
            while (i < b.getKeys().size() && k > b.getKeys().get(i)) {
                i++;
            }
            if(i < b.getKeys().size()){
                if(k == b.getKeys().get(i)){
                    return true;
                }else if(b.isLeaf()){
                    return false;
                }else
                    return search((DefaultMutableTreeNode)tn.getChildAt(i),k);
            }else if(b.isLeaf())
                return false;
            else
                return search((DefaultMutableTreeNode)tn.getChildAt(i),k);
        }
    }
}
