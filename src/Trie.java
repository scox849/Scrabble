package scrabble;
import java.io.InputStream;
import java.util.*;

/**
 * Trie object contains methods and constructor for trie object.
 * @author Sam Cox
 * @version date 3/6/20
 */
public class Trie {

    private TrieNode root;

    /**
     * Constructor for Trie object makes a new tree based on an input file.
     * @param file file name
     */
    public Trie(String file){

        root = new TrieNode();
        this.readInDictionary(file);
    }

    /**
     * Reads in the dictionary from a class loader.
     * @param file file name
     */
    private void readInDictionary(String file){
        InputStream dictionary = getClass().getClassLoader().
                getResourceAsStream(file);
        assert dictionary != null;
        Scanner scanner = new Scanner(dictionary);

        while(scanner.hasNextLine()){
            String word = scanner.nextLine();
            this.insert(word);
        }
    }

    /**
     * Inserts a word into the Trie.
     * @param word word to be inserted
     */
    protected void insert(String word){

        Map<Character, TrieNode> children = this.root.getChildren();
        word = word.toLowerCase();
        for(int i = 0; i < word.length(); i++){
            char newChar = word.charAt(i);
            TrieNode node;
            if(children.containsKey(newChar)){
                node = children.get(newChar);
            }else{
                node = new TrieNode(newChar);
                children.put(newChar, node);
            }
            children = node.getChildren();

            if(i ==  word.length() - 1){
                node.setLeaf();
            }
        }

    }

    /**
     * Searches for a partial word.
     * @param partial partial word to be checked
     * @return true or false if partial is in trie or not
     */
    protected boolean searchPartial(String partial){
        String copy;
        Map<Character, TrieNode> children = this.root.getChildren();
        TrieNode node;
        copy = partial.toLowerCase();
        for(int i =0; i < copy.length(); i++){
            char check = copy.charAt(i);
            if(children.containsKey(check)){
                node = children.get(check);
                children = node.getChildren();
            }else{
                return false;
            }
        }
        return true;
    }

    /**
     * Searches for a full word in the tree.
     * @param word word to search for
     * @return true or false
     */
    protected boolean search(String word){

        String copy;
        Map<Character, TrieNode> children = this.root.getChildren();
        TrieNode node = null;
        copy = word.toLowerCase();
        for(int i =0; i < copy.length(); i++){
            char check = copy.charAt(i);
            if(children.containsKey(check)){
                node = children.get(check);
                children = node.getChildren();
            }else{
                node = null;
                break;
            }
        }
        return node != null && node.isLeaf();
    }

    /**
     * Returns the possible next letter for a given node.
     * @param partialWord word to get to node
     * @return set of possible next characters
     */
    protected Set<Character> getNodeKeys(String partialWord){

        Map<Character, TrieNode> children = this.root.getChildren();
        TrieNode node;
        String copy;
        Set<Character> keys;

        copy = partialWord.toLowerCase();
        for(int i = 0; i < copy.length(); i++){
            char check = copy.charAt(i);
            if(children.containsKey(check)){
                node = children.get(check);
                children = node.getChildren();
            }else{
                break;
            }
        }
        keys = children.keySet();
        return keys;
    }

}
