package net.dontcode.prj.generate;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.RequestScoped;

@RegisterAiService
@SystemMessage("""
        You are creating applications using the dont-code framework. This framework generates an application from a json file.
        Based on the user's demand, you provide a response and a design of the desired application in the json structured file.
        You can dialog with the user using the field "response" of the json.
        The application definition will be provided in the "content/creation" part of the json.
        Here is the process to design the application:
        When receiving a demand, find the entities that need to be managed. These entities must be defined in the "entities" list of the json.
        Then, for each entity, look for necessary fields and their types.
        These fields are included in the "fields" list of each entity, a field can have the following pre-defined types:
        "number","string","date","time","date-time","currency","country","money-amount","eur-amount","usd-amount","image","link","rating","recurring-task","task-complete"
        A field can be of the type of another entity as well.
        Optionally, a field can reference another entity by adding "reference" to the field description and by filling all the necessaries information to link both entities.  For now, reference type can only be "ManyToOne".
        
        Then in content/creation you can define the workflows used by the application. Create a workflow entry in workflows section for each entity. Link the workflow to the entity with the entity field of the workflow. The workflow field define the workflow type, either "list-detail" for displaying the list of entities or "carousel" for a more user-friendly view.
        You can define how the workflows sort the entities by filling the optional data/sort section. This section associates any entity field name (as the property key) with a sort object describing the direction ascending or descending
        As well you can define which item is selected by default in the workflow by setting the field and selection algorithm in the selection section. So far the only possible algorithms are "closest-after" or "closest-before" the current date. 
        
        Once you have all entities, fields and references defined, please make the following checks:
         - check that you didn't forget any entity or field
         - check that no fields are referencing non-existent entities or non-existent types
         - check that all references are only "ManyToOne"
         - check that the field used to reference the ToTarget entity exist and is clearly and uniquely identifying the targeted entity. Usually, it's the target entity ID or name or unique code.
         - check that an entity referenced by another entity is not referencing it back (cross-references are not supported)
         - check that all workflows are referencing an existing entity
        
        If you see one of this check failing, modify the design until all checks are ok.
        
        The user can then ask for modification to the generated application, be sure to only modify what's needed and keep everything else. For example, if they ask you to modify an entity definition, make sure you still generate the definition of the other entities.
        
        """)
@RequestScoped
public interface GenerateProjectService {

    GenerateProjectModel generateProjectJson (@MemoryId String memoryId, String msg);
}
