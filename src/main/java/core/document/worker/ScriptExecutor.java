package core.document.worker;

import java.awt.Cursor;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.apache.log4j.Logger;

import action.api.ScriptApi;
import core.document.Document;
import core.document.NotifyInterface;

public class ScriptExecutor {
    private static final Logger log = Logger.getLogger(ScriptExecutor.class);

    private abstract class Worker extends SwingWorker<Void, Object>{
    	public void changeProgress(int progress){
    		super.setProgress(progress);
    	}
    	public Vector<ScriptApi> scripts = new Vector<ScriptApi>();
    	public Vector<ACTION> actions = new Vector<ACTION>();
    }
	
	private Worker worker = null;
	
	private Timer timer = new Timer();
    // «адача таймеру дл€ показа окошка "зан€т"
    private TimerTask tt;
    
    private final Object workerBlock = new Object();
    private final Object thrdLock = new Object();
    
    private Thread workerThread = null;
    
    private void setWorkerThread(Thread thrd){
    	synchronized (thrdLock) {
        	workerThread = thrd;
		}
    }

    private Thread getWorkerThread(){
    	synchronized (thrdLock) {
        	return workerThread;
		}
    }

    class BusyTask extends TimerTask {

        public void run() {
            synchronized (workerBlock) {
                if (worker == null || worker.isDone() || worker.isCancelled()){
                	return;
                }
			}
            document.getPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//            if (worker != null && !worker.isDone() && !worker.isCancelled()) {
            document.getDocContainer().lockFrame();
//            }
        }
    };
	
	
	
	private Document document = null;
	
	public ScriptExecutor(Document doc) {
		document = doc;
	}

	public synchronized void setProgress(int progress){
		if(worker != null)
			worker.changeProgress(progress);
	}
	
	
    public Object executeJavaScript(String script){
        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine engine = m.getEngineByName("js");
        Bindings bindings = engine.createBindings();
        bindings.putAll(document.getAliases());
        
        try {
            return engine.eval(script, bindings);
        } catch (ScriptException e) {
            log.error("", e);
            return null; 
        }
    }    

	
    public synchronized void executeScript(String script) throws Exception {
        if(script == null || script.trim().equals(""))
            return;
        
        ScriptApi calc = ScriptApi.getAPI(script);
        
        if (Thread.currentThread().equals(getWorkerThread())){ //(worker != null && worker.getState() == StateValue.STARTED) {
        	calc.eval(document.getAliases());
        } else {
        	synchronized (workerBlock) {
				
	            if (worker == null) {
	            	worker = new Worker() {
						@Override
						protected Void doInBackground() {
							setWorkerThread(Thread.currentThread());
							while(scripts.size() > 0){
		                        try {
		                        	scripts.remove(0).eval(document.getAliases());
		                        } catch (Exception ex) {
		                        	log.error("", ex);
		                        }
							}
							return null;
						}
						@Override
					    protected void done() {
							resetCalcThread();
							document.getDocContainer().unlockFrame();		
						}
	                };
	                worker.scripts.add(calc);
	                worker.execute();
	                timer.schedule(tt = new BusyTask(), 0);
	            }else {
	                //TODO:
	                //Ќа случай когда документ может получить несколько запросов на вычисление из разных потоков
	            	//¬ этом случае скрипты став€тс€ в очередь
	            	worker.scripts.add(calc);
	            }
	        }
		}
    }

    public synchronized void doAction(String action, NotifyInterface ni) throws Exception {
        if ((action == null) || (action.trim().length() == 0)) {
            return;
        }
        final ACTION act = new ACTION(document);
        act.prepareAction(action, document.getAliases(), ni);

//    	synchronized (block) {
	        if (Thread.currentThread().equals(getWorkerThread())) {
	        	act.runAction();
	        } else {
	        	
        	synchronized (workerBlock) {
	            if (worker == null) {
	            	worker = new Worker() {
	
						@Override
						protected Void doInBackground() {
//		            		document.getDocContainer().lockFrame();
							setWorkerThread(Thread.currentThread());
							while(actions.size() > 0){	                        
								try {
									actions.remove(0).runAction();
		                        } catch (Exception ex) {
		                        	log.error("", ex);
		                        }
							}
							return null;
						}
						@Override
					    protected void done() {
							resetCalcThread();
							document.getDocContainer().unlockFrame();		
						}
	                };
	                worker.actions.add(act);
	                worker.execute();
	                timer.schedule(tt = new BusyTask(), 0);
	            }else {
	                //TODO:
	                //Ќа случай когда документ может получить несколько запросов на вычисление из разных потоков
	            	//¬ этом случае скрипты став€тс€ в очередь
	            	worker.actions.add(act);	
	            }
	        }
    	}
    }

    public synchronized void resetCalcThread() {
    	synchronized (workerBlock) {
    		setWorkerThread(null);
	    	if(worker.getState() == StateValue.STARTED){
	    		worker.cancel(true);
	    	}
	        worker = null;
	        tt.cancel();
	        document.getPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
    }
}
