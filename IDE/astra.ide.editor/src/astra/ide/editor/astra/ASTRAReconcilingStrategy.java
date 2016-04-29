package astra.ide.editor.astra;

import java.util.ArrayList;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;

public class ASTRAReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
	private ASTRAEditor editor;
	private IDocument fDocument;

	/** holds the calculated positions */
	protected final ArrayList<Position> fPositions = new ArrayList<Position>();

	/** The offset of the next character to be read */
	protected int fOffset;

	/** The end offset of the range to be scanned */
	protected int fRangeEnd;

	/**
	 * @return Returns the editor.
	 */
	public ASTRAEditor getEditor() {
		return editor;
	}

	public void setEditor(ASTRAEditor editor) {
		this.editor = editor;
	}

	public void setDocument(IDocument document) {
		this.fDocument = document;
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		initialReconcile();
	}

	public void reconcile(IRegion partition) {
		initialReconcile();
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
	}

	public void initialReconcile() {
		fOffset = 0;
		fRangeEnd = fDocument.getLength();
		calculatePositions();
	}

	private int cNextPos = 0;

	private void calculatePositions() {
		fPositions.clear();
		cNextPos = fOffset;

		try {
			recursiveTokens(0);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				editor.updateFoldingStructure(fPositions);
			}

		});
	}
	
	class Mark {
		int offset;
		int newLines;

		public Mark(int offset, int newLines) { 
			this.offset = offset;
			this.newLines = newLines;
		}
	}
	
	/**
	 * emits tokens to {@link #fPositions}.
	 * 
	 * @return number of newLines
	 * @throws BadLocationException
	 */
	protected int recursiveTokens(int depth) throws BadLocationException {
		// Stack is supported for braces because we can have multiple blocks
		// Only one mark for comments though!
		Stack<Mark> braceStack = new Stack<Mark>();
		Mark comMark = null;
		
		char cLastNLChar = ' ';
		int newLines = 0;
		while (cNextPos < fRangeEnd) {
			while (cNextPos < fRangeEnd) {
				char ch = fDocument.getChar(cNextPos++);
				switch (ch) {
				case '/':
					ch = fDocument.getChar(cNextPos++);
					if ('*' == ch) {
						if (comMark == null) {
							comMark = new Mark(cNextPos-2, newLines);
						}
					}
					break;
				case '*':
					ch = fDocument.getChar(cNextPos++);
					if ('/' == ch) {
						if (comMark != null) {
							if (newLines > comMark.newLines + 1) {
								emitPosition(comMark.offset, cNextPos - comMark.offset);
							}
							comMark = null;
						}
					}
					break;
				case '{':
					if (comMark != null) break;
					braceStack.push(new Mark(cNextPos-1, newLines));
					break;
				case '}':
					if (comMark != null) break;
					Mark mark = braceStack.pop();
					if (newLines > mark.newLines + 1) {
						emitPosition(mark.offset, cNextPos - mark.offset);
					}
				case '\n':
				case '\r':
					if ((ch == cLastNLChar) || (' ' == cLastNLChar)) {
						newLines++;
						cLastNLChar = ch;
					}
					break;
				default:
					break;
				}
			}
		}
		return newLines;
	}

	protected void emitPosition(int startOffset, int length) {
		fPositions.add(new Position(startOffset, length));
	}
}
