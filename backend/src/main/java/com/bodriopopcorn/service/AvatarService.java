package com.bodriopopcorn.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bodriopopcorn.exception.ResourceNotFoundException;

@Service
public class AvatarService {
	private static final long MAX_BYTES = 2L * 1024 * 1024;
	private static final int MAX_DIMENSION = 4096;
	private final JdbcTemplate database;

	public AvatarService(JdbcTemplate database) {
		this.database = database;
	}

	public void upload(String email, MultipartFile file) throws IOException {
		if (file.isEmpty() || file.getSize() > MAX_BYTES) {
			throw new IllegalArgumentException("La imagen debe pesar hasta 2 MB.");
		}
		try (ImageInputStream input = ImageIO.createImageInputStream(file.getInputStream())) {
			if (input == null) throw new IllegalArgumentException("Selecciona una imagen JPEG o PNG válida.");
			Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
			if (!readers.hasNext()) throw new IllegalArgumentException("Selecciona una imagen JPEG o PNG válida.");
			ImageReader reader = readers.next();
			try {
				reader.setInput(input, true, true);
				String format = reader.getFormatName().toLowerCase(java.util.Locale.ROOT);
				if ((!format.equals("jpeg") && !format.equals("png"))
						|| reader.getWidth(0) > MAX_DIMENSION || reader.getHeight(0) > MAX_DIMENSION) {
					throw new IllegalArgumentException("Usa JPEG o PNG de hasta 4096 × 4096 píxeles.");
				}
				BufferedImage image = reader.read(0);
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				if (!ImageIO.write(image, format, output) || output.size() > MAX_BYTES) {
					throw new IllegalArgumentException("No se pudo procesar la imagen o supera 2 MB.");
				}
				database.update("""
						INSERT INTO user_avatars (user_id, content_type, image)
						SELECT id, ?, ? FROM users WHERE email = ?
						ON DUPLICATE KEY UPDATE content_type = VALUES(content_type), image = VALUES(image)
						""", "image/" + format, output.toByteArray(), email);
			} finally {
				reader.dispose();
			}
		} catch (IIOException exception) {
			throw new IllegalArgumentException("La imagen está dañada o no se puede leer.", exception);
		}
	}

	public Avatar get(String email) {
		try {
			return database.queryForObject("""
					SELECT a.content_type, a.image FROM user_avatars a
					JOIN users u ON u.id = a.user_id WHERE u.email = ?
					""", (rs, row) -> new Avatar(rs.getString(1), rs.getBytes(2)), email);
		} catch (EmptyResultDataAccessException exception) {
			throw new ResourceNotFoundException("Avatar", "current user");
		}
	}

	public record Avatar(String contentType, byte[] image) { }
}
