import com.sun.javafx.geom.Vec2f;

import java.awt.*;

public class Photon {
    static int speed=8000;
    private Vec2f p;
    private Vec2f v;
    public Photon(float x, float y){
        double theta=Math.random()*2*Math.PI;
        v=new Vec2f((float)(Math.cos(theta)*speed),(float)(Math.sin(theta)*speed));
        p=new Vec2f(x,y);
    }
    public Photon(float x, float y,Photon o){
        float ow=.92f;
        float nx=(o.p.x*ow+(1-ow)*x);
        float ny=(o.p.y*ow+(1-ow)*y);
        p=new Vec2f(nx,ny);
        v=new Vec2f(o.v);
    }
    public Photon(Photon o, int left, int right, int bot, int top){
        p=new Vec2f(0,0);
        while (p.x<left||p.x>right||p.y<bot||p.y>top) {
            float forw = (float) (20 * Math.random()-10) / speed;
            float side = (float) (14 * Math.random()-7) / speed;
            p.x = o.p.x + forw * o.v.x + side * o.v.y;
            p.y = o.p.y + forw * o.v.y + side * o.v.x;
        }
        v=new Vec2f(o.v);
    }
    public void paint(Graphics gfx){
        //BACKGROUND
        gfx.fillRect((int)p.x,(int)p.y,2,2);//background size
    }
    double refl=.9;
    public short move(float dt, boolean in, int left, int right, int bot, int top){
        p.x += v.x*dt;
        p.y += v.y*dt;
        if (p.y<bot||p.y>top){
            return 0;
        }
        if (p.x<left){
            if (in){
                v.x=-v.x;
                p.x=2*left-p.x;
            }else {
                return 0;
            }
        }else if (p.x>right){
            if (in){
                if (Math.random()<.95){
                    v.x=-v.x;
                    p.x=2*right-p.x;
                }else {
                    return 1;
                }
            }else {
                return 0;
            }
        }

        return 2;
    }

}
