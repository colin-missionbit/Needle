package com.missionbit.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.missionbit.game.Animations;
import com.missionbit.game.Button;
import com.missionbit.game.Interactables;
import com.missionbit.game.Needle;
import com.missionbit.game.PolygonButton;
import com.missionbit.game.characters.Female;

import java.sql.Time;
import java.util.ArrayList;

public class BasementState extends State {

    private Female female;
    private Texture bkgrd;
    private Button doorButton;
    private ShapeRenderer debugRenderer = new ShapeRenderer();
    private boolean showDebug = true;
    private PolygonButton safeButton;
    private float[] safevertices = {776, 100, 777, 186, 796, 142, 796, 58};
    private Interactables hangingBody, bleedingBody, bookshelf, invButton;
    private float[][] wall = new float[][]{
            {110.0f, 315.0f, 111.0f, 121.0f, 769.0f, 121.0f, 771.0f, 318.0f, 109.0f, 317.0f, },
            {769.0f, 315.0f, 825.0f, 348.0f, 826.0f, 188.0f, 859.0f, 85.0f, 812.0f, 27.0f, 769.0f, 122.0f, },
            {108.0f, 319.0f, 0.0f, 382.0f, 6.0f, 11.0f, 109.0f, 123.0f, },
    };

    private ArrayList<PolygonButton> walls;
    BitmapFont font;

    private boolean clicked = false;
    private GameStateManager gameStateManager;


    public BasementState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, Needle.WIDTH / 1.5f, Needle.HEIGHT / 1.5f);
        female = new Female(50, 50);
        bkgrd = new Texture("images/basement.png");

        //interactables
        hangingBody = new Interactables(new Texture("images/Hanging.png"), 150, 175, 70, 130, 8, 1f);
        bleedingBody = new Interactables(new Texture("images/Bleeding.png"), 220, 85, 362, 110, 8, 1f);
        bookshelf = new Interactables(new Texture("images/BOOKSHELF.png"), 589, 115, 163, 169, 28, 2f);
        invButton = new Interactables(new Texture("images/Inventory.png"),10,20,40,40);
        doorButton = new Button(830,300,70,100,"door");
        safeButton = new PolygonButton(safevertices);

        //wall bounds
        walls = new ArrayList<PolygonButton>();
        for(int i=0;i<wall.length;i++){
            walls.add(new PolygonButton(wall[i]));
        }

        //timer stuff
        gameStateManager = gsm;
        gameStateManager.setStartTime();
        font = new BitmapFont();

    }

    @Override
    protected void handleInput() {

        //move char if player taps
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(touchPos);
            System.out.println("Click" + touchPos.x + " " + touchPos.y);
            System.out.println(touchPos.x + ", " + touchPos.y);

            //switch to first person bookshelf if touched
            if (bookshelf.getButton().handleClick(touchPos)) {
                gsm.push(new BookshelfState(gsm));
            }
            //switch to safe
            else if (safeButton.handleClick(touchPos)) {
                gsm.push(new SafeState(gsm));
            }
            else if(doorButton.handleClick(touchPos)) {
                if (gsm.getInventory().getKey()){
                    gsm.push(new SecondFloorState(gsm));
                 }
            }
            else if(bleedingBody.getButton().handleClick(touchPos)){
                gsm.push(new DeadBodiesState(gsm));
            }
            else if(hangingBody.getButton().handleClick(touchPos)){
                gsm.push(new HangingState(gsm));
            }
            else if(invButton.getButton().handleClick(touchPos)){
                gsm.push(new InventoryState(gsm));
            }
            else {
                female.setTargetLoc((int) touchPos.x, (int) touchPos.y);
            }

        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        female.update(dt, walls);
        hangingBody.update(dt);
        bleedingBody.update(dt);
        bookshelf.update(dt);
        //camera bounds
        float minX = cam.viewportWidth / 2, maxX = bkgrd.getWidth() - cam.viewportWidth / 2;
        cam.position.x = female.getCharPos().x;
        if (cam.position.x <= minX) {
            cam.position.x = minX;
        } else if (cam.position.x > maxX) {
            cam.position.x = maxX;
        } else {
            cam.position.x = female.getCharPos().x;
        }

        //timer
        if (!gameStateManager.getStopTimer()){
            if (System.currentTimeMillis() - gameStateManager.getStartTime() > 180000) {
                gsm.set(new MenuState(gsm));
            }
        }

        gsm.getInventory().update(cam);

        cam.update();

    }


    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.setProjectionMatrix(cam.combined);

        //draw the background
        sb.draw(bkgrd, 0, 0, Needle.WIDTH, Needle.HEIGHT);

        //draw the interactables
        sb.draw(bookshelf.getFrame(), bookshelf.getXLoc(), bookshelf.getYLoc(), bookshelf.getWidth(), bookshelf.getHeight());
        sb.draw(hangingBody.getFrame(), hangingBody.getXLoc(), hangingBody.getYLoc(), hangingBody.getWidth(), hangingBody.getHeight());
        sb.draw(bleedingBody.getFrame(), bleedingBody.getXLoc(), bleedingBody.getYLoc(), bleedingBody.getWidth(), bleedingBody.getHeight());
        gsm.getInventory().draw(sb);

        //draw the character
        if (female.getMovingR()) {
            sb.draw(female.getAni(), female.getCharPos().x, female.getCharPos().y, 49, 98);
            //System.out.println("moving right");
        } else if (female.getMovingL()) {
            sb.draw(female.getAniWalkLeft(), female.getCharPos().x, female.getCharPos().y, 49, 98);
            //System.out.println("moving left");
        } else if (!female.getMovingR() && !female.getMovingL()) {
            sb.draw(female.getAniStill(), female.getCharPos().x, female.getCharPos().y, 50, 98);
            //System.out.println("still");
        }

        //display timer
        if(!gameStateManager.getStopTimer()) {
            font.setColor(Color.WHITE);
            font.getData().setScale(2, 2);
            font.draw(sb, ((181000 - (System.currentTimeMillis() - gameStateManager.getStartTime())) / 1000) + " ", cam.position.x, Needle.HEIGHT / 1.5f);
        }


        sb.end();

        if (showDebug) {
            debugRenderer.setProjectionMatrix(cam.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor(1, 1, 1, 1);
            female.drawDebug(debugRenderer);
            //bookshelfButton.drawDebug(debugRenderer);
            safeButton.drawDebug(debugRenderer);
            hangingBody.getButton().drawDebug(debugRenderer);
            bookshelf.getButton().drawDebug(debugRenderer);
            bleedingBody.getButton().drawDebug(debugRenderer);
            doorButton.drawDebug(debugRenderer);
            invButton.getButton().drawDebug(debugRenderer);
            for(PolygonButton wall:walls){
                wall.drawDebug(debugRenderer);
            }
        }
        debugRenderer.end();

    }


    @Override
    public void dispose() {
        female.dispose();
        bkgrd.dispose();
        hangingBody.dispose();
        bleedingBody.dispose();
        bookshelf.dispose();
    }
}


