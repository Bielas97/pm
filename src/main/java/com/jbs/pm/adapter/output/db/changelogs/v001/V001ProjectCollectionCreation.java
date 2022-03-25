package com.jbs.pm.adapter.output.db.changelogs.v001;

import static org.springframework.data.mongodb.core.schema.JsonSchemaProperty.*;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import lombok.val;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@ChangeLog(order = "001")
public class V001ProjectCollectionCreation {

  @ChangeSet(order = "001", id = "projectCollectionCreation", author = "jbielawski")
  public void createProjectCollection(MongoDatabase db) {
    val options = new CreateCollectionOptions();
    val jsonSchema =
        MongoJsonSchema.builder()
            .required("_id", "fullName", "shortName", "startDate", "endDate", "tags")
            .properties(
                objectId("_id"),
                array("tags"),
                date("startDate"),
                date("endDate"),
                string("shortName").minLength(1).maxLength(10))
            .build();
    val vOptions = new ValidationOptions();
    vOptions.validator(Validator.schema(jsonSchema).toDocument());
    options.validationOptions(vOptions);
    db.createCollection("projects", options);
  }
}
