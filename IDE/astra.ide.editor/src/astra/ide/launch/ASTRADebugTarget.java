package astra.ide.launch;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

public class ASTRADebugTarget extends ASTRADebugElement implements IDebugTarget {

    private IProcess process;
    private ILaunch launch;

    private ASTRAThread thread;

    // the IProcess and ILaunch are provided by an IDebugTarget and therefore
    // they should be passed. The getProcess() and getLaunch() method should be
    // overridden accordingly. See DebugElement implementation
    public ASTRADebugTarget(IProcess process, ILaunch launch, String wordToBeSpelled) {
            super(null);
            this.process = process;
            this.launch = launch;
            this.thread = new ASTRAThread(this, wordToBeSpelled);

            // notify that this element has been created
            fireCreationEvent();
    }

    // must be overridden since "this" cannot be passed in a constructor
    @Override
    public IDebugTarget getDebugTarget() {
            return this;
    }

    @Override
    public IProcess getProcess() {
            return this.process;
    }

    @Override
    public ILaunch getLaunch() {
            return launch;
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
    public void breakpointAdded(IBreakpoint breakpoint) {
    }

    @Override
    public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
    }

    @Override
    public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
    }

    @Override
    public boolean canDisconnect() {
            return false;
    }

    @Override
    public void disconnect() throws DebugException {
    }

    @Override
    public boolean isDisconnected() {
            return false;
    }

    @Override
    public boolean supportsStorageRetrieval() {
            return false;
    }

    @Override
    public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
            return null;
    }

    @Override
    public IThread[] getThreads() throws DebugException {
            if (isTerminated()) {
                    return new IThread[0];
            }
            return new IThread[]{thread};
    }

    @Override
    public boolean hasThreads() throws DebugException {
            return true;
    }

    @Override
    public String getName() throws DebugException {
            return "Example Debug target";
    }

    @Override
    public boolean supportsBreakpoint(IBreakpoint breakpoint) {
            return false;
    }

    public void dispose() {
            fireTerminateEvent();
    }
}