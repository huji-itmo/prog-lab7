package commands.auth;

import commands.CommandArgument;
import commands.CommandData;

public class LoginCommandData extends CommandData {
    public LoginCommandData() {
        super("login", "Not visible to user", new CommandArgument[]{
                new CommandArgument(String.class, "user_name", "", false),
                new CommandArgument(String.class, "password", "", false)
        });
    }
}
