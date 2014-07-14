package data;

/**
 * Esta clase representa una rejilla con una determinada Anchura
 * y Altura, en la que cada celda puede estar VACIA, contener
 * un BLOQUE (muro exterior), un BLOQUE_CELDA (donde se resguardan los fantasmas),
 * un PPEQUE o un PGRANDE (permite comer fantasmas)
 * @author Jose y Raul
 */
public class Rejilla{
    public static final char VACIA        = ' ';
    public static final char BLOQUE       = 'B';
    public static final char BLOQUE_CELDA = '-';
    public static final char PPEQUE       = '.';
    public static final char PGRANDE      = 'o';
    
    private int anchura;
    private int altura;
    private int numeroBolas;
    
    private char[][] celdas;
    
    
    /**
     * Constructor que inicializa una rejilla mediante un String con la forma del mapa del Comecocos
     * @param nivel 
     */
    public Rejilla(String nivel[]){
        initRejilla(nivel);
    }
    
    /**
     * Metodo para iniciar la rejilla segun el String del mapa del comecocos nivel[]
     * @param nivel String con el mapa del Comecocos
     */
    public void initRejilla(String nivel[]){
        numeroBolas=0;
        anchura=nivel[0].length();
        altura=nivel.length;
        //transformo el laberinto de string a una matriz de char
        celdas=new char[altura][anchura];
        for(int i=0;i<altura;i++){
            for(int j=0;j<anchura;j++){
                //Colocamos las bolas
                celdas[i][j]=nivel[i].charAt(j);
                if(celdas[i][j]==PPEQUE||celdas[i][j]==PGRANDE){
                    numeroBolas++;
                }
            }
        }
    }
    
    /**
     * Metodo para guardar la rejilla actual si deseamos salvar la partida, guardando los valores de las celdas
     * @return Rejilla guardada
     */
    public String[] guardarRejilla(){
        String[] rejilla=new String[altura];
        for(int i=0;i<altura;i++){
            for(int j=0;j<anchura;j++){
                if(j==0){
                    rejilla[i]=String.valueOf(celdas[i][j]);
                }else{
                    rejilla[i]=rejilla[i]+String.valueOf(celdas[i][j]);
                }
                
            }
        }
        return rejilla;
    }
    
    /**
     * Devuelve la anchura de la rejilla.
     * @return la anchura de la rejilla
     */
    public int getAnchura(){
        return anchura;
    }
    
    /**
     * Devuelve la altura de la rejilla.
     * @return la altura de la rejilla
     */
    public int getAltura(){
        return altura;
    }
    
    /**
     * Metodo para obtener el numero de bolas de la Rejilla
     * @return Numero de bolas de la Rejilla
     */
    public int getNumeroBolas() {
        return numeroBolas;
    }
       
    /**
     * Metodo que se encarga de actualizar una celda si nos hemos comido una bola
     * @param x Coordenada x de la rejilla
     * @param y Coordenada y de la rejilla
     */
    public void comerCasilla(int x,int y){
        if(celdas[y][x]==PPEQUE||celdas[y][x]==PGRANDE){
            numeroBolas--;
        }
        celdas[y][x]= VACIA ;
    }
    
    /**
     * Obtiene el tipo de celda en las coordenadas x e y de esta Rejilla
     * @param x coordenada x (columna)
     * @param y coordenada y (fila)
     * @return el tipo de Celda en la coordenada x,y.
     */
    public char getTipoCelda(int x,int y){
        return celdas[y][x];
    }

    /**
     * Controla si una Figura se choca con un bloque.
     * @param fig Figura considerada
     * @param direccion Direccion actual de la Figura
     * @return True si se choca, False sino lo hace
     */
    public boolean seChoca(Figura fig, int direccion){
        if(fig.getX()==0||fig.getX()==anchura-1){
            if((fig.getDireccion()==Figura.DERECHA||fig.getDireccion()==Figura.IZQUIERDA)){
                return false;
            }
            else{
                return true;
            }
        }else if(direccion==Figura.ARRIBA){
            int celda=celdas[fig.getY()-1][fig.getX()];
            if(celda==BLOQUE||(celda==BLOQUE_CELDA && fig.getClass().getName().compareTo("Fantasma")==0)){
                return true;
            }
        }else if(direccion==Figura.ABAJO){
            int celda=celdas[fig.getY()+1][fig.getX()];
            if(celda==BLOQUE||celda==BLOQUE_CELDA){
                return true;
            }
        return false;
        }else if(direccion==Figura.DERECHA){
            int celda=celdas[fig.getY()][fig.getX()+1];
            if(celda==BLOQUE||celda==BLOQUE_CELDA){
                return true;
            }
        }else if(direccion==Figura.IZQUIERDA){
            int celda=celdas[fig.getY()][fig.getX()-1];
            if(celda==BLOQUE||celda==BLOQUE_CELDA){
                return true;
            }
        }
        return false;
    }
    
    
}

 
