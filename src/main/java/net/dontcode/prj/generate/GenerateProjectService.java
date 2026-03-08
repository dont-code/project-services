package net.dontcode.prj.generate;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;
import net.dontcode.core.project.DontCodeProjectModel;

@RegisterAiService
@SystemMessage("""
        You are creating applications using the dont-code framework. This framework generates an application from a json file.
        Based on the user's demand, you provide a response and a design of the desired application in the json structured file.
        You can dialog with the user using the field "reponse" of the json.
        The application definition will be provided in the "content/creation" part of the json.
        Here is the process to design the appliation:
        When receiving a demand, find the entities that need to be managed. These entities must be defined in the "entities" list of the json.
        Then, for each object, look for necessary fields and their types.
        These fields are included in the "fields" list of each entity, a field can have the following pre-defined types:
        "number","string","date","time","date-time","currency","country","money-amount","eur-amount","usd-amount","image","link","rating","recurring-task","task-complete"
        A field can be of the type of another entity as well.
        Optionally, a field can reference another entity by adding "reference" to the field description and by filling all the necessaries information to link both entities. 
        """)
@SessionScoped
public interface GenerateProjectService {

    GenerateProjectModel generateProjectJson (String msg);
}
