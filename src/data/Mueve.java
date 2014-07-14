package data;


/**
 * Esta clase implementa una hebra que hace que se mueva continuamente la Figura actual.
 *  La hebra se encarga tambien de ir refrescando la pantalla
 * donde se dibuja todo
 * @author Jose y Raul
 */
public interface Mueve extends Runnable{
    @Override
    public void run();        
    /**
     * Detiene momentaneamente la ejecución de la hebra, haciendo que la Figura actual
     * quede parada.
     */
    public void suspender();
    
    /**
     * Reanuda el movimiento de la hebra. La Figura actual vuelve  a moverse.
     */
    public void reanudar();
       
    
    /**
     * Termina la ejecución de la hebra.
     */
    public void parar();
    
    /**
     * Nos dice si la hebra está o no parada.
     * @return true si la hebra de movimiento está parada, false en otro caso
     */
    public boolean getParado();
    

}
 
