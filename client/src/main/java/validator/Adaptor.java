package validator;

import commands.CommandArgument;
import commands.CommandData;

import java.util.Optional;

@FunctionalInterface
public interface Adaptor {

    Object check(Optional<String> input, CommandArgument argument, CommandData data);
}
