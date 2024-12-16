package org.bhn.eventlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.bhn.resource.model.SqsUserUpdateModel;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.timer.TimerProvider;
import org.keycloak.timer.TimerProviderFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.keycloak.models.utils.KeycloakModelUtils.runJobInTransaction;

/**
 * @author Vinod Atwal
 *
 */
@Slf4j
@AutoService(SQSSpiProviderFactory.class)
public class PersonEventListener implements SQSSpiProviderFactory {

	public static final String PROVIDER_ID = "person-sqs-listener";

	private static final int LONG_POLLING_INTERVAL_SECONDS = 2;
	private static final int MAX_MESSAGES_TO_RECEIVE = 10;
	private static final int SCHEDULER_INTERVAL_MILLIS = 5000;
	public static final int numThreads = 3 ;
	private static String QUEUE_URL;
	private static final String jobName = "person-identity-sync";
	private ReceiveMessageRequest receiveMessageRequest;
	private final Random random = new Random();
	private TimerProvider timerProvider;
	private SqsClient sqsClient;
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public SQSSpiProvider create(KeycloakSession session) {
		return null;
	}

	@Override
	public void init(Config.Scope config) {
		log.info("SQS Event listener Processor init");
		sqsClient = getSqsClient();
		QUEUE_URL = Optional.ofNullable(System.getenv().get("PERSON_IDENTITY_SYNC_QUEUE"))
				.orElseThrow(() -> new RuntimeException("environment variable PERSON_IDENTITY_SYNC_QUEUE not found"));

		receiveMessageRequest = ReceiveMessageRequest.builder()
				.queueUrl(QUEUE_URL)
				.maxNumberOfMessages(MAX_MESSAGES_TO_RECEIVE)
				.waitTimeSeconds(LONG_POLLING_INTERVAL_SECONDS)
				.build();
	}

	private SqsClient getSqsClient() {
		return SqsClient.builder().region(Region.US_WEST_2)
				.credentialsProvider(DefaultCredentialsProvider.builder().build())
				.build();
	}

	private void processMessage(KeycloakSession session, Message message) {
		long start = System.currentTimeMillis();
		String body = message.body();
		SqsUserUpdateModel userUpdateModel;
		log.info("process event data from person service:{}", body);
		try {
			userUpdateModel = mapper.readValue(body, SqsUserUpdateModel.class);
			UserModel userModel = getUser(session, userUpdateModel.getRealmName(), userUpdateModel.getUserId());
			if(userUpdateModel.getFirstName()!=null ){
				userModel.setFirstName(userUpdateModel.getFirstName());
			}
			if (userUpdateModel.getLastName()!=null) {
				userModel.setLastName(userUpdateModel.getLastName());
			}
			userModel.setAttribute("personId", List.of(userUpdateModel.getPersonId()));
			String clientId = userUpdateModel.getClientId();
			Map<String, String> metaData = userUpdateModel.getMetaData();
			String mfa = metaData.get("MFA");
			Map<String, List<String>> attributes = userModel.getAttributes();

			if ("true".equals(mfa)) {
				if(attributes.containsKey("MFA")){
					List<String> values = attributes.get("MFA");
					values.add(clientId);
					userModel.setAttribute("MFA", values.stream()
							.distinct()
							.collect(Collectors.toList()));
				}else{
					userModel.setAttribute("MFA",List.of(clientId));
				}
			}else if(attributes.containsKey("MFA")){
				List<String> values = attributes.get("MFA");
				values.remove(clientId);
				userModel.setAttribute("MFA" ,values);
			}

			userModel.setAttribute(clientId,List.of(mapper.writeValueAsString(userUpdateModel.getMetaData())));

			deleteMessage(message);
			long end = System.currentTimeMillis();
			log.info("performance per record {} ms messageId {}" ,(end-start),message.messageId());
		} catch (IllegalArgumentException | JsonProcessingException e) {
			log.error("Unable to Process message {} with message {}", e.getMessage(), message.body());
			deleteMessage(message);
		}
	}

	private void processRecords(KeycloakSession session) {
		long start = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
		for (int i = 0; i < numThreads; i++) {
			executorService.execute((()->{
				int randomTimeout = random.nextInt(500); // Generate a random value between 0 and 9
				try {
					Thread.sleep(randomTimeout);
				} catch (InterruptedException e) {
					log.error("thread got interrupted ");
				}
				ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
						.queueUrl(QUEUE_URL)
						.maxNumberOfMessages(MAX_MESSAGES_TO_RECEIVE)
						.waitTimeSeconds(LONG_POLLING_INTERVAL_SECONDS)
						.build();
				List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
				log.info("no of records polls {} ",messages.size());
				messages.parallelStream().forEach(message -> {

					runJobInTransaction(session.getKeycloakSessionFactory(), sessionTask -> processMessage(sessionTask, message));
				});
			}));
		}

		executorService.shutdown();
		long end = System.currentTimeMillis();
		log.info("performance per batch {}", (end-start));
	}

	private UserModel getUser(KeycloakSession session, String realmName, String userId) {
		RealmModel realm = session.realms().getRealmByName(realmName);
		if (realm == null) {
			throw new IllegalArgumentException("Invalid realmName");
		}
		UserModel user = session.users().getUserById(realm, userId);
		if (user == null) {
			throw new IllegalArgumentException("Invalid userId");
		}
		return user;

	}

	private void deleteMessage(Message message) {
		log.info("removing msg with id {}",message.messageId());
		DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
				.queueUrl(QUEUE_URL)
				.receiptHandle(message.receiptHandle())
				.build();
		sqsClient.deleteMessage(deleteMessageRequest);
	}

	@Override
	public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

		KeycloakSession keycloakSession = keycloakSessionFactory.create();

		TimerProviderFactory timerProviderFactory = (TimerProviderFactory) keycloakSessionFactory.getProviderFactory(TimerProvider.class);
		//execute task every 10 seconds
		timerProviderFactory.create(keycloakSession)
				.scheduleTask(this::processRecords, SCHEDULER_INTERVAL_MILLIS, jobName);
	}

	@Override
	public void close() {
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}
}
