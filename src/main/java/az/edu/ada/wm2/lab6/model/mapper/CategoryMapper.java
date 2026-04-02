package az.edu.ada.wm2.lab6.model.mapper;


import az.edu.ada.wm2.lab6.model.Category;
import az.edu.ada.wm2.lab6.model.dto.CategoryRequestDto;
import az.edu.ada.wm2.lab6.model.dto.CategoryResponseDto;

public class CategoryMapper {

    public static Category toEntity(CategoryRequestDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }

    public static CategoryResponseDto toResponseDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}