package com.missionbit.game.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.missionbit.game.Animations;
import com.missionbit.game.PolygonButton;

import java.util.ArrayList;

public class Female extends Character{

    private int charSize;
    private Vector3 direction;
    private Animations charAnimation, getUp, death;
    private boolean movingR, movingL,dead;
    private Rectangle bounds;

    public Female(int x, int y) {
        character = new Texture("images/femaleWalk.png");
        characterStill = new Texture("images/femaleIdle.png");
        charWalkLeft = new Texture("images/femaleWalkL.png");
        charSize = 150;
        charPos = new Vector3(x,y,0);
        targetLoc = new Vector3();
        direction = new Vector3();
        bounds = new Rectangle(x,y,charSize,charSize);
        charAnimation = new Animations(new TextureRegion(character),16,1f);
        charStill = new Animations("images/femaleIdle.png",6,4,21,1f,true);
        //charStillL = new Animations(new TextureRegion(charStillLeft), 21,1f);
        charWalkL = new Animations(new TextureRegion(charWalkLeft), 16,1f);
        getUp = new Animations("images/getup.png",4,4,14,1f,false);
        death = new Animations("images/death.png",4,6,22,1f,false);
        movingR = false;
        movingL = false;
        velocity = new Vector3(100,0,0);
        bounds = new Rectangle(charPos.x,charPos.y,49,98);
    }

    @Override
    public void update(float dt, ArrayList<PolygonButton> wall) {
        charAnimation.update(dt);
        charStill.update(dt);
        //charStillL.update(dt);
        charWalkL.update(dt);
        getUp.update(dt);
        if(dead) {
            death.update(dt);
        }


        if( charPos.dst2(targetLoc) >= 1) {
            movingR = direction.x > 0;
            movingL = direction.x < 0;
            charPos.x += direction.x * velocity.x * dt;
            charPos.y += direction.y * velocity.x * dt;
            for(PolygonButton walls:wall){
                if(walls.handleClick(charPos)){
                    //System.out.println("wall");
                    charPos.x -= direction.x * velocity.x * dt;
                    charPos.y -= direction.y * velocity.x * dt;
                    movingR = false;
                    movingL = false;
                }
            }

        } else{
            movingR = false;
            movingL = false;
        }

    }

    @Override
    public void dispose() {
        character.dispose();
        charWalkLeft.dispose();
        charStillLeft.dispose();
        characterStill.dispose();

    }

    public void draw(SpriteBatch sb){
        if (dead){
            sb.draw(death.getFrame(),charPos.x-20,charPos.y,108,112);
        }
        else if (movingR) {
            sb.draw(charAnimation.getFrame(), charPos.x, charPos.y, 49, 98);
        //    System.out.println("moving right");
        } else if (movingL) {
            sb.draw(charWalkL.getFrame(), charPos.x, charPos.y, 49, 98);
        //    System.out.println("moving left");
        } else if (!movingR && !movingL) {
            sb.draw(charStill.getFrame(), charPos.x, charPos.y, 50, 98);
        //    System.out.println("still");
        }
    }

    public void setDead(boolean x){
        dead = x;
    }
    public Animations getGetUp() {
        return getUp;
    }

    public Animations getDeath() {
        return death;
    }

    public Vector3 getTargetLoc(){
        return targetLoc;
    }

    public void setMovingL(boolean moveL){
        movingL = moveL;
    }

    public void setMovingR(boolean move){
        movingR = move;
    }

    public Boolean getMovingL(){
        return movingL;
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public Texture getChar(){
        return character;
    }

    public TextureRegion getAniWalkLeft() {
        return charWalkL.getFrame();
    }

    public TextureRegion getStillLeft(){
        return charStillL.getFrame();
    }

    public TextureRegion getAni(){
        return charAnimation.getFrame();
    }

    public TextureRegion getAniStill(){
        return charStill.getFrame();
    }

    public Vector3 getCharPos(){
        return charPos;
    }

    public void setTargetLoc(int x, int y){
        targetLoc.set(x, y, 0);
        direction.set(x, y, 0);
        direction.sub(charPos);
        direction.nor();
    }

    public void drawDebug(ShapeRenderer shapeRenderer){
        //Vector3 temp = new Vector3(charPos);
        //temp.add(direction);
        //temp.scl(10f);
        Vector3 temp = new Vector3(direction);
        temp.scl(10f);
        shapeRenderer.circle(targetLoc.x,targetLoc.y,10);
        shapeRenderer.circle(charPos.x,charPos.y,10);
        shapeRenderer.rect(charPos.x,charPos.y,49,98);


    }

    public boolean getMovingR(){
        return movingR;
    }

    public int getCharSize(){
        return charSize;
    }
}
