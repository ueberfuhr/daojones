package de.ars.daojones.sdk.codegen.generators;

/**
 * A utility class providing special characters for the template.
 * Use $esc to refer to this object.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class EscapeTool {

    /**
     * Returns the new line character.
     * @return the new line character
     */
    public char nl() {
        return '\n';
    }
    
    /**
     * Returns the hash character.
     * @return the hash character
     */
    public char h() {
        return '#';
    }

    /**
     * Returns the dollar character.
     * @return the dollar character
     */
    public char d() {
        return '$';
    }

}
