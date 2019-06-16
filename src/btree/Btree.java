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
        int resp, k;
        sc = new Scanner(System.in);
        sc.useDelimiter("\n");
        do {
            System.out.println("1. Insertar");
            System.out.println("2. Mostrar");
            System.out.println("3. Eliminar");
            System.out.println("0. Salir");
            System.out.print("Ingrese una opción: ");
            resp = sc.nextInt();
            switch (resp) {
                case 1:
                    System.out.print("Ingrese un número entero: ");
                    k = sc.nextInt();
                    if (!search((DefaultMutableTreeNode) ((DefaultTreeModel) bTree.getModel()).getRoot(), k)) {
                        ((DefaultTreeModel) bTree.getModel()).setRoot(insert((DefaultMutableTreeNode) ((DefaultTreeModel) bTree.getModel()).getRoot(), k));
                        if (((BNode) ((DefaultMutableTreeNode) ((DefaultTreeModel) bTree.getModel()).getRoot()).getUserObject()).getKeys().size() == 6) {
                            ((DefaultTreeModel) bTree.getModel()).setRoot(splitRoot((DefaultMutableTreeNode) ((DefaultTreeModel) bTree.getModel()).getRoot()));
                        }
                    } else
                        System.out.println("Esta llave ya se encuentra en el árbol");
                    break;
                case 2:
                    //impresion del arbol
                    Mostrar nuevo = new Mostrar();
                    nuevo.pack();
                    nuevo.setVisible(true);
                    nuevo.setLocationRelativeTo(null);
                    nuevo.SetArbol((DefaultTreeModel) bTree.getModel());
                    break;
                case 3:
                    System.out.println("Ingrese la llave que desea eliminar");
                    k = sc.nextInt();
                    if(search((DefaultMutableTreeNode)((DefaultTreeModel)bTree.getModel()).getRoot(),k)){
                        DefaultMutableTreeNode nr = delete((DefaultMutableTreeNode)((DefaultTreeModel)bTree.getModel()).getRoot(),k);
                        if (((BNode)nr.getUserObject()).getKeys().isEmpty()){
                            nr = (DefaultMutableTreeNode)nr.getChildAt(0);
                            nr.removeFromParent();
                        }
                        ((DefaultTreeModel)bTree.getModel()).setRoot(nr);
                    }
                    else
                        System.out.println("Esta llave no existe en el árbol");
            }
        } while (resp != 0);
    }

    public static DefaultMutableTreeNode insert(DefaultMutableTreeNode tn, int k) {
        //este método busca el nodo e inserta en el lugar adecuado
        if (tn == null || tn.getUserObject() instanceof String) {
            //árbol vacío
            BNode b = new BNode(true);
            b.getKeys().add(k);
            tn = new DefaultMutableTreeNode(b);
            return tn;
        } else {
            int index = 0;
            BNode b = (BNode) tn.getUserObject();
            if (b.isLeaf()) {//busca posición para insertar
                while (index < b.getKeys().size() && k > b.getKeys().get(index)) {
                    index++;
                }
                if (index == b.getKeys().size()) {
                    b.getKeys().add(k);
                } else {
                    b.getKeys().add(index, k);
                }
                tn.setUserObject(b);
                return tn;
            } else {//busca posición para bajar, revisa si su hijo necesita split
                while (index < b.getKeys().size() && k > b.getKeys().get(index)) {
                    index++;
                }
                DefaultMutableTreeNode nn = insert((DefaultMutableTreeNode) tn.getChildAt(index), k);
                try {
                    tn.insert(nn, index);
                    System.out.println("Insert nn, index");
                    System.out.println(tn.getChildCount());
                } catch (Exception e) {
                }
                if (((BNode) ((DefaultMutableTreeNode) tn.getChildAt(index)).getUserObject()).getKeys().size() == 6) {
                    
                    System.out.println("Split incoming");
                    tn = split(tn, index);
                }
                return tn;
            }
        }
    }

    public static DefaultMutableTreeNode split(DefaultMutableTreeNode parent, int index) {
        //manda la llave central del hijo sobre-llenado al padre y agrega un hijo nuevo dividiendo las llaves
        DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(index);
        BNode b = (BNode) child.getUserObject();
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
        if (!b.isLeaf()) {
            //manda los hijos del nodo sobre-llenado a los lados respectivos
            for (int i = 0; i < 3; i++) {
                left.add((DefaultMutableTreeNode) child.getChildAt(0));
            }
            for (int i = 3; i < 7; i++) {
                right.add((DefaultMutableTreeNode) child.getChildAt(0));
            }
        }
        //le entrega la llave restante al padre
        BNode bp = (BNode) parent.getUserObject();
        bp.getKeys().add(index, b.getKeys().get(2));
        parent.setUserObject(bp);
        //inserta los nodos en el padre
        //findme
        parent.remove(index);
        parent.insert(right, index);
        parent.insert(left, index);
        System.out.println("Parent child count: " + parent.getChildCount());
        System.out.println("Parent key count: " + bp.getKeys().size());
        return parent;
    }

    public static DefaultMutableTreeNode splitRoot(DefaultMutableTreeNode root) {
        //lo mismo que split pero para la raíz
        DefaultMutableTreeNode parent = new DefaultMutableTreeNode(new BNode(false));
        BNode b = (BNode) root.getUserObject();
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
        if (!b.isLeaf()) {
            //manda los hijos del nodo sobre-llenado a los lados respectivos
            for (int i = 0; i < 3; i++) {
                left.add((DefaultMutableTreeNode) root.getChildAt(0));
            }
            for (int i = 0; i < 4; i++) {
                right.add((DefaultMutableTreeNode) root.getChildAt(0));
            }
        }
        //le entrega la llave restante al padre
        BNode bp = (BNode) parent.getUserObject();
        bp.getKeys().add(b.getKeys().get(2));
        parent.setUserObject(bp);
        //inserta los nodos en el padre
        parent.add(left);
        parent.add(right);
        System.out.println("Root");
        return parent;
    }

    public static boolean search(DefaultMutableTreeNode tn, int k) {
        if (tn == null) {
            return false;
        } else if (!(tn.getUserObject() instanceof String)) {
            BNode b = (BNode) tn.getUserObject();
            int i = 0;
            while (i < b.getKeys().size() && k > b.getKeys().get(i)) {
                i++;
            }
            if (i < b.getKeys().size()) {
                if (k == b.getKeys().get(i)) {
                    return true;
                } else if (b.isLeaf()) {
                    return false;
                } else {
                    return search((DefaultMutableTreeNode) tn.getChildAt(i), k);
                }
            } else if (b.isLeaf()) {
                return false;
            } else {
                return search((DefaultMutableTreeNode) tn.getChildAt(i), k);
            }
        } else {
            return false;
        }
    }

    public static DefaultMutableTreeNode delete(DefaultMutableTreeNode tn, int k){
        int index = 0;
        BNode b = (BNode) tn.getUserObject();
        while (index < b.getKeys().size() && k > b.getKeys().get(index)) {
            index++;
        }
        if (index == b.getKeys().size()){
            //aquí se busca
            DefaultMutableTreeNode nn = delete((DefaultMutableTreeNode)tn.getChildAt(index),k);
            try {
                tn.insert(nn, index);
            } catch (Exception e) {
            }
            //revisar merge y split
            if((((BNode)((DefaultMutableTreeNode)tn.getChildAt(index)).getUserObject()).getKeys().size()) < 2){
                tn = merge(tn,index);
            }
        }
        else if (b.getKeys().get(index) == k){
            //aquí se borra
            if(b.isLeaf()){
                b.getKeys().remove(index);
                tn.setUserObject(b);
            }else{
                BNode lc = (BNode)((DefaultMutableTreeNode)tn.getChildAt(index)).getUserObject();
                int ck = lc.getKeys().get(lc.getKeys().size()-1);
                b.getKeys().set(index, ck);
                lc.getKeys().remove(lc.getKeys().size()-1);
                ((DefaultMutableTreeNode)tn.getChildAt(index)).setUserObject(lc);
                tn.setUserObject(b);
                if(lc.getKeys().size() < 2)
                    tn = merge(tn,index);
            }
            return tn;
        }
        else{
            //aquí se busca
            DefaultMutableTreeNode nn = delete((DefaultMutableTreeNode)tn.getChildAt(index),k);
            try {
                tn.insert(nn, index);
            } catch (Exception e) {
            }
            //revisar merge y split
            if((((BNode)((DefaultMutableTreeNode)tn.getChildAt(index)).getUserObject()).getKeys().size()) < 2){
                tn = merge(tn,index);
            }
        }
        return tn;
    }

    static DefaultMutableTreeNode merge(DefaultMutableTreeNode parent, int index){
        //manda la llave del nodo padre al hijo creando un solo nodo
        int k;
        BNode p = (BNode)parent.getUserObject();
        BNode c = (BNode)((DefaultMutableTreeNode)parent.getChildAt(index)).getUserObject();
        DefaultMutableTreeNode sibling;
        BNode bLeft;
        BNode bRight;
        if(index == 0){
            //tiene que merge con derecha
            bRight = (BNode)((DefaultMutableTreeNode)parent.getChildAt(index+1)).getUserObject();
            k = p.getKeys().get(index);
            p.getKeys().remove(index);
            c.getKeys().add(k);
            for (Integer key : bRight.getKeys()) {
                c.getKeys().add(key);
            }
            //findme si hay error es aquí y si funciona cambiar en split si tengo tiempo
            ((DefaultMutableTreeNode)parent.getChildAt(index)).setUserObject(c);
            if (!c.isLeaf()){
                sibling = (DefaultMutableTreeNode)parent.getChildAt(index+1);
                int cc = sibling.getChildCount();
                for (int i = 0; i < cc; i++) {
                    ((DefaultMutableTreeNode)parent.getChildAt(index)).add((DefaultMutableTreeNode)sibling.getChildAt(0));
                }
            }
            parent.remove(index+1);
            if(c.getKeys().size()>= 6){
                parent = split(parent,index);
            }
        }else if(index == parent.getChildCount()-1){
            //tiene que merge con izquierda
            bLeft = (BNode)((DefaultMutableTreeNode)parent.getChildAt(index-1)).getUserObject();
            k = p.getKeys().get(index-1);
            p.getKeys().remove(index-1);
            c.getKeys().add(0, k);
            for (int i = bLeft.getKeys().size() - 1; i >= 0; i--) {
                c.getKeys().add(0, bLeft.getKeys().get(i));
            }
            //findme si hay error es aquí y si funciona cambiar en split si tengo tiempo
            ((DefaultMutableTreeNode)parent.getChildAt(index)).setUserObject(c);
            if (!c.isLeaf()){
                sibling = (DefaultMutableTreeNode)parent.getChildAt(index-1);
                for (int i = sibling.getChildCount() - 1; i >= 0; i--) {
                    ((DefaultMutableTreeNode)parent.getChildAt(index)).insert((DefaultMutableTreeNode)sibling.getChildAt(i),0);
                }
            }
            parent.remove(index-1);
            if(c.getKeys().size()>= 6){
                parent = split(parent,index - 1);
            }
        }else{
            bLeft = (BNode)((DefaultMutableTreeNode)parent.getChildAt(index-1)).getUserObject();
            bRight = (BNode)((DefaultMutableTreeNode)parent.getChildAt(index+1)).getUserObject();
            if(bRight.getKeys().size() > bLeft.getKeys().size()){
                //revisa cual es más grande
                k = p.getKeys().get(index);
                p.getKeys().remove(index);
                c.getKeys().add(k);
                for (Integer key : bRight.getKeys()) {
                    c.getKeys().add(key);
                }
                //findme si hay error es aquí y si funciona cambiar en split si tengo tiempo
                ((DefaultMutableTreeNode)parent.getChildAt(index)).setUserObject(c);
                if (!c.isLeaf()){
                    sibling = (DefaultMutableTreeNode)parent.getChildAt(index+1);
                    for (int i = 0; i < sibling.getChildCount(); i++) {
                        ((DefaultMutableTreeNode)parent.getChildAt(index)).add((DefaultMutableTreeNode)sibling.getChildAt(0));
                    }
                }
            parent.remove(index+1);
            if(c.getKeys().size()>= 6){
                parent = split(parent,index);
            }
            }else{
                //si es sus hermanos tienen el mismo tamaño va por el de la izquierda
                k = p.getKeys().get(index-1);
                p.getKeys().remove(index-1);
                c.getKeys().add(0, k);
                for (int i = bLeft.getKeys().size() - 1; i >= 0; i--) {
                    c.getKeys().add(0, bLeft.getKeys().get(i));
                }
                //findme si hay error es aquí y si funciona cambiar en split si tengo tiempo
                ((DefaultMutableTreeNode)parent.getChildAt(index)).setUserObject(c);
                if (!c.isLeaf()){
                    sibling = (DefaultMutableTreeNode)parent.getChildAt(index-1);
                    for (int i = sibling.getChildCount() - 1; i >= 0; i--) {
                        ((DefaultMutableTreeNode)parent.getChildAt(index)).insert((DefaultMutableTreeNode)sibling.getChildAt(i),0);
                    }
                }
                parent.remove(index-1);
            if(c.getKeys().size()>= 6)
                parent = split(parent,index-1);
            }
        }
        return parent;
    }
}
