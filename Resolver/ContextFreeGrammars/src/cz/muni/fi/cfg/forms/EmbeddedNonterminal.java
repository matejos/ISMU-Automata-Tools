package cz.muni.fi.cfg.forms;

/**
 * @author      Daniel Pelisek <dpelisek@gmail.com>
 * @version     1.1                 
 * @since       2011-10-22
 */
public class EmbeddedNonterminal {

    private String name;
    private boolean left;
    private boolean right;

    /**
     * constructor of embedded nonterminal
     * 
     * @param name is the nonterminal symbol
     * @param left is true if some terminal symbols are on left side of nonterminal
     * @param right is true if some terminal symbols are on right side of nonterminal
     */
    public EmbeddedNonterminal(String name, boolean left, boolean right) {
        this.name = name;
        this.left = left;
        this.right = right;
    }
    
    /**
     * updates the values of left and right
     * 
     * keeps true if original value was true or changed to true if original
     * value was false and new value is true
     * 
     * @param left is true if some terminal symbols are on left side of nonterminal
     * @param right is true if some terminal symbols are on right side of nonterminal
     * @return true if some values were changed, false otherwise
     */
    public boolean update(boolean left, boolean right) {
        boolean result = false;
        if (left && !this.left)
            result = this.left = true;
        if (right && !this.right)
            result = this.right = true;
        return result;
    }

    /**
     * check if there are any terminal symbols on each side of nonterminal symbol
     * 
     * @return true if there are any terminal symbols on each side, false otherwise
     */
    public boolean isInside() {
        return (left && right);
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean left() {
        return left;
    }

    public boolean right() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EmbeddedNonterminal))
            return false;
        EmbeddedNonterminal other = (EmbeddedNonterminal) o;
        return name.equals(other.name) && left == other.left && right == other.right;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.left ? 1 : 0);
        hash = 59 * hash + (this.right ? 1 : 0);
        return hash;
    }
}