package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.restaurant.management.dto.request.ImagesRequest;
import vn.restaurant.management.entity.Images;
import vn.restaurant.management.exception.dish.DishNotFoundException;
import vn.restaurant.management.exception.images.ImagesNotFoundException;
import vn.restaurant.management.repository.DishRepository;
import vn.restaurant.management.repository.ImagesRepository;
import vn.restaurant.management.service.ImageService;
import vn.restaurant.management.utils.GeneralUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImagesServieceImpl implements ImageService {
    @Autowired
    ImagesRepository imagesRepository;

    @Autowired
    DishRepository dishRepository;
    @Autowired
    private S3FileUploadServiceImpl s3FileUploadServiceImpl;

    @Override
    /**
     * Lấy danh sách tất cả hình ảnh.
     *
     * @return Danh sách các đối tượng Images.
     */
    public List<Images> getImages() {
        return imagesRepository.findAll();
    }

    @Override
    /**
     * Lấy thông tin hình ảnh theo ID.
     *
     * @param id ID của hình ảnh cần lấy.
     * @return Đối tượng Images chứa thông tin của hình ảnh.
     * @throws ImagesNotFoundException Nếu không tìm thấy hình ảnh với ID đã cho.
     */
    public Images getImageById(String id) throws ImagesNotFoundException {
        // Tìm hình ảnh theo ID
        return imagesRepository.findById(id)
                .orElseThrow(() -> new ImagesNotFoundException("Không tìm thấy id: " + id));
    }

    @Override
    /**
     * Lấy danh sách hình ảnh theo ID món ăn.
     *
     * @param dishId ID của món ăn để lấy hình ảnh.
     * @return Danh sách các đối tượng Images liên quan đến món ăn với ID đã cho.
     * @throws ImagesNotFoundException Nếu không tìm thấy hình ảnh cho món ăn với ID đã cho.
     * @throws DishNotFoundException Nếu không tìm thấy món ăn với ID đã cho.
     */
    public List<Images> getImagesByDishId(String dishId) throws ImagesNotFoundException, DishNotFoundException {
        // Kiểm tra xem món ăn có tồn tại không
        if (!dishRepository.existsById(dishId)) {
            throw new DishNotFoundException("Không tìm thấy món ăn với ID: " + dishId);
        }

        // Lấy danh sách hình ảnh theo ID món ăn từ kho dữ liệu
        List<Images> images = imagesRepository.findByDishId(dishId);

        // Kiểm tra xem có hình ảnh nào không
        if (images.isEmpty()) {
            throw new ImagesNotFoundException("Không tìm thấy hình ảnh cho món ăn với ID: " + dishId);
        }

        // Trả về danh sách hình ảnh nếu tìm thấy
        return images;
    }


    @Override
    /**
     * Lưu hình ảnh mới được liên kết với món ăn.
     *
     * @param request Yêu cầu chứa thông tin chi tiết của hình ảnh cần lưu.
     * @return Thực thể hình ảnh đã được lưu.
     * @throws IOException Nếu có lỗi xảy ra trong quá trình tải tệp lên.
     * @throws ImagesNotFoundException Nếu ID món ăn không hợp lệ hoặc món ăn không tồn tại.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Images saveImage(ImagesRequest request) throws IOException, ImagesNotFoundException {
        // Check if dishId is provided and valid
        if (!StringUtils.hasText(request.getDishId())) {
            throw new ImagesNotFoundException(request.getDishId() + ": Dish id is invalid");
        }

        // Check if the dish exists in the repository
        if (!dishRepository.existsById(request.getDishId())) {
            throw new ImagesNotFoundException("Dish with id " + request.getDishId() + " does not exist");
        }

        Images images = Images.builder()
                .id(GeneralUtils.generateId())
                .linkImg((request.getLinkImg() != null && !request.getLinkImg().isEmpty())
                        ? s3FileUploadServiceImpl.uploadFile(request.getLinkImg()) :
                        null)
                .dishId(request.getDishId())
                .build();
        return imagesRepository.save(images);
    }

    @Override
    /**
     * Cập nhật thông tin hình ảnh đã có.
     *
     * @param imgId ID của hình ảnh cần cập nhật.
     * @param request Yêu cầu chứa thông tin cập nhật cho hình ảnh.
     * @return Thực thể hình ảnh đã được cập nhật.
     * @throws ImagesNotFoundException Nếu ID hình ảnh hoặc ID món ăn không hợp lệ, hoặc nếu không tồn tại.
     * @throws IOException Nếu có lỗi xảy ra trong quá trình tải tệp lên.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Images updateImage(String imgId,ImagesRequest request) throws ImagesNotFoundException, IOException {
        if(!StringUtils.hasText(imgId)) {
            throw new ImagesNotFoundException(imgId + ": id is invalid");
        }
        // Check if dishId is provided and valid
        if (!StringUtils.hasText(request.getDishId())) {
            throw new ImagesNotFoundException(request.getDishId() + ": Dish id is invalid");
        }

        // Check if the dish exists in the repository
        if (!dishRepository.existsById(request.getDishId())) {
            throw new ImagesNotFoundException("Dish with id " + request.getDishId() + " does not exist");
        }

        // Retrieve the existing image record
        Optional<Images> optionalImage = imagesRepository.findById(imgId);
        if (!optionalImage.isPresent()) {
            throw new ImagesNotFoundException("Image with id " + imgId + " does not exist");
        }
        Images images = optionalImage.get();

        // Update the fields as needed
        if (request.getLinkImg() != null && !request.getLinkImg().isEmpty()) {
            String updatedLink = s3FileUploadServiceImpl.uploadFile(request.getLinkImg());
            images.setLinkImg(updatedLink);
        }
        images.setDishId(request.getDishId());

        return imagesRepository.save(images);
    }

    @Override
    /**
     * Xóa hình ảnh bằng ID.
     *
     * @param id ID của hình ảnh cần xóa.
     * @throws ImagesNotFoundException Nếu ID hình ảnh không hợp lệ hoặc hình ảnh không tồn tại.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void deleteImage(String id) throws ImagesNotFoundException {
        // Validate id
        if (!StringUtils.hasText(id)) {
            throw new ImagesNotFoundException(id + ": id is invalid");
        }

        // Check if the image exists
        if (!imagesRepository.existsById(id)) {
            throw new ImagesNotFoundException("Image with id " + id + " does not exist");
        }

        // Delete the image
        imagesRepository.deleteById(id);
    }
}
