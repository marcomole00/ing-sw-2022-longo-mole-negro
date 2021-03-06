package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.GameException;
import it.polimi.ingsw.exceptions.NoSuchStudentException;
import it.polimi.ingsw.messages.GameEvent;
import it.polimi.ingsw.model.CharacterCard;


/**
 * This is the abstract class for the states generated by activating cards. Not all cards generate a state, only the ones
 * that requires an input from the user. The main difference from the GameState is that Character States need a reference
 * to the Game State that called them. This is because activating a Card is "special" move that can happen at any point
 * of the turn.
 *
 */
public abstract class CharacterState extends  GameState{


    //todo make actual use of inheritance and factor out nextState and cc in this class.

    CharacterState(RoundController context, int numberOfEvents) {
        super(context, numberOfEvents);
    }

    // this is here only for templating reasons.
    CharacterState(RoundController context, int numberOfEvents, GameState nextState, CharacterCard cc){
        super(context,numberOfEvents);
    }



    @Override
    public boolean checkValidEvent(GameEvent event) {
        return false;
    }

    @Override
    public void executeEvent(GameEvent event)  throws GameException {

    }
}
