package astra.ide.launch;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

public class ASTRAThread extends ASTRADebugElement implements IThread {

    // The different states, which this IThread can have
    private enum State {
            RUNNING, STEPPING, SUSPENDED, TERMINATED
    }

    private State currentState;

    private State requestedState;

    private Thread thread;

    private int charPos;

    private final String wordToBeSpelled;

    public ASTRAThread(IDebugTarget target, String wordToBeSpelled) {
            super(target);
            this.wordToBeSpelled = wordToBeSpelled;
            thread = new Thread(new Resume());
            thread.start();
            fireCreationEvent();
    }

    @Override
    public boolean canResume() {
            return isSuspended();
    }

    @Override
    public boolean canSuspend() {
            return !isTerminated() && !isSuspended();
    }

    @Override
    public boolean isSuspended() {
            return State.SUSPENDED.equals(currentState);
    }

    @Override
    public void resume() throws DebugException {
            synchronized (this) {
                    requestedState = State.RUNNING;
                    thread = new Thread(new Resume());
                    thread.start();
            }
    }

    @Override
    public void suspend() throws DebugException {
            synchronized (this) {
                    requestedState = State.SUSPENDED;
                    thread.interrupt();
            }
    }

    @Override
    public synchronized boolean isStepping() {
            return State.STEPPING.equals(currentState);
    }

    @Override
    public boolean canStepOver() {
            return isSuspended();
    }

    @Override
    public void stepOver() throws DebugException {
            synchronized (this) {
                    requestedState = State.STEPPING;
                    thread = new Thread(new StepOver());
                    thread.start();
            }
    }

    @Override
    public boolean canStepInto() {
            return false;
    }

    @Override
    public boolean canStepReturn() {
            return false;
    }

    @Override
    public void stepInto() throws DebugException {
    }

    @Override
    public void stepReturn() throws DebugException {
    }

    @Override
    public boolean canTerminate() {
            return !isTerminated();
    }

    @Override
    public boolean isTerminated() {
            return State.TERMINATED.equals(currentState);
    }

    @Override
    public void terminate() throws DebugException {
            synchronized (this) {
                    requestedState = State.TERMINATED;
                    if (isSuspended()) {
                            // run to termination
                            thread = new Thread(new Resume());
                            thread.start();
                    } else {
                            thread.interrupt();
                    }
            }
    }

    @Override
    public IStackFrame getTopStackFrame() throws DebugException {
            synchronized (this) {
                    if (isSuspended()) {
                            return new ASTRAStackFrame(this, wordToBeSpelled, charPos);
                    }
            }
            return null;
    }

    @Override
    public IStackFrame[] getStackFrames() throws DebugException {
            synchronized (this) {
                    if (isSuspended()) {
                            if (charPos < 1) {
                                    return new IStackFrame[] { new ASTRAStackFrame(this, wordToBeSpelled, charPos) };
                            }
                            IStackFrame[] frames = new IStackFrame[charPos + 1];
                            for (int i = charPos; i >= 0; i--) {
                                    frames[i] = new ASTRAStackFrame(this, wordToBeSpelled, charPos - i);
                            }
                            return frames;
                    }
            }
            return new IStackFrame[0];
    }

    @Override
    public boolean hasStackFrames() throws DebugException {
            // An IThread only has stack frames when it is suspended.
            return isSuspended();
    }

    @Override
    public int getPriority() throws DebugException {
            return 0;
    }

    @Override
    public String getName() throws DebugException {
            return "Example Thread with the word " + wordToBeSpelled;
    }

    @Override
    public IBreakpoint[] getBreakpoints() {
            return new IBreakpoint[0];
    }

    class StepOver implements Runnable {
            @Override
            public void run() {
                    synchronized (ASTRAThread.this) {
                            currentState = State.STEPPING;
                    }
                    fireResumeEvent(DebugEvent.STEP_OVER);
                    int event = doNextStep();
                    int detail = DebugEvent.UNSPECIFIED;
                    synchronized (ASTRAThread.this) {
                            // update state
                            switch (event) {
                            case DebugEvent.BREAKPOINT:
                                    currentState = State.SUSPENDED;
                                    detail = DebugEvent.BREAKPOINT;
                                    break;
                            case DebugEvent.UNSPECIFIED:
                                    currentState = State.SUSPENDED;
                                    detail = DebugEvent.STEP_END;
                                    break;
                            case DebugEvent.TERMINATE:
                                    currentState = State.TERMINATED;
                                    break;
                            case DebugEvent.SUSPEND:
                                    currentState = State.SUSPENDED;
                                    detail = DebugEvent.CLIENT_REQUEST;
                                    break;
                            }
                    }
                    switch (currentState) {
                    case SUSPENDED:
                            fireSuspendEvent(detail);
                            break;
                    case TERMINATED:
                            fireTerminateEvent();
                            ASTRADebugTarget target = (ASTRADebugTarget) getDebugTarget();
                            target.dispose();
                            break;
                    default:
                            break;
                    }
            }
    }

    class Resume implements Runnable {
            @Override
            public void run() {
                    synchronized (ASTRAThread.this) {
                            currentState = State.RUNNING;
                    }
                    fireResumeEvent(DebugEvent.CLIENT_REQUEST);
                    int detail = DebugEvent.UNSPECIFIED;
                    int event = DebugEvent.UNSPECIFIED;
                    while (event == DebugEvent.UNSPECIFIED) {
                            event = doNextStep();
                    }
                    synchronized (ASTRAThread.this) {
                            // update state
                            switch (event) {
                            case DebugEvent.BREAKPOINT:
                                    currentState = State.SUSPENDED;
                                    detail = DebugEvent.BREAKPOINT;
                                    break;
                            case DebugEvent.TERMINATE:
                                    currentState = State.TERMINATED;
                                    break;
                            case DebugEvent.SUSPEND:
                                    currentState = State.SUSPENDED;
                                    detail = DebugEvent.CLIENT_REQUEST;
                                    break;
                            }
                    }
                    switch (currentState) {
                    case SUSPENDED:
                            fireSuspendEvent(detail);
                            break;
                    case TERMINATED:
                            fireTerminateEvent();
                            ASTRADebugTarget target = (ASTRADebugTarget) getDebugTarget();
                            target.dispose();
                            break;
                    default:
                            break;
                    }
            }

    }

    private int doNextStep() {
            if (State.TERMINATED.equals(requestedState)) {
                    return DebugEvent.TERMINATE;
            }
            try {
                    Thread.sleep(100);
            } catch (InterruptedException e) {
                    switch (requestedState) {
                    case TERMINATED:
                            return DebugEvent.TERMINATE;
                    case SUSPENDED:
                            return DebugEvent.SUSPEND;
                    default:
                            break;
                    }
            }
            System.out.print(wordToBeSpelled.charAt(charPos));
            charPos++;
            if (charPos > wordToBeSpelled.length() - 1) {
                    System.out.println();
                    charPos = 0;
            }

            return DebugEvent.UNSPECIFIED;
    }
}
