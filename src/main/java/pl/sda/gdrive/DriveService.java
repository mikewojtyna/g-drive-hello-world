package pl.sda.gdrive;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.stream.Stream;

public class DriveService {
	private Drive drive;

	public DriveService(Drive drive) {
		this.drive = drive;
	}

	public String uploadFileWithContentAndName(String content,
						   String name) throws IOException {
		File fileToUpload = new File();
		fileToUpload.setName(name);
		AbstractInputStreamContent fileToUploadContent =
			new ByteArrayContent("text/plain", content
			.getBytes(Charset.forName("UTF-8")));
		File uploadedFile = drive.files()
			.create(fileToUpload, fileToUploadContent).execute();
		return uploadedFile.getId();
	}

	public void downloadFile(String fileId, Path target) throws IOException {
		drive.files().get(fileId)
			.executeMediaAndDownloadTo(new FileOutputStream(target
				.toFile()));
	}

	public Stream<String> getAllFileNames() throws IOException {
		FileList fileList = drive.files().list().execute();
		return fileList.getFiles().stream().map(file -> file.getName());
	}
}
