package pt.amov.reversISEC.interfaces.views;

import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class BoardUpdate extends Thread {

    private final SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;

    BoardUpdate(SurfaceHolder surfaceHolder, GameView gameView){
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    @Override
    public void run() {
        Canvas canvas;
        int delay = 20;
        while(running){
            canvas = null;
            long startTime = System.currentTimeMillis();
            this.gameView.update();
            long endTime = System.currentTimeMillis();

            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.gameView.render(canvas);
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(canvas != null){
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            try{
                if((endTime - startTime) <= delay){
                    sleep(delay - (endTime - startTime));
                }

            }catch(InterruptedException e){
                e.printStackTrace();
            }

        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
