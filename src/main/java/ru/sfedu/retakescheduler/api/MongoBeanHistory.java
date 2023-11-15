package ru.sfedu.retakescheduler.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.UuidRepresentation;
import ru.sfedu.retakescheduler.model.HistoryContent;
import ru.sfedu.retakescheduler.model.Status;
import ru.sfedu.retakescheduler.Constants;

import java.io.IOException;
import java.util.*;

import static ru.sfedu.retakescheduler.utils.PropertiesConfigUtil.getProperty;

public class MongoBeanHistory {
	private MongoDatabase database;
	private HistoryContent historyContent;
	private static final Logger log = LogManager.getLogger(MongoBeanHistory.class);

	public MongoBeanHistory() {
		log.info("MongoBeanHistory [1]: class object created");
		try  {
			MongoClientSettings settings = MongoClientSettings.builder()
					.uuidRepresentation(UuidRepresentation.STANDARD)
					.applyConnectionString(new ConnectionString(getProperty(Constants.MONGODB_PATH)))
					.build();
			MongoClient mongoClient = MongoClients.create(settings);
			database = mongoClient.getDatabase(getProperty(Constants.MONGO_DB_NAME));

		} catch (IOException e) {
			log.error("MongoBeanHistory [2]: {}", e.getMessage());
		}
	}

	private HistoryContent createHistoryContent(Object object, String method, Status status) {
		HistoryContent history = new HistoryContent();
		history.setId();
		history.setClassName(object.getClass().getName());
		history.setCreatedDate(new Date());
		history.setActor(Constants.ACTOR_CHANGED_OBJECT_DEFAULT);
		history.setMethodName(method);
		history.setStatus(status);
		history.setObject(objectToJsonArray(object));

		return history;
	}

	private void save() {
		try {
			MongoCollection<Document> collection = database.getCollection(getProperty(Constants.MONGODB_COLLECTION));
			Document doc = new Document(Constants.MONGO_FIELD_ID, historyContent.getId())
					.append(Constants.MONGO_FIELD_CLASSNAME, historyContent.getClassName())
					.append(Constants.MONGO_FIELD_DATE, historyContent.getCreatedDate())
					.append(Constants.MONGO_FIELD_ACTOR, historyContent.getActor())
					.append(Constants.MONGO_FIELD_METHOD, historyContent.getMethodName())
					.append(Constants.MONGO_FIELD_OBJECT, historyContent.getObject())
					.append(Constants.MONGO_FIELD_STATUS, historyContent.getStatus());

			collection.insertOne(doc);
		} catch (Exception e) {
			log.error("save[1]: {}", e.getMessage());
		}
	}

	public void logObject(Object object, String method, Status status) {
		historyContent = createHistoryContent(object, method, status);
		save();
	}

	public static <T> List<T> jsonArrayToObjectList(List<Map<String, Object>> map, Class<T> tClass) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			CollectionType listType = mapper.getTypeFactory()
					.constructCollectionType(ArrayList.class, tClass);
			List<T> objects = mapper.convertValue(map, listType);
			log.debug("jsonArrayToObjectList[1]: object = {}", objects);
			return objects;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ClassCastException(e.getMessage());
		}
	}

	public static Map<String, Object> objectToJsonArray(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> jsonArray = new HashMap<>();
		try {
			jsonArray = mapper.convertValue(object, Map.class);
			log.debug("objectToJsonArray[1]: jsonArray = {}", jsonArray);
		} catch (Exception e) {
			log.error("objectToJsonArray[2]: {}", e.getMessage());
		}
		return jsonArray;
	}

	public List<Object> getObjectInDocumentByClassName(String className) {
		log.info("getObjectInDocumentByClassName[1]: the method has been run, className = {}", className);
		String finalClassName = Constants.MODEL_PATH + className;

		try (MongoClient mongoClient = MongoClients.create(getProperty(Constants.MONGODB_PATH))) {
			database = mongoClient.getDatabase(getProperty(Constants.MONGO_DB_NAME));
			MongoCollection<Document> collection = database.getCollection(getProperty(Constants.MONGODB_COLLECTION));

			List<Document> historyContentObjects = collection.find(Filters.eq(Constants.MONGO_FIELD_CLASSNAME, finalClassName)).into(new ArrayList<>());

			List<Map<String, Object>> objectList = new ArrayList<>();
			Class<Object> objectClass = (Class<Object>) Class.forName(finalClassName);
			for (Document historyObject : historyContentObjects) {
				Map<String, Object> objectField = historyObject.get(Constants.MONGO_FIELD_OBJECT, Map.class);
				objectList.add(objectField);
			}

			return jsonArrayToObjectList(objectList, objectClass);
		} catch (Exception e) {
			log.error("getObjectInDocumentByClassName[2]: {}", e.getMessage());
		}
		return null;
	}
}
