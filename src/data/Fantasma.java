package data;

/**
 * Esta clase contiene lo neecsaria para los fantasmas del Comecocos e implementa una hebra. Cuenta con cuatro datos miembro:
 * dos boolean: continuar y suspendFlag para controlar las hebras; un entero: delay, para aplicar la velocidad
 * de movimiento segun el nivel de dificultad; un entero: comestible, una variable que indica si los fantasmas
 * estan en estado comestible
 * @author Jose y Raul
 */
public class Fantasma extends Figura implements Mueve {
    private boolean comestible;
    private int delay;
    private boolean continuar=true;
    private boolean suspendFlag=true;
    private LogicaJuego logicaJuego;
    
    /**
     * Constructor de la clase que inicializa los fantasmas. Como direccion inicial se
     * les indica hacia arriba. Posteriormente se lanza la hebra.
     * @param x Posicion x en la rejilla
     * @param y Posicion y en la rejilla
     * @param rejilla Rejilla considerada
     * @param nivel Nivel deseado
     * @param logicaJuego es la logica del juego al cual pertenece como fantasma
     */
    public Fantasma(int x,int y,Rejilla rejilla,int nivel,LogicaJuego logicaJuego){
        super(x,y,rejilla);
        this.logicaJuego=logicaJuego;
        comestible=false;
        delay=actualizaRetardo(nivel);
        setDireccion(ARRIBA);
        actualizarDireccion();
        Thread t=new Thread(this);
        t.start();
    }
    
    /**
     * Constructor de la clase que inicializa un fantasma pero teniendo en cuenta una partida guardada.
     * De esta forma, necesitaremos conocer si estaban en estado comestible.
     * @param x Posicion x de la rejilla
     * @param y Posicion y de la rejilla 
     * @param rejilla Rejilla considerada
     * @param direccion Direccion del fastasma
     * @param nivel Nivel deseado
     * @param comestible Indica si el fanstasma se encuentra en estado comestible
     */
    private Fantasma(int x,int y,Rejilla rejilla,int direccion,int nivel,boolean comestible,LogicaJuego logicaJuego){
        super(x,y,rejilla,direccion);
        this.logicaJuego=logicaJuego;
        this.comestible=comestible;
        delay=actualizaRetardo(nivel);
        Thread t=new Thread(this);
        t.start();
    }
    
    public void aumentarVelocidad(){
    	delay=delay-1;
    }
    
    /**
     * Metodo que comprueba si un fantasma es comestible
     * @return true si es comestible, false sino lo es
     */
    public boolean isComestible() {
        return comestible;
    }
    
    /**
     * Metodo para establecer el estado comestible de un fantasma
     * @param comestible true si es comestible, false si no lo es
     */
    public void setComestible(boolean comestible) {
        this.comestible = comestible;
    }
    
    /**
     * Metodo que guarda los fantasmas de la partida actual. 
     * @return String con los parametros de la partida guardada.
     */
    @Override
    public String guardarPartida(){
        String partidaGuardada=x+"%"+y+"%"+direccion+"%"+comestible;
        return partidaGuardada;
    }
    
    /**
     * Metodo para cargar los fantasmas de una partida guardada.
     * @param partidaGuardada String de la partida guardada a cargar
     * @param rejilla Rejilla a considerar
     * @param nivel Nivel de la partida
     * @return Fantasma cargado con todos los parametros que se grabaron al salvar la partida
     */
    public static Fantasma cargarPartida(String partidaGuardada,Rejilla rejilla,int nivel,LogicaJuego logicaJuego){
        String load[]=partidaGuardada.split("%");
        Fantasma cargada=new Fantasma(Integer.parseInt(load[0]),Integer.parseInt(load[1]),rejilla,Integer.parseInt(load[2]),nivel,Boolean.parseBoolean(load[3]),logicaJuego);
        return cargada;
    }
    
    /**
     * Metodo que controla la hebra de los fantasmas y su movimiento constante
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
                Thread.sleep(delay);  // el dealy indica cada cuanto se va a mover el fantasma
                logicaJuego.pedirPermisoMover();
                actualizarFigura(this);
                logicaJuego.devolverPermisoMover();
            }// end while(continuar)
        } catch(InterruptedException e){
            System.out.println("Hilo Mueve interrumpido");
        }
    }

    /**
     * Metodo que se encarga de actualizar el movimiento de los fantasmas
     * @param fantasma Fantasma a actualizar
     */
    private void actualizarFigura(Figura fantasma) {
        //aqui va el codigo de como se mueve el fantasma
        //Si esta parado, hacemos uso del metodo heuristica
        if(getDireccion()==PARADO){
            heuristica();
        }
        //Si no se choca con ninguna rejilla, calculamos la fase, y si esta es 0, hacemos uso del metodo
        //mueve de Figura, que cambia las coordenadas, sino, de heuristica
        if(!rejilla.seChoca(this,getDireccion())){
            siguienteFase();
            if(getFase()==0){
                mueve();  
            }             
        } 
        else{
            heuristica();
        }
    }
       
    /**
     * Metodo que se encarga de asignar una direccion a los fantasmas. 
     */
    private void heuristica(){
        do{
            setDireccion((int)(Math.random()*4)%4);//Para que siempre sea una direccion valida
            actualizarDireccion();
        }
        while(rejilla.seChoca(this, getDireccion())||getDireccion()==PARADO);//si esa direccion se choca con algo 
        //o es parado, piensa en otra direccion
    }
    
    /**
     * Detiene momentaneamente la ejecución de la hebra, haciendo que el fastasma actual
     * quede parado.
     */
    @Override
    synchronized public void suspender(){
        suspendFlag=true;
    }
    
    /**
     * Reanuda el movimiento de la hebra. El fantasma actual vuelve  a moverse.
     */
    @Override
    public synchronized void reanudar(){
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
     * Metodo para actualizar la velocidad de los fantasmas segun el nivel del juego.
     * @param nivel Nivel de dificultad
     * @return Retardo que hay que incorporar al movimiento
     */
    private int actualizaRetardo(int nivel) {
        if (nivel>10) nivel=10;
        else if (nivel<0) nivel=0;
        return ( 50-(nivel*2) );
    }
    
}
