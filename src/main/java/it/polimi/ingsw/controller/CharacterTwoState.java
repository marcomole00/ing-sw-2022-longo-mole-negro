package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.NoSpaceForStudentException;
import it.polimi.ingsw.exceptions.NoSuchStudentException;
import it.polimi.ingsw.messages.events.GameEventType;
import it.polimi.ingsw.messages.events.SwapStudentCardEntranceEvent;
import it.polimi.ingsw.messages.GameEvent;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;

/**
 *
 * È una carta con 6 studenti. scambia fino a 3 studenti presenti nel tuo ingresso
 */

public class CharacterTwoState extends CharacterState{


    CharacterCard cc;

    GameState nextState;


    public CharacterTwoState(RoundController context, int numberOfEvents, GameState nextState, CharacterCard cc) {
        super(context,numberOfEvents);
        this.cc = cc;
        this.nextState = nextState;
    }


    @Override
    public  boolean checkValidEvent(GameEvent event){

        return event.getEventType() == GameEventType.SWAP_STUDENT_CARD_ENTRANCE || event.getEventType() == GameEventType.END_SELECTION;

    }

    @Override
    public void executeEvent(GameEvent gameEvent) throws NoSuchStudentException{

        if (gameEvent.getEventType() == GameEventType.END_SELECTION){
            context.changeState(nextState);
        } else if(gameEvent.getEventType() == GameEventType.SWAP_STUDENT_CARD_ENTRANCE){

            SwapStudentCardEntranceEvent eventCast = (SwapStudentCardEntranceEvent) gameEvent;
          //  Player player = context.getPlayerByUsername(eventCast.getPlayerName());
            Player player = context.getSeatedPlayers()[eventCast.getPlayerNumber()];

            if (cc.getStudentHolder().getStudentByColor(eventCast.getStudentFromCard()) ==0)
                throw new NoSuchStudentException("the card doesn't have this student");
            if (player.getSchool().getStudentsAtEntrance().getStudentByColor(eventCast.getStudentFromEntrance()) ==0)
                throw new NoSuchStudentException("the player doesn't have this student in the entrance");

            try{
                player.getSchool().getStudentsAtEntrance().moveStudentTo(eventCast.getStudentFromEntrance(), cc.getStudentHolder());
                cc.getStudentHolder().moveStudentTo(eventCast.getStudentFromCard(), player.getSchool().getStudentsAtEntrance());
                context.gameManager.getModelObserver().moveStudentFromEntranceToCard(2,player.getPlayerNumber(),eventCast.getStudentFromEntrance());
                context.gameManager.getModelObserver().moveStudentFromCardToEntrance(2,player.getPlayerNumber(),eventCast.getStudentFromCard());

                numberOfEvents--;
            } catch (NoSpaceForStudentException ignored){}

        if (numberOfEvents ==0){

            context.changeState(nextState);
        }


        }



    }



}
