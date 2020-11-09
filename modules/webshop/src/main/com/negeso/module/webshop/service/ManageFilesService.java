package com.negeso.module.webshop.service;

import com.negeso.framework.Env;
import com.negeso.module.webshop.util.MimeTypes;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

@Service
public class ManageFilesService {

	private static final String MEDIA_FOLDER_PATH = "customers/site/media/";

	public void save(MultipartFile multipartFile) throws IOException{
		String originalFilename = multipartFile.getOriginalFilename();
		this.save(multipartFile, originalFilename.substring(0, originalFilename.indexOf('.')));
	}

	public void save(MultipartFile multipartFile, String fileName) throws IOException{
		String ext = MimeTypes.extensionOf(multipartFile.getContentType());
		String pathToFolder;

		if (ext == null) ext = "jpg";
		ext = "." + ext;

		if (ext.equals(".jpg")){
			pathToFolder = ManageFilesService.MEDIA_FOLDER_PATH + "productsImages";
		}else if (ext.equals(".pdf")){
			pathToFolder = ManageFilesService.MEDIA_FOLDER_PATH + "productsPdf";
		}else
			throw new IOException("Extension isn't recognized");

		this.prepareFolder(pathToFolder);

		String pathToFile = Env.getRealPath(pathToFolder + "/" + fileName + ext);
		File fileToAdd = new File(pathToFile);
		if (fileToAdd.exists())
			this.replaceOldFile(pathToFile);
		multipartFile.transferTo(fileToAdd);

		this.setFilePermission(fileToAdd);
	}

	private void replaceOldFile(String pathToFile) throws IOException{
		File file = new File(pathToFile);
		if (!file.exists())
			throw new IOException("File to rename doesn't exist");
		int i = 1;
		int dot = pathToFile.lastIndexOf('.');

		String newName;
		String pathExcExt = pathToFile.substring(0, dot);
		String ext = pathToFile.substring(dot);


		while (file.exists()){
			newName = pathExcExt + "_" + i + ext;
			file = new File(newName);
			i++;
		}

		new File(pathToFile).renameTo(file);
	}

	private void prepareFolder(String pathToFolder) throws IOException {
		File folder = new File(Env.getRealPath(pathToFolder));
		if (!folder.exists())
			if (!folder.mkdir())
				throw new IOException("Can't create folder");
	}

	public boolean exists(String pathFromMedia){
		return new File(Env.getRealPath(ManageFilesService.MEDIA_FOLDER_PATH + pathFromMedia)).exists();
	}

	private void setFilePermission(File file){
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);

		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_WRITE);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);

		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);

		try {
			Files.setPosixFilePermissions(file.toPath(), perms);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
