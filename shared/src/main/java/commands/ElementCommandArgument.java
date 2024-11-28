package commands;

import dataStructs.StudyGroup;

public class ElementCommandArgument extends CommandArgument{
    public ElementCommandArgument() {
        super(StudyGroup.class,
                "element",
                "If this argument is empty, element will be constructed based on user's input.\n" +
                        "Or command tries to construct element with signature: \n" +
                        "(name: String, " +
                        "coordinates.x :double, " +
                        "coordinates.y: double, " +
                        "studentsCount: int | null, " +
                        "averageMark: long, " +
                        "formOfEducation: FormOfEducation, " +
                        "semesterEnum: FormOfEducation, " +
                        "groupAdmin: Person | null" +
                        "groupAdmin.name: String, " +
                        "groupAdmin.birthday: String, " +
                        "groupAdmin.nationality: Country, " +
                        "groupAdmin.location.x: int, " +
                        "groupAdmin.location.y: float, " +
                        "groupAdmin.location.name: String)", true);
    }
}
