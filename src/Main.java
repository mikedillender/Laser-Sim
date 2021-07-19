import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Main extends Applet implements Runnable, KeyListener {

    //BASIC VARIABLES
    private final int WIDTH=3300, HEIGHT=800;

    //GRAPHICS OBJECTS
    private Thread thread;
    Graphics gfx;
    Image img;

    //COLORS
    Color background=new Color(255, 255, 255);
    Color gridColor=new Color(150, 150,150);

    ArrayList<Photon> photons=new ArrayList<>();
    ArrayList<Photon> ejects=new ArrayList<>();
    Color incol=new Color(0, 41, 255);
    Color outcol=new Color(13, 124, 0);
    Color shell=new Color(36, 38, 55);
    Color lens=new Color(118, 185, 191);
    int bottom=350;
    int top=bottom+200;
    int left=200;
    int right=left+900;
    static Color lvls[]={
            new Color(0, 0, 0),
            new Color(60, 75, 226),
            new Color(250, 141, 19),
            new Color(240, 51, 24)};

    float c=299792458;
    float atoms=1000000;
    float n[]=new float[]{atoms,0,0,0};
    double hw3pi8=8*Math.PI*6.626*Math.pow(10,27-34)/Math.pow(650,3);//h/wavelength^3=hv^3/c^3


    double b12=10;
    double a21=hw3pi8*b12;
    float Rstabs=0;
    float Rstem;
    float Rspem;


    public void init(){//STARTS THE PROGRAM
        a21=a21;//Speed up initial seeding
        this.resize(WIDTH, HEIGHT);
        this.addKeyListener(this);
        img=createImage(WIDTH,HEIGHT);
        gfx=img.getGraphics();
        thread=new Thread(this);
        thread.start();
        System.out.println("B12="+b12);
        System.out.println("A21="+a21);
    }
    public void paint(Graphics g){
        //BACKGROUND
        gfx.setColor(background);//background
        gfx.fillRect(0,0,WIDTH,HEIGHT);//background size
        gfx.setColor(gridColor);
        gfx.fillRect(left,bottom,WIDTH-left,5);//background size
        gfx.fillRect(left,top,WIDTH-left,5);//background size
        gfx.setColor(shell);
        gfx.fillRect(left,bottom,right-left,5);//background size
        gfx.fillRect(left,top,right-left,5);//background size
        gfx.fillRect(left,bottom,5,top-bottom);//background size
        gfx.setColor(lens);
        gfx.fillRect(right,bottom,5,top-bottom+5);//background size
        gfx.setColor(Color.BLACK);
        gfx.drawString("Rstem = "+Rstem,200,20);
        gfx.drawString("Rstabs = "+Rstabs,200,50);
        gfx.drawString("Rspem = "+Rspem,200,80);
        gfx.drawString("stem t= "+stem,240,35);
        gfx.drawString("stabs t= "+stabs,240,65);
        gfx.drawString("spem t= "+spem,240,95);
        //paintCoordGrid(gfx);
        int yoff=100;
        gfx.setColor(incol);//background
        for (int i=0;i<photons.size();i++){
            photons.get(i).paint(gfx);
        }
        gfx.setColor(outcol);//background
        for (int i=0;i<ejects.size();i++){
            ejects.get(i).paint(gfx);
        }
        for (int i=3;i>-1;i--){
            gfx.setColor(lvls[i]);
            int num=(int)((n[i]/atoms)*500);
            gfx.fillRect(50,yoff,100,num);
            yoff+=num;
        }

        //RENDER FOREGROUND


        //FINAL
        g.drawImage(img,0,0,this);
    }

    public void update(Graphics g){ //REDRAWS FRAME
        paint(g);
    }
    //wavelen=650nm => freq=4.3*10^14
    float stem=0;
    float spem=0;
    float stabs=0;
    int pc=10;//how many photons are represented by each ball
    float nmult=(float)(Math.pow(10,10));

    public void run() { for (;;){//CALLS UPDATES AND REFRESHES THE GAME
            float rdt=.005f;
            float dt=rdt*.01f;
            //UPDATES
            for (int i=0;i<ejects.size(); i++){
                short b=ejects.get(i).move(.015f, false,0,WIDTH,0,HEIGHT);
                if (b==0){
                    ejects.remove(i);
                    i--;
                }
            }
            for (int i=0;i<photons.size(); i++){
                //short b=photons.get(i).move(.015f, true, left,right,bottom,top);
                short b=photons.get(i).move(.015f, true, left,right,bottom,top);
                if (b!=2){
                    //if (b==1)
                    ejects.add(photons.get(i));
                    photons.remove(i);
                    i--;
                }
            }
            float irrad=(float)(photons.size()*Math.pow(10,-20+15));
            Rstabs=(float)(b12*n[1]*nmult*irrad/c);
            Rstem=(float)(b12*n[2]*nmult*irrad/c);
            Rspem=(float)(a21*n[2]*nmult)*100;
            stabs+=Rstabs*dt;
            stem+=Rstem*dt;
            spem+=Rspem*dt;
            while (spem>pc){
                int x=(int)(Math.random()*(right-left))+left;
                int y=(int)(Math.random()*(top-bottom))+bottom;
                photons.add(new Photon(x,y));
                n[2]-=pc;
                n[1]+=pc;
                spem-=pc;
            }
            while (stem>pc){
                Photon p=photons.get((int)(photons.size()*Math.random()));
                int x=(int)(Math.random()*(right-left))+left;
                int y=(int)(Math.random()*(top-bottom))+bottom;
                photons.add(new Photon(x,y,p));
                //photons.add(new Photon(p,left,right,bottom,top));
                n[2]-=pc;
                n[1]+=pc;
                stem-=pc;
            }
            while (stabs>pc){
                photons.remove((int)(photons.size()*Math.random()));
                n[1]-=pc;
                n[2]+=pc;
                stabs-=pc;
            }


            float pump=.02f*n[0];
            n[0]-=pump;
            n[3]+=pump;
            float n32=.05f*n[3];
            n[3]-=n32;
            n[2]+=n32;
            float n10=.005f*n[1];
            n[1]-=n10;
            n[0]+=n10;

            repaint();//UPDATES FRAME
            try{ Thread.sleep((int)(dt*1000)); } //ADDS TIME BETWEEN FRAMES (FPS)
            catch (InterruptedException e) { e.printStackTrace();System.out.println("GAME FAILED TO RUN"); }//TELLS USER IF GAME CRASHES AND WHY
    } }


    //INPUT
    public void keyPressed(KeyEvent e) {

    }
    public void keyReleased(KeyEvent e) {

    }
    public void keyTyped(KeyEvent e) { }

    //QUICK METHOD I MADE TO DISPLAY A COORDINATE GRID
    public void paintCoordGrid(Graphics gfx){
        gfx.setColor(gridColor);
        for (int x=100; x<WIDTH; x+=100){
            gfx.drawString(""+x, x, 20);
            gfx.drawRect(x, 20, 1, HEIGHT);
        }
        for (int y=100; y<HEIGHT; y+=100){
            gfx.drawString(""+y, 20, y);
            gfx.drawRect(20, y, WIDTH, 1);
        }
    }
}