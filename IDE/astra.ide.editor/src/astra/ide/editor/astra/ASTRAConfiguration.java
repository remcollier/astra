package astra.ide.editor.astra;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class ASTRAConfiguration extends SourceViewerConfiguration {
	private ASTRADoubleClickStrategy doubleClickStrategy;
	private ASTRAScanner scanner;
	private ASTRAEditor editor;
	private ColorManager colorManager;

	public ASTRAConfiguration(ColorManager colorManager,ASTRAEditor editor) {
		this.colorManager = colorManager;
		this.editor = editor;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			ASTRAPartitionScanner.ASTRA_COMMENT };
	}
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new ASTRADoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected ASTRAScanner getASTRAScanner() {
		if (scanner == null) {
			scanner = new ASTRAScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IASTRAColorConstants.DEFAULT))));
		}
		return scanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(IASTRAColorConstants.DEFAULT)));
		reconciler.setDamager(ndr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(ndr, IDocument.DEFAULT_CONTENT_TYPE);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(IASTRAColorConstants.ASTRA_COMMENT)));
		reconciler.setDamager(ndr, ASTRAPartitionScanner.ASTRA_COMMENT);
		reconciler.setRepairer(ndr, ASTRAPartitionScanner.ASTRA_COMMENT);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(IASTRAColorConstants.KEYWORD)));
		reconciler.setDamager(ndr, ASTRAPartitionScanner.ASTRA_KEYWORD);
		reconciler.setRepairer(ndr, ASTRAPartitionScanner.ASTRA_KEYWORD);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(IASTRAColorConstants.PERFORMATIVE)));
		reconciler.setDamager(ndr, ASTRAPartitionScanner.ASTRA_PERFORMATIVE);
		reconciler.setRepairer(ndr, ASTRAPartitionScanner.ASTRA_PERFORMATIVE);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(IASTRAColorConstants.DEFAULT)));
		reconciler.setDamager(ndr, ASTRAPartitionScanner.ASTRA_PUNCTUATION);
		reconciler.setRepairer(ndr, ASTRAPartitionScanner.ASTRA_PUNCTUATION);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(IASTRAColorConstants.STRING)));
		reconciler.setDamager(ndr, ASTRAPartitionScanner.ASTRA_STRING);
		reconciler.setRepairer(ndr, ASTRAPartitionScanner.ASTRA_STRING);
		return reconciler;

	}

	public IReconciler getReconciler(ISourceViewer sourceViewer) {
        ASTRAReconcilingStrategy strategy = new ASTRAReconcilingStrategy();
        strategy.setEditor(editor);
        
        MonoReconciler reconciler = new MonoReconciler(strategy,false);
        
        return reconciler;
    }
	
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant assistant = new ContentAssistant();
        assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
        assistant.setContentAssistProcessor(new ASTRAContentAssistantProcessor(),IDocument.DEFAULT_CONTENT_TYPE);
        assistant.setContentAssistProcessor(new ASTRAContentAssistantProcessor(),ASTRAPartitionScanner.ASTRA_STRING);
        assistant.enableAutoActivation(true);
        assistant.enableAutoInsert(true);
        assistant.enablePrefixCompletion(true);
        assistant.setAutoActivationDelay(0);
        return assistant;
    }
    
    @Override
    public ITextHover getTextHover(ISourceViewer viewer, String  contentType) {
    	return new ASTRATextHover();
    }  
 }