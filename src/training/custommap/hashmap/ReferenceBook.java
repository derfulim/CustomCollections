package training.custommap.hashmap;

import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public  class ReferenceBook<K, V> implements Book <K,V>  {

    public static void main(String[] args) {
        ReferenceBook <String, String> strings = new ReferenceBook<>();
        strings.insert("1","1");
        strings.insert("2","2");
        strings.insert("3","3");

//        System.out.println(strings.get("1"));
//        System.out.println(strings.size());

        for (String s:strings) {
            System.out.println(s);
        }


//        System.out.println(strings.get("a"));
//        strings.delete("a");
//        System.out.println(strings.get("a"));



    }
    private Node<K,V>[] hashTable;
    private int size = 0;
    private float threshold;

    public ReferenceBook() {
        hashTable = new Node[16];
        threshold = hashTable.length*0.75f;
    }

    /**
     * Purpose of the method to find number of the cell in which will be added new pair and minimize collision.
     * @param key
     * @return
     */
    private int hash(K key) {
        int hash = 31;
        hash = hash*17 + key.hashCode();
        return hash%hashTable.length;
    }

    @Override
    public boolean insert(K key, V value) {
        if(size+1>=threshold)
        {threshold*=2;
        arrayDoubling();}

        Node<K,V> newNode = new Node<> (key, value);
        int index = hash(key);
        if (hashTable [index] == null) {
            return simpleAdd(index, newNode);
        }

        List<Node<K,V>> nodeList = hashTable[index].getNodes();
        for (Node<K,V> node : nodeList) {
            if (keyExistButValueNew(node,newNode,value)||
                    collisionProcessing(node,newNode, nodeList)) {
                return true;
            }

        }
        return false;

    }

    private boolean simpleAdd(int index, Node<K,V> newNode) {
        hashTable[index] = new Node<>(null, null);
        hashTable[index].getNodes().add(newNode);
        size++;
        return true;
    }

    private boolean keyExistButValueNew(
            final Node<K,V> nodeFromList,
            final Node<K,V> newNode,
            final V value
    )
    {if(newNode.getKey().equals(nodeFromList.getKey())&&!newNode.getValue().equals(nodeFromList.getValue())) {
        nodeFromList.setValue(value);
        return true;}

        return false;
    }

    private boolean collisionProcessing(
            final Node<K,V> nodeFromList,
            final Node<K,V> newNode,
            List<Node<K,V>> nodes) {

        if(newNode.hashCode() == nodeFromList.hashCode()&&
        !Objects.equals(newNode.key,nodeFromList.key)&&
        !Objects.equals(newNode.value,nodeFromList.value)) {
            nodes.add(newNode);
            size++;
            return true;
        }
        return false;
    }

    private void arrayDoubling() {
        Node<K,V>[] oldHashTable = hashTable;
        hashTable = new Node [oldHashTable.length*2];
        size = 0;
        for (Node<K,V> node : oldHashTable) {
            if (node!=null) {
                for (Node<K,V> n: node.getNodes()){
                    insert(n.key,n.value);
                }
            }
        }
    }


    @Override
    public boolean delete(K key) {
        int index = hash(key);
        if(hashTable[index]==null)
        return false;

        if(hashTable[index].getNodes().size() == 1) {
            hashTable[index] = null;
            return true;
        }

        List<Node<K,V>> nodeList = hashTable[index].getNodes();
        for (Node<K,V> node : nodeList) {
            if (key.equals(node.getKey())) {
                nodeList.remove(node);
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int index = hash(key);
        if (index < hashTable.length && hashTable[index]!=null) {
            List<Node<K,V>> list = hashTable[index].getNodes();
            for (Node<K,V> node : list) {
                if (key.equals(node.getKey())) {
                    return node.getValue();
                }
            }
            }
        return null;
        }


    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            int counterArray = -1;
            int valuesCounter = 0;
            Iterator<ReferenceBook<K,V>.Node<K,V>> subIterator = null;

            @Override
            public boolean hasNext() {
                if (valuesCounter == size) {
                    return false;
                }

                if (subIterator == null || !subIterator.hasNext()) {
                    if (moveToNextCell()) {
                        subIterator = hashTable[counterArray].getNodes().iterator();}
                    else {
                        return false;
                    }
                }
                return subIterator.hasNext();
            }

            private boolean moveToNextCell() {
                counterArray++;
                while (counterArray<hashTable.length && hashTable[counterArray]== null) { counterArray++;
                }
                return counterArray<hashTable.length&&hashTable[counterArray] != null;
            }

            @Override
            public V next() {
                valuesCounter++;
                return subIterator.next().getValue();
            }
        };
    }

    private class Node<K,V>{
        private List<Node<K,V>> nodes;
        private int hash;
        private K key;
        private V value;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            nodes = new LinkedList<Node<K,V>>();
        }

        public List<Node<K, V>> getNodes() {
            return nodes;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        private int hash() {
            return hashCode()%hashTable.length;
        }

        @Override
        public int hashCode() {
            hash = 31;
            hash = hash * 17 + key.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof Node) {
                Node<K,V> node = (Node) obj;
                return (Objects.equals(key,node.getKey()))&&
                        (Objects.equals(value,node.getValue())) ||
                        (Objects.equals(hash,node.hashCode()));

            }

            return false;
        }
    }
}
