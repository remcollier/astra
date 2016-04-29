package astra.ide.editor.astra;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class ASTRATextHover implements ITextHover {

	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (!(textViewer instanceof ISourceViewer)) {
			return null;
		}
		
		ISourceViewer sourceViewer= (ISourceViewer) textViewer;
		IAnnotationModel model= sourceViewer.getAnnotationModel();
		
		if (model != null) {
			String message= getAnnotationModelHoverMessage(model, hoverRegion);
			if (message != null) {
				return message;
			}
		}
		return null;
	}
	
	private String getAnnotationModelHoverMessage(IAnnotationModel model, IRegion hoverRegion) {
		Iterator<?> it = model.getAnnotationIterator();
		while (it.hasNext()) {
			Object object = it.next();
			if (object instanceof MarkerAnnotation) {
				MarkerAnnotation annotation = (MarkerAnnotation) object;
				Position p = model.getPosition(annotation);
				if (p.overlapsWith(hoverRegion.getOffset(), hoverRegion.getLength())) {
					return annotation.getMarker().getAttribute(IMarker.MESSAGE, "");
				}
			}
		}
		return null;
	}
	
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if (textViewer != null) {
			return getRegion(textViewer, offset);
		}
		return null;
	}

	public static IRegion getRegion(ITextViewer textViewer, int offset) {
		IDocument document= textViewer.getDocument();
		
		int start= -1;
		int end= -1;
	    IRegion region= null;
		try {
			int pos= offset;
			char c;
            
            if (document.getChar(pos) == '"') {
                pos--;
            }
			while (pos >= 0) {
				c= document.getChar(pos);
				if (c != '.' && c != '-' && c != '/' &&  c != '\\' && c != ' ' && c != ')' && c != '('&& c != ':' && !Character.isJavaIdentifierPart(c) && pos != offset)
					break;
				--pos;
			}
			
			start= pos;
			
			pos= offset;
			int length= document.getLength();
			
			while (pos < length) {
				c= document.getChar(pos);
				if (c != '.' && c != '-' && c != '/' &&  c != '\\' && c != ' ' && c != ')' && c != '('&& c != ':' && !Character.isJavaIdentifierPart(c))
					break;
                if (c == '/' && (document.getLength() - 1) > (pos + 1) && document.getChar(pos + 1) == '>') {
                   //e.g. <name/>
                    break;
                }
				++pos;
			}
			
			end= pos;
			
		} catch (BadLocationException x) {
		}
		
		if (start > -1 && end > -1) {
			if (start == offset && end == offset) {
				return new Region(offset, 0);
			} else if (start == offset) {
				return new Region(start, end - start);
			} else {
                try { //correct for spaces at beginning or end
                    while(document.getChar(start + 1) == ' ') {
                        start++;
                    }
                    while(document.getChar(end - 1) == ' ') {
                        end--;
                    }
                } catch (BadLocationException e) {
                }
                region= new Region(start + 1, end - start - 1);
            }
        }
        
        if (region != null) {
            try {
                char c= document.getChar(region.getOffset() - 1);
				if (c == '"') {
					if (document.get(offset, region.getLength()).indexOf(',') != -1) {
						region = cleanRegionForNonProperty(offset, document, region);
					}
				} else if (c != '{') {
                	region = cleanRegionForNonProperty(offset, document, region);
                }
            } catch (BadLocationException e) {
            }
        }
            
		return region;
	}

	private static IRegion cleanRegionForNonProperty(int offset, IDocument document, IRegion region) throws BadLocationException {
		//do not allow spaces in region that is not a property
		String text= document.get(region.getOffset(), region.getLength());
		if (text.startsWith("/")) { //$NON-NLS-1$
			text= text.substring(1);
			region= new Region(region.getOffset() + 1, region.getLength() - 1);
		}
		StringTokenizer tokenizer= new StringTokenizer(text, " "); //$NON-NLS-1$
		if (tokenizer.countTokens() != 1) {
		    while(tokenizer.hasMoreTokens()) {
		        String token= tokenizer.nextToken();
		        int index= text.indexOf(token);
		        if (region.getOffset() + index <= offset && region.getOffset() + index + token.length() >= offset) {
		            region= new Region(region.getOffset() + index, token.length());
		            break;
		        }
		    }
		}
		
		return region;
	}    		

}
