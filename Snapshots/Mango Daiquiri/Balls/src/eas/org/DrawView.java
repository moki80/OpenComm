package eas.org;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class DrawView extends View {
   private ColorBall colorball1, colorball2, colorball3, colorball4, colorball5;
    
    public DrawView(Context context) {
        super(context);
        setFocusable(true); //not yet necessary, but you never know what the future brings
        
        // declare each ball with the ColorBall class
        colorball1 = new ColorBall(context,R.drawable.bol_groen);
        colorball2 = new ColorBall(context,R.drawable.bol_rood);
        colorball3 = new ColorBall(context,R.drawable.bol_blauw);
        colorball4 = new ColorBall(context,R.drawable.bol_geel);
        colorball5 = new ColorBall(context,R.drawable.bol_paars);
        
    }
    
    @Override protected void onDraw(Canvas canvas) {
        //canvas.drawColor(0xFFCCCCCC);     //if you want another background color       
        
    	// move the balls at every canvas draw
        colorball1.moveBall(5,3);
        colorball2.moveBall(3,4);
        colorball3.moveBall(2,2);
        colorball4.moveBall(4,5);
        colorball5.moveBall(5,1);

        //draw the balls on the canvas
        canvas.drawBitmap(colorball1.getBitmap(), colorball1.getX(), colorball1.getY(), null);
        canvas.drawBitmap(colorball2.getBitmap(), colorball2.getX(), colorball2.getY(), null);
        canvas.drawBitmap(colorball3.getBitmap(), colorball3.getX(), colorball3.getY(), null);
        canvas.drawBitmap(colorball4.getBitmap(), colorball4.getX(), colorball4.getY(), null);
        canvas.drawBitmap(colorball5.getBitmap(), colorball5.getX(), colorball5.getY(), null);
        
        // refresh the canvas
        invalidate();
    }
    
    
}
