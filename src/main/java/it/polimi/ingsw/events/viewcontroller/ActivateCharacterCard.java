package it.polimi.ingsw.events.viewcontroller;

import it.polimi.ingsw.model.Player;

public class ActivateCharacterCard implements VC_GameEvent{
    String playerName;


    @Override
    public GameEventType getEventType() {
        return GameEventType.ACTIVATE_CHARACTER_CARD;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }





}
