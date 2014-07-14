package data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import com.example.comecocos.R;

/**
 * Esta clase sirve como puente entre la vista o interfaz android y la logica de nuestro juego
 * de tal forma que sirve para desacoplar dicha logica de android y hacerla portable a otros dispositivos
 * que trabajen en java.
 */
public class AdaptadorVistaLogica {
    private Activity actividadInterfaz;
    private LogicaJuego logicaJuego;
    private String m_Text = "";
    
    public AdaptadorVistaLogica(Activity act) {
		actividadInterfaz=act;
    	logicaJuego=new LogicaJuego(this);
	}

    /**
     * 
     */
    public void iniciarJuego(int nivel){
    	logicaJuego.inicializaJuego(nivel);
    }
	/**
     * Establece el parametro actividad
     * @param actividad activity para pintar en la vista
     */
    public void setActividadInterfaz(Activity actividad){
    	actividadInterfaz=actividad;
    }
    
    public String pedirNombreJugador(){
    	if(actividadInterfaz!=null){
	    	actividadInterfaz.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(actividadInterfaz);
			    	builder.setTitle("Title");

			    	// Set up the input
			    	final EditText input = new EditText(actividadInterfaz);
			    	// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
			    	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			    	builder.setView(input);

			    	// Set up the buttons
			    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    	    @Override
			    	    public void onClick(DialogInterface dialog, int which) {
			    	        m_Text = input.getText().toString();
			    	    }
			    	});
			    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    	    @Override
			    	    public void onClick(DialogInterface dialog, int which) {
			    	        dialog.cancel();
			    	    }
			    	});

			    	builder.show();
				}
	    		
	    	});
    	}
    	return m_Text;
   	
    }
    
    /**
     * Metodo que limpia el valor de la variable actividadInterfaz.
     * Es llamado al pausar la actividad que inicio el comecocos,
     * de esta forma evitamos que si la actividad muere el colector 
     * de basura no pueda borrarla. 
     */
    public void limpiarActividadInterfaz(){
    	actividadInterfaz=null;
    }
    
    /**
     * Metodo que nos indica si el juego esta o no en pausa
     * @return Indica si el juego esta o no en pausa
     */
    public boolean isPausado(){
    	return logicaJuego.isPausado();
    }
    
    /**
     * Metodo que pausa el juego
     */
    public void pausar(){
    	logicaJuego.pausar();
    }
    

    
    /**
     * Metodo llamado cada vez que la vista quiere repintar
     */
    public void repaint(){
    	//no tiene nada pues existe en android una hebra encargada de repintar todo el rato el canvas
    }
    
    /**
     * Metodo empleado para asignar un nivel dado en la interfaz
     * @param nivel nivel a mostrar
     */
    public void setNivel(final int nivel){
    	if(actividadInterfaz!=null){
	    	actividadInterfaz.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					TextView tv_nivel=(TextView)actividadInterfaz.findViewById(R.id.tv_nivel);
		    		tv_nivel.setText(String.valueOf(nivel));
				}
	    		
	    	});
    	}
    }
    
    /**
     * Metodo empleado para asignar una puntuacion en la interfaz
     * @param puntuacion es la puntuacion a mostrar en la interfaz
     */
    public void setPuntuacion(final int puntuacion){
    	if(actividadInterfaz!=null){
	    	actividadInterfaz.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					TextView tv_puntuacion=(TextView)actividadInterfaz.findViewById(R.id.tv_puntuacion);
					String valor=String.valueOf(puntuacion);
					String cadena="";
					while(cadena.length()+valor.length()<6){
						cadena+="0";
					}
					cadena+=valor;
					tv_puntuacion.setText(cadena);
				}
	    	});
    	}
    }
    
    /**
     * Metodo empleado para asignar un numero de vidas en el interfaz
     * @param vidas numero de vidas a mostrar en el interfaz
     */
    public void setVidas(final int vidas){
    	if(actividadInterfaz!=null){
	    	actividadInterfaz.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					TextView tv_vidas=(TextView)actividadInterfaz.findViewById(R.id.tv_vidas);
					tv_vidas.setText(String.valueOf(vidas));
				}
	    	});
    	}
    }
    
    /**
     * Pinta en la interfaz el contador con los minutos y segundos dados.
     * @param contadorMin nos indica el numero entero de minutos a mostrar.
     * @param contadorSeg nos indica el numero entero de segundos a mostrar.
     */
    public void setContador(final int contadorMin,final int contadorSeg){
    	if(actividadInterfaz!=null){
	    	actividadInterfaz.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					TextView tv_tiempo=(TextView)actividadInterfaz.findViewById(R.id.tv_tiempo);
		    		if(contadorMin<10){
		    			if(contadorSeg<10){
		    				tv_tiempo.setText("0"+contadorMin+":"+"0"+contadorSeg);;
			    		}
			    		else{
			    			tv_tiempo.setText("0"+contadorMin+":"+contadorSeg);;
			    		}
		    		}
		    		else{
		    			if(contadorSeg<10){
			    			tv_tiempo.setText("contadorMin:"+"0"+contadorSeg);;
			    		}
			    		else{
			    			tv_tiempo.setText("contadorMin:"+contadorSeg);;
			    		}
		    		}
				}
	    		
	    	});
    	}
        /*Message message = new Message();
        message.what = ActividadJuego.ACTUALIZAR_TIEMPO;
        message.obj=cadena;
        puente.sendMessage(message);*/
    }
    
    /**
     * Devuelve la altura del tablero del juego
     * @return altura tablero del juego
     */
    public int getAlturaJuego(){
    	return logicaJuego.getRejilla().getAltura();
    }
    
    /**
     * Devuelve la anchura del tablero del juego
     * @return anchura tablero del juego
     */
    public int getAnchuraJuego(){
    	return logicaJuego.getRejilla().getAnchura();
    }
    
    /**
     * Devuelve la rejilla del juego
     * @return rejilla del juego
     */
    public Rejilla getRejilla(){
    	return logicaJuego.getRejilla();
    }
    
    /**
     * Devuelve un array con los fantasmas
     * @return Array de fantasmas
     */
    public Fantasma[] getFantasmas(){
    	return logicaJuego.getFantasmas();
    }
    
    /**
     * Devuelve el comecocos
     * @return Comecocos
     */
    public Figura getComecocos(){
    	return logicaJuego.getComecocos();
    }
}	
