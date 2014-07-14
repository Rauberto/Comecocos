package data;



/**
 * Esta clase implementa una hebra que hace que se mueva continuamente la Figura comecocos.
 *  La hebra se encarga tambien de ir refrescando la pantalla
 * dónde se dibuja todo. Además controla si el Comecocos
 * choca contra un muro o con los fantasmas. 
 * @author Jose y Raul
 */
public class MueveComecocos implements Mueve{
    private int delay;
    private boolean continuar=true;
    private boolean suspendFlag=true;
    private LogicaJuego logicaJuego;
       
    /**
     * Constructor de la clase, que inicializa la referencia utilizadas por
     * la hebra al Comecocos, establece el retardo en milisegundos
     * entre movimiento y movimiento de la Figura actual, y comienza a ejecutar
     * la hebra. 
     */
    public MueveComecocos(int nivel,LogicaJuego logicaJuego){
        this.logicaJuego=logicaJuego;
        delay= actualizaRetardo(nivel);
        Thread t=new Thread(this);
        t.start();
    }
    
    /**
     * Codigo que constituye las sentencias de la hebra. En este caso, se encarga
     * de hacer que se muevan continuamente el Comecocos y los Fantasmas.
     * La hebra se encarga tambien de ir refrescando la pantalla
     * donde se dibuja todo, y los puntos acumulados. Ademas controla si
     * el Comecocos se choca con los fantasmas.
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
                Thread.sleep(delay);
                Figura comecocos=logicaJuego.getComecocos();
                logicaJuego.pedirPermisoMover();
                actualizarFigura(comecocos);
                logicaJuego.devolverPermisoMover();
                if(logicaJuego.getAdaptadorVistaLogica()!=null)
                    logicaJuego.getAdaptadorVistaLogica().repaint();
            }// end while(continuar)
        } catch(InterruptedException e){
            System.out.println("Hilo Mueve interrumpido");
        }
    }
    
    /**
     * Metodo que se encarga de ir actualizando la figura y el juego del Comecocos segun que ocurra. 
     * Controla si perdemos la partida, si ganamos, si se choca el comecocos contra un muro y 
     * todo lo referente a los puntos que debemos comer para ganar la partida
     * @param comecocos Figura considerada 
     */
    private void actualizarFigura(Figura comecocos){
        //Comprobamos qué contiene la celda considerada
        switch(logicaJuego.getRejilla().getTipoCelda(comecocos.getX(), comecocos.getY())){
            //Si comemos un PGRANDE los fantasmas deben ser comestibles y subir los puntos correspondientes
            case Rejilla.PGRANDE:
                logicaJuego.setPuntos(logicaJuego.getPuntos()+LogicaJuego.PUNTOSGRANDE);
                logicaJuego.hacerFantasmasComestibles();                  
                break;
                //Si comemos un PPEQUE deben subirnos los puntos
            case Rejilla.PPEQUE:
                logicaJuego.setPuntos(logicaJuego.getPuntos()+LogicaJuego.PUNTOSPEQUE);
                break;
            default:
                break;
        }
        //Actualizamos el frame cuando nos hayamos comido el punto PEQUE o GRANDE
        logicaJuego.getRejilla().comerCasilla(comecocos.getX(), comecocos.getY());
        
        //Si chocamos con un fantasma, perdemos
        if(muertePorFantasmas(comecocos)){
            logicaJuego.lose();
        }
        
        //Si hemos acabado con todas las bolas, hemos ganado
        if(logicaJuego.getRejilla().getNumeroBolas()==0){
            logicaJuego.win();
        }
        //si no choca y esta en la fase 0, lo muevo, en caso contrario, si choca actualizo la direccion
        if(!logicaJuego.getRejilla().seChoca(comecocos,comecocos.getDireccion())){
            comecocos.siguienteFase();
            if(comecocos.getFase()==0){
                comecocos.mueve();
                comecocos.actualizarDireccion();
            }             
        } 
        else{
            comecocos.actualizarDireccion();
        }
        //Chequeamos si despues de movernos somos comidos
        if(muertePorFantasmas(comecocos)){
            logicaJuego.lose();
        }
    }
    
    /**
     * Metodo que nos indica si un Fantasma nos ha comido mientras estaba en estado no comestible.
     * @param comecocos Figura del Comecocos
     * @return False si no nos ha comido, True si si lo ha hecho
     */
    private boolean muertePorFantasmas(Figura comecocos){
        //Cargamos los fantasmas y comprobamos donde estan. Si el Comecocos esta en una casilla en la que tambien
        //hay un fantasma y no esta en estado comestible, nos mata
        Fantasma fantasmas[]=logicaJuego.getFantasmas();
        for(int i=0;i<fantasmas.length;i++){
            if(fantasmas[i]!=null && fantasmas[i].getX()==comecocos.getX() && fantasmas[i].getY()==comecocos.getY()){
                if(!fantasmas[i].isComestible()){
                    return true;
                }
                else{
                    //Si esta en estado comestible, nos comemos al fantasma y se nos suben los puntos correspondientes
                    logicaJuego.comerFantasma(i);
                    logicaJuego.setPuntos(logicaJuego.getPuntos()+LogicaJuego.PUNTOSFANTASMA);
                    
                }
            }
        }
        return false;
    }
    
    /**
     * Detiene momentaneamente la ejecución de la hebra, haciendo que la Figura actual
     * quede parada.
     */
    @Override
    synchronized public void suspender(){
        logicaJuego.getAdaptadorVistaLogica().repaint();
        suspendFlag=true;
    }
    
    /**
     * Reanuda el movimiento de la hebra. La Figura actual vuelve  a moverse.
     */
    @Override
    public synchronized void reanudar(){
        if(logicaJuego.getAdaptadorVistaLogica()!=null)
            logicaJuego.getAdaptadorVistaLogica().repaint();
        suspendFlag = false;
        notify();
    }
    
    /**
     * Termina la ejecución de la hebra.
     */
    @Override
    public void parar(){
        continuar=false;
    }
    
    /**
     * Nos dice si la hebra está o no parada.
     * @return true si la hebra de movimiento está parada, false en otro caso
     */
    @Override
    synchronized public boolean getParado(){
        return suspendFlag;
    }
    
    /**
     * La siguiente función actualiza el retardo que espera la hebra
     * para mover la Figura actual. El nivel más lento será
     * el 0 (retardo 80) y el más rápido el 10 (retardo 30)
     */
    private int actualizaRetardo(int nivel) {
        if (nivel>10) nivel=10;
        else if (nivel<0) nivel=0;
        return ( 50-(nivel*2) );
    }
}
 
