package com.example.the_cheaper.external.Storage;

import com.example.the_cheaper.exception.StorageException;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageService implements StorageService {
    private final Path rootLocation;

    @Autowired
    public FileStorageService(StorageProperties storageProperties) {
        if(storageProperties.getLocation().trim().length() == 0){
            throw new StorageException("File upload location can not be Empty.");
        }
        this.rootLocation = Paths.get(storageProperties.getLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {

            // Kiểm tra file có rỗng không
            // Ví dụ user submit mà không chọn file
            if (file.isEmpty()) {
                throw new StorageException(
                        "Failed to store empty file."
                );
            }
            /*
             * Tạo path đích để lưu file
             *
             * file.getOriginalFilename()
             * -> lấy tên file gốc user upload
             *
             * Paths.get(...)
             * -> convert string thành Path
             *
             * rootLocation.resolve(...)
             * -> ghép thư mục upload với tên file (nối đường dẫn )
             *
             * normalize()
             * -> loại bỏ các ký tự/path bất thường như:
             *    ../
             *    ./
             *
             * toAbsolutePath()
             * -> chuyển thành đường dẫn tuyệt đối
             */
            Path destinationFile =
                    this.rootLocation.resolve(
                                    Paths.get(
                                            file.getOriginalFilename()
                                    )
                            )
                            .normalize()
                            .toAbsolutePath();

            /*
             * Security Check
             *
             * Kiểm tra file có đang cố lưu ra ngoài thư mục upload không
             *
             * Ví dụ hacker upload filename:
             * ../../../windows/system32/test.exe
             *
             * Nếu không check:
             * -> có thể ghi file ra ngoài folder uploads
             * -> cực kỳ nguy hiểm (Path Traversal)
             *
             * getParent()
             * -> lấy thư mục cha của file
             *
             * so sánh với root upload folder
             */
            if (!destinationFile.getParent().equals(
                    this.rootLocation.toAbsolutePath()
            )) {

                throw new StorageException(
                        "Cannot store file outside current directory."
                );
            }

            /*
             * try-with-resources
             *
             * Tự động đóng InputStream sau khi dùng xong
             *
             * file.getInputStream()
             * -> lấy stream dữ liệu file upload
             */
            try (InputStream inputStream =
                         file.getInputStream()) {

                /*
                 * Copy dữ liệu từ stream -> file đích
                 *
                 * REPLACE_EXISTING
                 * -> nếu file tồn tại thì ghi đè
                 */
                Files.copy(
                        inputStream,
                        destinationFile,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
            return destinationFile.getFileName().toString();
        } catch (IOException e) {

            /*
             * IOException thường xảy ra khi:
             * - không có quyền ghi file
             * - folder không tồn tại
             * - disk full
             * - file đang bị lock
             * - lỗi filesystem
             */
            throw new StorageException(
                    "Failed to store file.",
                    e
            );
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return Stream.empty();
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {
        /*
         * rootLocation.toFile()
         *
         * Convert từ Path -> File
         *
         * Vì FileSystemUtils dùng java.io.File
         */
        FileSystemUtils.deleteRecursively(
                rootLocation.toFile()
        );

        /*
         * deleteRecursively()
         *
         * Xóa đệ quy toàn bộ:
         * - folder
         * - subfolder
         * - file bên trong

         * => xóa sạch tất cả
         */
    }


}
