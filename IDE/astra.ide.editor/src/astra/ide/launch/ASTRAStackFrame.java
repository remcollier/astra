package astra.ide.launch;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

public class ASTRAStackFrame extends ASTRADebugElement implements IStackFrame {

    private IThread thread;
    private int charPos;
    private String wordToBeSpelled;

    public ASTRAStackFrame(IThread thread, String wordToBeSpelled, int charPos) {
            super(thread.getDebugTarget());
            this.thread = thread;
            this.wordToBeSpelled = wordToBeSpelled;
            this.charPos = charPos;
    }

    @Override
    public boolean canStepInto() {
            return thread.canStepInto();
    }

    @Override
    public boolean canStepOver() {
            return thread.canStepOver();
    }

    @Override
    public boolean canStepReturn() {
            return thread.canStepReturn();
    }

    @Override
    public boolean isStepping() {
            return thread.isStepping();
    }

    @Override
    public void stepInto() throws DebugException {
            thread.stepInto();
    }

    @Override
    public void stepOver() throws DebugException {
            thread.stepOver();
    }

    @Override
    public void stepReturn() throws DebugException {
            thread.stepReturn();
    }

    @Override
    public boolean canResume() {
            return thread.canResume();
    }

    @Override
    public boolean canSuspend() {
            return thread.canSuspend();
    }

    @Override
    public boolean isSuspended() {
            return thread.isSuspended();
    }

    @Override
    public void resume() throws DebugException {
            thread.resume();
    }

    @Override
    public void suspend() throws DebugException {
            thread.suspend();
    }

    @Override
    public boolean canTerminate() {
            return thread.canTerminate();
    }

    @Override
    public boolean isTerminated() {
            return thread.isTerminated();
    }

    @Override
    public void terminate() throws DebugException {
            thread.terminate();
    }

    @Override
    public IThread getThread() {
            return thread;
    }

    @Override
    public IVariable[] getVariables() throws DebugException {
            return new IVariable[0];
    }

    @Override
    public boolean hasVariables() throws DebugException {
            return false;
    }

    @Override
    public int getLineNumber() throws DebugException {
            return charPos;
    }

    @Override
    public int getCharStart() throws DebugException {
            return charPos;
    }

    @Override
    public int getCharEnd() throws DebugException {
            return charPos + 1;
    }

    @Override
    public String getName() throws DebugException {
            return "Char '" + wordToBeSpelled.charAt(charPos) + "' position : " + getCharStart();
    }

    @Override
    public IRegisterGroup[] getRegisterGroups() throws DebugException {
            return new IRegisterGroup[0];
    }

    @Override
    public boolean hasRegisterGroups() throws DebugException {
            return false;
    }

//    @Override
//    public int hashCode() {
//            final int prime = 31;
//            int result = 1;
//            result = prime * result + charPos;
//            result = prime * result + ((thread == null) ? 0 : thread.hashCode());
//            result = prime * result + ((wordToBeSpelled == null) ? 0 : wordToBeSpelled.hashCode());
//            return result;
//    }

    @Override
    public boolean equals(Object obj) {
            if (this == obj)
                    return true;
            if (obj == null)
                    return false;
            if (getClass() != obj.getClass())
                    return false;
            ASTRAStackFrame other = (ASTRAStackFrame) obj;
            if (charPos != other.charPos)
                    return false;
            if (thread == null) {
                    if (other.thread != null)
                            return false;
            } else if (!thread.equals(other.thread))
                    return false;
            if (wordToBeSpelled == null) {
                    if (other.wordToBeSpelled != null)
                            return false;
            } else if (!wordToBeSpelled.equals(other.wordToBeSpelled))
                    return false;
            return true;
    }
}