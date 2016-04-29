package astra.ide.editor.astra;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordRule;

class WordPredicateRule extends WordRule implements IPredicateRule {
    private IToken fSuccessToken;

    public WordPredicateRule(IWordDetector detector,  IToken successToken, IToken defaultToken) {
        super(detector, defaultToken);
        fSuccessToken = successToken;
    }
    
    
    /*
     * @see org.eclipse.jface.text.rules.IPredicateRule#
     * evaluate(ICharacterScanner, boolean)
     */
    public IToken evaluate(ICharacterScanner scanner, boolean resume) {
        return super.evaluate(scanner);
    }

    
    /*
     * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
     */
    public IToken getSuccessToken() {
        return fSuccessToken;
    }        
}
