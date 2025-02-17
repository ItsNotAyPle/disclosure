package org.ayplesoftware.frontend;

public class CommandHandler {
    
    public static void exectute(String command) {
        if (!command.startsWith("/")) return;
        

        String[] tokens = command.replace("/", "").split(" ");

        switch (tokens[0]) {
            case "join":
                // join server

                if (tokens.length >= 3) {
                    MainWindow.outputStringToOutputArea(tokens[1]);
                } else {
                    MainWindow.outputStringToOutputArea("Invaild arguments <ip>:<port> <username>");
                }


                break;
            case "disconnect":
                // disconnect server if in one
                break;
            default:
                // unknown command
                break;
        }
    }
}
