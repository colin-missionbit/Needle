package com.missionbit.game.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.missionbit.game.Animations;
import com.missionbit.game.Needle;

public class IntroState extends State{

    private Animations introAni;
    private Animations ortniAni;
    private boolean boardAni, needleAni;
    private long needleStartTime,boardStartTime;

    public IntroState(GameStateManager gsm) {
        super(gsm,"Music/Layers_Of_Fear_Soundtrack_The_End_feat_Penelopa_Willmann_Szynalik_.mp3");
        cam.setToOrtho(false, Needle.WIDTH, Needle.HEIGHT);
        //intro = new Texture("images/IntroA.png");
       // introAni = new Animations(new TextureRegion(intro),75,10f);
        ortniAni = new Animations("images/ORTNI.png",5,11,51, 5f,false);
        introAni = new Animations("images/IntroA.png",9,9,75, 5f,false);

        //timer stuff
        needleAni = true;
        boardAni=false;
        needleStartTime = System.currentTimeMillis();

    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        //introAni.update(dt);
        //ortniAni.update(dt);

        //timer for animations
        if(needleAni) {
            if (System.currentTimeMillis() - needleStartTime > 5100) {
                needleAni = false;
                boardAni = true;
                boardStartTime = System.currentTimeMillis();
            }
        }
        else if(boardAni){
            if(System.currentTimeMillis()-boardStartTime>5800){
                introAni.update(dt);
                boardAni = false;
                gsm.set(new BasementState(gsm));
            }
        }

        if(!ortniAni.getDone()){
            ortniAni.update(dt);
        }
        else if(!introAni.getDone()){
            introAni.update(dt);
        }



    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        //sb.draw(intro,0,0);
        if(!ortniAni.getDone()){
            sb.draw(ortniAni.getFrame(),0,0,Needle.WIDTH,Needle.HEIGHT);
        }
        else if(!introAni.getDone()) {
            sb.draw(introAni.getFrame(), 175, 0, 600, 600);
        }
        //System.out.println(introAni.getFrame());
        //System.out.println(introAni.getFrame().getRegionHeight() + ""+introAni.getFrame().getRegionWidth());
        sb.end();

    }

    @Override
    public void dispose() {
        music.stop();
        music.dispose();
    }
}
