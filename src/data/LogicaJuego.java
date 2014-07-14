package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

public class LogicaJuego {
	public static final int PUNTOSPEQUE         = 10;
    public static final int PUNTOSGRANDE        = 50;
    public static final int PUNTOSFANTASMA      = 200;
    public static final int PUNTOSSEGUNDO       = 5; // Puntos por cada segundo sobrante al ganar
    
    private static String partidaSalvada;
    private static Puntuacion[] top10;
    private boolean pausado;
    
    private Contador contador;
    private Rejilla rejilla;
    private Figura comecocos;
    private Mueve mueve;
    private int puntos;
    private Fantasma fantasmas[];
    private int nivel;
    private int vidas;
    private int contadorMin;
    private int contadorSeg;
    private int tiempoFantasmasComestibles;//es una variable que sirve para cuantificar cuanto tiempo queda 
    //para que dejen de ser comestibles los Fantasmas en segundos
    private Lock cerrojoMovimiento; //Variable usada para que solo se puedan mover las figuras de 1 en 1.
    
    private AdaptadorVistaLogica adaptador;
    
    private static final String NIVEL0[]={
        "BBBBBBBBBBBBBBBBBBBBBBBBBBBB",
        "B............BB............B",
        "B.BBBBB.BBBB.BB.BBBBB.BBBB.B",
        "BoBBBBB.BBBB.BB.BBBBB.BBBBoB",
        "B.BBBBB.BBBB.BB.BBBBB.BBBB.B",
        "B..........................B",
        "B.BBBB.BB.BBBBBBBB.BB.BBBB.B",
        "B.BBBB.BB.BBBBBBBB.BB.BBBB.B",
        "B......BB....BB....BB......B",
        "BBBBBB.BBBBB BB BBBBB.BBBBBB",
        "     B.BBBBB BB BBBBB.B     ",
        "     B.BB          BB.B     ",
        "     B.BB BB----BB BB.B     ",
        "BBBBBB.BB B      B BB.BBBBBB",
        "      .   B      B   .      ",
        "BBBBBB.BB B      B BB.BBBBBB",
        "     B.BB BBBBBBBB BB.B     ",
        "     B.BB          BB.B     ",
        "     B.BB BBBBBBBB BB.B     ",
        "BBBBBB.BB BBBBBBBB BB.BBBBBB",
        "B............BB............B",
        "B.BBBB.BBBBB.BB.BBBBB.BBBB.B",
        "B.BBBB.BBBBB.BB.BBBBB.BBBB.B",
        "Bo..BB................BB..oB",
        "BBB.BB.BB.BBBBBBBB.BB.BB.BBB",
        "BBB.BB.BB.BBBBBBBB.BB.BB.BBB",
        "B......BB....BB....BB......B",
        "B.BBBBBBBBBB.BB.BBBBBBBBBB.B",
        "B.BBBBBBBBBB.BB.BBBBBBBBBB.B",
        "B..........................B",
        "BBBBBBBBBBBBBBBBBBBBBBBBBBBB",
        };
    /**
     * Constructor Logica del Juego
     * @param indica el adaptador que sirve de puente entre la logica y la interfaz del juego
     */
    public LogicaJuego(AdaptadorVistaLogica adaptador){
    	this.adaptador=adaptador;
    	top10=new Puntuacion[10];
        leerArchivoPuntuaciones();
        fantasmas=new Fantasma[4];
        contador=new Contador(this);
        pausado=false;
        cerrojoMovimiento=new ReentrantLock();
    }

    
    /**
     * Metodo que debe ser llamado antes de mover una figura de tal forma
     * que solo una se pueda mover a la ver conservando la integridad del
     * tablero
     */
    public void pedirPermisoMover(){
    	cerrojoMovimiento.lock();
    }
    
    /**
     * Devuelve el lock para que se puedan mover otras figuras
     */
    public void devolverPermisoMover(){
    	cerrojoMovimiento.unlock();
    }
    
    /**
     * Establece un adaptador entre la parte de la interfaz y la parte de la vista
     */
    public void setAdaptadorVistaLogica(AdaptadorVistaLogica adaptador_v_l){
    	adaptador=adaptador_v_l;
    }
    
    public AdaptadorVistaLogica getAdaptadorVistaLogica(){
    	return adaptador;
    }
    
    /**
    * Obtiene una referencia a la Rejilla del juego
    * @return una referencia a la Rejilla del juego
    */
    public Rejilla getRejilla(){
        return rejilla;
    }
    /**
    * Obtiene una referencia a la figura Comecocos
    * @return una referencia al Comecocos actual
    */
    public Figura getComecocos(){
        return comecocos;
    }
    
    /**
     * Obtiene una referencia a las figuras Fantasma
     * @return una referencia a los Fantasmas
     */
    public Fantasma[] getFantasmas() {
        return fantasmas;
    }
    
    /**
     * Obtiene una referencia a los puntos
     * @return referencia a los puntos
     */
    public int getPuntos() {
        return puntos;
    }
    
    /**
     * Metodo para establecer una puntuacion
     * @param puntos Puntos conseguidos a guardar
     */
    public void setPuntos(int puntos2) {
        puntos = puntos2;
        adaptador.setPuntuacion(puntos);
        //adaptador.setPuntuacion(puntos);
        /*if(puente!=null){
        	Message message = new Message();
            message.what = ActividadJuego.ACTUALIZAR_PUNTUACION;
            message.obj=puntos;
        	//puente.sendMessage(message);
        }*/
    }
    
    /**
     * Metodo para establecer el numero de vidas del Comecocos
     * @param vidas Vidas del comecocos
     */
    private void setVidas(int vidasIn) {
        vidas = vidasIn;
        adaptador.setVidas(vidas);
        /*if(puente!=null){
        	Message message = new Message();
            message.what = ActividadJuego.ACTUALIZAR_VIDA;
            message.obj=vidas;
        	puente.sendMessage(message);
        }   */    																
    }

    /**
     * Metodo para establecer el nivel del juego
     * @param nivel Nivel del juego
     */
    private void setNivel(int nivelIn) {
        nivel = nivelIn;
        adaptador.setNivel(nivel);
        /*if(puente!=null){
        	Message message = new Message();
            message.what = ActividadJuego.ACTUALIZAR_NIVEL;
            message.obj=nivel;
        	puente.sendMessage(message);
        }*/
    }
    
    
    
    /**
     * Metodo que se encarga de decrementar el valor del contador de tiempo de la partida cada vez que pasa un segundo y 
     * de controlar tambien el tiempo en el que los fantasmas estan comestibles
     */
    public void pasarUnSegundo(){
        if(contadorSeg==0){
            if(contadorMin==0){
                //lose();
            }
            else{
                contadorMin--;
                contadorSeg=59;
                for(int i=0;i<fantasmas.length;i++){
                	fantasmas[i].aumentarVelocidad();
                }
            }
        }
        else if(contadorSeg==30){
        	for(int i=0;i<fantasmas.length;i++){
            	fantasmas[i].aumentarVelocidad();
            }
        	contadorSeg--;
        }
        else{
            contadorSeg--;
        }
        adaptador.setContador(contadorMin,contadorSeg);
        tiempoFantasmasComestibles--;
        if(tiempoFantasmasComestibles==0){
            hacerFantasmasNoComestibles();
        } 
    }

    
    /**
     * Metodo que se encarga de inicializar un nuevo Fantasma si nos comemos uno
     * @param indiceFantasma Indice correspondiente del Fantasma comido del array
     */
    public void comerFantasma(int indiceFantasma){
        fantasmas[indiceFantasma].parar();
        fantasmas[indiceFantasma]=new Fantasma(13,15,rejilla,nivel,this);
        fantasmas[indiceFantasma].reanudar();
    }
    
    /**
     * Metodo para hacer a los fantasmas comestibles durante 10 segundos
     */
    public void hacerFantasmasComestibles(){
        for(int i=0;i<fantasmas.length;i++){
            if(fantasmas[i]!=null){
                fantasmas[i].setComestible(true);
            }
        }
        tiempoFantasmasComestibles=10;      
    }
    
    /**
     * Metodo para volver a los fantasmas a su estado normal (no comestibles)
     */
    public void hacerFantasmasNoComestibles(){
        for(int i=0;i<fantasmas.length;i++){
            if(fantasmas[i]!=null){
                fantasmas[i].setComestible(false);
            }
        }          
    }
   
    /**
     * Metodo que se encarga de mostrar un mensaje de que hemos ganado la partida y varias ventanas para, por ejemplo, 
     * meter nuestra nombre en el Top10 si la puntuacion se encuentra entre las mejores
     */
    public void win(){
        if(nivel<10){
            setNivel(nivel+1);
            int tempPuntos=puntos+(contadorSeg*PUNTOSSEGUNDO)+(contadorMin*60*PUNTOSSEGUNDO); //Calculamos los puntos que hemos obtenido
            //por que nos sobre tiempo
            int tempVidas=vidas;
            inicializaJuego(nivel);
            setPuntos(tempPuntos);
            setVidas(tempVidas);
            												//JOptionPane.showMessageDialog(this,"Siguiente nivel");
            pausar();
        }
        else{
            																	//JOptionPane.showMessageDialog(this, "Has ganado la puntuacion es de: "+puntos);
            //aniado la puntuacion actual al top10 si procede
            if(top10[9].getPuntuacion()<puntos){
                String nombre="nombre jugador";													//JOptionPane.showInputDialog("Introduzca su nombre para añadirlo al top 10");
                top10[9]=new Puntuacion(nombre,puntos);
                //ordenamos el array de puntuaciones
                Arrays.sort(top10);
            }
            parar();
        } 
    }
    
    /**
     * Metodo que se encarga de mostrarnos mensajes si perdemos una vida o perdemos directamente la partida al perder las tres.
     * En el caso de que la puntuacion este en el Top10, se pide al usuario un nombre.
     */
    public void lose(){
        if(vidas>1){
            int tempPuntos=puntos;
            int tempVidas=vidas-1;
            														//JOptionPane.showMessageDialog(this,"Perdiste una vida");
            inicializaJuego(nivel);
            setPuntos(tempPuntos);
            setVidas(tempVidas);
        }
        else{        	
        	
            adaptador.pedirNombreJugador();									//JOptionPane.showMessageDialog(this, "Has perdido la puntuacion es de: "+puntos);
            //añado la puntuacion actual al top10 si procede
            if(top10[9].getPuntuacion()<puntos){
                String nombre="Nombre jugador";											//JOptionPane.showInputDialog("Introduzca su nombre para añadirlo al top 10");
                top10[9]=new Puntuacion(nombre,puntos);
                Arrays.sort(top10);
            }
            parar();
        }
    }
    
    /**
     * Metodo para pausar las hebras de los fantasmas y tambien el contador. En el caso de que esten parados, se reanudan
     */
    public void pausar(){
        if(isPausado()){
                mueve.reanudar();
                for(int i=0;i<fantasmas.length;i++){
                    if(fantasmas[i]!=null){
                        fantasmas[i].reanudar();
                    }
                }
                contador.reanudar();
                pausado=false;
            }
            else{
                mueve.suspender();
                for(int i=0;i<fantasmas.length;i++){
                    if(fantasmas[i]!=null){
                        fantasmas[i].suspender();
                    }
                }
                contador.suspender();
                pausado=true;
            }
        
    }
    
    /**
     * Para todas las hebras de movimiento, fantasmas y contador.
     */
    private void parar(){
        mueve.parar();
        for(int i=0;i<fantasmas.length;i++){
            if(fantasmas[i]!=null){
                fantasmas[i].parar();
            }
        }
        contador.suspender();
    }
    
    /**
     * Metodo para conocer si la hebra y el juego estan pausados
     * @return True si esta pausada la hebra 
     */
    public boolean isPausado() {
        return pausado;
    }
    
    /**
     * Metodo para inicializar el juego del comecocos y sus variables segun el nivel de dificultad
     * @param nivel Nivel de dificutad
     */
    public void inicializaJuego(int nivel){
        setVidas(3);
        setNivel(nivel);
        if(rejilla==null){
            rejilla=new Rejilla(NIVEL0);
        }
        else{
            rejilla.initRejilla(NIVEL0);
        }
        if(mueve!=null){
            mueve.parar();
        }
        
        //Segun el nivel, tendremos mas o menos tiempo para comernos todas las bolas
        switch(nivel){
            case 0: 
                contadorMin=4;
                contadorSeg=0;
                break;
            case 1:
                contadorMin=3;
                contadorSeg=50;
                break;
            case 2:
                contadorMin=3;
                contadorSeg=40;
                break;
            case 3:
                contadorMin=3;
                contadorSeg=30;
                break;
            case 4:
                contadorMin=3;
                contadorSeg=20;
                break;
            case 5:
                contadorMin=3;
                contadorSeg=10;
                break;
            case 6:
                contadorMin=3;
                contadorSeg=0;
                break;
            case 7:
                contadorMin=2;
                contadorSeg=50;
                break;
            case 8:
                contadorMin=2;
                contadorSeg=30;
                break;
            case 9:
                contadorMin=2;
                contadorSeg=20;
                break;
            case 10:
                contadorMin=2;
                contadorSeg=0;
                break;
        }
        adaptador.setContador(contadorMin,contadorSeg);
        comecocos=new Figura(13,17,rejilla);
        setPuntos(0);
        mueve=new MueveComecocos(nivel,this);
        mueve.reanudar();
        fantasmas[0]=new Fantasma(13,15,rejilla,nivel,this);
        fantasmas[1]=new Fantasma(12,15,rejilla,nivel,this);
        fantasmas[2]=new Fantasma(14,15,rejilla,nivel,this);
        fantasmas[3]=new Fantasma(15,15,rejilla,nivel,this);
        for(int i=0;i<fantasmas.length;i++){
            fantasmas[i].reanudar();
        }
        if(contador.getParado()){
            contador.reanudar();
        }
        
    }
    
    /**
     * Metodo que se encarga de guardar la partida actual, tanto los parametros como la vida y el tiempo restante
     * como la posicion de los fantasmas y la rejilla.
     */
    private void salvarPartida(){
        String partidaSalvada;
        partidaSalvada=nivel+" "+puntos+" "+vidas+" "+contadorMin+" "+contadorSeg+" "+tiempoFantasmasComestibles+" ";
        partidaSalvada+=comecocos.guardarPartida()+" ";
        for(int i=0;i<fantasmas.length;i++){
            partidaSalvada+=fantasmas[i].guardarPartida()+" ";
        }
        String[] rejillaGuardada=rejilla.guardarRejilla();
        partidaSalvada+="#";
        for(int i=0;i<rejillaGuardada.length;i++){
            partidaSalvada+=rejillaGuardada[i]+"#";
        }
        this.partidaSalvada=partidaSalvada;
        																	//cargarPartida.setEnabled(true);//activa la opcion del menu de cargar partida
        
    }
    
    /**
     * Metodo para cargar una partida guardada anteriormente
     * @param partidaSalvada String de la partida guardada
     */
    private void cargarPartida(String partidaSalvada){
        if(partidaSalvada!=null){
            parar();
            String[] save=partidaSalvada.split(" ");
            setPuntos(Integer.parseInt(save[1]));
            setNivel(Integer.parseInt(save[0]));
            setVidas(Integer.parseInt(save[2]));
            contadorMin=Integer.parseInt(save[3]);
            contadorSeg=Integer.parseInt(save[4]);
            tiempoFantasmasComestibles=Integer.parseInt(save[5]);
            //la primera linea con el separador # no sirve pues son todos los otros datos
            //por eso rejillaCargada es lo mismo que temp pero sin la primera fila
            String[] temp=partidaSalvada.split("#");
            String[] rejillaCargada=new String[temp.length-1];

            for(int i=0;i<rejillaCargada.length;i++){
                rejillaCargada[i]=temp[i+1];
            }
            rejilla=new Rejilla(rejillaCargada);
            comecocos=Figura.cargarPartida(save[6], rejilla);  
            for(int i=0;i<fantasmas.length;i++){
                fantasmas[i]=Fantasma.cargarPartida(save[i+7], rejilla,nivel,this);
            }
            mueve=new MueveComecocos(nivel,this);
			//establezco los valores del contador en la vista
            adaptador.setContador(contadorMin, contadorSeg);
            adaptador.repaint();
        }
        
    }
    
    /**
     * Metodo para leer el archivo de puntuaciones que existe para el videojuego del Comecocos
     */
    private void leerArchivoPuntuaciones(){
        File file1 = new File("puntuaciones");
        if(file1.exists()){
            try {
                @SuppressWarnings("resource")
				Scanner input = new Scanner(file1);
                for(int i=0;i<10;i++){
                    if(input.hasNext()){
                        String[] split = input.nextLine().split("   ");
                        top10[i]=new Puntuacion(split[0],Integer.parseInt(split[1]));
                    }
                    else{
                        top10[i]=new Puntuacion("--empty--",0);
                    }
                }      
            } catch (FileNotFoundException ex) {
                
            }
        }
        else{
            for(int i=0;i<10;i++){
                top10[i]=new Puntuacion("--empty--",0);
            }
        }
    }
    
    /**
     * Graba la nueva puntuacion en el Top10 con el nombre correspondiente de jugador.
     */
    private void grabarArchivoPuntuaciones(){
        PrintWriter output = null;
        try {
            File file1 = new File("puntuaciones");
            output = new PrintWriter(file1);
            for(int i=0;i<10;i++){
                output.println(top10[i].getNombre()+"   "+top10[i].getPuntuacion());
            }
        } catch (FileNotFoundException ex) {
            
        } finally {
            output.close();
        }
    }
}
