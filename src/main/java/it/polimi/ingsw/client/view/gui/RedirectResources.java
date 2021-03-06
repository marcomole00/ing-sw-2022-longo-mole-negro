package it.polimi.ingsw.client.view.gui;

import javafx.scene.image.Image;

/**
 * Helps with finding the resources needed.
 */
public final class RedirectResources {

    private RedirectResources() {

    }

    /**
     * Returns the correct Character Card image given its index
     * @param ccIndex the given index of the Character Card
     * @return the image of the Character Card with id ccIndex
     */
    public static Image characterCardsImages(int ccIndex) {
        switch (ccIndex) {
            case 0 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC0.png")));}
            case 1 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC1.png")));}
            case 2 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC2.png")));}
            case 3 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC3.png")));}
            case 4 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC4.png")));}
            case 5 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC5.png")));}
            case 6 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC6.png")));}
            case 7 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC7.png")));}
            case 8 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC8.png")));}
            case 9 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC9.png")));}
            case 10 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC10.png")));}
            case 11 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/CC11.png")));}
            default -> {return null;}
        }
    }

    /**
     * Returns the correct Character Card for the CharacterCards.fxml
     * @param ccIndex the Character Card index
     * @return the image of the Character Card corresponding to that index
     */
    public static Image characterCardsDescImages(int ccIndex) {
        switch (ccIndex) {
            case 0 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC0.png")));}
            case 1 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC1.png")));}
            case 2 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC2.png")));}
            case 3 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC3.png")));}
            case 4 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC4.png")));}
            case 5 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC5.png")));}
            case 6 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC6.png")));}
            case 7 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC7.png")));}
            case 8 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC8.png")));}
            case 9 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC9.png")));}
            case 10 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC10.png")));}
            case 11 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Personaggi/DescCC11.png")));}
            default -> {return null;}
        }
    }

    /**
     * Returns the image of the student of the right size and color
     * @param color the color of the student needed
     * @return the image of the student of the given color
     */
    public static Image studentsImages(String color) {
        switch (color) {
            case "RED" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/RoundRed.png")));}
            case "BLUE" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/RoundBlue.png")));}
            case "GREEN" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/RoundGreen.png")));}
            case "PINK" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/RoundPink.png")));}
            case "YELLOW" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/RoundYellow.png")));}
            default -> {return null;}

        }

    }

    /**
     * Returns the image with the correct color and size (min) of the towers
     * @param color the color of the tower needed
     * @return the image of the tower with the given color
     */
    public static Image minTowersImages(String color) {
        switch (color) {
            case "WHITE" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/MinWhiteTower.png")));}
            case "GREY" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/MinGreyTower.png")));}
            case "BLACK" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/MinBlackTower.png")));}
            default -> {return null;}

        }

    }

    /**
     * Returns the image with the correct color and size (regular) of the towers
     * @param color the color of the tower needed
     * @return the image of the tower with the given color
     */
    public static Image towersImages(String color) {
        switch (color) {
            case "WHITE" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/WhiteTower.png")));}
            case "GREY" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/GreyTower.png")));}
            case "BLACK" -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/BlackTower.png")));}
            default -> {return null;}
        }

    }

    /**
     * Returns che image of the Assistant Card specified by the given index
     * @param acIndex the index of the Assistant Card needed
     * @return the image of the Assistant Card corresponding to the given index
     */
    public static Image ACImages(int acIndex) {
        switch (acIndex) {
            case 1 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente1.png")));}
            case 2 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente2.png")));}
            case 3 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente3.png")));}
            case 4 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente4.png")));}
            case 5 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente5.png")));}
            case 6 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente6.png")));}
            case 7 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente7.png")));}
            case 8 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente8.png")));}
            case 9 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente9.png")));}
            case 10 -> {return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Assistente10.png")));}
            default -> {return null;}
        }
    }

    /**
     * Returns the images of the back of the decks
     * @param index the player index
     * @return the image of the deck for that given index
     */
    public static Image getDeckImages(int index) {
        switch (index) {
            case 0 -> {
                return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Back1.png")));
            }
            case 1 -> {
                return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Back2.png")));
            }
            case 2 -> {
                return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Back3.png")));
            }
            case 3 -> {
                return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/Back4.png")));
            }
            default -> {return null;}
        }

    }

    /**
     * Returns the NoEntry image
     * @return the noEntry image
     */
    public static Image getNoEntryImage() {
        return new Image(String.valueOf(RedirectResources.class.getResource("/images/Elements/NoEntry.png")));
    }

    /**
     * Returns the information needed from the url given
     * @param url the url of the image given
     * @return the information taken from the url
     */
    public static String fromURLtoElement(String url) {
        int index = url.lastIndexOf('/');
        String name = url.substring(index+1);
        if (name.indexOf(".") > 0)
            name = name.substring(0, name.lastIndexOf("."));

        name = name.replace("Min", "");
        switch (name) {
            case "RoundRed"-> {return "RED";}
            case "RoundPink" -> {return "PINK";}
            case "RoundYellow" -> {return "YELLOW";}
            case "RoundBlue" -> {return "BLUE";}
            case "RoundGreen" -> {return "GREEN";}
            case "MotherNature" -> {return "MOTHERNATURE";}
            case "NoEntry" -> {return "NOENTRY";}
            default -> {return name;}
        }
    }
}
