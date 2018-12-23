package pt.amov.user.interfaces.views;

import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class RenderThread extends Thread {

    private final SurfaceHolder surfaceHolder;
    private ReversiView reversiView;
    private boolean running;

    RenderThread(SurfaceHolder surfaceHolder, ReversiView reversiView){
        this.surfaceHolder = surfaceHolder;
        this.reversiView = reversiView;
    }

    @Override
    public void run() {
        Canvas canvas;
        int delay = 20;
        while(running){
            canvas = null;
            long startTime = System.currentTimeMillis();
            this.reversiView.update();
            long endTime = System.currentTimeMillis();

            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.reversiView.render(canvas);
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
