package com.example.comecocos;



import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import data.AdaptadorVistaLogica;
import data.Figura;

public class ActividadJuego extends Activity{
	
	TextView tv_nivel,tv_vidas,tv_tiempo,tv_puntuacion;
	AdaptadorVistaLogica adaptador;
	
	/*Handler puente;
	public static final int ACTUALIZAR_NIVEL=0x01;
	public static final int ACTUALIZAR_VIDA=0x02;
	public static final int ACTUALIZAR_TIEMPO=0x03;
	public static final int ACTUALIZAR_PUNTUACION=0x04;*/
	
	private int x_push;
	private int y_push;
	private boolean pulsado=false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.actividad_juego);
		
		adaptador=new AdaptadorVistaLogica(this);
		adaptador.iniciarJuego(0);
		//setContentView(new VistaJuego(this,adaptador));
		tv_nivel=(TextView)this.findViewById(R.id.tv_nivel);
		tv_vidas=(TextView)this.findViewById(R.id.tv_vidas);
		tv_puntuacion=(TextView)this.findViewById(R.id.tv_puntuacion);
		tv_tiempo=(TextView)this.findViewById(R.id.tv_tiempo);

		anniadirVistaJuego();
		/*puente=new Handler(){
			@Override
			  public void handleMessage(Message msg) {
				switch(msg.what){
				case ACTUALIZAR_NIVEL:
					tv_nivel.setText((int) msg.obj);
					break;
				case ACTUALIZAR_VIDA:
					tv_vidas.setText((int)msg.obj);	
					break;
				case ACTUALIZAR_PUNTUACION:
					tv_puntuacion.setText((int)msg.obj);
					break;
				case ACTUALIZAR_TIEMPO:
					tv_tiempo.setText((CharSequence) msg.obj);
					break;
				}
			  }
		};*/
		

	}
	
	/*
	 * Añade la vista que pinta el juego al layout
	 */
	@SuppressWarnings("deprecation")
	private void anniadirVistaJuego(){
		LinearLayout l=(LinearLayout)this.findViewById(R.id.ll_juego);
		Display  display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();

        int celda;
        if(height<width){
        	celda=height/adaptador.getAlturaJuego();
        }
        else{
        	celda=width/adaptador.getAnchuraJuego();
        }
		l.addView(new VistaJuego(this,adaptador),celda*adaptador.getAnchuraJuego(),celda*adaptador.getAlturaJuego());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_juego, menu);
		return true;
	}
	
	/*
	 * Lisener de la barra de menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {

		}else if( id==R.id.pausar){
			adaptador.pausar();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(!adaptador.isPausado()){
			adaptador.pausar();
			adaptador.limpiarActividadInterfaz();
		}
	}
	
	public void onResume(){
		super.onResume();	
		adaptador.setActividadInterfaz(this);
	}
	
	/*
	 * Capturador de eventos tactiles
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if(pulsado){
					//compruebo que eje se ha movido mas
					if(Math.abs(x_push-x)>Math.abs(y_push-y)){
						if((x_push-x)>0){
							//izquierda
							adaptador.getComecocos().setDireccion(Figura.IZQUIERDA);
						}
						else{
							//derecha
							adaptador.getComecocos().setDireccion(Figura.DERECHA);
						}
					}
					else{
						if((y_push-y)>0){
							//arriba
							adaptador.getComecocos().setDireccion(Figura.ARRIBA);
						}
						else{
							//abajo
							adaptador.getComecocos().setDireccion(Figura.ABAJO);
						}
					}
					break;
				}

			case MotionEvent.ACTION_UP:
				pulsado=true;
				x_push=x;
				y_push=y;
				break;
			case MotionEvent.ACTION_DOWN:
				pulsado=false;
				break;
			}

		return true;
	}
	
}