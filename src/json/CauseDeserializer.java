package json;

import java.lang.reflect.Type;

import sendable.DataType;
import sendable.alarm.Cause;
import sendable.alarm.DataCause;
import sendable.alarm.PlayerCause;
import sendable.alarm.TrainerCause;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class CauseDeserializer implements JsonDeserializer<Cause> {

    @Override
    public Cause deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Gson gson = new Gson();

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int causeType = jsonObject.get("type").getAsInt();

        if (causeType == DataType.CAUSE_DATA) {
            return gson.fromJson(jsonElement, DataCause.class);
        } else if (causeType == DataType.CAUSE_TRAINER) {
            return gson.fromJson(jsonElement, TrainerCause.class);
        } else if (causeType == DataType.CAUSE_PLAYER) {
            return gson.fromJson(jsonElement, PlayerCause.class);
        } else {
            throw new JsonParseException("Could not find object where data type is " + causeType);
        }
    }
}
