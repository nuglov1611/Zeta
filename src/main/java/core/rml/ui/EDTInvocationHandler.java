package core.rml.ui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.swing.SwingUtilities;

import org.jfree.util.Log;

import core.rml.ui.interfaces.ZComponent;

/**
 * EDT invocation handler. Intercepts calls to the interface and invokes methods in EDT
 * according to the {@link RequiresEDTPolicy}
 *
 * @author Eugene Matyushkin aka Skipy
 *   
 * @since 13.08.2010
 */
public class EDTInvocationHandler implements InvocationHandler {

    /**
     * Method invocation result
     */
    private Object invocationResult = null;

    /**
     * Target object to translate method's call
     */
    private ZComponent ui;

    /**
     * Creates invocation handler
     *
     * @param ui target object
     */
    public EDTInvocationHandler(ZComponent ui) {
        this.ui = ui;
    }

    /**
     * Invokes method on target object. If {@link RequiresEDT} annotation present,
     * method is invoked in the EDT thread, otherwise - in current thread.
     *
     * @param proxy  proxy object
     * @param method method to invoke
     * @param args   method arguments
     * @return invocation result
     * @throws Throwable if error occures while calling method
     */
    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        invocationResult = null;
    	
        RequiresEDT mark = method.getAnnotation(RequiresEDT.class);
        if(method.getName().equals("add")){
        	Log.debug("");
        }
        if (mark != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                invocationResult = method.invoke(ui, args);
            } else {
                Runnable shell = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            invocationResult = method.invoke(ui, args);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                };
                if (RequiresEDTPolicy.ASYNC.equals(mark.value())) {
                    SwingUtilities.invokeLater(shell);
                } else {
                    SwingUtilities.invokeAndWait(shell);
                }
            }
        } else {
            invocationResult = method.invoke(ui, args);
        }
        return invocationResult;
    }
}