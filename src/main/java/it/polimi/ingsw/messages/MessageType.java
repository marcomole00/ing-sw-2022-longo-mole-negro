package it.polimi.ingsw.messages;

public enum MessageType {
    LOGIN,
    LOGOUT,
    ACK,
    NACK,
    CREATE_ROOM,
    ROOM_ID,
    GET_PUBLIC_ROOMS,
    PUBLIC_ROOMS,
    ACCESS_ROOM,
    ADD_PLAYER,
    START_GAME,
    MOVE_STUDENT,
    ADD_STUDENT_TO,
    MOVE_TOWER,
    MERGE_ISLANDS,
    SET_PROFESSOR_TO,
    MOVE_MOTHER_NATURE,
    PLAY_ASSISTANT_CARD,
    ACTIVATE_CHARACTER_CARD,
    ADD_COIN,
    REMOVE_COIN,
    CHANGE_PHASE,
    CHANGE_TURN,
    GAME_EVENT,
    END_GAME,
    LEAVE_ROOM
}
