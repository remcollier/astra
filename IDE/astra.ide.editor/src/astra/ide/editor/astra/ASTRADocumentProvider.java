package astra.ide.editor.astra;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class ASTRADocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner = new FastPartitioner(
					new ASTRAPartitionScanner(), new String[] {
							ASTRAPartitionScanner.ASTRA_COMMENT,
							ASTRAPartitionScanner.ASTRA_STRING,
							ASTRAPartitionScanner.ASTRA_KEYWORD,
							ASTRAPartitionScanner.ASTRA_PERFORMATIVE,
							ASTRAPartitionScanner.ASTRA_PUNCTUATION,
							IDocument.DEFAULT_CONTENT_TYPE});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}