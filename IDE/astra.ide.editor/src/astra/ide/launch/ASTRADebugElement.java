package astra.ide.launch;

import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

public abstract class ASTRADebugElement extends DebugElement {

        public ASTRADebugElement(IDebugTarget target) {
                super(target);
        }

        @Override
        public String getModelIdentifier() {
                return Activator.PLUGIN_ID;
        }
}