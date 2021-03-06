package it.polimi.ingsw.client.view.cli;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import it.polimi.ingsw.Log;
import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.ClientModelManager;
import it.polimi.ingsw.client.view.UI;
import it.polimi.ingsw.client.ConfigServer;
import it.polimi.ingsw.exceptions.UnexpectedMessageException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.events.*;
import it.polimi.ingsw.messages.events.ActivateCharacterCardEvent;
import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.server.RoomInfo;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;


import static org.fusesource.jansi.Ansi.ansi;

/**
 * Deals with user inputs and outputs if the player chose to play with the CLI option
 */
public class CLI implements UI {
    private ClientController clientController;
    private ClientModelManager cmm;
    private final Scanner input;
    private String username;
    boolean inARoom = false, kill;
    private BoardStatus bs;
    private Thread action;
    boolean logout = false;
    private String actionString;
    private static CLI instance;
    private final AtomicBoolean gameRunning;

    /**
     * Sets log file and final fields
     */
    private CLI() {
        this.input = new Scanner(System.in);
        new Log("CliLog.txt");
        gameRunning = new AtomicBoolean(false);
    }

    /**
     * Implements singleton pattern
     * @return the one and only cli instance
     */
    public static CLI getInstance() {
        if(instance == null)
            instance = new CLI();
        return instance;
    }

    /**
     * Sets game starting parameters and sets gameRunning to true, notifying all on gameRunning
     * @param numberOfPlayers Either 2, 3, or 4
     * @param expert True if game is expert mode, false otherwise
     * @param cmm The ClientModelManager connected to the starting game
     */
    @Override
    public void createGame(int numberOfPlayers, boolean expert, ClientModelManager cmm){
        printClear();
        this.cmm = cmm;
        this.bs = new BoardStatus(numberOfPlayers, expert);
        gameRunning.set(true);

        synchronized (gameRunning){
            gameRunning.notifyAll();
        }
    }

    /**
     * Refreshes view starting from the first turn change, to avoid waiting for useless prints
     */
    @Override
    public void refresh() {
        if(clientController.getPlayingPlayer() == -1)
            return;
        printClear();
        bs.printStatus(cmm, clientController);
        if(action != null)
            action.interrupt();
    }

    /**
     * Prints out all public rooms to the player
     * @param rooms The room info relative to the public rooms
     */
    @Override
    public void showPublicRooms(List<RoomInfo> rooms) {
        System.out.println(ansi().render("@|bg_black,bold,fg_yellow Public Games:|@").bgDefault().fgDefault());
        if(rooms.isEmpty()) {
            System.out.println("There are no public rooms available. \n");
        } else {
            for (RoomInfo room : rooms) {
                System.out.println("______________________________________________________________");
                System.out.print(room.toString());
                System.out.println();
                System.out.println();
            }
        }

    }

    /**
     * Prints NACK, ADD_PLAYER, END_GAME, PLAYER_DISCONNECT message to the player
     * @param message the message to be printed
     */
    @Override
    public void showMessage(Message message) {
        switch (message.getMessageType()) {
            case NACK -> System.out.println(((Nack) message).errorMessage());
            case ADD_PLAYER -> {
                AddPlayer ap = (AddPlayer) message;
                System.out.println("Player " + ap.username() +" joined! ");
            }
            case END_GAME -> {
                kill = true;
                inARoom = false;
                clientController.startOver();
                gameRunning.set(false);

                killGame();
                printClear();
                System.out.println("Game Over");
                System.out.println(message);
                System.out.println("Press anything to return to Lobby.");
                new Scanner(System.in).nextLine();
            }
            case PLAYER_DISCONNECT -> {
                PlayerDisconnect pd = (PlayerDisconnect) message;
                killGame();
                printClear();
                System.out.println("Player " + pd.username() + " has left the room");
                System.out.println("Press anything to return to Lobby.");
            }
            default ->
                    System.out.print(message.getMessageType());
        }
    }

    /**
     * Calls BoardStatus merge()
     * @param secondIsland The index of the island that was removed in the model
     */
    @Override
    public void merge(int secondIsland){
        bs.merge(secondIsland);
    }

    /**
     * @return The player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Cli main function, after connection and login alternates pregame with game function
     */
    public void start(){
        // initializing connections
        printClear();

        System.out.println("Welcome to ");
        gameTitle();
        String ipAddress;
        int serverPort;

        // trying connection from config file parameters
        try{
            Gson gson = new Gson();
            ConfigServer config = gson.fromJson( new String(Files.readAllBytes(Paths.get("config.json"))), ConfigServer.class);
            if(config != null) {
                clientController = new ClientController(this, config.getAddress(), config.getPort());
            }
        } catch (IOException e){
            clientController = null;
        }

        // trying connection from user input parameters
        while (clientController == null){
            System.out.println("Insert the Server IP: ");
            ipAddress = input.nextLine();
            System.out.println("Insert the Server Port: ");
            serverPort = input.nextInt();
            input.nextLine();
            try {
                clientController = new ClientController(this, ipAddress, serverPort);
            } catch (IOException e){
                System.out.println("Could not connect");
            }
        }

        // trying login
        boolean login = false;
        do {
            System.out.println("What's your nickname? ");
            username = input.nextLine();
            try {
                login = clientController.login(username);
            } catch (UnexpectedMessageException e){
                Log.logger.severe("Could not login, unexpected message exception");
            } catch (IOException e){
                Log.logger.severe("Could not login, IO exception");
                Log.logger.severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
        } while (!login);

        // pre game and game phase
        do {
            printClear();
            preGame();
            Thread game;

            if(!inARoom)
                return;

            kill = false;
            game = new Thread(this::game);
            synchronized (gameRunning){
                if(!gameRunning.get()) {
                    try{
                        gameRunning.wait();
                    } catch (InterruptedException e){
                        Log.logger.severe("Problems with gameRunning");
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            game.start();
            try{
                game.join();
            } catch (InterruptedException e){
                Log.logger.info("Thread game closing");
                Thread.currentThread().interrupt();
            } finally{
                inARoom = false;
            }
        } while(!logout);
    }

    /**
     * Prints game title in ascii art
     */
    private void gameTitle() {
        System.out.print("""
                                                                                                                                                                     \s
                        ..:^~~!~!!??!!77!!!!!!!!!7.                      ^!!!~.                                                                                      \s
                     .!?YYYYJJJGPB#P??JJ??Y5GBBBBG?                     7GGBBPP                                             .^                                       \s
                   .?YPJ~:.   ?G#&&?        .:!G&#P7                    ?@&&&&G                                            .PB.                                      \s
                  ^PP#!      JG&&&&?            :YBGJ                   .JGG5!.                                           :5G#.                                      \s
                  PG#J      :##&&&&7   :^~!!!~.   .!Y. .:::.    .^::.    .^^:            ..:^^^^::     .::.    .:^:.   .^75B&B~^~.  .^~~.     .!?!^    .:~~~~^::^!:  \s
                 .P##~      :B&&&&&~.^?5PGPY555^     .~?5PP?  ^?YPP5J: ^?YPP5J.       :~JYYJJ5PPPP.  ^7YP5J  :7YPP5Y^ :JYG#&&GYJ?..~?PBGY~.  ~PB&#57. ~?5YYY5PP55Y.  \s
                 .5#B!      .G&&&&#7?P#BJ^.  ?B!    ~?JB&##5^75B&&&&B^~!^Y&&&G:     ^75BB7:.^#&&&P.:??G&&&P~!5B#&&&#?  .:G&&&G: .!JG#&@@&G7. ^J#&&&G?~G&P~....!GG:   \s
                  J##J.     .G&&#&BP&B?.     ~?.    :.^B&##5JY~:P&&G~    ?&&&B:   .7Y#&G:   ^B&&&P....G&&&GJP?:!#&&&J   .P&&&P. ~!~:.^P&&&B?:  :G&&&J~#&BY?!^:...    \s
                  .P&P7:    .G#&&&&&P:                :G&&#P7. .PG!.     J#&&B:   !5#&#~    ^B#&&P.  .P&&&BY:  ~#&&&J   .P&&&P.       .Y&&&G?:  J&&5..J&&&&#G5J7^.   \s
                   .Y#GJ!:. .G###&&G.               . :B&&&5.  .^.       J#&&B:  .J#&&B~    ~B&&&P.  .P&&&B:   ~#&&&?.. .P&&&P.        .J#&&G?^^5#J. ..:?G#&&&&#GJ^  \s
                     :7JJ!!^.G###&#7              .!?.:B&&&5.            J#&&G:  .P&&&B7::~?JB&&&5.  .P&&&B:   ~#&&#J   .P&&&G. .:.     .?&&&G??G?. .??^ ..~JP#&&B7  \s
                            .P#&&&B:             .!J^ .P&&&J.            7#&&P:.:.?&&&&#GGG5!G&&&Y..:.J&&&P.   :B&&#7.:..5&&&5:~!!.      .7#&&BY~  ..7B?^..  .^P&#^  \s
                            .Y&&&&G:         ..:^JP~  .5&#P~             :B&&G~^: .J#&&&#Y^  7#&&J~^..?&&#7    .5&&&!^:. ~&&&BJ!.  .   ... ~#&Y. .....Y#GJ!^^^~5P~.  \s
                         .:^?#&&&&&J~^^^:^^^~7YG##~   .^!:.               .!?!:     :!7~.     :!7~.   ^7^.      .!7!:.   .^77~.        ....^GJ.........:~!!~^^:.     \s
                         ..:~~~~~~~~^^:::::^^~~~~:                                                                                    ...:?BJ.........               \s
                                                                                                                                     ...^Y&G......                   \s
                                                                                                                                    ...^P&&?..........               \s
                                                                                                                                   ....?&&&5:.....:~^...             \s
                                                                                                                                  .....~B&&&Y~^:^^7!.....            \s
                                                                                                                                  ......:!Y5P5?7!^:.....             \s
                """);
    }

    /**
     * Pre game phase, loops until player joins/creates a room or logs out
     */
    private void preGame(){
        int playersNumber;
        do{
            System.out.println("Hi " + username + ", what would you like to do?\n");
            System.out.println("""
                    1. REFRESH AVAILABLE PUBLIC ROOMS\s
                    2. CREATE GAME\s
                    3. ACCESS ROOM\s
                    4. LOGOUT\s
                    """);
            String chosenAction = input.nextLine();
            switch (chosenAction) {
                case ("1") -> {
                    System.out.println("Do you want to specify the number of players (2/3/4/ default: all games): ");
                    try {
                        playersNumber = Integer.parseInt(input.nextLine());
                    } catch (NumberFormatException e) {
                        playersNumber = 0;
                    }
                    System.out.println("Is the mode expert? (true/false/ default: all games) ");
                    String expert = input.nextLine();
                    printClear();
                    if (expert.startsWith("true")) {
                        if (playersNumber == 0) {
                            clientController.getPublicRooms(new GetPublicRooms(true));
                            break;
                        }
                        clientController.getPublicRooms(new GetPublicRooms(playersNumber, true));
                        break;
                    }
                    if (expert.startsWith("false")) {
                        if (playersNumber == 0) {
                            clientController.getPublicRooms(new GetPublicRooms(false));
                            break;
                        }
                        clientController.getPublicRooms(new GetPublicRooms(playersNumber, false));
                        break;
                    }
                    if (playersNumber == 0) {
                        clientController.getPublicRooms(new GetPublicRooms());
                        break;
                    }
                    clientController.getPublicRooms(new GetPublicRooms(playersNumber));
                }
                case ("2") -> {
                    boolean isPrivate;
                    boolean isExpert;

                    System.out.println("Enter the number of players (2/3/4/ default: 2): ");
                    try {
                        playersNumber = Integer.parseInt(input.nextLine());
                    } catch (NumberFormatException e) {
                        playersNumber = 2;
                    }
                    if (playersNumber != 3 && playersNumber != 4)
                        playersNumber = 2;

                    System.out.println("Is the mode expert? (true/false/ default: true) ");
                    isExpert = !input.nextLine().toLowerCase().startsWith("false");

                    System.out.println("Is your game private? (true/false/ default: false) ");
                    isPrivate = Boolean.parseBoolean(input.nextLine());

                    CreateRoom message = new CreateRoom(playersNumber, isExpert, isPrivate);
                    int roomID = clientController.createRoom(message);
                    if (roomID < 0) {
                        printClear();
                        System.out.println("Could not create room, please try again");
                        break;
                    }
                    printClear();
                    System.out.println("Your room ID is: " + roomID);
                    inARoom = true;
                }
                case ("3") -> {
                    int id = 0;
                    System.out.println("Enter the room ID: ");
                    try{
                        id = Integer.parseInt(input.nextLine());
                    }catch (NumberFormatException e){
                        printClear();
                        System.out.println("Please enter a valid ID");
                    }
                    if (clientController.accessRoom(id)) {
                        inARoom = true;
                    }
                    else
                        System.out.println("Could not enter room, please try again");

                }
                case ("4") -> {
                    if (clientController.logout()) {
                        logout = true;
                        return;
                    }
                    if(clientController.leaveRoom() && clientController.logout()){
                        logout = true;
                        return;
                    }
                    System.out.println("Could not logout");
                }
                default -> {
                    printClear();
                    System.out.println("Please enter a valid choice");
                }
            }
        }while(!inARoom);
    }

    /**
     * Game phase, loops while game is running listening for user inputs
     */
    private void game() {

        while(!kill){
            action = new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    actionString = (new Scanner(System.in)).nextLine();
                } catch (IndexOutOfBoundsException | InterruptedException ignored){
                    Log.logger.info("Stopping listen");
                    Thread.currentThread().interrupt();
                }
            });
            action.start();

            try{
                action.join();
            } catch (InterruptedException e){
                Log.logger.severe("InterruptedException in action.join()");
                Thread.currentThread().interrupt();
            }

            // if kill game has been changed while I was waiting for a line
            if(kill)
                return;

            if(actionString!= null)
                dealWithAction();
        }
    }

    /**
     * Sets kill parameters to make game phase come to an end
     */
    private void killGame(){
        kill = true;
        inARoom = false;
        clientController.startOver();
        gameRunning.set(false);

        printClear();
        System.out.println("Press anything to return to Lobby.");
    }

    /**
     * Clears the screen
     */
    private void printClear() {
        BoardStatus.printClear();
    }

    /**
     * Based on the action string received and the actions available from clientController.getActions(),
     * checks if action string is valid and then creates and sends GameEvent accordingly,
     * printing error message or cleaning error messages if needed
     */
    private void dealWithAction(){
        if(actionString == null)
            return;
        GameEvent event = null;

        try{
            if(actionString.length() == 0)
                throw new NumberFormatException();
            // removing all extra spaces
            actionString = actionString.trim().replaceAll(" +", " ");

            // getting action template
            int lineNum = Integer.parseInt(actionString.substring(0, 1));
            List<String> actions = clientController.getActions();
            if(lineNum >= actions.size() || lineNum < 0) {
                throw new NumberFormatException();
            }
            String template = actions.get(lineNum);

            // isolating input information
            actionString = actionString.substring(1).trim();
            int player = clientController.getPlayingPlayer();
            String[] inputs = actionString.split(" ");

            switch (template.substring(3).trim()){
                case ("Play assistant card #") -> {
                    int index = Integer.parseInt(actionString);
                    if(index < 1 || index > 10)
                        throw new NumberFormatException();
                    event = new PlayAssistantCardEvent(AssistantCard.values()[index -1], player);
                }
                case ("Move student X from E to I #") -> {

                    if(inputs.length != 2)
                        throw new NumberFormatException();

                    Color x = parseColor(inputs[0]);
                    int index = Integer.parseInt(inputs[1]);
                    event = new MoveStudentFromEntranceToIslandEvent(x, index, player);
                }
                case ("Move student X from E to DR") -> {
                    Color x = parseColor(actionString);
                    event = new MoveStudentFromEntranceToTableEvent(x, player);
                }
                case ("Move MN of # steps") -> {
                    int steps = Integer.parseInt(actionString);
                    if(steps < 1)
                        throw new NumberFormatException();
                    event = new MoveMotherNatureEvent(steps, player);
                }
                case ("Choose cloud #") -> {
                    int index = Integer.parseInt(actionString);
                    event = new ChooseCloudEvent(index, player);
                }
                case ("Activate card #") -> {
                    int id = Integer.parseInt(actionString);
                    event = new ActivateCharacterCardEvent(id, player);
                }
                case ("Display card # effect") -> {
                    printClear();
                    refresh();
                    System.out.println(CharacterCard.description(Integer.parseInt(actionString)));
                    System.out.print("            > ");

                    action.interrupt();
                }
                case ("Move student X from CC to I #") -> {
                    if(inputs.length != 2)
                        throw new NumberFormatException();

                    Color x = parseColor(inputs[0]);
                    int index = Integer.parseInt(inputs[1]);
                    event = new MoveStudentFromCardToIslandEvent(x, index, player);
                }
                case ("Swap X from CC with Y from E") -> {
                    if(inputs.length != 2)
                        throw new NumberFormatException();

                    Color x = parseColor(inputs[0]);
                    Color y = parseColor(inputs[1]);
                    event = new SwapStudentCardEntranceEvent(x, y, player);
                }
                case ("End selections") -> event = new EndSelection(player);
                case ("Swap X from E with Y from DR") -> {
                    if(inputs.length != 2)
                        throw new NumberFormatException();

                    Color x = parseColor(inputs[0]);
                    Color y = parseColor(inputs[1]);
                    event = new SwapStudentEntranceTableEvent(x, y, player);
                }
                case ("Choose I # to place a NoEntry"), ("Calculate influence in I #") -> {
                    int index = Integer.parseInt(actionString);
                    event = new ChooseIslandEvent(index, player);
                }
                case ("Move X from CC to DR") -> {
                    Color x = parseColor(actionString);
                    event = new MoveStudentFromCardToTableEvent(x, player);
                }
                case ("Choose X to not influence"), ("Choose X to remove from DR(s)") -> {
                    Color x = parseColor(actionString);
                    event = new ChooseColorEvent(x, player);
                }
                default -> {
                    printClear();
                    refresh();
                    if(action != null)
                        action.interrupt();
                    return;
                }
            }
        } catch (NumberFormatException e){
            System.out.println("            Not correctly formed action");
            System.out.print("            > ");
        }

        if(event != null) {
            Message answer = clientController.performEvent(event);
            if(answer.getMessageType() == MessageType.ACK) {
                printClear();
                refresh();
            }
            if(answer.getMessageType() == MessageType.NACK){
                AnsiConsole.systemInstall();
                AnsiConsole.out().print(Ansi.ansi().cursorRight(13));
                showMessage(answer);
                AnsiConsole.out().print(Ansi.ansi().cursorRight(13).a("> "));

                AnsiConsole.systemUninstall();
            }

            actionString = null;
            if(action != null)
                action.interrupt();
        }
    }

    /**
     * Helps dealWithAction function by parsing the color name given,
     * throwing an exception if a non-matching string is given
     * @param colorName The string containing one of the possible Color values
     * @return The Color value corresponding to the name
     * @throws NumberFormatException If the name passed does not correspond to any of the color values
     */
    private Color parseColor(String colorName) throws NumberFormatException{
        colorName = colorName.toUpperCase().trim();
        for(Color c : Color.values()){
            if(colorName.equals(c.name()))
                return c;
        }
        throw new NumberFormatException();
    }
}
