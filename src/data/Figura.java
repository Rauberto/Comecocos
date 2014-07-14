package data;

/**
 * Esta clase contiene todo lo necesario para el correcto funcionamiento de las figuras y su movimiento, ademas de algunos
 * metodos adicionales referentes a la partida. Cuenta con cinco datos miembro, uno para cada direccion de movimiento,
 * y otros cinco que son: x: posicion x de la figura; y:posicion y de la figura; direccionSiguiente: siguiente direccion
 * hacia donde debe moverse la figura; direccion: direccion actual de la figura; fase: una variable para controlar si
 * la figura ha pasado de una casilla a otra o esta en proceso de hacerlo.
 * @author Jose y Raul
 */
public class Figura {
    public static final int IZQUIERDA         = 0;
    public static final int DERECHA           = 1;
    public static final int ABAJO             = 2;
    public static final int ARRIBA            = 3;
    public static final int PARADO            =4;
    
    protected Rejilla rejilla;
    protected int x;
    protected int y;
    protected int direccionSiguiente;
    protected int direccion;
    private int fase;//las figuras dan 4 "pasos" entre una casilla y otra.
    //cuando vale 0 esta en una casilla, si vale 1 2 o 3 esta desplazandose entre casillas

    /**
     * Constructor de la clase que inicializa una figura parada, sin movimiento.
     * @param x Posicion X en la rejilla 
     * @param y Posicion Y en la rejilla
     * @param rejilla Rejilla considerada
     */
    public Figura(int x,int y,Rejilla rejilla) {
        this.x=x;
        this.y=y;
        this.rejilla=rejilla;
        direccion=PARADO;
        direccionSiguiente=PARADO;
        fase=0;
    }
    
    /**
     * Constructor de la clase especifico para cargar una partida pues incializa las direcciones de las figuras
     * dependiendo de las direcciones en el momento de guardar la partida
     * @param x Posicion X en la rejilla 
     * @param y Posicion Y en la rejilla
     * @param rejilla Rejilla considerada
     * @param direccion Direccion de la Figura
     */
    protected Figura(int x,int y,Rejilla rejilla,int direccion){
        this.x=x;
        this.y=y;
        this.rejilla=rejilla;
        this.direccion=direccion;
        this.direccionSiguiente=direccion;
        fase=0;
    }
    
    /**
     * Metodo para guardar la partida. Basicamente crea un string con los parametros fundamentales
     * @return String con los parametros de la partida guardada
     */
    public String guardarPartida(){
        String partidaGuardada=x+"%"+y+"%"+direccion;
        return partidaGuardada;
    }
    
    /**
     * Metodo para cargar una partida guardada a traves de una rejilla y de un String de partidaGuardada.
     * @param partidaGuardada String con los parametros de una partida guardada
     * @param rejilla Rejilla considerada
     * @return Figura con los parametros de partidaGuardada
     */
    public static Figura cargarPartida(String partidaGuardada,Rejilla rejilla){
        String[] comecocos=partidaGuardada.split("%");
        return new Figura(Integer.parseInt(comecocos[0]),Integer.parseInt(comecocos[1]),rejilla,Integer.parseInt(comecocos[2]));
    }
    
    /**
     * Metodo para obtener la direccion de una Figura
     * @return Direccion de la Figura
     */
    public int getDireccion() {
        return direccion;
    }
    
    /**
     * Metodo para establecer la direccion de una Figura
     * @param direccion Direccion de la Figura
     */
    public void setDireccion(int direccion) {
        this.direccionSiguiente = direccion;
    }

    /**
     * Metodo que devuelve la fase de la figura
     * @return Fase de la Figura
     */
    public int getFase() {
        return fase;
    }
    
    /**
     * Metodo para controlar la fase de la Figura. Si llega a 3, vuelve a 0, es decir, que ha pasado a una celda.
     */
    public void siguienteFase(){
        fase++;
        if(fase>3){
            fase=0;
        }
    }
    
    /**
     * Metodo para actualizar la direccion de la Figura. Si no se choca con la rejilla, la direccion seguira siendo la misma
     */
    public void actualizarDireccion(){
        if(!rejilla.seChoca(this, direccionSiguiente)){
            direccion=direccionSiguiente;
        }
        
    }
    
    /**
     * Obtiene la posición x respecto al origen de coordenadas de la Rejilla de la Figura actual
     * @return la posición x respecto al origen de coordenadas de la Rejilla de la Figura actual
     */
    public int getX(){
        return x;
    }
 
    /**
     * Obtiene la posición y respecto al origen de coordenadas de la Rejilla de la Figura actual
     * @return la posición y respecto al origen de coordenadas de la Rejilla de la Figura actual
     */
    public int getY(){
        return y;
    }
    
    /**
     * Mueve la Figura actual una casilla en la dirección indicado por direccion (ABAJO,IZQUIERDA, DERECHA o ARRIBA)
     */
    public void mueve(){
            if(direccion==ABAJO){
                y++;
            }else if(direccion==IZQUIERDA){
                if(x==0){
                    x=rejilla.getAnchura()-1;
                }else{
                    x--;
                }   
            }else if(direccion==DERECHA){
                if(x==rejilla.getAnchura()-1){
                    x=0;
                }else{
                    x++;
                } 
            }else if(direccion==ARRIBA){
                y--;
            }
    }
}
 
