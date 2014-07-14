
package data;

/**
 * Clase que se encarga de las puntuaciones del juego, asi como ordenar la table
 * de las mejores puntuaciones. Implementa la interfaz Comparable para comparar las
 * puntuaciones.
 * @author Jose y Raul
 */
public class Puntuacion implements Comparable {
    String nombre;
    int puntuacion;
    
    /**
     * Constructor de la clase que inicializa una puntacion con un nombre asociado
     * @param nombre Nombre del jugador
     * @param puntuacion Puntuacion 
     */
    public Puntuacion(String nombre,int puntuacion){
        this.nombre=nombre;
        this.puntuacion=puntuacion;
    }
    
    /**
     * Metodo para obtener el nombre del jugador
     * @return Nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Metodo para obtener la puntuacion de un jugador
     * @return Puntuacion del jugador
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    /**
     * Metodo que permite comparar las puntuaciones
     * @param t Objecto Puntuacion
     * @return 1 si la puntuacion actual nueva es mayor que la puntuacion comparada, -1 si no lo es.
     */    
    @Override
    public int compareTo(Object t) {
        Puntuacion temporal=(Puntuacion) t;
        if(temporal.puntuacion>puntuacion){
            return 1;
        }
        else{
            if(temporal.puntuacion<puntuacion){
                return -1;
            }
            else {
                return 0;
            }

        }
    }
}
