package astra.ide.editor.astra;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class ASTRAPartitionScanner extends RuleBasedPartitionScanner {
	public final static String ASTRA_COMMENT = "__astra_comment";
	public final static String ASTRA_STRING = "__astra_string";
	public final static String ASTRA_KEYWORD = "__astra_keyword";
	public final static String ASTRA_PERFORMATIVE = "__astra_performative";
	public final static String ASTRA_PUNCTUATION = "__astra_punctuation";
	
	String[] keywords = new String[] {"try", "recover", "if", "else", "rule",
			"import", "module", "foreach", "forall", "while", "when", "wait", "initial",
			"plan", "agent", "extends", "abstract", "string", "int", "long",
			"float", "double", "char", "boolean", "object", "list", "start",
			"stop", "active", "count_bindings", "function", "query", "send", 
			"@message", "@acre", "acre_add_repository", "acre_start",
			"acre_advance", "acre_history", "acre_forget", "acre_cancel",
			"acre_confirm_cancel", "acre_deny_cancel", "acre_set_timeout", "package",
			"@acre", "acre_start", "acre_advance", "acre_cancel", "acre_confirm_cancel",
			"acre_deny_cancel", "synchronized", "true", "false", "speechact",
			"funct", "formula","inference", "bind", "types"
	};
	
	String[] constants = new String[] {
			"accept-proposal", "agree", "cancel", "cfp", "confirm", "disconfirm",
	  		"failure", "inform", "inform-if", "inform-ref", "not-understood", "propogate",
	  		"propose", "proxy", "query-if", "query-ref", "refuse", "reject-proposal",
	  		"request", "request-when", "request-whenever", "subscribe", "added",
	  		"removed", "updated", "started", "ended", "failed", "cancel_request",
	  		"cancel_fail", "cancel_confirm", "timeout", "advanced", "unmatched",
	  		"ambiguous", "message"
	};
	
	char[] punctuation = new char[] {
			'.', ',', '(', ')', '{', '}', '$'
	};
	
	public ASTRAPartitionScanner() {
		IToken comment = new Token(ASTRA_COMMENT);
		IToken string = new Token(ASTRA_STRING);
		IToken keyword = new Token(ASTRA_KEYWORD);
		IToken performative = new Token(ASTRA_PERFORMATIVE);
		IToken punct = new Token(ASTRA_PUNCTUATION);
		IToken other = new Token(IDocument.DEFAULT_CONTENT_TYPE);

		// Add keywords
		WordPredicateRule rule = new WordPredicateRule(new IWordDetector() {
            public boolean isWordStart(char c) {
            	if (c == '@') return true;
            	return Character.isJavaIdentifierStart(c);
            }
            public boolean isWordPart(char c) {
            	if (c == '-') return true;
            	return Character.isJavaIdentifierPart(c); 
            }
        }, keyword, other);

        for (String kwd: keywords) {
        	rule.addWord(kwd, keyword);
        }

        for (String perf: constants) {
        	rule.addWord(perf, performative);
        }

		WordPredicateRule rule2 = new WordPredicateRule(new IWordDetector() {
            public boolean isWordStart(char c) {
            	for (int i=0; i<punctuation.length; i++) {
            		if (punctuation[i] == c) return true;
            	}
            	return false;
            }
            public boolean isWordPart(char c) {
            	return false; 
            }
        }, punct, other);

        for (char p: punctuation) {
        	rule2.addWord(""+p, punct);
        }

		setPredicateRules(new IPredicateRule[] {
				rule, rule2,
				new EndOfLineRule("//", comment),
				new MultiLineRule("/*", "*/", comment),
				new SingleLineRule("\"", "\"", string, '\\'),
				new SingleLineRule("\'", "\'", string, '\\')
		});
	}
}

