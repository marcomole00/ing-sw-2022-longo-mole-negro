package it.polimi.ingsw.model;


import it.polimi.ingsw.controller.CharacterEightState;
import it.polimi.ingsw.controller.CharacterState;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.RoundController;
import it.polimi.ingsw.server.ModelObserver;

/**
 *
 * Choose an island calculate the influence as if mother nature was on it.
 * This card generates a Character State.
 */
public class CharacterCardEight extends CharacterCard {

    public CharacterCardEight(ModelObserver modelObserver){
        this.id = 8;
        this.price=3;
        this.setModelObserver(modelObserver);
    }

    @Override
    public int getPrice() {
        return price;
    }




    @Override
    public StudentHolder getStudentHolder() {
        return null;
    }

    @Override
    public void deactivateEffect() {

    }


    public CharacterState getCharacterState(RoundController context, GameState nextState){

        return new CharacterEightState(context,1,nextState) ;

    }

}
