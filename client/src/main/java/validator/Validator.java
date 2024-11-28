package validator;

import commands.CommandArgument;
import commands.CommandData;
import commands.exceptions.IllegalCommandSyntaxException;

import java.util.*;

public class Validator {

    private final Map<Class<?>, Adaptor> adapters = new HashMap<>();

    public void addAdapter(Class<?> clazz, Adaptor adapter) {
        adapters.put(clazz, adapter);
    }


    public List<Object> checkSyntax(CommandData data, String args) throws IllegalCommandSyntaxException {
        args = args.trim();

        List<String> splitArguments = new ArrayList<>(List.of(args.split(" ", args.length())));

        splitArguments.replaceAll(String::trim);
        splitArguments.removeIf(String::isBlank);

        int currentElement = 0;

        List<Object> outList = new ArrayList<>();

        if (splitArguments.size() > data.getArguments().length) {
            throw new IllegalCommandSyntaxException("Too many parameters!", data);
        }

        for (CommandArgument argument : data.getArguments()) {
            if (currentElement >= splitArguments.size() ) {
                if (!argument.isOptional()) {
                    throw new IllegalCommandSyntaxException("Wrong number of parameters!", data);
                }

                outList.add(adapters.get(argument.getClazz()).check(Optional.empty(), argument, data));
            } else {
                outList.add(adapters.get(argument.getClazz()).check(Optional.of(splitArguments.get(currentElement)), argument, data));
            }

            currentElement++;
        }

        return outList;
    }
}
