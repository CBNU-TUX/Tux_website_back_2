package kr.ac.cbnu.tux.service;

import jakarta.transaction.Transactional;
import kr.ac.cbnu.tux.domain.Attachment;
import kr.ac.cbnu.tux.domain.Community;
import kr.ac.cbnu.tux.domain.ReferenceRoom;
import kr.ac.cbnu.tux.repository.AttachmentRepository;
import kr.ac.cbnu.tux.utility.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Transactional
    public Attachment create(MultipartFile multipartFile, ReferenceRoom data) {
        Attachment file = new Attachment();
        file.setFilename(multipartFile.getOriginalFilename());
        file.setPath("/api/referenceroom/" + data.getId() + "/file/" +
                Objects.requireNonNull(multipartFile.getOriginalFilename()).replaceAll("[\\\\/:*?\"<>| ]", "_"));
        file.setIsImage(multipartFile.getContentType().startsWith("image"));
        file.setOrder(data.getAttachments().size() + 1);
        file.setData(data);
        file.setDownloadCount(0L);
        return attachmentRepository.save(file);
    }

    @Transactional
    public Attachment create(MultipartFile multipartFile, Community post) {
        Attachment file = new Attachment();
        file.setFilename(multipartFile.getOriginalFilename());
        file.setPath("/api/community/" + post.getId() + "/file/" +
                Objects.requireNonNull(multipartFile.getOriginalFilename()).replaceAll("[\\\\/:*?\"<>| ]", "_"));
        file.setIsImage(multipartFile.getContentType().startsWith("image"));
        file.setOrder(post.getAttachments().size() + 1);
        file.setPost(post);
        file.setDownloadCount(0L);
        return attachmentRepository.save(file);
    }

    public void increaseDownloadCountById(Long attachmentId) {
        attachmentRepository.increaseDownloadCountById(attachmentId);
    }

    public Optional<Attachment> getFile(String filename, Community post) {
        return attachmentRepository.findByFilenameAndPost(filename, post);
    }

    public Optional<Attachment> getFile(String filename, ReferenceRoom data) {
        return attachmentRepository.findByFilenameAndData(filename, data);
    }

    @Transactional
    public void delete(Attachment file, Community post) throws Exception {
        FileHandler.deleteAttactment("community", post.getId().toString(), file);
        post.removeAttachment(file);
        attachmentRepository.delete(file);
    }

    @Transactional
    public void delete(Attachment file, ReferenceRoom data) throws Exception {
        FileHandler.deleteAttactment("referenceroom", data.getId().toString(), file);
        data.removeAttachment(file);
        attachmentRepository.delete(file);
    }


}
