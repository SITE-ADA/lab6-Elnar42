package az.edu.ada.wm2.lab6.service;

import az.edu.ada.wm2.lab6.model.Category;
import az.edu.ada.wm2.lab6.model.Product;
import az.edu.ada.wm2.lab6.model.dto.ProductRequestDto;
import az.edu.ada.wm2.lab6.model.dto.ProductResponseDto;
import az.edu.ada.wm2.lab6.model.mapper.ProductMapper;
import az.edu.ada.wm2.lab6.repository.CategoryRepository;
import az.edu.ada.wm2.lab6.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        // Validate price before doing anything else
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        // Convert DTO to entity
        Product product = productMapper.toEntity(dto);

        // Fetch categories from repository by IDs in the DTO
        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());

        // Set categories on product
        product.setCategories(categories);

        // Save product entity
        Product savedProduct = productRepository.save(product);

        // Convert saved product entity back to response DTO and return
        return productMapper.toResponseDto(savedProduct);
    }

    @Override
    public ProductResponseDto getProductById(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toResponseDto)
                .orElseThrow();
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    @Override
    public ProductResponseDto updateProduct(UUID productId, ProductRequestDto dto) {
        // Validate price first
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        // Fetch existing product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        // Update product fields
        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());

        // Update categories
        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
        product.setCategories(categories);

        // Save updated product
        Product updatedProduct = productRepository.save(product);

        // Return DTO
        return productMapper.toResponseDto(updatedProduct);
    }

    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponseDto> getProductsExpiringBefore(LocalDate date) {
        return productRepository.findByExpirationDateBefore(date)
                .stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max)
                .stream()
                .map(productMapper::toResponseDto)
                .toList();
    }
}