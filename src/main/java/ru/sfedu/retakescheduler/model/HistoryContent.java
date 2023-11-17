package ru.sfedu.retakescheduler.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.Status;

import java.time.LocalDateTime;
import java.util.*;

public class HistoryContent {
//	private String Id;
	private UUID Id;
	private String className;
	private LocalDateTime createdDate;
	private String actor = Constants.ACTOR_CHANGED_OBJECT_DEFAULT;
	private String methodName;
	private Map<String, Object> object;
	private Status status;


	private static final Logger log = LogManager.getLogger(HistoryContent.class);

	public HistoryContent() {}

	public UUID getId() {
		return Id;
	}

//	public void setId(UUID id) {
//		Id = id;
//	}

	public void setId() {
		Id = UUID.randomUUID();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Map<String, Object> getObject() {
		return object;
	}

	public void setObject(Map<String, Object> object) {
		this.object = object;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "HistoryContent{" +
				"Id='" + Id + '\'' +
				", className='" + className + '\'' +
				", createdDate=" + createdDate +
				", actor='" + actor + '\'' +
				", methodName='" + methodName + '\'' +
				", object=" + object +
				", status=" + status +
				'}';
	}

}
