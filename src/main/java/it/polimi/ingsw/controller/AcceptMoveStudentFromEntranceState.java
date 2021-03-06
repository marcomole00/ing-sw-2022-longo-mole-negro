package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.GameException;
import it.polimi.ingsw.messages.GameEvent;
import it.polimi.ingsw.messages.events.ActivateCharacterCardEvent;
import it.polimi.ingsw.messages.events.GameEventType;
import it.polimi.ingsw.messages.events.MoveStudentFromEntranceToIslandEvent;
import it.polimi.ingsw.messages.events.MoveStudentFromEntranceToTableEvent;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Player;

/**
 * First step of Action Phase.
 * The only allowed moves are: moving students from entrance and activating a character card.
 *
 */
public class AcceptMoveStudentFromEntranceState extends  GameState {


    AcceptMoveStudentFromEntranceState(RoundController context, int numberOfEvents) {
        super(context, numberOfEvents);
    }


    /**
     * @param event The event to check
     * @return true if the event is a MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND or MOVE_STUDENT_FROM_ENTRANCE_TO_TABLE or ACTIVATE_CHARACTER_CARD
     */
    @Override
    public boolean checkValidEvent(GameEvent event) {
        if (event.getEventType() == GameEventType.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND) {
            return true;
        }

        if (event.getEventType() == GameEventType.MOVE_STUDENT_FROM_ENTRANCE_TO_TABLE) {
            return true;
        }

        if (event.getEventType() == GameEventType.ACTIVATE_CHARACTER_CARD) {
            return true;
        }

        return false;
    }

    /**
     *  It moves the students specified in the events, either to the entrance or the table.
     *   When done it moves to the "Move Mother Nature" phase.
     * @param event The event to process.
     * @throws GameException If a problem arises in the movement of students to the entrance or the table
     */
    public void executeEvent(GameEvent event) throws GameException {

        switch (event.getEventType()) {
            case MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND: {
                MoveStudentFromEntranceToIslandEvent eventCast = (MoveStudentFromEntranceToIslandEvent) event;
                Player player = context.getSeatedPlayers()[eventCast.getPlayerNumber()];
                Color color = eventCast.getColor();
                int indexIsland = eventCast.getIndexIsland();

                    context.gameManager.moveStudentFromEntranceToIsland(player, color, indexIsland);
                    numberOfEvents--;

                break;
            }

            case MOVE_STUDENT_FROM_ENTRANCE_TO_TABLE: {
                MoveStudentFromEntranceToTableEvent eventCast = (MoveStudentFromEntranceToTableEvent) event;
                Player player = context.getSeatedPlayers()[eventCast.getPlayerNumber()];
                Color color = eventCast.getColor();

                context.gameManager.moveStudentFromEntranceToTable(player, color);
                numberOfEvents--;

                break;
            }

            case ACTIVATE_CHARACTER_CARD: {
                ActivateCharacterCardEvent eventCast = (ActivateCharacterCardEvent) event;
                int cardId = eventCast.getCardId();
                context.handleCard(this, cardId);
                break;

            }
        }
        if (numberOfEvents == 0){
            context.changeState(new AcceptMotherNatureMoveState(context, 1));
            context.gameManager.getModelObserver().changePhase(GamePhase.ACTION_PHASE_TWO);
        }

    }

}


