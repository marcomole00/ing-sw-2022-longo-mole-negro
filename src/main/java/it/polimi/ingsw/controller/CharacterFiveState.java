package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.events.ChooseIslandEvent;
import it.polimi.ingsw.messages.events.GameEventType;
import it.polimi.ingsw.messages.GameEvent;
import it.polimi.ingsw.model.CharacterCardFive;


/**
 * This is the state generated by the card 5 ({@link it.polimi.ingsw.model.CharacterCardFive}).
 * When the CHOOSE_ISLAND event is received it adds a NoToken to it.
 *
 */

public class CharacterFiveState extends  CharacterState{


    CharacterCardFive cc;
    GameState nextState;

    public CharacterFiveState(RoundController context, int numberOfEvents, GameState nextState, CharacterCardFive cc) {
        super(context, numberOfEvents);
        this.cc = cc;
        this.nextState=nextState;
    }

    /**
     * @param event The event to check
     * @return true if the event is a CHOOSE_ISLAND
     */
    @Override
    public boolean checkValidEvent(GameEvent event) {
        return event.getEventType()== GameEventType.CHOOSE_ISLAND;
    }

    /**
     * Adds a no entry token to the island specified in the event
     * @param event The event to process.
     */
    @Override
    public void executeEvent(GameEvent event)  {


        if (cc.getNoEntryToken() > 0) {
            ChooseIslandEvent eventCast = (ChooseIslandEvent) event;
            context.gameManager.addNoEntry(eventCast.getIslandIndex());
            numberOfEvents--;
            cc.removeNoEntryToken();
        } // if the player activate the card with 0 token left, that's his fault, not mine.


        if(numberOfEvents == 0){
            context.changeState(nextState);
        }

    }
}
