package scrabble;
import java.util.*;

/**
 * TrieNode class contains methods and constructor for a Trie Node.
 * @author Sam Cox
 * @version date 3/6/20
 */
public class TrieNode {

    private char letter;
    private Map<Character, TrieNode> children = new HashMap<>();
    private boolean isLeaf = false;

    /**
     * Empty TieNode Constructor makes an empty TrieNode.
     */
    public TrieNode(){

    }

    /**
     * TrieNode constructor makes a trienode and sets the letter
     * to the given letter.
     * @param letter letter of node
     */
    public TrieNode(char letter){

        this.letter = letter;
    }

    /**
     * Gets the next letters from the node
     * @return map of characters to next nodes
     */
    protected Map<Character, TrieNode> getChildren(){

        return children;
    }

    /**
     * Checks node is a terminal node.
     * @return true or false
     */
    protected boolean isLeaf(){

        return isLeaf;
    }

    /**
     * Sets the node to be a terminal node.
     */
    protected void setLeaf(){

        this.isLeaf = true;
    }

}
