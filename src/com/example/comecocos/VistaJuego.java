package com.example.comecocos;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import data.AdaptadorVistaLogica;
import data.Fantasma;
import data.Figura;

import data.Rejilla;

public class VistaJuego extends SurfaceView implements SurfaceHolder.Callback {
	HebraRepintadoJuego thread;
	private int anchoCelda=-1;
	//private int x_push;
	//private int y_push;
	//private boolean pulsado=false;
	private int xoffset=0;
	private AdaptadorVistaLogica adaptador;
	//private DragAndDropCanvas frame;
	
	public VistaJuego(Context context,AdaptadorVistaLogica adaptador) {
		super(context);
		this.adaptador=adaptador;
		getHolder().addCallback(this);
	}

	
	@Override
	public void onDraw(Canvas canvas) {
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		p.setAntiAlias(true);
		canvas.drawRect(0, 0, getWidth(), getHeight(), p);
		
		if(anchoCelda==-1){
            anchoCelda=Math.max(getWidth()/adaptador.getRejilla().getAnchura(),(getHeight())/adaptador.getRejilla().getAltura());
        }
		xoffset=(getWidth()-adaptador.getRejilla().getAnchura()*anchoCelda)/2;
		
		p.setColor(Color.GRAY);
		canvas.drawRect(xoffset, 0, xoffset+anchoCelda*adaptador.getRejilla().getAnchura(),anchoCelda*adaptador.getRejilla().getAltura(), p);
        dibujaRejilla(canvas);
        dibujaComecocos(canvas);
        dibujaFantasmas(canvas);
        if(adaptador.isPausado()){
        	p.setColor(Color.GREEN);
            p.setTextSize(30);
            canvas.drawText("PAUSADO",xoffset+anchoCelda*adaptador.getRejilla().getAnchura()/3 , anchoCelda*adaptador.getRejilla().getAltura()/2, p);
        }
        
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		thread = new HebraRepintadoJuego(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}
	

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) { }
		}
	}
	
	/*
	 * Capturador de eventos tactiles
	 */
	/*@Override
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
	}*/
	
	/**
	 * Dibuja cada una de las celdas de la matriz bidimensional de la Rejilla.
	 * en la que cada celda puede estar VACIA, contener
	 * un BLOQUE (muro exterior), un BLOQUE_CELDA (donde se resguardan los fantasmas),
	 * un PPEQUE o un PGRANDE (permite comer fantasmas)
	 * @param el Graphics donde se dibujar Ì�
	 */
	 private void dibujaRejilla(Canvas g){
		 int i,j;
	     Paint p = new Paint();
	     Rejilla rejilla=adaptador.getRejilla();
	      
	     for(i=0;i<rejilla.getAnchura();i++){
	    	 for(j=0;j<rejilla.getAltura();j++){
	    		 if(rejilla.getTipoCelda(i,j) == Rejilla.BLOQUE){
	    			 p.setColor(Color.DKGRAY);
	                 g.drawRect(xoffset+i*anchoCelda,j*anchoCelda,xoffset+(i+1)*anchoCelda,(j+1)*anchoCelda,p);
	    			 //g.drawRect(left, top, right, bottom, paint);
	             } else if(rejilla.getTipoCelda(i,j) == Rejilla.PPEQUE){
	            	 p.setColor(Color.YELLOW);
	                 g.drawCircle(xoffset+i*anchoCelda+(anchoCelda/2),j*anchoCelda+(anchoCelda/2), anchoCelda/6, p);
	             }else if(rejilla.getTipoCelda(i,j) == Rejilla.PGRANDE){
	            	 p.setColor(Color.RED);
	                 g.drawCircle(xoffset+i*anchoCelda+(anchoCelda/2), j*anchoCelda+(anchoCelda/2), anchoCelda/4, p);
	             }else if(rejilla.getTipoCelda(i,j) == Rejilla.BLOQUE_CELDA){
	            	 p.setColor(Color.BLUE);
	                 g.drawCircle(xoffset+i*anchoCelda+anchoCelda/2, j*anchoCelda+anchoCelda/2, anchoCelda/2-anchoCelda/5, p);
	             }
	        }
	    }
	 }
	    
	/**
	 * Dibuja el elemento Comecocos segun este moviendose hacia arriba, hacia abajo, derecha, izquierda o este parado
	 * @param g Graphics donde se va a dibujar
	 */
	 private void dibujaComecocos(Canvas g){
		 Paint p = new Paint();
	     Figura comecocos=adaptador.getComecocos();
	     p.setColor(Color.YELLOW);
	     int x[]=new int[3];
	     int y[]=new int[3];
	     switch(comecocos.getDireccion()){
	         case Figura.DERECHA:
	             g.drawCircle(xoffset+comecocos.getX()*anchoCelda+anchoCelda/2+comecocos.getFase()*(anchoCelda/4), comecocos.getY()*anchoCelda+anchoCelda/2, anchoCelda/2, p);
	             if(comecocos.getFase()==2||comecocos.getFase()==3){
	                /* x[0]=xoffset+(anchoCelda/2)+comecocos.getFase()*(anchoCelda/4)+comecocos.getX()*anchoCelda;
	                 x[1]=xoffset+(anchoCelda)+comecocos.getFase()*(anchoCelda/4)+comecocos.getX()*anchoCelda+(anchoCelda/4);
	                 x[2]=xoffset+(anchoCelda)+comecocos.getFase()*(anchoCelda/4)+comecocos.getX()*anchoCelda+(anchoCelda/4);
	                 y[0]=comecocos.getY()*anchoCelda+(anchoCelda/2);
	                 y[1]=comecocos.getY()*anchoCelda;
	                 y[2]=comecocos.getY()*anchoCelda+(anchoCelda*3/4);
	                 g.setColor(Color.LIGHT_GRAY);
	                 g.fillPolygon(x, y, 3);*/
	             }
	               
	             break;
	         case Figura.IZQUIERDA:
	             g.drawCircle(xoffset+comecocos.getX()*anchoCelda+anchoCelda/2-comecocos.getFase()*(anchoCelda/4), comecocos.getY()*anchoCelda+anchoCelda/2, anchoCelda/2,p);
	             if(comecocos.getFase()==2||comecocos.getFase()==3){
	                 /*x[0]=xoffset+(anchoCelda/2)-comecocos.getFase()*(anchoCelda/4)+comecocos.getX()*anchoCelda;
	                 x[1]=xoffset-comecocos.getFase()*(anchoCelda/4)+comecocos.getX()*anchoCelda;
	                 x[2]=xoffset-comecocos.getFase()*(anchoCelda/4)+comecocos.getX()*anchoCelda;
	                 y[0]=comecocos.getY()*anchoCelda+(anchoCelda/2);
	                 y[1]=comecocos.getY()*anchoCelda;
	                 y[2]=comecocos.getY()*anchoCelda+(anchoCelda*3/4);
	                 g.setColor(Color.LIGHT_GRAY);
	                 g.fillPolygon(x, y, 3);*/
	             }
	                
	             break;
	         case Figura.ARRIBA:
	             g.drawCircle(xoffset+comecocos.getX()*anchoCelda+anchoCelda/2, comecocos.getY()*anchoCelda+anchoCelda/2-comecocos.getFase()*(anchoCelda/4), anchoCelda/2, p);
	             if(comecocos.getFase()==2||comecocos.getFase()==3){
	                 /*x[0]=xoffset+(anchoCelda/4)+comecocos.getX()*anchoCelda;
	                 x[1]=xoffset+(anchoCelda*3/4)+comecocos.getX()*anchoCelda;
	                 x[2]=xoffset+(anchoCelda/2)+comecocos.getX()*anchoCelda;
	                 y[0]=(comecocos.getY()*anchoCelda)-comecocos.getFase()*(anchoCelda/4);
	                 y[1]=(comecocos.getY()*anchoCelda)-comecocos.getFase()*(anchoCelda/4);
	                 y[2]=(comecocos.getY()*anchoCelda)+(anchoCelda/2)-comecocos.getFase()*(anchoCelda/4);
	                 //g.setColor(Color.LIGHT_GRAY);
	                 //g.fillPolygon(x, y, 3);*/
	             }
	             break;   
	         case Figura.ABAJO:
	             g.drawCircle(xoffset+comecocos.getX()*anchoCelda+anchoCelda/2, comecocos.getY()*anchoCelda+anchoCelda/2+comecocos.getFase()*(anchoCelda/4), anchoCelda/2, p);
	             if(comecocos.getFase()==2||comecocos.getFase()==3){
	             /*    x[0]=xoffset+(anchoCelda/4)+comecocos.getX()*anchoCelda;
	                 x[1]=xoffset+(anchoCelda*3/4)+comecocos.getX()*anchoCelda;
	                 x[2]=xoffset+(anchoCelda/2)+comecocos.getX()*anchoCelda;
	                 y[0]=anchoCelda+(comecocos.getY()*anchoCelda)+comecocos.getFase()*(anchoCelda/4);
	                 y[1]=anchoCelda+(comecocos.getY()*anchoCelda)+comecocos.getFase()*(anchoCelda/4);
	                 y[2]=(comecocos.getY()*anchoCelda)+(anchoCelda/2)+comecocos.getFase()*(anchoCelda/4);
	                 g.setColor(Color.LIGHT_GRAY);
	                 g.fillPolygon(x, y, 3);*/
	             }
	             break;    
	         case Figura.PARADO:
	             g.drawCircle(xoffset+comecocos.getX()*anchoCelda+anchoCelda/2, comecocos.getY()*anchoCelda+anchoCelda/2, anchoCelda/2,p);
	             break;   
	    }
	  }
	    
	   /**
	    * Dibuja a los fantasmas dependiendo de la direccion y tambien si es comestible o no.
	    * @param g Graphics donde se va a dibujar
	    */
	   private void dibujaFantasmas(Canvas g){
	        Paint p = new Paint();
	        Fantasma[] fantasmas=adaptador.getFantasmas();
	        
	        for(int i=0;i<fantasmas.length;i++){
	            Fantasma fantasma=fantasmas[i];
	            if(fantasma!=null){
	                if(fantasma.isComestible()){
	                    p.setColor(Color.BLUE);
	                }
	                else{
	                    p.setColor(Color.RED);
	                }
	                
	                switch(fantasma.getDireccion()){
	                    case Figura.DERECHA:
	                        g.drawCircle(xoffset+fantasma.getX()*anchoCelda+anchoCelda/2+fantasma.getFase()*(anchoCelda/4), fantasma.getY()*anchoCelda+anchoCelda/2, anchoCelda/2, p);
	                        //g.drawRect(xoffset+fantasma.getX()*(anchoCelda)+fantasma.getFase()*(anchoCelda/4),(fantasma.getY()*anchoCelda)+anchoCelda/2,anchoCelda,anchoCelda/2,p);
	                        p.setColor(Color.YELLOW);
	                        //g.drawCircle(xoffset+fantasma.getX()*anchoCelda+fantasma.getFase()*(anchoCelda/4)+anchoCelda/4,fantasma.getY()*anchoCelda+anchoCelda/4,anchoCelda/5,p);
	                        //g.drawCircle(xoffset+fantasma.getX()*anchoCelda+fantasma.getFase()*(anchoCelda/4)+anchoCelda*3/4,fantasma.getY()*anchoCelda+anchoCelda/4,anchoCelda/5,p);
	                        break;
	                    case Figura.IZQUIERDA:
	                        g.drawCircle(xoffset+fantasma.getX()*anchoCelda+anchoCelda/2-fantasma.getFase()*(anchoCelda/4), fantasma.getY()*anchoCelda+anchoCelda/2, anchoCelda/2, p);
	                        //g.drawRect(xoffset+fantasma.getX()*(anchoCelda)-fantasma.getFase()*(anchoCelda/4),(fantasma.getY()*anchoCelda)+anchoCelda/2,anchoCelda,anchoCelda/2,p);
	                        p.setColor(Color.YELLOW);
	                        //g.drawCircle(xoffset+fantasma.getX()*anchoCelda-fantasma.getFase()*(anchoCelda/4)+anchoCelda/4,fantasma.getY()*anchoCelda+anchoCelda/4,anchoCelda/5,p);
	                        //g.drawCircle(xoffset+fantasma.getX()*anchoCelda-fantasma.getFase()*(anchoCelda/4)+anchoCelda*3/4,fantasma.getY()*anchoCelda+anchoCelda/4,anchoCelda/5,p);
	                        break;
	                    case Figura.ARRIBA:
	                        g.drawCircle(xoffset+fantasma.getX()*anchoCelda+anchoCelda/2, fantasma.getY()*anchoCelda+anchoCelda/2-fantasma.getFase()*(anchoCelda/4), anchoCelda/2, p);
	                        //g.drawRect(xoffset+fantasma.getX()*(anchoCelda),(fantasma.getY()*anchoCelda)+(anchoCelda/2)-fantasma.getFase()*(anchoCelda/4),anchoCelda,anchoCelda/2,p);
	                        p.setColor(Color.YELLOW);
	                        //g.drawCircle(xoffset+fantasma.getX()*anchoCelda+anchoCelda/4,fantasma.getY()*anchoCelda-fantasma.getFase()*(anchoCelda/4)+anchoCelda/4,anchoCelda/5,p);
	                        //g.drawCircle(xoffset+fantasma.getX()*anchoCelda+anchoCelda*3/4,fantasma.getY()*anchoCelda-fantasma.getFase()*(anchoCelda/4)+anchoCelda/4,anchoCelda/5,p);
	                        
	                        break;   
	                    case Figura.ABAJO:
	                        g.drawCircle(xoffset+fantasma.getX()*anchoCelda+anchoCelda/2, fantasma.getY()*anchoCelda+anchoCelda/2+fantasma.getFase()*(anchoCelda/4), anchoCelda/2, p);
	                        //g.drawRect(xoffset+fantasma.getX()*(anchoCelda),(fantasma.getY()*anchoCelda)+(anchoCelda/2)+fantasma.getFase()*(anchoCelda/4),anchoCelda,anchoCelda/2,p);
	                        p.setColor(Color.YELLOW);
	                        //g.drawCircle(xoffset+fantasma.getX()*anchoCelda+anchoCelda/4,fantasma.getY()*anchoCelda+fantasma.getFase()*(anchoCelda/4)+anchoCelda/4,anchoCelda/5,p);
	                        //g.drawCircle(xoffset+fantasma.getX()*anchoCelda+anchoCelda*3/4,fantasma.getY()*anchoCelda+fantasma.getFase()*(anchoCelda/4)+anchoCelda/4,anchoCelda/5,p);
	                        break;    
	                    case Figura.PARADO:
	                        g.drawCircle(xoffset+fantasma.getX()*anchoCelda+anchoCelda/2, fantasma.getY()*anchoCelda+anchoCelda/2, anchoCelda/2, p);
	                        //g.drawRect(xoffset+fantasma.getX()*(anchoCelda),(fantasma.getY()*anchoCelda)+(anchoCelda/2),anchoCelda,anchoCelda/2,p);
	                        break;   
	                }
	            }
	        }
	    }
}
