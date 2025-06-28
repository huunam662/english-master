package com.example.englishmaster_be.domain.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.englishmaster_be.domain.cloudinary.dto.request.CloudinaryOptionsRequest;
import com.example.englishmaster_be.domain.cloudinary.dto.response.CloudinaryFileResponse;
import com.example.englishmaster_be.domain.cloudinary.dto.response.CloudinaryPageFileResponse;
import com.example.englishmaster_be.shared.dto.response.FileResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j(topic = "CLOUDINARY-SERVICE")
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unchecked")
public class CloudinaryService implements ICloudinaryService {

	RestTemplate restTemplate;

	Cloudinary cloudinary;

	@SneakyThrows
	@Override
	public FileResponse uploadFile(MultipartFile file){

		Map uploadResultResponse = cloudinary.uploader().upload(file.getBytes(), Map.of());

		String imageUrl = String.valueOf(uploadResultResponse.get("secure_url"));
//			String fileType = String.valueOf(uploadResultResponse.get("type"));

		return FileResponse.builder()
				.url(imageUrl)
				.type(file.getContentType())
				.build();
	}

	@SneakyThrows
	@Override
	public FileResponse uploadAudio(MultipartFile file) {

		Map uploadResult = cloudinary.uploader().upload(
				file.getBytes(),
				ObjectUtils.asMap(
						"resource_type", "video"
				)
		);

		String audioUrl = String.valueOf(uploadResult.get("secure_url"));

		return FileResponse.builder()
				.url(audioUrl)
				.type(file.getContentType())
				.build();
	}

	@Override
	public List<CloudinaryFileResponse> getFileList() throws Exception {
		List<Map<String, Object>> imagesResult = getFileListToType("image", null);
		List<Map<String, Object>> videosResult = getFileListToType("video", null);
		List<Map<String, Object>> rawsResult = getFileListToType("raw", null);
		List<Map<String, Object>> results = Stream.of(imagesResult, videosResult, rawsResult).flatMap(Collection::stream).toList();
		return results.stream().map(CloudinaryFileResponse::new).toList();
	}

	private List<Map<String, Object>> getFileListToType(String resourceType, String nextCursor) throws Exception {
		Map options = ObjectUtils.asMap(
				"resource_type", resourceType,
				"max_results", 500
		);
		if(nextCursor != null) options.put("next_cursor", nextCursor);
		Map results = cloudinary.api().resources(options);
		List<Map<String, Object>> resources = (List<Map<String, Object>>) results.get("resources");
		String nextCursorNew = (String) results.get("next_cursor");
		if(nextCursorNew == null) return resources;
		resources.addAll(getFileListToType(resourceType, nextCursorNew));
		return resources;
	}

	@Override
	public CloudinaryPageFileResponse getPageFile(CloudinaryOptionsRequest params) throws Exception {
		Map options = ObjectUtils.asMap(
				"type", "upload",
				"max_results", params.getSize()
		);
		if(params.getType() != null && !params.getType().isEmpty()){
			options.put("resource_type", params.getType());
		}
		if(params.getNextPageToken() != null && !params.getNextPageToken().isEmpty()){
			options.put("next_cursor", params.getNextPageToken());
		}
		Map results = cloudinary.api().resources(options);

		return new CloudinaryPageFileResponse(results, params);
	}

	@Override
	public void deleteFileByPublicId(String publicId) throws FileNotFoundException, BadRequestException {
		CloudinaryFileResponse file = getFileToPublicId(publicId);
		try{
			cloudinary.uploader().destroy(
					file.getPublicId(),
					ObjectUtils.asMap(
							"type", "upload"
					)
			);
		}
		catch (Exception e){
			throw new BadRequestException("Delete file fail.");
		}
	}

	@Override
	public CloudinaryFileResponse getFileToPublicId(String publicId) throws FileNotFoundException {
		Assert.notNull(publicId, "Public id to get file information is required.");
		List<String> types = List.of("image", "video", "raw");
		for(String type : types){
			try{
				Map fileResult = cloudinary.api().resource(
						publicId,
						ObjectUtils.asMap(
								"type", "upload",
								"resource_type", type
						)
				);
				return new CloudinaryFileResponse(fileResult);
			}
			catch (Exception e){
				log.error(e.getMessage(), e);
			}
		}
		throw new FileNotFoundException("File not found.");
	}

	@Override
	public byte[] getFileByteToPublicId(String publicId, String type, HttpServletResponse response) {
		try{
			CloudinaryFileResponse file = getFileToPublicId(publicId);
			ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
					file.getUrl(),
					HttpMethod.GET,
					new HttpEntity<>(null),
					byte[].class
			);
			if(responseEntity.getStatusCode().is2xxSuccessful()) {
				Optional<MediaType> contentTypeResponse = MediaTypeFactory.getMediaType(file.getUrl());
                contentTypeResponse.ifPresent(mediaType -> response.setContentType(mediaType.toString()));
				if("download".equalsIgnoreCase(type)){
					response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getPublicId() + "." + file.getFormat() + "\"");
				}
				else{
					response.addHeader("Content-Disposition", "inline; filename=\"" + file.getPublicId() + "." + file.getFormat() + "\"");
				}
				return responseEntity.getBody();
			}
			throw new FileNotFoundException("File not found.");
		}
		catch (Exception e){
			log.error(e.getMessage(), e);
			return new byte[0];
		}
	}
}
