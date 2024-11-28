package commands.clientSideCommands;


import commands.clientSideCommands.commandData.ExitDatabaseCommandData;

public class ExitDatabaseCommandImpl extends ClientSideCommand {

    public ExitDatabaseCommandImpl() {
        setCommandData(new ExitDatabaseCommandData());
    }

    @Override
    public String execute(String args) {

        System.exit(0);

        return null;
    }


//    public static void davidSosatPisos(){
//        while(true){
//            david.sosatPisosNonStopom();
//        }
//    }
}
