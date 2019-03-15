package pl.sda.gdrive;

import java.io.IOException;
import java.nio.file.Paths;

public class GoogleDriveServiceAccountExample {
	public static void main(String[] args) throws IOException {
		DriveService driveService = createDriveService();

		driveService.getAllFileNames().forEach(System.out::println);
		String uploadedFileId = driveService
			.uploadFileWithContentAndName("hello", "hello");
		driveService.downloadFile(uploadedFileId, Paths
			.get("files", uploadedFileId));
	}

	private static DriveService createDriveService() throws IOException {
		DriveServiceFactory factory = new DriveServiceFactory();
		//		return factory
		//		.createServiceAccountDriveService();
		return factory.createAuthorizationCodeFlowDriveService();
	}
}
