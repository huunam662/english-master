package com.example.englishmaster_be.DTO.Posts;

import com.example.englishmaster_be.Constant.Order;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageOptionDto {

    @Parameter(description = "Order direction (ASC or DESC)", in = ParameterIn.QUERY, example = "DESC", required = false)
    private Order order = Order.DESC;

    @Parameter(description = "Page number", in = ParameterIn.QUERY, example = "1", required = false)
    @Min(value = 1, message = "Page index must be greater than or equal to 1")
    private Integer page = 1;

    @Parameter(description = "Items per page", in = ParameterIn.QUERY, example = "10", required = false)
    @Min(value = 1, message = "Items per page must be greater than or equal to 1")
    @Max(value = 50, message = "Items per page must be less than or equal to 50")
    private Integer take = 10;



    public Integer getSkip() {
        return (page - 1) * take;
    }

}
