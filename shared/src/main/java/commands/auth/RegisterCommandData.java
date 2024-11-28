package commands.auth;

import commands.CommandArgument;
import commands.CommandData;

public class RegisterCommandData extends CommandData {
    public RegisterCommandData() {
        super("register", "Not visible to user", new CommandArgument[]{
                new CommandArgument(String.class, "user_name", "", false),
                new CommandArgument(String.class, "password", "", false)
        });
    }
}
