package commands;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class CommandData implements Serializable {
    private String name;
    private String description;
    private CommandArgument[] arguments;

    @Serial
    private static final long serialVersionUID=1L;

    public String getArgumentsSyntax() {

        List<String> variants = new ArrayList<>();
        variants.add("");

        for (CommandArgument argument : getArguments()) {
            if (argument.isOptional()) {
                int size = variants.size();
                for (int i = 0; i < size; i++) {
                    variants.add(variants.get(i) + argument);
                }

            }
            else {
                for (int i = 0; i < variants.size(); i++) {
                    variants.set(i, variants.get(i) + argument);
                }
            }
        }
        if(variants.remove("")) {
            variants.add("no arguments");
        }

        StringBuilder builder = new StringBuilder(variants.get(0));

        for (int i = 1; i < variants.size(); i++) {
            builder.append(" or ").append(variants.get(i));
        }

        return builder.append(".").toString();
    }
}
