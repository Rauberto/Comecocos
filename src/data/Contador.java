package data;

/**
 * Esta clase implementa una hebra que se encarga basicamente de ir contando el tiempo que
 * pasa en segundos. Cuenta con cuatro datos miembro: dos boolean; continuar y suspendFlag
 * para controlar las hebras, frame, un objeto de la clase ComeFrame, y vueltas, un dato para controlar
 * los segundos
 * @author Jose y Raul
 */
public class Contador implements Runnable{
    private boolean continuar=true;
    private boolean suspendFlag=true;
    private int vueltas;
    private LogicaJuego logicaJuego;
       
    /**
     * Constructor de la clase, que inicializa la referencia utilizadas por
     * la hebra al Comecocos, establece el retardo en milisegundos
     * entre movimiento y movimiento de la Figura actual, y comienza a ejecutar
     * la hebra. 
     */   
    public Contador(LogicaJuego logicaJuego){
    	this.logicaJuego=logicaJuego;
        vueltas=0;
        Thread t=new Thread(this);
        t.start();
    }
    
    
    /**
     * Metodo principal de la hebra que controla cuando pasa exactamente un segundo. En ese caso, 
     * indicamos al frame que estamos controlando que ha pasado un segundo.
     */
    @Override
    public void run(){
        try{
            while(continuar){
                synchronized(this){
                    while(suspendFlag){
                        wait();
                    }
                }
                //cuando da 10 vueltas realmente pasa un segundo
                //esto lo hacemos asi para que al suspender la hebra no se pierda siempre un segundo
                if(vueltas==10){
                    vueltas=0;
                    logicaJuego.pasarUnSegundo();
                }
                Thread.sleep(100);
                vueltas++;
            }// end while(continuar)
        } catch(InterruptedException e){
            System.out.println("Hilo Mueve interrumpido");
        }
    }

    /**
     * Detiene momentaneamente la ejecución de la hebra, haciendo que el contador actual
     * quede parado.
     */
    synchronized public void suspender(){
        suspendFlag=true;
    }
    
    /**
     * Reanuda el movimiento de la hebra. El contador actual vuelve  a moverse.
     */
    public synchronized void reanudar(){
        suspendFlag = false;
        notify();
    }
    /**
     * Termina la ejecución de la hebra.
     */
    public void parar(){
        continuar=false;
    }
    
    /**
     * Nos dice si la hebra está o no parada.
     * @return true si la hebra de movimiento está parada, false en otro caso
     */
    synchronized public boolean getParado(){
        return suspendFlag;
    }
    
}
 
