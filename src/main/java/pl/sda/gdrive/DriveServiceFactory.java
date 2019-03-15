package pl.sda.gdrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

public class DriveServiceFactory {
	private static final String CREDENTIALS_FILE_PATH = "/credentials" +
		".json";
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private HttpTransport httpTransport;
	private JsonFactory jsonFactory;

	public DriveServiceFactory() {
		httpTransport = new NetHttpTransport();
		jsonFactory = JacksonFactory.getDefaultInstance();
	}

	public DriveService createServiceAccountDriveService() throws IOException {
		return createDriveService(createServiceAccountCredential());
	}

	public DriveService createAuthorizationCodeFlowDriveService() throws IOException {
		return createDriveService(createAuthorizationCodeFlow());
	}

	private DriveService createDriveService(Credential credential) {
		Drive drive = new Drive.Builder(httpTransport, jsonFactory,
			credential)
			.setApplicationName("gdrive-hello-world").build();

		return new DriveService(drive);
	}

	private static Credential createServiceAccountCredential() throws IOException {
		return GoogleCredential.fromStream(DriveServiceFactory.class
			.getResourceAsStream("/sda-download-manager" +
				"-a45cfa61168c.json"))
			.createScoped(Collections.singleton(DriveScopes.DRIVE));
	}

	private Credential createAuthorizationCodeFlow() throws IOException {
		// Load client secrets.
		InputStream in = DriveServiceFactory.class
			.getResourceAsStream(CREDENTIALS_FILE_PATH);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets
			.load(jsonFactory, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow =
			new GoogleAuthorizationCodeFlow.Builder(httpTransport,
				jsonFactory, clientSecrets, Collections
			.singleton(DriveScopes.DRIVE))
			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
			.setAccessType("offline").build();
		LocalServerReceiver receiver =
			new LocalServerReceiver.Builder()
			.setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver)
			.authorize("user");

	}
}
